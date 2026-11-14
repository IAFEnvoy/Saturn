package com.iafenvoy.saturn.data.action.builtin.bientity.meta;

import com.iafenvoy.saturn.data.action.BiEntityAction;
import com.iafenvoy.saturn.data.condition.BiEntityCondition;
import com.iafenvoy.saturn.data.context.action.BiEntityActionContext;
import com.iafenvoy.saturn.data.context.condition.BiEntityConditionContext;
import com.mojang.serialization.Codec;
import com.mojang.serialization.MapCodec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import org.jspecify.annotations.NonNull;

public record WhileAction(BiEntityCondition condition, BiEntityAction action,
                          int maxIterations) implements BiEntityAction {
    public static final MapCodec<WhileAction> CODEC = RecordCodecBuilder.mapCodec(i -> i.group(
            BiEntityCondition.CODEC.fieldOf("condition").forGetter(WhileAction::condition),
            BiEntityAction.CODEC.fieldOf("action").forGetter(WhileAction::action),
            Codec.intRange(1, Integer.MAX_VALUE).lenientOptionalFieldOf("max_iterations", 1024).forGetter(WhileAction::maxIterations)
    ).apply(i, WhileAction::new));

    @Override
    public void execute(@NonNull BiEntityActionContext ctx) {
        int repetitions = 0;
        while (this.condition.test(ctx.copyTo(new BiEntityConditionContext(ctx.actor(), ctx.target(), ctx.formula())))) {
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
