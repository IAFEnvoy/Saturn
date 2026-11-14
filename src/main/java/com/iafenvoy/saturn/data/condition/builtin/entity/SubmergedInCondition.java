package com.iafenvoy.saturn.data.condition.builtin.entity;

import com.iafenvoy.saturn.data.condition.EntityCondition;
import com.iafenvoy.saturn.data.context.condition.EntityConditionContext;
import com.mojang.serialization.MapCodec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.world.level.material.Fluid;
import org.jspecify.annotations.NonNull;

public record SubmergedInCondition(Fluid fluid) implements EntityCondition {
    public static final MapCodec<SubmergedInCondition> CODEC = RecordCodecBuilder.mapCodec(i -> i.group(
            BuiltInRegistries.FLUID.byNameCodec().fieldOf("fluid").forGetter(SubmergedInCondition::fluid)
    ).apply(i, SubmergedInCondition::new));

    @Override
    public boolean test(@NonNull EntityConditionContext ctx) {
        return ctx.entity().isEyeInFluid(this.fluid.getFluidType());
    }

    @Override
    public @NonNull MapCodec<SubmergedInCondition> codec() {
        return CODEC;
    }
}
