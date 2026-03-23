package com.iafenvoy.saturn.event.lifecycle;

import com.iafenvoy.saturn.event.Event;
import com.iafenvoy.saturn.event.EventFactory;
import net.minecraft.server.MinecraftServer;
//? fabric {
/*import net.fabricmc.fabric.api.event.lifecycle.v1.ServerLifecycleEvents;
import net.fabricmc.fabric.api.event.lifecycle.v1.ServerTickEvents;
*///?} else neoforge {
import net.neoforged.neoforge.common.NeoForge;
import net.neoforged.neoforge.event.server.ServerStartedEvent;
import net.neoforged.neoforge.event.server.ServerStartingEvent;
import net.neoforged.neoforge.event.server.ServerStoppedEvent;
import net.neoforged.neoforge.event.server.ServerStoppingEvent;
import net.neoforged.neoforge.event.tick.ServerTickEvent;
//?} else forge {
/*import com.iafenvoy.saturn.event.ForgeEventHelper;
import net.minecraftforge.event.TickEvent;
import net.minecraftforge.event.server.*;
*///?}

import java.util.function.Consumer;

public final class ServerLifeCycleEvents {
    public static final Event<Consumer<MinecraftServer>> STARTING = EventFactory.consumer();
    public static final Event<Consumer<MinecraftServer>> STARTED = EventFactory.consumer();
    public static final Event<Consumer<MinecraftServer>> TICK_START = EventFactory.consumer();
    public static final Event<Consumer<MinecraftServer>> TICK_END = EventFactory.consumer();
    public static final Event<Consumer<MinecraftServer>> STOPPING = EventFactory.consumer();
    public static final Event<Consumer<MinecraftServer>> STOPPED = EventFactory.consumer();

    static {
        //? fabric {
        /*ServerLifecycleEvents.SERVER_STARTING.register(x -> STARTING.invoker().accept(x));
        ServerLifecycleEvents.SERVER_STARTED.register(x -> STARTED.invoker().accept(x));
        ServerTickEvents.START_SERVER_TICK.register(x -> TICK_START.invoker().accept(x));
        ServerTickEvents.END_SERVER_TICK.register(x -> TICK_END.invoker().accept(x));
        ServerLifecycleEvents.SERVER_STOPPING.register(x -> STOPPING.invoker().accept(x));
        ServerLifecycleEvents.SERVER_STOPPED.register(x -> STOPPED.invoker().accept(x));
        *///?} else neoforge {
        NeoForge.EVENT_BUS.addListener(ServerStartingEvent.class, event -> STARTING.invoker().accept(event.getServer()));
        NeoForge.EVENT_BUS.addListener(ServerStartedEvent.class, event -> STARTED.invoker().accept(event.getServer()));
        NeoForge.EVENT_BUS.addListener(ServerTickEvent.Pre.class, event -> TICK_START.invoker().accept(event.getServer()));
        NeoForge.EVENT_BUS.addListener(ServerTickEvent.Post.class, event -> TICK_END.invoker().accept(event.getServer()));
        NeoForge.EVENT_BUS.addListener(ServerStoppingEvent.class, event -> STOPPING.invoker().accept(event.getServer()));
        NeoForge.EVENT_BUS.addListener(ServerStoppedEvent.class, event -> STOPPED.invoker().accept(event.getServer()));
        //?} else forge {
        /*ForgeEventHelper.addListener(ServerStartingEvent.class, event -> STARTING.invoker().accept(event.getServer()));
        ForgeEventHelper.addListener(ServerStartedEvent.class, event -> STARTED.invoker().accept(event.getServer()));
        ForgeEventHelper.addListener(TickEvent.ServerTickEvent.class, event -> {
            switch (event.phase) {
                case START -> TICK_START.invoker().accept(event.getServer());
                case END -> TICK_END.invoker().accept(event.getServer());
            }
        });
        ForgeEventHelper.addListener(ServerStoppingEvent.class, event -> STOPPING.invoker().accept(event.getServer()));
        ForgeEventHelper.addListener(ServerStoppedEvent.class, event -> STOPPED.invoker().accept(event.getServer()));
        *///?}
    }
}
