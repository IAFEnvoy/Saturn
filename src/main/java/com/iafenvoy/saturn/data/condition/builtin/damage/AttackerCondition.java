package com.iafenvoy.saturn.data.condition.builtin.damage;

import com.iafenvoy.saturn.data.condition.DamageCondition;
import com.iafenvoy.saturn.data.condition.EntityCondition;
import com.iafenvoy.saturn.data.context.condition.DamageConditionContext;
import com.mojang.serialization.MapCodec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import org.jspecify.annotations.NonNull;

public record AttackerCondition(EntityCondition entityCondition) implements DamageCondition {
    public static final MapCodec<AttackerCondition> CODEC = RecordCodecBuilder.mapCodec(i -> i.group(
            EntityCondition.CODEC.fieldOf("entity_condition").forGetter(AttackerCondition::entityCondition)
    ).apply(i, AttackerCondition::new));

    @Override
    public boolean test(@NonNull DamageConditionContext ctx) {
        return ctx.source().getEntity() != null && this.entityCondition.test(ctx.source().getEntity(), ctx);
    }

    @Override
    public @NonNull MapCodec<AttackerCondition> codec() {
        return CODEC;
    }
}
