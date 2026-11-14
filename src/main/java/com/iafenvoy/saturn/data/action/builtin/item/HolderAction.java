package com.iafenvoy.saturn.data.action.builtin.item;

import com.iafenvoy.saturn.data.action.EntityAction;
import com.iafenvoy.saturn.data.action.ItemAction;
import com.iafenvoy.saturn.data.context.action.ItemActionContext;
import com.mojang.serialization.MapCodec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import org.jspecify.annotations.NonNull;

public record HolderAction(EntityAction action) implements ItemAction {
    public static final MapCodec<HolderAction> CODEC = RecordCodecBuilder.mapCodec(i -> i.group(
            EntityAction.CODEC.fieldOf("action").forGetter(HolderAction::action)
    ).apply(i, HolderAction::new));

    @Override
    public void execute(@NonNull ItemActionContext ctx) {
        this.action.execute(ctx.holder(), ctx);
    }

    @Override
    public @NonNull MapCodec<HolderAction> codec() {
        return CODEC;
    }
}
