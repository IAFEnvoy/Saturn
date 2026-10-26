package com.iafenvoy.saturn.data.condition.builtin.item;

import com.iafenvoy.saturn.data.condition.ItemCondition;
import com.iafenvoy.saturn.data.context.condition.ItemConditionContext;
import com.mojang.serialization.MapCodec;
import net.minecraft.world.entity.player.Player;
import org.jspecify.annotations.NonNull;

public enum OnCooldownCondition implements ItemCondition {
    INSTANCE;
    public static final MapCodec<OnCooldownCondition> CODEC = MapCodec.unit(INSTANCE);

    @Override
    public boolean test(@NonNull ItemConditionContext ctx) {
        return ctx.holder() instanceof Player player && player.getCooldowns().isOnCooldown(ctx.stack());
    }

    @Override
    public @NonNull MapCodec<OnCooldownCondition> codec() {
        return CODEC;
    }
}
