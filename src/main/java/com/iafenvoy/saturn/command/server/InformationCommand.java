package com.iafenvoy.saturn.command.server;

import com.iafenvoy.saturn.command.ServerCommandManager;
import com.iafenvoy.saturn.data.information.*;
import com.iafenvoy.saturn.data.information.model.InfomationGroup;
import com.iafenvoy.saturn.data.information.model.InformationContext;
import com.iafenvoy.saturn.data.information.model.InformationDocument;
import com.iafenvoy.saturn.data.information.model.InformationRowResult;
import com.iafenvoy.saturn.registry.SaturnResourceKeys;
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
import net.minecraft.world.entity.Entity;

import java.util.Optional;
import java.util.stream.Stream;

import static net.minecraft.commands.Commands.argument;
import static net.minecraft.commands.Commands.literal;

/**
 * Operator diagnostic for the same getter/expression results used by screens and cards.
 */
public final class InformationCommand {
    private static final DynamicCommandExceptionType UNKNOWN_GROUP = new DynamicCommandExceptionType(
            id -> Component.translatable("command.saturn.information.unknown_group", id.toString()));

    private InformationCommand() {
    }

    public static LiteralArgumentBuilder<CommandSourceStack> node() {
        return literal("information").requires(ServerCommandManager::mayChange)
                .then(argument("target", EntityArgument.entity())
                        .executes(ctx -> show(ctx.getSource(), EntityArgument.getEntity(ctx, "target"), Optional.empty()))
                        .then(argument("group", IdentifierArgument.id())
                                .suggests((ctx, builder) -> SharedSuggestionProvider.suggestResource(groupIds(ctx.getSource()), builder))
                                .executes(ctx -> show(ctx.getSource(), EntityArgument.getEntity(ctx, "target"),
                                        Optional.of(IdentifierArgument.getId(ctx, "group"))))));
    }

    private static Stream<Identifier> groupIds(CommandSourceStack source) {
        return Stream.concat(source.registryAccess().lookupOrThrow(SaturnResourceKeys.INFORMATION_GROUP).listElementIds()
                        .map(ResourceKey::identifier), PlayerInformation.groups().stream().map(InfomationGroup::id))
                .distinct().sorted();
    }

    private static InfomationGroup group(CommandSourceStack source, Identifier id) throws CommandSyntaxException {
        Optional<Holder.Reference<InformationGroup>> definition = source.registryAccess().lookupOrThrow(SaturnResourceKeys.INFORMATION_GROUP)
                .get(ResourceKey.create(SaturnResourceKeys.INFORMATION_GROUP, id));
        if (definition.isPresent()) return definition.get().value().resolve(id);
        return PlayerInformation.groups().stream().filter(group -> group.id().equals(id)).findFirst()
                .orElseThrow(() -> UNKNOWN_GROUP.create(id));
    }

    private static int show(CommandSourceStack source, Entity target, Optional<Identifier> groupId) throws CommandSyntaxException {
        InfomationGroup selected = groupId.isPresent() ? group(source, groupId.get()) : null;
        PlayerViewSnapshot view = PlayerViews.parse(target);
        InformationContext context = new InformationContext("en_us", source.registryAccess());
        InformationDocument document = selected == null ? PlayerInformation.collectAll(view, context)
                : PlayerInformation.collect(view, context, selected);
        source.sendSuccess(() -> selected == null
                ? Component.translatable("command.saturn.information.all", target.getDisplayName(), document.rows().size())
                : Component.translatable("command.saturn.information.group", target.getDisplayName(), selected.title(), document.rows().size()), false);
        if (document.rows().isEmpty()) source.sendSuccess(() -> Component.translatable("info.saturn.empty"), false);
        for (InformationRowResult row : document.rows())
            for (Component line : InformationText.lines(row.content())) source.sendSuccess(() -> line, false);
        return document.rows().size();
    }
}
