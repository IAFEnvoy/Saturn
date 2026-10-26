package com.iafenvoy.saturn.data.ability.type;

import com.iafenvoy.saturn.data.ability.*;
import com.iafenvoy.saturn.data.ability.target.SelfTargetSelector;
import com.iafenvoy.saturn.data.action.BiEntityAction;
import com.iafenvoy.saturn.data.action.EntityAction;
import com.iafenvoy.saturn.data.condition.BiEntityCondition;
import com.iafenvoy.saturn.data.condition.DamageCondition;
import com.iafenvoy.saturn.data.storage.DataStorageCollector;
import com.iafenvoy.saturn.data.trigger.Trigger;
import com.iafenvoy.saturn.util.MiscUtil;
import com.iafenvoy.saturn.util.formula.FormulaContext;
import com.iafenvoy.saturn.util.formula.NumberProvider;
import com.iafenvoy.saturn.util.formula.number.Constant;
import com.mojang.serialization.MapCodec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import net.minecraft.world.entity.LivingEntity;

import java.util.List;

/**
 * A skill a signal wakes up: which signals, how often they get through, and the action fields that then run.
 */
public record TriggeredAbilityType(List<Trigger> triggers, NumberProvider chance, DamageCondition damageCondition,
                                   EntityAction entityAction, TargetSelector targetSelector,
                                   BiEntityCondition targetCondition, BiEntityAction biEntityAction,
                                   NumberProvider cooldown)
        implements AbilityType, ActionCarrier, TriggerSource, CooldownSource {
    public static final MapCodec<TriggeredAbilityType> CODEC = RecordCodecBuilder.mapCodec(i -> i.group(
            Trigger.CODEC.listOf().optionalFieldOf("triggers", List.of()).forGetter(TriggeredAbilityType::triggers),
            NumberProvider.CODEC.optionalFieldOf("chance", new Constant(1.0D)).forGetter(TriggeredAbilityType::chance),
            DamageCondition.optionalCodec("damage_condition").forGetter(TriggeredAbilityType::damageCondition),
            EntityAction.optionalCodec("entity_action").forGetter(TriggeredAbilityType::entityAction),
            TargetSelector.CODEC.optionalFieldOf("target_selector", SelfTargetSelector.INSTANCE).forGetter(TriggeredAbilityType::targetSelector),
            BiEntityCondition.optionalCodec("target_condition").forGetter(TriggeredAbilityType::targetCondition),
            BiEntityAction.optionalCodec("bi_entity_action").forGetter(TriggeredAbilityType::biEntityAction),
            NumberProvider.CODEC.optionalFieldOf("cooldown", new Constant(0.0D)).forGetter(TriggeredAbilityType::cooldown)
    ).apply(i, TriggeredAbilityType::new));

    @Override
    public MapCodec<TriggeredAbilityType> codec() {
        return CODEC;
    }

    // A signal pays through the same gate a press does and then runs the same fields, so the kinds are the same as
    // saturn:active's.
    @Override
    public void createComponents(Ability ability, DataStorageCollector collector) {
        AbilityType.super.createComponents(ability, collector);
        AbilityType.declareUseStorages(ability, collector);
    }

    // A chance the formula cannot answer is a chance that does not get through.
    @Override
    public boolean rolls(LivingEntity entity, FormulaContext context) {
        final double chance;
        try {
            chance = this.chance.evaluate(context);
        } catch (RuntimeException exception) {
            return false;
        }
        if (MiscUtil.positive(chance) <= 0.0D) return false;
        return chance >= 1.0D || entity.getRandom().nextDouble() < chance;
    }
}
