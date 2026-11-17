package com.iafenvoy.saturn.network;

import com.iafenvoy.saturn.network.payload.OpenScreenS2CPayload;
import com.iafenvoy.saturn.network.payload.WheelActionC2SPayload;
import com.iafenvoy.saturn.network.payload.WheelLayoutC2SPayload;
import com.iafenvoy.saturn.network.payload.WheelSelectionC2SPayload;
import net.neoforged.bus.api.SubscribeEvent;
import net.neoforged.fml.common.EventBusSubscriber;
import net.neoforged.neoforge.network.event.RegisterPayloadHandlersEvent;
import net.neoforged.neoforge.network.handling.MainThreadPayloadHandler;

@EventBusSubscriber
public final class NetworkManager {
    @SubscribeEvent
    public static void registerPayloads(RegisterPayloadHandlersEvent event) {
        event.registrar("1")
                .playToServer(WheelActionC2SPayload.TYPE, WheelActionC2SPayload.STREAM_CODEC, new MainThreadPayloadHandler<>(WheelNetworkHandler::action))
                .playToServer(WheelLayoutC2SPayload.TYPE, WheelLayoutC2SPayload.STREAM_CODEC, new MainThreadPayloadHandler<>(WheelNetworkHandler::layout))
                .playToServer(WheelSelectionC2SPayload.TYPE, WheelSelectionC2SPayload.STREAM_CODEC, new MainThreadPayloadHandler<>(WheelNetworkHandler::selection))
                .playToClient(OpenScreenS2CPayload.TYPE, OpenScreenS2CPayload.STREAM_CODEC, new MainThreadPayloadHandler<>(ClientNetworkHandler::onOpenScreen));
    }
}
