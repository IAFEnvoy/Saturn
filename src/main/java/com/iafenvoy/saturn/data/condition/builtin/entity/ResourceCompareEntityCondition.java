package com.iafenvoy.saturn.data.condition.builtin.entity;

import com.iafenvoy.saturn.data.condition.EntityCondition;
import com.iafenvoy.saturn.data.context.condition.EntityConditionContext;
import com.iafenvoy.saturn.data.resource.Resource;
import com.iafenvoy.saturn.registry.SaturnAttachments;
import com.iafenvoy.saturn.util.formula.NumberProvider;
import com.mojang.serialization.MapCodec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import net.minecraft.core.Holder;
import org.jspecify.annotations.NonNull;

public record ResourceCompareEntityCondition(Holder<Resource> resource,
                                             NumberProvider min) implements EntityCondition {
    public static final MapCodec<ResourceCompareEntityCondition> CODEC = RecordCodecBuilder.mapCodec(i -> i.group(
            Resource.CODEC.fieldOf("resource").forGetter(ResourceCompareEntityCondition::resource), NumberProvider.CODEC.fieldOf("min").forGetter(ResourceCompareEntityCondition::min)
    ).apply(i, ResourceCompareEntityCondition::new));

    @Override
    public boolean test(@NonNull EntityConditionContext ctx) {
        return ctx.entity().getData(SaturnAttachments.RESOURCE_HOLDER).get(this.resource) >= this.min.evaluate(ctx.formula());
    }

    @Override
    public @NonNull MapCodec<ResourceCompareEntityCondition> codec() {
        return CODEC;
    }
}
