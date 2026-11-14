package com.iafenvoy.saturn.data.condition.builtin.entity;

import com.iafenvoy.saturn.data.condition.EntityCondition;
import com.iafenvoy.saturn.data.context.condition.EntityConditionContext;
import com.iafenvoy.saturn.util.DatapackCommands;
import com.iafenvoy.saturn.util.math.Comparison;
import com.mojang.serialization.Codec;
import com.mojang.serialization.MapCodec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import net.minecraft.server.level.ServerLevel;
import org.jspecify.annotations.NonNull;

public record CommandCondition(String command, Comparison comparison) implements EntityCondition {
    public static final MapCodec<CommandCondition> CODEC = RecordCodecBuilder.mapCodec(i -> i.group(
            Codec.STRING.fieldOf("command").forGetter(CommandCondition::command),
            Comparison.CODEC.forGetter(CommandCondition::comparison)
    ).apply(i, CommandCondition::new));

    @Override
    public boolean test(@NonNull EntityConditionContext ctx) {
        return ctx.entity().level() instanceof ServerLevel && this.comparison.compare(DatapackCommands.execute(ctx.entity(), ctx.entity().position(), this.command));
    }

    @Override
    public @NonNull MapCodec<CommandCondition> codec() {
        return CODEC;
    }
}
