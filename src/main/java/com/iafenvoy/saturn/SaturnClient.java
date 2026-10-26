package com.iafenvoy.saturn;

import com.iafenvoy.saturn.screen.resourcebar.ResourceBarOverlay;
import net.neoforged.api.distmarker.Dist;
import net.neoforged.bus.api.SubscribeEvent;
import net.neoforged.fml.common.EventBusSubscriber;
import net.neoforged.fml.event.lifecycle.FMLClientSetupEvent;

/**
 * The client registration the framework owns: the four resource-bar HUD elements, registered from client setup
 * rather than the first frame so the HUD editor finds them on the main menu too.
 *
 * <p>{@link ResourceBarOverlay#registerEntries()} is idempotent, so a dependent mod that still calls it from its own
 * client setup registers nothing twice.
 */
@EventBusSubscriber(Dist.CLIENT)
public final class SaturnClient {
    private SaturnClient() {
    }

    @SubscribeEvent
    public static void onClientSetup(FMLClientSetupEvent event) {
        event.enqueueWork(ResourceBarOverlay::registerEntries);
    }
}
