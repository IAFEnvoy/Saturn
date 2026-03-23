package com.iafenvoy.saturn.event;

import java.util.Collection;
import java.util.Comparator;
import java.util.TreeSet;
import java.util.function.Function;

public class Event<T> {
    public static final int DEFAULT_PRIORITY = 0;//bigger->higher priority
    private final TreeSet<EventHolder<T>> holders = new TreeSet<>(Comparator.
            <EventHolder<T>>comparingInt(EventHolder::priority).reversed()
            .thenComparing(holder -> System.identityHashCode(holder.callback())));
    private final Function<Collection<T>, T> provider;

    protected Event(Function<Collection<T>, T> provider) {
        this.provider = provider;
    }

    public static <T> Event<T> create(Function<Collection<T>, T> provider) {
        return new Event<>(provider);
    }

    public void register(T callback) {
        this.register(DEFAULT_PRIORITY, callback);
    }

    public void register(int priority, T callback) {
        this.holders.add(new EventHolder<>(priority, callback));
    }

    public void unregister(T callback) {
        this.holders.removeIf(x -> x.callback() == callback);
    }

    public T invoker() {
        return this.provider.apply(this.holders.stream().map(EventHolder::callback).toList());
    }

    private record EventHolder<T>(int priority, T callback) {
    }
}
