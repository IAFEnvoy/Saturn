package com.iafenvoy.saturn.data.information.getter.builtin;

import com.iafenvoy.saturn.api.playerview.PlayerView;
import com.iafenvoy.saturn.data.information.getter.EntityValueGetter;
import com.iafenvoy.saturn.data.information.model.PlayerViewData;
import com.iafenvoy.saturn.data.information.model.SavedPlayerViewContext;
import com.iafenvoy.saturn.data.information.model.ViewValue;
import com.iafenvoy.saturn.util.codec.ExtraEnumCodecs;
import com.iafenvoy.saturn.util.codec.MiscCodecs;
import com.mojang.serialization.Codec;
import com.mojang.serialization.MapCodec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import net.minecraft.resources.Identifier;
import net.minecraft.world.entity.Entity;

import java.util.List;
import java.util.Optional;

/**
 * Reads a saved/current resource value or its recorded bounds; never initializes resources.
 */
public record ResourceGetter(Identifier resource, Value value) implements EntityValueGetter<Double> {
    public static final MapCodec<ResourceGetter> CODEC = RecordCodecBuilder.mapCodec(i -> i.group(
            Identifier.CODEC.fieldOf("resource").forGetter(ResourceGetter::resource),
            Value.CODEC.optionalFieldOf("value", Value.CURRENT).forGetter(ResourceGetter::value)
    ).apply(i, ResourceGetter::new));

    @Override
    public MapCodec<ResourceGetter> codec() {
        return CODEC;
    }

    @Override
    public Codec<Double> valueCodec() {
        return MiscCodecs.FINITE;
    }

    @Override
    public ViewValue<Double> capture(Entity entity) {
        return this.select(ResourcesGetter.INSTANCE.capture(entity));
    }

    @Override
    public ViewValue<Double> readSaved(SavedPlayerViewContext context) {
        return this.select(ResourcesGetter.INSTANCE.readSaved(context));
    }

    @Override
    public ViewValue<Double> fallback(PlayerView view) {
        return this.select(view.read(ResourcesGetter.INSTANCE));
    }

    @Override
    public List<EntityValueGetter<?>> dependencies() {
        return List.of(ResourcesGetter.INSTANCE);
    }

    private ViewValue<Double> select(ViewValue<List<PlayerViewData.Resource>> resources) {
        return GetterSupport.select(resources, entry -> entry.id().equals(this.resource), entry -> switch (this.value) {
            case CURRENT -> Optional.of(entry.value());
            case MINIMUM -> entry.minimum();
            case MAXIMUM -> entry.maximum();
        });
    }

    public enum Value {
        CURRENT, MINIMUM, MAXIMUM;
        public static final Codec<Value> CODEC = ExtraEnumCodecs.enumCodec(Value.class);
    }
}
