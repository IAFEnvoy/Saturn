package com.iafenvoy.saturn.data.information.expression;

import com.google.gson.JsonElement;
import com.iafenvoy.saturn.data.information.expression.builtin.ConstantExpression;
import com.iafenvoy.saturn.data.information.getter.EntityValueGetter;
import com.iafenvoy.saturn.data.information.getter.EntityValueGetters;
import com.iafenvoy.saturn.registry.SaturnRegistries;
import com.mojang.datafixers.util.Either;
import com.mojang.serialization.Codec;
import com.mojang.serialization.DataResult;
import com.mojang.serialization.JsonOps;
import com.mojang.serialization.MapCodec;
import net.minecraft.resources.Identifier;

import java.util.HashSet;
import java.util.Set;
import java.util.function.Function;

/**
 * Register a MapCodec in INFORMATION_EXPRESSION_TYPE to add a reusable, read-only operation.
 */
public interface InformationExpression {
    // Nodes reference this dispatch codec in their operands; defer construction until static initialization is complete.
    Codec<InformationExpression> CODEC = Codec.lazyInitialized(() -> {
        Codec<InformationExpression> literal = Codec.PASSTHROUGH.validate(value -> {
            JsonElement json = value.convert(JsonOps.INSTANCE).getValue();
            return json.isJsonPrimitive() ? DataResult.success(value) : DataResult.error(() -> "Expected a primitive expression or a typed object");
        }).xmap(ConstantExpression::new, expression -> ((ConstantExpression) expression).value());
        Codec<MapCodec<? extends InformationExpression>> types = Identifier.CODEC.comapFlatMap(id ->
                        SaturnRegistries.INFORMATION_EXPRESSION_TYPE.containsKey(id)
                                ? DataResult.success(SaturnRegistries.INFORMATION_EXPRESSION_TYPE.getValue(id))
                                : DataResult.error(() -> "Unknown information expression type: " + id),
                SaturnRegistries.INFORMATION_EXPRESSION_TYPE::getKey);
        Codec<InformationExpression> typed = types.dispatch("type", InformationExpression::codec, Function.identity());
        return Codec.either(literal, typed).xmap(either -> either.map(Function.identity(), Function.identity()),
                expression -> expression instanceof ConstantExpression constant
                        && constant.value().convert(JsonOps.INSTANCE).getValue().isJsonPrimitive() ? Either.left(expression) : Either.right(expression));
    });

    MapCodec<? extends InformationExpression> codec();

    ExpressionValue evaluate(InformationEvaluation context);

    /**
     * Include dependencies of all branches, including branches not selected at runtime.
     */
    Set<EntityValueGetter<?>> getters();

    default Set<Identifier> sourceFields() {
        Set<Identifier> result = new HashSet<>();
        this.getters().forEach(getter -> result.addAll(EntityValueGetters.sources(getter)));
        return Set.copyOf(result);
    }
}
