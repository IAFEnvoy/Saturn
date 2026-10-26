package com.iafenvoy.saturn.data.condition.builtin.entity;

import com.iafenvoy.saturn.data.ability.Ability;
import com.iafenvoy.saturn.data.condition.EntityCondition;
import com.iafenvoy.saturn.data.context.condition.EntityConditionContext;
import com.iafenvoy.saturn.data.storage.builtin.TimerDataStorage;
import com.iafenvoy.saturn.util.OptionalBoolean;
import com.iafenvoy.saturn.util.codec.OptionalCodecs;
import com.iafenvoy.saturn.util.formula.NumberRange;
import com.mojang.serialization.MapCodec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import net.minecraft.core.Holder;
import org.jspecify.annotations.NonNull;

import java.util.Optional;
import java.util.OptionalDouble;

/**
 * Reads how much of the {@code saturn:timer} of a named ability is left as ticks until its stored {@code ends_at}, and
 * whether that end has passed. A timer with no {@code ends_at} is not running, so it counts as ended.
 */
public record AbilityStorageTimerCondition(Holder<Ability> ability, Optional<NumberRange> remaining,
                                           OptionalBoolean ended) implements EntityCondition {
    public static final MapCodec<AbilityStorageTimerCondition> CODEC = RecordCodecBuilder.mapCodec(i -> i.group(
            Ability.CODEC.fieldOf("ability").forGetter(AbilityStorageTimerCondition::ability),
            NumberRange.CODEC.optionalFieldOf("remaining").forGetter(AbilityStorageTimerCondition::remaining),
            OptionalCodecs.boolCodec("ended").forGetter(AbilityStorageTimerCondition::ended)
    ).apply(i, AbilityStorageTimerCondition::new));

    @Override
    public boolean test(@NonNull EntityConditionContext ctx) {
        AbilityStorageReading reading = AbilityStorageReading.of(ctx.entity(), this.ability);
        if (reading.declared(TimerDataStorage.class).isEmpty()) return false;
        OptionalDouble endsAt = reading.stored(TimerDataStorage.class).map(TimerDataStorage::endsAt).orElseGet(OptionalDouble::empty);
        long gameTime = ctx.entity().level().getGameTime();
        // "Ticks until the end" cannot be negative: an ended timer is out of time, exactly like one that never ran.
        double left = endsAt.isPresent() ? Math.max(0.0D, endsAt.getAsDouble() - gameTime) : 0.0D;
        boolean finished = endsAt.isEmpty() || endsAt.getAsDouble() <= gameTime;
        if (this.remaining.isPresent() && !this.remaining.get().test(left, ctx.formula())) return false;
        return this.ended.isEmpty() || this.ended.getAsBoolean() == finished;
    }

    @Override
    public @NonNull MapCodec<AbilityStorageTimerCondition> codec() {
        return CODEC;
    }
}
