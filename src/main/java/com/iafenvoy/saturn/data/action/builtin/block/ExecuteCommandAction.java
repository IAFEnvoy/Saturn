package com.iafenvoy.saturn.data.action.builtin.block;

import com.iafenvoy.saturn.data.action.BlockAction;
import com.iafenvoy.saturn.data.context.action.BlockActionContext;
import com.iafenvoy.saturn.util.DatapackCommands;
import com.mojang.serialization.Codec;
import com.mojang.serialization.MapCodec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.server.permissions.LevelBasedPermissionSet;
import org.jspecify.annotations.NonNull;

public record ExecuteCommandAction(String command) implements BlockAction {
    public static final MapCodec<ExecuteCommandAction> CODEC = RecordCodecBuilder.mapCodec(i -> i.group(
            Codec.STRING.fieldOf("command").forGetter(ExecuteCommandAction::command)
    ).apply(i, ExecuteCommandAction::new));

    @Override
    public void execute(@NonNull BlockActionContext ctx) {
        if (ctx.level() instanceof ServerLevel level)
            DatapackCommands.execute(level.getServer().createCommandSourceStack().withLevel(level).withPosition(ctx.pos().getCenter())
                    .withPermission(LevelBasedPermissionSet.GAMEMASTER).withSuppressedOutput(), this.command);
    }

    @Override
    public @NonNull MapCodec<ExecuteCommandAction> codec() {
        return CODEC;
    }
}
