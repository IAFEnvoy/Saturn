package com.iafenvoy.saturn.command;

import com.iafenvoy.saturn.data.trigger.TriggerContext;
import com.iafenvoy.saturn.data.trigger.TriggerRule;
import com.iafenvoy.saturn.registry.SaturnRegistries;
import com.iafenvoy.saturn.registry.cache.RegistryCaches;
import com.iafenvoy.saturn.runtime.trigger.TriggerDispatcher;
import com.iafenvoy.saturn.runtime.trigger.TriggerSubscription;
import com.iafenvoy.saturn.util.HolderHelper;
import com.iafenvoy.saturn.util.formula.FormulaContext;
import com.mojang.brigadier.builder.LiteralArgumentBuilder;
import com.mojang.brigadier.exceptions.CommandSyntaxException;
import com.mojang.brigadier.suggestion.Suggestions;
import com.mojang.brigadier.suggestion.SuggestionsBuilder;
import net.minecraft.commands.CommandBuildContext;
import net.minecraft.commands.CommandSourceStack;
import net.minecraft.commands.SharedSuggestionProvider;
import net.minecraft.commands.arguments.EntityArgument;
import net.minecraft.commands.arguments.IdentifierArgument;
import net.minecraft.core.Holder;
import net.minecraft.network.chat.Component;
import net.minecraft.resources.Identifier;
import net.minecraft.world.entity.Entity;

import java.util.LinkedHashSet;
import java.util.List;
import java.util.Set;
import java.util.concurrent.CompletableFuture;

import static net.minecraft.commands.Commands.argument;
import static net.minecraft.commands.Commands.literal;

/**
 * {@code /saturn triggers ...}: what a running server has armed, the rules a signal reaches, and a way to publish
 * one signal by hand.
 *
 * <p>Subscriptions are never persisted, so this is the only way to see what is listening.
 */
public final class SaturnTriggersCommand {
    private SaturnTriggersCommand() {
    }

    public static LiteralArgumentBuilder<CommandSourceStack> node(CommandBuildContext context) {
        return literal("triggers")
                .then(literal("list")
                        .executes(ctx -> listTriggers(ctx.getSource(), null))
                        .then(argument("entity", EntityArgument.entity())
                                .executes(ctx -> listTriggers(ctx.getSource(), EntityArgument.getEntity(ctx, "entity")))))
                .then(literal("rules")
                        .then(argument("signal", IdentifierArgument.id())
                                .suggests((ctx, builder) -> suggestRuleSignals(builder))
                                .executes(ctx -> listTriggerRules(ctx.getSource(), IdentifierArgument.getId(ctx, "signal")))))
                .then(literal("publish").requires(SaturnCommands::mayChange)
                        .then(argument("signal", IdentifierArgument.id())
                                .suggests((ctx, builder) -> suggestPublishedSignals(builder))
                                .executes(ctx -> publishTrigger(ctx.getSource(), IdentifierArgument.getId(ctx, "signal"), null))
                                .then(argument("entity", EntityArgument.entity())
                                        .executes(ctx -> publishTrigger(ctx.getSource(), IdentifierArgument.getId(ctx, "signal"),
                                                EntityArgument.getEntity(ctx, "entity"))))));
    }

    private static CompletableFuture<Suggestions> suggestRuleSignals(SuggestionsBuilder builder) {
        return SharedSuggestionProvider.suggest(RegistryCaches.TRIGGER_RULES_BY_SIGNAL.suggestions(), builder);
    }

    // Every signal a rule reacts to or a subscription listens to: the two sets a signal can be published against.
    private static CompletableFuture<Suggestions> suggestPublishedSignals(SuggestionsBuilder builder) {
        Set<String> ids = new LinkedHashSet<>();
        RegistryCaches.TRIGGER_RULES_BY_SIGNAL.suggestions().forEach(ids::add);
        TriggerDispatcher.signals().forEach(signal -> ids.add(signal.toString()));
        return SharedSuggestionProvider.suggest(ids, builder);
    }

    private static int listTriggers(CommandSourceStack source, Entity target) throws CommandSyntaxException {
        Entity entity = target == null ? source.getEntityOrException() : target;
        List<TriggerSubscription> subscriptions = TriggerDispatcher.subscriptions(entity.getUUID());
        source.sendSuccess(() -> Component.translatable("command.saturn.trigger.list.header",
                entity.getDisplayName(), subscriptions.size()), false);
        subscriptions.forEach(subscription -> source.sendSuccess(() -> Component.translatable("command.saturn.trigger.list.entry",
                subscription.module(), subscription.identity(), subscription.trigger().signalType().toString(),
                subscription.state().name()), false));
        return subscriptions.size();
    }

    private static int listTriggerRules(CommandSourceStack source, Identifier signal) {
        List<Holder<TriggerRule>> rules = RegistryCaches.TRIGGER_RULES_BY_SIGNAL.get(signal);
        source.sendSuccess(() -> Component.translatable("command.saturn.trigger.rules.header", signal.toString(), rules.size()), false);
        rules.forEach(rule -> source.sendSuccess(() -> Component.translatable("command.saturn.trigger.rules.entry",
                HolderHelper.id(rule).toString(),
                String.valueOf(SaturnRegistries.ENTITY_ACTION_TYPE.getKey(rule.value().action().codec()))), false));
        return rules.size();
    }

    private static int publishTrigger(CommandSourceStack source, Identifier signal, Entity target) throws CommandSyntaxException {
        Entity entity = target == null ? source.getEntityOrException() : target;
        boolean heard = TriggerDispatcher.hasListener(signal);
        TriggerDispatcher.publish(signal, new TriggerContext().actor(entity).level(entity.level())
                .formula(FormulaContext.of(entity)), entity.level().getGameTime());
        source.sendSuccess(() -> Component.translatable(heard ? "command.saturn.trigger.published"
                : "command.saturn.trigger.published_unheard", signal.toString(), entity.getDisplayName()), true);
        return 1;
    }
}
