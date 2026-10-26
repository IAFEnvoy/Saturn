package com.iafenvoy.saturn.registry.content;

import com.iafenvoy.saturn.Saturn;
import com.iafenvoy.saturn.data.resourcebar.ResourceBarVisibility;
import com.iafenvoy.saturn.data.resourcebar.builtin.visibility.*;
import com.iafenvoy.saturn.registry.SaturnRegistries;
import com.mojang.serialization.MapCodec;
import net.neoforged.neoforge.registries.DeferredRegister;

/**
 * The built-in {@code resource_bar_visibility_type} entries.
 */
public final class SaturnResourceBarVisibilities {
    public static final DeferredRegister<MapCodec<? extends ResourceBarVisibility>> REGISTRY = DeferredRegister.create(SaturnRegistries.RESOURCE_BAR_VISIBILITY_TYPE, Saturn.MOD_ID);

    static {
        REGISTRY.register("always", () -> AlwaysVisibility.CODEC);
        REGISTRY.register("non_full", () -> NonFullVisibility.CODEC);
        REGISTRY.register("non_zero", () -> NonZeroVisibility.CODEC);
        REGISTRY.register("recently_changed", () -> RecentlyChangedVisibility.CODEC);
        REGISTRY.register("resource_range", () -> RangeVisibility.CODEC);
        REGISTRY.register("and", () -> AndVisibility.CODEC);
        REGISTRY.register("or", () -> OrVisibility.CODEC);
        REGISTRY.register("not", () -> NotVisibility.CODEC);
    }

    private SaturnResourceBarVisibilities() {
    }
}
