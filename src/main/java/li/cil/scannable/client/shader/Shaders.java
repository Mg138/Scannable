package li.cil.scannable.client.shader;

import com.mojang.blaze3d.systems.RenderSystem;
import com.mojang.blaze3d.vertex.DefaultVertexFormat;
import com.mojang.blaze3d.vertex.VertexFormat;
import li.cil.scannable.api.API;
import net.minecraft.client.Minecraft;
import net.minecraft.client.renderer.ShaderInstance;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.server.packs.resources.ReloadableResourceManager;
import net.minecraft.server.packs.resources.ResourceManager;
import net.minecraft.server.packs.resources.ResourceManagerReloadListener;
import net.minecraft.server.packs.resources.ResourceProvider;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import javax.annotation.Nullable;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.function.Consumer;

@SuppressWarnings("deprecation")
@OnlyIn(Dist.CLIENT)
public final class Shaders implements ResourceManagerReloadListener {
    private static final Shaders INSTANCE = new Shaders();
    private static final List<ShaderReference> SHADERS = new ArrayList<>();

    public static ShaderInstance scanEffectShader;
    public static ShaderInstance scanResultShader;

    public static void initialize() {
        addShader("scan_effect", DefaultVertexFormat.POSITION_TEX, shader -> scanEffectShader = shader);
        addShader("scan_result", DefaultVertexFormat.POSITION_TEX_COLOR, shader -> scanResultShader = shader);

        loadAndListenToReload();
    }

    @Nullable
    public static ShaderInstance getScanEffectShader() {
        return scanEffectShader;
    }

    @Nullable
    public static ShaderInstance getScanResultShader() {
        return scanResultShader;
    }

    @Override
    public void onResourceManagerReload(final ResourceManager manager) {
        reloadShaders(manager);
    }

    private static void loadAndListenToReload() {
        final ResourceManager manager = Minecraft.getInstance().getResourceManager();
        Minecraft.getInstance().submitAsync(() -> INSTANCE.onResourceManagerReload(manager));
        if (manager instanceof ReloadableResourceManager) {
            ((ReloadableResourceManager) manager).registerReloadListener(INSTANCE);
        }
    }

    private static void reloadShaders(final ResourceProvider provider) {
        RenderSystem.assertOnRenderThread();
        SHADERS.forEach(reference -> reference.reload(provider));
    }

    private static void addShader(final String name, final VertexFormat format, final Consumer<ShaderInstance> reloadAction) {
        SHADERS.add(new ShaderReference(name, format, reloadAction));
    }

    private static final class ShaderReference {
        private static final Logger LOGGER = LogManager.getLogger();

        private final String name;
        private final VertexFormat format;
        private final Consumer<ShaderInstance> reloadAction;
        private ShaderInstance shader;

        public ShaderReference(final String name, final VertexFormat format, final Consumer<ShaderInstance> reloadAction) {
            this.name = name;
            this.format = format;
            this.reloadAction = reloadAction;
        }

        public void reload(final ResourceProvider provider) {
            if (shader != null) {
                shader.close();
                shader = null;
            }

            try {
                shader = new ShaderInstance(location -> {
                    try {
                        return provider.getResource(new ResourceLocation(API.MOD_ID, location.getPath()));
                    } catch (final IOException e) {
                        return provider.getResource(location);
                    }
                }, name, format);
            } catch (final Exception e) {
                LOGGER.error(e);
            }

            reloadAction.accept(shader);
        }
    }

    private Shaders() {
    }
}
