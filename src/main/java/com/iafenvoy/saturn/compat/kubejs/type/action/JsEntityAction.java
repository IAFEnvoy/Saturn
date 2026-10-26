package com.iafenvoy.saturn.compat.kubejs.type.action;

import com.google.gson.JsonObject;
import com.iafenvoy.saturn.compat.kubejs.callback.SaturnJsActionCallbacks;
import com.iafenvoy.saturn.compat.kubejs.codec.SaturnJsCodecs;
import com.iafenvoy.saturn.data.action.EntityAction;
import com.iafenvoy.saturn.data.context.action.EntityActionContext;
import com.mojang.serialization.Codec;
import com.mojang.serialization.MapCodec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import org.jspecify.annotations.NonNull;

public record JsEntityAction(String id, JsonObject params) implements EntityAction {
    public static final MapCodec<JsEntityAction> CODEC = RecordCodecBuilder.mapCodec(i -> i.group(
            Codec.STRING.fieldOf("id").forGetter(JsEntityAction::id),
            SaturnJsCodecs.PARAMS.optionalFieldOf("params", new JsonObject()).forGetter(JsEntityAction::params)
    ).apply(i, JsEntityAction::new));

    @Override
    public void execute(@NonNull EntityActionContext context) {
        SaturnJsActionCallbacks.executeEntity(this.id, context.entity(), this.params, context.formula());
    }

    @Override
    public @NonNull MapCodec<JsEntityAction> codec() {
        return CODEC;
    }
}
