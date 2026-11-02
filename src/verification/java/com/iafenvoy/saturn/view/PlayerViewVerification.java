package com.iafenvoy.saturn.view;

import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.iafenvoy.saturn.api.playerview.*;
import com.iafenvoy.saturn.data.information.*;
import com.iafenvoy.saturn.data.information.getter.*;
import com.iafenvoy.saturn.data.information.getter.builtin.*;
import com.mojang.serialization.MapCodec;
import com.iafenvoy.saturn.registry.SaturnRegistries;
import net.minecraft.core.Registry;
import net.minecraft.resources.Identifier;
import java.util.function.Function;
import com.iafenvoy.saturn.data.information.model.*;
import com.mojang.serialization.Codec;
import com.mojang.serialization.JsonOps;
import net.minecraft.core.RegistryAccess;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.nbt.NbtOps;
import net.minecraft.network.chat.Component;

import java.util.*;

/** Standalone codec and contract checks; no game, world, connection or player is created. */
public final class PlayerViewVerification {
    private static int checks;

    public static void main(String[] args) {
        // Standalone fixtures populate native codec registries without bootstrapping the game.
        for (var entry : List.of(
                Map.entry("abilities", EntityValueGetters.ABILITIES.codec()),
                Map.entry("absorption", EntityValueGetters.ABSORPTION.codec()),
                Map.entry("advancements", EntityValueGetters.ADVANCEMENTS.codec()),
                Map.entry("air", EntityValueGetters.AIR.codec()),
                Map.entry("attributes", EntityValueGetters.ATTRIBUTES.codec()),
                Map.entry("carried_item", EntityValueGetters.CARRIED_ITEM.codec()),
                Map.entry("effects", EntityValueGetters.EFFECTS.codec()),
                Map.entry("ender_chest", EntityValueGetters.ENDER_CHEST.codec()),
                Map.entry("equipment", EntityValueGetters.EQUIPMENT.codec()),
                Map.entry("experience", EntityValueGetters.EXPERIENCE.codec()),
                Map.entry("food", EntityValueGetters.FOOD.codec()),
                Map.entry("game_mode", EntityValueGetters.GAME_MODE.codec()),
                Map.entry("health", EntityValueGetters.HEALTH.codec()),
                Map.entry("inventory", EntityValueGetters.INVENTORY.codec()),
                Map.entry("location", EntityValueGetters.LOCATION.codec()),
                Map.entry("max_health", EntityValueGetters.MAX_HEALTH.codec()),
                Map.entry("profile", EntityValueGetters.PROFILE.codec()),
                Map.entry("resources", EntityValueGetters.RESOURCES.codec()),
                Map.entry("scores", EntityValueGetters.SCORES.codec()),
                Map.entry("selected_slot", EntityValueGetters.SELECTED_SLOT.codec()),
                Map.entry("statistics", EntityValueGetters.STATISTICS.codec()),
                Map.entry("team", EntityValueGetters.TEAM.codec())))
            Registry.register(SaturnRegistries.ENTITY_VALUE_GETTER, Identifier.fromNamespaceAndPath("saturn", entry.getKey()), entry.getValue());
        Registry.register(SaturnRegistries.ENTITY_VALUE_GETTER, Identifier.parse("saturn:attribute"), AttributeGetter.CODEC);
        Registry.register(SaturnRegistries.ENTITY_VALUE_GETTER, Identifier.parse("saturn:resource"), ResourceGetter.CODEC);
        Registry.register(SaturnRegistries.ENTITY_VALUE_GETTER, Identifier.parse("saturn:effect"), EffectGetter.CODEC);
        Registry.register(SaturnRegistries.ENTITY_VALUE_GETTER, Identifier.parse("saturn:score"), ScoreGetter.CODEC);
        snapshots();
        groups();
        importance();
        suppliedData();
        queries();
        contracts();
        System.out.println("Player view verification: " + checks + " checks passed");
    }

