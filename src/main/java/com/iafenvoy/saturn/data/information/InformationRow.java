package com.iafenvoy.saturn.data.information;

import com.iafenvoy.saturn.data.information.expression.ExpressionValue;
import com.iafenvoy.saturn.data.information.expression.InformationEvaluation;
import com.iafenvoy.saturn.data.information.expression.InformationExpression;
import com.iafenvoy.saturn.data.information.getter.EntityValueGetter;
import com.iafenvoy.saturn.data.information.model.InformationRowContent;
import com.iafenvoy.saturn.data.information.model.InformationValue;
import com.iafenvoy.saturn.util.codec.ExtraEnumCodecs;
import com.mojang.serialization.Codec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import net.minecraft.network.chat.Component;
import net.minecraft.network.chat.ComponentSerialization;
import net.minecraft.resources.Identifier;

import java.util.HashSet;
import java.util.Optional;
import java.util.Set;

/**
 * An executable recipe, never a serialized player snapshot. The registry supplies its row id.
 */
public record InformationRow(int order, int importance, boolean enabled,
                             Component label, Optional<InformationExpression> labelExpression,
                             InformationExpression value, Optional<Component> tooltip,
                             Optional<InformationExpression> tooltipExpression, InformationRowContent.Tone tone,
                             Optional<InformationExpression> visible, MissingPolicy missing) {
    public static final Codec<InformationRow> CODEC = RecordCodecBuilder.create(i -> i.group(
            Codec.INT.optionalFieldOf("order", 0).forGetter(InformationRow::order),
            Codec.INT.optionalFieldOf("importance", 0).forGetter(InformationRow::importance),
            Codec.BOOL.optionalFieldOf("enabled", true).forGetter(InformationRow::enabled),
            ComponentSerialization.CODEC.fieldOf("label").forGetter(InformationRow::label),
            InformationExpression.CODEC.optionalFieldOf("label_expression").forGetter(InformationRow::labelExpression),
            InformationExpression.CODEC.fieldOf("value").forGetter(InformationRow::value),
            ComponentSerialization.CODEC.optionalFieldOf("tooltip").forGetter(InformationRow::tooltip),
            InformationExpression.CODEC.optionalFieldOf("tooltip_expression").forGetter(InformationRow::tooltipExpression),
            InformationRowContent.Tone.CODEC.optionalFieldOf("tone", InformationRowContent.Tone.NORMAL).forGetter(InformationRow::tone),
            InformationExpression.CODEC.optionalFieldOf("visible").forGetter(InformationRow::visible),
            MissingPolicy.CODEC.optionalFieldOf("missing", MissingPolicy.SHOW).forGetter(InformationRow::missing)
    ).apply(i, InformationRow::new));

    public InformationRow {
        label = new InformationValue.Text(label).text();
        tooltip = tooltip.map(text -> new InformationValue.Text(text).text());
    }

    @Override
    public Component label() {
        return new InformationValue.Text(this.label).text();
    }

    @Override
    public Optional<Component> tooltip() {
        return this.tooltip.map(value -> new InformationValue.Text(value).text());
    }

    public Set<EntityValueGetter<?>> getters() {
        Set<EntityValueGetter<?>> result = new HashSet<>(this.value.getters());
        this.visible.ifPresent(expression -> result.addAll(expression.getters()));
        this.labelExpression.ifPresent(expression -> result.addAll(expression.getters()));
        this.tooltipExpression.ifPresent(expression -> result.addAll(expression.getters()));
        return Set.copyOf(result);
    }

    public Set<Identifier> sourceFields() {
        Set<Identifier> result = new HashSet<>(this.value.sourceFields());
        this.visible.ifPresent(expression -> result.addAll(expression.sourceFields()));
        this.labelExpression.ifPresent(expression -> result.addAll(expression.sourceFields()));
        this.tooltipExpression.ifPresent(expression -> result.addAll(expression.sourceFields()));
        return Set.copyOf(result);
    }

    public Optional<InformationRowContent> collect(InformationEvaluation context) {
        if (!this.enabled) return Optional.empty();
        if (this.visible.isPresent()) {
            ExpressionValue condition = context.evaluate(this.visible.get());
            if (condition instanceof ExpressionValue.Missing) return Optional.empty();
            try {
                if (!condition.flag()) return Optional.empty();
            } catch (ExpressionValue.Unavailable failure) {
                return Optional.empty();
            }
        }
        InformationValue value = context.evaluate(this.value).information();
        if (this.missing == MissingPolicy.HIDE && value instanceof InformationValue.Missing) return Optional.empty();
        Component label = this.labelExpression.flatMap(expression -> text(context, expression)).orElseGet(this::label);
        Optional<Component> tooltip = this.tooltipExpression.flatMap(expression -> text(context, expression)).or(this::tooltip);
        return Optional.of(new InformationRowContent(label, value, tooltip, this.tone, this.sourceFields()));
    }

    private static Optional<Component> text(InformationEvaluation context, InformationExpression expression) {
        try {
            return Optional.of(context.evaluate(expression).text());
        } catch (ExpressionValue.Unavailable failure) {
            return Optional.empty();
        }
    }

    public enum MissingPolicy {
        SHOW, HIDE;
        public static final Codec<MissingPolicy> CODEC = ExtraEnumCodecs.enumCodec(MissingPolicy.class);
    }
}
