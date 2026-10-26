package com.iafenvoy.saturn.data.resource.builtin;

import com.google.gson.JsonObject;
import com.iafenvoy.saturn.attachment.ResourceHolderAttachment;
import com.iafenvoy.saturn.compat.kubejs.callback.SaturnJsValueCallbacks;
import com.iafenvoy.saturn.compat.kubejs.codec.SaturnJsCodecs;
import com.iafenvoy.saturn.data.resource.Resource;
import com.iafenvoy.saturn.data.resource.ResourceValueProvider;
import com.iafenvoy.saturn.util.formula.FormulaContext;
import com.mojang.serialization.Codec;
import com.mojang.serialization.MapCodec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import net.minecraft.core.Holder;

public record JsResourceValueProvider(String id, JsonObject params) implements ResourceValueProvider {
    public static final MapCodec<JsResourceValueProvider> CODEC = RecordCodecBuilder.mapCodec(i -> i.group(
            Codec.STRING.fieldOf("id").forGetter(JsResourceValueProvider::id),
            SaturnJsCodecs.PARAMS.optionalFieldOf("params", new JsonObject()).forGetter(JsResourceValueProvider::params)
    ).apply(i, JsResourceValueProvider::new));

    @Override
    public double resolve(ResourceHolderAttachment holder, Holder<Resource> resource, FormulaContext context) {
        return SaturnJsValueCallbacks.resource(this.id, holder, resource, context, this.params);
    }

    @Override
    public MapCodec<JsResourceValueProvider> codec() {
        return CODEC;
    }
}
