package com.iafenvoy.saturn.util.function;

@FunctionalInterface
public interface QuadConsumer<A, B, C, D> {
    void accept(A first, B second, C third, D fourth);
}
