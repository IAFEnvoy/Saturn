package com.iafenvoy.saturn.data.action.builtin.entity;

import com.iafenvoy.saturn.data.action.EntityAction;
import com.iafenvoy.saturn.data.context.action.EntityActionContext;
import com.iafenvoy.saturn.util.MiscUtil;
import com.iafenvoy.saturn.util.formula.NumberProvider;
import com.mojang.serialization.MapCodec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import net.minecraft.core.Holder;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.world.damagesource.DamageSource;
import net.minecraft.world.damagesource.DamageType;
import org.jspecify.annotations.NonNull;

public record DamageAction(Holder<DamageType> damageType, NumberProvider amount) implements EntityAction {
    public static final MapCodec<DamageAction> CODEC = RecordCodecBuilder.mapCodec(i -> i.group(
            DamageType.CODEC.fieldOf("damage_type").forGetter(DamageAction::damageType),
            NumberProvider.CODEC.fieldOf("amount").forGetter(DamageAction::amount)
    ).apply(i, DamageAction::new));

    @Override
    public void execute(@NonNull EntityActionContext ctx) {
        double amount = MiscUtil.positive(this.amount.evaluate(ctx.formula()));
        if (amount > 0 && ctx.entity().level() instanceof ServerLevel level)
            ctx.entity().hurtServer(level, new DamageSource(this.damageType), (float) amount);
    }

    @Override
    public @NonNull MapCodec<DamageAction> codec() {
        return CODEC;
    }
}
