package com.iafenvoy.saturn.data.resource;

import com.iafenvoy.saturn.attachment.ResourceHolderAttachment;
import com.iafenvoy.saturn.registry.SaturnAttachments;
import com.iafenvoy.saturn.registry.SaturnRegistries;
import com.iafenvoy.saturn.util.formula.FormulaContext;
import com.mojang.serialization.Codec;
import com.mojang.serialization.MapCodec;
import net.minecraft.core.Holder;
import net.minecraft.world.entity.LivingEntity;

import java.util.function.Function;

/**
 * Resolves a resource-derived value at runtime without exposing attachment mutation to data.
 */
public interface ResourceValueProvider {
    Codec<ResourceValueProvider> CODEC = SaturnRegistries.RESOURCE_VALUE_PROVIDER_TYPE.byNameCodec().dispatch("type", ResourceValueProvider::codec, Function.identity());

    double resolve(ResourceHolderAttachment holder, Holder<Resource> resource, FormulaContext context);

    // Providers that depend on world state override this overload; attachment-backed ones keep the original.
    default double resolve(LivingEntity entity, Holder<Resource> resource, FormulaContext context) {
        return this.resolve(entity.getData(SaturnAttachments.RESOURCE_HOLDER), resource, context);
    }

    MapCodec<? extends ResourceValueProvider> codec();
}
