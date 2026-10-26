package com.iafenvoy.saturn.api;

import com.iafenvoy.saturn.data.ability.Ability;
import com.iafenvoy.saturn.util.formula.FormulaContext;
import net.minecraft.core.Holder;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.item.ItemStack;

import java.util.List;

/**
 * The questions the ability runtime asks of the world it is running in, which Saturn does not own.
 *
 * <p>Saturn runs a cast end to end - conditions, costs, cooldown, charges, channel, storage, events - and knows
 * nothing about damage pipelines, cultivation affinities, quality, artifacts or Curios. A dependent mod installs one
 * implementation with {@link #set} and answers as much or as little as it likes; every method defaults to the answer
 * that changes nothing, so an uninstalled hook leaves the runtime behaving exactly as it does with no extension mod.
 *
 * <p>The contract is per process, not per call: set it once from the mod constructor.
 */
public interface AbilityHooks {
    /**
     * The no-op implementation: no damage scaling, every press allowed, nothing granted by an item.
     */
    AbilityHooks NONE = new AbilityHooks() {
    };

    /**
     * The context name a damage action reads for the actor's outgoing multiplier. Content's own damage pipeline reads
     * the same name, which is why it is spelled here rather than at the call site.
     */
    String DAMAGE_MULTIPLIER = "damage_multiplier";

    /**
     * The one installed implementation. A nested class because an interface field is implicitly final.
     */
    final class Installed {
        private static volatile AbilityHooks current = NONE;
    }

    static AbilityHooks get() {
        return Installed.current;
    }

    static void set(AbilityHooks hooks) {
        Installed.current = hooks == null ? NONE : hooks;
    }

    /**
     * The outgoing damage multiplier this ability puts on its caster for the duration of one use.
     */
    default double damageMultiplier(LivingEntity actor, Holder<Ability> ability) {
        return 1.0D;
    }

    /**
     * A last word before a use is prepared: {@code false} refuses it with {@code PERMISSION_DENIED}. A dependent mod
     * whose ability types carry requirements of their own validates them here.
     */
    default boolean validate(Ability definition, Entity actor, FormulaContext context) {
        return true;
    }

    /**
     * A use was committed: this is where a criterion, a statistic or a quest hook is told about it.
     */
    default void onCommitted(ServerPlayer player, Holder<Ability> ability) {
    }

    /**
     * Whether the item may be used at all right now (a quality gate, a locked artifact). A {@code false} refuses a
     * trigger whose payload is that item.
     */
    default boolean canUseItem(LivingEntity entity, ItemStack stack) {
        return true;
    }

    /**
     * The abilities an item grants while it is carried. Empty when item-borne abilities are not a thing in your mod.
     */
    default List<Holder<Ability>> itemAbilities(LivingEntity entity, ItemStack stack) {
        return List.of();
    }

    /**
     * The stacks that count as part of the body (a Curios inventory, an accessory slot): their abilities are granted
     * under one shared source, so equipping a second one cannot release the first one's.
     */
    default List<ItemStack> equippedItems(LivingEntity entity) {
        return List.of();
    }
}
