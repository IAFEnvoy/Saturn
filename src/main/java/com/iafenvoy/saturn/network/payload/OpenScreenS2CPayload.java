package com.iafenvoy.saturn.network.payload;

import com.iafenvoy.saturn.Saturn;
import com.iafenvoy.saturn.registry.SaturnResourceKeys;
import com.iafenvoy.saturn.screen.opener.OpenScreenData;
import net.minecraft.network.RegistryFriendlyByteBuf;
import net.minecraft.network.codec.ByteBufCodecs;
import net.minecraft.network.codec.StreamCodec;
import net.minecraft.network.protocol.common.custom.CustomPacketPayload;
import net.minecraft.resources.Identifier;
import org.jspecify.annotations.NonNull;

import java.util.function.Function;

public record OpenScreenS2CPayload(OpenScreenData data) implements CustomPacketPayload {
    public static final Type<OpenScreenS2CPayload> TYPE = new Type<>(Identifier.fromNamespaceAndPath(Saturn.MOD_ID, "open_screen_s2c"));
    public static final StreamCodec<RegistryFriendlyByteBuf, OpenScreenS2CPayload> STREAM_CODEC = ByteBufCodecs.registry(SaturnResourceKeys.OPEN_SCREEN_DATA)
            .dispatch(OpenScreenData::streamCodec, Function.identity()).map(OpenScreenS2CPayload::new, OpenScreenS2CPayload::data);

    @Override
    public @NonNull Type<? extends CustomPacketPayload> type() {
        return TYPE;
    }
}
