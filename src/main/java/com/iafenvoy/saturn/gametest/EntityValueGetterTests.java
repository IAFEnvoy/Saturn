package com.iafenvoy.saturn.gametest;

import com.google.gson.JsonElement;
import com.google.gson.JsonParser;
import com.iafenvoy.saturn.Saturn;
import com.iafenvoy.saturn.attachment.AbilityAttachment;
import com.iafenvoy.saturn.attachment.ResourceHolderAttachment;
import com.iafenvoy.saturn.data.information.PlayerViewSnapshot;
import com.iafenvoy.saturn.data.information.PlayerViews;
import com.iafenvoy.saturn.data.information.expression.InformationEvaluation;
import com.iafenvoy.saturn.data.information.getter.EntityValueGetter;
import com.iafenvoy.saturn.data.information.getter.EntityValueGetters;
import com.iafenvoy.saturn.data.information.getter.builtin.AttributeGetter;
import com.iafenvoy.saturn.data.information.model.*;
import com.iafenvoy.saturn.registry.SaturnAttachments;
import com.iafenvoy.saturn.registry.SaturnRegistries;
import com.iafenvoy.saturn.registry.content.SaturnEntityValueGetters;
import com.iafenvoy.saturn.util.codec.ExtraEnumCodecs;
import com.mojang.authlib.GameProfile;
import com.mojang.serialization.Codec;
import com.mojang.serialization.JsonOps;
import com.mojang.serialization.MapCodec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import net.minecraft.core.Registry;
import net.minecraft.gametest.framework.GameTestHelper;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.nbt.ListTag;
import net.minecraft.nbt.NbtOps;
import net.minecraft.nbt.Tag;
import net.minecraft.resources.Identifier;
import net.minecraft.resources.RegistryOps;
import net.minecraft.server.level.ClientInformation;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.ai.attributes.AttributeInstance;
import net.minecraft.world.entity.ai.attributes.AttributeModifier;
import net.minecraft.world.entity.ai.attributes.Attributes;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.Items;
import net.neoforged.bus.api.IEventBus;
import net.neoforged.neoforge.registries.DeferredRegister;

import java.util.List;
import java.util.Objects;
import java.util.Optional;
import java.util.UUID;

/**
 * Query registration, parameter identity, live/saved capture and detached evaluation in the real game.
 */
final class EntityValueGetterTests {
    private static final TestGetter CUSTOM = new TestGetter(42, Mode.NORMAL);
    private static final TestGetter OTHER = new TestGetter(13, Mode.NORMAL);
    private static final TestGetter BROKEN = new TestGetter(0, Mode.FAIL);

    private EntityValueGetterTests() {
    }

    static void register(IEventBus bus) {
        DeferredRegister<MapCodec<? extends EntityValueGetter<?>>> getters = DeferredRegister.create(SaturnRegistries.ENTITY_VALUE_GETTER, Saturn.MOD_ID);
        getters.register("gametest/custom", () -> TestGetter.CODEC);
        getters.register(bus);
    }

    static void registry(GameTestHelper helper) {
        helper.assertValueEqual(SaturnEntityValueGetters.REGISTRY.getEntries().size(), 26, "22 unit getters and 4 parameterized getters registered");
        RegistryOps<JsonElement> ops = helper.getLevel().registryAccess().createSerializationContext(JsonOps.INSTANCE);
        AttributeGetter query = new AttributeGetter(Identifier.parse("minecraft:max_health"), AttributeGetter.Value.BASE);
        helper.assertValueEqual(EntityValueGetter.CODEC.parse(ops, EntityValueGetter.CODEC.encodeStart(ops, query).getOrThrow()).getOrThrow(), query,
                "getter parameters survive dispatch codec");
        helper.assertTrue(EntityValueGetter.CODEC.parse(ops, JsonParser.parseString("{\"type\":\"absent:getter\"}")).isError(), "unknown getter type is a codec error");
        helper.assertTrue(EntityValueGetter.CODEC.parse(ops, JsonParser.parseString("{\"type\":\"saturn:attribute\",\"attribute\":\"minecraft:max_health\",\"value\":\"wrong\"}")).isError(),
                "invalid enum value is a codec error");
        helper.assertTrue(EntityValueGetter.CODEC.parse(ops, JsonParser.parseString("{\"type\":\"saturn:attribute\"}")).isError(), "required getter argument enforced");
        helper.assertTrue(EntityValueGetters.defaults(ops).stream().noneMatch(getter -> getter instanceof AttributeGetter || getter instanceof TestGetter),
                "parameterized getters are captured when configured");
        boolean frozen = false;
        try {
            Registry.register(SaturnRegistries.ENTITY_VALUE_GETTER, Identifier.parse("saturn:gametest/late"), TestGetter.CODEC);
        } catch (IllegalStateException expected) {
            frozen = true;
        }
        helper.assertTrue(frozen, "getter types follow native registry freeze lifecycle");
        helper.succeed();
    }

