package com.iafenvoy.saturn.data.action.builtin.block;

import com.iafenvoy.saturn.data.action.BlockAction;
import com.iafenvoy.saturn.data.context.action.BlockActionContext;
import com.iafenvoy.saturn.util.formula.NumberProvider;
import com.mojang.serialization.Codec;
import com.mojang.serialization.MapCodec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import net.minecraft.util.StringRepresentable;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.block.state.properties.Property;
import org.jspecify.annotations.NonNull;

import java.util.Locale;
import java.util.Optional;

public record ModifyBlockStateAction(String property, boolean cycle, Optional<Boolean> value,
                                     Optional<String> enumValue, Optional<NumberProvider> change,
                                     Operation operation) implements BlockAction {
    public static final MapCodec<ModifyBlockStateAction> CODEC = RecordCodecBuilder.mapCodec(i -> i.group(
            Codec.STRING.fieldOf("property").forGetter(ModifyBlockStateAction::property),
            Codec.BOOL.lenientOptionalFieldOf("cycle", false).forGetter(ModifyBlockStateAction::cycle),
            Codec.BOOL.lenientOptionalFieldOf("value").forGetter(ModifyBlockStateAction::value),
            Codec.STRING.lenientOptionalFieldOf("enum").forGetter(ModifyBlockStateAction::enumValue),
            NumberProvider.CODEC.lenientOptionalFieldOf("change").forGetter(ModifyBlockStateAction::change),
            Operation.CODEC.lenientOptionalFieldOf("operation", Operation.ADD).forGetter(ModifyBlockStateAction::operation)
    ).apply(i, ModifyBlockStateAction::new));

    @Override
    public void execute(@NonNull BlockActionContext ctx) {
        if (ctx.level().isClientSide()) return;
        BlockState state = ctx.level().getBlockState(ctx.pos());
        Property<?> property = state.getBlock().getStateDefinition().getProperty(this.property);
        if (property == null) return;
        BlockState changed = this.cycle ? state.cycle(property) : this.modify(state, property, ctx);
        if (changed != state) ctx.level().setBlockAndUpdate(ctx.pos(), changed);
    }

    @Override
    public @NonNull MapCodec<ModifyBlockStateAction> codec() {
        return CODEC;
    }

    private <T extends Comparable<T>> BlockState modify(BlockState state, Property<T> property, BlockActionContext ctx) {
        T actual = state.getValue(property);
        String desired;
        if (actual instanceof Boolean && this.value.isPresent()) desired = this.value.get().toString();
        else if (actual instanceof Integer current && this.change.isPresent()) {
            int change = (int) this.change.get().evaluate(ctx.formula());
            desired = Integer.toString(this.operation == Operation.SET ? change : current + change);
        } else if (this.enumValue.isPresent()) desired = this.enumValue.get();
        else return state;
        return property.getValue(desired).map(value -> state.setValue(property, value)).orElse(state);
    }

    public enum Operation implements StringRepresentable {
        ADD, SET;
        public static final Codec<Operation> CODEC = StringRepresentable.fromEnum(Operation::values);

        @Override
        public @NonNull String getSerializedName() {
            return this.name().toLowerCase(Locale.ROOT);
        }
    }
}
