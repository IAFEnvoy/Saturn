package com.iafenvoy.saturn.registry.content;

import com.iafenvoy.saturn.Saturn;
import com.iafenvoy.saturn.data.timeline.TimelineEntry;
import com.iafenvoy.saturn.data.timeline.builtin.ActionEntry;
import com.iafenvoy.saturn.data.timeline.builtin.BranchEntry;
import com.iafenvoy.saturn.data.timeline.builtin.IdleEntry;
import com.iafenvoy.saturn.data.timeline.builtin.WaitForEntry;
import com.iafenvoy.saturn.registry.SaturnRegistries;
import com.mojang.serialization.MapCodec;
import net.neoforged.neoforge.registries.DeferredRegister;

/**
 * The built-in {@code timeline_entry_type} entries.
 */
public final class SaturnTimelineEntries {
    public static final DeferredRegister<MapCodec<? extends TimelineEntry>> REGISTRY = DeferredRegister.create(SaturnRegistries.TIMELINE_ENTRY_TYPE, Saturn.MOD_ID);

    static {
        REGISTRY.register("action", () -> ActionEntry.CODEC);
        REGISTRY.register("branch", () -> BranchEntry.CODEC);
        REGISTRY.register("idle", () -> IdleEntry.CODEC);
        REGISTRY.register("wait_for", () -> WaitForEntry.CODEC);
    }

    private SaturnTimelineEntries() {
    }
}
