package com.iafenvoy.saturn.network;

import com.iafenvoy.saturn.network.payload.InformationGuiS2CPayload;
import com.iafenvoy.saturn.screen.information.PlayerInformationScreen;
import net.minecraft.client.Minecraft;
import net.neoforged.neoforge.network.handling.IPayloadContext;

public final class ClientNetworkHandler {
    static void onInformationGui(InformationGuiS2CPayload payload, IPayloadContext context) {
        Minecraft.getInstance().setScreen(new PlayerInformationScreen(null, payload.document(), payload.selectedGroups()));
    }
}
