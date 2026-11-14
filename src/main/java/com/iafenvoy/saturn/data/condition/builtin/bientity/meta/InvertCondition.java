package com.iafenvoy.saturn.data.condition.builtin.bientity.meta;

import com.iafenvoy.saturn.data.condition.BiEntityCondition;
import com.iafenvoy.saturn.data.context.condition.BiEntityConditionContext;
import com.mojang.serialization.MapCodec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import org.jspecify.annotations.NonNull;

public record InvertCondition(BiEntityCondition condition) implements BiEntityCondition {
    public static final MapCodec<InvertCondition> CODEC = RecordCodecBuilder.mapCodec(i -> i.group(
            BiEntityCondition.CODEC.fieldOf("condition").forGetter(InvertCondition::condition)
    ).apply(i, InvertCondition::new));

    @Override
    public boolean test(@NonNull BiEntityConditionContext ctx) {
        return this.condition.test(ctx.target(), ctx.actor(), ctx);
    }

    @Override
    public @NonNull MapCodec<InvertCondition> codec() {
        return CODEC;
    }
}
