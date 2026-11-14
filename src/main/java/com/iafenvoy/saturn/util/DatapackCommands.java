package com.iafenvoy.saturn.util;

import net.minecraft.commands.CommandSourceStack;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.server.permissions.LevelBasedPermissionSet;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.phys.Vec3;

import java.util.concurrent.atomic.AtomicInteger;

/**
 * Commands supplied by trusted datapack definitions, run with the vanilla gamemaster command permission.
 */
public final class DatapackCommands {
    private DatapackCommands() {
    }

    public static CommandSourceStack source(Entity entity, ServerLevel level, Vec3 position) {
        return entity.createCommandSourceStackForNameResolution(level).withPosition(position)
                .withPermission(LevelBasedPermissionSet.GAMEMASTER).withSuppressedOutput();
    }

    public static int execute(Entity entity, Vec3 position, String command) {
        if (!(entity.level() instanceof ServerLevel level)) return 0;
        return execute(source(entity, level, position), command);
    }

    public static int execute(CommandSourceStack source, String command) {
        if (command.isBlank()) return 0;
        AtomicInteger result = new AtomicInteger();
        source.getServer().getCommands().performPrefixedCommand(source.withCallback((success, value) -> result.set(value)), command);
        return result.get();
    }
}
