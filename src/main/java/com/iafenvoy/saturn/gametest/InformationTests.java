package com.iafenvoy.saturn.gametest;

import com.google.gson.JsonArray;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.google.gson.JsonParser;
import com.iafenvoy.saturn.Saturn;
import com.iafenvoy.saturn.data.information.*;
import com.iafenvoy.saturn.data.information.expression.ExpressionValue;
import com.iafenvoy.saturn.data.information.expression.InformationEvaluation;
import com.iafenvoy.saturn.data.information.expression.InformationExpression;
import com.iafenvoy.saturn.data.information.getter.EntityValueGetters;
import com.iafenvoy.saturn.data.information.getter.builtin.ExperienceGetter;
import com.iafenvoy.saturn.data.information.model.*;
import com.iafenvoy.saturn.registry.SaturnResourceKeys;
import com.mojang.datafixers.util.Either;
import com.mojang.serialization.Codec;
import com.mojang.serialization.DynamicOps;
import com.mojang.serialization.JsonOps;
import com.mojang.serialization.Lifecycle;
import net.minecraft.core.*;
import net.minecraft.core.registries.Registries;
import net.minecraft.gametest.framework.FunctionGameTestInstance;
import net.minecraft.gametest.framework.GameTestHelper;
import net.minecraft.gametest.framework.TestData;
import net.minecraft.gametest.framework.TestEnvironmentDefinition;
import net.minecraft.nbt.NbtOps;
import net.minecraft.nbt.Tag;
import net.minecraft.network.chat.Component;
import net.minecraft.resources.Identifier;
import net.minecraft.resources.RegistryOps;
import net.minecraft.resources.ResourceKey;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.Items;
import net.neoforged.bus.api.IEventBus;
import net.neoforged.neoforge.event.RegisterGameTestsEvent;
import net.neoforged.neoforge.gametest.GameTestHooks;
import net.neoforged.neoforge.registries.DeferredRegister;

import java.util.*;
import java.util.function.Consumer;
import java.util.stream.Stream;

/**
 * Exercises native datapack loading and Component codecs in a real registry/bootstrap environment.
 */
public final class InformationTests {
    private InformationTests() {
    }

    public static void register(IEventBus bus) {
        if (!GameTestHooks.isGametestEnabled()) return;
        EntityValueGetterTests.register(bus);
        Map<String, Consumer<GameTestHelper>> tests = Map.of(
                "builtins", InformationTests::builtins, "expressions", InformationTests::expressions,
                "overrides", InformationTests::overrides, "document_codec", InformationTests::documentCodec,
                "group_selections", InformationTests::groupSelections,
                "field_registry", EntityValueGetterTests::registry,
                "live_fields", EntityValueGetterTests::live,
                "saved_fields", EntityValueGetterTests::saved,
                "command", InformationCommandTests::run);
        DeferredRegister<Consumer<GameTestHelper>> functions = DeferredRegister.create(Registries.TEST_FUNCTION, Saturn.MOD_ID);
        tests.forEach((name, test) -> functions.register("information/" + name, () -> test));
        functions.register(bus);
        bus.addListener((RegisterGameTestsEvent event) -> tests.forEach((name, test) -> {
            Holder<TestEnvironmentDefinition<?>> environment = event.registerEnvironment(id("information/" + name), new TestEnvironmentDefinition.AllOf(List.of()));
            event.registerTest(id("information/" + name), new FunctionGameTestInstance(ResourceKey.create(Registries.TEST_FUNCTION, id("information/" + name)),
                    new TestData<>(environment, id("gametest/empty"), 200, 0, true)));
        }));
    }

    private static PlayerViewSnapshot.Builder builder(GameTestHelper helper) {
        PlayerViewMeta meta = new PlayerViewMeta(PlayerViewRef.current(new UUID(0, 1)), new UUID(0, 2), PlayerViewMeta.SourceKind.ARCHIVE,
                Optional.of(100L), Optional.empty(), 200L);
        return PlayerViewSnapshot.builder(meta, helper.getLevel().registryAccess().createSerializationContext(NbtOps.INSTANCE));
    }

