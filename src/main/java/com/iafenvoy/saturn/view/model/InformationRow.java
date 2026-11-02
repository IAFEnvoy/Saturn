package com.iafenvoy.saturn.view.model;

import com.mojang.serialization.Codec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import net.minecraft.resources.Identifier;

public record InformationRow(Identifier id, InfomationGroup group, int order, InformationRowContent content) {
    public static final Codec<InformationRow> CODEC = RecordCodecBuilder.create(i -> i.group(
            Identifier.CODEC.fieldOf("id").forGetter(InformationRow::id),
            InfomationGroup.CODEC.fieldOf("group").forGetter(InformationRow::group),
            Codec.INT.fieldOf("order").forGetter(InformationRow::order),
            InformationRowContent.CODEC.fieldOf("content").forGetter(InformationRow::content)
    ).apply(i, InformationRow::new));
}
