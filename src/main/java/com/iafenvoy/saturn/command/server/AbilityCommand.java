package com.iafenvoy.saturn.command.server;

import com.iafenvoy.saturn.Saturn;
import com.iafenvoy.saturn.attachment.AbilityAttachment;
import com.iafenvoy.saturn.command.ServerCommandManager;
import com.iafenvoy.saturn.data.ability.Ability;
import com.iafenvoy.saturn.registry.SaturnAttachments;
import com.iafenvoy.saturn.registry.SaturnResourceKeys;
import com.iafenvoy.saturn.runtime.ability.AbilityEventBridge;
import com.iafenvoy.saturn.runtime.ability.AbilityService;
import com.iafenvoy.saturn.runtime.ability.AbilityService.UseResult;
import com.iafenvoy.saturn.util.DefinitionText;
import com.iafenvoy.saturn.util.formula.FormulaContext;
import com.mojang.brigadier.builder.LiteralArgumentBuilder;
import com.mojang.brigadier.context.CommandContext;
import com.mojang.brigadier.exceptions.CommandSyntaxException;
import net.minecraft.commands.CommandBuildContext;
import net.minecraft.commands.CommandSourceStack;
import net.minecraft.commands.arguments.EntityArgument;
import net.minecraft.commands.arguments.ResourceArgument;
import net.minecraft.core.Holder;
import net.minecraft.core.Holder.Reference;
import net.minecraft.network.chat.Component;
import net.minecraft.resources.Identifier;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.LivingEntity;
import org.jetbrains.annotations.NotNull;

import java.util.Collection;

import static net.minecraft.commands.Commands.argument;
import static net.minecraft.commands.Commands.literal;

/**
 * {@code /saturn ability ...}: what a body holds, granting and revoking under the command's own source, and casting
 * one ability by id.
 *
 * <p>Granting and revoking go through the same source ledger content uses, under the command's own source, so an
 * operator cannot take away what a quest reward or a piece of gear granted.
 */
public final class AbilityCommand {
    // Recorded as the source of what the command grants, in the same shape the other modules use.
    private static final Identifier SOURCE = Identifier.fromNamespaceAndPath(Saturn.MOD_ID, "command");

    private AbilityCommand() {
    }

    public static LiteralArgumentBuilder<CommandSourceStack> node(CommandBuildContext context) {
        return literal("ability")
                .then(literal("list")
                        .executes(ctx -> list(ctx.getSource(), ctx.getSource().getPlayerOrException()))
                        .then(argument("target", EntityArgument.entity())
                                .executes(ctx -> list(ctx.getSource(), EntityArgument.getEntity(ctx, "target")))))
                .then(literal("cast")
                        .requires(ServerCommandManager::mayChange)
                        .then(argument("id", ResourceArgument.resource(context, SaturnResourceKeys.ABILITY))
                                .executes(ctx -> castAbility(ctx.getSource(), id(ctx)))))
                .then(literal("grant")
                        .requires(ServerCommandManager::mayChange)
                        .then(argument("targets", EntityArgument.entities())
                                .then(argument("ability", ResourceArgument.resource(context, SaturnResourceKeys.ABILITY))
                                        .executes(AbilityCommand::grant))))
                .then(literal("revoke")
                        .requires(ServerCommandManager::mayChange)
                        .then(argument("targets", EntityArgument.entities())
                                .then(argument("ability", ResourceArgument.resource(context, SaturnResourceKeys.ABILITY))
                                        .executes(AbilityCommand::revoke))));
    }

    private static Reference<Ability> id(CommandContext<CommandSourceStack> ctx) throws CommandSyntaxException {
        return ResourceArgument.getResource(ctx, "id", SaturnResourceKeys.ABILITY);
    }

