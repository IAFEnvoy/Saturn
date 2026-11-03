package com.iafenvoy.saturn.command.server;

import com.iafenvoy.saturn.data.information.*;
import com.iafenvoy.saturn.data.information.model.InformationContext;
import com.iafenvoy.saturn.data.information.model.InformationDocument;
import com.iafenvoy.saturn.data.information.model.InformationEntry;
import com.iafenvoy.saturn.registry.SaturnResourceKeys;
import com.iafenvoy.saturn.screen.information.InformationOpenScreenData;
import com.iafenvoy.saturn.screen.opener.ScreenOpener;
import com.mojang.brigadier.builder.LiteralArgumentBuilder;
import com.mojang.brigadier.exceptions.CommandSyntaxException;
import com.mojang.brigadier.exceptions.DynamicCommandExceptionType;
import net.minecraft.commands.CommandSourceStack;
import net.minecraft.commands.SharedSuggestionProvider;
import net.minecraft.commands.arguments.EntityArgument;
import net.minecraft.commands.arguments.IdentifierArgument;
import net.minecraft.core.Holder;
import net.minecraft.network.chat.Component;
import net.minecraft.resources.Identifier;
import net.minecraft.resources.ResourceKey;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.server.permissions.Permissions;
import net.minecraft.world.entity.Entity;

import java.util.Optional;
import java.util.Set;
import java.util.stream.Stream;

import static net.minecraft.commands.Commands.argument;
import static net.minecraft.commands.Commands.literal;

/**
 * Operator diagnostic for the same getter/expression results used by screens and cards.
 */
public final class InformationCommand {
    private static final DynamicCommandExceptionType UNKNOWN_GROUP = new DynamicCommandExceptionType(id -> Component.translatable("command.saturn.information.unknown_group", id.toString()));

    private InformationCommand() {
    }

    public static LiteralArgumentBuilder<CommandSourceStack> node() {
        return literal("information").requires(source -> source.permissions().hasPermission(Permissions.COMMANDS_ADMIN))
                .then(outputNode("query", false))
                .then(outputNode("gui", true));
    }

    private static LiteralArgumentBuilder<CommandSourceStack> outputNode(String name, boolean gui) {
        return literal(name)
                .then(argument("target", EntityArgument.entity())
                        .executes(ctx -> show(ctx.getSource(), EntityArgument.getEntity(ctx, "target"), Optional.empty(), gui))
                        .then(argument("group", IdentifierArgument.id())
                                .suggests((ctx, builder) -> SharedSuggestionProvider.suggestResource(groupIds(ctx.getSource()), builder))
                                .executes(ctx -> show(ctx.getSource(), EntityArgument.getEntity(ctx, "target"),
                                        Optional.of(IdentifierArgument.getId(ctx, "group")), gui))));
    }

    private static Stream<Identifier> groupIds(CommandSourceStack source) {
        return source.registryAccess().lookupOrThrow(SaturnResourceKeys.INFORMATION_GROUP).listElementIds()
                .map(ResourceKey::identifier).sorted();
    }

    private static Holder<InformationGroup> group(CommandSourceStack source, Identifier id) throws CommandSyntaxException {
        return source.registryAccess().lookupOrThrow(SaturnResourceKeys.INFORMATION_GROUP)
                .get(ResourceKey.create(SaturnResourceKeys.INFORMATION_GROUP, id))
                .orElseThrow(() -> UNKNOWN_GROUP.create(id));
    }

    private static int show(CommandSourceStack source, Entity target, Optional<Identifier> groupId, boolean gui) throws CommandSyntaxException {
        ServerPlayer viewer = gui ? source.getPlayerOrException() : null;
        Holder<InformationGroup> selected = groupId.isPresent() ? group(source, groupId.get()) : null;
        PlayerViewSnapshot view = PlayerViews.parse(target);
        ServerPlayer executor = source.getPlayer();
        InformationContext context = new InformationContext(executor == null ? "en_us" : executor.clientInformation().language(), source.registryAccess());
        InformationDocument document = selected == null ? PlayerInformation.collectAll(view, context) : PlayerInformation.collect(view, context, selected);
        if (viewer != null) {
            ScreenOpener.open(viewer, new InformationOpenScreenData(document, selected == null ? Set.of() : Set.of(selected)));
            return document.rows().size();
        }
        source.sendSuccess(() -> selected == null
                ? Component.translatable("command.saturn.information.all", target.getDisplayName(), document.rows().size())
                : Component.translatable("command.saturn.information.group", target.getDisplayName(), selected.value().title(), document.rows().size()), false);
        if (document.rows().isEmpty()) source.sendSuccess(() -> Component.translatable("info.saturn.empty"), false);
        for (InformationEntry row : document.rows())
            for (Component line : InformationText.lines(row)) source.sendSuccess(() -> line, false);
        return document.rows().size();
    }
}
