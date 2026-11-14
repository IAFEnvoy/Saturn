package com.iafenvoy.saturn.data.condition.builtin.item.meta;

import com.iafenvoy.saturn.data.condition.ItemCondition;
import com.iafenvoy.saturn.data.context.condition.ItemConditionContext;
import com.mojang.serialization.Codec;
import com.mojang.serialization.MapCodec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import org.jspecify.annotations.NonNull;

public record ConstantCondition(boolean value) implements ItemCondition {
    public static final MapCodec<ConstantCondition> CODEC = RecordCodecBuilder.mapCodec(i -> i.group(
            Codec.BOOL.fieldOf("value").forGetter(ConstantCondition::value)
    ).apply(i, ConstantCondition::new));

    @Override
    public boolean test(@NonNull ItemConditionContext ctx) {
        return this.value;
    }

    @Override
    public @NonNull MapCodec<ConstantCondition> codec() {
        return CODEC;
    }
}
