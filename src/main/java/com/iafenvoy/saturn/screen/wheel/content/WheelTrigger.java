package com.iafenvoy.saturn.screen.wheel.content;

import com.iafenvoy.saturn.api.wheel.WheelMenuEntry;
import com.iafenvoy.saturn.api.wheel.WheelSource;
import com.iafenvoy.saturn.network.payload.WheelActionC2SPayload;
import net.neoforged.neoforge.client.network.ClientPacketDistributor;

public final class WheelTrigger {
    private WheelTrigger() {
    }

    public static void send(WheelMenuEntry entry, WheelSource source) {
        ClientPacketDistributor.sendToServer(WheelActionC2SPayload.press(source.id(), entry.kind().id(), entry.id()));
    }
}