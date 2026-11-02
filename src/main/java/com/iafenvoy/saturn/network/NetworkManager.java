package com.iafenvoy.saturn.network;

import com.iafenvoy.saturn.network.payload.InformationGuiS2CPayload;
import net.neoforged.api.distmarker.Dist;
import net.neoforged.bus.api.SubscribeEvent;
import net.neoforged.fml.common.EventBusSubscriber;
import net.neoforged.fml.loading.FMLEnvironment;
import net.neoforged.neoforge.network.event.RegisterPayloadHandlersEvent;
import net.neoforged.neoforge.network.handling.MainThreadPayloadHandler;
import net.neoforged.neoforge.network.registration.PayloadRegistrar;

@EventBusSubscriber
public final class NetworkManager {
    @SubscribeEvent
    public static void registerPayloads(RegisterPayloadHandlersEvent event) {
        PayloadRegistrar registrar = event.registrar("1");
        // A dedicated server needs the codec for sending, without loading client screen handlers.
        if (FMLEnvironment.getDist() != Dist.CLIENT) {
            registrar.playToClient(InformationGuiS2CPayload.TYPE, InformationGuiS2CPayload.STREAM_CODEC);
            return;
        }
        registrar.playToClient(InformationGuiS2CPayload.TYPE, InformationGuiS2CPayload.STREAM_CODEC, new MainThreadPayloadHandler<>(ClientNetworkHandler::onInformationGui));
    }
}
