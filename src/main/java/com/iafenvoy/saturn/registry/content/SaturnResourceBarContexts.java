package com.iafenvoy.saturn.registry.content;

import com.iafenvoy.saturn.Saturn;
import com.iafenvoy.saturn.data.resourcebar.ResourceBarContext;
import com.iafenvoy.saturn.data.resourcebar.builtin.context.BossOverlayContext;
import com.iafenvoy.saturn.data.resourcebar.builtin.context.SelfHudContext;
import com.iafenvoy.saturn.data.resourcebar.builtin.context.TargetOverlayContext;
import com.iafenvoy.saturn.registry.SaturnRegistries;
import net.neoforged.neoforge.registries.DeferredRegister;

/**
 * The built-in {@code resource_bar_context} entries.
 */
public final class SaturnResourceBarContexts {
    public static final DeferredRegister<ResourceBarContext> REGISTRY = DeferredRegister.create(SaturnRegistries.RESOURCE_BAR_CONTEXT, Saturn.MOD_ID);

    static {
        REGISTRY.register("self_hud", () -> SelfHudContext.INSTANCE);
        REGISTRY.register("target_overlay", () -> TargetOverlayContext.INSTANCE);
        REGISTRY.register("boss_overlay", () -> BossOverlayContext.INSTANCE);
    }

    private SaturnResourceBarContexts() {
    }
}
