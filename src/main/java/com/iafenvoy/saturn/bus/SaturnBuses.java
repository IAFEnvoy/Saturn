package com.iafenvoy.saturn.bus;

//? !fabric {

import net.neoforged.bus.api.IEventBus;
import org.jetbrains.annotations.NotNull;

import java.util.HashMap;
import java.util.Map;

public final class SaturnBuses {
    private static final Map<String, IEventBus> BUSES = new HashMap<>();

    public static void submitBus(String namespace, IEventBus modBus) {
        BUSES.put(namespace, modBus);
    }

    @NotNull
    public static IEventBus findBus(String namespace) {
        IEventBus bus = BUSES.get(namespace);
        if (bus == null)
            throw new RuntimeException("Cannot find bus for " + namespace + ", forget to SaturnBuses.submitBus?");
        return bus;
    }
}
