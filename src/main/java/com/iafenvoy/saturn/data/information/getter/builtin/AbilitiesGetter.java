package com.iafenvoy.saturn.data.information.getter.builtin;

import com.iafenvoy.saturn.Saturn;
import com.iafenvoy.saturn.data.information.getter.EntityValueGetter;
import com.iafenvoy.saturn.data.information.model.SavedPlayerViewContext;
import com.iafenvoy.saturn.data.information.model.ValueOrigin;
import com.iafenvoy.saturn.data.information.model.ViewValue;
import com.iafenvoy.saturn.registry.SaturnAttachments;
import com.mojang.serialization.Codec;
import com.mojang.serialization.MapCodec;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.nbt.ListTag;
import net.minecraft.nbt.Tag;
import net.minecraft.resources.Identifier;
import net.minecraft.world.entity.Entity;

import java.util.List;
import java.util.Set;
import java.util.TreeSet;

public enum AbilitiesGetter implements EntityValueGetter<List<Identifier>> {
    INSTANCE;

    public static final MapCodec<AbilitiesGetter> CODEC = MapCodec.unit(INSTANCE);

    @Override
    public MapCodec<AbilitiesGetter> codec() {
        return CODEC;
    }

    @Override
    public Codec<List<Identifier>> valueCodec() {
        return Identifier.CODEC.listOf();
    }

    @Override
    public ViewValue<List<Identifier>> capture(Entity entity) {
        return ViewValue.known(entity.getExistingData(SaturnAttachments.ABILITY_HOLDER)
                .map(attachment -> attachment.sources().keys().stream().map(holder -> holder.unwrapKey().orElseThrow().identifier()).sorted().toList())
                .orElse(List.of()), ValueOrigin.CAPTURED);
    }

    @Override
    public ViewValue<List<Identifier>> readSaved(SavedPlayerViewContext context) {
        CompoundTag attachments = context.player().getCompound("neoforge:attachments").orElseGet(CompoundTag::new);
        return GetterSupport.saved(attachments, Saturn.MOD_ID + ":ability_holder", data -> {
            CompoundTag abilities = data.getCompound(Saturn.MOD_ID + ":ability_holder").orElseThrow();
            Set<Identifier> ids = new TreeSet<>();
            CompoundTag sources = abilities.getCompound("sources").orElseGet(CompoundTag::new);
            for (String key : sources.keySet()) {
                Tag values = sources.get(key);
                if (values instanceof ListTag list && !list.isEmpty()) ids.add(Identifier.parse(key));
            }
            return List.copyOf(ids);
        });
    }
}
