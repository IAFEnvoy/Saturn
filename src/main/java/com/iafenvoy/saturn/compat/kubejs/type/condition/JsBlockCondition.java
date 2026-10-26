package com.iafenvoy.saturn.compat.kubejs.type.condition;

import com.google.gson.JsonObject;
import com.iafenvoy.saturn.compat.kubejs.callback.SaturnJsConditionCallbacks;
import com.iafenvoy.saturn.compat.kubejs.codec.SaturnJsCodecs;
import com.iafenvoy.saturn.data.condition.BlockCondition;
import com.iafenvoy.saturn.data.context.condition.BlockConditionContext;
import com.mojang.serialization.Codec;
import com.mojang.serialization.MapCodec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import org.jspecify.annotations.NonNull;

public record JsBlockCondition(String id, JsonObject params) implements BlockCondition {
    public static final MapCodec<JsBlockCondition> CODEC = RecordCodecBuilder.mapCodec(i -> i.group(
            Codec.STRING.fieldOf("id").forGetter(JsBlockCondition::id),
            SaturnJsCodecs.PARAMS.optionalFieldOf("params", new JsonObject()).forGetter(JsBlockCondition::params)
    ).apply(i, JsBlockCondition::new));

    @Override
    public boolean test(@NonNull BlockConditionContext context) {
        return SaturnJsConditionCallbacks.testBlock(this.id, context.level(), context.pos(), this.params, context.formula());
    }

    @Override
    public @NonNull MapCodec<JsBlockCondition> codec() {
        return CODEC;
    }
}
