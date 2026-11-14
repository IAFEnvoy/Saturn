package com.iafenvoy.saturn.data.condition.builtin.entity;

import com.iafenvoy.saturn.data.condition.EntityCondition;
import com.iafenvoy.saturn.data.condition.ItemCondition;
import com.iafenvoy.saturn.data.context.condition.EntityConditionContext;
import com.iafenvoy.saturn.util.EntitySlots;
import com.iafenvoy.saturn.util.math.Comparison;
import com.mojang.serialization.MapCodec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import net.minecraft.world.entity.SlotAccess;
import net.minecraft.world.item.ItemStack;
import org.jspecify.annotations.NonNull;

import java.util.List;

public record InventoryCondition(EntitySlots.ProcessMode processMode, ItemCondition itemCondition, List<Integer> slot,
                                 Comparison comparison) implements EntityCondition {
    public static final MapCodec<InventoryCondition> CODEC = RecordCodecBuilder.mapCodec(i -> i.group(
            EntitySlots.ProcessMode.CODEC.lenientOptionalFieldOf("process_mode", EntitySlots.ProcessMode.ITEMS).forGetter(InventoryCondition::processMode),
            ItemCondition.optionalCodec("item_condition").forGetter(InventoryCondition::itemCondition),
            EntitySlots.CODEC.lenientOptionalFieldOf("slot", List.of()).forGetter(InventoryCondition::slot),
            Comparison.CODEC.forGetter(InventoryCondition::comparison)
    ).apply(i, InventoryCondition::new));

    public InventoryCondition {
        slot = List.copyOf(slot);
    }

    @Override
    public boolean test(@NonNull EntityConditionContext ctx) {
        long count = 0;
        for (SlotAccess access : EntitySlots.select(ctx.entity(), this.slot)) {
            ItemStack stack = access.get();
            if (!stack.isEmpty() && this.itemCondition.test(ctx.entity(), stack, ctx))
                count += this.processMode.count(stack);
        }
        return this.comparison.compare((double) count);
    }

    @Override
    public @NonNull MapCodec<InventoryCondition> codec() {
        return CODEC;
    }
}
