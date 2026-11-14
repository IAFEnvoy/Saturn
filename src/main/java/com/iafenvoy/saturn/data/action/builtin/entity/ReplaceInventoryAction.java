package com.iafenvoy.saturn.data.action.builtin.entity;

import com.iafenvoy.saturn.data.action.EntityAction;
import com.iafenvoy.saturn.data.condition.ItemCondition;
import com.iafenvoy.saturn.data.context.action.EntityActionContext;
import com.iafenvoy.saturn.util.EntitySlots;
import com.mojang.serialization.MapCodec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import net.minecraft.world.entity.SlotAccess;
import net.minecraft.world.item.ItemStackTemplate;
import org.jspecify.annotations.NonNull;

import java.util.List;

public record ReplaceInventoryAction(ItemStackTemplate stack, ItemCondition itemCondition,
                                     List<Integer> slot) implements EntityAction {
    public static final MapCodec<ReplaceInventoryAction> CODEC = RecordCodecBuilder.mapCodec(i -> i.group(
            ItemStackTemplate.CODEC.fieldOf("stack").forGetter(ReplaceInventoryAction::stack),
            ItemCondition.optionalCodec("item_condition").forGetter(ReplaceInventoryAction::itemCondition),
            EntitySlots.CODEC.lenientOptionalFieldOf("slot", List.of()).forGetter(ReplaceInventoryAction::slot)
    ).apply(i, ReplaceInventoryAction::new));

    public ReplaceInventoryAction {
        slot = List.copyOf(slot);
    }

    @Override
    public void execute(@NonNull EntityActionContext ctx) {
        if (ctx.entity().level().isClientSide()) return;
        for (SlotAccess access : EntitySlots.select(ctx.entity(), this.slot))
            if (this.itemCondition.test(ctx.entity(), access.get(), ctx)) access.set(this.stack.create());
    }

    @Override
    public @NonNull MapCodec<ReplaceInventoryAction> codec() {
        return CODEC;
    }
}
