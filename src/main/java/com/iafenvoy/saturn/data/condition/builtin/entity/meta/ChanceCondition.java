package com.iafenvoy.saturn.data.condition.builtin.entity.meta;

import com.iafenvoy.saturn.data.condition.EntityCondition;
import com.iafenvoy.saturn.data.context.condition.EntityConditionContext;
import com.mojang.serialization.Codec;
import com.mojang.serialization.MapCodec;
import org.jspecify.annotations.NonNull;

public record ChanceCondition(double chance) implements EntityCondition {
    public static final MapCodec<ChanceCondition> CODEC = Codec.doubleRange(0.0D, 1.0D).fieldOf("chance").xmap(ChanceCondition::new, ChanceCondition::chance);

    @Override
    public boolean test(@NonNull EntityConditionContext ctx) {
        return ctx.entity().getRandom().nextDouble() < this.chance;
    }

    @Override
    public @NonNull MapCodec<ChanceCondition> codec() {
        return CODEC;
    }
}
