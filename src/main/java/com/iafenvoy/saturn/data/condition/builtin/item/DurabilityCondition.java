package com.iafenvoy.saturn.data.condition.builtin.item;

import com.iafenvoy.saturn.data.condition.ItemCondition;
import com.iafenvoy.saturn.data.context.condition.ItemConditionContext;
import com.iafenvoy.saturn.util.math.Comparison;
import com.mojang.serialization.MapCodec;
import org.jspecify.annotations.NonNull;

public record DurabilityCondition(Comparison comparison) implements ItemCondition {
    public static final MapCodec<DurabilityCondition> CODEC = Comparison.CODEC.xmap(DurabilityCondition::new, DurabilityCondition::comparison);

    @Override
    public boolean test(@NonNull ItemConditionContext ctx) {
        return ctx.stack().isDamageableItem() && this.comparison.compare(ctx.stack().getMaxDamage() - ctx.stack().getDamageValue());
    }

    @Override
    public @NonNull MapCodec<DurabilityCondition> codec() {
        return CODEC;
    }
}
