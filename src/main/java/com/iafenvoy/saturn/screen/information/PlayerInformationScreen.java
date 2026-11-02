package com.iafenvoy.saturn.screen.information;

import com.iafenvoy.saturn.data.information.InformationGroup;
import com.iafenvoy.saturn.data.information.InformationText;
import com.iafenvoy.saturn.data.information.model.InformationDocument;
import com.iafenvoy.saturn.data.information.model.InformationEntry;
import net.minecraft.client.gui.GuiGraphicsExtractor;
import net.minecraft.client.gui.components.Button;
import net.minecraft.client.gui.screens.Screen;
import net.minecraft.core.Holder;
import net.minecraft.network.chat.CommonComponents;
import net.minecraft.network.chat.Component;
import net.minecraft.util.FormattedCharSequence;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.Set;

/**
 * Renders caller-supplied rows, without resolving a player, requesting data or writing a save.
 */
public final class PlayerInformationScreen extends Screen {
    private final @Nullable Screen parent;
    private final InformationDocument document;
    private final Set<Holder<InformationGroup>> selectedGroups;
    private final List<Line> lines = new ArrayList<>();
    private int scroll;

    public PlayerInformationScreen(@Nullable Screen parent, InformationDocument document) {
        this(parent, document, Set.of());
    }

    /**
     * An empty selection shows all groups and ungrouped rows.
     */
    public PlayerInformationScreen(@Nullable Screen parent, InformationDocument document, Set<Holder<InformationGroup>> groups) {
        super(Component.translatable("screen.saturn.player_information"));
        this.parent = parent;
        this.document = document;
        this.selectedGroups = Set.copyOf(groups);
    }

    @Override
    protected void init() {
        this.lines.clear();
        for (Holder<InformationGroup> group : this.document.groups()) {
            if (!this.selectedGroups.isEmpty() && !this.selectedGroups.contains(group)) continue;
            this.addLines(group.value().title(), Optional.empty(), 0xFFFFD24A);
            for (InformationEntry row : this.document.rows(group)) this.addRow(row);
        }
        if (this.selectedGroups.isEmpty()) {
            List<InformationEntry> ungrouped = this.document.rows().stream().filter(row -> row.groups().isEmpty()).toList();
            if (!ungrouped.isEmpty())
                this.addLines(Component.translatable("info.saturn.group.ungrouped"), Optional.empty(), 0xFFFFD24A);
            ungrouped.forEach(this::addRow);
        }
        if (this.lines.isEmpty())
            this.addLines(Component.translatable("info.saturn.empty"), Optional.empty(), 0xFFE0E4EC);
        this.scroll = Math.clamp(this.scroll, 0, this.maximumScroll());
        this.addRenderableWidget(Button.builder(CommonComponents.GUI_DONE, button -> this.onClose())
                .bounds(this.width / 2 - 60, this.height - 28, 120, 20).build());
    }

    private void addRow(InformationEntry row) {
        int color = switch (row.tone()) {
            case NORMAL -> 0xFFE0E4EC;
            case POSITIVE -> 0xFF55FF55;
            case WARNING -> 0xFFFFAA00;
        };
        for (Component text : InformationText.lines(row)) this.addLines(text, row.tooltip(), color);
    }

    private void addLines(Component text, Optional<Component> tooltip, int color) {
        for (FormattedCharSequence line : this.font.split(text, Math.max(20, this.width - 48)))
            this.lines.add(new Line(line, tooltip, color));
    }

    @Override
    public void extractRenderState(@NotNull GuiGraphicsExtractor graphics, int mouseX, int mouseY, float partialTick) {
        graphics.fill(12, 28, this.width - 12, this.height - 36, 0xCC10151D);
        graphics.centeredText(this.font, this.title, this.width / 2, 12, 0xFFFFFFFF);
        graphics.enableScissor(16, 32, this.width - 16, this.height - 40);
        for (int index = 0; index < this.lines.size(); index++) {
            int y = 34 + index * 13 - this.scroll;
            if (y + 13 < 32 || y > this.height - 40) continue;
            Line line = this.lines.get(index);
            graphics.text(this.font, line.text, 24, y, line.color);
            if (mouseX >= 16 && mouseX < this.width - 16 && mouseY >= Math.max(32, y) && mouseY < Math.min(this.height - 40, y + 13))
                line.tooltip.ifPresent(tooltip -> graphics.setTooltipForNextFrame(this.font, tooltip, mouseX, mouseY));
        }
        graphics.disableScissor();
        super.extractRenderState(graphics, mouseX, mouseY, partialTick);
    }

    @Override
    public boolean mouseScrolled(double x, double y, double scrollX, double scrollY) {
        this.scroll = Math.clamp(this.scroll - (int) Math.round(scrollY * 26), 0, this.maximumScroll());
        return true;
    }

    private int maximumScroll() {
        return Math.max(0, this.lines.size() * 13 - Math.max(0, this.height - 76));
    }

    @Override
    public void onClose() {
        this.minecraft.setScreen(this.parent);
    }

    @Override
    public boolean isPauseScreen() {
        return false;
    }

    private record Line(FormattedCharSequence text, Optional<Component> tooltip, int color) {
    }
}
