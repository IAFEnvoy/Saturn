package com.iafenvoy.saturn.data.ability.target;

import com.google.gson.JsonObject;
import com.iafenvoy.saturn.compat.kubejs.callback.SaturnJsSelectorCallbacks;
import com.iafenvoy.saturn.compat.kubejs.codec.SaturnJsCodecs;
import com.iafenvoy.saturn.data.ability.TargetSelector;
import com.iafenvoy.saturn.util.formula.FormulaContext;
import com.mojang.serialization.Codec;
import com.mojang.serialization.MapCodec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import net.minecraft.world.entity.Entity;

import java.util.stream.Stream;

/**
 * A target selector whose entity list comes from a server script callback.
 */
public record JsTargetSelector(String id, JsonObject params) implements TargetSelector {
    public static final MapCodec<JsTargetSelector> CODEC = RecordCodecBuilder.mapCodec(i -> i.group(
            Codec.STRING.fieldOf("id").forGetter(JsTargetSelector::id),
            SaturnJsCodecs.PARAMS.optionalFieldOf("params", new JsonObject()).forGetter(JsTargetSelector::params)
    ).apply(i, JsTargetSelector::new));

    @Override
    public Stream<Entity> select(Entity actor, FormulaContext context) {
        return SaturnJsSelectorCallbacks.select(this.id, actor, context, this.params).stream();
    }

    @Override
    public MapCodec<JsTargetSelector> codec() {
        return CODEC;
    }
}
