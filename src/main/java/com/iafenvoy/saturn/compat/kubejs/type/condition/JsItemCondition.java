package com.iafenvoy.saturn.compat.kubejs.type.condition;

import com.google.gson.JsonObject;
import com.iafenvoy.saturn.compat.kubejs.callback.SaturnJsConditionCallbacks;
import com.iafenvoy.saturn.compat.kubejs.codec.SaturnJsCodecs;
import com.iafenvoy.saturn.data.condition.ItemCondition;
import com.iafenvoy.saturn.data.context.condition.ItemConditionContext;
import com.mojang.serialization.Codec;
import com.mojang.serialization.MapCodec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import org.jspecify.annotations.NonNull;

public record JsItemCondition(String id, JsonObject params) implements ItemCondition {
    public static final MapCodec<JsItemCondition> CODEC = RecordCodecBuilder.mapCodec(i -> i.group(
            Codec.STRING.fieldOf("id").forGetter(JsItemCondition::id),
            SaturnJsCodecs.PARAMS.optionalFieldOf("params", new JsonObject()).forGetter(JsItemCondition::params)
    ).apply(i, JsItemCondition::new));

    @Override
    public boolean test(@NonNull ItemConditionContext context) {
        return SaturnJsConditionCallbacks.testItem(this.id, context.holder(), context.stack(), this.params, context.formula());
    }

    @Override
    public @NonNull MapCodec<JsItemCondition> codec() {
        return CODEC;
    }
}
