package com.iafenvoy.saturn.command.client;

import com.iafenvoy.saturn.Saturn;
import com.mojang.brigadier.builder.LiteralArgumentBuilder;
import net.minecraft.commands.CommandSourceStack;
import net.neoforged.api.distmarker.Dist;
import net.neoforged.bus.api.SubscribeEvent;
import net.neoforged.fml.common.EventBusSubscriber;
import net.neoforged.neoforge.client.event.RegisterClientCommandsEvent;

import static net.minecraft.commands.Commands.literal;

/**
 * The client half of the command surface: nodes that only make sense in the player's own game, registered on the
 * client dispatcher as children of the same {@code /saturn} root the server builds.
 *
 * <p>Brigadier merges the two trees by literal name, so the server's nodes and these coexist; a dependent mod that
 * wants its own client node adds it the same way.
 */
@EventBusSubscriber(Dist.CLIENT)
public final class SaturnClientCommands {
    private SaturnClientCommands() {
    }

    @SubscribeEvent
    public static void register(RegisterClientCommandsEvent event) {
        LiteralArgumentBuilder<CommandSourceStack> root = literal(Saturn.MOD_ID);
        root.then(SaturnHudCommand.node());
        event.getDispatcher().register(root);
    }
}
