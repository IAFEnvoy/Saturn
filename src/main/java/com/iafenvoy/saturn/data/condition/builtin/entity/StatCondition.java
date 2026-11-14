package com.iafenvoy.saturn.data.condition.builtin.entity;

import com.iafenvoy.saturn.data.condition.EntityCondition;
import com.iafenvoy.saturn.data.context.condition.EntityConditionContext;
import com.iafenvoy.saturn.util.StatReference;
import com.iafenvoy.saturn.util.math.Comparison;
import com.mojang.serialization.MapCodec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import net.minecraft.server.level.ServerPlayer;
import org.jspecify.annotations.NonNull;

public record StatCondition(StatReference stat, Comparison comparison) implements EntityCondition {
    public static final MapCodec<StatCondition> CODEC = RecordCodecBuilder.mapCodec(i -> i.group(
            StatReference.CODEC.fieldOf("stat").forGetter(StatCondition::stat),
            Comparison.CODEC.forGetter(StatCondition::comparison)
    ).apply(i, StatCondition::new));

    @Override
    public boolean test(@NonNull EntityConditionContext ctx) {
        return ctx.entity() instanceof ServerPlayer player && this.stat.resolve().map(stat -> this.comparison.compare(player.getStats().getValue(stat))).orElse(false);
    }

    @Override
    public @NonNull MapCodec<StatCondition> codec() {
        return CODEC;
    }
}
