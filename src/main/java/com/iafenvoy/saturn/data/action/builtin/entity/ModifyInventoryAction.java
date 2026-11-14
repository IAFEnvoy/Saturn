package com.iafenvoy.saturn.data.action.builtin.entity;

import com.iafenvoy.saturn.data.action.EntityAction;
import com.iafenvoy.saturn.data.action.ItemAction;
import com.iafenvoy.saturn.data.condition.ItemCondition;
import com.iafenvoy.saturn.data.context.action.EntityActionContext;
import com.iafenvoy.saturn.util.EntitySlots;
import com.mojang.serialization.Codec;
import com.mojang.serialization.MapCodec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import net.minecraft.world.entity.SlotAccess;
import net.minecraft.world.item.ItemStack;
import org.jspecify.annotations.NonNull;

import java.util.List;

public record ModifyInventoryAction(ItemCondition itemCondition, ItemAction itemAction, EntityAction entityAction,
                                    List<Integer> slot, int limit) implements EntityAction {
    public static final MapCodec<ModifyInventoryAction> CODEC = RecordCodecBuilder.mapCodec(i -> i.group(
            ItemCondition.optionalCodec("item_condition").forGetter(ModifyInventoryAction::itemCondition),
            ItemAction.CODEC.fieldOf("item_action").forGetter(ModifyInventoryAction::itemAction),
            EntityAction.optionalCodec("entity_action").forGetter(ModifyInventoryAction::entityAction),
            EntitySlots.CODEC.lenientOptionalFieldOf("slot", List.of()).forGetter(ModifyInventoryAction::slot),
            Codec.INT.lenientOptionalFieldOf("limit", 0).forGetter(ModifyInventoryAction::limit)
    ).apply(i, ModifyInventoryAction::new));

    public ModifyInventoryAction {
        slot = List.copyOf(slot);
    }

    @Override
    public void execute(@NonNull EntityActionContext ctx) {
        if (ctx.entity().level().isClientSide()) return;
        int changed = 0;
        for (SlotAccess access : EntitySlots.select(ctx.entity(), this.slot)) {
            ItemStack stack = access.get();
            if (stack.isEmpty() || !this.itemCondition.test(ctx.entity(), stack, ctx)) continue;
            this.entityAction.execute(ctx);
            this.itemAction.execute(ctx.entity(), stack, ctx);
            access.set(stack);
            if (this.limit > 0 && ++changed >= this.limit) break;
        }
    }

    @Override
    public @NonNull MapCodec<ModifyInventoryAction> codec() {
        return CODEC;
    }
}
