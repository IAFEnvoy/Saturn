package com.iafenvoy.saturn.screen;

import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.world.MenuProvider;

public interface ExtendedMenuProvider extends MenuProvider {
    void writeExtraData(FriendlyByteBuf buf);
}
