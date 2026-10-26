package com.iafenvoy.saturn.data.condition.builtin.entity;

import com.iafenvoy.saturn.data.condition.BlockCondition;
import com.iafenvoy.saturn.data.condition.EntityCondition;
import com.iafenvoy.saturn.data.context.condition.EntityConditionContext;
import com.mojang.serialization.MapCodec;
import net.minecraft.world.entity.Entity;
import org.jspecify.annotations.NonNull;

public record OnBlockCondition(BlockCondition condition) implements EntityCondition {
    public static final MapCodec<OnBlockCondition> CODEC = BlockCondition.CODEC.fieldOf("condition").xmap(OnBlockCondition::new, OnBlockCondition::condition);

    @Override
    public boolean test(@NonNull EntityConditionContext ctx) {
        Entity entity = ctx.entity();
        return this.condition.test(entity.level(), entity.blockPosition().below(), ctx);
    }

    @Override
    public @NonNull MapCodec<OnBlockCondition> codec() {
        return CODEC;
    }
}
