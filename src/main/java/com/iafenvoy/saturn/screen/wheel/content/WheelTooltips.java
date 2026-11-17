package com.iafenvoy.saturn.screen.wheel.content;

import com.iafenvoy.saturn.data.cost.Cost;
import com.iafenvoy.saturn.data.cost.CostPayment;
import com.iafenvoy.saturn.data.cost.context.CostContext;
import com.iafenvoy.saturn.data.cost.context.CostOrigins;
import com.iafenvoy.saturn.util.DefinitionText;
import com.iafenvoy.saturn.util.TooltipText;
import net.minecraft.network.chat.Component;
import net.minecraft.world.entity.player.Player;

import java.util.ArrayList;
import java.util.List;
import java.util.Locale;

final class WheelTooltips {
    private WheelTooltips() {
    }

    static Component number(double value) {
        if (!Double.isFinite(value)) return Component.literal("0");
        if (value == Math.rint(value) && Math.abs(value) < 1.0E15D)
            return Component.literal(Long.toString((long) value));
        return Component.literal(String.format(Locale.ROOT, "%.1f", value));
    }

    // Only the resource channel is spelled out - everything else becomes "something else", never a guess - and the
    // amounts are what the server would collect for the whole array, in the context it would collect it in. Pricing is
    // one pass with no store asked, so entries that reach the same resource add up the way they will when it is paid.
    static Component costs(List<Cost> costs, Player player) {
        CostContext context = CostContext.of(player, CostOrigins.ABILITY);
        CostPayment payment = CostPayment.of(context);
        boolean other = payment.price(costs).isPresent();
        List<Component> parts = new ArrayList<>(costs.size());
        // The price is keyed by the resource itself, so naming one needs no registry at all.
        payment.resourceAmounts().forEach((resource, amount) -> parts.add(Component.translatable("wheel.saturn.tooltip.cost_part",
                DefinitionText.nameOrId(resource), number(amount))));
        if (other || payment.beyondResources()) parts.add(Component.translatable("wheel.saturn.tooltip.cost_other"));
        return TooltipText.join(parts);
    }

}
