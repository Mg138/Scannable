package li.cil.scannable.api.scanning;

import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;

import java.util.function.Predicate;

/**
 * Specialized interface for using the built-in block scan result provider.
 * <p>
 * When implementing this interface, return the result provider implementation
 * obtained from the provider registry like so:
 * <pre>
 * RegistryManager.ACTIVE.getRegistry(ScanResultProvider.class).getValue(API.SCAN_RESULT_PROVIDER_BLOCKS);
 * </pre>
 */
public interface BlockScannerModule extends ScannerModule {
    /**
     * Modifies the local range of the scan. Modules can boost or reduce the range
     * for only their own filtering via {@link #getFilter(ItemStack)}.
     *
     * @param range the input range.
     * @return the adjusted range.
     */
    @OnlyIn(Dist.CLIENT)
    default float adjustLocalRange(final float range) {
        return range;
    }

    /**
     * Get a filter that will be used for testing whether blocks should be included
     * in the scan result.
     *
     * @param module the module to get the filter for.
     * @return the filter to use.
     */
    @OnlyIn(Dist.CLIENT)
    Predicate<BlockState> getFilter(final ItemStack module);
}
