package com.iafenvoy.saturn.data.information;

import com.iafenvoy.saturn.Saturn;
import com.iafenvoy.saturn.api.playerview.PlayerInformationRowProvider;
import com.iafenvoy.saturn.api.playerview.PlayerView;
import com.iafenvoy.saturn.data.information.expression.InformationEvaluation;
import com.iafenvoy.saturn.data.information.model.InfomationGroup;
import com.iafenvoy.saturn.data.information.model.InformationContext;
import com.iafenvoy.saturn.data.information.model.InformationDocument;
import com.iafenvoy.saturn.data.information.model.InformationRowResult;
import com.iafenvoy.saturn.registry.SaturnResourceKeys;
import com.iafenvoy.saturn.util.codec.RegistryCodecs;
import net.minecraft.core.Holder;
import net.minecraft.core.HolderLookup;
import net.minecraft.core.Registry;
import net.minecraft.network.chat.Component;
import net.minecraft.resources.Identifier;
import net.minecraft.resources.RegistryOps;
import net.minecraft.resources.ResourceKey;

import java.util.*;
import java.util.stream.Stream;

/**
 * Groups select independent rows. Code memberships are defaults, replaced by a world's group definitions.
 */
public final class PlayerInformationRegistry {
    private static final Comparator<InfomationGroup> GROUP_ORDER = Comparator.comparingInt(InfomationGroup::order).thenComparing(InfomationGroup::id);
    private static final Comparator<SelectedRow> ORDER = Comparator
            .comparing((SelectedRow row) -> row.groups.stream().min(GROUP_ORDER).orElse(null), Comparator.nullsLast(GROUP_ORDER))
            .thenComparingInt(row -> row.row.order).thenComparing(row -> row.row.id);
    private final Map<Identifier, InfomationGroup> groups = new LinkedHashMap<>();
    private final Map<Identifier, RegisteredRow> rows = new LinkedHashMap<>();
    private final Map<Identifier, Set<Identifier>> groupRows = new LinkedHashMap<>();

    public synchronized InfomationGroup registerGroup(Identifier id, Component title, int order) {
        InfomationGroup group = new InfomationGroup(id, title, order);
        if (this.groups.putIfAbsent(id, group) != null)
            throw new IllegalArgumentException("Duplicate information group: " + id);
        this.groupRows.put(id, new LinkedHashSet<>());
        return group;
    }

    public void register(Identifier id, int order, int importance, PlayerInformationRowProvider provider) {
        this.register(id, List.of(), order, importance, provider);
    }

    public void register(Identifier id, InfomationGroup group, int order, PlayerInformationRowProvider provider) {
        this.register(id, group, order, 0, provider);
    }

    public void register(Identifier id, InfomationGroup group, int order, int importance, PlayerInformationRowProvider provider) {
        this.register(id, List.of(group), order, importance, provider);
    }

    public void register(Identifier id, List<InfomationGroup> groups, int order, PlayerInformationRowProvider provider) {
        this.register(id, groups, order, 0, provider);
    }

    /**
     * Compatibility helper: registers the row, then adds its id to the named code groups' default selections.
     */
    public synchronized void register(Identifier id, List<InfomationGroup> groups, int order, int importance, PlayerInformationRowProvider provider) {
        for (InfomationGroup group : groups)
            if (!this.groups.containsKey(group.id()))
                throw new IllegalArgumentException("Unregistered information group: " + group.id());
        RegisteredRow row = new RegisteredRow(Objects.requireNonNull(id), order, importance, Objects.requireNonNull(provider));
        if (this.rows.putIfAbsent(id, row) != null)
            throw new IllegalArgumentException("Duplicate information row: " + id);
        groups.forEach(group -> this.groupRows.get(group.id()).add(id));
    }

    public synchronized List<InfomationGroup> groups() {
        return this.groups.values().stream().sorted(GROUP_ORDER).toList();
    }

