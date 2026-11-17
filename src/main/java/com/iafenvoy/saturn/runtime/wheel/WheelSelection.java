package com.iafenvoy.saturn.runtime.wheel;

import com.iafenvoy.saturn.api.wheel.WheelMenuEntry;
import com.iafenvoy.saturn.api.wheel.WheelSource;

public record WheelSelection(WheelSource source, int number, WheelMenuEntry entry, Method method) {
    public enum Method {
        // While the wheel is open the pointed cell, while it is closed the remembered one.
        KEY,
        // The left mouse button was clicked while the wheel was open.
        CLICK
    }
}