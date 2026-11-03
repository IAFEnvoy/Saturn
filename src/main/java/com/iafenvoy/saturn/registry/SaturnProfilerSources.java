package com.iafenvoy.saturn.registry;

import com.iafenvoy.saturn.Saturn;
import com.iafenvoy.saturn.profiling.ProfilerSource;

/**
 * The buckets Saturn samples under itself. Claimed from the mod constructor ({@link #init()}) so they are part of
 * {@link SaturnRegistries#PROFILER_SOURCE} before the register event runs; {@code ProfilerSource} owns the registry,
 * this class only names the entries.
 */
public final class SaturnProfilerSources {
    public static final ProfilerSource RESOURCE = ProfilerSource.register(Saturn.MOD_ID, "resource");
    public static final ProfilerSource ABILITY = ProfilerSource.register(Saturn.MOD_ID, "ability");
    public static final ProfilerSource TRIGGER = ProfilerSource.register(Saturn.MOD_ID, "trigger");
    public static final ProfilerSource ITEM = ProfilerSource.register(Saturn.MOD_ID, "item");
    public static final ProfilerSource ENTITY = ProfilerSource.register(Saturn.MOD_ID, "entity");
    public static final ProfilerSource COST = ProfilerSource.register(Saturn.MOD_ID, "cost");
    public static final ProfilerSource ABILITY_RECONCILE = ProfilerSource.register(Saturn.MOD_ID, "ability/reconcile", ABILITY);
    public static final ProfilerSource ABILITY_CAST = ProfilerSource.register(Saturn.MOD_ID, "ability/cast", ABILITY);
    public static final ProfilerSource ABILITY_CHANNEL = ProfilerSource.register(Saturn.MOD_ID, "ability/channel", ABILITY);

    private SaturnProfilerSources() {
    }

    // Touching the class claims the constants above; the mod constructor calls this, before the register event.
    public static void init() {
    }
}
