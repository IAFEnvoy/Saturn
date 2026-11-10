package com.iafenvoy.saturn.data.trigger.builtin;

import com.iafenvoy.saturn.data.trigger.Trigger;
import com.iafenvoy.saturn.data.trigger.TriggerPayload;
import com.iafenvoy.saturn.data.trigger.TriggerSignal;
import com.iafenvoy.saturn.data.trigger.TriggerSignals;
import com.mojang.serialization.MapCodec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import net.minecraft.resources.Identifier;

import java.util.Optional;

/**
 * Matches a whole advancement being earned; omitting its id accepts any completed advancement.
 */
public record AdvancementCompletedTrigger(Optional<Identifier> advancement) implements Trigger {
    public static final MapCodec<AdvancementCompletedTrigger> CODEC = RecordCodecBuilder.mapCodec(i -> i.group(
            Identifier.CODEC.optionalFieldOf("advancement").forGetter(AdvancementCompletedTrigger::advancement)
    ).apply(i, AdvancementCompletedTrigger::new));

    @Override
    public MapCodec<AdvancementCompletedTrigger> codec() {
        return CODEC;
    }

    @Override
    public Identifier signalType() {
        return TriggerSignals.ADVANCEMENT_COMPLETED;
    }

    @Override
    public boolean matches(TriggerSignal signal) {
        if (!this.signalType().equals(signal.type())) return false;
        return signal.context().payload(TriggerPayload.ADVANCEMENT) instanceof Identifier completed
                && this.advancement.map(completed::equals).orElse(true);
    }
}
