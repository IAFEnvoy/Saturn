package com.iafenvoy.saturn.view;

import com.iafenvoy.saturn.api.playerview.*;
import com.iafenvoy.saturn.view.model.*;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.nbt.*;
import net.minecraft.resources.Identifier;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.util.ProblemReporter;
import net.minecraft.world.Container;
import net.minecraft.world.entity.EquipmentSlot;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.GameType;
import net.minecraft.world.level.storage.TagValueOutput;

import java.util.*;

import static com.iafenvoy.saturn.view.PlayerViewFields.*;

final class VanillaPlayerViewReader implements PlayerViewContributor, SavedPlayerViewReader {
    @Override
    public Identifier id() {
        return Identifier.parse("saturn:vanilla");
    }

    @Override
    public Set<PlayerViewField<?>> fields() {
        return Set.of(PROFILE, HEALTH, MAX_HEALTH, ABSORPTION, AIR, FOOD, EXPERIENCE, LOCATION, GAME_MODE,
                SELECTED_SLOT, EQUIPMENT, INVENTORY, ENDER_CHEST, CARRIED_ITEM, EFFECTS, ATTRIBUTES, STATISTICS, ADVANCEMENTS, SCORES, TEAM);
    }

    @Override
    public void capture(Player player, PlayerViewWriter writer) {
        Map<String, String> properties = new LinkedHashMap<>();
        player.getGameProfile().properties().entries().forEach(entry -> {
            properties.put(entry.getKey(), entry.getValue().value());
            if (entry.getValue().hasSignature()) properties.put(entry.getKey() + ".signature", entry.getValue().signature());
        });
        writer.put(PROFILE, new PlayerViewData.Profile(player.getGameProfile().name(), player.getDisplayName(), properties));
        writer.put(HEALTH, (double) player.getHealth());
        writer.put(MAX_HEALTH, (double) player.getMaxHealth());
        writer.put(ABSORPTION, (double) player.getAbsorptionAmount());
        writer.put(AIR, player.getAirSupply());
        TagValueOutput food = TagValueOutput.createWithContext(ProblemReporter.DISCARDING, player.registryAccess());
        player.getFoodData().addAdditionalSaveData(food);
        CompoundTag foodTag = food.buildResult();
        writer.put(FOOD, new PlayerViewData.Food(player.getFoodData().getFoodLevel(), player.getFoodData().getSaturationLevel(),
                foodTag.getDouble("foodExhaustionLevel").orElseThrow()));
        writer.put(EXPERIENCE, new PlayerViewData.Experience(player.experienceLevel, player.totalExperience, player.experienceProgress));
        writer.put(LOCATION, new PlayerViewData.Location(player.level().dimension().identifier(), player.getX(), player.getY(), player.getZ(), player.getYRot(), player.getXRot()));
        Map<String, PlayerViewItem> equipment = new LinkedHashMap<>();
        for (EquipmentSlot slot : EquipmentSlot.values()) {
            ItemStack stack = player.getItemBySlot(slot);
            if (!stack.isEmpty()) equipment.put(slot.getSerializedName(), PlayerViewItem.capture(stack, player.registryAccess()));
        }
        writer.put(EQUIPMENT, equipment);
        writer.put(SELECTED_SLOT, player.getInventory().getSelectedSlot());
        writer.put(INVENTORY, container(player, player.getInventory(), player.getInventory().getNonEquipmentItems().size()));
        writer.put(ENDER_CHEST, container(player, player.getEnderChestInventory(), player.getEnderChestInventory().getContainerSize()));
        ItemStack carried = player.containerMenu.getCarried();
        writer.put(CARRIED_ITEM, carried.isEmpty() ? Map.of() : Map.of("cursor", PlayerViewItem.capture(carried, player.registryAccess())));
        writer.put(EFFECTS, player.getActiveEffects().stream().map(effect -> new PlayerViewData.Effect(
                BuiltInRegistries.MOB_EFFECT.getKey(effect.getEffect().value()), effect.getAmplifier(), effect.getDuration(), effect.isAmbient(), effect.isVisible())).toList());
        writer.put(ATTRIBUTES, BuiltInRegistries.ATTRIBUTE.listElements().filter(player.getAttributes()::hasAttribute)
                .map(attribute -> new PlayerViewData.Attribute(attribute.key().identifier(), player.getAttributeBaseValue(attribute),
                        Optional.of(player.getAttributeValue(attribute)))).toList());
        Map<String, Integer> scores = new LinkedHashMap<>();
        player.level().getScoreboard().listPlayerScores(player).forEach((objective, score) -> scores.put(objective.getName(), score));
        writer.put(SCORES, scores);
        writer.put(TEAM, player.getTeam() == null ? "" : player.getTeam().getName());
        if (player instanceof ServerPlayer server) writer.put(GAME_MODE, server.gameMode.getGameModeForPlayer().getName());
        else writer.missing(GAME_MODE, MissingReason.UNSUPPORTED);
        // These private vanilla stores need a host-supplied projection; querying them must not save files or start progress.
        writer.missing(STATISTICS, MissingReason.UNSUPPORTED);
        writer.missing(ADVANCEMENTS, MissingReason.UNSUPPORTED);
    }

