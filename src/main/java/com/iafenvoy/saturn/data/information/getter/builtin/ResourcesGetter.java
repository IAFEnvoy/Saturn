package com.iafenvoy.saturn.data.information.getter.builtin;

import com.iafenvoy.saturn.attachment.ResourceHolderAttachment;
import com.iafenvoy.saturn.data.information.getter.EntityValueGetter;
import com.iafenvoy.saturn.data.information.model.PlayerViewData;
import com.iafenvoy.saturn.data.information.model.SavedPlayerViewContext;
import com.iafenvoy.saturn.data.information.model.ValueOrigin;
import com.iafenvoy.saturn.data.information.model.ViewValue;
import com.iafenvoy.saturn.registry.SaturnAttachments;
import com.mojang.serialization.Codec;
import com.mojang.serialization.MapCodec;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.resources.Identifier;
import net.minecraft.world.entity.Entity;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

public enum ResourcesGetter implements EntityValueGetter<List<PlayerViewData.Resource>> {
    INSTANCE;

    public static final MapCodec<ResourcesGetter> CODEC = MapCodec.unit(INSTANCE);

    @Override
    public MapCodec<ResourcesGetter> codec() {
        return CODEC;
    }

    @Override
    public Codec<List<PlayerViewData.Resource>> valueCodec() {
        return PlayerViewData.Resource.CODEC.listOf();
    }

    @Override
    public ViewValue<List<PlayerViewData.Resource>> capture(Entity entity) {
        List<PlayerViewData.Resource> resources = new ArrayList<>();
        entity.getExistingData(SaturnAttachments.RESOURCE_HOLDER).ifPresent(attachment -> attachment.values().forEach((holder, value) -> {
            ResourceHolderAttachment.Audit audit = attachment.audit().get(holder);
            resources.add(new PlayerViewData.Resource(holder.unwrapKey().orElseThrow().identifier(), value,
                    audit == null ? Optional.empty() : Optional.of(audit.minSnapshot()),
                    audit == null ? Optional.empty() : Optional.of(audit.maxSnapshot()), audit == null ? -1 : audit.lastChangedTick()));
        }));
        return ViewValue.known(resources, ValueOrigin.CAPTURED);
    }

    @Override
    public ViewValue<List<PlayerViewData.Resource>> readSaved(SavedPlayerViewContext context) {
        CompoundTag attachments = context.player().getCompound("neoforge:attachments").orElseGet(CompoundTag::new);
        return GetterSupport.saved(attachments, "saturn:resource_holder", data -> {
            CompoundTag resource = data.getCompound("saturn:resource_holder").orElseThrow();
            CompoundTag values = resource.getCompound("values").orElseThrow();
            CompoundTag audits = resource.getCompound("audit").orElseGet(CompoundTag::new);
            List<PlayerViewData.Resource> resources = new ArrayList<>();
            for (String key : values.keySet()) {
                CompoundTag audit = audits.getCompound(key).orElseGet(CompoundTag::new);
                resources.add(new PlayerViewData.Resource(Identifier.parse(key), values.getDouble(key).orElseThrow(),
                        audit.getDouble("min_snapshot"), audit.getDouble("max_snapshot"), audit.getLong("last_changed_tick").orElse(-1L)));
            }
            return resources;
        });
    }
}
