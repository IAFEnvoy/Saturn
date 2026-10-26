package com.iafenvoy.saturn.compat.kubejs;

import com.iafenvoy.saturn.Saturn;
import com.iafenvoy.saturn.compat.kubejs.binding.*;
import com.iafenvoy.saturn.compat.kubejs.callback.SaturnJsCallbacks;
import dev.latvian.mods.kubejs.plugin.KubeJSPlugin;
import dev.latvian.mods.kubejs.script.BindingRegistry;
import dev.latvian.mods.kubejs.script.ScriptManager;
import dev.latvian.mods.kubejs.script.ScriptType;

/**
 * KubeJS entry point, discovered through {@code kubejs.plugins.txt}. Only loaded when KubeJS is installed, which is
 * why the whole {@code compat.kubejs} package is safe to ship in a mod that does not depend on it.
 *
 * <p>What is bound here is the vocabulary Saturn owns: how to build an action, a condition, a cost or a value
 * provider from a script, and how to pay a costs array. Triggers are not bound: publishing a signal and reading a
 * subscription is the dependent mod's runtime, so that binding lives there.
 */
public final class SaturnKubeJsPlugin implements KubeJSPlugin {
    @Override
    public void init() {
        Saturn.LOGGER.info("Saturn KubeJS bridge initialized");
    }

    @Override
    public void registerBindings(BindingRegistry registry) {
        registry.add("SaturnActions", new SaturnKubeJsActionBindings());
        registry.add("SaturnConditions", new SaturnKubeJsConditionBindings());
        registry.add("SaturnCosts", new SaturnKubeJsCostBindings());
        registry.add("SaturnValues", new SaturnKubeJsValueBindings());
        registry.add("SaturnResources", new SaturnKubeJsResourceBindings());
    }

    // A reload re-evaluates the scripts, so a fixed bridge function must be allowed to report again.
    @Override
    public void beforeScriptsLoaded(ScriptManager manager) {
        if (manager.scriptType == ScriptType.SERVER) SaturnJsCallbacks.clear();
    }
}
