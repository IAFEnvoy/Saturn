package com.iafenvoy.saturn.command;

import com.iafenvoy.saturn.Saturn;
import com.iafenvoy.saturn.attachment.AbilityAttachment;
import com.iafenvoy.saturn.attachment.ResourceHolderAttachment;
import com.iafenvoy.saturn.data.storage.builtin.CooldownDataStorage;
import com.iafenvoy.saturn.registry.SaturnAttachments;
import com.iafenvoy.saturn.registry.SaturnDatapackRegistries;
import com.iafenvoy.saturn.registry.SaturnRegistries;
import com.mojang.brigadier.builder.LiteralArgumentBuilder;
import com.mojang.brigadier.exceptions.CommandSyntaxException;
import net.minecraft.commands.CommandSourceStack;
import net.minecraft.core.DefaultedRegistry;
import net.minecraft.core.Registry;
import net.minecraft.network.chat.Component;
import net.minecraft.resources.ResourceKey;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.server.permissions.Permissions;
import net.neoforged.bus.api.SubscribeEvent;
import net.neoforged.fml.common.EventBusSubscriber;
import net.neoforged.neoforge.event.RegisterCommandsEvent;

import java.util.stream.Collectors;

import static net.minecraft.commands.Commands.literal;

/**
 * The server command surface: everything that can be read or changed without a screen. Saturn registers one root,
 * {@code /saturn}, and a dependent mod keeps its own; the two never share a top-level literal.
 *
 * <p>What is <em>not</em> here is a node for a system Saturn does not run: there is no node for a content event, a
 * world rule or a page. Reading definitions, stored state, and the framework's own runtimes is what belongs here.
 */
@EventBusSubscriber
public final class SaturnCommands {
    private SaturnCommands() {
    }

    // The one answer to "may this source change somebody else's state", so no node spells its own check.
    public static boolean mayChange(CommandSourceStack source) {
        return source.permissions().hasPermission(Permissions.COMMANDS_GAMEMASTER);
    }

    @SubscribeEvent
    public static void register(RegisterCommandsEvent event) {
        LiteralArgumentBuilder<CommandSourceStack> root = literal(Saturn.MOD_ID);
        root.then(SaturnDebugCommand.node());
        root.then(SaturnAbilityCommand.node(event.getBuildContext()));
        root.then(SaturnTriggersCommand.node(event.getBuildContext()));
        root.then(SaturnResourceCommand.node());
        root.then(literal("registries").executes(ctx -> listRegistries(ctx.getSource())));
        root.then(literal("attachment").then(literal("status").executes(ctx -> attachmentStatus(ctx.getSource()))));
        event.getDispatcher().register(root);
    }

    // Datapack registries are counted through the access; code registries carry their default id, which is the
    // contract a definition without a type field relies on.
    private static int listRegistries(CommandSourceStack source) {
        String datapack = SaturnDatapackRegistries.registries().stream()
                .map(key -> key.identifier() + "=" + source.registryAccess().lookupOrThrow(key).size())
                .collect(Collectors.joining(", "));
        String code = SaturnRegistries.all().stream()
                .map(SaturnCommands::describe)
                .collect(Collectors.joining(", "));
        source.sendSuccess(() -> Component.translatable("command.saturn.registries.datapack", datapack), false);
        source.sendSuccess(() -> Component.translatable("command.saturn.registries.code", code), false);
        return SaturnDatapackRegistries.registries().size() + SaturnRegistries.all().size();
    }

    private static String describe(DefaultedRegistry<?> registry) {
        ResourceKey<? extends Registry<?>> key = registry.key();
        return key.identifier() + "=" + registry.size() + "/" + registry.getDefaultKey();
    }

    private static int attachmentStatus(CommandSourceStack source) throws CommandSyntaxException {
        ServerPlayer player = source.getPlayerOrException();
        AbilityAttachment abilities = player.getData(SaturnAttachments.ABILITY_HOLDER);
        ResourceHolderAttachment resources = player.getData(SaturnAttachments.RESOURCE_HOLDER);
        source.sendSuccess(() -> Component.translatable("command.saturn.attachment.status",
                resources.values().size(), abilities.sources().size(), abilities.storage().count(CooldownDataStorage.class)), false);
        return 1;
    }
}
