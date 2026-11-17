package com.iafenvoy.saturn.runtime.wheel;

import com.iafenvoy.saturn.api.AbilityHooks;
import com.iafenvoy.saturn.api.wheel.WheelEntryKind;
import com.iafenvoy.saturn.api.wheel.WheelSource;
import com.iafenvoy.saturn.attachment.AbilityAttachment;
import com.iafenvoy.saturn.attachment.WheelLayoutAttachment;
import com.iafenvoy.saturn.data.ability.Ability;
import com.iafenvoy.saturn.registry.SaturnAttachments;
import com.iafenvoy.saturn.runtime.Sources;
import com.iafenvoy.saturn.runtime.ability.AbilityActivationService;
import com.iafenvoy.saturn.util.HolderHelper;
import net.minecraft.core.Holder;
import net.minecraft.resources.Identifier;
import net.minecraft.world.entity.EquipmentSlot;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.item.ItemStack;

import java.util.*;

public final class WheelSources {
    private WheelSources() {
    }

    // In id order, so the same moment always gives the same slot. Only abilities that can be pressed, since they
    // are the only kind the wheel can trigger. Deliberately not cut to one page - how many entries fit is the
    // client's business, and all the server asks is whether one id is in it.
    public static List<Holder<Ability>> abilities(LivingEntity entity, WheelSource source) {
        List<Identifier> sources = source.grantSources(entity);
        if (sources.isEmpty()) return List.of();
        AbilityAttachment holder = entity.getExistingData(SaturnAttachments.ABILITY_HOLDER).orElse(null);
        if (holder == null) return List.of();
        return abilities(entity).stream()
                .filter(ability -> holder.sources().of(ability).stream().anyMatch(sources::contains))
                .toList();
    }

    // Every pressable ability the player holds, whatever granted it: the ledger the editor's pool and the saved
    // cells' resolution list are both built from, read here rather than from the equipment so neither depends on
    // where the thing that grants it happens to be.
    public static List<Holder<Ability>> abilities(LivingEntity entity) {
        AbilityAttachment holder = entity.getExistingData(SaturnAttachments.ABILITY_HOLDER).orElse(null);
        if (holder == null) return List.of();
        LinkedHashSet<Holder<Ability>> found = new LinkedHashSet<>();
        holder.sources().keys().stream()
                .sorted(Comparator.comparing(ability -> HolderHelper.id(ability).toString()))
                .filter(AbilityActivationService::togglable)
                .forEach(found::add);
        return List.copyOf(found);
    }

    // Where one ability's grant comes from right now; empty once nothing holds it, which is a different answer
    // from "held by something the tooltip cannot name".
    public static Set<Identifier> sources(LivingEntity entity, Holder<Ability> ability) {
        AbilityAttachment holder = entity.getExistingData(SaturnAttachments.ABILITY_HOLDER).orElse(null);
        return holder == null ? Set.of() : holder.sources().of(ability);
    }

    // True when a hand is the only thing holding it: such a skill belongs on that item's own page, because the
    // hand is whatever is picked up next. Armor and extension equipment remain configurable.
    public static boolean handOnly(LivingEntity entity, Holder<Ability> ability) {
        Set<Identifier> sources = sources(entity, ability);
        Identifier main = Sources.equipment(EquipmentSlot.MAINHAND, entity.getMainHandItem());
        Identifier off = Sources.equipment(EquipmentSlot.OFFHAND, entity.getOffhandItem());
        return !sources.isEmpty() && sources.stream().allMatch(source -> source.equals(main) || source.equals(off));
    }

    // The stack an ability acts on, for one page: the first item the page names that offers it. Empty when the
    // ability came from somewhere else entirely (a book, a command), which an item-side ability reports as
    // NO_CARRIER instead of guessing.
    public static Optional<ItemStack> carrier(LivingEntity entity, WheelSource source, Holder<Ability> ability) {
        return Optional.ofNullable(carriers(entity, source).get(ability));
    }

    // Every ability this page's equipment offers, mapped to the stack offering it, in one pass over the stacks:
    // item definitions are resolved once per stack instead of once per cell.
    public static Map<Holder<Ability>, ItemStack> carriers(LivingEntity entity, WheelSource source) {
        Map<Holder<Ability>, ItemStack> found = new LinkedHashMap<>();
        for (ItemStack stack : source.equipment(entity)) {
            if (stack.isEmpty()) continue;
            for (Holder<Ability> ability : AbilityHooks.get().itemAbilities(entity, stack))
                found.putIfAbsent(ability, stack);
        }
        return found;
    }

    // The check behind every trigger: the id alone is not enough, because the same ability can come from something
    // the player no longer has. Which page answers that question is the page's own business.
    public static boolean offers(LivingEntity entity, WheelSource source, WheelEntryKind kind, Identifier id) {
        if (kind == null || id == null || !kind.holdsEntry()) return false;
        return source.offers(entity, kind, id);
    }

    // Whether the player's saved layout holds that cell, which is what the configured page answers with.
    static boolean inLayout(LivingEntity entity, WheelEntryKind kind, Identifier id) {
        WheelLayout layout = entity.getExistingData(SaturnAttachments.WHEEL_LAYOUT)
                .flatMap(WheelLayoutAttachment::layout).orElse(WheelLayout.EMPTY);
        for (int sector = 0; sector < WheelLayout.SLOTS; sector++) {
            WheelSlot slot = layout.slot(sector);
            if (slot.kind().is(kind) && slot.id().equals(id)) return true;
        }
        return false;
    }

    // Whether a derived page's own grants currently offer that ability, which is the answer every equipment page
    // and extension equipment pages give.
    static boolean granted(LivingEntity entity, WheelSource source, WheelEntryKind kind, Identifier id) {
        return kind.is(WheelEntryKinds.ABILITY)
                && abilities(entity, source).stream().anyMatch(ability -> ability.is(id));
    }
}
