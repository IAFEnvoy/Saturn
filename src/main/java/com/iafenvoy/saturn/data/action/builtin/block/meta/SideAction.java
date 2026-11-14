package com.iafenvoy.saturn.data.action.builtin.block.meta;

import com.iafenvoy.saturn.data.action.BlockAction;
import com.iafenvoy.saturn.data.context.action.BlockActionContext;
import com.iafenvoy.saturn.util.LogicalSide;
import com.mojang.serialization.MapCodec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import org.jspecify.annotations.NonNull;

public record SideAction(BlockAction action, LogicalSide side) implements BlockAction {
    public static final MapCodec<SideAction> CODEC = RecordCodecBuilder.mapCodec(i -> i.group(
            BlockAction.CODEC.fieldOf("action").forGetter(SideAction::action),
            LogicalSide.CODEC.fieldOf("side").forGetter(SideAction::side)
    ).apply(i, SideAction::new));

    @Override
    public void execute(@NonNull BlockActionContext ctx) {
        if (this.side.matches(ctx.level())) this.action.execute(ctx);
    }

    @Override
    public @NonNull MapCodec<SideAction> codec() {
        return CODEC;
    }
}
