package com.iafenvoy.saturn.config;

import net.neoforged.neoforge.common.ModConfigSpec;

/**
 * The library's own settings: which commands are also registered at the top level, and how the resource bars draw.
 *
 * <p>Saturn ships no config screen on purpose - those belong to the mods that depend on it - so both files are plain
 * TOML under {@code config/} ({@code saturn-common.toml} and {@code saturn-client.toml}). A dependent mod may read a value or wrap its own screen around it, and it keeps
 * its own file.
 */
public final class SaturnConfig {
    public static final ModConfigSpec COMMON_SPEC;
    public static final ModConfigSpec.BooleanValue PROFILER_ROOT;
    public static final ModConfigSpec.BooleanValue ABILITY_ROOT;
    public static final ModConfigSpec.BooleanValue TRIGGERS_ROOT;
    public static final ModConfigSpec.BooleanValue RESOURCE_ROOT;
    public static final ModConfigSpec.BooleanValue REGISTRIES_ROOT;
    public static final ModConfigSpec.BooleanValue ATTACHMENT_ROOT;

    public static final ModConfigSpec CLIENT_SPEC;
    public static final ModConfigSpec.BooleanValue SHOW_NAMES;
    public static final ModConfigSpec.BooleanValue ICONS_IN_CENTER;

    static {
        ModConfigSpec.Builder common = new ModConfigSpec.Builder();
        common.comment("Whether each node is also registered at the top level, next to /saturn.",
                        "A literal another mod already owns at that level is skipped, with one log line.")
                .push("commands");
        PROFILER_ROOT = root(common, "profiler");
        ABILITY_ROOT = root(common, "ability");
        TRIGGERS_ROOT = root(common, "triggers");
        RESOURCE_ROOT = root(common, "resource");
        REGISTRIES_ROOT = root(common, "registries");
        ATTACHMENT_ROOT = root(common, "attachment");
        common.pop();
        COMMON_SPEC = common.build();

        ModConfigSpec.Builder client = new ModConfigSpec.Builder();
        client.comment("How the resource bars are drawn.").push("resource_bars");
        SHOW_NAMES = client.comment("Draw the resource name beside each bar.")
                .define("show_names", true);
        ICONS_IN_CENTER = client.comment("Draw the two icon slots in the middle of the fill instead of on its sides.")
                .define("icons_in_center", false);
        client.pop();
        CLIENT_SPEC = client.build();
    }

    private SaturnConfig() {
    }

    private static ModConfigSpec.BooleanValue root(ModConfigSpec.Builder builder, String command) {
        return builder.comment("Register /" + command + " as well as /saturn " + command + ".")
                .define(command, true);
    }
}
