package com.iafenvoy.saturn.registry;

import net.minecraft.client.KeyMapping;
//? fabric {
import net.fabricmc.fabric.api.client.keymapping.v1.KeyMappingHelper;
 //?} else neoforge {
/*import net.neoforged.neoforge.client.event.RegisterKeyMappingsEvent;
 *///?}

public final class KeyMappingRegistry {
    //? neoforge {
    /*private static final ForgeLikeEventRegistrar<KeyMapping, Void> KEY_MAPPINGS = new ForgeLikeEventRegistrar<>(RegisterKeyMappingsEvent.class, (e, k, v) -> e.register(k));
    *///?}

    public static KeyMapping register(KeyMapping key) {
        //? fabric {
        return KeyMappingHelper.registerKeyMapping(key);
         //?} else {
        /*KEY_MAPPINGS.register(() -> key, null);
        return key;
        *///?}
    }
}
