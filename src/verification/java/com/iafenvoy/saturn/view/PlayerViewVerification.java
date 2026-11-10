package com.iafenvoy.saturn.view;

import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.google.gson.JsonParser;
import com.google.gson.JsonPrimitive;
import com.iafenvoy.saturn.api.playerview.*;
import com.iafenvoy.saturn.data.information.*;
import com.iafenvoy.saturn.data.information.expression.InformationEvaluation;
import com.iafenvoy.saturn.data.information.getter.*;
import com.iafenvoy.saturn.data.information.expression.builtin.ConstantExpression;
import com.iafenvoy.saturn.data.information.expression.builtin.GetterExpression;
import com.iafenvoy.saturn.registry.SaturnResourceKeys;
import com.mojang.datafixers.util.Either;
import com.mojang.serialization.Lifecycle;
import com.mojang.serialization.Dynamic;
import net.minecraft.core.Holder;
import net.minecraft.core.HolderLookup;
import net.minecraft.core.MappedRegistry;
import net.minecraft.nbt.ListTag;
import net.minecraft.nbt.Tag;
import net.minecraft.network.chat.MutableComponent;
import net.minecraft.network.chat.contents.TranslatableContents;
import net.minecraft.resources.ResourceKey;
import net.minecraft.tags.TagKey;
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
import java.util.stream.Stream;

/** Standalone codec and contract checks; no game, world, connection or player is created. */
public final class PlayerViewVerification {
    private static int checks;

    static void main(String[] args) {
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
        MappedRegistry<InformationRow> rows = new MappedRegistry<>(SaturnResourceKeys.INFORMATION_ROW, Lifecycle.stable());
        Registry.register(rows, id("realm"), numericRow(0, 0, true));
        Registry.register(rows, id("food"), numericRow(10, 0, true));
        Registry.register(rows, id("health"), numericRow(0, 0, true));
        Registry.register(rows, id("hidden"), numericRow(20, 0, false));
        Registry.register(rows, id("shared"), numericRow(40, 0, true));
        Registry.register(rows, id("no_groups"), numericRow(0, 0, true));
        rows.freeze();
        MappedRegistry<InformationGroup> groups = new MappedRegistry<>(SaturnResourceKeys.INFORMATION_GROUP, Lifecycle.stable());
        Holder<InformationGroup> cultivation = registerGroup(groups, rows, "cultivation", 10, "realm", "shared");
        Holder<InformationGroup> basic = registerGroup(groups, rows, "basic", 0, "health", "food", "hidden", "shared", "shared");
        Holder<InformationGroup> empty = registerGroup(groups, rows, "empty", 20);
        groups.freeze();
        InformationContext context = new InformationContext("en_us", HolderLookup.Provider.create(Stream.of(rows, groups)));
        PlayerViewSnapshot view = PlayerViewSnapshot.builder(meta("one"), NbtOps.INSTANCE).build();
        InformationDocument all = PlayerInformation.collectAll(view, context);
        check(all.rows().stream().map(InformationEntry::id).toList().equals(List.of(id("health"), id("food"), id("shared"), id("realm"), id("no_groups"))), "registry groups and ungrouped rows ordered");
        check(PlayerInformation.collect(view, context, basic).rows().equals(all.rows(basic)), "by group equals all filtered");
        check(PlayerInformation.collect(view, context, Set.of()).rows().isEmpty(), "empty selection means none");
        check(all.groups().equals(List.of(basic, cultivation)), "document groups include only groups with visible rows");
        check(PlayerInformation.groups(view, context).equals(List.of(basic, cultivation, empty)), "registry group query includes empty groups");
        check(all.groups().getFirst() == basic, "collection reuses the registry group holder");
        check(all.rows().getFirst().row() == rows.get(ResourceKey.create(SaturnResourceKeys.INFORMATION_ROW, id("health"))).orElseThrow(),
                "collection reuses the registry row holder");
        InformationValue number = new InformationValue.Number(7);
        check(InformationValue.CODEC.parse(JsonOps.INSTANCE, InformationValue.CODEC.encodeStart(JsonOps.INSTANCE, number)
                .getOrThrow()).getOrThrow().equals(number), "numeric display codec");
        check(PlayerInformation.collectAll(view).rows().isEmpty(), "detached view without registry does not borrow global world definitions");
        check(PlayerInformation.groups(view).isEmpty(), "detached view has no synthetic builtin groups");
        check(PlayerInformation.collect(view, context, cultivation).rows().stream().anyMatch(r -> r.id().equals(id("shared"))), "shared row belongs to both groups");
        check(PlayerInformation.collect(view, context, Set.of(basic, cultivation)).rows().stream().filter(r -> r.id().equals(id("shared"))).count() == 1,
                "union and all collect shared row once");
        check(all.rows().stream().filter(r -> r.id().equals(id("shared"))).findFirst().orElseThrow().groups().size() == 2,
                "duplicate group membership removed");
        check(all.rows().stream().anyMatch(r -> r.id().equals(id("no_groups")) && r.groups().isEmpty()), "ungrouped row included by all");
        check(PlayerInformation.collect(view, context, Set.of(basic, cultivation)).rows().stream().noneMatch(r -> r.id().equals(id("no_groups"))), "ungrouped row excluded from group union");
        check(PlayerInformation.collect(view, context, empty).rows().isEmpty(), "empty registry group collects no rows");
        check(PlayerInformation.collect(view, context, Holder.direct(new InformationGroup(Component.empty(), 0, List.of()))).rows().isEmpty(), "unregistered group cannot supply rows");
        MutableComponent title = Component.literal("Original");
        InformationGroup detached = new InformationGroup(title, 0, List.of());
        title.append("changed");
        check(detached.title().getString().equals("Original"), "group title detached");
        MutableComponent sibling = Component.literal("child");
        InformationGroup nested = new InformationGroup(Component.literal("root").append(sibling), 0, List.of());
        sibling.append("changed");
        check(nested.title().getString().equals("rootchild"), "nested sibling detached on input");
        ((MutableComponent) nested.title().getSiblings().getFirst()).append("changed");
        check(nested.title().getString().equals("rootchild"), "nested sibling detached on output");
        MutableComponent argument = Component.literal("arg");
        InformationValue.Text translated = new InformationValue.Text(Component.translatableWithFallback("verification.missing_key", "%s", argument));
        argument.append("changed");
        check(translated.text().getString().equals("arg"), "translation arguments detached on input");
        TranslatableContents contents = (TranslatableContents) translated.text().getContents();
        ((MutableComponent) contents.getArgs()[0]).append("changed");
        check(translated.text().getString().equals("arg"), "translation arguments detached on output");
        check(InformationValue.CODEC.parse(JsonOps.INSTANCE, JsonParser.parseString("{\"type\":\"unknown\"}")).isError(), "unknown display kind rejected as codec error");
    }

