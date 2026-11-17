package com.iafenvoy.saturn.screen.wheel.content;

import com.iafenvoy.saturn.api.wheel.AbilityWheelTooltip;
import net.minecraft.resources.Identifier;

import java.util.List;
import java.util.Map;
import java.util.TreeMap;

public final class AbilityWheelTooltips {
    private static final Map<Identifier, AbilityWheelTooltip> PROVIDERS = new TreeMap<>();

    private AbilityWheelTooltips() {
    }

    public static synchronized void register(Identifier owner, AbilityWheelTooltip provider) {
        PROVIDERS.put(owner, provider);
    }

    static synchronized List<AbilityWheelTooltip> providers() {
        return List.copyOf(PROVIDERS.values());
    }
}
