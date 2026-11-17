package com.iafenvoy.saturn.api.wheel;

import net.minecraft.world.entity.player.Player;

import java.util.List;

/**
 * Client-side entries for one registered kind. Register during client setup, never common setup.
 */
@FunctionalInterface
public interface WheelEntryProvider {
    /**
     * Entries visible in the combined configuration pool. Called again when the screen is opened.
     */
    List<WheelMenuEntry> options(Player player);

    /**
     * Entries that can resolve saved slots, including ones excluded from new configuration.
     */
    default List<WheelMenuEntry> available(Player player) {
        return this.options(player);
    }
}