    private static void snapshots() {
        EntityValueGetter<CompoundTag> mutable = register("mutable", CompoundTag.CODEC, context -> ViewValue.missing(MissingReason.NOT_CAPTURED));
        CompoundTag original = new CompoundTag();
        original.putInt("n", 7);
        PlayerViewSnapshot.Builder builder = PlayerViewSnapshot.builder(meta("one"), NbtOps.INSTANCE);
        builder.put(mutable, original);
        builder.put(EntityValueGetters.HEALTH, 0.0);
        original.putInt("n", 9);
        PlayerViewSnapshot view = builder.build();
        check(view.read(mutable).value().orElseThrow().getInt("n").orElseThrow() == 7, "input data detached");
        view.read(mutable).value().orElseThrow().putInt("n", 99);
        check(view.read(mutable).value().orElseThrow().getInt("n").orElseThrow() == 7, "read data detached");
        check(view.read(EntityValueGetters.HEALTH).isKnown() && view.read(EntityValueGetters.HEALTH).value().orElseThrow() == 0, "zero is known");
        check(view.read(EntityValueGetters.MAX_HEALTH).missingReason().orElseThrow() == MissingReason.NOT_CAPTURED, "missing is not zero");
        builder.put(EntityValueGetters.HEALTH, 20.0);
        check(view.read(EntityValueGetters.HEALTH).value().orElseThrow() == 0, "builder changes detached");
        JsonObject json = PlayerViewSnapshot.CODEC.encodeStart(JsonOps.INSTANCE, view).getOrThrow().getAsJsonObject();
        JsonObject unknown = new JsonObject();
        unknown.addProperty("schema", 17);
        unknown.addProperty("custom", "uninstalled mod");
        json.getAsJsonObject("fields").add("absent:field", unknown);
        PlayerViewSnapshot decoded = PlayerViewSnapshot.CODEC.parse(JsonOps.INSTANCE, json).getOrThrow();
        JsonElement encoded = PlayerViewSnapshot.CODEC.encodeStart(JsonOps.INSTANCE, decoded).getOrThrow();
        check(encoded.getAsJsonObject().getAsJsonObject("fields").get("absent:field").equals(unknown), "unknown field preserved");
        check(decoded.read(mutable).value().orElseThrow().getInt("n").orElseThrow() == 7, "JSON round trip");
        check(PlayerViewSnapshot.CODEC.parse(NbtOps.INSTANCE, PlayerViewSnapshot.CODEC.encodeStart(NbtOps.INSTANCE, decoded).getOrThrow())
                .getOrThrow().read(EntityValueGetters.HEALTH).value().orElseThrow() == 0, "NBT round trip");
        json.getAsJsonObject("fields").getAsJsonObject("saturn:health").addProperty("schema", 2);
        check(PlayerViewSnapshot.CODEC.parse(JsonOps.INSTANCE, json).getOrThrow().read(EntityValueGetters.HEALTH)
                .missingReason().orElseThrow() == MissingReason.UNSUPPORTED, "future field schema isolated");
        json.getAsJsonObject("fields").getAsJsonObject("saturn:health").addProperty("schema", 1);
        json.getAsJsonObject("fields").getAsJsonObject("saturn:health").addProperty("value", "bad");
        PlayerViewSnapshot invalid = PlayerViewSnapshot.CODEC.parse(JsonOps.INSTANCE, json).getOrThrow();
        check(invalid.read(EntityValueGetters.HEALTH).missingReason().orElseThrow() == MissingReason.INVALID
                && invalid.read(mutable).isKnown(), "invalid field does not poison snapshot");
        check(decoded.filter(field -> field.equals(EntityValueGetters.HEALTH.type())).getterIds().equals(Set.of(EntityValueGetters.HEALTH.type())), "wire filtering removes unknown fields too");
        CompoundTag itemData = new CompoundTag();
        itemData.putString("id", "absent:item");
        itemData.putInt("count", 2);
        PlayerViewItem item = PlayerViewItem.saved(itemData, RegistryAccess.EMPTY).orElseThrow();
        check(item.count() == 2 && item.id().toString().equals("absent:item"), "removed item keeps id and count");
        item.data().putInt("count", 99);
        check(item.data().getInt("count").orElseThrow() == 2, "item data detached");
        check(!meta("one").ref().equals(meta("two").ref()), "historical records have distinct identity");
    }

