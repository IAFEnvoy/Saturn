package com.iafenvoy.saturn.data.condition.builtin.block;

import com.iafenvoy.saturn.data.condition.BlockCondition;
import com.iafenvoy.saturn.data.context.condition.BlockConditionContext;
import com.iafenvoy.saturn.util.math.CompareOperation;
import com.mojang.serialization.Codec;
import com.mojang.serialization.MapCodec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.block.state.properties.Property;
import org.jspecify.annotations.NonNull;

import java.util.Optional;

public record BlockStateCondition(String property, Optional<Boolean> value, Optional<String> enumValue,
                                  Optional<CompareOperation> comparison,
                                  Optional<Double> compareTo) implements BlockCondition {
    public static final MapCodec<BlockStateCondition> CODEC = RecordCodecBuilder.mapCodec(i -> i.group(
            Codec.STRING.fieldOf("property").forGetter(BlockStateCondition::property),
            Codec.BOOL.lenientOptionalFieldOf("value").forGetter(BlockStateCondition::value),
            Codec.STRING.lenientOptionalFieldOf("enum").forGetter(BlockStateCondition::enumValue),
            CompareOperation.CODEC.lenientOptionalFieldOf("comparison").forGetter(BlockStateCondition::comparison),
            Codec.DOUBLE.lenientOptionalFieldOf("compare_to").forGetter(BlockStateCondition::compareTo)
    ).apply(i, BlockStateCondition::new));

    @Override
    public boolean test(@NonNull BlockConditionContext ctx) {
        BlockState state = ctx.level().getBlockState(ctx.pos());
        Property<?> property = state.getBlock().getStateDefinition().getProperty(this.property);
        return property != null && this.matches(state, property);
    }

    @Override
    public @NonNull MapCodec<BlockStateCondition> codec() {
        return CODEC;
    }

    private <T extends Comparable<T>> boolean matches(BlockState state, Property<T> property) {
        T actual = state.getValue(property);
        if (actual instanceof Boolean bool) return this.value.map(expected -> expected.equals(bool)).orElse(false);
        if (actual instanceof Integer number)
            return this.comparison.isPresent() && this.compareTo.isPresent() && this.comparison.get().compare(number.doubleValue(), this.compareTo.get());
        return this.enumValue.map(expected -> property.getName(actual).equalsIgnoreCase(expected)).orElse(false);
    }
}
