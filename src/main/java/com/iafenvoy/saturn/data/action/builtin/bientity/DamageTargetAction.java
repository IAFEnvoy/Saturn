package com.iafenvoy.saturn.data.action.builtin.bientity;

import com.iafenvoy.saturn.data.action.BiEntityAction;
import com.iafenvoy.saturn.data.context.action.BiEntityActionContext;
import com.iafenvoy.saturn.util.MiscUtil;
import com.iafenvoy.saturn.util.formula.NumberProvider;
import com.mojang.serialization.MapCodec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import net.minecraft.core.Holder;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.world.damagesource.DamageSource;
import net.minecraft.world.damagesource.DamageType;
import org.jspecify.annotations.NonNull;

public record DamageTargetAction(Holder<DamageType> damageType, NumberProvider amount) implements BiEntityAction {
    public static final MapCodec<DamageTargetAction> CODEC = RecordCodecBuilder.mapCodec(i -> i.group(
            DamageType.CODEC.fieldOf("damage_type").forGetter(DamageTargetAction::damageType),
            NumberProvider.CODEC.fieldOf("amount").forGetter(DamageTargetAction::amount)
    ).apply(i, DamageTargetAction::new));

    @Override
    public void execute(@NonNull BiEntityActionContext ctx) {
        double amount = MiscUtil.positive(this.amount.evaluate(ctx.formula()));
        if (amount > 0 && ctx.target().level() instanceof ServerLevel level)
            ctx.target().hurtServer(level, new DamageSource(this.damageType, ctx.actor()), (float) amount);
    }

    @Override
    public @NonNull MapCodec<DamageTargetAction> codec() {
        return CODEC;
    }
}
