package com.iafenvoy.saturn.data.condition.builtin.item;

import com.iafenvoy.saturn.data.condition.ItemCondition;
import com.iafenvoy.saturn.data.context.condition.ItemConditionContext;
import com.iafenvoy.saturn.util.math.Comparison;
import com.mojang.serialization.MapCodec;
import org.jspecify.annotations.NonNull;

public record FuelCondition(Comparison comparison) implements ItemCondition {
    public static final MapCodec<FuelCondition> CODEC = Comparison.CODEC.xmap(FuelCondition::new, FuelCondition::comparison);

    @Override
    public boolean test(@NonNull ItemConditionContext ctx) {
        return this.comparison.compare(ctx.stack().getBurnTime(null, ctx.holder().level().fuelValues()));
    }

    @Override
    public @NonNull MapCodec<FuelCondition> codec() {
        return CODEC;
    }
}