    public List<InfomationGroup> groups(PlayerView view, InformationContext context) {
        return this.resolvedGroups(view, context).values().stream().sorted(GROUP_ORDER).toList();
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
        Map<Identifier, InfomationGroup> groups = this.resolvedGroups(view, context);
        Map<Identifier, RegisteredRow> rows;
        Map<Identifier, Set<Identifier>> selections = new LinkedHashMap<>();
        synchronized (this) {
            rows = new LinkedHashMap<>(this.rows);
            this.groupRows.forEach((id, values) -> selections.put(id, new LinkedHashSet<>(values)));
        }
        InformationEvaluation evaluation = new InformationEvaluation(view, context);
        Optional<HolderLookup.RegistryLookup<InformationRow>> rowRegistry = lookup(view, context, SaturnResourceKeys.INFORMATION_ROW);
        rowRegistry.stream().flatMap(HolderLookup::listElements).forEach(holder -> {
            Identifier id = holder.key().identifier();
            InformationRow definition = holder.value();
            rows.put(id, new RegisteredRow(id, definition.order(), definition.importance(), (v, c) -> definition.collect(evaluation.forRow())));
        });
        definitions(view, context, SaturnResourceKeys.INFORMATION_GROUP).forEach(holder -> {
            Set<Identifier> members = new LinkedHashSet<>();
            rowRegistry.ifPresent(registry -> RegistryCodecs.resolve(holder.value().rows(), registry).forEach(row ->
                    row.unwrapKey().ifPresent(key -> members.add(key.identifier()))));
            selections.put(holder.key().identifier(), members);
        });
        Map<Identifier, List<InfomationGroup>> memberships = new HashMap<>();
        selections.forEach((groupId, members) -> members.forEach(rowId ->
                memberships.computeIfAbsent(rowId, key -> new ArrayList<>()).add(groups.get(groupId))));
        Comparator<SelectedRow> order = selected.isPresent() && selected.get().size() == 1
                ? Comparator.comparingInt((SelectedRow row) -> row.row.order).thenComparing(row -> row.row.id) : ORDER;
        List<InformationRowResult> result = new ArrayList<>();
        rows.values().stream().map(row -> new SelectedRow(row, memberships.getOrDefault(row.id, List.of()).stream().sorted(GROUP_ORDER).toList()))
                .filter(row -> selected.isEmpty() || row.groups.stream().anyMatch(selected.get()::contains)).sorted(order).forEach(selection -> {
                    RegisteredRow row = selection.row;
                    try {
                        row.provider.collect(view, context).ifPresent(content -> result.add(new InformationRowResult(row.id, selection.groups, row.order, row.importance, content)));
                    } catch (RuntimeException failure) {
                        Saturn.LOGGER.warn("Failed to collect player information row {}", row.id, failure);
                    }
                });
        return new InformationDocument(result);
    }

    private Map<Identifier, InfomationGroup> resolvedGroups(PlayerView view, InformationContext context) {
        Map<Identifier, InfomationGroup> result;
        synchronized (this) {
            result = new LinkedHashMap<>(this.groups);
        }
        definitions(view, context, SaturnResourceKeys.INFORMATION_GROUP).forEach(holder ->
                result.put(holder.key().identifier(), holder.value().resolve(holder.key().identifier())));
        return result;
    }

    private static <T> Optional<HolderLookup.RegistryLookup<T>> lookup(PlayerView view, InformationContext context, ResourceKey<Registry<T>> key) {
        if (context.ops(view) instanceof RegistryOps<?> ops && ops.owner(key).orElse(null) instanceof HolderLookup.RegistryLookup<?> lookup) {
            @SuppressWarnings("unchecked") HolderLookup.RegistryLookup<T> typed = (HolderLookup.RegistryLookup<T>) lookup;
            return Optional.of(typed);
        }
        return Optional.empty();
    }

    private static <T> Stream<Holder.Reference<T>> definitions(PlayerView view, InformationContext context, ResourceKey<Registry<T>> key) {
        return lookup(view, context, key).stream().flatMap(HolderLookup::listElements);
    }

    private record RegisteredRow(Identifier id, int order, int importance, PlayerInformationRowProvider provider) {
    }

    private record SelectedRow(RegisteredRow row, List<InfomationGroup> groups) {
    }
}
