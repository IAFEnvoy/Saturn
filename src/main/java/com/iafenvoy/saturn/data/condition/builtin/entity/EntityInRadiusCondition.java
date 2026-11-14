package com.iafenvoy.saturn.data.condition.builtin.entity;

import com.iafenvoy.saturn.data.condition.EntityCondition;
import com.iafenvoy.saturn.data.context.condition.EntityConditionContext;
import com.iafenvoy.saturn.util.formula.NumberProvider;
import com.iafenvoy.saturn.util.math.Comparison;
import com.iafenvoy.saturn.util.math.Shape;
import com.mojang.serialization.MapCodec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import org.jspecify.annotations.NonNull;

public record EntityInRadiusCondition(EntityCondition entityCondition, NumberProvider radius, Shape shape,
                                      Comparison comparison) implements EntityCondition {
    public static final MapCodec<EntityInRadiusCondition> CODEC = RecordCodecBuilder.mapCodec(i -> i.group(
            EntityCondition.optionalCodec("entity_condition").forGetter(EntityInRadiusCondition::entityCondition),
            NumberProvider.CODEC.fieldOf("radius").forGetter(EntityInRadiusCondition::radius),
            Shape.CODEC.lenientOptionalFieldOf("shape", Shape.CUBE).forGetter(EntityInRadiusCondition::shape),
            Comparison.CODEC.forGetter(EntityInRadiusCondition::comparison)
    ).apply(i, EntityInRadiusCondition::new));

    @Override
    public boolean test(@NonNull EntityConditionContext ctx) {
        return this.comparison.compare((double) this.shape.entities(ctx.entity().level(), ctx.entity().position(), this.radius.evaluate(ctx.formula())).stream()
                .filter(entity -> this.entityCondition.test(entity, ctx)).count());
    }

    @Override
    public @NonNull MapCodec<EntityInRadiusCondition> codec() {
        return CODEC;
    }
}
