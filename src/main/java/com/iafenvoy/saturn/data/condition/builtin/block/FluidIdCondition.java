package com.iafenvoy.saturn.data.condition.builtin.block;

import com.iafenvoy.saturn.data.condition.BlockCondition;
import com.iafenvoy.saturn.data.context.condition.BlockConditionContext;
import com.mojang.serialization.MapCodec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.world.level.material.Fluid;
import org.jspecify.annotations.NonNull;

public record FluidIdCondition(Fluid fluid) implements BlockCondition {
    public static final MapCodec<FluidIdCondition> CODEC = RecordCodecBuilder.mapCodec(i -> i.group(
            BuiltInRegistries.FLUID.byNameCodec().fieldOf("fluid").forGetter(FluidIdCondition::fluid)
    ).apply(i, FluidIdCondition::new));

    @Override
    public boolean test(@NonNull BlockConditionContext ctx) {
        return ctx.level().getFluidState(ctx.pos()).is(this.fluid);
    }

    @Override
    public @NonNull MapCodec<FluidIdCondition> codec() {
        return CODEC;
    }
}
