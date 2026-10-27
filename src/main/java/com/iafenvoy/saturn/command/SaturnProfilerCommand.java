package com.iafenvoy.saturn.command;

import com.iafenvoy.saturn.profiling.Profiler;
import com.iafenvoy.saturn.profiling.ProfilerSource;
import com.iafenvoy.saturn.profiling.ProfilerWindowSnapshot;
import com.iafenvoy.saturn.profiling.ServerProfiling;
import com.iafenvoy.saturn.profiling.ServerProfiling.Source;
import com.mojang.brigadier.arguments.StringArgumentType;
import com.mojang.brigadier.builder.LiteralArgumentBuilder;
import net.minecraft.ChatFormatting;
import net.minecraft.commands.CommandSourceStack;
import net.minecraft.commands.SharedSuggestionProvider;
import net.minecraft.network.chat.Component;
import net.minecraft.server.permissions.Permissions;
import net.neoforged.fml.common.EventBusSubscriber;

import java.util.LinkedHashSet;
import java.util.List;
import java.util.Locale;

import static net.minecraft.commands.Commands.argument;
import static net.minecraft.commands.Commands.literal;

/**
 * {@code /saturn profiler ...}: turning the server profiler on and off and printing what it measured.
 *
 * <p>Commands are the only door Saturn offers to the profiler: there is no screen and no packet, so the numbers are
 * read where they are produced. A dependent mod that wants a graphical view of them reads
 * {@link ServerProfiling#snapshot} itself and sends whatever it likes over its own network.
 */
@EventBusSubscriber
public final class SaturnProfilerCommand {
    private SaturnProfilerCommand() {
    }

    public static LiteralArgumentBuilder<CommandSourceStack> node() {
        LiteralArgumentBuilder<CommandSourceStack> statusNode = literal("status")
                .executes(ctx -> status(ctx.getSource(), ""))
                .then(argument("sources", StringArgumentType.greedyString())
                        .suggests((_, builder) -> {
                            String remaining = builder.getRemaining();
                            int offset = remaining.lastIndexOf(' ') + 1;
                            return SharedSuggestionProvider.suggest(Source.all().stream().map(Source::path),
                                    builder.createOffset(builder.getStart() + offset));
                        })
                        .executes(ctx -> status(ctx.getSource(), StringArgumentType.getString(ctx, "sources"))));
        return literal("profiler").requires(SaturnProfilerCommand::mayView)
                .then(literal("on").executes(ctx -> toggle(ctx.getSource(), true)))
                .then(literal("off").executes(ctx -> toggle(ctx.getSource(), false)))
                .then(statusNode);
    }

    private static boolean mayView(CommandSourceStack source) {
        return source.permissions().hasPermission(Permissions.COMMANDS_ADMIN);
    }

    private static int toggle(CommandSourceStack source, boolean enabled) {
        if (!ServerProfiling.setEnabled(source.getServer(), enabled)) {
            source.sendFailure(Component.translatable("command.saturn.profiler.unavailable"));
            return 0;
        }
        source.sendSuccess(() -> Component.translatable(enabled ? "command.saturn.profiler.enabled" : "command.saturn.profiler.disabled"), true);
        return 1;
    }

    private static int status(CommandSourceStack source, String arguments) {
        LinkedHashSet<Source> selected = new LinkedHashSet<>();
        if (arguments.isBlank()) selected.addAll(Source.all());
        else for (String id : arguments.strip().split("\\s+")) {
            Source match = Source.byPath(id);
            if (match == null) {
                source.sendFailure(Component.translatable("command.saturn.profiler.unknown_source", id));
                return 0;
            }
            selected.add(match);
        }
        if (!ServerProfiling.enabled(source.getServer())) {
            source.sendFailure(Component.translatable("command.saturn.profiler.disabled"));
            return 0;
        }
        List<Source> sources = List.copyOf(selected);
        ServerProfiling.snapshot(source.getServer()).thenAccept(snapshot ->
                source.getServer().execute(() -> show(source, sources, snapshot)));
        return sources.size();
    }

    private static void show(CommandSourceStack source, List<Source> selected, ProfilerWindowSnapshot snapshot) {
        if (!mayView(source)) return;
        if (snapshot.producers().isEmpty()) {
            source.sendSuccess(() -> Component.translatable("command.saturn.profiler.no_data"), false);
            return;
        }
        source.sendSuccess(() -> Component.translatable("command.saturn.profiler.header", Profiler.WINDOW_TICKS, discarded(snapshot.droppedBatches())), false);
        for (ProfilerWindowSnapshot.Producer producer : snapshot.producers()) {
            source.sendSuccess(() -> Component.translatable("command.saturn.profiler.producer", producer.name(), number(producer.sampleCount()),
                    discarded(producer.discardedSpans())), false);
            for (Source selectedSource : selected) {
                ProfilerSource registered = snapshot.sources().stream()
                        .filter(value -> value.id().equals(selectedSource.id())).findFirst().orElse(null);
                if (registered == null) continue;
                ProfilerWindowSnapshot.Statistics stats = producer.statistics(registered);
                source.sendSuccess(() -> Component.translatable("command.saturn.profiler.source", selectedSource.path(),
                        millis(stats.minNanos()), millis(stats.averageNanos()), millis(stats.maxNanos()),
                        millis(stats.medianNanos()), millis(stats.p95Nanos()), millis(stats.p99Nanos()), number(stats.hits()), number(stats.samples())), false);
            }
        }
    }

    private static Component millis(double nanos) {
        return Component.literal(String.format(Locale.ROOT, "%.3f", nanos / 1_000_000.0D))
                .withStyle(nanos >= 50_000_000D ? ChatFormatting.RED : nanos >= 10_000_000D ? ChatFormatting.YELLOW : ChatFormatting.GREEN);
    }

    private static Component number(long value) {
        return Component.literal(Long.toString(value)).withStyle(ChatFormatting.AQUA);
    }

    private static Component discarded(long value) {
        return Component.literal(Long.toString(value)).withStyle(value == 0L ? ChatFormatting.GREEN : ChatFormatting.RED);
    }
}