    private static void builtins(GameTestHelper helper) {
        RegistryAccess registries = helper.getLevel().registryAccess();
        helper.assertValueEqual(registries.lookupOrThrow(SaturnResourceKeys.INFORMATION_ROW).listElements().count(), 16L, "all builtin rows loaded from JSON");
        helper.assertValueEqual(registries.lookupOrThrow(SaturnResourceKeys.INFORMATION_GROUP).listElements().count(), 3L, "builtin groups loaded");
        PlayerViewSnapshot.Builder builder = builder(helper);
        builder.put(EntityValueGetters.PROFILE, new PlayerViewData.Profile("A", Component.literal("Styled").withColor(0x123456), Map.of()));
        builder.put(EntityValueGetters.HEALTH, 7.0);
        builder.put(EntityValueGetters.MAX_HEALTH, 20.0);
        builder.put(EntityValueGetters.FOOD, new PlayerViewData.Food(12, 2, 0));
        builder.put(EntityValueGetters.EXPERIENCE, new PlayerViewData.Experience(3, 27, 0.2));
        builder.put(EntityValueGetters.LOCATION, new PlayerViewData.Location(id("dimension"), 0, 0, 0, 0, 0));
        builder.put(EntityValueGetters.GAME_MODE, "survival");
        builder.put(EntityValueGetters.ABSORPTION, 0.0);
        builder.put(EntityValueGetters.AIR, 300);
        builder.put(EntityValueGetters.EFFECTS, List.of(new PlayerViewData.Effect(Identifier.parse("minecraft:speed"), 0, 100, false, true)));
        builder.put(EntityValueGetters.ATTRIBUTES, List.of(new PlayerViewData.Attribute(id("attribute"), 2, Optional.of(4.0))));
        builder.put(EntityValueGetters.RESOURCES, List.of(new PlayerViewData.Resource(id("resource"), 3, Optional.of(0.0), Optional.of(10.0), 0)));
        builder.put(EntityValueGetters.ABILITIES, List.of(id("ability")));
        Map<String, PlayerViewItem> items = Map.of("0", PlayerViewItem.capture(new ItemStack(Items.DIAMOND, 2), registries));
        builder.put(EntityValueGetters.EQUIPMENT, items);
        builder.put(EntityValueGetters.INVENTORY, items);
        builder.put(EntityValueGetters.ENDER_CHEST, Map.of());
        builder.put(EntityValueGetters.CARRIED_ITEM, Map.of());
        PlayerViewSnapshot view = builder.build();
        InformationDocument document = PlayerInformation.collectAll(view);
        helper.assertValueEqual(document.rows().size(), 16, "all 16 definitions evaluated");
        for (InformationRowResult row : document.rows())
            helper.assertTrue(!(row.content().value() instanceof InformationValue.Missing), "builtin must evaluate: " + row.id());
        helper.assertValueEqual(((InformationValue.Meter) row(document, "health").content().value()).value(), 7.0, "meter reads multiple fields");
        helper.assertTrue(row(document, "health").content().sourceFields().containsAll(Set.of(id("health"), id("max_health"))), "fallback dependencies retained");
        helper.assertValueEqual(((InformationValue.Text) row(document, "name").content().value()).text().getStyle().getColor().getValue(), 0x123456, "component style retained");
        helper.assertValueEqual(((InformationValue.Table) row(document, "resources").content().value()).rowIds(), List.of("saturn:resource"), "table row ids come from local scope");
        helper.assertValueEqual(((InformationValue.Items) row(document, "inventory").content().value()).items().get("0").count(), 2, "item codec keeps structured data");
        helper.assertValueEqual(row(document, "game_mode").importance(), 40, "game mode importance");
        helper.assertValueEqual(row(document, "abilities").importance(), 70, "ability importance");
        helper.assertValueEqual(row(document, "resources").importance(), 30, "resource importance");
        builder.missing(EntityValueGetters.MAX_HEALTH, MissingReason.NOT_CAPTURED);
        helper.assertTrue(row(PlayerInformation.collectAll(builder.build()), "health").content().value() instanceof InformationValue.Number, "missing max health falls back to number");
        helper.assertValueEqual(PlayerInformation.collect(view, PlayerInformation.BASIC).rows(), document.rows(PlayerInformation.BASIC), "group collection retains order");
        helper.succeed();
    }

