package com.iafenvoy.saturn.api.playerview;

import com.mojang.serialization.Codec;
import net.minecraft.resources.Identifier;

/** A registered, versioned field. Its codec owns validation and migrations of the payload. */
public interface PlayerViewField<T> {
    Identifier id();

    int schemaVersion();

    Codec<T> codec();
}
