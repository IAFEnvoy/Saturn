package com.iafenvoy.saturn.compat.kubejs.type.action;

import com.google.gson.JsonObject;
import com.iafenvoy.saturn.compat.kubejs.callback.SaturnJsActionCallbacks;
import com.iafenvoy.saturn.compat.kubejs.codec.SaturnJsCodecs;
import com.iafenvoy.saturn.data.action.BiEntityAction;
import com.iafenvoy.saturn.data.context.action.BiEntityActionContext;
import com.mojang.serialization.Codec;
import com.mojang.serialization.MapCodec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import org.jspecify.annotations.NonNull;

public record JsBiEntityAction(String id, JsonObject params) implements BiEntityAction {
    public static final MapCodec<JsBiEntityAction> CODEC = RecordCodecBuilder.mapCodec(i -> i.group(
            Codec.STRING.fieldOf("id").forGetter(JsBiEntityAction::id),
            SaturnJsCodecs.PARAMS.optionalFieldOf("params", new JsonObject()).forGetter(JsBiEntityAction::params)
    ).apply(i, JsBiEntityAction::new));

    @Override
    public void execute(@NonNull BiEntityActionContext context) {
        SaturnJsActionCallbacks.executeBiEntity(this.id, context.actor(), context.target(), this.params, context.formula());
    }

    @Override
    public @NonNull MapCodec<JsBiEntityAction> codec() {
        return CODEC;
    }
}
