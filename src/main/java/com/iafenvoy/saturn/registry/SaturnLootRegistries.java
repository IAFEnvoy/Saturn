package com.iafenvoy.saturn.registry;

import com.iafenvoy.saturn.Saturn;
import com.iafenvoy.saturn.loot.GrantAbilityLootFunction;
import com.iafenvoy.saturn.loot.HasAbilityLootCondition;
import com.mojang.serialization.MapCodec;
import net.minecraft.core.registries.Registries;
import net.minecraft.world.level.storage.loot.functions.LootItemFunction;
import net.minecraft.world.level.storage.loot.predicates.LootItemCondition;
import net.neoforged.bus.api.IEventBus;
import net.neoforged.neoforge.registries.DeferredRegister;

/**
 * The two vanilla loot entries Saturn ships: a condition that asks whether an ability is granted, and a function
 * that grants one. Both read only the ability registry and the ability attachment, so they are vocabulary a data
 * pack writes, never a rule of a world.
 */
public final class SaturnLootRegistries {
    public static final DeferredRegister<MapCodec<? extends LootItemCondition>> CONDITIONS =
            DeferredRegister.create(Registries.LOOT_CONDITION_TYPE, Saturn.MOD_ID);
    public static final DeferredRegister<MapCodec<? extends LootItemFunction>> FUNCTIONS =
            DeferredRegister.create(Registries.LOOT_FUNCTION_TYPE, Saturn.MOD_ID);

    static {
        CONDITIONS.register("has_ability", () -> HasAbilityLootCondition.CODEC);
        FUNCTIONS.register("grant_ability", () -> GrantAbilityLootFunction.CODEC);
    }

    private SaturnLootRegistries() {
    }

    public static void register(IEventBus bus) {
        CONDITIONS.register(bus);
        FUNCTIONS.register(bus);
    }
}
