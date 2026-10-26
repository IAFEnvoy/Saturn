package com.iafenvoy.saturn.data.condition.builtin.entity;

import com.iafenvoy.saturn.data.condition.EntityCondition;
import com.iafenvoy.saturn.data.context.condition.EntityConditionContext;
import com.iafenvoy.saturn.util.math.Comparison;
import com.mojang.serialization.MapCodec;
import net.minecraft.world.entity.player.Player;
import org.jspecify.annotations.NonNull;

public record SaturationLevelCondition(Comparison comparison) implements EntityCondition {
    public static final MapCodec<SaturationLevelCondition> CODEC = Comparison.CODEC.xmap(SaturationLevelCondition::new, SaturationLevelCondition::comparison);

    @Override
    public boolean test(@NonNull EntityConditionContext ctx) {
        return ctx.entity() instanceof Player player && this.comparison.compare(player.getFoodData().getSaturationLevel());
    }

    @Override
    public @NonNull MapCodec<SaturationLevelCondition> codec() {
        return CODEC;
    }
}
