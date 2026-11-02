package com.iafenvoy.saturn.view.model;

import com.mojang.serialization.Codec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import net.minecraft.resources.Identifier;

import java.util.List;
import java.util.function.Predicate;

/** A flat ordered row list preserves group metadata while allowing consumers to ignore grouping entirely. */
public record InformationDocument(List<InformationRow> rows) {
    public static final Codec<InformationDocument> CODEC = RecordCodecBuilder.create(i -> i.group(
            InformationRow.CODEC.listOf().fieldOf("rows").forGetter(InformationDocument::rows)
    ).apply(i, InformationDocument::new));

    public InformationDocument {
        rows = List.copyOf(rows);
    }

    public List<InformationRow> rows(InfomationGroup group) {
        return this.rows.stream().filter(row -> row.group().equals(group)).toList();
    }

    public List<InfomationGroup> groups() {
        return this.rows.stream().map(InformationRow::group).distinct().toList();
    }

    public InformationDocument filter(Predicate<Identifier> allowedField) {
        return new InformationDocument(this.rows.stream()
                .filter(row -> row.content().sourceFields().stream().allMatch(allowedField)).toList());
    }
}
