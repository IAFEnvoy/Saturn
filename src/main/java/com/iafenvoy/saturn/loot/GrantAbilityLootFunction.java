package com.iafenvoy.saturn.loot;

import com.iafenvoy.saturn.Saturn;
import com.iafenvoy.saturn.registry.SaturnAttachments;
import com.iafenvoy.saturn.registry.SaturnDatapackRegistries;
import com.iafenvoy.saturn.registry.SaturnResourceKeys;
import com.iafenvoy.saturn.runtime.ability.AbilityEventBridge;
import com.mojang.serialization.MapCodec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import net.minecraft.resources.Identifier;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.storage.loot.LootContext;
import net.minecraft.world.level.storage.loot.LootContext.EntityTarget;
import net.minecraft.world.level.storage.loot.functions.LootItemConditionalFunction;
import net.minecraft.world.level.storage.loot.predicates.LootItemCondition;
import org.jspecify.annotations.NonNull;

import java.util.List;

public final class GrantAbilityLootFunction extends LootItemConditionalFunction {
    public static final MapCodec<GrantAbilityLootFunction> CODEC = RecordCodecBuilder.mapCodec(i -> commonFields(i).and(i.group(
            EntityTarget.CODEC.optionalFieldOf("entity", EntityTarget.THIS).forGetter(function -> function.target),
            Identifier.CODEC.fieldOf("ability").forGetter(function -> function.ability),
            Identifier.CODEC.optionalFieldOf("source", Identifier.fromNamespaceAndPath(Saturn.MOD_ID, "loot")).forGetter(function -> function.source)
    )).apply(i, GrantAbilityLootFunction::new));
    private final EntityTarget target;
    private final Identifier ability;
    private final Identifier source;

    private GrantAbilityLootFunction(List<LootItemCondition> conditions, EntityTarget target, Identifier ability, Identifier source) {
        super(conditions);
        this.target = target;
        this.ability = ability;
        this.source = source;
    }

    @Override
    public @NonNull MapCodec<GrantAbilityLootFunction> codec() {
        return CODEC;
    }

    @Override
    public @NonNull ItemStack run(@NonNull ItemStack stack, @NonNull LootContext context) {
        Entity entity = this.target.get(context);
        if (entity != null)
            SaturnDatapackRegistries.holder(entity.registryAccess(), SaturnResourceKeys.ABILITY, this.ability)
                    .ifPresent(ability -> {
                        if (entity.getData(SaturnAttachments.ABILITY_HOLDER).grant(ability, this.source)
                                && entity instanceof LivingEntity living)
                            AbilityEventBridge.rebuildTriggerSubscriptions(living);
                    });
        return stack;
    }
}
