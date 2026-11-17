package com.iafenvoy.saturn.api.wheel;

import com.iafenvoy.saturn.data.ability.Ability;
import net.minecraft.core.Holder;
import net.minecraft.network.chat.Component;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import org.jspecify.annotations.Nullable;

import java.util.List;

/**
 * Client-only enrichment of a generic ability tooltip. Register each owner independently.
 */
@FunctionalInterface
public interface AbilityWheelTooltip {
    /**
     * Appends content-specific lines after Saturn's generic fields. The carrier may be absent.
     */
    void append(Player player, Holder<Ability> ability, @Nullable ItemStack carrier, List<Component> lines);
}
