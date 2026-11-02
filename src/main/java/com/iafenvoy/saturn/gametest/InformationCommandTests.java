package com.iafenvoy.saturn.gametest;

import com.iafenvoy.saturn.Saturn;
import com.iafenvoy.saturn.data.information.InformationText;
import com.iafenvoy.saturn.data.information.PlayerInformation;
import com.iafenvoy.saturn.data.information.PlayerViews;
import com.iafenvoy.saturn.data.information.model.InfomationGroup;
import com.iafenvoy.saturn.data.information.model.InformationContext;
import com.iafenvoy.saturn.data.information.model.InformationDocument;
import com.mojang.authlib.GameProfile;
import com.mojang.brigadier.CommandDispatcher;
import com.mojang.brigadier.exceptions.CommandSyntaxException;
import com.mojang.brigadier.suggestion.Suggestions;
import net.minecraft.commands.CommandSource;
import net.minecraft.commands.CommandSourceStack;
import net.minecraft.core.BlockPos;
import net.minecraft.gametest.framework.GameTestHelper;
import net.minecraft.network.chat.Component;
import net.minecraft.network.chat.contents.TranslatableContents;
import net.minecraft.resources.Identifier;
import net.minecraft.server.MinecraftServer;
import net.minecraft.server.level.ClientInformation;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.server.permissions.PermissionSet;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.animal.cow.Cow;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.Items;
import net.minecraft.world.phys.Vec2;
import org.jspecify.annotations.NonNull;

import java.util.ArrayList;
import java.util.List;
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
        helper.assertValueEqual(dispatcher.execute("saturn information @s", admin), all.rows().size(), "command returns total logical row count");
        helper.assertValueEqual(key(messages.lines.getFirst()), "command.saturn.information.all", "all output has translated heading");
        helper.assertValueEqual(messages.lines.subList(1, messages.lines.size()).stream().map(Component::getString).toList(), lines(all),
                "all output matches the information document including tables and inventory");
        helper.assertTrue(messages.lines.stream().anyMatch(line -> line.getString().contains("7 / 20")), "command prints captured health value");
        messages.lines.clear();

        InformationDocument basic = PlayerInformation.collect(PlayerViews.parse(target), context, PlayerInformation.BASIC);
        helper.assertValueEqual(dispatcher.execute("saturn information @s saturn:basic", admin), basic.rows().size(), "group output returns selected row count");
        helper.assertTrue(basic.rows().size() < all.rows().size(), "group filters actual rows");
        helper.assertValueEqual(key(messages.lines.getFirst()), "command.saturn.information.group", "group output has translated heading");
        helper.assertValueEqual(messages.lines.subList(1, messages.lines.size()).stream().map(Component::getString).toList(), lines(basic),
                "group output contains exactly the selected rows in order");
        messages.lines.forEach(line -> Saturn.LOGGER.info("Information command sample: {}", line.getString()));
        messages.lines.clear();

        Suggestions suggestions = dispatcher.getCompletionSuggestions(dispatcher.parse("saturn information @s saturn:", admin)).join();
        helper.assertTrue(suggestions.getList().stream().anyMatch(suggestion -> suggestion.getText().equals("saturn:basic")), "group ids complete from registry");
        try {
            dispatcher.execute("saturn information @s absent:group", admin);
            throw new IllegalStateException("Unknown group was accepted");
        } catch (CommandSyntaxException expected) {
            helper.assertValueEqual(key((Component) expected.getRawMessage()), "command.saturn.information.unknown_group", "unknown group gives translated error");
        }
        helper.assertTrue(messages.lines.isEmpty(), "unknown group prints no partial result");

        InfomationGroup empty = PlayerInformation.registerGroup(Identifier.parse("saturn:gametest/command_empty"), Component.literal("Empty"), 999);
        helper.assertValueEqual(dispatcher.execute("saturn information @s " + empty.id(), admin), 0, "empty Java group supported");
        helper.assertValueEqual(key(messages.lines.getLast()), "info.saturn.empty", "empty group prints explicit empty message");
        messages.lines.clear();

        try {
            dispatcher.execute("saturn information @s", source(helper, target, messages, PermissionSet.NO_PERMISSIONS));
            throw new IllegalStateException("Information diagnostics accepted an unprivileged source");
        } catch (CommandSyntaxException expected) {
            helper.assertTrue(messages.lines.isEmpty(), "permission rejection exposes no information");
        }

        Cow cow = helper.spawn(EntityType.COW, new BlockPos(1, 2, 1));
        try {
            cow.setHealth(5);
            helper.assertTrue(dispatcher.execute("saturn information " + cow.getUUID() + " saturn:status", admin) > 0, "UUID target supports non-player entities");
            helper.assertTrue(!messages.lines.isEmpty(), "entity target produces output");
        } finally {
            cow.discard();
        }
    }

    private static List<String> lines(InformationDocument document) {
        return document.rows().stream().flatMap(row -> InformationText.lines(row.content()).stream()).map(Component::getString).toList();
    }

    private static String key(Component component) {
        return component.getContents() instanceof TranslatableContents translation ? translation.getKey() : "";
    }

    private static CommandSourceStack source(GameTestHelper helper, ServerPlayer target, Messages messages, PermissionSet permissions) {
        return new CommandSourceStack(messages, target.position(), Vec2.ZERO, helper.getLevel(), permissions,
                target.getGameProfile().name(), target.getName(), helper.getLevel().getServer(), target);
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
