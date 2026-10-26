package com.iafenvoy.saturn.compat.kubejs.type.condition;

import com.google.gson.JsonObject;
import com.iafenvoy.saturn.compat.kubejs.callback.SaturnJsConditionCallbacks;
import com.iafenvoy.saturn.compat.kubejs.codec.SaturnJsCodecs;
import com.iafenvoy.saturn.data.condition.DamageCondition;
import com.iafenvoy.saturn.data.context.condition.DamageConditionContext;
import com.mojang.serialization.Codec;
import com.mojang.serialization.MapCodec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import org.jspecify.annotations.NonNull;

public record JsDamageCondition(String id, JsonObject params) implements DamageCondition {
    public static final MapCodec<JsDamageCondition> CODEC = RecordCodecBuilder.mapCodec(i -> i.group(
            Codec.STRING.fieldOf("id").forGetter(JsDamageCondition::id),
            SaturnJsCodecs.PARAMS.optionalFieldOf("params", new JsonObject()).forGetter(JsDamageCondition::params)
    ).apply(i, JsDamageCondition::new));

    @Override
    public boolean test(@NonNull DamageConditionContext context) {
        return SaturnJsConditionCallbacks.testDamage(this.id, context.source(), context.amount(), this.params, context.formula());
    }

    @Override
    public @NonNull MapCodec<JsDamageCondition> codec() {
        return CODEC;
    }
}
