package com.iafenvoy.saturn.runtime.wheel;

import com.iafenvoy.saturn.Saturn;
import com.iafenvoy.saturn.api.AbilityHooks;
import com.iafenvoy.saturn.api.wheel.WheelEntryKind;
import com.iafenvoy.saturn.api.wheel.WheelSource;
import com.iafenvoy.saturn.runtime.Sources;
import net.minecraft.network.chat.Component;
import net.minecraft.resources.Identifier;
import net.minecraft.world.entity.EquipmentSlot;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.item.ItemStack;

import java.util.*;

public final class WheelSourceTypes {
    public static final WheelSource CONFIGURED = new BuiltinSource("configured", null, true);
    public static final WheelSource MAIN_HAND = new BuiltinSource("main_hand", EquipmentSlot.MAINHAND, false);
    public static final WheelSource OFF_HAND = new BuiltinSource("off_hand", EquipmentSlot.OFFHAND, false);
    public static final WheelSource EQUIPPED = new BuiltinSource("equipped", null, false);
    public static final List<WheelSource> BUILT_IN = List.of(CONFIGURED, MAIN_HAND, OFF_HAND, EQUIPPED);
    private static final Map<Identifier, WheelSource> REGISTERED = new LinkedHashMap<>();

    static {
        BUILT_IN.forEach(WheelSourceTypes::register);
    }

    private WheelSourceTypes() {
    }

    public static synchronized void register(WheelSource source) {
        REGISTERED.putIfAbsent(source.id(), source);
    }

    public static synchronized Optional<WheelSource> byId(Identifier id) {
        return Optional.ofNullable(REGISTERED.get(id));
    }

    public static synchronized List<WheelSource> pages() {
        return List.copyOf(REGISTERED.values());
    }

    public static WheelSource first() {
        return CONFIGURED;
    }

    public static WheelSource step(WheelSource from, int delta) {
        List<WheelSource> pages = pages();
        int index = 0;
        for (int i = 0; i < pages.size(); i++) if (pages.get(i).is(from)) index = i;
        return pages.get(Math.floorMod(index + delta, pages.size()));
    }

    public static int pageNumber(WheelSource source) {
        List<WheelSource> pages = pages();
        for (int i = 0; i < pages.size(); i++) if (pages.get(i).is(source)) return i + 1;
        return 0;
    }

    private record BuiltinSource(String path, EquipmentSlot slot, boolean configured) implements WheelSource {
        @Override
        public Identifier id() {
            return Identifier.fromNamespaceAndPath(Saturn.MOD_ID, this.path);
        }

        @Override
        public Component displayName() {
            return Component.translatable("wheel.saturn.source." + this.path);
        }

        @Override
        public List<Identifier> grantSources(LivingEntity entity) {
            return this.configured ? List.of() : this.slot == null ? List.of(Sources.EQUIPPED)
                    : List.of(Sources.equipment(this.slot, entity.getItemBySlot(this.slot)));
        }

        @Override
        public List<ItemStack> equipment(LivingEntity entity) {
            if (this.slot != null) return List.of(entity.getItemBySlot(this.slot));
            List<ItemStack> stacks = new ArrayList<>(AbilityHooks.get().equippedItems(entity));
            if (this.configured)
                for (EquipmentSlot slot : EquipmentSlot.values()) stacks.add(entity.getItemBySlot(slot));
            return List.copyOf(stacks);
        }

        @Override
        public boolean offers(LivingEntity entity, WheelEntryKind kind, Identifier id) {
            return this.configured ? WheelSources.inLayout(entity, kind, id) : WheelSources.granted(entity, this, kind, id);
        }
    }
}
