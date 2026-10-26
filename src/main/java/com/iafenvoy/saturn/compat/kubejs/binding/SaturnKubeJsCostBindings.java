package com.iafenvoy.saturn.compat.kubejs.binding;

import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.iafenvoy.saturn.compat.kubejs.SaturnJsWarnings;
import com.iafenvoy.saturn.compat.kubejs.callback.SaturnJsCostCallbacks;
import com.iafenvoy.saturn.compat.kubejs.codec.SaturnKubeJsDataCodec;
import com.iafenvoy.saturn.data.cost.Cost;
import com.iafenvoy.saturn.data.cost.CostPayment;
import com.iafenvoy.saturn.data.cost.context.CostContext;
import com.iafenvoy.saturn.data.cost.context.CostOrigins;
import com.iafenvoy.saturn.util.formula.FormulaContext;
import com.iafenvoy.saturn.util.function.TriConsumer;
import com.iafenvoy.saturn.util.function.TriPredicate;
import dev.latvian.mods.kubejs.typings.Info;
import net.minecraft.world.entity.player.Player;

import java.util.List;

/**
 * Complete data-driven cost operations exposed as {@code SaturnCosts}.
 */
public final class SaturnKubeJsCostBindings {
    @Info("Registers a script cost. Datapack type: saturn:js")
    public void register(String id, TriPredicate<Player, JsonObject, FormulaContext> check,
                         TriConsumer<Player, JsonObject, FormulaContext> consume) {
        SaturnJsCostCallbacks.register(id, check, consume);
    }

    @Info("Checks one complete Saturn cost definition without changing player state.")
    public boolean check(Player player, JsonElement definition) {
        Cost cost = SaturnKubeJsDataCodec.decodeCached(Cost.CODEC, definition, player.level().registryAccess());
        CostPayment payment = CostPayment.of(CostContext.of(player, CostOrigins.SCRIPT));
        return payment.price(List.of(cost)).isEmpty() && payment.test().isEmpty();
    }

    @Info("Simulates one Saturn cost, then attempts its deduction; a real failure only warns and still returns true.")
    public boolean consume(Player player, JsonElement definition) {
        if (player.level().isClientSide()) {
            SaturnJsWarnings.warnOnce("cost.client", "SaturnCosts.consume was called from a client script; nothing was consumed");
            return false;
        }
        Cost cost = SaturnKubeJsDataCodec.decodeCached(Cost.CODEC, definition, player.level().registryAccess());
        // One definition is one fee, and it pays through the same bill every other caller uses - so a script cannot
        // reach a channel the common payment would have checked.
        return CostPayment.pay(List.of(cost), CostContext.of(player, CostOrigins.SCRIPT)).isSuccess();
    }
}
