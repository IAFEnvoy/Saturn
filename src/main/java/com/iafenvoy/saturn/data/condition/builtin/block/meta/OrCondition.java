package com.iafenvoy.saturn.data.condition.builtin.block.meta;

import com.iafenvoy.saturn.data.condition.BlockCondition;
import com.iafenvoy.saturn.data.context.condition.BlockConditionContext;
import com.mojang.serialization.MapCodec;
import org.jspecify.annotations.NonNull;

import java.util.List;

public record OrCondition(List<BlockCondition> conditions) implements BlockCondition {
    public static final MapCodec<OrCondition> CODEC = SINGLE_CODEC.listOf().fieldOf("conditions").xmap(OrCondition::new, OrCondition::conditions);

    @Override
    public boolean test(@NonNull BlockConditionContext ctx) {
        return this.conditions.stream().anyMatch(condition -> condition.test(ctx.level(), ctx.pos(), ctx));
    }

    @Override
    public @NonNull MapCodec<OrCondition> codec() {
        return CODEC;
    }
}
