package li.cil.scannable.client.gui;

import com.mojang.blaze3d.systems.RenderSystem;
import com.mojang.blaze3d.vertex.PoseStack;
import li.cil.scannable.api.API;
import li.cil.scannable.common.container.ScannerContainerMenu;
import net.minecraft.client.gui.screens.inventory.AbstractContainerScreen;
import net.minecraft.network.chat.Component;
import net.minecraft.network.chat.TranslatableComponent;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.entity.player.Inventory;
import net.minecraft.world.inventory.ClickType;
import net.minecraft.world.inventory.Slot;
import net.minecraft.world.item.ItemStack;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;

import javax.annotation.Nullable;

@OnlyIn(Dist.CLIENT)
public class ScannerContainerScreen extends AbstractContainerScreen<ScannerContainerMenu> {
    private static final ResourceLocation BACKGROUND = new ResourceLocation(API.MOD_ID, "textures/gui/container/scanner.png");
    private static final TranslatableComponent SCANNER_MODULES_TEXT = new TranslatableComponent("gui.scannable.scanner.active_modules");
    private static final TranslatableComponent SCANNER_MODULES_TOOLTIP = new TranslatableComponent("gui.scannable.scanner.active_modules.desc");
    private static final TranslatableComponent SCANNER_MODULES_INACTIVE_TEXT = new TranslatableComponent("gui.scannable.scanner.inactive_modules");
    private static final TranslatableComponent SCANNER_MODULES_INACTIVE_TOOLTIP = new TranslatableComponent("gui.scannable.scanner.inactive_modules.desc");

    // --------------------------------------------------------------------- //

    public ScannerContainerScreen(final ScannerContainerMenu container, final Inventory inventory, final Component title) {
        super(container, inventory, title);
        imageHeight = 159;
        passEvents = false;
        inventoryLabelX = 8;
        inventoryLabelY = 65;
    }

    // --------------------------------------------------------------------- //

    @Override
    public void render(final PoseStack poseStack, final int mouseX, final int mouseY, final float partialTicks) {
        renderBackground(poseStack);
        super.render(poseStack, mouseX, mouseY, partialTicks);

        if (isHovering(8, 23, font.width(SCANNER_MODULES_TEXT), font.lineHeight, mouseX, mouseY)) {
            renderTooltip(poseStack, SCANNER_MODULES_TOOLTIP, mouseX, mouseY);
        }
        if (isHovering(8, 49, font.width(SCANNER_MODULES_INACTIVE_TEXT), font.lineHeight, mouseX, mouseY)) {
            renderTooltip(poseStack, SCANNER_MODULES_INACTIVE_TOOLTIP, mouseX, mouseY);
        }

        renderTooltip(poseStack, mouseX, mouseY);
    }

    @Override
    protected void renderLabels(final PoseStack poseStack, final int mouseX, final int mouseY) {
        super.renderLabels(poseStack, mouseX, mouseY);

        font.draw(poseStack, SCANNER_MODULES_TEXT, (float) 8, (float) 23, 0x404040);
        font.draw(poseStack, SCANNER_MODULES_INACTIVE_TEXT, (float) 8, (float) 49, 0x404040);
    }

    @Override
    protected void renderBg(final PoseStack poseStack, final float partialTicks, final int mouseX, final int mouseY) {
        RenderSystem.setShaderTexture(0, BACKGROUND);
        final int x = (width - imageWidth) / 2;
        final int y = (height - imageHeight) / 2;
        blit(poseStack, x, y, 0, 0, imageWidth, imageHeight);
    }

    @Override
    protected void slotClicked(@Nullable final Slot slot, final int slotId, final int mouseButton, final ClickType type) {
        if (slot != null) {
            final ItemStack scannerItemStack = menu.getPlayer().getItemInHand(menu.getHand());
            if (slot.getItem() == scannerItemStack) {
                return;
            }
            if (type == ClickType.SWAP && menu.getPlayer().getInventory().getItem(mouseButton) == scannerItemStack) {
                return;
            }
        }

        super.slotClicked(slot, slotId, mouseButton, type);
    }
}
