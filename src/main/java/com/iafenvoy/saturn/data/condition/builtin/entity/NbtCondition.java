package com.iafenvoy.saturn.data.condition.builtin.entity;

import com.iafenvoy.saturn.data.condition.EntityCondition;
import com.iafenvoy.saturn.data.context.condition.EntityConditionContext;
import com.mojang.serialization.MapCodec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.nbt.NbtUtils;
import net.minecraft.util.ProblemReporter;
import net.minecraft.world.level.storage.TagValueOutput;
import org.jspecify.annotations.NonNull;

public record NbtCondition(CompoundTag nbt) implements EntityCondition {
    public static final MapCodec<NbtCondition> CODEC = RecordCodecBuilder.mapCodec(i -> i.group(
            CompoundTag.CODEC.fieldOf("nbt").forGetter(NbtCondition::nbt)
    ).apply(i, NbtCondition::new));

    @Override
    public boolean test(@NonNull EntityConditionContext ctx) {
        TagValueOutput output = TagValueOutput.createWithContext(ProblemReporter.DISCARDING, ctx.entity().registryAccess());
        ctx.entity().saveWithoutId(output);
        return NbtUtils.compareNbt(this.nbt, output.buildResult(), true);
    }

    @Override
    public @NonNull MapCodec<NbtCondition> codec() {
        return CODEC;
    }

    public NbtCondition {
        nbt = nbt.copy();
    }
}
