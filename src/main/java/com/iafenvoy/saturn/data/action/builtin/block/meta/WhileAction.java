package com.iafenvoy.saturn.data.action.builtin.block.meta;

import com.iafenvoy.saturn.data.action.BlockAction;
import com.iafenvoy.saturn.data.condition.BlockCondition;
import com.iafenvoy.saturn.data.context.action.BlockActionContext;
import com.iafenvoy.saturn.data.context.condition.BlockConditionContext;
import com.mojang.serialization.Codec;
import com.mojang.serialization.MapCodec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import org.jspecify.annotations.NonNull;

public record WhileAction(BlockCondition condition, BlockAction action, int maxIterations) implements BlockAction {
    public static final MapCodec<WhileAction> CODEC = RecordCodecBuilder.mapCodec(i -> i.group(
            BlockCondition.CODEC.fieldOf("condition").forGetter(WhileAction::condition),
            BlockAction.CODEC.fieldOf("action").forGetter(WhileAction::action),
            Codec.intRange(1, Integer.MAX_VALUE).lenientOptionalFieldOf("max_iterations", 1024).forGetter(WhileAction::maxIterations)
    ).apply(i, WhileAction::new));

    @Override
    public void execute(@NonNull BlockActionContext ctx) {
        int repetitions = 0;
        while (this.condition.test(ctx.copyTo(new BlockConditionContext(ctx.level(), ctx.pos(), ctx.formula())))) {
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
