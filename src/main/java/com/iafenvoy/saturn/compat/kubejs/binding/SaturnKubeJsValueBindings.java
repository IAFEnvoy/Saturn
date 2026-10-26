package com.iafenvoy.saturn.compat.kubejs.binding;

import com.google.gson.JsonObject;
import com.iafenvoy.saturn.attachment.ResourceHolderAttachment;
import com.iafenvoy.saturn.compat.kubejs.callback.SaturnJsValueCallbacks;
import com.iafenvoy.saturn.compat.kubejs.codec.SaturnKubeJsDataCodec;
import com.iafenvoy.saturn.data.resource.Resource;
import com.iafenvoy.saturn.data.resource.ResourceValueProvider;
import com.iafenvoy.saturn.registry.SaturnDatapackRegistries;
import com.iafenvoy.saturn.registry.SaturnResourceKeys;
import com.iafenvoy.saturn.util.formula.FormulaContext;
import com.iafenvoy.saturn.util.formula.NumberProvider;
import com.iafenvoy.saturn.util.function.QuadFunction;
import dev.latvian.mods.kubejs.typings.Info;
import net.minecraft.core.Holder;
import net.minecraft.resources.Identifier;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.LivingEntity;

import java.util.function.BiFunction;

/**
 * KubeJS registrations for script-backed number and resource value providers.
 */
public final class SaturnKubeJsValueBindings {
    @Info("Registers a number provider. Datapack type: saturn:js")
    public void number(String id, BiFunction<FormulaContext, JsonObject, Double> callback) {
        SaturnJsValueCallbacks.registerNumber(id, callback);
    }

    @Info("Registers a resource value provider. Datapack type: saturn:js")
    public void resourceValue(String id, QuadFunction<ResourceHolderAttachment, Holder<Resource>, FormulaContext, JsonObject, Double> callback) {
        SaturnJsValueCallbacks.registerResource(id, callback);
    }

    @Info("Decodes and evaluates any registered number provider definition.")
    public double evaluateNumber(Entity entity, JsonObject definition) {
        NumberProvider provider = SaturnKubeJsDataCodec.decodeCached(NumberProvider.CODEC, definition, entity.level().registryAccess());
        double value = provider.evaluate(FormulaContext.of(entity));
        return provider.assertFinite(value) ? value : 0.0D;
    }

    @Info("Decodes and evaluates any registered resource-value provider definition.")
    public double evaluateResource(LivingEntity entity, String resource, JsonObject definition) {
        Identifier id = Identifier.tryParse(resource);
        if (id == null) throw new IllegalArgumentException("Invalid Saturn identifier: " + resource);
        Holder<Resource> holder = SaturnDatapackRegistries.holder(entity.level().registryAccess(), SaturnResourceKeys.RESOURCE, id)
                .orElseThrow(() -> new IllegalArgumentException("Unknown Saturn resource: " + resource));
        ResourceValueProvider provider = SaturnKubeJsDataCodec.decodeCached(ResourceValueProvider.CODEC, definition, entity.level().registryAccess());
        return provider.resolve(entity, holder, FormulaContext.of(entity));
    }
}
