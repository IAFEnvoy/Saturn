package com.iafenvoy.saturn.compat.kubejs.type.condition;

import com.google.gson.JsonObject;
import com.iafenvoy.saturn.compat.kubejs.callback.SaturnJsConditionCallbacks;
import com.iafenvoy.saturn.compat.kubejs.codec.SaturnJsCodecs;
import com.iafenvoy.saturn.data.condition.BiEntityCondition;
import com.iafenvoy.saturn.data.context.condition.BiEntityConditionContext;
import com.mojang.serialization.Codec;
import com.mojang.serialization.MapCodec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import org.jspecify.annotations.NonNull;

public record JsBiEntityCondition(String id, JsonObject params) implements BiEntityCondition {
    public static final MapCodec<JsBiEntityCondition> CODEC = RecordCodecBuilder.mapCodec(i -> i.group(
            Codec.STRING.fieldOf("id").forGetter(JsBiEntityCondition::id),
            SaturnJsCodecs.PARAMS.optionalFieldOf("params", new JsonObject()).forGetter(JsBiEntityCondition::params)
    ).apply(i, JsBiEntityCondition::new));

    @Override
    public boolean test(@NonNull BiEntityConditionContext context) {
        return SaturnJsConditionCallbacks.testBiEntity(this.id, context.actor(), context.target(), this.params, context.formula());
    }

    @Override
    public @NonNull MapCodec<JsBiEntityCondition> codec() {
        return CODEC;
    }
}
