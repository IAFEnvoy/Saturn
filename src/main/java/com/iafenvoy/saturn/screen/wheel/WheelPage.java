package com.iafenvoy.saturn.screen.wheel;

import com.iafenvoy.saturn.api.wheel.WheelMenuEntry;
import com.iafenvoy.saturn.api.wheel.WheelSource;
import org.jspecify.annotations.Nullable;

import java.util.List;

public record WheelPage(WheelSource source, List<@Nullable WheelMenuEntry> sectors, int shown) {
    public int hudShown(boolean configured) {
        return this.source.configured() && !configured ? 0 : this.shown;
    }
}
