package com.iafenvoy.saturn.util.function;

@FunctionalInterface
public interface TriConsumer<A, B, C> {
    void accept(A first, B second, C third);
}
