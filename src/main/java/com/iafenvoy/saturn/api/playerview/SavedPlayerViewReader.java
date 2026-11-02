package com.iafenvoy.saturn.api.playerview;

import com.iafenvoy.saturn.view.model.SavedPlayerViewContext;
import net.minecraft.resources.Identifier;

import java.util.Set;

/** Imports saved facts without constructing a Player. Readers may only fill fields absent from a recorded view. */
public interface SavedPlayerViewReader {
    Identifier id();

    Set<PlayerViewField<?>> fields();

    void read(SavedPlayerViewContext context, PlayerViewWriter writer);
}
