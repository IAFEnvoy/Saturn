package com.iafenvoy.saturn.compat.kubejs.type.number;

import com.google.gson.JsonObject;
import com.iafenvoy.saturn.compat.kubejs.callback.SaturnJsValueCallbacks;
import com.iafenvoy.saturn.compat.kubejs.codec.SaturnJsCodecs;
import com.iafenvoy.saturn.util.formula.FormulaContext;
import com.iafenvoy.saturn.util.formula.NumberProvider;
import com.mojang.serialization.Codec;
import com.mojang.serialization.MapCodec;
import com.mojang.serialization.codecs.RecordCodecBuilder;

public record JsNumberProvider(String id, JsonObject params) implements NumberProvider {
    public static final MapCodec<JsNumberProvider> MAP_CODEC = RecordCodecBuilder.mapCodec(i -> i.group(
            Codec.STRING.fieldOf("id").forGetter(JsNumberProvider::id),
            SaturnJsCodecs.PARAMS.optionalFieldOf("params", new JsonObject()).forGetter(JsNumberProvider::params)
    ).apply(i, JsNumberProvider::new));

    @Override
    public double evaluate(FormulaContext context) {
        return SaturnJsValueCallbacks.number(this.id, context, this.params);
    }

    @Override
    public MapCodec<JsNumberProvider> codec() {
        return MAP_CODEC;
    }
}
