package com.iafenvoy.saturn.data.resourcebar;

import com.iafenvoy.saturn.registry.SaturnRegistries;
import com.mojang.serialization.Codec;
import com.mojang.serialization.MapCodec;

import java.util.function.Function;

public interface ResourceBarRenderData {
    Codec<ResourceBarRenderData> CODEC = SaturnRegistries.RESOURCE_BAR_RENDER_DATA_TYPE.byNameCodec().dispatch("type", ResourceBarRenderData::codec, Function.identity());

    MapCodec<? extends ResourceBarRenderData> codec();

    default int width() {
        return 71;
    }

    default int height() {
        return 8;
    }
}
