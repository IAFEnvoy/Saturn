package com.iafenvoy.saturn.data.action.builtin.bientity.meta;

import com.iafenvoy.saturn.data.action.BiEntityAction;
import com.iafenvoy.saturn.data.context.action.BiEntityActionContext;
import com.mojang.serialization.MapCodec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import org.jspecify.annotations.NonNull;

public record InvertAction(BiEntityAction action) implements BiEntityAction {
    public static final MapCodec<InvertAction> CODEC = RecordCodecBuilder.mapCodec(i -> i.group(
            BiEntityAction.CODEC.fieldOf("action").forGetter(InvertAction::action)
    ).apply(i, InvertAction::new));

    @Override
    public void execute(@NonNull BiEntityActionContext ctx) {
        this.action.execute(ctx.target(), ctx.actor(), ctx);
    }

    @Override
    public @NonNull MapCodec<InvertAction> codec() {
        return CODEC;
    }
}
