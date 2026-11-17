package com.iafenvoy.saturn.screen.wheel.content;

import com.iafenvoy.saturn.api.wheel.WheelMenuEntry;
import com.iafenvoy.saturn.registry.SaturnKeyMappings;
import com.iafenvoy.saturn.render.IconRenderer;
import com.iafenvoy.saturn.runtime.wheel.WheelLayout;
import com.iafenvoy.saturn.runtime.wheel.WheelSlot;
import com.iafenvoy.saturn.runtime.wheel.WheelSourceTypes;
import com.iafenvoy.saturn.screen.wheel.WheelConfigurationLayout;
import net.minecraft.client.KeyMapping;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.GuiGraphicsExtractor;
import net.minecraft.client.gui.components.Button;
import net.minecraft.client.gui.screens.Screen;
import net.minecraft.client.input.MouseButtonEvent;
import net.minecraft.client.player.LocalPlayer;
import net.minecraft.network.chat.CommonComponents;
import net.minecraft.network.chat.Component;
import net.minecraft.util.Mth;
import org.jspecify.annotations.NonNull;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

public final class WheelConfigurationScreen extends Screen {
    private static final int CELL = WheelConfigurationLayout.CELL, STEP = WheelConfigurationLayout.STEP;
    private final List<WheelMenuEntry> options;
    private final Map<WheelSlot, WheelMenuEntry> available = new HashMap<>();
    private WheelLayout draft;
    private int selected = -1, scroll;
    private WheelConfigurationLayout layout;
    private boolean draggingScroll;
    private boolean saved;

    public WheelConfigurationScreen() {
        super(Component.translatable("screen.saturn.wheel_configuration"));
        LocalPlayer player = Minecraft.getInstance().player;
        this.options = WheelContent.options(player);
        this.draft = WheelContent.layoutFor(player);
        for (WheelMenuEntry entry : WheelContent.entries(player, WheelSourceTypes.CONFIGURED))
            if (entry != null) this.available.put(WheelSlot.of(entry.kind(), entry.id()), entry);
        for (WheelMenuEntry entry : this.options) this.available.put(WheelSlot.of(entry.kind(), entry.id()), entry);
    }

    public static boolean open() {
        Minecraft minecraft = Minecraft.getInstance();
        if (minecraft.player == null) return false;
        minecraft.setScreen(new WheelConfigurationScreen());
        return true;
    }

    @Override
    protected void init() {
        this.layout = WheelConfigurationLayout.of(this.width, this.height);
        this.clampScroll();
        this.addRenderableWidget(Button.builder(CommonComponents.GUI_DONE, _ -> this.onClose())
                .bounds(this.width / 2 - 40, this.height - 26, 80, 20).build());
    }

    @Override
    public void extractRenderState(@NonNull GuiGraphicsExtractor graphics, int mouseX, int mouseY, float partialTick) {
        super.extractRenderState(graphics, mouseX, mouseY, partialTick);
        IconRenderer.renderName(graphics, this.font, this.title, this.width / 2, 12, this.width - 12);
        if (!this.layout.compact())
            IconRenderer.renderName(graphics, this.font, Component.translatable("wheel.saturn.configuration_hint"), this.width / 2, 28, this.width - 12);
        graphics.enableScissor(this.layout.left(), this.layout.top(), this.layout.left() + this.layout.columns() * STEP, this.layout.bottom());
        for (int i = 0; i < this.options.size(); i++) {
            int y = this.layout.top() + i / this.layout.columns() * STEP - this.scroll;
            if (y + CELL <= this.layout.top() || y >= this.layout.bottom()) continue;
            this.cell(graphics, this.options.get(i), this.layout.left() + i % this.layout.columns() * STEP, y, i == this.selected, false);
        }
        graphics.disableScissor();
        int max = this.maximumScroll(), band = this.layout.bottom() - this.layout.top();
        if (max > 0) {
            int x = this.layout.left() + this.layout.columns() * STEP + 2;
            int thumb = Math.max(10, band * band / (max + band));
            int y = this.layout.top() + (band - thumb) * this.scroll / max;
            graphics.fill(x, this.layout.top(), x + 3, this.layout.bottom(), 0x80303A48);
            graphics.fill(x, y, x + 3, y + thumb, 0xFFB0B8C8);
        }
        if (this.options.isEmpty())
            IconRenderer.renderName(graphics, this.font, Component.translatable("wheel.saturn.pool.empty"), this.width / 2, this.layout.top() + 12, this.width - 16);
        for (int i = 0; i < WheelLayout.SLOTS; i++) {
            int x = this.layout.left() + i % this.layout.columns() * STEP, y = this.layout.slotY(i);
            WheelSlot slot = this.draft.slot(i);
            this.cell(graphics, this.available.get(slot), x, y, false, !slot.isEmpty() && !this.available.containsKey(slot));
            graphics.text(this.font, Integer.toString(i + 1), x + 1, y + 1, 0xFFB0B8C8, true);
            KeyMapping key = SaturnKeyMappings.WHEEL_SLOTS.get(i).get();
            if (!this.layout.compact() && !key.isUnbound())
                IconRenderer.renderName(graphics, this.font, key.getTranslatedKeyMessage(), x + CELL / 2, y + CELL + 6, CELL);
        }
        int index = this.optionAt(mouseX, mouseY);
        WheelMenuEntry hovered = index >= 0 ? this.options.get(index) : null;
        int slot = this.slotAt(mouseX, mouseY);
        if (slot >= 0) {
            WheelSlot value = this.draft.slot(slot);
            hovered = this.available.get(value);
            if (hovered == null && !value.isEmpty()) graphics.setComponentTooltipForNextFrame(this.font,
                    List.of(Component.translatable("wheel.saturn.tooltip.stale", value.id().toString())), mouseX, mouseY);
        }
        if (hovered != null)
            graphics.setComponentTooltipForNextFrame(this.font, hovered.tooltip(this.minecraft.player), mouseX, mouseY);
    }

