package com.iafenvoy.saturn.data.action.builtin.entity;

import com.iafenvoy.saturn.data.action.EntityAction;
import com.iafenvoy.saturn.data.context.action.EntityActionContext;
import com.iafenvoy.saturn.util.DatapackCommands;
import com.mojang.serialization.Codec;
import com.mojang.serialization.MapCodec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import org.jspecify.annotations.NonNull;

public record ExecuteCommandAction(String command) implements EntityAction {
    public static final MapCodec<ExecuteCommandAction> CODEC = RecordCodecBuilder.mapCodec(i -> i.group(
            Codec.STRING.fieldOf("command").forGetter(ExecuteCommandAction::command)
    ).apply(i, ExecuteCommandAction::new));

    @Override
    public void execute(@NonNull EntityActionContext ctx) {
        DatapackCommands.execute(ctx.entity(), ctx.position(), this.command);
    }

    @Override
    public @NonNull MapCodec<ExecuteCommandAction> codec() {
        return CODEC;
    }
}
