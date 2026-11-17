package com.iafenvoy.saturn.screen.wheel;

import com.iafenvoy.saturn.api.wheel.WheelMenuEntry;
import com.iafenvoy.saturn.registry.SaturnKeyMappings;
import com.iafenvoy.saturn.render.IconRenderer;
import com.iafenvoy.saturn.runtime.wheel.WheelSelection;
import net.minecraft.client.gui.GuiGraphicsExtractor;
import net.minecraft.client.gui.screens.Screen;
import net.minecraft.client.input.MouseButtonEvent;
import net.minecraft.network.chat.Component;
import net.minecraft.util.Mth;
import org.jspecify.annotations.NonNull;
import org.jspecify.annotations.Nullable;

import java.util.ArrayList;
import java.util.List;

public final class WheelMenuScreen extends Screen {
    private record Span(int x, int y, int width, int sector) {
    }

    private final List<Span> spans = new ArrayList<>();
    private WheelGeometry.Ring ring;

    public WheelMenuScreen() {
        super(Component.translatable("screen.saturn.wheel"));
    }

    @Override
    protected void init() {
        this.ring = WheelGeometry.ring(this.width, this.height, 1);
        this.spans.clear();
        int radius = (int) Math.ceil(this.ring.outerRadius());
        for (int y = -radius; y <= radius; y++) {
            int start = -radius, last = -1;
            for (int x = -radius; x <= radius + 1; x++) {
                double r = Math.hypot(x + .5, y + .5);
                double angle = Math.toDegrees(Math.atan2(y + .5, x + .5));
                int sector = WheelGeometry.sectorAt(angle);
                double delta = Math.abs(Math.IEEEremainder(angle - WheelGeometry.sectorCentre(sector), 360));
                int current = x <= radius && r >= this.ring.innerRadius() && r <= this.ring.outerRadius()
                        && delta < WheelGeometry.SECTOR_DEGREES / 2 - .7 ? sector : -1;
                if (current == last) continue;
                if (last >= 0) this.spans.add(new Span(start, y, x - start, last));
                start = x;
                last = current;
            }
        }
    }

    public int pointedSector() {
        return WheelGeometry.sectorAt(WheelGeometry.pointerAngle());
    }

    @Nullable WheelSelection selection(WheelSelection.Method method) {
        int number = WheelSelectionState.numberAt(this.pointedSector());
        WheelMenuEntry entry = WheelMenuContent.entry(WheelSelectionState.pages(), number);
        return entry == null ? null : new WheelSelection(WheelMenuContent.source(WheelSelectionState.pages(), number), number, entry, method);
    }

    @Override
    public void extractRenderState(@NonNull GuiGraphicsExtractor graphics, int mouseX, int mouseY, float partialTick) {
        int pointed = this.pointedSector();
        List<WheelMenuEntry> entries = WheelMenuContent.sectors(WheelSelectionState.pages(), WheelSelectionState.page());
        int shown = WheelSelectionState.pages().isEmpty() ? 0 : WheelSelectionState.pages().get(WheelSelectionState.page()).shown();
        for (Span span : this.spans) {
            if (span.sector >= shown) continue;
            int color = entries.get(span.sector) == null ? 0x90202530 : span.sector == pointed ? 0xE05C5032 : 0xC028303E;
            graphics.fill(this.ring.centreX() + span.x, this.ring.centreY() + span.y,
                    this.ring.centreX() + span.x + span.width, this.ring.centreY() + span.y + 1, color);
        }
        for (int i = 0; i < entries.size(); i++) {
            WheelMenuEntry entry = entries.get(i);
            if (entry == null) continue;
            int x = (int) Math.round(this.ring.x(WheelGeometry.sectorCentre(i), this.ring.iconRadius())) - 11;
            int y = (int) Math.round(this.ring.y(WheelGeometry.sectorCentre(i), this.ring.iconRadius())) - 11;
            IconRenderer.renderOrName(graphics, this.font, entry.icon(), entry.title(), x, y, 22);
            graphics.fill(x + 2, y + 20, x + 20, y + 22, entry.accentColor());
            long remaining = entry.cooldownTicks(this.minecraft.player);
            if (remaining > 0) {
                long length = entry.cooldownLength(this.minecraft.player);
                float fraction = length <= 0 ? 1 : Mth.clamp((remaining - partialTick) / length, 0, 1);
                graphics.fill(x + 3, y + 3 + Mth.floor(16 * (1 - fraction)), x + 19, y + 19, 0x80FFFFFF);
            } else if (!entry.usable(this.minecraft.player)) graphics.fill(x + 2, y + 2, x + 20, y + 20, 0x903A2A20);
        }
        WheelMenuEntry entry = entries.get(pointed);
        if (entry != null) {
            IconRenderer.renderName(graphics, this.font, entry.title(), this.width / 2, this.height / 2 - 7, (int) (this.ring.innerRadius() * 1.8));
            List<Component> lines = entry.tooltip(this.minecraft.player);
            if (!lines.isEmpty()) graphics.setComponentTooltipForNextFrame(this.font, lines, mouseX, mouseY);
        }
        Component use = Component.translatable("wheel.saturn.use_hint", SaturnKeyMappings.WHEEL_USE.get().getTranslatedKeyMessage());
        IconRenderer.renderName(graphics, this.font, use, this.width / 2, this.height / 2 + 9, (int) (this.ring.innerRadius() * 1.8));
        Component page = Component.translatable("wheel.saturn.page_hint", WheelSelectionState.page() + 1,
                WheelSelectionState.pages().size(), WheelSelectionState.pageSource().displayName(),
                SaturnKeyMappings.WHEEL_PREVIOUS.get().getTranslatedKeyMessage(), SaturnKeyMappings.WHEEL_NEXT.get().getTranslatedKeyMessage());
        IconRenderer.renderName(graphics, this.font, page, this.width / 2,
                this.height / 2 + (int) this.ring.outerRadius() + 12, this.width - 12);
    }

    @Override
    public boolean mouseClicked(MouseButtonEvent event, boolean doubleClick) {
        if (event.button() != 0) return false;
        WheelMenuController.usePointed(WheelSelection.Method.CLICK);
        return true;
    }

    @Override
    public boolean mouseScrolled(double x, double y, double dx, double dy) {
        if (!WheelMenuController.scrollTurnsPages() || dy == 0) return false;
        WheelMenuController.stepPage(dy > 0 ? -1 : 1);
        return true;
    }

    @Override
    public boolean isPauseScreen() {
        return false;
    }

    @Override
    public boolean isInGameUi() {
        return true;
    }

    @Override
    public void extractBackground(@NonNull GuiGraphicsExtractor graphics, int x, int y, float partialTick) {
    }

    @Override
    public void removed() {
        WheelMenuController.screenRemoved(this);
    }
}