    private void cell(GuiGraphicsExtractor graphics, WheelMenuEntry entry, int x, int y, boolean selected, boolean stale) {
        graphics.fill(x, y, x + CELL, y + CELL, 0xD018202C);
        graphics.outline(x, y, CELL, CELL, selected ? 0xFFFFD24A : stale ? 0xFFE07070 : 0xFF586278);
        if (entry == null) return;
        IconRenderer.renderOrName(graphics, this.font, entry.icon(), entry.title(), x, y, CELL);
        graphics.fill(x + 1, y + CELL - 3, x + CELL - 1, y + CELL - 1, entry.accentColor());
    }

    private int maximumScroll() {
        return this.layout.maximumScroll(this.options.size());
    }

    private void clampScroll() {
        this.scroll = Mth.clamp(this.scroll, 0, this.maximumScroll());
    }

    private int optionAt(double x, double y) {
        return this.layout.optionAt(x, y, this.scroll, this.options.size());
    }

    private int slotAt(double x, double y) {
        return this.layout.slotAt(x, y);
    }

    private void scrollTo(double y) {
        int band = this.layout.bottom() - this.layout.top();
        int max = this.maximumScroll(), thumb = Math.max(10, band * band / (max + band));
        this.scroll = (int) Math.round((y - this.layout.top() - thumb / 2.0) * max / Math.max(1, band - thumb));
        this.clampScroll();
    }

    @Override
    public boolean mouseClicked(@NonNull MouseButtonEvent event, boolean doubleClick) {
        if (super.mouseClicked(event, doubleClick)) return true;
        int barX = this.layout.left() + this.layout.columns() * STEP + 2;
        if (event.button() == 0 && this.maximumScroll() > 0 && event.x() >= barX - 1 && event.x() < barX + 4
                && event.y() >= this.layout.top() && event.y() < this.layout.bottom()) {
            this.draggingScroll = true;
            this.scrollTo(event.y());
            return true;
        }
        int option = this.optionAt(event.x(), event.y());
        if (option >= 0 && event.button() == 0) {
            this.selected = this.selected == option ? -1 : option;
            return true;
        }
        int slot = this.slotAt(event.x(), event.y());
        if (slot < 0 || event.button() > 1) return false;
        WheelMenuEntry entry = this.selected < 0 || event.button() == 1 ? null : this.options.get(this.selected);
        this.draft = this.draft.with(slot, entry == null ? WheelSlot.EMPTY : WheelSlot.of(entry.kind(), entry.id()));
        return true;
    }

    @Override
    public boolean mouseDragged(@NonNull MouseButtonEvent event, double dx, double dy) {
        if (!this.draggingScroll) return super.mouseDragged(event, dx, dy);
        this.scrollTo(event.y());
        return true;
    }

    @Override
    public boolean mouseReleased(@NonNull MouseButtonEvent event) {
        this.draggingScroll = false;
        return super.mouseReleased(event);
    }

    @Override
    public boolean mouseScrolled(double x, double y, double dx, double dy) {
        if (y < this.layout.top() || y >= this.layout.bottom()) return false;
        this.scroll -= (int) Math.round(dy * STEP * 2);
        this.clampScroll();
        return true;
    }

    @Override
    public void removed() {
        if (!this.saved && this.minecraft.player != null && this.minecraft.getConnection() != null) {
            this.saved = true;
            WheelContent.save(this.draft);
        }
    }

    @Override
    public boolean isPauseScreen() {
        return false;
    }

    @Override
    public boolean isInGameUi() {
        return true;
    }
}
