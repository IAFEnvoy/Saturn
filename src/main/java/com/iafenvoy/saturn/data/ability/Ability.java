package com.iafenvoy.saturn.data.ability;

import com.iafenvoy.saturn.data.IconReference;
import com.iafenvoy.saturn.data.condition.EntityCondition;
import com.iafenvoy.saturn.data.cost.Cost;
import com.iafenvoy.saturn.data.storage.DataStorage;
import com.iafenvoy.saturn.data.storage.DataStorageCollector;
import com.iafenvoy.saturn.data.storage.DataStorageDeclaration;
import com.iafenvoy.saturn.data.storage.builtin.ChargesDataStorage;
import com.iafenvoy.saturn.registry.SaturnResourceKeys;
import com.iafenvoy.saturn.util.formula.NumberProvider;
import com.iafenvoy.saturn.util.formula.number.Constant;
import com.mojang.serialization.Codec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import net.minecraft.core.Holder;
import net.minecraft.resources.RegistryFixedCodec;
import org.jspecify.annotations.NonNull;

import java.util.List;
import java.util.Optional;

/**
 * A named ability; behaviour is selected by the built-in type, which declares the state kinds the ability keeps and
 * carries whatever parameters its own algorithm reads, so this record holds what every type shares: what it costs,
 * when it may run, and how it is named and drawn.
 *
 * <p>Every ability is a registry entry, named by its own id: its registry holder is what the grant ledger and the
 * cooldowns hand around. {@code hidden} only drops the entry from an artifact's tooltip; the action
 * fields, the cooldown and the damage condition are declared and read by the types that use them, so this record
 * declares none of them.
 *
 * <p>Element affinity is not part of this vocabulary: which elements an ability prefers is a rule of the world it
 * belongs to, so a dependent mod expresses it with its own condition or its own ability type rather than a field here.
 */
public record Ability(AbilityType type, List<Cost> costs,
                      NumberProvider castTime, Optional<IconReference> icon,
                      Optional<ChargesDataStorage.Settings> charges, EntityCondition condition,
                      boolean hidden) implements DataStorageDeclaration {
    public static final Codec<Holder<Ability>> CODEC = RegistryFixedCodec.create(SaturnResourceKeys.ABILITY);
    public static final Codec<Ability> DIRECT_CODEC = RecordCodecBuilder.create(i -> i.group(
            AbilityType.CODEC.forGetter(Ability::type),
            Cost.LIST_CODEC.optionalFieldOf("costs", List.of()).forGetter(Ability::costs),
            NumberProvider.CODEC.optionalFieldOf("cast_time", new Constant(0.0D)).forGetter(Ability::castTime),
            IconReference.CODEC.optionalFieldOf("icon").forGetter(Ability::icon),
            ChargesDataStorage.Settings.CODEC.optionalFieldOf("charges").forGetter(Ability::charges),
            EntityCondition.optionalCodec("condition").forGetter(Ability::condition),
            Codec.BOOL.optionalFieldOf("hidden", false).forGetter(Ability::hidden)
    ).apply(i, Ability::new));

    // Collected on demand from the type, which is where the declaration lives; the holder only ever stores what was
    // written, so a kind nothing wrote costs nothing.
    @Override
    public List<DataStorage> storages() {
        DataStorageCollector collector = DataStorageCollector.create();
        this.type.createComponents(this, collector);
        return collector.build();
    }

    // Actions refer to abilities through holders: keep diagnostic output shallow so logging a cyclic datapack
    // definition cannot recurse through its holder.
    @Override
    public @NonNull String toString() {
        return "Ability[type=" + this.type.getClass().getSimpleName() + ", costs=" + this.costs.size()
                + ", charges=" + this.charges.isPresent() + "]";
    }
}