    private static void groups() {
        PlayerInformationRegistry registry = new PlayerInformationRegistry();
        InfomationGroup cultivation = registry.registerGroup(id("cultivation"), Component.literal("Cultivation"), 10);
        InfomationGroup basic = registry.registerGroup(id("basic"), Component.literal("Basic"), 0);
        PlayerInformationRowProvider row = (view, context) -> Optional.of(new InformationRowContent(Component.literal("X"), new InformationValue.Number(3), Set.of()));
        registry.register(id("realm"), cultivation, 0, row);
        registry.register(id("food"), basic, 10, row);
        registry.register(id("health"), basic, 0, row);
        registry.register(id("hidden"), basic, 20, (view, context) -> Optional.empty());
        PlayerViewSnapshot view = PlayerViewSnapshot.builder(meta("one"), NbtOps.INSTANCE).build();
        InformationDocument all = registry.collectAll(view, InformationContext.DEFAULT);
        check(all.rows().stream().map(InformationRowResult::id).toList().equals(List.of(id("health"), id("food"), id("realm"))), "all groups ordered");
        check(registry.collect(view, InformationContext.DEFAULT, basic).rows().equals(all.rows(basic)), "by group equals all filtered");
        check(registry.collect(view, InformationContext.DEFAULT, Set.of()).rows().isEmpty(), "empty selection means none");
        check(all.groups().equals(List.of(basic, cultivation)), "group metadata retained");
        InfomationGroup otherLanguage = new InfomationGroup(basic.id(), Component.literal("Different title"), 100);
        check(registry.collect(view, InformationContext.DEFAULT, otherLanguage).rows().size() == 2, "group identity ignores title and order");
        expectFailure(() -> registry.registerGroup(id("basic"), Component.empty(), 0), "duplicate group rejected");
        expectFailure(() -> registry.register(id("health"), basic, 0, row), "duplicate row rejected");
        expectFailure(() -> registry.register(id("unknown"), new InfomationGroup(id("unknown"), Component.empty(), 0), 0, row), "unknown group rejected");
        registry.register(id("broken"), basic, 30, (v, c) -> { throw new IllegalStateException("Expected verification failure"); });
        check(registry.collectAll(view, InformationContext.DEFAULT).rows().size() == 3, "row failure isolated");
        InformationValue number = new InformationValue.Number(7);
        check(InformationValue.CODEC.parse(JsonOps.INSTANCE, InformationValue.CODEC.encodeStart(JsonOps.INSTANCE, number)
                .getOrThrow()).getOrThrow().equals(number), "numeric display codec");
        check(PlayerInformation.collectAll(view).rows().isEmpty(), "detached view without registry does not borrow global world definitions");
        registry.register(id("shared"), List.of(basic, cultivation, basic), 40, row);
        check(registry.collect(view, InformationContext.DEFAULT, basic).rows().stream().anyMatch(r -> r.id().equals(id("shared")))
                && registry.collect(view, InformationContext.DEFAULT, cultivation).rows().stream().anyMatch(r -> r.id().equals(id("shared"))), "shared row belongs to both groups");
        check(registry.collect(view, InformationContext.DEFAULT, Set.of(basic, cultivation)).rows().stream().filter(r -> r.id().equals(id("shared"))).count() == 1,
                "union and all collect shared row once");
        check(registry.collectAll(view, InformationContext.DEFAULT).rows().stream().filter(r -> r.id().equals(id("shared"))).findFirst().orElseThrow().groups().size() == 2,
                "duplicate group membership removed");
        registry.register(id("no_groups"), 0, 0, row);
        check(registry.collectAll(view, InformationContext.DEFAULT).rows().stream().anyMatch(r -> r.id().equals(id("no_groups")) && r.groups().isEmpty()), "ungrouped row included by all");
        check(registry.collect(view, InformationContext.DEFAULT, Set.of(basic, cultivation)).rows().stream().noneMatch(r -> r.id().equals(id("no_groups"))), "ungrouped row excluded from group union");
        Component title = Component.literal("Original");
        InfomationGroup detached = new InfomationGroup(id("title"), title, 0);
        ((net.minecraft.network.chat.MutableComponent) title).append("changed");
        check(detached.title().getString().equals("Original"), "group title detached");
        var sibling = Component.literal("child");
        InfomationGroup nested = new InfomationGroup(id("nested"), Component.literal("root").append(sibling), 0);
        sibling.append("changed");
        check(nested.title().getString().equals("rootchild"), "nested sibling detached on input");
        ((net.minecraft.network.chat.MutableComponent) nested.title().getSiblings().getFirst()).append("changed");
        check(nested.title().getString().equals("rootchild"), "nested sibling detached on output");
        var argument = Component.literal("arg");
        var translated = new InformationValue.Text(Component.translatableWithFallback("verification.missing_key", "%s", argument));
        argument.append("changed");
        check(translated.text().getString().equals("arg"), "translation arguments detached on input");
        var contents = (net.minecraft.network.chat.contents.TranslatableContents) translated.text().getContents();
        ((net.minecraft.network.chat.MutableComponent) contents.getArgs()[0]).append("changed");
        check(translated.text().getString().equals("arg"), "translation arguments detached on output");
        check(InformationValue.CODEC.parse(JsonOps.INSTANCE, com.google.gson.JsonParser.parseString("{\"type\":\"unknown\"}")).isError(), "unknown display kind rejected as codec error");
    }

