package com.iafenvoy.saturn.event;

import com.iafenvoy.saturn.util.function.Consumer3;

import java.util.function.BiConsumer;
import java.util.function.Consumer;

public final class EventFactory {
    public static Event<Runnable> runnable() {
        return Event.create(consumers -> () -> consumers.forEach(Runnable::run));
    }

    public static <T> Event<Consumer<T>> consumer() {
        return Event.create(consumers -> arg -> consumers.forEach(x -> x.accept(arg)));
    }

    public static <T, U> Event<BiConsumer<T, U>> biConsumer() {
        return Event.create(consumers -> (arg1, arg2) -> consumers.forEach(x -> x.accept(arg1, arg2)));
    }

    public static <T1, T2, T3> Event<Consumer3<T1, T2, T3>> consumer3() {
        return Event.create(consumers -> (arg1, arg2, arg3) -> consumers.forEach(x -> x.accept(arg1, arg2, arg3)));
    }
}
