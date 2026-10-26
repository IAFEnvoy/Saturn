package com.iafenvoy.saturn.registry.content;

import com.iafenvoy.saturn.Saturn;
import com.iafenvoy.saturn.data.ability.AbilityType;
import com.iafenvoy.saturn.data.ability.type.*;
import com.iafenvoy.saturn.registry.SaturnRegistries;
import com.mojang.serialization.MapCodec;
import net.neoforged.neoforge.registries.DeferredRegister;

/**
 * The built-in {@code ability_type} entries.
 */
public final class SaturnAbilityTypes {
    public static final DeferredRegister<MapCodec<? extends AbilityType>> REGISTRY = DeferredRegister.create(SaturnRegistries.ABILITY_TYPE, Saturn.MOD_ID);

    static {
        REGISTRY.register("active", () -> ActiveAbilityType.CODEC);
        REGISTRY.register("triggered", () -> TriggeredAbilityType.CODEC);
        REGISTRY.register("modifier", () -> ModifierAbilityType.CODEC);
        REGISTRY.register("aura", () -> AuraAbilityType.CODEC);
        REGISTRY.register("interval", () -> IntervalAbilityType.CODEC);
        REGISTRY.register("channelled", () -> ChannelledAbilityType.CODEC);
        REGISTRY.register("targeted", () -> TargetedAbilityType.CODEC);
        REGISTRY.register("empty", () -> EmptyAbilityType.CODEC);
        REGISTRY.register("upkeep", () -> UpkeepAbilityType.CODEC);
    }

    private SaturnAbilityTypes() {
    }
}
