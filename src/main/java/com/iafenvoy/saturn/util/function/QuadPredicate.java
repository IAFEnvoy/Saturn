package com.iafenvoy.saturn.util.function;

@FunctionalInterface
public interface QuadPredicate<A, B, C, D> {
    boolean test(A first, B second, C third, D fourth);
}
