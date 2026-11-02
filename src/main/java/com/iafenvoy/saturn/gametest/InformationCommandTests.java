package com.iafenvoy.saturn.gametest;

import com.iafenvoy.saturn.Saturn;
import com.iafenvoy.saturn.data.information.*;
import com.iafenvoy.saturn.data.information.model.InformationContext;
import com.iafenvoy.saturn.data.information.model.InformationDocument;
import com.iafenvoy.saturn.network.payload.InformationGuiS2CPayload;
import com.iafenvoy.saturn.registry.SaturnResourceKeys;
import com.mojang.authlib.GameProfile;
import com.mojang.brigadier.CommandDispatcher;
import com.mojang.brigadier.exceptions.CommandSyntaxException;
import com.mojang.brigadier.suggestion.Suggestions;
import com.mojang.serialization.Lifecycle;
import io.netty.buffer.Unpooled;
import net.minecraft.commands.CommandSource;
import net.minecraft.commands.CommandSourceStack;
import net.minecraft.core.*;
import net.minecraft.gametest.framework.GameTestHelper;
import net.minecraft.network.Connection;
import net.minecraft.network.RegistryFriendlyByteBuf;
import net.minecraft.network.chat.Component;
import net.minecraft.network.chat.contents.TranslatableContents;
import net.minecraft.network.protocol.Packet;
import net.minecraft.network.protocol.PacketFlow;
import net.minecraft.network.protocol.common.ClientboundCustomPayloadPacket;
import net.minecraft.resources.Identifier;
import net.minecraft.server.MinecraftServer;
import net.minecraft.server.level.ClientInformation;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.server.network.CommonListenerCookie;
import net.minecraft.server.network.ServerGamePacketListenerImpl;
import net.minecraft.server.permissions.PermissionSet;
import net.minecraft.server.permissions.Permissions;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.animal.cow.Cow;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.Items;
import net.minecraft.world.phys.Vec2;
import org.jspecify.annotations.NonNull;

import java.util.ArrayList;
import java.util.List;
import java.util.Set;
import java.util.UUID;

final class InformationCommandTests {
    private InformationCommandTests() {
    }

    static void run(GameTestHelper helper) {
        try {
            check(helper);
            helper.succeed();
        } catch (CommandSyntaxException failure) {
            throw new IllegalStateException("Information command failed", failure);
        }
    }

