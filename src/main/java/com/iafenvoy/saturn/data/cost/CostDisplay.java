package com.iafenvoy.saturn.data.cost;

import net.minecraft.network.chat.Component;
import net.minecraft.world.item.ItemStackTemplate;

import java.math.BigDecimal;
import java.util.List;

/**
 * Evaluated price presentation, independent of the payment channel and the host mod's UI or network format.
 */
public record CostDisplay(List<ItemPreview> items, List<Component> descriptions) {
    public CostDisplay {
        items = List.copyOf(items);
        descriptions = List.copyOf(descriptions);
    }

    public static CostDisplay item(ItemStackTemplate item, double amount) {
        return new CostDisplay(List.of(new ItemPreview(item, number(amount))), List.of());
    }

    public static CostDisplay text(Component description) {
        return new CostDisplay(List.of(), List.of(description));
    }

    public static String number(double value) {
        return Double.isFinite(value) ? BigDecimal.valueOf(value).stripTrailingZeros().toPlainString() : Double.toString(value);
    }

    /**
     * The count is display text: it is not limited by a stack's maximum size or integer count.
     */
    public record ItemPreview(ItemStackTemplate item, String amount) {
    }
}
