package com.iafenvoy.saturn.data.action.builtin.entity;

import com.iafenvoy.saturn.data.ability.Ability;
import com.iafenvoy.saturn.data.action.EntityAction;
import com.iafenvoy.saturn.data.context.action.EntityActionContext;
import com.iafenvoy.saturn.registry.SaturnAttachments;
import com.iafenvoy.saturn.runtime.ability.AbilityEventBridge;
import com.mojang.serialization.MapCodec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import net.minecraft.core.Holder;
import net.minecraft.resources.Identifier;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.LivingEntity;
import org.jspecify.annotations.NonNull;

/**
 * Grants an ability using an explicit persistent source identity.
 */
public record GrantAbilityAction(Holder<Ability> ability, Identifier source) implements EntityAction {
    public static final MapCodec<GrantAbilityAction> CODEC = RecordCodecBuilder.mapCodec(i -> i.group(
            Ability.CODEC.fieldOf("ability").forGetter(GrantAbilityAction::ability),
            Identifier.CODEC.fieldOf("source").forGetter(GrantAbilityAction::source)
    ).apply(i, GrantAbilityAction::new));

    @Override
    public void execute(@NonNull EntityActionContext ctx) {
        Entity entity = ctx.entity();
        if (entity.getData(SaturnAttachments.ABILITY_HOLDER).grant(this.ability, this.source)
                && entity instanceof LivingEntity living)
            AbilityEventBridge.rebuildTriggerSubscriptions(living);
    }

    @Override
    public @NonNull MapCodec<GrantAbilityAction> codec() {
        return CODEC;
    }
}
