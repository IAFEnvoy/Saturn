package com.iafenvoy.saturn.runtime.ability;

import com.iafenvoy.saturn.Saturn;
import com.iafenvoy.saturn.api.AbilityHooks;
import com.iafenvoy.saturn.data.ability.Ability;
import com.iafenvoy.saturn.util.formula.FormulaContext;
import net.minecraft.core.Holder;
import net.minecraft.resources.Identifier;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.item.ItemStack;

import java.util.*;

public final class AbilityHookRegistry {
    public static final Identifier LEGACY = Identifier.fromNamespaceAndPath(Saturn.MOD_ID, "legacy_hooks");
    private static final Map<Identifier, AbilityHooks> PROVIDERS = new LinkedHashMap<>();
    private static volatile AbilityHooks combined = AbilityHooks.NONE;

    private AbilityHookRegistry() {
    }

    public static AbilityHooks get() {
        return combined;
    }

    public static synchronized void register(Identifier owner, AbilityHooks hooks) {
        Objects.requireNonNull(owner, "owner");
        if (hooks == null || hooks == AbilityHooks.NONE) PROVIDERS.remove(owner);
        else PROVIDERS.put(owner, hooks);
        combined = PROVIDERS.isEmpty() ? AbilityHooks.NONE : new Combined(List.copyOf(PROVIDERS.values()));
    }

    private record Combined(List<AbilityHooks> providers) implements AbilityHooks {
        @Override
        public double damageMultiplier(LivingEntity actor, Holder<Ability> ability) {
            double result = 1.0D;
            for (AbilityHooks hooks : this.providers) result *= hooks.damageMultiplier(actor, ability);
            return result;
        }

        @Override
        public boolean validate(Ability definition, Entity actor, FormulaContext context) {
            for (AbilityHooks hooks : this.providers) if (!hooks.validate(definition, actor, context)) return false;
            return true;
        }

        @Override
        public void onCommitted(ServerPlayer player, Holder<Ability> ability) {
            for (AbilityHooks hooks : this.providers) hooks.onCommitted(player, ability);
        }

        @Override
        public boolean canUseItem(LivingEntity entity, ItemStack stack) {
            for (AbilityHooks hooks : this.providers) if (!hooks.canUseItem(entity, stack)) return false;
            return true;
        }

        @Override
        public List<Holder<Ability>> itemAbilities(LivingEntity entity, ItemStack stack) {
            Set<Holder<Ability>> result = new LinkedHashSet<>();
            for (AbilityHooks hooks : this.providers) result.addAll(hooks.itemAbilities(entity, stack));
            return List.copyOf(result);
        }

        @Override
        public List<ItemStack> equippedItems(LivingEntity entity) {
            List<ItemStack> result = new ArrayList<>();
            Set<ItemStack> seen = Collections.newSetFromMap(new IdentityHashMap<>());
            for (AbilityHooks hooks : this.providers)
                for (ItemStack stack : hooks.equippedItems(entity)) if (seen.add(stack)) result.add(stack);
            return List.copyOf(result);
        }
    }
}