    private static void importance() {
        PlayerInformationRegistry registry = new PlayerInformationRegistry();
        InfomationGroup group = registry.registerGroup(id("importance"), Component.literal("Importance"), 0);
        PlayerInformationRowProvider provider = (view, context) -> Optional.of(new InformationRowContent(
                Component.literal("Value"), new InformationValue.Number(1), Set.of(id("private_field"))));
        registry.register(id("ordinary"), group, 0, provider);
        registry.register(id("essential"), group, 10, 100, provider);
        registry.register(id("detail"), group, 20, -10, provider);
        registry.register(id("important"), group, 30, 50, provider);
        PlayerViewSnapshot view = PlayerViewSnapshot.builder(meta("importance"), NbtOps.INSTANCE).build();
        InformationDocument all = registry.collectAll(view, InformationContext.DEFAULT);
        check(all.rows().stream().map(InformationRowResult::importance).toList().equals(List.of(0, 100, -10, 50)),
                "importance defaults to zero and does not replace display order");
        check(registry.collect(view, InformationContext.DEFAULT, group).rows().equals(all.rows()), "importance retained by group collection");
        check(all.withMinimumImportance(50).rows().stream().map(InformationRowResult::id).toList().equals(List.of(id("essential"), id("important"))),
                "importance threshold is inclusive and preserves display order");
        check(all.withMinimumImportance(Integer.MIN_VALUE).rows().equals(all.rows()), "minimum threshold keeps every int importance");
        check(all.withMinimumImportance(101).rows().isEmpty(), "threshold can hide every row");
        check(all.rows().size() == 4, "importance filtering leaves source document intact");
        check(all.withMinimumImportance(50).filter(field -> !field.equals(id("private_field"))).rows().isEmpty(),
                "importance filtering preserves permission provenance");
        InformationRowResult explicit = all.rows().get(1);
        check(new InformationRowResult(explicit.id(), group, explicit.order(), explicit.content()).importance() == 0,
                "legacy constructor defaults importance to zero");
        PlayerInformation.register(id("importance_facade"), PlayerInformation.BASIC, 1000, 75, provider);
        check(PlayerInformation.collectAll(view).rows().stream().filter(row -> row.id().equals(id("importance_facade")))
                .findFirst().orElseThrow().importance() == 75, "facade carries explicit importance");
    }

