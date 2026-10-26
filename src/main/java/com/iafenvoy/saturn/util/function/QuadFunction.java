package com.iafenvoy.saturn.util.function;

@FunctionalInterface
public interface QuadFunction<A, B, C, D, R> {
    R apply(A first, B second, C third, D fourth);
}
