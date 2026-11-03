package com.iafenvoy.saturn.screen.opener;

import net.minecraft.network.RegistryFriendlyByteBuf;
import net.minecraft.network.codec.StreamCodec;

public interface OpenScreenData {
    StreamCodec<RegistryFriendlyByteBuf, ? extends OpenScreenData> streamCodec();
}