    private static void suppliedData() {
        CompoundTag data = new CompoundTag();
        data.putFloat("Health", 3);
        data.putInt("XpLevel", 5);
        data.putInt("XpTotal", 55);
        data.putFloat("XpP", 0.25F);
        SavedPlayerViewContext context = new SavedPlayerViewContext(meta("saved"), RegistryAccess.EMPTY, data, Optional.of("{}"), Optional.of("{}"));
        data.putFloat("Health", 99);
        PlayerViewSnapshot view = PlayerViews.parse(context);
        check(view.read(EntityValueGetters.HEALTH).value().orElseThrow() == 3, "supplied NBT detached");
        check(view.read(EntityValueGetters.HEALTH).origin() == ValueOrigin.SAVED, "saved provenance retained");
        check(view.read(EntityValueGetters.EXPERIENCE).value().orElseThrow().level() == 5, "experience parsed");
        check(view.read(EntityValueGetters.MAX_HEALTH).missingReason().orElseThrow() == MissingReason.NOT_CAPTURED, "no invented historical max health");
        check(view.read(EntityValueGetters.INVENTORY).missingReason().orElseThrow() == MissingReason.NOT_CAPTURED, "absent inventory is not empty");
        check(!view.getterIds().contains(EntityValueGetters.PROFILE.type()) && !view.getterIds().contains(EntityValueGetters.TEAM.type()),
                "uncaptured saved fields remain absent so host can supplement them");
        var host = view.toBuilder();
        if (!host.contains(EntityValueGetters.TEAM)) host.put(EntityValueGetters.TEAM, "host-team", ValueOrigin.SAVED);
        check(host.build().read(EntityValueGetters.TEAM).value().orElseThrow().equals("host-team"), "host supplementation after saved parsing still works");
        PlayerViewSnapshot.Builder recorded = PlayerViewSnapshot.builder(context.meta(), NbtOps.INSTANCE);
        recorded.put(EntityValueGetters.HEALTH, 10.0);
        PlayerViewSnapshot merged = PlayerViews.parse(context, Optional.of(recorded.build()));
        check(merged.read(EntityValueGetters.HEALTH).value().orElseThrow() == 10 && merged.read(EntityValueGetters.EXPERIENCE).isKnown(), "recorded fields take precedence with legacy fallback");
        expectFailure(() -> PlayerViews.parse(context, Optional.of(PlayerViewSnapshot.builder(meta("wrong"), NbtOps.INSTANCE).build())), "wrong record rejected");
        CompoundTag corrupt = new CompoundTag();
        corrupt.putString("foodLevel", "bad");
        corrupt.putFloat("Health", 6);
        corrupt.put("Inventory", new net.minecraft.nbt.ListTag());
        PlayerViewSnapshot partial = PlayerViews.parse(new SavedPlayerViewContext(meta("partial"), RegistryAccess.EMPTY, corrupt,
                Optional.of("{\"stats\":{}}"), Optional.empty()));
        check(partial.read(EntityValueGetters.FOOD).missingReason().orElseThrow() == MissingReason.INVALID, "bad saved field is invalid");
        check(partial.read(EntityValueGetters.HEALTH).value().orElseThrow() == 6 && partial.read(EntityValueGetters.STATISTICS).isKnown(), "bad saved field does not stop later fields");
        check(partial.read(EntityValueGetters.INVENTORY).value().orElseThrow().isEmpty(), "explicit empty inventory is known");
        CompoundTag saturnData = new CompoundTag();
        CompoundTag attachments = new CompoundTag();
        CompoundTag resource = new CompoundTag();
        CompoundTag values = new CompoundTag();
        values.putDouble("absent:mana", 12);
        resource.put("values", values);
        resource.put("audit", new CompoundTag());
        attachments.put("saturn:resource_holder", resource);
        saturnData.put("neoforge:attachments", attachments);
        PlayerViewSnapshot resourceView = PlayerViews.parse(new SavedPlayerViewContext(meta("resources"), RegistryAccess.EMPTY, saturnData, Optional.empty(), Optional.empty()));
        PlayerViewData.Resource mana = resourceView.read(EntityValueGetters.RESOURCES).value().orElseThrow().getFirst();
        check(mana.value() == 12 && mana.maximum().isEmpty(), "saved resources need neither current definitions nor invented bounds");
    }

