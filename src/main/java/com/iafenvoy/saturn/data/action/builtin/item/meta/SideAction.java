package com.iafenvoy.saturn.data.action.builtin.item.meta;

import com.iafenvoy.saturn.data.action.ItemAction;
import com.iafenvoy.saturn.data.context.action.ItemActionContext;
import com.iafenvoy.saturn.util.LogicalSide;
import com.mojang.serialization.MapCodec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import org.jspecify.annotations.NonNull;

public record SideAction(ItemAction action, LogicalSide side) implements ItemAction {
    public static final MapCodec<SideAction> CODEC = RecordCodecBuilder.mapCodec(i -> i.group(
            ItemAction.CODEC.fieldOf("action").forGetter(SideAction::action),
            LogicalSide.CODEC.fieldOf("side").forGetter(SideAction::side)
    ).apply(i, SideAction::new));

    @Override
    public void execute(@NonNull ItemActionContext ctx) {
        if (this.side.matches(ctx.holder().level())) this.action.execute(ctx);
    }

    @Override
    public @NonNull MapCodec<SideAction> codec() {
        return CODEC;
    }
}
