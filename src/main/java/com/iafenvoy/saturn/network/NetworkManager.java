package com.iafenvoy.saturn.network;

import com.iafenvoy.saturn.network.payload.OpenScreenS2CPayload;
import net.neoforged.bus.api.SubscribeEvent;
import net.neoforged.fml.common.EventBusSubscriber;
import net.neoforged.neoforge.network.event.RegisterPayloadHandlersEvent;
import net.neoforged.neoforge.network.handling.MainThreadPayloadHandler;

@EventBusSubscriber
public final class NetworkManager {
    @SubscribeEvent
    public static void registerPayloads(RegisterPayloadHandlersEvent event) {
        event.registrar("1")
                .playToClient(OpenScreenS2CPayload.TYPE, OpenScreenS2CPayload.STREAM_CODEC, new MainThreadPayloadHandler<>(ClientNetworkHandler::onOpenScreen));
    }
}
