package com.iafenvoy.saturn.network.payload;

import com.iafenvoy.saturn.Saturn;
import net.minecraft.network.RegistryFriendlyByteBuf;
import net.minecraft.network.codec.ByteBufCodecs;
import net.minecraft.network.codec.StreamCodec;
import net.minecraft.network.protocol.common.custom.CustomPacketPayload;
import net.minecraft.resources.Identifier;
import org.jspecify.annotations.NonNull;

import java.util.Optional;

// Both forms validate the source before the registered kind handles the request.
public record WheelActionC2SPayload(Identifier source, Identifier kind, Identifier id,
                                    Optional<Boolean> enabled) implements CustomPacketPayload {
    public static final Type<WheelActionC2SPayload> TYPE = new Type<>(Identifier.fromNamespaceAndPath(Saturn.MOD_ID, "wheel_action_c2s"));
    public static final StreamCodec<RegistryFriendlyByteBuf, WheelActionC2SPayload> STREAM_CODEC = StreamCodec.composite(
            Identifier.STREAM_CODEC, WheelActionC2SPayload::source,
            Identifier.STREAM_CODEC, WheelActionC2SPayload::kind,
            Identifier.STREAM_CODEC, WheelActionC2SPayload::id,
            ByteBufCodecs.optional(ByteBufCodecs.BOOL), WheelActionC2SPayload::enabled,
            WheelActionC2SPayload::new);

    // What a cell press sends: the direction, if the entry has one, is read on the server.
    public static WheelActionC2SPayload press(Identifier source, Identifier kind, Identifier id) {
        return new WheelActionC2SPayload(source, kind, id, Optional.empty());
    }

    @Override
    public @NonNull Type<WheelActionC2SPayload> type() {
        return TYPE;
    }
}
