package com.iafenvoy.saturn.data.condition.builtin.item.meta;

import com.iafenvoy.saturn.data.condition.ItemCondition;
import com.iafenvoy.saturn.data.context.condition.ItemConditionContext;
import com.mojang.serialization.MapCodec;
import org.jspecify.annotations.NonNull;

import java.util.List;

public record OrCondition(List<ItemCondition> conditions) implements ItemCondition {
    public static final MapCodec<OrCondition> CODEC = SINGLE_CODEC.listOf().fieldOf("conditions").xmap(OrCondition::new, OrCondition::conditions);

    @Override
    public boolean test(@NonNull ItemConditionContext ctx) {
        return this.conditions.stream().anyMatch(condition -> condition.test(ctx.holder(), ctx.stack(), ctx));
    }

    @Override
    public @NonNull MapCodec<OrCondition> codec() {
        return CODEC;
    }
}
