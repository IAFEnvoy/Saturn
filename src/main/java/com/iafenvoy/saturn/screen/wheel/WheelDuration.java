package com.iafenvoy.saturn.screen.wheel;

import net.minecraft.network.chat.Component;

import java.util.Locale;

public final class WheelDuration {
    private static final double TICKS_PER_SECOND = 20.0D;

    private WheelDuration() {
    }

    // A non-finite value is written as zero rather than as NaN seconds.
    public static Component seconds(double ticks) {
        double seconds = Double.isFinite(ticks) ? ticks / TICKS_PER_SECOND : 0.0D;
        return Component.translatable("wheel.saturn.tooltip.seconds",
                Component.literal(String.format(Locale.ROOT, "%.1f", seconds)));
    }
}
