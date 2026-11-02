package com.iafenvoy.saturn.api.playerview;

import net.minecraft.resources.Identifier;
import net.minecraft.world.entity.player.Player;

import java.util.Set;

/** Captures only the declared fields, on the player's owning thread, without changing gameplay state. */
public interface PlayerViewContributor {
    Identifier id();

    Set<PlayerViewField<?>> fields();

    void capture(Player player, PlayerViewWriter writer);
}