    private static ExpressionValue evaluate(GameTestHelper helper, String json) {
        PlayerViewSnapshot view = builder(helper).build();
        DynamicOps<JsonElement> ops = view.serializationOps();
        InformationExpression expression = InformationExpression.CODEC.parse(ops, JsonParser.parseString(json)).getOrThrow();
        JsonElement encoded = InformationExpression.CODEC.encodeStart(ops, expression).getOrThrow();
        expression = InformationExpression.CODEC.parse(ops, encoded).getOrThrow();
        return new InformationEvaluation(view, InformationContext.DEFAULT).evaluate(expression);
    }

    private static void expressions(GameTestHelper helper) {
        helper.assertValueEqual(evaluate(helper, "0").number(), 0.0, "zero literal");
        helper.assertTrue(!evaluate(helper, "false").flag(), "false literal");
        helper.assertValueEqual(evaluate(helper, """
                {"type":"saturn:arithmetic","operation":"add","left":2,"right":3}
                """).number(), 5.0, "nested typed codec roundtrip");
        helper.assertTrue(evaluate(helper, """
                {"type":"saturn:arithmetic","operation":"divide","left":1,"right":0}
                """) instanceof ExpressionValue.Missing, "nonfinite rejected");
        helper.assertValueEqual(evaluate(helper, """
                {"type":"saturn:coalesce","values":[{"type":"saturn:getter","getter":{"type":"saturn:max_health"}},0]}
                """).number(), 0.0, "unknown mod field can fall back to zero");
        helper.assertValueEqual(evaluate(helper, """
                {"type":"saturn:if","condition":true,"then":"yes","else":{"type":"saturn:getter","getter":{"type":"saturn:max_health"}}}
                """).text().getString(), "yes", "unused branch not evaluated");
        helper.assertTrue(evaluate(helper, """
                {"type":"saturn:number","value":"wrong"}
                """) instanceof ExpressionValue.Missing, "wrong operand type stays missing");
        helper.assertValueEqual(((InformationValue.Values) evaluate(helper, """
                {"type":"saturn:list","source":{"type":"saturn:constant","value":[]},"element":{"type":"saturn:local"}}
                """).information()).values().size(), 0, "empty list remains known");
        helper.assertValueEqual(evaluate(helper, """
                {"type":"saturn:format","format":"%.1f","arguments":[3]}
                """).text().getString(), "3.0", "format receives numeric arguments");
        RegistryOps<JsonElement> ops = helper.getLevel().registryAccess().createSerializationContext(JsonOps.INSTANCE);
        helper.assertTrue(InformationExpression.CODEC.parse(ops, JsonParser.parseString("{\"type\":\"absent:expression\"}")).isError(), "unknown expression rejected");
        helper.assertTrue(InformationExpression.CODEC.parse(ops, JsonParser.parseString("{\"value\":0}")).isError(), "typed objects require explicit type");
        helper.assertTrue(InformationExpression.CODEC.parse(ops, JsonParser.parseString("{\"type\":\"saturn:logic\",\"operation\":\"not\",\"values\":[]}")).isError(), "invalid operation shape rejected");
        ExperienceGetter field = EntityValueGetters.EXPERIENCE;
        PlayerViewSnapshot.Builder builder = builder(helper);
        builder.put(field, new PlayerViewData.Experience(5, 0, 0));
        InformationEvaluation evaluation = new InformationEvaluation(builder.build(), InformationContext.DEFAULT);
        helper.assertValueEqual(evaluation.getter(field, List.of("level")).number(), 5.0, "field codec member path");
        helper.assertTrue(evaluation.getter(field, List.of("absent")) instanceof ExpressionValue.Missing, "absent member is missing");
        PlayerViewSnapshot.Builder componentView = builder(helper);
        componentView.put(EntityValueGetters.ABILITIES, List.of(id("first"), id("second")));
        helper.assertValueEqual(new InformationEvaluation(componentView.build(), InformationContext.DEFAULT).getter(EntityValueGetters.ABILITIES, List.of("1")).decode(Codec.STRING), "saturn:second", "array path index");
        helper.assertTrue(new InformationEvaluation(componentView.build(), InformationContext.DEFAULT).getter(EntityValueGetters.ABILITIES, List.of("2")) instanceof ExpressionValue.Missing, "array bounds become missing");
        ExpressionValue table = evaluate(helper, """
                {"type":"saturn:table","source":{"type":"saturn:constant","value":[{"id":"same"},{"id":"same"}]},
                 "row_id":{"type":"saturn:local","path":["id"]},"columns":[]}
                """);
        helper.assertTrue(table instanceof ExpressionValue.Missing, "duplicate table rows rejected");
        InformationRow hidden = definition(helper, """
                {"label":"Missing","missing":"hide","value":{"type":"saturn:getter","getter":{"type":"saturn:max_health"}}}
                """);
        helper.assertTrue(hidden.collect(evaluation).isEmpty(), "row missing policy hides unknown data");
        InformationRow provenance = definition(helper, """
                {"label":"X","value":{"type":"saturn:if","condition":true,"then":0,
                 "else":{"type":"saturn:getter","getter":{"type":"saturn:statistics"}}}}
                """);
        helper.assertTrue(provenance.sourceFields().contains(Identifier.parse("saturn:statistics")), "unused branch dependencies remain declared");
        InformationRowContent captions = definition(helper, """
                {"label":"Fallback","value":0,
                 "label_expression":{"type":"saturn:getter","getter":{"type":"saturn:team"}},"tooltip_expression":"Available tooltip"}
                """).collect(evaluation).orElseThrow();
        helper.assertValueEqual(captions.label().getString(), "Fallback", "missing label preserves static fallback");
        helper.assertValueEqual(captions.tooltip().orElseThrow().getString(), "Available tooltip", "tooltip independent of missing label");
        helper.succeed();
    }

