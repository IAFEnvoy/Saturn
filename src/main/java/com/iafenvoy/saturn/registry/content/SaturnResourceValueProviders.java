package com.iafenvoy.saturn.registry.content;

import com.iafenvoy.saturn.Saturn;
import com.iafenvoy.saturn.data.resource.ResourceValueProvider;
import com.iafenvoy.saturn.data.resource.builtin.*;
import com.iafenvoy.saturn.registry.SaturnRegistries;
import com.mojang.serialization.MapCodec;
import net.neoforged.fml.ModList;
import net.neoforged.neoforge.registries.DeferredRegister;

/**
 * The built-in {@code resource_value_provider_type} entries.
 */
public final class SaturnResourceValueProviders {
    public static final DeferredRegister<MapCodec<? extends ResourceValueProvider>> REGISTRY = DeferredRegister.create(SaturnRegistries.RESOURCE_VALUE_PROVIDER_TYPE, Saturn.MOD_ID);

    static {
        REGISTRY.register("current", () -> CurrentResourceValueProvider.CODEC);
        REGISTRY.register("max", () -> MaximumResourceValueProvider.CODEC);
        REGISTRY.register("missing", () -> MissingResourceValueProvider.CODEC);
        REGISTRY.register("constant", () -> ConstantResourceValueProvider.CODEC);
        // KubeJS is compile-only: without the mod present the bridge classes must not be touched.
        if (ModList.get().isLoaded("kubejs")) REGISTRY.register("js", () -> JsResourceValueProvider.CODEC);
    }

    private SaturnResourceValueProviders() {
    }
}
