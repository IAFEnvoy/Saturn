package com.iafenvoy.saturn.util;

import net.minecraft.core.Holder;
import net.minecraft.resources.Identifier;
import net.minecraft.resources.ResourceKey;

import java.util.Optional;
import java.util.function.Function;

public final class HolderHelper {
    public static final Identifier EMPTY = Identifier.fromNamespaceAndPath("", "");

    public static Identifier id(Holder<?> holder) {
        return idOptional(holder).orElse(EMPTY);
    }

    public static Identifier idOrNull(Holder<?> holder) {
        return idOptional(holder).orElse(null);
    }

    public static Optional<Identifier> idOptional(Holder<?> holder) {
        return holder.unwrapKey().map(ResourceKey::identifier);
    }

    public static <T> Function<Holder.Reference<T>, Holder<T>> cast() {
        return x -> x;
    }
}
