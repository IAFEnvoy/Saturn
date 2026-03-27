package com.iafenvoy.saturn.resource;

//? neoforge {
/*import net.neoforged.neoforge.client.event.AddClientReloadListenersEvent;
import net.neoforged.neoforge.event.AddServerReloadListenersEvent;
import net.minecraft.server.packs.PackType;
import net.neoforged.bus.api.SubscribeEvent;
import net.neoforged.fml.common.EventBusSubscriber;
import org.jetbrains.annotations.ApiStatus;

@ApiStatus.Internal
@EventBusSubscriber
public final class ResourceReloaderEvents {
    @SubscribeEvent
    public static void registerServerListener(AddServerReloadListenersEvent event) {
        ResourceReloaders.get(PackType.SERVER_DATA).visit(event::addListener);
    }

    @SubscribeEvent
    public static void registerClientListener(AddClientReloadListenersEvent event) {
        ResourceReloaders.get(PackType.CLIENT_RESOURCES).visit(event::addListener);
    }
}
*/