package com.iafenvoy.saturn.data.action.builtin.entity.meta;

import com.iafenvoy.saturn.data.action.EntityAction;
import com.iafenvoy.saturn.data.context.action.EntityActionContext;
import com.iafenvoy.saturn.util.LogicalSide;
import com.mojang.serialization.MapCodec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import org.jspecify.annotations.NonNull;

public record SideAction(EntityAction action, LogicalSide side) implements EntityAction {
    public static final MapCodec<SideAction> CODEC = RecordCodecBuilder.mapCodec(i -> i.group(
            EntityAction.CODEC.fieldOf("action").forGetter(SideAction::action),
            LogicalSide.CODEC.fieldOf("side").forGetter(SideAction::side)
    ).apply(i, SideAction::new));

    @Override
    public void execute(@NonNull EntityActionContext ctx) {
        if (this.side.matches(ctx.entity().level())) this.action.execute(ctx);
    }

    @Override
    public @NonNull MapCodec<SideAction> codec() {
        return CODEC;
    }
}
