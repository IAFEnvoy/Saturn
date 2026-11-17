package com.iafenvoy.saturn.screen.wheel;

import com.iafenvoy.saturn.runtime.wheel.WheelLayout;

public record WheelConfigurationLayout(int columns, int left, int top, int bottom, int slotY, int slotStep,
                                       boolean compact) {
    public static final int CELL = 22;
    public static final int STEP = 24;

    public static WheelConfigurationLayout of(int width, int height) {
        int columns = Math.clamp((width - 24) / STEP, 1, WheelLayout.SLOTS);
        int rows = (WheelLayout.SLOTS + columns - 1) / columns;
        boolean compact = height < 108 + rows * 36;
        int slotStep = compact ? STEP : STEP + 12;
        int slotY = height - 34 - CELL - (rows - 1) * slotStep - (compact ? 0 : 12);
        int bottom = slotY - 14;
        int top = Math.min(compact ? 28 : 44, Math.max(0, bottom - CELL));
        return new WheelConfigurationLayout(columns, (width - columns * STEP + 2) / 2,
                top, Math.max(top, bottom), slotY, slotStep, compact);
    }

    public int x(int index) {
        return this.left + index % this.columns * STEP;
    }

    public int slotY(int index) {
        return this.slotY + index / this.columns * this.slotStep;
    }

    public int maximumScroll(int options) {
        return Math.max(0, (options + this.columns - 1) / this.columns * STEP - 2 - (this.bottom - this.top));
    }

    public int optionAt(double x, double y, int scroll, int options) {
        if (y < this.top || y >= this.bottom) return -1;
        return this.indexAt(x, y + scroll, this.top, STEP, options);
    }

    public int slotAt(double x, double y) {
        return this.indexAt(x, y, this.slotY, this.slotStep, WheelLayout.SLOTS);
    }

    private int indexAt(double x, double y, int originY, int rowStep, int count) {
        double dx = x - this.left, dy = y - originY;
        if (dx < 0 || dy < 0 || dx >= this.columns * STEP || dx % STEP >= CELL || dy % rowStep >= CELL) return -1;
        int index = (int) (dy / rowStep) * this.columns + (int) (dx / STEP);
        return index < count ? index : -1;
    }
}
