package com.iafenvoy.saturn.loot;

import com.iafenvoy.saturn.registry.SaturnAttachments;
import com.iafenvoy.saturn.registry.SaturnDatapackRegistries;
import com.iafenvoy.saturn.registry.SaturnResourceKeys;
import com.mojang.serialization.MapCodec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import net.minecraft.resources.Identifier;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.level.storage.loot.LootContext;
import net.minecraft.world.level.storage.loot.LootContext.EntityTarget;
import net.minecraft.world.level.storage.loot.predicates.LootItemCondition;
import org.jspecify.annotations.NonNull;

public record HasAbilityLootCondition(EntityTarget target, Identifier ability) implements LootItemCondition {
    public static final MapCodec<HasAbilityLootCondition> CODEC = RecordCodecBuilder.mapCodec(i -> i.group(
            EntityTarget.CODEC.optionalFieldOf("entity", EntityTarget.THIS).forGetter(HasAbilityLootCondition::target),
            Identifier.CODEC.fieldOf("ability").forGetter(HasAbilityLootCondition::ability)
    ).apply(i, HasAbilityLootCondition::new));

    @Override
    public @NonNull MapCodec<HasAbilityLootCondition> codec() {
        return CODEC;
    }

    @Override
    public boolean test(LootContext context) {
        Entity entity = this.target.get(context);
        return entity != null && SaturnDatapackRegistries.holder(entity.registryAccess(), SaturnResourceKeys.ABILITY, this.ability)
                .map(ability -> entity.getData(SaturnAttachments.ABILITY_HOLDER).has(ability)).orElse(false);
    }
}
