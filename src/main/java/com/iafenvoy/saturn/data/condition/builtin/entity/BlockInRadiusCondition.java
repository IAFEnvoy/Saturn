package com.iafenvoy.saturn.data.condition.builtin.entity;

import com.iafenvoy.saturn.data.condition.BlockCondition;
import com.iafenvoy.saturn.data.condition.EntityCondition;
import com.iafenvoy.saturn.data.context.condition.EntityConditionContext;
import com.iafenvoy.saturn.util.formula.NumberProvider;
import com.iafenvoy.saturn.util.math.Comparison;
import com.iafenvoy.saturn.util.math.Shape;
import com.mojang.serialization.MapCodec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import org.jspecify.annotations.NonNull;

public record BlockInRadiusCondition(BlockCondition blockCondition, NumberProvider radius, Shape shape,
                                     Comparison comparison) implements EntityCondition {
    public static final MapCodec<BlockInRadiusCondition> CODEC = RecordCodecBuilder.mapCodec(i -> i.group(
            BlockCondition.optionalCodec("block_condition").forGetter(BlockInRadiusCondition::blockCondition),
            NumberProvider.CODEC.fieldOf("radius").forGetter(BlockInRadiusCondition::radius),
            Shape.CODEC.lenientOptionalFieldOf("shape", Shape.CUBE).forGetter(BlockInRadiusCondition::shape),
            Comparison.CODEC.forGetter(BlockInRadiusCondition::comparison)
    ).apply(i, BlockInRadiusCondition::new));

    @Override
    public boolean test(@NonNull EntityConditionContext ctx) {
        return this.comparison.compare((double) this.shape.blocks(ctx.entity().blockPosition(), this.radius.evaluate(ctx.formula()))
                .filter(pos -> this.blockCondition.test(ctx.entity().level(), pos, ctx)).count());
    }

    @Override
    public @NonNull MapCodec<BlockInRadiusCondition> codec() {
        return CODEC;
    }
}
