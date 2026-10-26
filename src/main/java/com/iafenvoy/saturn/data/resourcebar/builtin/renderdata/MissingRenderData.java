package com.iafenvoy.saturn.data.resourcebar.builtin.renderdata;

import com.iafenvoy.saturn.data.resourcebar.ResourceBarRenderData;
import com.mojang.serialization.MapCodec;

public enum MissingRenderData implements ResourceBarRenderData {
    INSTANCE;

    public static final MapCodec<MissingRenderData> CODEC = MapCodec.unit(INSTANCE);

    @Override
    public MapCodec<MissingRenderData> codec() {
        return CODEC;
    }
}
