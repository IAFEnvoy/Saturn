package com.iafenvoy.saturn.command.server;

import com.iafenvoy.saturn.attachment.ResourceHolderAttachment;
import com.iafenvoy.saturn.data.resource.Resource;
import com.iafenvoy.saturn.registry.SaturnAttachments;
import com.iafenvoy.saturn.registry.SaturnDatapackRegistries;
import com.iafenvoy.saturn.registry.SaturnResourceKeys;
import com.iafenvoy.saturn.util.HolderHelper;
import com.iafenvoy.saturn.util.formula.FormulaContext;
import com.mojang.brigadier.arguments.DoubleArgumentType;
import com.mojang.brigadier.builder.LiteralArgumentBuilder;
import net.minecraft.commands.CommandSourceStack;
import net.minecraft.commands.arguments.EntityArgument;
import net.minecraft.commands.arguments.IdentifierArgument;
import net.minecraft.core.Holder;
import net.minecraft.network.chat.Component;
import net.minecraft.resources.Identifier;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.server.permissions.Permissions;
import net.minecraft.util.Mth;
import net.minecraft.world.entity.LivingEntity;

import java.util.Collection;
import java.util.Locale;

import static net.minecraft.commands.Commands.argument;
import static net.minecraft.commands.Commands.literal;

/**
 * {@code /saturn resource ...}: reading and writing a body's numeric resources, which are {@link ResourceHolderAttachment}
 * values and therefore Saturn's own state.
 *
 * <p>No aura node: a pool or a bank is a store of a dependent mod, and so is the command that moves it.
 */
public final class ResourceCommand {
    private ResourceCommand() {
    }

    public static LiteralArgumentBuilder<CommandSourceStack> node() {
        return literal("resource")
                .then(literal("get").then(argument("resource", IdentifierArgument.id())
                        .executes(ctx -> get(ctx.getSource(), IdentifierArgument.getId(ctx, "resource")))))
                .then(literal("list").executes(ctx -> list(ctx.getSource())))
                .then(literal("set").requires(source -> source.permissions().hasPermission(Permissions.COMMANDS_GAMEMASTER))
                        .then(argument("targets", EntityArgument.players())
                                .then(argument("resource", IdentifierArgument.id())
                                        .then(argument("amount", DoubleArgumentType.doubleArg(0.0D))
                                                .executes(ctx -> write(ctx.getSource(), EntityArgument.getPlayers(ctx, "targets"),
                                                        IdentifierArgument.getId(ctx, "resource"),
                                                        DoubleArgumentType.getDouble(ctx, "amount"), false))))))
                .then(literal("add").requires(source -> source.permissions().hasPermission(Permissions.COMMANDS_GAMEMASTER))
                        .then(argument("targets", EntityArgument.players())
                                .then(argument("resource", IdentifierArgument.id())
                                        .then(argument("amount", DoubleArgumentType.doubleArg())
                                                .executes(ctx -> write(ctx.getSource(), EntityArgument.getPlayers(ctx, "targets"),
                                                        IdentifierArgument.getId(ctx, "resource"),
                                                        DoubleArgumentType.getDouble(ctx, "amount"), true))))));
    }

    private static int get(CommandSourceStack source, Identifier id) {
        if (!(source.getEntity() instanceof LivingEntity entity)) {
            source.sendFailure(Component.translatable("command.saturn.resource.no_player"));
            return 0;
        }
        Holder<Resource> resource = SaturnDatapackRegistries.holder(source.registryAccess(), SaturnResourceKeys.RESOURCE, id).orElse(null);
        if (resource == null) {
            source.sendFailure(Component.translatable("command.saturn.resource.unknown", id.toString()));
            return 0;
        }
        double value = entity.getData(SaturnAttachments.RESOURCE_HOLDER).get(resource);
        source.sendSuccess(() -> Component.translatable("command.saturn.resource.get", entity.getDisplayName(),
                id.toString(), format(value)), false);
        return 1;
    }

    private static int list(CommandSourceStack source) {
        if (!(source.getEntity() instanceof LivingEntity entity)) {
            source.sendFailure(Component.translatable("command.saturn.resource.no_player"));
            return 0;
        }
        ResourceHolderAttachment holder = entity.getData(SaturnAttachments.RESOURCE_HOLDER);
        StringBuilder text = new StringBuilder();
        holder.values().object2DoubleEntrySet().forEach(entry -> {
            if (!text.isEmpty()) text.append(", ");
            text.append(HolderHelper.id(entry.getKey())).append('=').append(format(entry.getDoubleValue()));
        });
        source.sendSuccess(() -> Component.translatable("command.saturn.resource.list", entity.getDisplayName(),
                text.isEmpty() ? "-" : text.toString()), false);
        return holder.values().size();
    }

    private static int write(CommandSourceStack source, Collection<ServerPlayer> targets,
                             Identifier id, double amount, boolean add) {
        Holder<Resource> resource = SaturnDatapackRegistries.holder(source.registryAccess(), SaturnResourceKeys.RESOURCE, id).orElse(null);
        if (resource == null) {
            source.sendFailure(Component.translatable("command.saturn.resource.unknown", id.toString()));
            return 0;
        }
        int changed = 0;
        for (LivingEntity entity : targets) {
            FormulaContext formula = FormulaContext.of(entity);
            Resource definition = resource.value();
            double minimum = definition.min().evaluate(formula);
            double maximum = definition.max().evaluate(formula);
            ResourceHolderAttachment holder = entity.getData(SaturnAttachments.RESOURCE_HOLDER);
            double value = add ? holder.get(resource) + amount : amount;
            double clamped = Mth.clamp(value, Math.min(minimum, maximum), maximum);
            holder.set(resource, clamped);
            source.sendSuccess(() -> Component.translatable("command.saturn.resource.set", entity.getDisplayName(),
                    id.toString(), format(clamped)), true);
            changed++;
        }
        return changed;
    }

    private static String format(double value) {
        return String.format(Locale.ROOT, "%.2f", value);
    }
}
