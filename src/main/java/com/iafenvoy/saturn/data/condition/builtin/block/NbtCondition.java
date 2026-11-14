package com.iafenvoy.saturn.data.condition.builtin.block;

import com.iafenvoy.saturn.data.condition.BlockCondition;
import com.iafenvoy.saturn.data.context.condition.BlockConditionContext;
import com.mojang.serialization.MapCodec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.nbt.NbtUtils;
import net.minecraft.world.level.block.entity.BlockEntity;
import org.jspecify.annotations.NonNull;

public record NbtCondition(CompoundTag nbt) implements BlockCondition {
    public static final MapCodec<NbtCondition> CODEC = RecordCodecBuilder.mapCodec(i -> i.group(
            CompoundTag.CODEC.fieldOf("nbt").forGetter(NbtCondition::nbt)
    ).apply(i, NbtCondition::new));

    @Override
    public boolean test(@NonNull BlockConditionContext ctx) {
        BlockEntity entity = ctx.level().getBlockEntity(ctx.pos());
        return entity != null && NbtUtils.compareNbt(this.nbt, entity.saveWithoutMetadata(ctx.level().registryAccess()), true);
    }

    @Override
    public @NonNull MapCodec<NbtCondition> codec() {
        return CODEC;
    }

    public NbtCondition {
        nbt = nbt.copy();
    }
}