    private static InformationRow definition(GameTestHelper helper, String json) {
        return InformationRow.CODEC.parse(helper.getLevel().registryAccess().createSerializationContext(JsonOps.INSTANCE), JsonParser.parseString(json)).getOrThrow();
    }

    private static void overrides(GameTestHelper helper) {
        MappedRegistry<InformationRow> rows = new MappedRegistry<>(SaturnResourceKeys.INFORMATION_ROW, Lifecycle.stable());
        Registry.register(rows, id("shared"), definition(helper, """
                {"importance":90,"label":"Shared","value":{"type":"saturn:getter","getter":{"type":"saturn:health"}},
                 "visible":{"type":"saturn:is_known","value":{"type":"saturn:getter","getter":{"type":"saturn:max_health"}}},
                 "tooltip_expression":{"type":"saturn:text","value":{"type":"saturn:getter","getter":{"type":"saturn:air"}}}}
                """));
        Registry.register(rows, id("disabled"), definition(helper, """
                {"enabled":false,"label":"Disabled","value":0}
                """));
        rows.freeze();
        Holder.Reference<InformationRow> shared = rows.get(ResourceKey.create(SaturnResourceKeys.INFORMATION_ROW, id("shared"))).orElseThrow();
        Holder.Reference<InformationRow> disabled = rows.get(ResourceKey.create(SaturnResourceKeys.INFORMATION_ROW, id("disabled"))).orElseThrow();
        MappedRegistry<InformationGroup> groups = new MappedRegistry<>(SaturnResourceKeys.INFORMATION_GROUP, Lifecycle.stable());
        Registry.register(groups, id("basic"), new InformationGroup(Component.literal("Changed"), 0, List.of(Either.left(shared), Either.left(disabled))));
        Registry.register(groups, id("extra"), new InformationGroup(Component.literal("Extra"), 10, List.of(Either.left(shared))));
        groups.freeze();
        HolderLookup.Provider provider = HolderLookup.Provider.create(Stream.of(groups, rows));
        InformationContext context = new InformationContext("en_us", provider);
        PlayerInformationRegistry registry = new PlayerInformationRegistry();
        InfomationGroup basic = registry.registerGroup(id("basic"), Component.literal("Original"), 100);
        registry.register(id("shared"), basic, 0, (v, c) -> Optional.of(new InformationRowContent(Component.literal("Code"), new InformationValue.Number(-1), Set.of())));
        registry.register(id("disabled"), basic, 0, (v, c) -> Optional.of(new InformationRowContent(Component.literal("Code"), new InformationValue.Number(-1), Set.of())));
        PlayerViewSnapshot.Builder builder = builder(helper);
        builder.put(EntityValueGetters.HEALTH, 8.0);
        builder.put(EntityValueGetters.MAX_HEALTH, 20.0);
        builder.put(EntityValueGetters.AIR, 100);
        PlayerViewSnapshot view = builder.build();
        InformationDocument all = registry.collectAll(view, context);
        helper.assertValueEqual(all.rows().size(), 1, "data overrides code and disabled suppresses row");
        helper.assertValueEqual(((InformationValue.Number) all.rows().getFirst().content().value()).value(), 8.0, "data provider replaces code provider");
        helper.assertValueEqual(all.groups().size(), 2, "multi group metadata");
        helper.assertValueEqual(all.rows().getFirst().groups().getFirst().title().getString(), "Changed", "data overrides code group metadata");
        helper.assertValueEqual(registry.collect(view, context, Set.copyOf(all.groups())).rows().size(), 1, "union deduplicated");
        helper.assertTrue(all.filter(id -> !id.equals(id("max_health"))).rows().isEmpty(), "visibility dependencies filtered");
        helper.assertTrue(all.filter(id -> !id.equals(id("air"))).rows().isEmpty(), "tooltip dependencies filtered");
        helper.assertTrue(registry.collectAll(builder(helper).build(), context).rows().isEmpty(), "unknown visibility hides row");
        helper.assertValueEqual(registry.collectAll(view, new InformationContext("en_us", RegistryAccess.EMPTY)).rows().size(), 2, "world contexts do not leak overrides");

        // A row shared with an earlier group must still respect order inside the later group.
        InfomationGroup earlier = registry.registerGroup(id("earlier"), Component.literal("Earlier"), -10);
        InfomationGroup later = registry.registerGroup(id("later"), Component.literal("Later"), 20);
        registry.register(id("late_shared"), List.of(earlier, later), 100, (v, c) -> Optional.of(new InformationRowContent(Component.literal("Late"), new InformationValue.Number(1), Set.of())));
        registry.register(id("early_single"), later, 0, (v, c) -> Optional.of(new InformationRowContent(Component.literal("Early"), new InformationValue.Number(1), Set.of())));
        InformationDocument sorted = registry.collect(view, context, later);
        helper.assertValueEqual(sorted.rows().stream().map(InformationRowResult::id).toList(), List.of(id("early_single"), id("late_shared")), "single group order independent of other memberships");
        helper.assertValueEqual(sorted.rows(), registry.collectAll(view, context).rows(later), "document group ordering matches direct collection");
        helper.succeed();
    }