    private static void queries() {
        Identifier health = Identifier.parse("minecraft:max_health"), speed = Identifier.parse("minecraft:movement_speed");
        var baseHealth = new AttributeGetter(health, AttributeGetter.Value.BASE);
        var effectiveHealth = new AttributeGetter(health, AttributeGetter.Value.EFFECTIVE);
        var baseSpeed = new AttributeGetter(speed, AttributeGetter.Value.BASE);
        var builder = PlayerViewSnapshot.builder(meta("queries"), NbtOps.INSTANCE);
        builder.put(EntityValueGetters.ATTRIBUTES, List.of(
                new PlayerViewData.Attribute(speed, 0.1, Optional.of(0.12)),
                new PlayerViewData.Attribute(health, 20, Optional.of(24.0))), ValueOrigin.SAVED);
        var view = builder.build();
        check(view.read(baseHealth).value().orElseThrow() == 20 && view.read(effectiveHealth).value().orElseThrow() == 24,
                "attribute getter selects id and enum value without relying on list order");
        check(view.read(baseSpeed).value().orElseThrow() == 0.1 && view.read(baseSpeed).origin() == ValueOrigin.SAVED,
                "projected query keeps recorded value and origin");
        check(view.read(new AttributeGetter(id("absent"), AttributeGetter.Value.BASE)).missingReason().orElseThrow() == MissingReason.NOT_APPLICABLE,
                "absent attribute differs from absent capture");
        builder.put(baseHealth, 30.0, ValueOrigin.CAPTURED);
        builder.put(effectiveHealth, 35.0, ValueOrigin.CAPTURED);
        builder.put(baseSpeed, 0.2, ValueOrigin.CAPTURED);
        var explicit = builder.build();
        check(explicit.read(baseHealth).value().orElseThrow() == 30 && explicit.read(effectiveHealth).value().orElseThrow() == 35
                && explicit.read(baseSpeed).value().orElseThrow() == 0.2, "full getter parameters identify independent captured entries");
        var decoded = PlayerViewSnapshot.CODEC.parse(JsonOps.INSTANCE, PlayerViewSnapshot.CODEC.encodeStart(JsonOps.INSTANCE, explicit).getOrThrow()).getOrThrow();
        check(decoded.read(effectiveHealth).value().orElseThrow() == 35, "parameterized query JSON roundtrip");
        var nbt = PlayerViewSnapshot.CODEC.encodeStart(NbtOps.INSTANCE, explicit).getOrThrow();
        check(PlayerViewSnapshot.CODEC.parse(NbtOps.INSTANCE, nbt).getOrThrow().read(baseSpeed).value().orElseThrow() == 0.2,
                "parameterized query NBT roundtrip");
        var evaluation = new com.iafenvoy.saturn.data.information.expression.InformationEvaluation(explicit, InformationContext.DEFAULT);
        check(evaluation.getter(baseHealth, List.of()).number() == 30 && evaluation.getter(baseSpeed, List.of()).number() == 0.2,
                "evaluation cache includes every getter argument");
        builder.missing(effectiveHealth, MissingReason.REDACTED);
        check(builder.build().read(effectiveHealth).missingReason().orElseThrow() == MissingReason.REDACTED, "explicit missing query blocks broader fallback");
        var filtered = explicit.filter(id -> !id.equals(Identifier.parse("saturn:attributes")) && !id.equals(Identifier.parse("saturn:attribute")));
        check(!filtered.read(baseHealth).isKnown() && !filtered.getterIds().contains(Identifier.parse("saturn:attribute")), "wire filtering removes parameterized queries and dependencies");
        check(EntityValueGetters.sources(baseHealth).containsAll(Set.of(Identifier.parse("saturn:attribute"), Identifier.parse("saturn:attributes"))),
                "query provenance includes broader source facts");
        var json = PlayerViewSnapshot.CODEC.encodeStart(JsonOps.INSTANCE, explicit).getOrThrow().getAsJsonObject();
        var unknown = com.google.gson.JsonParser.parseString("""
                {"getter":{"type":"absent:query","argument":"kept"},"data":{"schema":9,"state":"custom","payload":[1,2]}}
                """);
        json.getAsJsonArray("queries").add(unknown);
        var roundtrip = PlayerViewSnapshot.CODEC.encodeStart(JsonOps.INSTANCE, PlayerViewSnapshot.CODEC.parse(JsonOps.INSTANCE, json).getOrThrow()).getOrThrow();
        check(roundtrip.getAsJsonObject().getAsJsonArray("queries").contains(unknown), "uninstalled parameter getter definition and payload survive roundtrip");
        var key = EntityValueKey.of(baseHealth, JsonOps.INSTANCE);
        key.definition().getAsJsonObject().addProperty("attribute", "changed");
        check(key.equals(EntityValueKey.of(baseHealth, JsonOps.INSTANCE)), "query key definition is detached");
        var resource = new ResourceGetter(id("mana"), ResourceGetter.Value.CURRENT);
        var resources = PlayerViewSnapshot.builder(meta("resources_query"), NbtOps.INSTANCE);
        resources.put(EntityValueGetters.RESOURCES, List.of(new PlayerViewData.Resource(id("mana"), 12, Optional.empty(), Optional.empty(), 3)));
        check(resources.build().read(resource).value().orElseThrow() == 12 && !resources.build().read(new ResourceGetter(id("mana"), ResourceGetter.Value.MAXIMUM)).isKnown(),
                "resource query preserves value and unknown historical bounds");
        check(AttributeGetter.Value.CODEC.parse(JsonOps.INSTANCE, new com.google.gson.JsonPrimitive("wrong")).isError(), "unknown enum option is a codec error");
    }

