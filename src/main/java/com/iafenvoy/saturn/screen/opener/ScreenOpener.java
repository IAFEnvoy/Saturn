package com.iafenvoy.saturn.screen.opener;

import com.iafenvoy.saturn.network.payload.OpenScreenS2CPayload;
import net.minecraft.server.level.ServerPlayer;
import net.neoforged.neoforge.network.PacketDistributor;

public final class ScreenOpener {
    public static void open(ServerPlayer player, OpenScreenData data) {
        PacketDistributor.sendToPlayer(player, new OpenScreenS2CPayload(data));
    }
}
