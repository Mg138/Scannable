package li.cil.scannable.common.scanning;

import li.cil.scannable.api.API;
import li.cil.scannable.api.scanning.BlockScannerModule;
import li.cil.scannable.api.scanning.ScanResultProvider;
import li.cil.scannable.client.scanning.ScanResultProviders;
import li.cil.scannable.client.scanning.filter.BlockCacheScanFilter;
import li.cil.scannable.client.scanning.filter.FluidTagScanFilter;
import li.cil.scannable.common.config.CommonConfig;
import li.cil.scannable.common.config.Constants;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.material.Fluid;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.fml.event.config.ModConfigEvent;
import net.minecraftforge.registries.ForgeRegistries;
import net.minecraftforge.registries.tags.ITagManager;

import java.util.ArrayList;
import java.util.List;
import java.util.function.Predicate;

@Mod.EventBusSubscriber(modid = API.MOD_ID, bus = Mod.EventBusSubscriber.Bus.MOD)
public enum FluidBlockScannerModule implements BlockScannerModule {
    INSTANCE;

    private Predicate<BlockState> filter;

    @Override
    public int getEnergyCost(final ItemStack module) {
        return CommonConfig.energyCostModuleFluid;
    }

    @OnlyIn(Dist.CLIENT)
    @Override
    public ScanResultProvider getResultProvider() {
        return ScanResultProviders.BLOCKS.get();
    }

    @OnlyIn(Dist.CLIENT)
    @Override
    public float adjustLocalRange(final float range) {
        return range * Constants.BLOCK_MODULE_RADIUS_MULTIPLIER;
    }

    @OnlyIn(Dist.CLIENT)
    @Override
    public Predicate<BlockState> getFilter(final ItemStack module) {
        validateFilter();
        return filter;
    }

    @OnlyIn(Dist.CLIENT)
    private void validateFilter() {
        if (filter != null) {
            return;
        }

        final List<Predicate<BlockState>> filters = new ArrayList<>();
        final ITagManager<Fluid> tags = ForgeRegistries.FLUIDS.tags();
        if (tags != null) {
            tags.getTagNames().forEach(tag -> {
                if (!CommonConfig.ignoredFluidTags.contains(tag.location())) {
                    filters.add(new FluidTagScanFilter(tag));
                }
            });
        }
        filter = new BlockCacheScanFilter(filters);
    }

    @OnlyIn(Dist.CLIENT)
    @SubscribeEvent
    public static void onModConfigEvent(final ModConfigEvent configEvent) {
        // Reset on any config change so we also rebuild the filter when resource reload
        // kicks in which can result in ids changing and thus our cache being invalid.
        FluidBlockScannerModule.INSTANCE.filter = null;
    }
}
