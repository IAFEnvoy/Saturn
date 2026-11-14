package com.iafenvoy.saturn.data.action.builtin.entity;

import com.iafenvoy.saturn.data.action.EntityAction;
import com.iafenvoy.saturn.data.context.action.EntityActionContext;
import com.iafenvoy.saturn.util.StatReference;
import com.iafenvoy.saturn.util.codec.MiscCodecs;
import com.iafenvoy.saturn.util.formula.NumberProvider;
import com.iafenvoy.saturn.util.formula.number.Constant;
import com.mojang.serialization.MapCodec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import net.minecraft.server.level.ServerPlayer;
import org.jspecify.annotations.NonNull;

import java.util.List;

public record AwardStatAction(List<StatReference> stats, NumberProvider amount) implements EntityAction {
    public static final MapCodec<AwardStatAction> CODEC = RecordCodecBuilder.mapCodec(i -> i.group(
            MiscCodecs.combineCodec(StatReference.CODEC).fieldOf("stat").forGetter(AwardStatAction::stats),
            NumberProvider.CODEC.lenientOptionalFieldOf("amount", new Constant(1)).forGetter(AwardStatAction::amount)
    ).apply(i, AwardStatAction::new));

    public AwardStatAction {
        stats = List.copyOf(stats);
    }

    @Override
    public void execute(@NonNull EntityActionContext ctx) {
        if (!(ctx.entity() instanceof ServerPlayer player)) return;
        int amount = (int) this.amount.evaluate(ctx.formula());
        for (StatReference stat : this.stats) stat.resolve().ifPresent(value -> player.awardStat(value, amount));
    }

    @Override
    public @NonNull MapCodec<AwardStatAction> codec() {
        return CODEC;
    }
}
