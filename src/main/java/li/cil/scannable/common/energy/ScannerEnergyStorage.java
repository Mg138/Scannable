package li.cil.scannable.common.energy;

import li.cil.scannable.common.config.CommonConfig;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.nbt.Tag;
import net.minecraft.world.item.ItemStack;
import net.minecraftforge.energy.EnergyStorage;

public final class ScannerEnergyStorage extends EnergyStorage {
    private static final String TAG_ENERGY = "energy";

    private final ItemStack container;

    public static ScannerEnergyStorage of(final ItemStack stack) {
        final ScannerEnergyStorage energyStorage = new ScannerEnergyStorage(stack);
        energyStorage.updateFromNBT();
        return energyStorage;
    }

    public ScannerEnergyStorage(final ItemStack container) {
        super(CommonConfig.energyCapacityScanner);
        this.container = container;
    }

    public void updateFromNBT() {
        final CompoundTag tag = container.getTag();
        if (tag != null && tag.contains(TAG_ENERGY, Tag.TAG_INT)) {
            deserializeNBT(tag.get(TAG_ENERGY));
        }
    }

    // --------------------------------------------------------------------- //
    // IEnergyStorage

    @Override
    public int receiveEnergy(final int maxReceive, final boolean simulate) {
        if (!CommonConfig.useEnergy) {
            return 0;
        }

        final int energyReceived = super.receiveEnergy(maxReceive, simulate);
        if (!simulate && energyReceived != 0) {
            container.addTagElement(TAG_ENERGY, serializeNBT());
        }

        return energyReceived;
    }

    @Override
    public int extractEnergy(final int maxExtract, final boolean simulate) {
        if (!CommonConfig.useEnergy) {
            return 0;
        }

        final int energyExtracted = super.extractEnergy(maxExtract, simulate);
        if (!simulate && energyExtracted != 0) {
            container.addTagElement(TAG_ENERGY, serializeNBT());
        }

        return energyExtracted;
    }
}
