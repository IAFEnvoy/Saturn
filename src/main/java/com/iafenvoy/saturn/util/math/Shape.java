package com.iafenvoy.saturn.util.math;

import com.mojang.serialization.Codec;
import net.minecraft.core.BlockPos;
import net.minecraft.util.StringRepresentable;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.EntitySelector;
import net.minecraft.world.level.Level;
import net.minecraft.world.phys.AABB;
import net.minecraft.world.phys.Vec3;
import org.jspecify.annotations.NonNull;

import java.util.List;
import java.util.Locale;
import java.util.stream.Stream;
import java.util.stream.StreamSupport;

/**
 * Origins-style distance shapes, measured relative to the query centre on all three axes.
 */
public enum Shape implements StringRepresentable {
    CUBE,
    STAR,
    SPHERE;

    public static final Codec<Shape> CODEC = StringRepresentable.fromEnum(Shape::values);

    public double distance(double x, double y, double z) {
        return switch (this) {
            case CUBE -> Math.max(Math.max(Math.abs(x), Math.abs(y)), Math.abs(z));
            case STAR -> Math.abs(x) + Math.abs(y) + Math.abs(z);
            case SPHERE -> Math.sqrt(x * x + y * y + z * z);
        };
    }

    public Stream<BlockPos> blocks(BlockPos center, double radius) {
        if (!Double.isFinite(radius) || radius < 0) return Stream.empty();
        int extent = (int) radius;
        return StreamSupport.stream(BlockPos.betweenClosed(center.offset(-extent, -extent, -extent), center.offset(extent, extent, extent)).spliterator(), false)
                .filter(pos -> this.distance(pos.getX() - center.getX(), pos.getY() - center.getY(), pos.getZ() - center.getZ()) <= extent)
                .map(BlockPos::immutable);
    }

    public List<Entity> entities(Level level, Vec3 center, double radius) {
        if (!Double.isFinite(radius) || radius < 0) return List.of();
        return level.getEntitiesOfClass(Entity.class, new AABB(center.subtract(radius, radius, radius), center.add(radius, radius, radius)),
                EntitySelector.NO_SPECTATORS.and(entity -> this.distance(entity.getX() - center.x, entity.getY() - center.y, entity.getZ() - center.z) <= radius));
    }

    @Override
    public @NonNull String getSerializedName() {
        return this.name().toLowerCase(Locale.ROOT);
    }
}