    static void live(GameTestHelper helper) {
        ServerPlayer player = new ServerPlayer(helper.getLevel().getServer(), helper.getLevel(),
                new GameProfile(UUID.randomUUID(), "SaturnGetterTest"), ClientInformation.createDefault());
        player.setHealth(7);
        player.getInventory().setItem(0, new ItemStack(Items.DIAMOND, 3));
        Optional<ResourceHolderAttachment> resources = player.getExistingData(SaturnAttachments.RESOURCE_HOLDER);
        Optional<AbilityAttachment> abilities = player.getExistingData(SaturnAttachments.ABILITY_HOLDER);
        AttributeInstance attribute = Objects.requireNonNull(player.getAttribute(Attributes.MAX_HEALTH));
        attribute.setBaseValue(20);
        attribute.addPermanentModifier(new AttributeModifier(Identifier.parse("saturn:gametest/bonus"), 4, AttributeModifier.Operation.ADD_VALUE));
        PlayerViewMeta meta = meta(player.getUUID());
        PlayerViewSnapshot view = PlayerViews.parse(player, meta, List.of(CUSTOM, OTHER, BROKEN));
        helper.assertValueEqual(view.read(EntityValueGetters.HEALTH).value().orElseThrow(), 7.0, "live health getter");
        helper.assertValueEqual(view.read(EntityValueGetters.INVENTORY).value().orElseThrow().get("0").count(), 3, "live inventory getter");
        helper.assertTrue(view.read(EntityValueGetters.GAME_MODE).isKnown() && view.read(EntityValueGetters.PROFILE).isKnown(), "other getters survive one failure");
        helper.assertValueEqual(view.read(CUSTOM).value().orElseThrow(), 42, "requested parameter query captured");
        helper.assertValueEqual(view.read(OTHER).value().orElseThrow(), 13, "different parameters do not share snapshot entry");
        helper.assertValueEqual(view.read(BROKEN).missingReason().orElseThrow(), MissingReason.INVALID, "getter failure isolated");
        AttributeGetter base = new AttributeGetter(Identifier.parse("minecraft:max_health"), AttributeGetter.Value.BASE);
        AttributeGetter effective = new AttributeGetter(Identifier.parse("minecraft:max_health"), AttributeGetter.Value.EFFECTIVE);
        helper.assertValueEqual(view.read(base).value().orElseThrow(), 20.0, "attribute query projects base from saved complete attributes");
        helper.assertValueEqual(view.read(effective).value().orElseThrow(), 24.0, "attribute query projects effective value by id");
        helper.assertValueEqual(player.getExistingData(SaturnAttachments.RESOURCE_HOLDER), resources, "reading resources does not create attachment");
        helper.assertValueEqual(player.getExistingData(SaturnAttachments.ABILITY_HOLDER), abilities, "reading abilities does not create attachment");
        player.setHealth(15);
        attribute.setBaseValue(100);
        player.getInventory().getItem(0).setCount(1);
        helper.assertValueEqual(view.read(base).value().orElseThrow(), 20.0, "attribute query never borrows current entity");
        helper.assertValueEqual(view.read(EntityValueGetters.HEALTH).value().orElseThrow(), 7.0, "captured health detached");
        helper.assertValueEqual(view.read(EntityValueGetters.INVENTORY).value().orElseThrow().get("0").count(), 3, "mutable item detached");
        InformationEvaluation evaluation = new InformationEvaluation(view, InformationContext.DEFAULT);
        helper.assertValueEqual(evaluation.getter(CUSTOM, List.of()).number(), 42.0, "parameter query evaluation");
        helper.assertValueEqual(evaluation.getter(OTHER, List.of()).number(), 13.0, "cache includes getter parameters");
        helper.succeed();
    }

