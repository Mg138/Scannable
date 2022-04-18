package li.cil.scannable.client.scanning.filter;

import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.EntityType;

import java.util.Objects;
import java.util.function.Predicate;

@Environment(EnvType.CLIENT)
public record EntityTypeScanFilter(EntityType<?> entityType) implements Predicate<Entity> {
    @Override
    public boolean test(final Entity entity) {
        return Objects.equals(entityType, entity.getType());
    }
}