    private static void contracts() {
        check(EntityValueGetter.CODEC.parse(JsonOps.INSTANCE, com.google.gson.JsonParser.parseString("{\"type\":\"absent:getter\"}")).isError(), "unknown getter type has no default fallback");
        EntityValueGetter<Integer> custom = register("owned", Codec.INT, context -> ViewValue.known(7, ValueOrigin.SAVED));
        check(SaturnRegistries.ENTITY_VALUE_GETTER.getValue(custom.type()) == custom.codec() && EntityValueGetters.defaults(JsonOps.INSTANCE).contains(custom),
                "native field registration is used by lookups and enumeration");
        EntityValueGetter<Integer> broken = register("broken_reader", Codec.INT, context -> { throw new IllegalStateException("Expected field failure"); });
        EntityValueGetter<Integer> invalid = register("invalid_payload", Codec.intRange(0, 5), context -> ViewValue.known(9, ValueOrigin.SAVED));
        EntityValueGetter<Integer> nullResult = register("null_result", Codec.INT, context -> null);
        int[] reads = {0};
        EntityValueGetter<Integer> counted = register("counted", Codec.INT, context -> {
            reads[0]++;
            return ViewValue.known(11, ValueOrigin.RECONSTRUCTED);
        });
        CompoundTag data = new CompoundTag();
        data.putFloat("Health", 4);
        SavedPlayerViewContext context = new SavedPlayerViewContext(meta("owned"), RegistryAccess.EMPTY, data, Optional.empty(), Optional.empty());
        PlayerViewSnapshot parsed = PlayerViews.parse(context);
        check(parsed.read(EntityValueGetters.HEALTH).value().orElseThrow() == 4 && parsed.read(custom).value().orElseThrow() == 7,
                "each field supplies only its own value");
        check(parsed.read(broken).missingReason().orElseThrow() == MissingReason.INVALID && parsed.read(custom).isKnown(),
                "throwing reader does not stop another field");
        check(parsed.read(invalid).missingReason().orElseThrow() == MissingReason.INVALID, "field codec failure isolated");
        check(parsed.read(nullResult).missingReason().orElseThrow() == MissingReason.INVALID, "null reader result isolated");
        check(parsed.read(counted).origin() == ValueOrigin.RECONSTRUCTED, "field reader preserves explicit value origin");

        int beforeMerge = reads[0];
        PlayerViewSnapshot.Builder recorded = PlayerViewSnapshot.builder(context.meta(), NbtOps.INSTANCE);
        recorded.missing(counted, MissingReason.REDACTED);
        PlayerViewSnapshot merged = PlayerViews.parse(context, Optional.of(recorded.build()));
        check(merged.read(counted).missingReason().orElseThrow() == MissingReason.REDACTED && reads[0] == beforeMerge,
                "recorded missing envelope is authoritative and never invokes reader");
        PlayerViewResult result = PlayerViewResult.success(8, parsed);
        check(PlayerViewResult.CODEC.parse(JsonOps.INSTANCE, PlayerViewResult.CODEC.encodeStart(JsonOps.INSTANCE, result).getOrThrow())
                .getOrThrow().requestId() == 8, "host result codec");

        EntityValueGetter<Integer> detached = new FixtureGetter<>(Codec.INT,
                ignored -> ViewValue.missing(MissingReason.NOT_CAPTURED));
        expectFailure(() -> PlayerViewSnapshot.builder(meta("invalid"), NbtOps.INSTANCE).put(detached, 1), "unregistered field cannot be written");
        check(detached.capture(null).missingReason().orElseThrow() == MissingReason.UNSUPPORTED, "default live capture is unsupported");
        EntityValueGetter<Integer> defaults = new EntityValueGetter<>() {
            public MapCodec<? extends EntityValueGetter<Integer>> codec() { return MapCodec.unit(this); }
            public Codec<Integer> valueCodec() { return Codec.INT; }
        };
        check(defaults.readSaved(context).missingReason().orElseThrow() == MissingReason.NOT_CAPTURED, "saved reader is optional");
        SaturnRegistries.ENTITY_VALUE_GETTER.freeze();
    }

