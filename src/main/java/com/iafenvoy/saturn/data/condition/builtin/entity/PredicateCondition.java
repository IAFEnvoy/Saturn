package com.iafenvoy.saturn.data.condition.builtin.entity;

import com.iafenvoy.saturn.data.condition.EntityCondition;
import com.iafenvoy.saturn.data.context.condition.EntityConditionContext;
import com.mojang.serialization.MapCodec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import net.minecraft.core.Holder;
import net.minecraft.core.registries.Registries;
import net.minecraft.resources.Identifier;
import net.minecraft.resources.ResourceKey;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.world.level.storage.loot.LootContext;
import net.minecraft.world.level.storage.loot.LootParams;
import net.minecraft.world.level.storage.loot.parameters.LootContextParamSets;
import net.minecraft.world.level.storage.loot.parameters.LootContextParams;
import net.minecraft.world.level.storage.loot.predicates.LootItemCondition;
import org.jspecify.annotations.NonNull;

import java.util.Optional;

public record PredicateCondition(Identifier predicate) implements EntityCondition {
    public static final MapCodec<PredicateCondition> CODEC = RecordCodecBuilder.mapCodec(i -> i.group(
            Identifier.CODEC.fieldOf("predicate").forGetter(PredicateCondition::predicate)
    ).apply(i, PredicateCondition::new));

    @Override
    public boolean test(@NonNull EntityConditionContext ctx) {
        if (!(ctx.entity().level() instanceof ServerLevel level)) return false;
        Optional<Holder.Reference<LootItemCondition>> condition = level.getServer().reloadableRegistries().lookup().get(ResourceKey.create(Registries.PREDICATE, this.predicate));
        return condition.isPresent() && condition.get().value().test(new LootContext.Builder(new LootParams.Builder(level)
                .withParameter(LootContextParams.ORIGIN, ctx.entity().position())
                .withParameter(LootContextParams.THIS_ENTITY, ctx.entity()).create(LootContextParamSets.COMMAND)).create(Optional.empty()));
    }

    @Override
    public @NonNull MapCodec<PredicateCondition> codec() {
        return CODEC;
    }
}
