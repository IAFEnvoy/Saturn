package com.iafenvoy.saturn.compat.kubejs.type.action;

import com.google.gson.JsonObject;
import com.iafenvoy.saturn.compat.kubejs.callback.SaturnJsActionCallbacks;
import com.iafenvoy.saturn.compat.kubejs.codec.SaturnJsCodecs;
import com.iafenvoy.saturn.data.action.ItemAction;
import com.iafenvoy.saturn.data.context.action.ItemActionContext;
import com.mojang.serialization.Codec;
import com.mojang.serialization.MapCodec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import org.jspecify.annotations.NonNull;

public record JsItemAction(String id, JsonObject params) implements ItemAction {
    public static final MapCodec<JsItemAction> CODEC = RecordCodecBuilder.mapCodec(i -> i.group(
            Codec.STRING.fieldOf("id").forGetter(JsItemAction::id),
            SaturnJsCodecs.PARAMS.optionalFieldOf("params", new JsonObject()).forGetter(JsItemAction::params)
    ).apply(i, JsItemAction::new));

    @Override
    public void execute(@NonNull ItemActionContext context) {
        SaturnJsActionCallbacks.executeItem(this.id, context.holder(), context.stack(), this.params, context.formula());
    }

    @Override
    public @NonNull MapCodec<JsItemAction> codec() {
        return CODEC;
    }
}