    private static void check(GameTestHelper helper) throws CommandSyntaxException {
        MinecraftServer server = helper.getLevel().getServer();
        CommandDispatcher<CommandSourceStack> dispatcher = server.getCommands().getDispatcher();
        ServerPlayer target = new ServerPlayer(server, helper.getLevel(), new GameProfile(UUID.randomUUID(), "InformationTest"), ClientInformation.createDefault());
        target.setHealth(7);
        target.getInventory().setItem(0, new ItemStack(Items.DIAMOND, 3));
        Messages messages = new Messages();
        CommandSourceStack admin = source(helper, target, messages, PermissionSet.ALL_PERMISSIONS);
        InformationContext context = new InformationContext("en_us", helper.getLevel().registryAccess());

        InformationDocument all = PlayerInformation.collectAll(PlayerViews.parse(target), context);
        helper.assertValueEqual(dispatcher.execute("information query @s", admin), all.rows().size(), "command returns total logical row count");
        helper.assertValueEqual(key(messages.lines.getFirst()), "command.saturn.information.all", "all output has translated heading");
        helper.assertValueEqual(messages.lines.subList(1, messages.lines.size()).stream().map(Component::getString).toList(), lines(all),
                "all output matches the information document including tables and inventory");
        helper.assertTrue(messages.lines.stream().anyMatch(line -> line.getString().contains("7 / 20")), "command prints captured health value");
        messages.lines.clear();

        Holder<InformationGroup> basicGroup = InformationTests.group(helper, "basic");
        InformationDocument basic = PlayerInformation.collect(PlayerViews.parse(target), context, basicGroup);
        helper.assertValueEqual(dispatcher.execute("information query @s saturn:basic", admin), basic.rows().size(), "group output returns selected row count");
        helper.assertTrue(basic.rows().size() < all.rows().size(), "group filters actual rows");
        helper.assertValueEqual(key(messages.lines.getFirst()), "command.saturn.information.group", "group output has translated heading");
        helper.assertValueEqual(messages.lines.subList(1, messages.lines.size()).stream().map(Component::getString).toList(), lines(basic),
                "group output contains exactly the selected rows in order");
        messages.lines.forEach(line -> Saturn.LOGGER.info("Information command sample: {}", line.getString()));
        messages.lines.clear();

        for (String mode : List.of("query", "gui")) {
            Suggestions suggestions = dispatcher.getCompletionSuggestions(dispatcher.parse("information " + mode + " @s saturn:", admin)).join();
            helper.assertTrue(suggestions.getList().stream().anyMatch(suggestion -> suggestion.getText().equals("saturn:basic")), "group ids complete for " + mode);
        }
        try {
            dispatcher.execute("information query @s absent:group", admin);
            throw new IllegalStateException("Unknown group was accepted");
        } catch (CommandSyntaxException expected) {
            helper.assertValueEqual(key((Component) expected.getRawMessage()), "command.saturn.information.unknown_group", "unknown group gives translated error");
        }
        helper.assertTrue(messages.lines.isEmpty(), "unknown group prints no partial result");

        Identifier emptyId = Identifier.parse("saturn:basic");
        CommandSourceStack emptySource = emptyRowsSource(helper, target, messages);
        helper.assertValueEqual(dispatcher.execute("information query @s " + emptyId, emptySource), 0, "group without available rows supported");
        helper.assertValueEqual(key(messages.lines.getLast()), "info.saturn.empty", "empty group prints explicit empty message");
        messages.lines.clear();

        PermissionSet gamemaster = permission -> permission.equals(Permissions.COMMANDS_GAMEMASTER)
                || permission.equals(Permissions.COMMANDS_ENTITY_SELECTORS);
        for (PermissionSet permissions : List.of(PermissionSet.NO_PERMISSIONS, gamemaster)) {
            for (String root : List.of("information", "saturn information")) {
                for (String mode : List.of("query", "gui")) {
                    try {
                        dispatcher.execute(root + " " + mode + " @s", source(helper, target, messages, permissions));
                        throw new IllegalStateException("Information command accepted a source without admin permission");
                    } catch (CommandSyntaxException expected) {
                        helper.assertTrue(messages.lines.isEmpty(), "permission rejection exposes no information");
                    }
                }
            }
        }

        CapturingListener listener = new CapturingListener(server, target);
        helper.assertValueEqual(dispatcher.execute("saturn information query @s saturn:basic", admin), basic.rows().size(), "namespaced query alias works");
        messages.lines.clear();
        helper.assertValueEqual(dispatcher.execute("information gui @s", admin), all.rows().size(), "GUI returns logical row count");
        InformationGuiS2CPayload allPayload = receive(helper, listener);
        helper.assertTrue(allPayload.selectedGroups().isEmpty(), "empty selection represents all groups");
        helper.assertValueEqual(allPayload.document(), all, "GUI transports the same evaluated document as chat");
        helper.assertValueEqual(dispatcher.execute("saturn information gui @s saturn:basic", admin), basic.rows().size(), "namespaced GUI alias works");
        InformationGuiS2CPayload basicPayload = receive(helper, listener);
        helper.assertValueEqual(basicPayload.selectedGroups(), Set.of(basicGroup), "GUI retains selected group for multi-group rows");
        helper.assertValueEqual(basicPayload.document(), basic, "GUI transports only selected rows with styles and tooltips");
        helper.assertValueEqual(dispatcher.execute("information gui @s " + emptyId, emptySource), 0, "empty groups can open a GUI");
        helper.assertTrue(receive(helper, listener).document().rows().isEmpty(), "empty GUI transports an empty document");
        helper.assertTrue(messages.lines.isEmpty(), "GUI sends no chat output");
        try {
            dispatcher.execute("information gui @s absent:group", admin);
            throw new IllegalStateException("GUI accepted an unknown group");
        } catch (CommandSyntaxException expected) {
            helper.assertValueEqual(key((Component) expected.getRawMessage()), "command.saturn.information.unknown_group", "GUI rejects unknown groups");
            helper.assertTrue(listener.packets.isEmpty(), "unknown group sends no GUI document");
        }
        Cow cow = helper.spawn(EntityType.COW, new BlockPos(1, 2, 1));
        try {
            cow.setHealth(5);
            CommandSourceStack console = new CommandSourceStack(messages, target.position(), Vec2.ZERO, helper.getLevel(),
                    PermissionSet.ALL_PERMISSIONS, "Console", Component.literal("Console"), server, null);
            try {
                dispatcher.execute("information gui " + cow.getUUID(), console);
                throw new IllegalStateException("GUI accepted a non-player command source");
            } catch (CommandSyntaxException expected) {
                helper.assertValueEqual(expected.getType(), CommandSourceStack.ERROR_NOT_PLAYER, "GUI requires a player executor");
                helper.assertTrue(listener.packets.isEmpty(), "non-player source receives no GUI document");
            }
            helper.assertTrue(dispatcher.execute("information query " + cow.getUUID() + " saturn:status", console) > 0, "console can query entity information in text");
            messages.lines.clear();
            helper.assertTrue(dispatcher.execute("information query " + cow.getUUID() + " saturn:status", admin) > 0, "UUID target supports non-player entities");
            helper.assertTrue(!messages.lines.isEmpty(), "entity target produces output");
            InformationDocument cowDocument = PlayerInformation.collect(PlayerViews.parse(cow), context, InformationTests.group(helper, "status"));
            helper.assertValueEqual(dispatcher.execute("information gui " + cow.getUUID() + " saturn:status", admin), cowDocument.rows().size(), "GUI supports non-player targets");
            helper.assertValueEqual(receive(helper, listener).document(), cowDocument, "GUI goes to executor with the target entity's information");
        } finally {
            cow.discard();
        }
    }

