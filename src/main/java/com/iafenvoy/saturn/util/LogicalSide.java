package com.iafenvoy.saturn.util;

import com.mojang.serialization.Codec;
import net.minecraft.util.StringRepresentable;
import net.minecraft.world.level.Level;
import org.jspecify.annotations.NonNull;

/**
 * The logical level side, including the integrated server inside a client process.
 */
public enum LogicalSide implements StringRepresentable {
    CLIENT,
    SERVER;

    public static final Codec<LogicalSide> CODEC = StringRepresentable.fromEnum(LogicalSide::values);

    public boolean matches(Level level) {
        return level.isClientSide() == (this == CLIENT);
    }

    @Override
    public @NonNull String getSerializedName() {
        return this == CLIENT ? "client" : "server";
    }
}
