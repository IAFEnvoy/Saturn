package com.iafenvoy.saturn.view.model;

import com.iafenvoy.saturn.util.codec.ExtraEnumCodecs;
import com.mojang.serialization.Codec;
import net.minecraft.network.chat.Component;

import java.util.Locale;

public enum MissingReason {
    NOT_CAPTURED, NOT_REQUESTED, UNSUPPORTED, REDACTED, INVALID, NOT_APPLICABLE;

    public static final Codec<MissingReason> CODEC = ExtraEnumCodecs.enumCodec(MissingReason.class);

    public Component text() {
        return Component.translatable("info.saturn.missing." + this.name().toLowerCase(Locale.ROOT));
    }
}
