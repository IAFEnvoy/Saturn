package com.iafenvoy.saturn.data.condition.builtin.block.meta;

import com.iafenvoy.saturn.data.condition.BlockCondition;
import com.iafenvoy.saturn.data.context.condition.BlockConditionContext;
import com.mojang.serialization.Codec;
import com.mojang.serialization.MapCodec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import org.jspecify.annotations.NonNull;

public record ConstantCondition(boolean value) implements BlockCondition {
    public static final MapCodec<ConstantCondition> CODEC = RecordCodecBuilder.mapCodec(i -> i.group(
            Codec.BOOL.fieldOf("value").forGetter(ConstantCondition::value)
    ).apply(i, ConstantCondition::new));

    @Override
    public boolean test(@NonNull BlockConditionContext ctx) {
        return this.value;
    }

    @Override
    public @NonNull MapCodec<ConstantCondition> codec() {
        return CODEC;
    }
}
