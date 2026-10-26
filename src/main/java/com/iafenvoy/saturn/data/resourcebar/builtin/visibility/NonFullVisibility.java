package com.iafenvoy.saturn.data.resourcebar.builtin.visibility;

import com.iafenvoy.saturn.data.resourcebar.ResourceBarView;
import com.iafenvoy.saturn.data.resourcebar.ResourceBarVisibility;
import com.mojang.serialization.MapCodec;

public enum NonFullVisibility implements ResourceBarVisibility {
    INSTANCE;

    public static final MapCodec<NonFullVisibility> CODEC = MapCodec.unit(INSTANCE);

    @Override
    public boolean visible(ResourceBarView view) {
        return view.current() < view.maximum();
    }

    @Override
    public MapCodec<NonFullVisibility> codec() {
        return CODEC;
    }
}
