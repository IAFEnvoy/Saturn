package com.iafenvoy.saturn.data.information.model;

import com.mojang.serialization.Codec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import net.minecraft.network.chat.Component;
import net.minecraft.network.chat.ComponentSerialization;
import net.minecraft.resources.Identifier;

import java.util.Objects;

/**
 * Group identity is its id, including after a document crosses a codec or language boundary.
 */
public record InfomationGroup(Identifier id, Component title, int order) {
    public static final Codec<InfomationGroup> CODEC = RecordCodecBuilder.create(i -> i.group(
            Identifier.CODEC.fieldOf("id").forGetter(InfomationGroup::id),
            ComponentSerialization.CODEC.fieldOf("title").forGetter(InfomationGroup::title),
            Codec.INT.fieldOf("order").forGetter(InfomationGroup::order)
    ).apply(i, InfomationGroup::new));

    public InfomationGroup {
        Objects.requireNonNull(id);
        title = ViewComponents.copy(title);
    }

    @Override
    public Component title() {
        return ViewComponents.copy(this.title);
    }

    @Override
    public boolean equals(Object other) {
        return other instanceof InfomationGroup group && this.id.equals(group.id);
    }

    @Override
    public int hashCode() {
        return this.id.hashCode();
    }
}
