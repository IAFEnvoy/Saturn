package com.iafenvoy.saturn.data.action.builtin.item.meta;

import com.iafenvoy.saturn.data.action.ItemAction;
import com.iafenvoy.saturn.data.condition.ItemCondition;
import com.iafenvoy.saturn.data.context.action.ItemActionContext;
import com.iafenvoy.saturn.data.context.condition.ItemConditionContext;
import com.mojang.serialization.Codec;
import com.mojang.serialization.MapCodec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import org.jspecify.annotations.NonNull;

public record WhileAction(ItemCondition condition, ItemAction action, int maxIterations) implements ItemAction {
    public static final MapCodec<WhileAction> CODEC = RecordCodecBuilder.mapCodec(i -> i.group(
            ItemCondition.CODEC.fieldOf("condition").forGetter(WhileAction::condition),
            ItemAction.CODEC.fieldOf("action").forGetter(WhileAction::action),
            Codec.intRange(1, Integer.MAX_VALUE).lenientOptionalFieldOf("max_iterations", 1024).forGetter(WhileAction::maxIterations)
    ).apply(i, WhileAction::new));

    @Override
    public void execute(@NonNull ItemActionContext ctx) {
        int repetitions = 0;
        while (this.condition.test(ctx.copyTo(new ItemConditionContext(ctx.holder(), ctx.stack(), ctx.formula())))) {
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