    private static void groupSelections(GameTestHelper helper) {
        Registry<InformationRow> rows = helper.getLevel().registryAccess().lookupOrThrow(SaturnResourceKeys.INFORMATION_ROW);
        MappedRegistry<InformationGroup> groups = new MappedRegistry<>(SaturnResourceKeys.INFORMATION_GROUP, Lifecycle.stable());
        HolderLookup.Provider provider = HolderLookup.Provider.create(Stream.of(rows, groups));
        RegistryOps<JsonElement> ops = provider.createSerializationContext(JsonOps.INSTANCE);
        InformationGroup mixed = InformationGroup.CODEC.parse(ops, JsonParser.parseString("""
                {"title":"Mixed","order":0,"rows":["saturn:health","#saturn:basic","saturn:health","#saturn:not_defined"]}
                """)).getOrThrow();
        Registry.register(groups, id("basic"), mixed);
        Registry.register(groups, id("extra"), InformationGroup.CODEC.parse(ops, JsonParser.parseString("""
                {"title":"Extra","order":10,"rows":["saturn:health"]}
                """)).getOrThrow());
        Registry.register(groups, id("empty"), new InformationGroup(Component.literal("Empty"), 20, List.of()));
        groups.freeze();
        helper.assertTrue(mixed.rows().getFirst().left().isPresent() && mixed.rows().get(1).right().isPresent(), "references decode as Either Holder or Tag");
        JsonElement json = InformationGroup.CODEC.encodeStart(ops, mixed).getOrThrow();
        helper.assertTrue(json.getAsJsonObject().getAsJsonArray("rows").asList().stream().anyMatch(value -> value.getAsString().equals("#saturn:basic")), "group codec preserves tag references");
        helper.assertTrue(InformationGroup.CODEC.parse(ops, JsonParser.parseString("{\"title\":\"Bad\",\"rows\":[\"absent:row\"]}")).isError(), "unresolved direct row rejected");
        PlayerInformationRegistry registry = new PlayerInformationRegistry();
        InfomationGroup basic = registry.registerGroup(id("basic"), Component.literal("Code basic"), 0);
        registry.register(id("legacy_default"), basic, 0, (v, c) -> Optional.of(new InformationRowContent(Component.literal("Legacy"), new InformationValue.Number(1), Set.of())));
        PlayerViewSnapshot view = builder(helper).build();
        InformationContext context = new InformationContext("en_us", provider);
        InformationDocument all = registry.collectAll(view, context);
        helper.assertValueEqual(all.rows().size(), 17, "all includes rows not selected by any group");
        InformationDocument selected = registry.collect(view, context, basic);
        helper.assertValueEqual(selected.rows().size(), 6, "direct and tag overlap deduplicated; unknown tag empty");
        helper.assertTrue(selected.rows().stream().noneMatch(row -> row.id().equals(id("legacy_default"))), "data group replaces code group selections");
        helper.assertTrue(row(all, "legacy_default").groups().isEmpty(), "code row removed from group remains in all");
        helper.assertValueEqual(row(all, "health").groups().size(), 2, "same row selected by multiple groups");
        helper.assertTrue(row(all, "abilities").groups().isEmpty(), "unselected data row has no computed groups");
        helper.assertValueEqual(registry.collect(view, context, Set.of(basic, mixed.resolve(id("extra")))).rows().size(), 6, "union deduplicates shared row");
        helper.assertTrue(registry.collect(view, context, mixed.resolve(id("empty"))).rows().isEmpty(), "explicit empty group");
        helper.assertTrue(registry.collect(view, context, Set.of()).rows().isEmpty(), "empty group selection still means none");
        helper.assertTrue(registry.collectAll(view, context).filter(field -> !field.equals(id("health"))).rows().stream().noneMatch(row -> row.id().equals(id("health"))), "shared row field filtering preserved");
        helper.succeed();
    }

