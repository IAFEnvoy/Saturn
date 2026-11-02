package com.iafenvoy.saturn.view;

import com.iafenvoy.saturn.Saturn;
import com.iafenvoy.saturn.api.playerview.PlayerInformationRowProvider;
import com.iafenvoy.saturn.api.playerview.PlayerView;
import com.iafenvoy.saturn.view.model.*;
import net.minecraft.network.chat.Component;
import net.minecraft.resources.Identifier;

import java.util.*;

public final class PlayerInformationRegistry {
    private static final Comparator<RegisteredRow> ORDER = Comparator
            .comparingInt((RegisteredRow row) -> row.group.order()).thenComparing(row -> row.group.id())
            .thenComparingInt(RegisteredRow::order).thenComparing(RegisteredRow::id);
    private final Map<Identifier, InfomationGroup> groups = new LinkedHashMap<>();
    private final Map<Identifier, RegisteredRow> rows = new LinkedHashMap<>();

    public synchronized InfomationGroup registerGroup(Identifier id, Component title, int order) {
        InfomationGroup group = new InfomationGroup(id, title, order);
        if (this.groups.putIfAbsent(id, group) != null) throw new IllegalArgumentException("Duplicate information group: " + id);
        return group;
    }

    public synchronized void register(Identifier id, InfomationGroup group, int order, PlayerInformationRowProvider provider) {
        InfomationGroup registered = this.groups.get(group.id());
        if (registered == null) throw new IllegalArgumentException("Unregistered information group: " + group.id());
        RegisteredRow row = new RegisteredRow(Objects.requireNonNull(id), registered, order, Objects.requireNonNull(provider));
        if (this.rows.putIfAbsent(id, row) != null) throw new IllegalArgumentException("Duplicate information row: " + id);
    }

    public synchronized List<InfomationGroup> groups() {
        return this.groups.values().stream().sorted(Comparator.comparingInt(InfomationGroup::order).thenComparing(InfomationGroup::id)).toList();
    }

    public InformationDocument collectAll(PlayerView view, InformationContext context) {
        return this.collectSelected(view, context, Optional.empty());
    }

    public InformationDocument collect(PlayerView view, InformationContext context, InfomationGroup group) {
        return this.collect(view, context, Set.of(group));
    }

    public InformationDocument collect(PlayerView view, InformationContext context, Set<InfomationGroup> groups) {
        return this.collectSelected(view, context, Optional.of(Set.copyOf(groups)));
    }

    private InformationDocument collectSelected(PlayerView view, InformationContext context, Optional<Set<InfomationGroup>> selected) {
        List<RegisteredRow> registrations;
        synchronized (this) {
            registrations = this.rows.values().stream()
                    .filter(row -> selected.isEmpty() || selected.get().contains(row.group)).sorted(ORDER).toList();
        }
        List<InformationRow> result = new ArrayList<>();
        for (RegisteredRow row : registrations) {
            try {
                row.provider.collect(view, context).ifPresent(content -> result.add(new InformationRow(row.id, row.group, row.order, content)));
            } catch (RuntimeException failure) {
                Saturn.LOGGER.warn("Failed to collect player information row {}", row.id, failure);
            }
        }
        return new InformationDocument(result);
    }

    private record RegisteredRow(Identifier id, InfomationGroup group, int order, PlayerInformationRowProvider provider) {
    }
}
