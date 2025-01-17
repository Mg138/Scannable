package li.cil.scannable.client.scanning.filter;

import net.minecraft.tags.TagKey;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.material.Fluid;
import net.minecraft.world.level.material.FluidState;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;

import java.util.function.Predicate;

@OnlyIn(Dist.CLIENT)
public record FluidTagScanFilter(TagKey<Fluid> tag) implements Predicate<BlockState> {
    @Override
    public boolean test(final BlockState state) {
        final FluidState fluidState = state.getFluidState();
        return !fluidState.isEmpty() && fluidState.is(tag);
    }
}
