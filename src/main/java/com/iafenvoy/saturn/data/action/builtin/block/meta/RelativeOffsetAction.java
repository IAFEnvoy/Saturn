package com.iafenvoy.saturn.data.action.builtin.block.meta;

import com.iafenvoy.saturn.data.action.BlockAction;
import com.iafenvoy.saturn.data.context.action.BlockActionContext;
import com.iafenvoy.saturn.util.formula.NumberProvider;
import com.mojang.serialization.MapCodec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import org.jspecify.annotations.NonNull;

public record RelativeOffsetAction(BlockAction action, NumberProvider distance) implements BlockAction {
    public static final MapCodec<RelativeOffsetAction> CODEC = RecordCodecBuilder.mapCodec(i -> i.group(
            BlockAction.CODEC.fieldOf("action").forGetter(RelativeOffsetAction::action),
            NumberProvider.CODEC.fieldOf("distance").forGetter(RelativeOffsetAction::distance)
    ).apply(i, RelativeOffsetAction::new));

    @Override
    public void execute(@NonNull BlockActionContext ctx) {
        int distance = (int) this.distance.evaluate(ctx.formula());
        ctx.direction().ifPresent(direction -> this.action.execute(ctx.level(), ctx.pos().relative(direction, distance), ctx.direction(), ctx));
    }

    @Override
    public @NonNull MapCodec<RelativeOffsetAction> codec() {
        return CODEC;
    }
}
