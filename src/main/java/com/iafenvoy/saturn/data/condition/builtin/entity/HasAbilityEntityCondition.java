package com.iafenvoy.saturn.data.condition.builtin.entity;

import com.iafenvoy.saturn.data.ability.Ability;
import com.iafenvoy.saturn.data.condition.EntityCondition;
import com.iafenvoy.saturn.data.context.condition.EntityConditionContext;
import com.iafenvoy.saturn.registry.SaturnAttachments;
import com.mojang.serialization.MapCodec;
import net.minecraft.core.Holder;
import org.jspecify.annotations.NonNull;

public record HasAbilityEntityCondition(Holder<Ability> ability) implements EntityCondition {
    public static final MapCodec<HasAbilityEntityCondition> CODEC = Ability.CODEC.fieldOf("ability").xmap(HasAbilityEntityCondition::new, HasAbilityEntityCondition::ability);

    @Override
    public boolean test(@NonNull EntityConditionContext ctx) {
        return ctx.entity().getData(SaturnAttachments.ABILITY_HOLDER).has(this.ability);
    }

    @Override
    public @NonNull MapCodec<HasAbilityEntityCondition> codec() {
        return CODEC;
    }
}