    // Read from the attachment rather than the registry, so what the ledger holds is what is reported. The ledger is
    // keyed by the ability holder, so an ability the pack no longer provides is not in it to be listed.
    private static int list(CommandSourceStack source, @NotNull Entity target) {
        AbilityAttachment abilities = target.getData(SaturnAttachments.ABILITY_HOLDER);
        source.sendSuccess(() -> Component.translatable("command.saturn.ability.list.header", target.getDisplayName()), false);
        if (abilities.sources().keys().isEmpty()) {
            source.sendSuccess(() -> Component.translatable("command.saturn.ability.list.empty"), false);
            return 1;
        }
        for (Holder<Ability> ability : abilities.sources().keys()) {
            Component name = DefinitionText.nameOrId(ability);
            Component from = Component.literal(abilities.sources().of(ability).stream()
                    .map(Identifier::toString).sorted().reduce((a, b) -> a + ", " + b).orElse("-"));
            source.sendSuccess(() -> Component.translatable("command.saturn.ability.list.line", name, from), false);
        }
        return 1;
    }

    private static int grant(CommandContext<CommandSourceStack> ctx) throws CommandSyntaxException {
        CommandSourceStack source = ctx.getSource();
        Reference<Ability> ability = ResourceArgument.getResource(ctx, "ability", SaturnResourceKeys.ABILITY);
        Collection<? extends Entity> targets = EntityArgument.getEntities(ctx, "targets");
        int granted = 0;
        for (Entity target : targets) {
            AbilityAttachment abilities = target.getData(SaturnAttachments.ABILITY_HOLDER);
            if (abilities.grant(ability, SOURCE)) {
                granted++;
                rebuild(target);
                source.sendSuccess(() -> Component.translatable("command.saturn.ability.granted",
                        DefinitionText.nameOrId(ability), target.getDisplayName()), true);
            } else {
                source.sendFailure(Component.translatable("command.saturn.ability.grant_failed",
                        target.getDisplayName(), DefinitionText.nameOrId(ability)));
            }
        }
        return granted;
    }

    // Dropping a source only removes the ability when that was its last one, so a target that never held the
    // source is a failure rather than a silent success.
    private static int revoke(CommandContext<CommandSourceStack> ctx) throws CommandSyntaxException {
        CommandSourceStack source = ctx.getSource();
        Holder<Ability> ability = ResourceArgument.getResource(ctx, "ability", SaturnResourceKeys.ABILITY);
        Collection<? extends Entity> targets = EntityArgument.getEntities(ctx, "targets");
        int revoked = 0;
        for (Entity target : targets) {
            if (target.getData(SaturnAttachments.ABILITY_HOLDER).revoke(ability, SOURCE)) {
                revoked++;
                rebuild(target);
                source.sendSuccess(() -> Component.translatable("command.saturn.ability.revoked",
                        DefinitionText.nameOrId(ability), target.getDisplayName()), true);
            } else {
                source.sendFailure(Component.translatable("command.saturn.ability.revoke_failed",
                        target.getDisplayName(), DefinitionText.nameOrId(ability)));
            }
        }
        return revoked;
    }

    // A source change moves which triggers the entity listens for.
    private static void rebuild(Entity target) {
        if (target instanceof LivingEntity living) AbilityEventBridge.rebuildTriggerSubscriptions(living);
    }

    private static int castAbility(CommandSourceStack source, Reference<Ability> ability) throws CommandSyntaxException {
        ServerPlayer player = source.getPlayerOrException();
        UseResult result = AbilityService.use(ability, player, player.getData(SaturnAttachments.ABILITY_HOLDER),
                player.getData(SaturnAttachments.RESOURCE_HOLDER), player.level().getGameTime(), FormulaContext.of(player));
        // An accepted cast is a success too: it commits when the cast time runs out, so there is no failure to
        // name here - reading one would be a null pointer for every skill that has a cast time.
        if (result.failure() != null) {
            source.sendFailure(Component.translatable("command.saturn.ability.cast_failed", result.failure().message()));
            return 0;
        }
        source.sendSuccess(() -> Component.translatable("command.saturn.ability.cast_success", DefinitionText.nameOrId(ability)), true);
        return 1;
    }
}
