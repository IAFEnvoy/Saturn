package com.iafenvoy.saturn.compat.kubejs.type.condition;

import com.google.gson.JsonObject;
import com.iafenvoy.saturn.compat.kubejs.callback.SaturnJsConditionCallbacks;
import com.iafenvoy.saturn.compat.kubejs.codec.SaturnJsCodecs;
import com.iafenvoy.saturn.data.condition.EntityCondition;
import com.iafenvoy.saturn.data.context.condition.EntityConditionContext;
import com.mojang.serialization.Codec;
import com.mojang.serialization.MapCodec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import org.jspecify.annotations.NonNull;

public record JsEntityCondition(String id, JsonObject params) implements EntityCondition {
    public static final MapCodec<JsEntityCondition> CODEC = RecordCodecBuilder.mapCodec(i -> i.group(
            Codec.STRING.fieldOf("id").forGetter(JsEntityCondition::id),
            SaturnJsCodecs.PARAMS.optionalFieldOf("params", new JsonObject()).forGetter(JsEntityCondition::params)
    ).apply(i, JsEntityCondition::new));

    @Override
    public boolean test(@NonNull EntityConditionContext context) {
        return SaturnJsConditionCallbacks.testEntity(this.id, context.entity(), this.params, context.formula());
    }

    @Override
    public @NonNull MapCodec<JsEntityCondition> codec() {
        return CODEC;
    }
}
