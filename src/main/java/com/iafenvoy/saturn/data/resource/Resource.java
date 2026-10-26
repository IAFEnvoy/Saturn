package com.iafenvoy.saturn.data.resource;

import com.iafenvoy.saturn.data.IconReference;
import com.iafenvoy.saturn.registry.SaturnResourceKeys;
import com.iafenvoy.saturn.util.formula.NumberProvider;
import com.iafenvoy.saturn.util.formula.number.Constant;
import com.mojang.serialization.Codec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import net.minecraft.core.Holder;
import net.minecraft.resources.RegistryFixedCodec;
import net.minecraft.util.ExtraCodecs;
import org.jspecify.annotations.NonNull;

import java.util.List;
import java.util.Optional;

/**
 * Datapack definition of a stored value: its bounds and how it is displayed.
 */
public record Resource(NumberProvider defaultValue, NumberProvider min, NumberProvider max,
                       Optional<IconReference> icon, int particleColor, List<ResourceBar> bars) {
    public static final Codec<Holder<Resource>> CODEC = RegistryFixedCodec.create(SaturnResourceKeys.RESOURCE);
    public static final Codec<Resource> DIRECT_CODEC = RecordCodecBuilder.create(i -> i.group(
            NumberProvider.CODEC.fieldOf("default_value").forGetter(Resource::defaultValue),
            NumberProvider.CODEC.optionalFieldOf("min", new Constant(0.0D)).forGetter(Resource::min),
            NumberProvider.CODEC.fieldOf("max").forGetter(Resource::max),
            IconReference.CODEC.optionalFieldOf("icon").forGetter(Resource::icon),
            ExtraCodecs.STRING_RGB_COLOR.optionalFieldOf("particle_color", 0xFFFFFF).forGetter(Resource::particleColor),
            ResourceBar.CODEC.listOf().optionalFieldOf("bars", List.of()).forGetter(Resource::bars)
    ).apply(i, Resource::new));

    @Override
    public @NonNull String toString() {
        return "Resource[bars=" + this.bars.size() + "]";
    }
}
