package com.iafenvoy.saturn.runtime.wheel.builtin;

import com.iafenvoy.saturn.Saturn;
import com.iafenvoy.saturn.api.wheel.WheelEntryKind;
import com.iafenvoy.saturn.api.wheel.WheelSource;
import net.minecraft.core.RegistryAccess;
import net.minecraft.network.chat.Component;
import net.minecraft.resources.Identifier;
import net.minecraft.server.level.ServerPlayer;

public final class EmptyKind implements WheelEntryKind {
    @Override
    public Identifier id() {
        return Identifier.fromNamespaceAndPath(Saturn.MOD_ID, "empty");
    }

    @Override
    public Component displayName() {
        return Component.translatable("wheel.saturn.kind.empty");
    }

    @Override
    public boolean holdsEntry() {
        return false;
    }

    @Override
    public boolean exists(RegistryAccess access, Identifier id) {
        return false;
    }

    @Override
    public boolean trigger(ServerPlayer player, WheelSource source, Identifier id) {
        return false;
    }
}
