package com.iafenvoy.saturn.data.information.model;

import com.mojang.serialization.Codec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import net.minecraft.resources.Identifier;

import java.util.Comparator;
import java.util.List;
import java.util.function.Predicate;

/**
 * A flat ordered row list preserves group metadata while allowing consumers to ignore grouping entirely.
 */
public record InformationDocument(List<InformationRowResult> rows) {
    public static final Codec<InformationDocument> CODEC = RecordCodecBuilder.create(i -> i.group(
            InformationRowResult.CODEC.listOf().fieldOf("rows").forGetter(InformationDocument::rows)
    ).apply(i, InformationDocument::new));

    public InformationDocument {
        rows = List.copyOf(rows);
    }

    public List<InformationRowResult> rows(InfomationGroup group) {
        return this.rows.stream().filter(row -> row.groups().contains(group))
                .sorted(Comparator.comparingInt(InformationRowResult::order).thenComparing(InformationRowResult::id)).toList();
    }

    public List<InfomationGroup> groups() {
        return this.rows.stream().flatMap(row -> row.groups().stream()).distinct()
                .sorted(Comparator.comparingInt(InfomationGroup::order).thenComparing(InfomationGroup::id)).toList();
    }

    /**
     * Keeps rows at or above the threshold in their original display order.
     */
    public InformationDocument withMinimumImportance(int minimum) {
        return new InformationDocument(this.rows.stream().filter(row -> row.importance() >= minimum).toList());
    }

    public InformationDocument filter(Predicate<Identifier> allowedField) {
        return new InformationDocument(this.rows.stream()
                .filter(row -> row.content().sourceFields().stream().allMatch(allowedField)).toList());
    }
}
