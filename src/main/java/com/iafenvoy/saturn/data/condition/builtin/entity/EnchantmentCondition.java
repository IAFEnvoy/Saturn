package com.iafenvoy.saturn.data.condition.builtin.entity;

import com.iafenvoy.saturn.data.condition.EntityCondition;
import com.iafenvoy.saturn.data.context.condition.EntityConditionContext;
import com.iafenvoy.saturn.util.math.Comparison;
import com.mojang.serialization.Codec;
import com.mojang.serialization.MapCodec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import net.minecraft.core.Holder;
import net.minecraft.util.StringRepresentable;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.EquipmentSlot;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.item.enchantment.Enchantment;
import org.jspecify.annotations.NonNull;

import java.util.Locale;
import java.util.function.IntBinaryOperator;

public record EnchantmentCondition(Holder<Enchantment> enchantment, Calculation calculation,
                                   Comparison comparison) implements EntityCondition {
    public static final MapCodec<EnchantmentCondition> CODEC = RecordCodecBuilder.mapCodec(i -> i.group(
            Enchantment.CODEC.fieldOf("enchantment").forGetter(EnchantmentCondition::enchantment),
            Calculation.CODEC.lenientOptionalFieldOf("calculation", Calculation.SUM).forGetter(EnchantmentCondition::calculation),
            Comparison.CODEC.forGetter(EnchantmentCondition::comparison)
    ).apply(i, EnchantmentCondition::new));

    @Override
    public @NonNull MapCodec<? extends EntityCondition> codec() {
        return CODEC;
    }

    @Override
    public boolean test(@NonNull EntityConditionContext ctx) {
        Entity entity = ctx.entity();
        if (!(entity instanceof LivingEntity living)) return false;
        int level = 0;
        for (EquipmentSlot slot : EquipmentSlot.values())
            level = this.calculation.process(level, living.getItemBySlot(slot).getEnchantmentLevel(this.enchantment));
        return this.comparison.compare(level);
    }

    public enum Calculation implements StringRepresentable {
        SUM(Integer::sum),
        MAX(Math::max);
        public static final Codec<Calculation> CODEC = StringRepresentable.fromEnum(Calculation::values);
        private final IntBinaryOperator processor;

        Calculation(IntBinaryOperator processor) {
            this.processor = processor;
        }

        public int process(int a, int b) {
            return this.processor.applyAsInt(a, b);
        }

        @Override
        public @NonNull String getSerializedName() {
            return this.name().toLowerCase(Locale.ROOT);
        }
    }
}