    private static void importance() {
        MappedRegistry<InformationRow> rows = new MappedRegistry<>(SaturnResourceKeys.INFORMATION_ROW, Lifecycle.stable());
        int[] importance = {0, 100, -10, 50};
        String[] names = {"ordinary", "essential", "detail", "important"};
        for (int i = 0; i < names.length; i++) {
            Registry.register(rows, id(names[i]), new InformationRow(i * 10, importance[i], true, Component.literal("Value"), Optional.empty(),
                    new GetterExpression(EntityValueGetters.HEALTH, List.of()), Optional.empty(), Optional.empty(),
                    InformationRow.Tone.NORMAL, Optional.empty(), InformationRow.MissingPolicy.SHOW));
        }
        rows.freeze();
        MappedRegistry<InformationGroup> groups = new MappedRegistry<>(SaturnResourceKeys.INFORMATION_GROUP, Lifecycle.stable());
        Holder<InformationGroup> group = registerGroup(groups, rows, "importance", 0, names);
        groups.freeze();
        InformationContext context = new InformationContext("en_us", HolderLookup.Provider.create(Stream.of(rows, groups)));
        PlayerViewSnapshot.Builder builder = PlayerViewSnapshot.builder(meta("importance"), NbtOps.INSTANCE);
        builder.put(EntityValueGetters.HEALTH, 1.0);
        PlayerViewSnapshot view = builder.build();
        InformationDocument all = PlayerInformation.collectAll(view, context);
        check(all.rows().stream().map(InformationEntry::importance).toList().equals(List.of(0, 100, -10, 50)),
                "registry importance does not replace display order");
        check(PlayerInformation.collect(view, context, group).rows().equals(all.rows()), "importance retained by group collection");
        check(all.withMinimumImportance(50).rows().stream().map(InformationEntry::id).toList().equals(List.of(id("essential"), id("important"))),
                "importance threshold is inclusive and preserves display order");
        check(all.withMinimumImportance(Integer.MIN_VALUE).rows().equals(all.rows()), "minimum threshold keeps every int importance");
        check(all.withMinimumImportance(101).rows().isEmpty(), "threshold can hide every row");
        check(all.rows().size() == 4, "importance filtering leaves source document intact");
        check(all.withMinimumImportance(50).filter(field -> !field.equals(EntityValueGetters.HEALTH.type())).rows().isEmpty(),
                "importance filtering preserves permission provenance");
        InformationEntry explicit = all.rows().get(1);
        check(explicit.importance() == explicit.row().value().importance(), "importance comes directly from the row definition");

    }

