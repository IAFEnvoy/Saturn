package com.iafenvoy.saturn.compat.kubejs.callback;

import com.iafenvoy.saturn.compat.kubejs.SaturnJsWarnings;

/**
 * Coordinates KubeJS callback invalidation during script reloads.
 */
public final class SaturnJsCallbacks {
    private SaturnJsCallbacks() {
    }

    public static void clear() {
        SaturnJsActionCallbacks.clear();
        SaturnJsConditionCallbacks.clear();
        SaturnJsValueCallbacks.clear();
        SaturnJsTriggerMatchers.clear();
        SaturnJsCostCallbacks.clear();
        SaturnJsSelectorCallbacks.clear();
        SaturnJsWarnings.clear();
    }
}