    private static void documentCodec(GameTestHelper helper) {
        RegistryOps<JsonElement> ops = helper.getLevel().registryAccess().createSerializationContext(JsonOps.INSTANCE);
        InfomationGroup extra = new InfomationGroup(id("extra"), Component.literal("Extra"), 100);
        InformationRowResult row = new InformationRowResult(id("shared"), List.of(PlayerInformation.BASIC, extra), 0, 70,
                new InformationRowContent(Component.literal("Label"), new InformationValue.Number(3), Set.of(id("health"))));
        JsonElement json = InformationRowResult.CODEC.encodeStart(ops, row).getOrThrow();
        helper.assertValueEqual(InformationRowResult.CODEC.parse(ops, json).getOrThrow().groups().size(), 2, "new multi group wire roundtrip");
        JsonObject legacy = json.deepCopy().getAsJsonObject();
        legacy.add("group", legacy.remove("groups").getAsJsonArray().get(0));
        legacy.remove("importance");
        helper.assertValueEqual(InformationRowResult.CODEC.parse(ops, legacy).getOrThrow().importance(), 0, "legacy wire with single group and no importance");
        JsonObject ungrouped = json.deepCopy().getAsJsonObject();
        ungrouped.add("groups", new JsonArray());
        helper.assertTrue(InformationRowResult.CODEC.parse(ops, ungrouped).getOrThrow().groups().isEmpty(), "ungrouped results remain serializable");
        helper.assertTrue(InformationRow.CODEC.parse(ops, JsonParser.parseString("{\"label\":\"X\",\"value\":0}")).isSuccess(), "row definitions are independent of groups");
        InformationDocument document = new InformationDocument(List.of(row));
        Tag tag = InformationDocument.CODEC.encodeStart(helper.getLevel().registryAccess().createSerializationContext(NbtOps.INSTANCE), document).getOrThrow();
        helper.assertValueEqual(InformationDocument.CODEC.parse(helper.getLevel().registryAccess().createSerializationContext(NbtOps.INSTANCE), tag).getOrThrow().rows().getFirst().groups().size(), 2, "NBT document roundtrip");
        helper.succeed();
    }

    private static InformationRowResult row(InformationDocument document, String name) {
        return document.rows().stream().filter(row -> row.id().equals(id(name))).findFirst().orElseThrow();
    }

    private static Identifier id(String path) {
        return Identifier.fromNamespaceAndPath(Saturn.MOD_ID, path);
    }
}
