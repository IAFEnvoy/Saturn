package com.iafenvoy.saturn.registry;

import com.iafenvoy.saturn.Saturn;
import com.iafenvoy.saturn.screen.information.InformationOpenScreenData;
import com.iafenvoy.saturn.screen.opener.OpenScreenData;
import net.minecraft.network.RegistryFriendlyByteBuf;
import net.minecraft.network.codec.StreamCodec;
import net.neoforged.neoforge.registries.DeferredHolder;
import net.neoforged.neoforge.registries.DeferredRegister;

@SuppressWarnings("unused")
public final class SaturnOpenScreenData {
    public static final DeferredRegister<StreamCodec<RegistryFriendlyByteBuf, ? extends OpenScreenData>> REGISTRY = DeferredRegister.create(SaturnRegistries.OPEN_SCREEN_DATA, Saturn.MOD_ID);

    public static final DeferredHolder<StreamCodec<RegistryFriendlyByteBuf, ? extends OpenScreenData>, StreamCodec<RegistryFriendlyByteBuf, InformationOpenScreenData>> INFORMATION = REGISTRY.register("information", () -> InformationOpenScreenData.STREAM_CODEC);
}