    @Override
    public void read(SavedPlayerViewContext context, PlayerViewWriter writer) {
        CompoundTag data = context.player();
        SavedFields.read(writer, HEALTH, data.contains("Health"), () -> data.getDouble("Health").orElseThrow());
        SavedFields.read(writer, ABSORPTION, data.contains("AbsorptionAmount"), () -> data.getDouble("AbsorptionAmount").orElseThrow());
        SavedFields.read(writer, AIR, data.contains("Air"), () -> data.getInt("Air").orElseThrow());
        SavedFields.read(writer, FOOD, data.contains("foodLevel"), () -> new PlayerViewData.Food(
                data.getInt("foodLevel").orElseThrow(), data.getDouble("foodSaturationLevel").orElseThrow(), data.getDouble("foodExhaustionLevel").orElseThrow()));
        SavedFields.read(writer, EXPERIENCE, data.contains("XpLevel"), () -> new PlayerViewData.Experience(
                data.getInt("XpLevel").orElseThrow(), data.getInt("XpTotal").orElseThrow(), data.getDouble("XpP").orElseThrow()));
        SavedFields.read(writer, LOCATION, data.contains("Dimension"), () -> {
            ListTag pos = data.getList("Pos").orElseThrow();
            ListTag rot = data.getList("Rotation").orElseThrow();
            if (pos.size() != 3 || rot.size() != 2) throw new IllegalArgumentException("Invalid saved location");
            return new PlayerViewData.Location(Identifier.parse(data.getString("Dimension").orElseThrow()),
                    pos.getDouble(0).orElseThrow(), pos.getDouble(1).orElseThrow(), pos.getDouble(2).orElseThrow(),
                    rot.getDouble(0).orElseThrow(), rot.getDouble(1).orElseThrow());
        });
        SavedFields.read(writer, GAME_MODE, data.contains("playerGameType"), () -> {
            int mode = data.getInt("playerGameType").orElseThrow();
            if (mode < 0 || mode >= GameType.values().length) throw new IllegalArgumentException("Invalid saved game mode");
            return GameType.byId(mode).getName();
        });
        SavedFields.read(writer, SELECTED_SLOT, data.contains("SelectedItemSlot"), () -> data.getInt("SelectedItemSlot").orElseThrow());
        SavedFields.read(writer, INVENTORY, data.contains("Inventory"), () -> savedItems(data.getList("Inventory").orElseThrow(), context));
        SavedFields.read(writer, ENDER_CHEST, data.contains("EnderItems"), () -> savedItems(data.getList("EnderItems").orElseThrow(), context));
        SavedFields.read(writer, EQUIPMENT, data.contains("equipment"), () -> {
            CompoundTag items = data.getCompound("equipment").orElseThrow();
            Map<String, PlayerViewItem> equipment = new LinkedHashMap<>();
            for (String slot : items.keySet()) equipment.put(slot,
                    PlayerViewItem.saved(items.getCompound(slot).orElseThrow(), context.registries()).orElseThrow());
            if (data.contains("Inventory") && data.contains("SelectedItemSlot")) {
                String selected = Integer.toString(data.getInt("SelectedItemSlot").orElseThrow());
                PlayerViewItem main = savedItems(data.getList("Inventory").orElseThrow(), context).get(selected);
                if (main != null) equipment.put("mainhand", main);
            }
            return equipment;
        });
        SavedFields.read(writer, EFFECTS, data.contains("active_effects"), () -> {
            List<PlayerViewData.Effect> effects = new ArrayList<>();
            for (Tag raw : data.getList("active_effects").orElseThrow()) {
                if (!(raw instanceof CompoundTag effect)) throw new IllegalArgumentException("Invalid saved effect");
                effects.add(new PlayerViewData.Effect(Identifier.parse(effect.getString("id").orElseThrow()),
                        effect.getInt("amplifier").orElse(0), effect.getInt("duration").orElse(0),
                        effect.getBoolean("ambient").orElse(false), effect.getBoolean("show_particles").orElse(true)));
            }
            return effects;
        });
        SavedFields.read(writer, ATTRIBUTES, data.contains("attributes"), () -> {
            ListTag items = data.getList("attributes").orElseThrow();
            List<PlayerViewData.Attribute> attributes = new ArrayList<>();
            for (Tag raw : items) {
                if (!(raw instanceof CompoundTag attribute)) throw new IllegalArgumentException("Invalid saved attribute");
                attributes.add(new PlayerViewData.Attribute(Identifier.parse(attribute.getString("id").orElseThrow()),
                        attribute.getDouble("base").orElseThrow(), Optional.empty()));
            }
            return attributes;
        });
        context.statistics().ifPresent(value -> writer.put(STATISTICS, value, ValueOrigin.SAVED));
        context.advancements().ifPresent(value -> writer.put(ADVANCEMENTS, value, ValueOrigin.SAVED));
    }

    private static Map<String, PlayerViewItem> container(Player player, Container container, int size) {
        Map<String, PlayerViewItem> items = new LinkedHashMap<>();
        for (int slot = 0; slot < size; slot++) {
            ItemStack stack = container.getItem(slot);
            if (!stack.isEmpty()) items.put(Integer.toString(slot), PlayerViewItem.capture(stack, player.registryAccess()));
        }
        return items;
    }

    private static Map<String, PlayerViewItem> savedItems(ListTag list, SavedPlayerViewContext context) {
        Map<String, PlayerViewItem> items = new LinkedHashMap<>();
        for (Tag raw : list) {
            if (!(raw instanceof CompoundTag item)) throw new IllegalArgumentException("Invalid saved item");
            int slot = item.getByte("Slot").map(Byte::toUnsignedInt).orElseThrow();
            PlayerViewItem value = PlayerViewItem.saved(item, context.registries()).orElseThrow();
            if (items.putIfAbsent(Integer.toString(slot), value) != null) throw new IllegalArgumentException("Duplicate saved item slot");
        }
        return items;
    }
}
