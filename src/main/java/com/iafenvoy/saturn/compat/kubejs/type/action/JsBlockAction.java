package com.iafenvoy.saturn.compat.kubejs.type.action;

import com.google.gson.JsonObject;
import com.iafenvoy.saturn.compat.kubejs.callback.SaturnJsActionCallbacks;
import com.iafenvoy.saturn.compat.kubejs.codec.SaturnJsCodecs;
import com.iafenvoy.saturn.data.action.BlockAction;
import com.iafenvoy.saturn.data.context.action.BlockActionContext;
import com.mojang.serialization.Codec;
import com.mojang.serialization.MapCodec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import org.jspecify.annotations.NonNull;

public record JsBlockAction(String id, JsonObject params) implements BlockAction {
    public static final MapCodec<JsBlockAction> CODEC = RecordCodecBuilder.mapCodec(i -> i.group(
            Codec.STRING.fieldOf("id").forGetter(JsBlockAction::id),
            SaturnJsCodecs.PARAMS.optionalFieldOf("params", new JsonObject()).forGetter(JsBlockAction::params)
    ).apply(i, JsBlockAction::new));

    @Override
    public void execute(@NonNull BlockActionContext context) {
        SaturnJsActionCallbacks.executeBlock(this.id, context.level(), context.pos(), this.params, context.formula());
    }

    @Override
    public @NonNull MapCodec<JsBlockAction> codec() {
        return CODEC;
    }
}
