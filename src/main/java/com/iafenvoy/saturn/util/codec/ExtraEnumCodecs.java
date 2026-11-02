package com.iafenvoy.saturn.util.codec;

import com.mojang.serialization.Codec;
import com.mojang.serialization.DataResult;
import net.minecraft.world.level.ClipContext;
import net.minecraft.world.level.LightLayer;

import java.util.Locale;

public final class ExtraEnumCodecs {
    public static final Codec<LightLayer> LIGHT_LAYER = enumCodec(LightLayer.class);
    public static final Codec<ClipContext.Block> CLIP_CONTEXT_BLOCK = enumCodec(ClipContext.Block.class);
    public static final Codec<ClipContext.Fluid> CLIP_CONTEXT_FLUID = enumCodec(ClipContext.Fluid.class);

    public static <T extends Enum<T>> Codec<T> enumCodec(Class<T> clazz) {
        return Codec.STRING.comapFlatMap(name -> {
            try {
                return DataResult.success(Enum.valueOf(clazz, name.toUpperCase(Locale.ROOT)));
            } catch (IllegalArgumentException ignored) {
                return DataResult.error(() -> "Unknown " + clazz.getSimpleName() + ": " + name);
            }
        }, value -> value.name().toLowerCase(Locale.ROOT));
    }
}
