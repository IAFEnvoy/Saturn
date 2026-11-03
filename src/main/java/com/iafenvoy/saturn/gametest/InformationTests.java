package com.iafenvoy.saturn.gametest;

import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.google.gson.JsonParser;
import com.google.gson.JsonPrimitive;
import com.iafenvoy.saturn.Saturn;
import com.iafenvoy.saturn.data.information.InformationGroup;
import com.iafenvoy.saturn.data.information.InformationRow;
import com.iafenvoy.saturn.data.information.PlayerInformation;
import com.iafenvoy.saturn.data.information.PlayerViewSnapshot;
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
import net.minecraft.network.chat.contents.TranslatableContents;
import net.minecraft.resources.Identifier;
import net.minecraft.resources.RegistryOps;
import net.minecraft.resources.ResourceKey;
import net.minecraft.world.entity.ai.attributes.Attribute;
import net.minecraft.world.entity.ai.attributes.Attributes;
import net.minecraft.world.entity.ai.attributes.RangedAttribute;
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
                "contexts", InformationTests::contexts, "document_codec", InformationTests::documentCodec,
                "group_selections", InformationTests::groupSelections,
                "field_registry", EntityValueGetterTests::registry,
                "live_fields", EntityValueGetterTests::live,
                "saved_fields", EntityValueGetterTests::saved);
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
        builder.put(EntityValueGetters.ATTRIBUTES, List.of(
                new PlayerViewData.Attribute(Identifier.parse("minecraft:max_health"), 20, Optional.of(24.0)),
                new PlayerViewData.Attribute(id("attribute"), 2, Optional.of(4.0))));
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
        for (InformationEntry row : document.rows())
            helper.assertTrue(!(row.value() instanceof InformationValue.Missing), "builtin must evaluate: " + row.id());
        helper.assertValueEqual(((InformationValue.Meter) row(document, "health").value()).value(), 7.0, "meter reads multiple fields");
        helper.assertTrue(row(document, "health").sourceFields().containsAll(Set.of(id("health"), id("max_health"))), "fallback dependencies retained");
        helper.assertValueEqual(((InformationValue.Text) row(document, "name").value()).text().getStyle().getColor().getValue(), 0x123456, "component style retained");
        helper.assertValueEqual(((InformationValue.Table) row(document, "resources").value()).rowIds(), List.of("saturn:resource"), "table row ids come from local scope");
        InformationValue.Table attributes = (InformationValue.Table) row(document, "attributes").value();
        Component attributeName = ((InformationValue.Text) attributes.rows().getFirst().getFirst()).text();
        helper.assertValueEqual(((TranslatableContents) attributeName.getContents()).getKey(), Attributes.MAX_HEALTH.value().getDescriptionId(),
                "attribute table keeps the native translation key for the client");
        helper.assertValueEqual(((InformationValue.Text) attributes.rows().get(1).getFirst()).text().getString(), "saturn:attribute",
                "uninstalled attributes keep their recorded id");
        helper.assertValueEqual(((InformationValue.Number) attributes.rows().getFirst().get(2)).value(), 24.0,
                "translating names preserves the recorded effective attribute value");
        helper.assertValueEqual(((InformationValue.Items) row(document, "inventory").value()).items().get("0").count(), 2, "item codec keeps structured data");
        helper.assertValueEqual(row(document, "game_mode").importance(), 40, "game mode importance");
        helper.assertValueEqual(row(document, "abilities").importance(), 70, "ability importance");
        helper.assertValueEqual(row(document, "resources").importance(), 30, "resource importance");
        builder.missing(EntityValueGetters.MAX_HEALTH, MissingReason.NOT_CAPTURED);
        helper.assertTrue(row(PlayerInformation.collectAll(builder.build()), "health").value() instanceof InformationValue.Number, "missing max health falls back to number");
        helper.assertValueEqual(PlayerInformation.collect(view, group(helper, "basic")).rows(), document.rows(group(helper, "basic")), "group collection retains order");
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
        InformationExpression attributeName = InformationExpression.CODEC.parse(JsonOps.INSTANCE, JsonParser.parseString("""
                {"type":"saturn:registry_name","registry":"minecraft:attribute","value":"saturn:custom_attribute"}
                """)).getOrThrow();
        MappedRegistry<Attribute> attributes = new MappedRegistry<>(Registries.ATTRIBUTE, Lifecycle.stable());
        Registry.register(attributes, id("custom_attribute"), new RangedAttribute("external.custom.translation", 1, 0, 100));
        attributes.freeze();
        InformationContext names = new InformationContext("en_us", HolderLookup.Provider.create(Stream.of(attributes)));
        Component customName = new InformationEvaluation(builder(helper).build(), names).evaluate(attributeName).text();
        helper.assertValueEqual(((TranslatableContents) customName.getContents()).getKey(), "external.custom.translation",
                "custom attributes use their actual description id instead of deriving a key from their registry id");
        helper.assertValueEqual(customName.getString(), "saturn:custom_attribute", "missing client translations fall back to the attribute id");
        helper.assertTrue(evaluate(helper, """
                {"type":"saturn:registry_name","registry":"minecraft:attribute","value":"invalid id"}
                """) instanceof ExpressionValue.Missing, "invalid name input remains missing");
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
        helper.assertTrue(InformationEntry.collect(Holder.direct(hidden), Set.of(), evaluation).isEmpty(), "row missing policy hides unknown data");
        InformationRow provenance = definition(helper, """
                {"label":"X","value":{"type":"saturn:if","condition":true,"then":0,
                 "else":{"type":"saturn:getter","getter":{"type":"saturn:statistics"}}}}
                """);
        helper.assertTrue(provenance.sourceFields().contains(Identifier.parse("saturn:statistics")), "unused branch dependencies remain declared");
        InformationEntry captions = InformationEntry.collect(Holder.direct(definition(helper, """
                {"label":"Fallback","value":0,
                 "label_expression":{"type":"saturn:getter","getter":{"type":"saturn:team"}},"tooltip_expression":"Available tooltip"}
                """)), Set.of(), evaluation).orElseThrow();
        helper.assertValueEqual(captions.label().getString(), "Fallback", "missing label preserves static fallback");
        helper.assertValueEqual(captions.tooltip().orElseThrow().getString(), "Available tooltip", "tooltip independent of missing label");
        helper.succeed();
    }

    private static InformationRow definition(GameTestHelper helper, String json) {
        return InformationRow.DIRECT_CODEC.parse(helper.getLevel().registryAccess().createSerializationContext(JsonOps.INSTANCE), JsonParser.parseString(json)).getOrThrow();
    }

    private static void contexts(GameTestHelper helper) {
        MappedRegistry<InformationRow> rows = new MappedRegistry<>(SaturnResourceKeys.INFORMATION_ROW, Lifecycle.stable());
        Registry.register(rows, id("shared"), definition(helper, """
                {"importance":90,"label":"Shared","value":{"type":"saturn:getter","getter":{"type":"saturn:health"}},
                 "visible":{"type":"saturn:is_known","value":{"type":"saturn:getter","getter":{"type":"saturn:max_health"}}},
                 "tooltip_expression":{"type":"saturn:text","value":{"type":"saturn:getter","getter":{"type":"saturn:air"}}}}
                """));
        Registry.register(rows, id("disabled"), definition(helper, """
                {"enabled":false,"label":"Disabled","value":0}
                """));
        Registry.register(rows, id("late_shared"), definition(helper, """
                {"order":100,"label":"Late","value":1}
                """));
        Registry.register(rows, id("early_single"), definition(helper, """
                {"label":"Early","value":1}
                """));
        rows.freeze();
        Holder.Reference<InformationRow> late = rows.get(ResourceKey.create(SaturnResourceKeys.INFORMATION_ROW, id("late_shared"))).orElseThrow();
        Holder.Reference<InformationRow> early = rows.get(ResourceKey.create(SaturnResourceKeys.INFORMATION_ROW, id("early_single"))).orElseThrow();
        Holder.Reference<InformationRow> shared = rows.get(ResourceKey.create(SaturnResourceKeys.INFORMATION_ROW, id("shared"))).orElseThrow();
        Holder.Reference<InformationRow> disabled = rows.get(ResourceKey.create(SaturnResourceKeys.INFORMATION_ROW, id("disabled"))).orElseThrow();
        MappedRegistry<InformationGroup> groups = new MappedRegistry<>(SaturnResourceKeys.INFORMATION_GROUP, Lifecycle.stable());
        Registry.register(groups, id("basic"), new InformationGroup(Component.literal("Changed"), 0, List.of(Either.left(shared), Either.left(disabled))));
        Registry.register(groups, id("extra"), new InformationGroup(Component.literal("Extra"), 10, List.of(Either.left(shared))));
        Registry.register(groups, id("earlier"), new InformationGroup(Component.literal("Earlier"), -10, List.of(Either.left(late))));
        Registry.register(groups, id("later"), new InformationGroup(Component.literal("Later"), 20, List.of(Either.left(late), Either.left(early))));
        groups.freeze();
        HolderLookup.Provider provider = HolderLookup.Provider.create(Stream.of(groups, rows));
        InformationContext context = new InformationContext("en_us", provider);
        PlayerViewSnapshot.Builder builder = builder(helper);
        builder.put(EntityValueGetters.HEALTH, 8.0);
        builder.put(EntityValueGetters.MAX_HEALTH, 20.0);
        builder.put(EntityValueGetters.AIR, 100);
        PlayerViewSnapshot view = builder.build();
        InformationDocument all = PlayerInformation.collectAll(view, context);
        helper.assertValueEqual(all.rows().size(), 3, "only enabled registry rows are evaluated");
        helper.assertValueEqual(((InformationValue.Number) row(all, "shared").value()).value(), 8.0, "registry expression reads supplied view");
        helper.assertValueEqual(all.groups().size(), 4, "multi group metadata comes from registry");
        helper.assertValueEqual(PlayerInformation.collect(view, context, Set.copyOf(all.groups())).rows().size(), 3, "union deduplicated");
        helper.assertTrue(all.filter(id -> !id.equals(id("max_health"))).rows().stream().noneMatch(row -> row.id().equals(id("shared"))), "visibility dependencies filtered");
        helper.assertTrue(all.filter(id -> !id.equals(id("air"))).rows().stream().noneMatch(row -> row.id().equals(id("shared"))), "tooltip dependencies filtered");
        helper.assertValueEqual(PlayerInformation.collectAll(builder(helper).build(), context).rows().size(), 2, "unknown visibility hides row");
        InformationContext empty = new InformationContext("en_us", RegistryAccess.EMPTY);
        helper.assertTrue(PlayerInformation.collectAll(view, empty).rows().isEmpty(), "explicit empty context does not borrow view registries");
        helper.assertTrue(PlayerInformation.groups(view, empty).isEmpty(), "no code group defaults without registries");
        helper.assertValueEqual(PlayerInformation.collectAll(view).rows().size(), 16, "view retains its original world definitions");
        helper.assertValueEqual(PlayerInformation.collectAll(view, context), all, "contexts do not leak between queries");

        // A row shared with an earlier group must still respect order inside the later group.
        Holder<InformationGroup> later = groups.get(ResourceKey.create(SaturnResourceKeys.INFORMATION_GROUP, id("later"))).orElseThrow();
        InformationDocument sorted = PlayerInformation.collect(view, context, later);
        helper.assertValueEqual(sorted.rows().stream().map(InformationEntry::id).toList(), List.of(id("early_single"), id("late_shared")), "single group order independent of other memberships");
        helper.assertValueEqual(sorted.rows(), all.rows(later), "document group ordering matches direct collection");
        helper.succeed();
    }

    private static void groupSelections(GameTestHelper helper) {
        Registry<InformationRow> rows = helper.getLevel().registryAccess().lookupOrThrow(SaturnResourceKeys.INFORMATION_ROW);
        MappedRegistry<InformationGroup> groups = new MappedRegistry<>(SaturnResourceKeys.INFORMATION_GROUP, Lifecycle.stable());
        HolderLookup.Provider provider = HolderLookup.Provider.create(Stream.of(rows, groups));
        RegistryOps<JsonElement> ops = provider.createSerializationContext(JsonOps.INSTANCE);
        InformationGroup mixed = InformationGroup.DIRECT_CODEC.parse(ops, JsonParser.parseString("""
                {"title":"Mixed","order":0,"rows":["saturn:health","#saturn:basic","saturn:health","#saturn:not_defined"]}
                """)).getOrThrow();
        Registry.register(groups, id("basic"), mixed);
        Registry.register(groups, id("extra"), InformationGroup.DIRECT_CODEC.parse(ops, JsonParser.parseString("""
                {"title":"Extra","order":10,"rows":["saturn:health"]}
                """)).getOrThrow());
        Registry.register(groups, id("empty"), new InformationGroup(Component.literal("Empty"), 20, List.of()));
        groups.freeze();
        helper.assertTrue(mixed.rows().getFirst().left().isPresent() && mixed.rows().get(1).right().isPresent(), "references decode as Either Holder or Tag");
        JsonElement json = InformationGroup.DIRECT_CODEC.encodeStart(ops, mixed).getOrThrow();
        helper.assertTrue(json.getAsJsonObject().getAsJsonArray("rows").asList().stream().anyMatch(value -> value.getAsString().equals("#saturn:basic")), "group codec preserves tag references");
        helper.assertTrue(InformationGroup.DIRECT_CODEC.parse(ops, JsonParser.parseString("{\"title\":\"Bad\",\"rows\":[\"absent:row\"]}")).isError(), "unresolved direct row rejected");
        Holder<InformationGroup> basic = groups.get(ResourceKey.create(SaturnResourceKeys.INFORMATION_GROUP, id("basic"))).orElseThrow();
        PlayerViewSnapshot view = builder(helper).build();
        InformationContext context = new InformationContext("en_us", provider);
        InformationDocument all = PlayerInformation.collectAll(view, context);
        helper.assertValueEqual(all.rows().size(), 16, "all includes rows not selected by any group");
        InformationDocument selected = PlayerInformation.collect(view, context, basic);
        helper.assertValueEqual(selected.rows().size(), 6, "direct and tag overlap deduplicated; unknown tag empty");
        helper.assertValueEqual(PlayerInformation.groups(view, context).size(), 3, "registry listing includes empty groups");
        helper.assertValueEqual(row(all, "health").groups().size(), 2, "same row selected by multiple groups");
        helper.assertTrue(row(all, "abilities").groups().isEmpty(), "unselected data row has no computed groups");
        helper.assertValueEqual(PlayerInformation.collect(view, context, Set.of(basic, groups.get(ResourceKey.create(SaturnResourceKeys.INFORMATION_GROUP, id("extra"))).orElseThrow())).rows().size(), 6, "union deduplicates shared row");
        helper.assertTrue(PlayerInformation.collect(view, context, groups.get(ResourceKey.create(SaturnResourceKeys.INFORMATION_GROUP, id("empty"))).orElseThrow()).rows().isEmpty(), "explicit empty group");
        helper.assertTrue(PlayerInformation.collect(view, context, Set.of()).rows().isEmpty(), "empty group selection still means none");
        helper.assertTrue(PlayerInformation.collectAll(view, context).filter(field -> !field.equals(id("health"))).rows().stream().noneMatch(row -> row.id().equals(id("health"))), "shared row field filtering preserved");
        helper.succeed();
    }

    private static void documentCodec(GameTestHelper helper) {
        RegistryOps<JsonElement> ops = helper.getLevel().registryAccess().createSerializationContext(JsonOps.INSTANCE);
        InformationEntry row = new InformationEntry(InformationRow.CODEC.parse(ops, new JsonPrimitive("saturn:health")).getOrThrow(),
                Set.of(group(helper, "basic"), group(helper, "status")), Component.literal("Label"), new InformationValue.Number(3), Optional.of(Component.literal("Tooltip")));
        JsonElement json = InformationEntry.CODEC.encodeStart(ops, row).getOrThrow();
        helper.assertValueEqual(json.getAsJsonObject().get("row").getAsString(), "saturn:health", "entry refers to the registered row by id");
        helper.assertTrue(!json.getAsJsonObject().has("order") && !json.getAsJsonObject().has("importance") && !json.getAsJsonObject().has("content"), "entry does not duplicate definition metadata or nest content");
        InformationEntry decoded = InformationEntry.CODEC.parse(ops, json).getOrThrow();
        helper.assertValueEqual(decoded, row, "flat entry JSON roundtrip");
        helper.assertTrue(decoded.row() == row.row(), "entry codec resolves the original registry holder");
        helper.assertValueEqual(decoded.importance(), row.row().value().importance(), "importance read from the registered definition");
        JsonObject ungrouped = json.deepCopy().getAsJsonObject();
        ungrouped.remove("groups");
        helper.assertTrue(InformationEntry.CODEC.parse(ops, ungrouped).getOrThrow().groups().isEmpty(), "ungrouped entries default to an empty set");
        JsonObject unknown = json.deepCopy().getAsJsonObject();
        unknown.addProperty("row", "absent:row");
        helper.assertTrue(InformationEntry.CODEC.parse(ops, unknown).isError(), "unregistered row references are rejected");
        helper.assertValueEqual(InformationRow.DIRECT_CODEC.parse(ops, JsonParser.parseString("{\"label\":\"X\",\"value\":0}")).getOrThrow().importance(), 0, "row definition importance defaults to zero");
        InformationDocument document = new InformationDocument(List.of(row));
        Tag tag = InformationDocument.CODEC.encodeStart(helper.getLevel().registryAccess().createSerializationContext(NbtOps.INSTANCE), document).getOrThrow();
        helper.assertValueEqual(InformationDocument.CODEC.parse(helper.getLevel().registryAccess().createSerializationContext(NbtOps.INSTANCE), tag).getOrThrow().rows().getFirst().groups().size(), 2, "NBT document roundtrip");
        helper.succeed();
    }

    static Holder<InformationGroup> group(GameTestHelper helper, String path) {
        return helper.getLevel().registryAccess().lookupOrThrow(SaturnResourceKeys.INFORMATION_GROUP)
                .get(ResourceKey.create(SaturnResourceKeys.INFORMATION_GROUP, id(path))).orElseThrow();
    }

    private static InformationEntry row(InformationDocument document, String name) {
        return document.rows().stream().filter(row -> row.id().equals(id(name))).findFirst().orElseThrow();
    }

    private static Identifier id(String path) {
        return Identifier.fromNamespaceAndPath(Saturn.MOD_ID, path);
    }
}
