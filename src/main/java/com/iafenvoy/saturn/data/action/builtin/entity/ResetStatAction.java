package com.iafenvoy.saturn.data.action.builtin.entity;

import com.iafenvoy.saturn.data.action.EntityAction;
import com.iafenvoy.saturn.data.context.action.EntityActionContext;
import com.iafenvoy.saturn.util.StatReference;
import com.iafenvoy.saturn.util.codec.MiscCodecs;
import com.mojang.serialization.MapCodec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import net.minecraft.server.level.ServerPlayer;
import org.jspecify.annotations.NonNull;

import java.util.List;

public record ResetStatAction(List<StatReference> stats) implements EntityAction {
    public static final MapCodec<ResetStatAction> CODEC = RecordCodecBuilder.mapCodec(i -> i.group(
            MiscCodecs.combineCodec(StatReference.CODEC).fieldOf("stat").forGetter(ResetStatAction::stats)
    ).apply(i, ResetStatAction::new));

    public ResetStatAction {
        stats = List.copyOf(stats);
    }

    @Override
    public void execute(@NonNull EntityActionContext ctx) {
        if (ctx.entity() instanceof ServerPlayer player)
            for (StatReference stat : this.stats) stat.resolve().ifPresent(player::resetStat);
    }

    @Override
    public @NonNull MapCodec<ResetStatAction> codec() {
        return CODEC;
    }
}
