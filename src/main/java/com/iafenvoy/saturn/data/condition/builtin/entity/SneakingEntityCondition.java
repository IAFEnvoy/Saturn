package com.iafenvoy.saturn.data.condition.builtin.entity;

import com.iafenvoy.saturn.data.condition.EntityCondition;
import com.iafenvoy.saturn.data.context.condition.EntityConditionContext;
import com.mojang.serialization.MapCodec;
import org.jspecify.annotations.NonNull;

public enum SneakingEntityCondition implements EntityCondition {
    INSTANCE;
    public static final MapCodec<SneakingEntityCondition> CODEC = MapCodec.unit(INSTANCE);

    @Override
    public boolean test(@NonNull EntityConditionContext ctx) {
        return ctx.entity().isShiftKeyDown();
    }

    @Override
    public @NonNull MapCodec<SneakingEntityCondition> codec() {
        return CODEC;
    }
}
