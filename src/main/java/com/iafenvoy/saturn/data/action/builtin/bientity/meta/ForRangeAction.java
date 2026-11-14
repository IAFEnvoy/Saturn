package com.iafenvoy.saturn.data.action.builtin.bientity.meta;

import com.iafenvoy.saturn.data.action.BiEntityAction;
import com.iafenvoy.saturn.data.context.action.BiEntityActionContext;
import com.iafenvoy.saturn.util.formula.NumberProvider;
import com.mojang.serialization.Codec;
import com.mojang.serialization.MapCodec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import org.jspecify.annotations.NonNull;

public record ForRangeAction(NumberProvider range, BiEntityAction action, int maxIterations) implements BiEntityAction {
    public static final MapCodec<ForRangeAction> CODEC = RecordCodecBuilder.mapCodec(i -> i.group(
            NumberProvider.CODEC.fieldOf("range").forGetter(ForRangeAction::range),
            BiEntityAction.CODEC.fieldOf("action").forGetter(ForRangeAction::action),
            Codec.intRange(1, Integer.MAX_VALUE).lenientOptionalFieldOf("max_iterations", 1024).forGetter(ForRangeAction::maxIterations)
    ).apply(i, ForRangeAction::new));

    @Override
    public void execute(@NonNull BiEntityActionContext ctx) {
        int count = Math.min((int) this.range.evaluate(ctx.formula()), this.maxIterations);
        for (int index = 0; index < count; index++) this.action.execute(ctx);
    }

    @Override
    public @NonNull MapCodec<ForRangeAction> codec() {
        return CODEC;
    }
}
