package com.iafenvoy.saturn.data.condition.builtin.damage;

import com.iafenvoy.saturn.data.condition.DamageCondition;
import com.iafenvoy.saturn.data.context.condition.DamageConditionContext;
import com.mojang.serialization.MapCodec;
import net.minecraft.tags.DamageTypeTags;
import org.jspecify.annotations.NonNull;

/**
 * Checks whether the incoming damage belongs to Minecraft's fire damage tag.
 */
public final class FireDamageCondition implements DamageCondition {
    public static final FireDamageCondition INSTANCE = new FireDamageCondition();
    public static final MapCodec<FireDamageCondition> CODEC = MapCodec.unit(() -> INSTANCE);

    private FireDamageCondition() {
    }

    @Override
    public boolean test(@NonNull DamageConditionContext ctx) {
        return ctx.source().is(DamageTypeTags.IS_FIRE);
    }

    @Override
    public @NonNull MapCodec<FireDamageCondition> codec() {
        return CODEC;
    }
}
