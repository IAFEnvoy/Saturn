package com.iafenvoy.saturn.view;

import com.iafenvoy.saturn.api.playerview.*;
import com.iafenvoy.saturn.attachment.ResourceHolderAttachment;
import com.iafenvoy.saturn.registry.SaturnAttachments;
import com.iafenvoy.saturn.view.model.*;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.nbt.ListTag;
import net.minecraft.nbt.Tag;
import net.minecraft.resources.Identifier;
import net.minecraft.world.entity.player.Player;

import java.util.*;

final class SaturnPlayerViewReader implements PlayerViewContributor, SavedPlayerViewReader {
    @Override
    public Identifier id() {
        return Identifier.parse("saturn:attachments");
    }

    @Override
    public Set<PlayerViewField<?>> fields() {
        return Set.of(PlayerViewFields.RESOURCES, PlayerViewFields.ABILITIES);
    }

    @Override
    public void capture(Player player, PlayerViewWriter writer) {
        List<PlayerViewData.Resource> resources = new ArrayList<>();
        player.getExistingData(SaturnAttachments.RESOURCE_HOLDER).ifPresent(attachment -> attachment.values().forEach((holder, value) -> {
            ResourceHolderAttachment.Audit audit = attachment.audit().get(holder);
            resources.add(new PlayerViewData.Resource(holder.unwrapKey().orElseThrow().identifier(), value,
                    audit == null ? Optional.empty() : Optional.of(audit.minSnapshot()),
                    audit == null ? Optional.empty() : Optional.of(audit.maxSnapshot()), audit == null ? -1 : audit.lastChangedTick()));
        }));
        writer.put(PlayerViewFields.RESOURCES, resources);
        writer.put(PlayerViewFields.ABILITIES, player.getExistingData(SaturnAttachments.ABILITY_HOLDER)
                .map(attachment -> attachment.sources().keys().stream().map(holder -> holder.unwrapKey().orElseThrow().identifier()).sorted().toList())
                .orElse(List.of()));
    }

    @Override
    public void read(SavedPlayerViewContext context, PlayerViewWriter writer) {
        CompoundTag attachments = context.player().getCompound("neoforge:attachments").orElseGet(CompoundTag::new);
        SavedFields.read(writer, PlayerViewFields.RESOURCES, attachments.contains("saturn:resource_holder"), () -> {
            CompoundTag resource = attachments.getCompound("saturn:resource_holder").orElseThrow();
            List<PlayerViewData.Resource> resources = new ArrayList<>();
            CompoundTag values = resource.getCompound("values").orElseThrow();
            CompoundTag audits = resource.getCompound("audit").orElseGet(CompoundTag::new);
            for (String key : values.keySet()) {
                CompoundTag audit = audits.getCompound(key).orElseGet(CompoundTag::new);
                resources.add(new PlayerViewData.Resource(Identifier.parse(key), values.getDouble(key).orElseThrow(),
                        audit.getDouble("min_snapshot"), audit.getDouble("max_snapshot"), audit.getLong("last_changed_tick").orElse(-1L)));
            }
            return resources;
        });
        SavedFields.read(writer, PlayerViewFields.ABILITIES, attachments.contains("saturn:ability_holder"), () -> {
            CompoundTag abilities = attachments.getCompound("saturn:ability_holder").orElseThrow();
            Set<Identifier> ids = new TreeSet<>();
            abilities.getCompound("sources").ifPresent(sources -> {
                for (String key : sources.keySet()) {
                    Tag values = sources.get(key);
                    if (values instanceof ListTag list && !list.isEmpty()) ids.add(Identifier.parse(key));
                }
            });
            return List.copyOf(ids);
        });
    }
}
