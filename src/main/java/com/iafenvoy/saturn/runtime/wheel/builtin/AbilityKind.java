package com.iafenvoy.saturn.runtime.wheel.builtin;

import com.iafenvoy.saturn.Saturn;
import com.iafenvoy.saturn.api.wheel.WheelEntryKind;
import com.iafenvoy.saturn.api.wheel.WheelSource;
import com.iafenvoy.saturn.registry.SaturnDatapackRegistries;
import com.iafenvoy.saturn.registry.SaturnResourceKeys;
import com.iafenvoy.saturn.runtime.ability.AbilityActivationService;
import com.iafenvoy.saturn.runtime.wheel.WheelService;
import net.minecraft.core.RegistryAccess;
import net.minecraft.network.chat.Component;
import net.minecraft.resources.Identifier;
import net.minecraft.server.level.ServerPlayer;

public final class AbilityKind implements WheelEntryKind {
    @Override
    public Identifier id() {
        return Identifier.fromNamespaceAndPath(Saturn.MOD_ID, "ability");
    }

    @Override
    public Component displayName() {
        return Component.translatable("wheel.saturn.kind.ability");
    }

    @Override
    public boolean holdsEntry() {
        return true;
    }

    @Override
    public boolean exists(RegistryAccess access, Identifier id) {
        return SaturnDatapackRegistries.holder(access, SaturnResourceKeys.ABILITY, id)
                .filter(AbilityActivationService::togglable).isPresent();
    }

    @Override
    public boolean trigger(ServerPlayer player, WheelSource source, Identifier id) {
        return WheelService.press(player, source, id);
    }

    @Override
    public boolean directed(ServerPlayer player, WheelSource source, Identifier id, boolean wanted) {
        return WheelService.directed(player, source, id, wanted);
    }
}
