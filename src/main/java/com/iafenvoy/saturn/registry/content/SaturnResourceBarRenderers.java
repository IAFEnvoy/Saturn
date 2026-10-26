package com.iafenvoy.saturn.registry.content;

import com.iafenvoy.saturn.Saturn;
import com.iafenvoy.saturn.data.resourcebar.ResourceBarRenderData;
import com.iafenvoy.saturn.data.resourcebar.builtin.renderdata.*;
import com.iafenvoy.saturn.registry.SaturnRegistries;
import com.mojang.serialization.MapCodec;
import net.neoforged.neoforge.registries.DeferredRegister;

/**
 * The built-in {@code resource_bar_render_data_type} entries.
 */
public final class SaturnResourceBarRenderers {
    public static final DeferredRegister<MapCodec<? extends ResourceBarRenderData>> REGISTRY = DeferredRegister.create(SaturnRegistries.RESOURCE_BAR_RENDER_DATA_TYPE, Saturn.MOD_ID);

    static {
        REGISTRY.register("textured_bar", () -> TexturedRenderData.CODEC);
        REGISTRY.register("segmented_bar", () -> SegmentedRenderData.CODEC);
        REGISTRY.register("radial_bar", () -> RadialRenderData.CODEC);
        REGISTRY.register("text_only", () -> TextOnlyRenderData.CODEC);
        REGISTRY.register("boss_bar", () -> OriginsRenderData.CODEC);
        REGISTRY.register("missing", () -> MissingRenderData.CODEC);
    }

    private SaturnResourceBarRenderers() {
    }
}
