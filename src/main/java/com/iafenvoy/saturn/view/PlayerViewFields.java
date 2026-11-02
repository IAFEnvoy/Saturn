package com.iafenvoy.saturn.view;

import com.iafenvoy.saturn.api.playerview.PlayerViewField;
import com.iafenvoy.saturn.util.codec.MiscCodecs;
import com.iafenvoy.saturn.view.model.PlayerViewData;
import com.iafenvoy.saturn.view.model.PlayerViewItem;
import com.mojang.serialization.Codec;
import net.minecraft.resources.Identifier;

import java.util.*;

public final class PlayerViewFields {
    private static final Map<Identifier, PlayerViewField<?>> FIELDS = new LinkedHashMap<>();
    public static final PlayerViewField<PlayerViewData.Profile> PROFILE = builtin("profile", PlayerViewData.Profile.CODEC);
    public static final PlayerViewField<Double> HEALTH = builtin("health", MiscCodecs.FINITE);
    public static final PlayerViewField<Double> MAX_HEALTH = builtin("max_health", MiscCodecs.FINITE);
    public static final PlayerViewField<Double> ABSORPTION = builtin("absorption", MiscCodecs.FINITE);
    public static final PlayerViewField<Integer> AIR = builtin("air", Codec.INT);
    public static final PlayerViewField<PlayerViewData.Food> FOOD = builtin("food", PlayerViewData.Food.CODEC);
    public static final PlayerViewField<PlayerViewData.Experience> EXPERIENCE = builtin("experience", PlayerViewData.Experience.CODEC);
    public static final PlayerViewField<PlayerViewData.Location> LOCATION = builtin("location", PlayerViewData.Location.CODEC);
    public static final PlayerViewField<String> GAME_MODE = builtin("game_mode", Codec.STRING);
    public static final PlayerViewField<Integer> SELECTED_SLOT = builtin("selected_slot", Codec.intRange(0, 8));
    public static final PlayerViewField<Map<String, PlayerViewItem>> EQUIPMENT = builtin("equipment", Codec.unboundedMap(Codec.STRING, PlayerViewItem.CODEC));
    public static final PlayerViewField<Map<String, PlayerViewItem>> INVENTORY = builtin("inventory", Codec.unboundedMap(Codec.STRING, PlayerViewItem.CODEC));
    public static final PlayerViewField<Map<String, PlayerViewItem>> ENDER_CHEST = builtin("ender_chest", Codec.unboundedMap(Codec.STRING, PlayerViewItem.CODEC));
    public static final PlayerViewField<Map<String, PlayerViewItem>> CARRIED_ITEM = builtin("carried_item", Codec.unboundedMap(Codec.STRING, PlayerViewItem.CODEC));
    public static final PlayerViewField<List<PlayerViewData.Effect>> EFFECTS = builtin("effects", PlayerViewData.Effect.CODEC.listOf());
    public static final PlayerViewField<List<PlayerViewData.Attribute>> ATTRIBUTES = builtin("attributes", PlayerViewData.Attribute.CODEC.listOf());
    public static final PlayerViewField<List<PlayerViewData.Resource>> RESOURCES = builtin("resources", PlayerViewData.Resource.CODEC.listOf());
    public static final PlayerViewField<List<Identifier>> ABILITIES = builtin("abilities", Identifier.CODEC.listOf());
    public static final PlayerViewField<String> STATISTICS = builtin("statistics", Codec.STRING);
    public static final PlayerViewField<String> ADVANCEMENTS = builtin("advancements", Codec.STRING);
    public static final PlayerViewField<Map<String, Integer>> SCORES = builtin("scores", Codec.unboundedMap(Codec.STRING, Codec.INT));
    public static final PlayerViewField<String> TEAM = builtin("team", Codec.STRING);

    private PlayerViewFields() {
    }

    public static synchronized <T> PlayerViewField<T> register(Identifier id, int schema, Codec<T> codec) {
        if (schema < 1) throw new IllegalArgumentException("Field schema must be positive");
        Field<T> field = new Field<>(Objects.requireNonNull(id), schema, Objects.requireNonNull(codec));
        if (FIELDS.putIfAbsent(id, field) != null) throw new IllegalArgumentException("Duplicate player view field: " + id);
        return field;
    }

    public static synchronized List<PlayerViewField<?>> all() {
        return List.copyOf(FIELDS.values());
    }

    public static synchronized void requireRegistered(PlayerViewField<?> field) {
        if (FIELDS.get(field.id()) != field) throw new IllegalArgumentException("Unregistered player view field: " + field.id());
    }

    private static <T> PlayerViewField<T> builtin(String name, Codec<T> codec) {
        return register(Identifier.fromNamespaceAndPath("saturn", name), 1, codec);
    }

    private record Field<T>(Identifier id, int schemaVersion, Codec<T> codec) implements PlayerViewField<T> {
    }
}
