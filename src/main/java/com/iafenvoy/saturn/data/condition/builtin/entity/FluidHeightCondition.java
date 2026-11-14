package com.iafenvoy.saturn.data.condition.builtin.entity;

import com.iafenvoy.saturn.data.condition.EntityCondition;
import com.iafenvoy.saturn.data.context.condition.EntityConditionContext;
import com.iafenvoy.saturn.util.math.Comparison;
import com.mojang.serialization.MapCodec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import net.neoforged.neoforge.fluids.FluidType;
import net.neoforged.neoforge.registries.NeoForgeRegistries;
import org.jspecify.annotations.NonNull;

public record FluidHeightCondition(FluidType fluid, Comparison comparison) implements EntityCondition {
    public static final MapCodec<FluidHeightCondition> CODEC = RecordCodecBuilder.mapCodec(i -> i.group(
            NeoForgeRegistries.FLUID_TYPES.byNameCodec().fieldOf("fluid").forGetter(FluidHeightCondition::fluid),
            Comparison.CODEC.forGetter(FluidHeightCondition::comparison)
    ).apply(i, FluidHeightCondition::new));

    @Override
    public boolean test(@NonNull EntityConditionContext ctx) {
        return this.comparison.compare(ctx.entity().getFluidTypeHeight(this.fluid));
    }

    @Override
    public @NonNull MapCodec<FluidHeightCondition> codec() {
        return CODEC;
    }
}
