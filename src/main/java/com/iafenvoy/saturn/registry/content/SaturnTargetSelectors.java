package com.iafenvoy.saturn.registry.content;

import com.iafenvoy.saturn.Saturn;
import com.iafenvoy.saturn.data.ability.TargetSelector;
import com.iafenvoy.saturn.data.ability.target.*;
import com.iafenvoy.saturn.registry.SaturnRegistries;
import com.mojang.serialization.MapCodec;
import net.neoforged.fml.ModList;
import net.neoforged.neoforge.registries.DeferredRegister;

/**
 * The built-in {@code ability_target_selector_type} entries.
 */
public final class SaturnTargetSelectors {
    public static final DeferredRegister<MapCodec<? extends TargetSelector>> REGISTRY = DeferredRegister.create(SaturnRegistries.ABILITY_TARGET_SELECTOR_TYPE, Saturn.MOD_ID);

    static {
        REGISTRY.register("self", () -> SelfTargetSelector.CODEC);
        REGISTRY.register("area", () -> AreaTargetSelector.CODEC);
        REGISTRY.register("ray", () -> RayTargetSelector.CODEC);
        REGISTRY.register("cone", () -> ConeTargetSelector.CODEC);
        // KubeJS is compile-only: without the mod present the bridge classes must not be touched.
        if (ModList.get().isLoaded("kubejs")) REGISTRY.register("js", () -> JsTargetSelector.CODEC);
    }

    private SaturnTargetSelectors() {
    }
}
