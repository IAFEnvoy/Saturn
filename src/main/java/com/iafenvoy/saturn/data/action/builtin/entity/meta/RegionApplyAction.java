package com.iafenvoy.saturn.data.action.builtin.entity.meta;

import com.iafenvoy.saturn.data.action.BiEntityAction;
import com.iafenvoy.saturn.data.action.EntityAction;
import com.iafenvoy.saturn.data.condition.BiEntityCondition;
import com.iafenvoy.saturn.data.context.action.EntityActionContext;
import com.iafenvoy.saturn.util.formula.NumberProvider;
import com.iafenvoy.saturn.util.formula.number.Constant;
import com.iafenvoy.saturn.util.math.Shape;
import com.mojang.serialization.Codec;
import com.mojang.serialization.MapCodec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import net.minecraft.world.entity.Entity;
import org.jspecify.annotations.NonNull;

public record RegionApplyAction(NumberProvider radius, Shape shape, BiEntityAction biEntityAction,
                                BiEntityCondition biEntityCondition, boolean includeActor) implements EntityAction {
    public static final MapCodec<RegionApplyAction> CODEC = RecordCodecBuilder.mapCodec(i -> i.group(
            NumberProvider.CODEC.lenientOptionalFieldOf("radius", new Constant(16)).forGetter(RegionApplyAction::radius),
            Shape.CODEC.lenientOptionalFieldOf("shape", Shape.CUBE).forGetter(RegionApplyAction::shape),
            BiEntityAction.CODEC.fieldOf("bientity_action").forGetter(RegionApplyAction::biEntityAction),
            BiEntityCondition.optionalCodec("bientity_condition").forGetter(RegionApplyAction::biEntityCondition),
            Codec.BOOL.lenientOptionalFieldOf("include_actor", false).forGetter(RegionApplyAction::includeActor)
    ).apply(i, RegionApplyAction::new));

    @Override
    public void execute(@NonNull EntityActionContext ctx) {
        for (Entity target : this.shape.entities(ctx.entity().level(), ctx.position(), this.radius.evaluate(ctx.formula()))) {
            if (target == ctx.entity() && !this.includeActor) continue;
            if (this.biEntityCondition.test(ctx.entity(), target, ctx))
                this.biEntityAction.execute(ctx.entity(), target, ctx);
        }
    }

    @Override
    public @NonNull MapCodec<RegionApplyAction> codec() {
        return CODEC;
    }
}
