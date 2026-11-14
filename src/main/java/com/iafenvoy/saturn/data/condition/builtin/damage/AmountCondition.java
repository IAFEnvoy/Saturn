package com.iafenvoy.saturn.data.condition.builtin.damage;

import com.iafenvoy.saturn.data.condition.DamageCondition;
import com.iafenvoy.saturn.data.context.condition.DamageConditionContext;
import com.iafenvoy.saturn.util.math.Comparison;
import com.mojang.serialization.MapCodec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import org.jspecify.annotations.NonNull;

public record AmountCondition(Comparison comparison) implements DamageCondition {
    public static final MapCodec<AmountCondition> CODEC = RecordCodecBuilder.mapCodec(i -> i.group(
            Comparison.CODEC.forGetter(AmountCondition::comparison)
    ).apply(i, AmountCondition::new));

    @Override
    public boolean test(@NonNull DamageConditionContext ctx) {
        return this.comparison.compare(ctx.amount());
    }

    @Override
    public @NonNull MapCodec<AmountCondition> codec() {
        return CODEC;
    }
}