    private static <T> EntityValueGetter<T> register(String path, Codec<T> codec, Function<SavedPlayerViewContext, ViewValue<T>> reader) {
        FixtureGetter<T> getter = new FixtureGetter<>(codec, reader);
        Registry.register(SaturnRegistries.ENTITY_VALUE_GETTER, id(path), getter.codec());
        return getter;
    }

    private static final class FixtureGetter<T> implements EntityValueGetter<T> {
        private final Codec<T> payload;
        private final Function<SavedPlayerViewContext, ViewValue<T>> parser;
        private final MapCodec<FixtureGetter<T>> codec = MapCodec.unit(this);

        private FixtureGetter(Codec<T> payload, Function<SavedPlayerViewContext, ViewValue<T>> parser) {
            this.payload = payload;
            this.parser = parser;
        }

        public MapCodec<FixtureGetter<T>> codec() { return this.codec; }
        public Codec<T> valueCodec() { return this.payload; }
        public ViewValue<T> readSaved(SavedPlayerViewContext context) { return this.parser.apply(context); }
    }

    private static PlayerViewMeta meta(String record) {
        return new PlayerViewMeta(new PlayerViewRef(new UUID(0, 1), id("life"), record), new UUID(0, 2),
                PlayerViewMeta.SourceKind.ARCHIVE, Optional.of(100L), Optional.of(20L), 200L);
    }

    private static net.minecraft.resources.Identifier id(String path) {
        return net.minecraft.resources.Identifier.fromNamespaceAndPath("verification", path);
    }

    private static void check(boolean condition, String name) {
        if (!condition) throw new AssertionError(name);
        checks++;
    }

    private static void expectFailure(Runnable action, String name) {
        try {
            action.run();
        } catch (IllegalArgumentException | IllegalStateException expected) {
            checks++;
            return;
        }
        throw new AssertionError(name);
    }
}
