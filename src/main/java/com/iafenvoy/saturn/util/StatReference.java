package com.iafenvoy.saturn.util;

import com.mojang.datafixers.util.Either;
import com.mojang.serialization.Codec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.resources.Identifier;
import net.minecraft.stats.Stat;
import net.minecraft.stats.StatType;
import net.minecraft.stats.Stats;

import java.util.Optional;

/**
 * A vanilla custom-stat ID, or a stat type and an ID from that type's registry.
 */
public record StatReference(Either<Identifier, TypedStat> stat) {
    public static final Codec<StatReference> CODEC = Codec.either(Identifier.CODEC, TypedStat.CODEC).xmap(StatReference::new, StatReference::stat);

    public Optional<Stat<?>> resolve() {
        return this.stat.map(id -> BuiltInRegistries.CUSTOM_STAT.getOptional(id).map(Stats.CUSTOM::get), TypedStat::resolve);
    }

    public record TypedStat(Identifier statType, Identifier id) {
        public static final Codec<TypedStat> CODEC = RecordCodecBuilder.create(i -> i.group(
                Identifier.CODEC.fieldOf("stat_type").forGetter(TypedStat::statType),
                Identifier.CODEC.fieldOf("id").forGetter(TypedStat::id)
        ).apply(i, TypedStat::new));

        private Optional<Stat<?>> resolve() {
            return BuiltInRegistries.STAT_TYPE.getOptional(this.statType).flatMap(type -> resolve(type, this.id));
        }

        private static <T> Optional<Stat<?>> resolve(StatType<T> type, Identifier id) {
            return type.getRegistry().getOptional(id).map(type::get);
        }
    }
}
