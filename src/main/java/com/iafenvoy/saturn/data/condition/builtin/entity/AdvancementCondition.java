package com.iafenvoy.saturn.data.condition.builtin.entity;

import com.iafenvoy.saturn.data.condition.EntityCondition;
import com.iafenvoy.saturn.data.context.condition.EntityConditionContext;
import com.mojang.serialization.MapCodec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import net.minecraft.advancements.AdvancementHolder;
import net.minecraft.resources.Identifier;
import net.minecraft.server.level.ServerPlayer;
import org.jspecify.annotations.NonNull;

public record AdvancementCondition(Identifier advancement) implements EntityCondition {
    public static final MapCodec<AdvancementCondition> CODEC = RecordCodecBuilder.mapCodec(i -> i.group(
            Identifier.CODEC.fieldOf("advancement").forGetter(AdvancementCondition::advancement)
    ).apply(i, AdvancementCondition::new));

    @Override
    public boolean test(@NonNull EntityConditionContext ctx) {
        if (!(ctx.entity() instanceof ServerPlayer player)) return false;
        AdvancementHolder holder = player.level().getServer().getAdvancements().get(this.advancement);
        return holder != null && player.getAdvancements().getOrStartProgress(holder).isDone();
    }

    @Override
    public @NonNull MapCodec<AdvancementCondition> codec() {
        return CODEC;
    }
}
