package com.iafenvoy.saturn.command;

import com.iafenvoy.saturn.command.client.HudCommand;
import net.neoforged.api.distmarker.Dist;
import net.neoforged.bus.api.SubscribeEvent;
import net.neoforged.fml.common.EventBusSubscriber;
import net.neoforged.neoforge.client.event.RegisterClientCommandsEvent;

/**
 * The client half of the command surface: nodes that only make sense in the player's own game, registered on the
 * client dispatcher as children of the same {@code /saturn} root the server builds.
 *
 * <p>Brigadier merges the two trees by literal name, so the server's nodes and these coexist; a dependent mod that
 * wants its own client node adds it the same way.
 */
@EventBusSubscriber(Dist.CLIENT)
public final class ClientCommandManager {
    private ClientCommandManager() {
    }

    @SubscribeEvent
    public static void register(RegisterClientCommandsEvent event) {
        event.getDispatcher().register(HudCommand.node());
    }
}
