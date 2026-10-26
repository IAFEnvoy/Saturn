package com.iafenvoy.saturn.screen.resourcebar.renderer;

import com.iafenvoy.saturn.config.SaturnClientConfig;
import com.iafenvoy.saturn.config.SaturnClientConfig.ResourceBarIconLayout;
import com.iafenvoy.saturn.data.resource.ResourceBar.Anchor;
import com.iafenvoy.saturn.data.resource.ResourceBar.ValueDisplay;
import com.iafenvoy.saturn.data.resourcebar.builtin.renderdata.OriginsRenderData;
import com.iafenvoy.saturn.screen.resourcebar.ResourceBarRenderState;
import com.iafenvoy.saturn.screen.resourcebar.ResourceBarRenderer.Context;
import net.minecraft.network.chat.Component;

import java.util.Locale;
import java.util.Optional;

/**
 * Common labels, icons, and number display shared by stateless resource-bar renderers.
 */
final class ResourceBarRendererHelper {
    static void decorations(Context context, boolean showValueDisplay) {
        ResourceBarRenderState state = context.state();
        icon(state).ifPresent(icon -> renderIcon(context, icon));
        if (showValueDisplay && state.valueDisplay() != ValueDisplay.NONE) renderValueDisplay(context);
        if (SaturnClientConfig.INSTANCE.resourceBars.showNames.getValue())
            state.name().ifPresent(name -> renderName(context, name));
    }

    static void value(Context context, int color, boolean showMaximum, String valueFormat) {
        ResourceBarRenderState state = context.state();
        String value = valueFormat.replace("%current%", format(state.current()))
                .replace("%maximum%", format(state.maximum()));
        if (showMaximum && !valueFormat.contains("%maximum%")) value += " / " + format(state.maximum());
        context.graphics().text(context.minecraft().font, value,
                context.x() + (state.renderData().width() - context.minecraft().font.width(value)) / 2,
                contentY(context), color, true);
    }

    private static void renderIcon(Context context, OriginsRenderData data) {
        ResourceBarRenderState state = context.state();
        boolean center = SaturnClientConfig.INSTANCE.resourceBars.iconLayout.getValue() == ResourceBarIconLayout.CENTER;
        int x = state.anchor() == Anchor.LEFT
                ? center ? context.x() + state.renderData().width() + 3 : context.x() - 11
                : center ? context.x() - 11 : context.x() + state.renderData().width() + 3;
        OriginsResourceBarRenderer.renderIconAt(data, context.graphics(), x, context.y() - 2);
    }

    private static void renderName(Context context, Component name) {
        ResourceBarRenderState state = context.state();
        String text = name.getString();
        if (text.isBlank()) return;
        boolean left = state.anchor() == Anchor.LEFT;
        boolean hasSideIcon = icon(state).isPresent()
                && SaturnClientConfig.INSTANCE.resourceBars.iconLayout.getValue() != ResourceBarIconLayout.CENTER;
        int x = left ? context.x() - 3 - context.minecraft().font.width(text)
                : context.x() + state.renderData().width() + 3;
        if (hasSideIcon) x += left ? -11 : 11;
        context.graphics().text(context.minecraft().font, name, x, contentY(context), 0xFFFFFFFF, true);
    }

    private static void renderValueDisplay(Context context) {
        ResourceBarRenderState state = context.state();
        String value = switch (state.valueDisplay()) {
            case NONE -> "";
            case CURRENT -> format(state.current());
            case CURRENT_AND_MAXIMUM -> format(state.current()) + " / " + format(state.maximum());
            case PERCENTAGE -> format(state.percentage() * 100.0D) + "%";
        };
        if (!value.isBlank()) context.graphics().text(context.minecraft().font, value,
                context.x() + (state.renderData().width() - context.minecraft().font.width(value)) / 2,
                contentY(context), 0xFFFFFFFF, true);
    }

    private static int contentY(Context context) {
        int barY = context.y();
        if (context.state().renderData() instanceof OriginsRenderData) barY -= 2;
        return barY + (context.state().renderData().height() - context.minecraft().font.lineHeight) / 2;
    }

    private static Optional<OriginsRenderData> icon(ResourceBarRenderState state) {
        return state.renderData() instanceof OriginsRenderData origins ? Optional.of(origins) : Optional.empty();
    }

    static String format(double value) {
        return Math.abs(value - Math.rint(value)) < 0.0001D ? Long.toString(Math.round(value))
                : String.format(Locale.ROOT, "%.1f", value);
    }
}
