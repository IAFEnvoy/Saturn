package com.iafenvoy.saturn.util;

import com.iafenvoy.saturn.util.codec.MiscCodecs;
import com.mojang.datafixers.util.Either;
import com.mojang.serialization.Codec;
import net.minecraft.util.StringRepresentable;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.SlotAccess;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.inventory.SlotRange;
import net.minecraft.world.inventory.SlotRanges;
import net.minecraft.world.item.ItemStack;
import org.jspecify.annotations.NonNull;

import java.util.ArrayList;
import java.util.LinkedHashSet;
import java.util.List;
import java.util.Locale;
import java.util.stream.Stream;

/**
 * Vanilla command slot IDs and ranges, with a player's selected hotbar slot counted only once.
 */
public final class EntitySlots {
    public static final Codec<List<Integer>> CODEC = MiscCodecs.combineCodec(Codec.either(Codec.INT, SlotRanges.CODEC)).xmap(
            values -> values.stream().flatMap(value -> value.map(Stream::of, range -> range.slots().stream())).toList(),
            slots -> slots.stream().map(Either::<Integer, SlotRange>left).toList());
    private static final List<String> DEFAULT_RANGES = List.of("hotbar.*", "inventory.*", "weapon.*", "armor.*", "saddle", "mob.inventory.*");

    private EntitySlots() {
    }

    public static List<SlotAccess> select(Entity entity, List<Integer> requested) {
        LinkedHashSet<Integer> slots = new LinkedHashSet<>(requested);
        if (slots.isEmpty())
            for (String name : DEFAULT_RANGES) {
                SlotRange range = SlotRanges.nameToIds(name);
                if (range != null) slots.addAll(range.slots());
            }
        if (entity instanceof Player player && slots.contains(player.getInventory().getSelectedSlot())) {
            SlotRange mainHand = SlotRanges.nameToIds("weapon.mainhand");
            if (mainHand != null) mainHand.slots().forEach(slots::remove);
        }
        List<SlotAccess> result = new ArrayList<>();
        for (int slot : slots) {
            SlotAccess access = entity.getSlot(slot);
            if (access != null) result.add(access);
        }
        return List.copyOf(result);
    }

    public enum ProcessMode implements StringRepresentable {
        STACKS,
        ITEMS;

        public static final Codec<ProcessMode> CODEC = StringRepresentable.fromEnum(ProcessMode::values);

        public int count(ItemStack stack) {
            return this == STACKS ? 1 : stack.getCount();
        }

        @Override
        public @NonNull String getSerializedName() {
            return this.name().toLowerCase(Locale.ROOT);
        }
    }

}