    static void saved(GameTestHelper helper) {
        CompoundTag data = new CompoundTag();
        data.putFloat("Health", 6);
        data.putString("foodLevel", "broken");
        data.putInt("custom", 9);
        data.put("Inventory", new ListTag());
        CompoundTag attribute = new CompoundTag();
        attribute.putString("id", "minecraft:max_health");
        attribute.putDouble("base", 30);
        ListTag attributes = new ListTag();
        attributes.add(attribute);
        data.put("attributes", attributes);
        SavedPlayerViewContext context = new SavedPlayerViewContext(meta(new UUID(0, 1)), helper.getLevel().registryAccess(), data, Optional.empty(), Optional.empty());
        PlayerViewSnapshot view = PlayerViews.parse(context, Optional.empty(), List.of(CUSTOM, BROKEN));
        helper.assertValueEqual(view.read(EntityValueGetters.HEALTH).value().orElseThrow(), 6.0, "builtin saved getter");
        helper.assertValueEqual(view.read(CUSTOM).value().orElseThrow(), 51, "parameterized getter supplies saved reader");
        helper.assertValueEqual(view.read(CUSTOM).origin(), ValueOrigin.SAVED, "saved origin preserved");
        helper.assertValueEqual(view.read(BROKEN).missingReason().orElseThrow(), MissingReason.INVALID, "saved callback failure isolated");
        helper.assertValueEqual(view.read(EntityValueGetters.FOOD).missingReason().orElseThrow(), MissingReason.INVALID, "bad saved payload isolated");
        helper.assertTrue(!view.getterIds().contains(EntityValueGetters.PROFILE.type()), "host may supplement absent saved values");
        AttributeGetter base = new AttributeGetter(Identifier.parse("minecraft:max_health"), AttributeGetter.Value.BASE);
        AttributeGetter effective = new AttributeGetter(Identifier.parse("minecraft:max_health"), AttributeGetter.Value.EFFECTIVE);
        helper.assertValueEqual(view.read(base).value().orElseThrow(), 30.0, "attribute id query works on supplied NBT");
        helper.assertValueEqual(view.read(effective).missingReason().orElseThrow(), MissingReason.NOT_CAPTURED, "historical effective value not invented");
        RegistryOps<Tag> ops = context.registries().createSerializationContext(NbtOps.INSTANCE);
        PlayerViewSnapshot.Builder recorded = PlayerViewSnapshot.builder(context.meta(), ops);
        recorded.missing(CUSTOM, MissingReason.REDACTED);
        PlayerViewSnapshot merged = PlayerViews.parse(context, Optional.of(recorded.build()), List.of(CUSTOM));
        helper.assertValueEqual(merged.read(CUSTOM).missingReason().orElseThrow(), MissingReason.REDACTED, "recorded missing query remains authoritative");
        helper.assertTrue(merged.read(EntityValueGetters.HEALTH).isKnown(), "absent recorded values receive saved fallback");
        helper.assertValueEqual(PlayerViewSnapshot.CODEC.parse(ops, PlayerViewSnapshot.CODEC.encodeStart(ops, view).getOrThrow())
                .getOrThrow().read(CUSTOM).value().orElseThrow(), 51, "parameterized snapshot NBT roundtrip");
        helper.succeed();
    }

    private static PlayerViewMeta meta(UUID account) {
        return new PlayerViewMeta(PlayerViewRef.current(account), UUID.randomUUID(), PlayerViewMeta.SourceKind.ARCHIVE, Optional.empty(), Optional.empty(), 1);
    }

    private record TestGetter(int amount, Mode mode) implements EntityValueGetter<Integer> {
        private static final MapCodec<TestGetter> CODEC = RecordCodecBuilder.mapCodec(i -> i.group(
                Codec.INT.fieldOf("amount").forGetter(TestGetter::amount),
                Mode.CODEC.optionalFieldOf("mode", Mode.NORMAL).forGetter(TestGetter::mode)
        ).apply(i, TestGetter::new));

        @Override
        public MapCodec<TestGetter> codec() {
            return CODEC;
        }

        @Override
        public Codec<Integer> valueCodec() {
            return Codec.INT;
        }

        @Override
        public ViewValue<Integer> capture(Entity entity) {
            if (this.mode == Mode.FAIL) throw new IllegalStateException("Expected getter failure");
            return ViewValue.known(this.amount, ValueOrigin.CAPTURED);
        }

        @Override
        public ViewValue<Integer> readSaved(SavedPlayerViewContext context) {
            if (this.mode == Mode.FAIL) throw new IllegalStateException("Expected saved getter failure");
            return context.player().getInt("custom").map(value -> ViewValue.known(value + this.amount, ValueOrigin.SAVED))
                    .orElseGet(() -> ViewValue.missing(MissingReason.NOT_CAPTURED));
        }
    }

    private enum Mode {
        NORMAL, FAIL;
        private static final Codec<Mode> CODEC = ExtraEnumCodecs.enumCodec(Mode.class);
    }
}
