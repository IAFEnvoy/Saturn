package com.iafenvoy.saturn.data.action.builtin.entity;

import com.iafenvoy.saturn.data.action.BiEntityAction;
import com.iafenvoy.saturn.data.action.EntityAction;
import com.iafenvoy.saturn.data.action.builtin.entity.meta.RegionApplyAction;
import com.iafenvoy.saturn.data.condition.BiEntityCondition;
import com.iafenvoy.saturn.data.context.action.EntityActionContext;
import com.iafenvoy.saturn.util.formula.NumberProvider;
import com.iafenvoy.saturn.util.formula.number.Constant;
import com.iafenvoy.saturn.util.math.Shape;
import com.mojang.serialization.Codec;
import com.mojang.serialization.MapCodec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import org.jspecify.annotations.NonNull;

public record AreaOfEffectAction(NumberProvider radius, BiEntityAction biEntityAction,
                                 BiEntityCondition biEntityCondition, boolean includeTarget) implements EntityAction {
    public static final MapCodec<AreaOfEffectAction> CODEC = RecordCodecBuilder.mapCodec(i -> i.group(
            NumberProvider.CODEC.lenientOptionalFieldOf("radius", new Constant(16)).forGetter(AreaOfEffectAction::radius),
            BiEntityAction.CODEC.fieldOf("bientity_action").forGetter(AreaOfEffectAction::biEntityAction),
            BiEntityCondition.optionalCodec("bientity_condition").forGetter(AreaOfEffectAction::biEntityCondition),
            Codec.BOOL.lenientOptionalFieldOf("include_target", false).forGetter(AreaOfEffectAction::includeTarget)
    ).apply(i, AreaOfEffectAction::new));

    @Override
    public void execute(@NonNull EntityActionContext ctx) {
        new RegionApplyAction(this.radius, Shape.SPHERE, this.biEntityAction, this.biEntityCondition, this.includeTarget).execute(ctx);
    }

    @Override
    public @NonNull MapCodec<AreaOfEffectAction> codec() {
        return CODEC;
    }
}
