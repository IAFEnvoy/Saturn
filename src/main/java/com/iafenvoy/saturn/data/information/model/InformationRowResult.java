package com.iafenvoy.saturn.data.information.model;

import com.mojang.serialization.Codec;
import com.mojang.serialization.DataResult;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import net.minecraft.resources.Identifier;

import java.util.List;
import java.util.Objects;
import java.util.Optional;

public record InformationRowResult(Identifier id, List<InfomationGroup> groups, int order, int importance,
                                   InformationRowContent content) {
    private static final Codec<List<InfomationGroup>> GROUPS = InfomationGroup.CODEC.listOf();
    public static final Codec<InformationRowResult> CODEC = RecordCodecBuilder.<Wire>create(i -> i.group(
            Identifier.CODEC.fieldOf("id").forGetter(Wire::id),
            GROUPS.optionalFieldOf("groups").forGetter(Wire::groups),
            InfomationGroup.CODEC.optionalFieldOf("group").forGetter(Wire::legacyGroup),
            Codec.INT.fieldOf("order").forGetter(Wire::order),
            Codec.INT.optionalFieldOf("importance", 0).forGetter(Wire::importance),
            InformationRowContent.CODEC.fieldOf("content").forGetter(Wire::content)
    ).apply(i, Wire::new)).comapFlatMap(wire -> wire.groups.or(() -> wire.legacyGroup.map(List::of))
                    .map(groups -> DataResult.success(new InformationRowResult(wire.id, groups, wire.order, wire.importance, wire.content)))
                    .orElseGet(() -> DataResult.error(() -> "Missing information row groups")),
            row -> new Wire(row.id, Optional.of(row.groups), Optional.empty(), row.order, row.importance, row.content));

    public InformationRowResult {
        Objects.requireNonNull(id);
        groups = groups.stream().distinct().toList();
        Objects.requireNonNull(content);
    }

    public InformationRowResult(Identifier id, InfomationGroup group, int order, int importance, InformationRowContent content) {
        this(id, List.of(group), order, importance, content);
    }

    public InformationRowResult(Identifier id, InfomationGroup group, int order, InformationRowContent content) {
        this(id, group, order, 0, content);
    }

    public InformationRowResult(Identifier id, List<InfomationGroup> groups, int order, InformationRowContent content) {
        this(id, groups, order, 0, content);
    }

    /**
     * Compatibility accessor for grouped rows. All collection can also return rows with no groups.
     */
    @Deprecated
    public InfomationGroup group() {
        return this.groups.getFirst();
    }

    private record Wire(Identifier id, Optional<List<InfomationGroup>> groups, Optional<InfomationGroup> legacyGroup,
                        int order, int importance, InformationRowContent content) {
    }
}
