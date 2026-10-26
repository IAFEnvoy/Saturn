package com.iafenvoy.saturn.compat.kubejs.callback;

import com.google.gson.JsonObject;
import com.iafenvoy.saturn.compat.kubejs.SaturnJsWarnings;
import com.iafenvoy.saturn.util.formula.FormulaContext;
import com.iafenvoy.saturn.util.function.TriFunction;
import net.minecraft.world.entity.Entity;

import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.concurrent.ConcurrentHashMap;

/**
 * Callback storage for the JavaScript ability target selector type. A callback returns the entities to
 * affect as an array; {@code null} entries are dropped, and a missing or failing callback selects nobody.
 */
public final class SaturnJsSelectorCallbacks {
    private static final Map<String, TriFunction<Entity, FormulaContext, JsonObject, List<Entity>>> SELECTORS = new ConcurrentHashMap<>();

    private SaturnJsSelectorCallbacks() {
    }

    public static void register(String id, TriFunction<Entity, FormulaContext, JsonObject, List<Entity>> callback) {
        SELECTORS.put(id, callback);
    }

    public static List<Entity> select(String id, Entity actor, FormulaContext context, JsonObject params) {
        TriFunction<Entity, FormulaContext, JsonObject, List<Entity>> callback = SELECTORS.get(id);
        if (callback == null) return unknown(id);
        try {
            List<Entity> selected = callback.apply(actor, context, params);
            if (selected == null) return List.of();
            return selected.stream().filter(Objects::nonNull).toList();
        } catch (Exception exception) {
            return failed(id, exception);
        }
    }

    public static void clear() {
        SELECTORS.clear();
    }

    private static List<Entity> unknown(String id) {
        SaturnJsWarnings.WARNING.log("Unknown KubeJS target selector '{}'; no entity is selected", id);
        return List.of();
    }

    private static List<Entity> failed(String id, Exception exception) {
        SaturnJsWarnings.FAILURE.log("KubeJS target selector '" + id + "' failed; no entity is selected", exception);
        return List.of();
    }
}
