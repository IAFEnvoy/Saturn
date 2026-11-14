package com.iafenvoy.saturn.data.action.builtin.bientity.meta;

import com.iafenvoy.saturn.data.action.BiEntityAction;
import com.iafenvoy.saturn.data.context.action.BiEntityActionContext;
import com.iafenvoy.saturn.util.LogicalSide;
import com.mojang.serialization.MapCodec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import org.jspecify.annotations.NonNull;

public record SideAction(BiEntityAction action, LogicalSide side) implements BiEntityAction {
    public static final MapCodec<SideAction> CODEC = RecordCodecBuilder.mapCodec(i -> i.group(
            BiEntityAction.CODEC.fieldOf("action").forGetter(SideAction::action),
            LogicalSide.CODEC.fieldOf("side").forGetter(SideAction::side)
    ).apply(i, SideAction::new));

    @Override
    public void execute(@NonNull BiEntityActionContext ctx) {
        if (this.side.matches(ctx.actor().level())) this.action.execute(ctx);
    }

    @Override
    public @NonNull MapCodec<SideAction> codec() {
        return CODEC;
    }
}
