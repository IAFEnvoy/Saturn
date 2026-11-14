package com.iafenvoy.saturn.data.action.builtin.block.meta;

import com.iafenvoy.saturn.data.action.BlockAction;
import com.iafenvoy.saturn.data.condition.BlockCondition;
import com.iafenvoy.saturn.data.context.action.BlockActionContext;
import com.iafenvoy.saturn.util.formula.NumberProvider;
import com.iafenvoy.saturn.util.formula.number.Constant;
import com.iafenvoy.saturn.util.math.Shape;
import com.mojang.serialization.MapCodec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import org.jspecify.annotations.NonNull;

public record RegionApplyAction(NumberProvider radius, Shape shape, BlockAction blockAction,
                                BlockCondition blockCondition) implements BlockAction {
    public static final MapCodec<RegionApplyAction> CODEC = RecordCodecBuilder.mapCodec(i -> i.group(
            NumberProvider.CODEC.lenientOptionalFieldOf("radius", new Constant(16)).forGetter(RegionApplyAction::radius),
            Shape.CODEC.lenientOptionalFieldOf("shape", Shape.CUBE).forGetter(RegionApplyAction::shape),
            BlockAction.CODEC.fieldOf("block_action").forGetter(RegionApplyAction::blockAction),
            BlockCondition.optionalCodec("block_condition").forGetter(RegionApplyAction::blockCondition)
    ).apply(i, RegionApplyAction::new));

    @Override
    public void execute(@NonNull BlockActionContext ctx) {
        this.shape.blocks(ctx.pos(), this.radius.evaluate(ctx.formula()))
                .filter(pos -> this.blockCondition.test(ctx.level(), pos, ctx))
                .forEach(pos -> this.blockAction.execute(ctx.level(), pos, ctx.direction(), ctx));
    }

    @Override
    public @NonNull MapCodec<RegionApplyAction> codec() {
        return CODEC;
    }
}
