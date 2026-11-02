package com.iafenvoy.saturn.data.information.getter.builtin;

import com.iafenvoy.saturn.data.information.getter.EntityValueGetter;
import com.iafenvoy.saturn.data.information.model.PlayerViewData;
import com.iafenvoy.saturn.data.information.model.SavedPlayerViewContext;
import com.iafenvoy.saturn.data.information.model.ValueOrigin;
import com.iafenvoy.saturn.data.information.model.ViewValue;
import com.mojang.serialization.Codec;
import com.mojang.serialization.MapCodec;
import net.minecraft.nbt.ListTag;
import net.minecraft.resources.Identifier;
import net.minecraft.world.entity.Entity;

public enum LocationGetter implements EntityValueGetter<PlayerViewData.Location> {
    INSTANCE;

    public static final MapCodec<LocationGetter> CODEC = MapCodec.unit(INSTANCE);

    @Override
    public MapCodec<LocationGetter> codec() {
        return CODEC;
    }

    @Override
    public Codec<PlayerViewData.Location> valueCodec() {
        return PlayerViewData.Location.CODEC;
    }

    @Override
    public ViewValue<PlayerViewData.Location> capture(Entity entity) {
        return ViewValue.known(new PlayerViewData.Location(entity.level().dimension().identifier(), entity.getX(), entity.getY(), entity.getZ(),
                entity.getYRot(), entity.getXRot()), ValueOrigin.CAPTURED);
    }

    @Override
    public ViewValue<PlayerViewData.Location> readSaved(SavedPlayerViewContext context) {
        return GetterSupport.saved(context.player(), "Dimension", data -> {
            ListTag pos = data.getList("Pos").orElseThrow();
            ListTag rot = data.getList("Rotation").orElseThrow();
            if (pos.size() != 3 || rot.size() != 2) throw new IllegalArgumentException("Invalid saved location");
            return new PlayerViewData.Location(Identifier.parse(data.getString("Dimension").orElseThrow()),
                    pos.getDouble(0).orElseThrow(), pos.getDouble(1).orElseThrow(), pos.getDouble(2).orElseThrow(),
                    rot.getDouble(0).orElseThrow(), rot.getDouble(1).orElseThrow());
        });
    }
}
