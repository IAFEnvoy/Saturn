package com.iafenvoy.saturn.compat.kubejs.binding;

import com.google.gson.JsonElement;
import com.iafenvoy.saturn.compat.kubejs.SaturnKubeJsApi;
import com.iafenvoy.saturn.compat.kubejs.codec.SaturnKubeJsDataCodec;
import com.iafenvoy.saturn.data.cost.Cost;
import com.iafenvoy.saturn.util.formula.FormulaContext;
import dev.latvian.mods.kubejs.typings.Info;
import net.minecraft.world.entity.Entity;

import java.util.List;

/**
 * Common resource operations exposed as {@code SaturnResources}.
 */
public final class SaturnKubeJsResourceBindings {
    @Info("Simulates the whole cost array, then attempts each deduction in order; real failures warn and continue.")
    public Object consume(Entity entity, JsonElement costs) {
        List<Cost> decoded = SaturnKubeJsDataCodec.decodeCached(Cost.LIST_CODEC, costs, entity.level().registryAccess());
        return SaturnKubeJsApi.tryConsumeResources(entity, decoded, FormulaContext.of(entity));
    }
}
