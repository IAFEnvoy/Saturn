package com.iafenvoy.saturn.compat.kubejs.callback;

import com.google.gson.JsonObject;
import com.iafenvoy.saturn.compat.kubejs.SaturnJsWarnings;
import com.iafenvoy.saturn.data.trigger.TriggerSignal;
import com.iafenvoy.saturn.util.formula.FormulaContext;
import com.iafenvoy.saturn.util.function.TriPredicate;

import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

/**
 * Callback storage for the JavaScript trigger matcher type: a matcher only decides whether a signal that
 * already reached the owning subscription fires.
 */
public final class SaturnJsTriggerMatchers {
    private static final Map<String, TriPredicate<TriggerSignal, JsonObject, FormulaContext>> MATCHERS = new ConcurrentHashMap<>();

    private SaturnJsTriggerMatchers() {
    }

    public static void register(String id, TriPredicate<TriggerSignal, JsonObject, FormulaContext> callback) {
        MATCHERS.put(id, callback);
    }

    public static boolean matches(String id, TriggerSignal signal, JsonObject params) {
        TriPredicate<TriggerSignal, JsonObject, FormulaContext> callback = MATCHERS.get(id);
        if (callback == null) return unknown(id);
        try {
            return callback.test(signal, params, signal.context().formula());
        } catch (Exception exception) {
            return failed(id, exception);
        }
    }

    public static void clear() {
        MATCHERS.clear();
    }

    private static boolean unknown(String id) {
        SaturnJsWarnings.WARNING.log("Unknown KubeJS trigger '{}'; the trigger does not match", id);
        return false;
    }

    private static boolean failed(String id, Exception exception) {
        SaturnJsWarnings.FAILURE.log("KubeJS trigger '" + id + "' failed; the trigger does not match", exception);
        return false;
    }
}
