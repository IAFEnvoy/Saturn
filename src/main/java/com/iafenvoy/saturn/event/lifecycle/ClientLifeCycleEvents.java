package com.iafenvoy.saturn.event.lifecycle;

import com.iafenvoy.saturn.event.Event;
import com.iafenvoy.saturn.event.EventFactory;
import net.minecraft.client.Minecraft;
//? fabric {
/*import net.fabricmc.fabric.api.client.event.lifecycle.v1.ClientTickEvents;
 *///?} else neoforge {
import net.neoforged.neoforge.client.event.ClientTickEvent;
import net.neoforged.neoforge.common.NeoForge;
//?} else forge {
/*import com.iafenvoy.saturn.event.ForgeEventHelper;
import net.minecraftforge.event.TickEvent;
*///?}

import java.util.function.Consumer;

public final class ClientLifeCycleEvents {
    public static final Event<Consumer<Minecraft>> TICK_START = EventFactory.consumer();
    public static final Event<Consumer<Minecraft>> TICK_END = EventFactory.consumer();

    static {
        //? fabric {
       /*ClientTickEvents.START_CLIENT_TICK.register(client -> TICK_START.invoker().accept(client));
       ClientTickEvents.END_CLIENT_TICK.register(client -> TICK_END.invoker().accept(client));
        *///?} else neoforge {
        NeoForge.EVENT_BUS.addListener(ClientTickEvent.Pre.class, event -> TICK_START.invoker().accept(Minecraft.getInstance()));
        NeoForge.EVENT_BUS.addListener(ClientTickEvent.Post.class, event -> TICK_END.invoker().accept(Minecraft.getInstance()));
        //?} else forge {
        /*ForgeEventHelper.addListener(TickEvent.ClientTickEvent.class, event -> {
            switch (event.phase) {
                case START -> TICK_START.invoker().accept(Minecraft.getInstance());
                case END -> TICK_END.invoker().accept(Minecraft.getInstance());
            }
        });
        *///?}
    }
}
