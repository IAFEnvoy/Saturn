package com.iafenvoy.saturn.event.level;

import com.iafenvoy.saturn.event.Event;
import com.iafenvoy.saturn.event.EventFactory;
import com.iafenvoy.saturn.annotation.NeoForgeOnly;
import net.minecraft.world.level.LevelAccessor;
//? fabric {
import net.fabricmc.fabric.api.event.lifecycle.v1.ServerTickEvents;
import net.fabricmc.fabric.api.event.lifecycle.v1.ServerLevelEvents;
//?} else neoforge {
/*import net.neoforged.neoforge.common.NeoForge;
import net.neoforged.neoforge.event.level.LevelEvent;
import net.neoforged.neoforge.event.tick.LevelTickEvent;
*///?}

import java.util.function.Consumer;

public final class ServerLevelLifeCycleEvents {
    public static final Event<Consumer<LevelAccessor>> LOAD = EventFactory.consumer();
    public static final Event<Consumer<LevelAccessor>> UNLOAD = EventFactory.consumer();
    @NeoForgeOnly
    public static final Event<Consumer<LevelAccessor>> SAVE = EventFactory.consumer();
    public static final Event<Consumer<LevelAccessor>> TICK_START = EventFactory.consumer();
    public static final Event<Consumer<LevelAccessor>> TICK_END = EventFactory.consumer();

    static {
        //? fabric {
        ServerLevelEvents.LOAD.register((s, l) -> LOAD.invoker().accept(l));
        ServerLevelEvents.UNLOAD.register((s, l) -> UNLOAD.invoker().accept(l));
        ServerTickEvents.START_LEVEL_TICK.register(x -> TICK_START.invoker().accept(x));
        ServerTickEvents.END_LEVEL_TICK.register(x -> TICK_END.invoker().accept(x));
        //?} else neoforge {
        /*NeoForge.EVENT_BUS.addListener(LevelEvent.Load.class, event -> LOAD.invoker().accept(event.getLevel()));
        NeoForge.EVENT_BUS.addListener(LevelEvent.Unload.class, event -> UNLOAD.invoker().accept(event.getLevel()));
        NeoForge.EVENT_BUS.addListener(LevelEvent.Save.class, event -> SAVE.invoker().accept(event.getLevel()));
        NeoForge.EVENT_BUS.addListener(LevelTickEvent.Pre.class, event -> TICK_START.invoker().accept(event.getLevel()));
        NeoForge.EVENT_BUS.addListener(LevelTickEvent.Post.class, event -> TICK_END.invoker().accept(event.getLevel()));
        *///?}
    }
}
