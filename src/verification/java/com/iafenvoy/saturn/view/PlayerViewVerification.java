package com.iafenvoy.saturn.view;

import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.iafenvoy.saturn.api.playerview.*;
import com.iafenvoy.saturn.view.model.*;
import com.iafenvoy.saturn.view.runtime.*;
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
        snapshots();
        groups();
        suppliedData();
        contracts();
        System.out.println("Player view verification: " + checks + " checks passed");
    }

    private static void snapshots() {
        PlayerViewField<CompoundTag> mutable = PlayerViewFields.register(id("mutable"), 1, CompoundTag.CODEC);
        CompoundTag original = new CompoundTag();
        original.putInt("n", 7);
        PlayerViewSnapshot.Builder builder = PlayerViewSnapshot.builder(meta("one"), NbtOps.INSTANCE);
        builder.put(mutable, original);
        builder.put(PlayerViewFields.HEALTH, 0.0);
        original.putInt("n", 9);
        PlayerViewSnapshot view = builder.build();
        check(view.read(mutable).value().orElseThrow().getInt("n").orElseThrow() == 7, "input data detached");
        view.read(mutable).value().orElseThrow().putInt("n", 99);
        check(view.read(mutable).value().orElseThrow().getInt("n").orElseThrow() == 7, "read data detached");
        check(view.read(PlayerViewFields.HEALTH).isKnown() && view.read(PlayerViewFields.HEALTH).value().orElseThrow() == 0, "zero is known");
        check(view.read(PlayerViewFields.MAX_HEALTH).missingReason().orElseThrow() == MissingReason.NOT_CAPTURED, "missing is not zero");
        builder.put(PlayerViewFields.HEALTH, 20.0);
        check(view.read(PlayerViewFields.HEALTH).value().orElseThrow() == 0, "builder changes detached");
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
                .getOrThrow().read(PlayerViewFields.HEALTH).value().orElseThrow() == 0, "NBT round trip");
        json.getAsJsonObject("fields").getAsJsonObject("saturn:health").addProperty("schema", 2);
        check(PlayerViewSnapshot.CODEC.parse(JsonOps.INSTANCE, json).getOrThrow().read(PlayerViewFields.HEALTH)
                .missingReason().orElseThrow() == MissingReason.UNSUPPORTED, "future field schema isolated");
        json.getAsJsonObject("fields").getAsJsonObject("saturn:health").addProperty("schema", 1);
        json.getAsJsonObject("fields").getAsJsonObject("saturn:health").addProperty("value", "bad");
        PlayerViewSnapshot invalid = PlayerViewSnapshot.CODEC.parse(JsonOps.INSTANCE, json).getOrThrow();
        check(invalid.read(PlayerViewFields.HEALTH).missingReason().orElseThrow() == MissingReason.INVALID
                && invalid.read(mutable).isKnown(), "invalid field does not poison snapshot");
        check(decoded.filter(field -> field.equals(PlayerViewFields.HEALTH.id())).fieldIds().equals(Set.of(PlayerViewFields.HEALTH.id())), "wire filtering removes unknown fields too");
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
        Group cultivation = registry.registerGroup(id("cultivation"), Component.literal("Cultivation"), 10);
        Group basic = registry.registerGroup(id("basic"), Component.literal("Basic"), 0);
        PlayerInformationRowProvider row = (view, context) -> Optional.of(new InformationRowContent(Component.literal("X"), new InformationValue.Number(3), Set.of()));
        registry.register(id("realm"), cultivation, 0, row);
        registry.register(id("food"), basic, 10, row);
        registry.register(id("health"), basic, 0, row);
        registry.register(id("hidden"), basic, 20, (view, context) -> Optional.empty());
        PlayerViewSnapshot view = PlayerViewSnapshot.builder(meta("one"), NbtOps.INSTANCE).build();
        InformationDocument all = registry.collectAll(view, InformationContext.DEFAULT);
        check(all.rows().stream().map(InformationRow::id).toList().equals(List.of(id("health"), id("food"), id("realm"))), "all groups ordered");
        check(registry.collect(view, InformationContext.DEFAULT, basic).rows().equals(all.rows(basic)), "by group equals all filtered");
        check(registry.collect(view, InformationContext.DEFAULT, Set.of()).rows().isEmpty(), "empty selection means none");
        check(all.groups().equals(List.of(basic, cultivation)), "group metadata retained");
        Group otherLanguage = new Group(basic.id(), Component.literal("Different title"), 100);
        check(registry.collect(view, InformationContext.DEFAULT, otherLanguage).rows().size() == 2, "group identity ignores title and order");
        expectFailure(() -> registry.registerGroup(id("basic"), Component.empty(), 0), "duplicate group rejected");
        expectFailure(() -> registry.register(id("health"), basic, 0, row), "duplicate row rejected");
        expectFailure(() -> registry.register(id("unknown"), new Group(id("unknown"), Component.empty(), 0), 0, row), "unknown group rejected");
        registry.register(id("broken"), basic, 30, (v, c) -> { throw new IllegalStateException("Expected verification failure"); });
        check(registry.collectAll(view, InformationContext.DEFAULT).rows().size() == 3, "row failure isolated");
        InformationValue number = new InformationValue.Number(7);
        check(InformationValue.CODEC.parse(JsonOps.INSTANCE, InformationValue.CODEC.encodeStart(JsonOps.INSTANCE, number)
                .getOrThrow()).getOrThrow().equals(number), "numeric display codec");
        check(PlayerInformation.collectAll(view).rows().size() > PlayerInformation.collect(view, PlayerInformation.BASIC).rows().size(), "global facade supports both modes");
        Component title = Component.literal("Original");
        Group detached = new Group(id("title"), title, 0);
        ((net.minecraft.network.chat.MutableComponent) title).append("changed");
        check(detached.title().getString().equals("Original"), "group title detached");
        var sibling = Component.literal("child");
        Group nested = new Group(id("nested"), Component.literal("root").append(sibling), 0);
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

    private static void suppliedData() {
        CompoundTag data = new CompoundTag();
        data.putFloat("Health", 3);
        data.putInt("XpLevel", 5);
        data.putInt("XpTotal", 55);
        data.putFloat("XpP", 0.25F);
        SavedPlayerViewContext context = new SavedPlayerViewContext(meta("saved"), RegistryAccess.EMPTY, data, Optional.of("{}"), Optional.of("{}"));
        data.putFloat("Health", 99);
        PlayerViewSnapshot view = PlayerViews.parse(context);
        check(view.read(PlayerViewFields.HEALTH).value().orElseThrow() == 3, "supplied NBT detached");
        check(view.read(PlayerViewFields.HEALTH).origin() == ValueOrigin.SAVED, "saved provenance retained");
        check(view.read(PlayerViewFields.EXPERIENCE).value().orElseThrow().level() == 5, "experience parsed");
        check(view.read(PlayerViewFields.MAX_HEALTH).missingReason().orElseThrow() == MissingReason.NOT_CAPTURED, "no invented historical max health");
        check(view.read(PlayerViewFields.INVENTORY).missingReason().orElseThrow() == MissingReason.NOT_CAPTURED, "absent inventory is not empty");
        PlayerViewSnapshot.Builder recorded = PlayerViewSnapshot.builder(context.meta(), NbtOps.INSTANCE);
        recorded.put(PlayerViewFields.HEALTH, 10.0);
        PlayerViewSnapshot merged = PlayerViews.parse(context, Optional.of(recorded.build()));
        check(merged.read(PlayerViewFields.HEALTH).value().orElseThrow() == 10 && merged.read(PlayerViewFields.EXPERIENCE).isKnown(), "recorded fields take precedence with legacy fallback");
        expectFailure(() -> PlayerViews.parse(context, Optional.of(PlayerViewSnapshot.builder(meta("wrong"), NbtOps.INSTANCE).build())), "wrong record rejected");
        CompoundTag corrupt = new CompoundTag();
        corrupt.putString("foodLevel", "bad");
        corrupt.putFloat("Health", 6);
        corrupt.put("Inventory", new net.minecraft.nbt.ListTag());
        PlayerViewSnapshot partial = PlayerViews.parse(new SavedPlayerViewContext(meta("partial"), RegistryAccess.EMPTY, corrupt,
                Optional.of("{\"stats\":{}}"), Optional.empty()));
        check(partial.read(PlayerViewFields.FOOD).missingReason().orElseThrow() == MissingReason.INVALID, "bad saved field is invalid");
        check(partial.read(PlayerViewFields.HEALTH).value().orElseThrow() == 6 && partial.read(PlayerViewFields.STATISTICS).isKnown(), "bad saved field does not stop later fields");
        check(partial.read(PlayerViewFields.INVENTORY).value().orElseThrow().isEmpty(), "explicit empty inventory is known");
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
        PlayerViewData.Resource mana = resourceView.read(PlayerViewFields.RESOURCES).value().orElseThrow().getFirst();
        check(mana.value() == 12 && mana.maximum().isEmpty(), "saved resources need neither current definitions nor invented bounds");
    }

    private static void contracts() {
        PlayerViewField<Integer> custom = PlayerViewFields.register(id("owned"), 1, Codec.INT);
        expectFailure(() -> PlayerViewFields.register(id("owned"), 1, Codec.INT), "duplicate field rejected");
        PlayerViews.registerReader(new SavedPlayerViewReader() {
            public net.minecraft.resources.Identifier id() { return PlayerViewVerification.id("ownership"); }
            public Set<PlayerViewField<?>> fields() { return Set.of(custom); }
            public void read(SavedPlayerViewContext context, PlayerViewWriter writer) { writer.put(PlayerViewFields.HEALTH, 999.0); }
        });
        CompoundTag data = new CompoundTag();
        data.putFloat("Health", 4);
        PlayerViewSnapshot parsed = PlayerViews.parse(new SavedPlayerViewContext(meta("owned"), RegistryAccess.EMPTY, data, Optional.empty(), Optional.empty()));
        check(parsed.read(PlayerViewFields.HEALTH).value().orElseThrow() == 4, "reader cannot overwrite another module");
        check(parsed.read(custom).missingReason().orElseThrow() == MissingReason.INVALID, "failed reader only marks its own fields");
        PlayerViewResult result = PlayerViewResult.success(8, parsed);
        check(PlayerViewResult.CODEC.parse(JsonOps.INSTANCE, PlayerViewResult.CODEC.encodeStart(JsonOps.INSTANCE, result).getOrThrow())
                .getOrThrow().requestId() == 8, "host result codec");
        PlayerViewField<Integer> frozenField = PlayerViewFields.register(id("frozen_owner"), 1, Codec.INT);
        Set<PlayerViewField<?>> fields = new HashSet<>(Set.of(frozenField));
        PlayerViews.registerReader(new SavedPlayerViewReader() {
            public net.minecraft.resources.Identifier id() { return PlayerViewVerification.id("frozen_reader"); }
            public Set<PlayerViewField<?>> fields() { return fields; }
            public void read(SavedPlayerViewContext context, PlayerViewWriter writer) { writer.put(frozenField, 7, ValueOrigin.SAVED); }
        });
        fields.clear();
        PlayerViewSnapshot frozen = PlayerViews.parse(new SavedPlayerViewContext(meta("frozen"), RegistryAccess.EMPTY, new CompoundTag(), Optional.empty(), Optional.empty()));
        check(frozen.read(frozenField).value().orElseThrow() == 7, "registered ownership is frozen");
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
