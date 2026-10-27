package com.iafenvoy.saturn.config;

/**
 * The client settings the resource-bar renderers read, answered from {@link SaturnConfig}'s client file. A dependent
 * mod that wants a screen of its own wraps these two questions; nothing here keeps a second copy of the value.
 */
public final class SaturnClientConfig {
    private SaturnClientConfig() {
    }

    /**
     * Whether the resource name is drawn beside each bar.
     */
    public static boolean showNames() {
        return SaturnConfig.SHOW_NAMES.get();
    }

    /**
     * Whether the two icon slots go in the middle of the fill rather than on its sides.
     */
    public static boolean iconsInCenter() {
        return SaturnConfig.ICONS_IN_CENTER.get();
    }
}
