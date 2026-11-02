package com.iafenvoy.saturn.view;

import com.iafenvoy.saturn.Saturn;
import com.iafenvoy.saturn.api.playerview.PlayerInformationRowProvider;
import com.iafenvoy.saturn.api.playerview.PlayerView;
import com.iafenvoy.saturn.view.model.InfomationGroup;
import com.iafenvoy.saturn.view.model.InformationContext;
import com.iafenvoy.saturn.view.model.InformationDocument;
import net.minecraft.network.chat.Component;
import net.minecraft.resources.Identifier;

import java.util.List;
import java.util.Set;

public final class PlayerInformation {
    private static final PlayerInformationRegistry REGISTRY = new PlayerInformationRegistry();
    public static final InfomationGroup BASIC = registerGroup(Identifier.fromNamespaceAndPath(Saturn.MOD_ID, "basic"), Component.translatable("info.saturn.group.basic"), 0);
    public static final InfomationGroup STATUS = registerGroup(Identifier.fromNamespaceAndPath(Saturn.MOD_ID, "status"), Component.translatable("info.saturn.group.status"), 10);
    public static final InfomationGroup EQUIPMENT = registerGroup(Identifier.fromNamespaceAndPath(Saturn.MOD_ID, "equipment"), Component.translatable("info.saturn.group.equipment"), 20);

    static {
        BuiltinPlayerInformation.register();
    }

    private PlayerInformation() {
    }

    public static InfomationGroup registerGroup(Identifier id, Component title, int order) {
        return REGISTRY.registerGroup(id, title, order);
    }

    public static void register(Identifier id, InfomationGroup group, int order, PlayerInformationRowProvider provider) {
        REGISTRY.register(id, group, order, provider);
    }

    public static List<InfomationGroup> groups() {
        return REGISTRY.groups();
    }

    public static InformationDocument collectAll(PlayerView view) {
        return collectAll(view, InformationContext.DEFAULT);
    }

    public static InformationDocument collectAll(PlayerView view, InformationContext context) {
        return REGISTRY.collectAll(view, context);
    }

    public static InformationDocument collect(PlayerView view, InfomationGroup group) {
        return collect(view, InformationContext.DEFAULT, group);
    }

    public static InformationDocument collect(PlayerView view, InformationContext context, InfomationGroup group) {
        return REGISTRY.collect(view, context, group);
    }

    public static InformationDocument collect(PlayerView view, InformationContext context, Set<InfomationGroup> groups) {
        return REGISTRY.collect(view, context, groups);
    }
}
