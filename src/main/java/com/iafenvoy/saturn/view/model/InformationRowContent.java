package com.iafenvoy.saturn.view.model;

import com.iafenvoy.saturn.util.codec.ExtraEnumCodecs;
import com.mojang.serialization.Codec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import net.minecraft.network.chat.Component;
import net.minecraft.network.chat.ComponentSerialization;
import net.minecraft.resources.Identifier;

import java.util.Objects;
import java.util.Optional;
import java.util.Set;

public record InformationRowContent(Component label, InformationValue value, Optional<Component> tooltip,
                                     Tone tone, Set<Identifier> sourceFields) {
    public static final Codec<InformationRowContent> CODEC = RecordCodecBuilder.create(i -> i.group(
            ComponentSerialization.CODEC.fieldOf("label").forGetter(InformationRowContent::label),
            InformationValue.CODEC.fieldOf("value").forGetter(InformationRowContent::value),
            ComponentSerialization.CODEC.optionalFieldOf("tooltip").forGetter(InformationRowContent::tooltip),
            Tone.CODEC.optionalFieldOf("tone", Tone.NORMAL).forGetter(InformationRowContent::tone),
            Identifier.CODEC.listOf().xmap(Set::copyOf, set -> set.stream().sorted().toList()).fieldOf("fields").forGetter(InformationRowContent::sourceFields)
    ).apply(i, InformationRowContent::new));

    public InformationRowContent {
        label = ViewComponents.copy(label);
        Objects.requireNonNull(value);
        tooltip = tooltip.map(ViewComponents::copy);
        Objects.requireNonNull(tone);
        sourceFields = Set.copyOf(sourceFields);
    }

    public InformationRowContent(Component label, InformationValue value, Set<Identifier> sourceFields) {
        this(label, value, Optional.empty(), Tone.NORMAL, sourceFields);
    }

    @Override
    public Component label() {
        return ViewComponents.copy(this.label);
    }

    @Override
    public Optional<Component> tooltip() {
        return this.tooltip.map(ViewComponents::copy);
    }

    public enum Tone {
        NORMAL, POSITIVE, WARNING;

        public static final Codec<Tone> CODEC = ExtraEnumCodecs.enumCodec(Tone.class);
    }
}
