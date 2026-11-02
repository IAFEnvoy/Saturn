package com.iafenvoy.saturn.view.model;

import com.iafenvoy.saturn.util.codec.ExtraEnumCodecs;
import com.mojang.serialization.Codec;

public enum ValueOrigin {
    CAPTURED, SAVED, RECONSTRUCTED;

    public static final Codec<ValueOrigin> CODEC = ExtraEnumCodecs.enumCodec(ValueOrigin.class);
}
