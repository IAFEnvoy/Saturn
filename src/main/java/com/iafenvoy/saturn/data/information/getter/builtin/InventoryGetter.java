package com.iafenvoy.saturn.data.information.getter.builtin;

import com.iafenvoy.saturn.data.information.getter.EntityValueGetter;
import com.iafenvoy.saturn.data.information.model.*;
import com.mojang.serialization.Codec;
import com.mojang.serialization.MapCodec;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.player.Player;

import java.util.Map;

public enum InventoryGetter implements EntityValueGetter<Map<String, PlayerViewItem>> {
    INSTANCE;

    public static final MapCodec<InventoryGetter> CODEC = MapCodec.unit(INSTANCE);

    @Override
    public MapCodec<InventoryGetter> codec() {
        return CODEC;
    }

    @Override
    public Codec<Map<String, PlayerViewItem>> valueCodec() {
        return Codec.unboundedMap(Codec.STRING, PlayerViewItem.CODEC);
    }

    @Override
    public ViewValue<Map<String, PlayerViewItem>> capture(Entity entity) {
        if (!(entity instanceof Player player)) return ViewValue.missing(MissingReason.UNSUPPORTED);
        return ViewValue.known(GetterSupport.container(player, player.getInventory(), player.getInventory().getNonEquipmentItems().size()), ValueOrigin.CAPTURED);
    }

    @Override
    public ViewValue<Map<String, PlayerViewItem>> readSaved(SavedPlayerViewContext context) {
        return GetterSupport.saved(context.player(), "Inventory", data -> GetterSupport.savedItems(data.getList("Inventory").orElseThrow(), context));
    }
}
