package com.iafenvoy.saturn.network;

import com.iafenvoy.saturn.network.payload.OpenScreenS2CPayload;
import com.iafenvoy.saturn.screen.opener.ClientScreenDispatcher;
import net.neoforged.neoforge.network.handling.IPayloadContext;

public final class ClientNetworkHandler {
    static void onOpenScreen(OpenScreenS2CPayload payload, IPayloadContext context) {
        ClientScreenDispatcher.onPacket(payload.data());
    }
}
