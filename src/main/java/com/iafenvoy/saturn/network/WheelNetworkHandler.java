package com.iafenvoy.saturn.network;

import com.iafenvoy.saturn.network.payload.WheelActionC2SPayload;
import com.iafenvoy.saturn.network.payload.WheelLayoutC2SPayload;
import com.iafenvoy.saturn.network.payload.WheelSelectionC2SPayload;
import com.iafenvoy.saturn.registry.SaturnAttachments;
import com.iafenvoy.saturn.runtime.wheel.WheelEntryKinds;
import com.iafenvoy.saturn.runtime.wheel.WheelService;
import com.iafenvoy.saturn.runtime.wheel.WheelSourceTypes;
import net.minecraft.server.level.ServerPlayer;
import net.neoforged.neoforge.network.handling.IPayloadContext;

final class WheelNetworkHandler {
    static void action(WheelActionC2SPayload payload, IPayloadContext context) {
        if (context.player() instanceof ServerPlayer player)
            WheelService.trigger(player, WheelSourceTypes.byId(payload.source()).orElse(null),
                    WheelEntryKinds.byId(payload.kind()).orElse(null), payload.id(), payload.enabled());
    }

    static void layout(WheelLayoutC2SPayload payload, IPayloadContext context) {
        if (context.player() instanceof ServerPlayer player)
            player.getData(SaturnAttachments.WHEEL_LAYOUT).setLayout(WheelService.sanitize(player, payload.layout()));
    }

    static void selection(WheelSelectionC2SPayload payload, IPayloadContext context) {
        if (context.player() instanceof ServerPlayer player)
            player.getData(SaturnAttachments.WHEEL_LAYOUT).setArmed(WheelService.sanitizeArmed(payload.armed().orElse(null)));
    }
}
