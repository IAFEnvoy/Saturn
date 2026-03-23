package com.iafenvoy.saturn._loader.neoforge;

//? neoforge {

import com.iafenvoy.saturn.Saturn;
import com.iafenvoy.saturn.bus.SaturnBuses;
import net.neoforged.bus.api.IEventBus;
import net.neoforged.fml.common.Mod;

@Mod(Saturn.MOD_ID)
public final class SaturnNeoForge {
    public SaturnNeoForge(IEventBus bus) {
        SaturnBuses.submitBus(Saturn.MOD_ID, bus);
    }
}
