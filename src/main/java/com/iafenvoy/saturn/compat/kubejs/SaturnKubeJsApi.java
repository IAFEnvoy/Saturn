package com.iafenvoy.saturn.compat.kubejs;

import com.iafenvoy.saturn.data.cost.Cost;
import com.iafenvoy.saturn.data.cost.CostPayment;
import com.iafenvoy.saturn.data.cost.context.CostContext;
import com.iafenvoy.saturn.data.cost.context.CostOrigins;
import com.iafenvoy.saturn.runtime.resulting.Failures;
import com.iafenvoy.saturn.runtime.resulting.ResultWithData;
import com.iafenvoy.saturn.util.formula.FormulaContext;
import net.minecraft.resources.Identifier;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.LivingEntity;

import java.util.List;
import java.util.Map;

/**
 * The helpers a script calls. Only the generic ones live here: anything that names a rule of a world (quality,
 * cultivation, aura) belongs to the mod that owns that rule.
 */
public final class SaturnKubeJsApi {
    private SaturnKubeJsApi() {
    }

    /**
     * Pays a whole array of costs on behalf of an entity, as {@code origin = script}. Prices are evaluated against
     * the given formula context, and the answer is the id-keyed table of what was really taken.
     */
    public static ResultWithData<Map<Identifier, Double>> tryConsumeResources(Entity entity, List<Cost> costs, FormulaContext context) {
        if (!(entity instanceof LivingEntity payer) || entity.level().isClientSide())
            return ResultWithData.failure(Map.of(), Failures.UNKNOWN);
        CostPayment.PaymentResult payment = CostPayment.pay(costs, CostContext.of(payer, context, CostOrigins.SCRIPT));
        return payment.isSuccess() ? ResultWithData.success(payment.resources())
                : ResultWithData.failure(payment.resources(), Failures.UNKNOWN, payment.failedResource());
    }
}
