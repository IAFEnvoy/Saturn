package com.iafenvoy.saturn.data.trigger.builtin;

import com.google.gson.JsonObject;
import com.iafenvoy.saturn.compat.kubejs.callback.SaturnJsTriggerMatchers;
import com.iafenvoy.saturn.compat.kubejs.codec.SaturnJsCodecs;
import com.iafenvoy.saturn.data.trigger.Trigger;
import com.iafenvoy.saturn.data.trigger.TriggerSignal;
import com.mojang.serialization.Codec;
import com.mojang.serialization.MapCodec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import net.minecraft.resources.Identifier;

/**
 * A trigger matcher whose decision is a server script callback, published by a pack with
 * {@code SaturnTriggers.publish}. The signal field is required because the dispatcher indexes subscriptions by it.
 */
public record JsTrigger(Identifier signal, String id, JsonObject params) implements Trigger {
    public static final MapCodec<JsTrigger> CODEC = RecordCodecBuilder.mapCodec(i -> i.group(
            Identifier.CODEC.fieldOf("signal").forGetter(JsTrigger::signal),
            Codec.STRING.fieldOf("id").forGetter(JsTrigger::id),
            SaturnJsCodecs.PARAMS.optionalFieldOf("params", new JsonObject()).forGetter(JsTrigger::params)
    ).apply(i, JsTrigger::new));

    @Override
    public Identifier signalType() {
        return this.signal;
    }

    @Override
    public boolean matches(TriggerSignal signal) {
        return this.signal.equals(signal.type()) && SaturnJsTriggerMatchers.matches(this.id, signal, this.params);
    }

    @Override
    public MapCodec<JsTrigger> codec() {
        return CODEC;
    }
}
