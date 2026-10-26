package com.iafenvoy.saturn.data.condition.builtin.entity.meta;

import com.iafenvoy.saturn.data.condition.EntityCondition;
import com.iafenvoy.saturn.data.context.condition.EntityConditionContext;
import com.mojang.serialization.MapCodec;
import org.jspecify.annotations.NonNull;

import java.util.List;

public record AndEntityCondition(List<EntityCondition> conditions) implements EntityCondition {
    public static final MapCodec<AndEntityCondition> CODEC = SINGLE_CODEC.listOf().fieldOf("conditions").xmap(AndEntityCondition::new, AndEntityCondition::conditions);

    @Override
    public boolean test(@NonNull EntityConditionContext ctx) {
        return this.conditions.stream().allMatch(condition -> condition.test(ctx.entity(), ctx));
    }

    @Override
    public @NonNull MapCodec<AndEntityCondition> codec() {
        return CODEC;
    }
}
