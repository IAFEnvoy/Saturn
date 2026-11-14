package com.iafenvoy.saturn.data.action.builtin.entity.meta;

import com.iafenvoy.saturn.data.action.EntityAction;
import com.iafenvoy.saturn.data.condition.EntityCondition;
import com.iafenvoy.saturn.data.context.action.EntityActionContext;
import com.iafenvoy.saturn.data.context.condition.EntityConditionContext;
import com.mojang.serialization.Codec;
import com.mojang.serialization.MapCodec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import org.jspecify.annotations.NonNull;

public record WhileAction(EntityCondition condition, EntityAction action, int maxIterations) implements EntityAction {
    public static final MapCodec<WhileAction> CODEC = RecordCodecBuilder.mapCodec(i -> i.group(
            EntityCondition.CODEC.fieldOf("condition").forGetter(WhileAction::condition),
            EntityAction.CODEC.fieldOf("action").forGetter(WhileAction::action),
            Codec.intRange(1, Integer.MAX_VALUE).lenientOptionalFieldOf("max_iterations", 1024).forGetter(WhileAction::maxIterations)
    ).apply(i, WhileAction::new));

    @Override
    public void execute(@NonNull EntityActionContext ctx) {
        int repetitions = 0;
        while (this.condition.test(ctx.copyTo(new EntityConditionContext(ctx.entity(), ctx.formula())))) {
            if (repetitions++ >= this.maxIterations)
                throw new IllegalStateException("A saturn:while action exceeded " + this.maxIterations + " iterations");
            this.action.execute(ctx);
        }
    }

    @Override
    public @NonNull MapCodec<WhileAction> codec() {
        return CODEC;
    }
}