    private static InformationGuiS2CPayload receive(GameTestHelper helper, CapturingListener listener) {
        helper.assertValueEqual(listener.packets.size(), 1, "only one GUI packet is sent to the executor");
        Packet<?> packet = listener.packets.removeFirst();
        helper.assertTrue(packet instanceof ClientboundCustomPayloadPacket, "GUI uses a clientbound payload");
        InformationGuiS2CPayload payload = (InformationGuiS2CPayload) ((ClientboundCustomPayloadPacket) packet).payload();
        RegistryFriendlyByteBuf buffer = new RegistryFriendlyByteBuf(Unpooled.buffer(), helper.getLevel().registryAccess());
        try {
            InformationGuiS2CPayload.STREAM_CODEC.encode(buffer, payload);
            InformationGuiS2CPayload decoded = InformationGuiS2CPayload.STREAM_CODEC.decode(buffer);
            helper.assertValueEqual(buffer.readableBytes(), 0, "GUI codec consumes entire document");
            helper.assertValueEqual(decoded, payload, "GUI codec preserves the document and selection");
            return decoded;
        } finally {
            buffer.release();
        }
    }

    private static List<String> lines(InformationDocument document) {
        return document.rows().stream().flatMap(row -> InformationText.lines(row).stream()).map(Component::getString).toList();
    }

    private static String key(Component component) {
        return component.getContents() instanceof TranslatableContents translation ? translation.getKey() : "";
    }

    private static CommandSourceStack source(GameTestHelper helper, ServerPlayer target, Messages messages, PermissionSet permissions) {
        return new CommandSourceStack(messages, target.position(), Vec2.ZERO, helper.getLevel(), permissions,
                target.getGameProfile().name(), target.getName(), helper.getLevel().getServer(), target);
    }

    private static CommandSourceStack emptyRowsSource(GameTestHelper helper, ServerPlayer target, Messages messages) {
        MappedRegistry<InformationRow> rows = new MappedRegistry<>(SaturnResourceKeys.INFORMATION_ROW, Lifecycle.stable());
        rows.freeze();
        List<Registry<?>> registries = new ArrayList<>();
        helper.getLevel().getServer().registryAccess().registries()
                .filter(entry -> !entry.key().equals(SaturnResourceKeys.INFORMATION_ROW)).forEach(entry -> registries.add(entry.value()));
        registries.add(rows);
        RegistryAccess access = new RegistryAccess.ImmutableRegistryAccess(registries);
        return new CommandSourceStack(messages, target.position(), Vec2.ZERO, helper.getLevel(), PermissionSet.ALL_PERMISSIONS,
                target.getGameProfile().name(), target.getName(), helper.getLevel().getServer(), target) {
            @Override
            public @NonNull RegistryAccess registryAccess() {
                return access;
            }
        };
    }

    private static final class CapturingListener extends ServerGamePacketListenerImpl {
        private final List<Packet<?>> packets = new ArrayList<>();

        private CapturingListener(MinecraftServer server, ServerPlayer player) {
            super(server, new Connection(PacketFlow.SERVERBOUND), player, CommonListenerCookie.createInitial(player.getGameProfile(), false));
        }

        @Override
        public void send(@NonNull Packet<?> packet) {
            this.packets.add(packet);
        }
    }

    private static final class Messages implements CommandSource {
        private final List<Component> lines = new ArrayList<>();

        @Override
        public void sendSystemMessage(@NonNull Component message) {
            this.lines.add(message);
        }

        @Override
        public boolean acceptsSuccess() {
            return true;
        }

        @Override
        public boolean acceptsFailure() {
            return true;
        }

        @Override
        public boolean shouldInformAdmins() {
            return false;
        }
    }
}