    private static InformationRow numericRow(int order, int importance, boolean enabled) {
        return new InformationRow(order, importance, enabled, Component.literal("Value"), Optional.empty(),
                new ConstantExpression(new Dynamic<>(JsonOps.INSTANCE, new JsonPrimitive(3))), Optional.empty(), Optional.empty(),
                InformationRow.Tone.NORMAL, Optional.empty(), InformationRow.MissingPolicy.SHOW);
    }

    private static Holder<InformationGroup> registerGroup(MappedRegistry<InformationGroup> groups, MappedRegistry<InformationRow> rows,
                                                 String name, int order, String... members) {
        List<Either<Holder<InformationRow>, TagKey<InformationRow>>> references = Arrays.stream(members)
                .map(member -> Either.<Holder<InformationRow>, TagKey<InformationRow>>left(rows.get(ResourceKey.create(SaturnResourceKeys.INFORMATION_ROW, id(member))).orElseThrow())).toList();
        InformationGroup definition = new InformationGroup(Component.literal(name), order, references);
        Registry.register(groups, id(name), definition);
        return groups.get(ResourceKey.create(SaturnResourceKeys.INFORMATION_GROUP, id(name))).orElseThrow();
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
        PlayerViewSnapshot.Builder host = view.toBuilder();
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
        corrupt.put("Inventory", new ListTag());
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
        AttributeGetter baseHealth = new AttributeGetter(health, AttributeGetter.Value.BASE);
        AttributeGetter effectiveHealth = new AttributeGetter(health, AttributeGetter.Value.EFFECTIVE);
        AttributeGetter baseSpeed = new AttributeGetter(speed, AttributeGetter.Value.BASE);
        PlayerViewSnapshot.Builder builder = PlayerViewSnapshot.builder(meta("queries"), NbtOps.INSTANCE);
        builder.put(EntityValueGetters.ATTRIBUTES, List.of(
                new PlayerViewData.Attribute(speed, 0.1, Optional.of(0.12)),
                new PlayerViewData.Attribute(health, 20, Optional.of(24.0))), ValueOrigin.SAVED);
        PlayerViewSnapshot view = builder.build();
        check(view.read(baseHealth).value().orElseThrow() == 20 && view.read(effectiveHealth).value().orElseThrow() == 24,
                "attribute getter selects id and enum value without relying on list order");
        check(view.read(baseSpeed).value().orElseThrow() == 0.1 && view.read(baseSpeed).origin() == ValueOrigin.SAVED,
                "projected query keeps recorded value and origin");
        check(view.read(new AttributeGetter(id("absent"), AttributeGetter.Value.BASE)).missingReason().orElseThrow() == MissingReason.NOT_APPLICABLE,
                "absent attribute differs from absent capture");
        builder.put(baseHealth, 30.0, ValueOrigin.CAPTURED);
        builder.put(effectiveHealth, 35.0, ValueOrigin.CAPTURED);
        builder.put(baseSpeed, 0.2, ValueOrigin.CAPTURED);
        PlayerViewSnapshot explicit = builder.build();
        check(explicit.read(baseHealth).value().orElseThrow() == 30 && explicit.read(effectiveHealth).value().orElseThrow() == 35
                && explicit.read(baseSpeed).value().orElseThrow() == 0.2, "full getter parameters identify independent captured entries");
        PlayerViewSnapshot decoded = PlayerViewSnapshot.CODEC.parse(JsonOps.INSTANCE, PlayerViewSnapshot.CODEC.encodeStart(JsonOps.INSTANCE, explicit).getOrThrow()).getOrThrow();
        check(decoded.read(effectiveHealth).value().orElseThrow() == 35, "parameterized query JSON roundtrip");
        Tag nbt = PlayerViewSnapshot.CODEC.encodeStart(NbtOps.INSTANCE, explicit).getOrThrow();
        check(PlayerViewSnapshot.CODEC.parse(NbtOps.INSTANCE, nbt).getOrThrow().read(baseSpeed).value().orElseThrow() == 0.2,
                "parameterized query NBT roundtrip");
        InformationEvaluation evaluation = new InformationEvaluation(explicit, InformationContext.DEFAULT);
        check(evaluation.getter(baseHealth, List.of()).number() == 30 && evaluation.getter(baseSpeed, List.of()).number() == 0.2,
                "evaluation cache includes every getter argument");
        builder.missing(effectiveHealth, MissingReason.REDACTED);
        check(builder.build().read(effectiveHealth).missingReason().orElseThrow() == MissingReason.REDACTED, "explicit missing query blocks broader fallback");
        PlayerViewSnapshot filtered = explicit.filter(id -> !id.equals(Identifier.parse("saturn:attributes")) && !id.equals(Identifier.parse("saturn:attribute")));
        check(!filtered.read(baseHealth).isKnown() && !filtered.getterIds().contains(Identifier.parse("saturn:attribute")), "wire filtering removes parameterized queries and dependencies");
        check(EntityValueGetters.sources(baseHealth).containsAll(Set.of(Identifier.parse("saturn:attribute"), Identifier.parse("saturn:attributes"))),
                "query provenance includes broader source facts");
        JsonObject json = PlayerViewSnapshot.CODEC.encodeStart(JsonOps.INSTANCE, explicit).getOrThrow().getAsJsonObject();
        JsonElement unknown = JsonParser.parseString("""
                {"getter":{"type":"absent:query","argument":"kept"},"data":{"schema":9,"state":"custom","payload":[1,2]}}
                """);
        json.getAsJsonArray("queries").add(unknown);
        JsonElement roundtrip = PlayerViewSnapshot.CODEC.encodeStart(JsonOps.INSTANCE, PlayerViewSnapshot.CODEC.parse(JsonOps.INSTANCE, json).getOrThrow()).getOrThrow();
        check(roundtrip.getAsJsonObject().getAsJsonArray("queries").contains(unknown), "uninstalled parameter getter definition and payload survive roundtrip");
        EntityValueKey key = EntityValueKey.of(baseHealth, JsonOps.INSTANCE);
        key.definition().getAsJsonObject().addProperty("attribute", "changed");
        check(key.equals(EntityValueKey.of(baseHealth, JsonOps.INSTANCE)), "query key definition is detached");
        ResourceGetter resource = new ResourceGetter(id("mana"), ResourceGetter.Value.CURRENT);
        PlayerViewSnapshot.Builder resources = PlayerViewSnapshot.builder(meta("resources_query"), NbtOps.INSTANCE);
        resources.put(EntityValueGetters.RESOURCES, List.of(new PlayerViewData.Resource(id("mana"), 12, Optional.empty(), Optional.empty(), 3)));
        check(resources.build().read(resource).value().orElseThrow() == 12 && !resources.build().read(new ResourceGetter(id("mana"), ResourceGetter.Value.MAXIMUM)).isKnown(),
                "resource query preserves value and unknown historical bounds");
        check(AttributeGetter.Value.CODEC.parse(JsonOps.INSTANCE, new JsonPrimitive("wrong")).isError(), "unknown enum option is a codec error");
    }

    private static void contracts() {
        check(EntityValueGetter.CODEC.parse(JsonOps.INSTANCE, JsonParser.parseString("{\"type\":\"absent:getter\"}")).isError(), "unknown getter type has no default fallback");
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
            @Override
            public MapCodec<? extends EntityValueGetter<Integer>> codec() { return MapCodec.unit(this); }
            @Override
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

        @Override
        public MapCodec<FixtureGetter<T>> codec() { return this.codec; }
        @Override
        public Codec<T> valueCodec() { return this.payload; }
        @Override
        public ViewValue<T> readSaved(SavedPlayerViewContext context) { return this.parser.apply(context); }
    }

    private static PlayerViewMeta meta(String record) {
        return new PlayerViewMeta(new PlayerViewRef(new UUID(0, 1), id("life"), record), new UUID(0, 2),
                PlayerViewMeta.SourceKind.ARCHIVE, Optional.of(100L), Optional.of(20L), 200L);
    }

    private static Identifier id(String path) {
        return Identifier.fromNamespaceAndPath("verification", path);
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
