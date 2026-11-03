package com.iafenvoy.saturn.screen.information;

import com.iafenvoy.saturn.api.playerview.PlayerView;
import com.iafenvoy.saturn.data.information.InformationGroup;
import com.iafenvoy.saturn.data.information.PlayerInformation;
import com.iafenvoy.saturn.data.information.model.InformationContext;
import net.minecraft.client.gui.screens.Screen;
import net.minecraft.core.Holder;
import org.jetbrains.annotations.Nullable;

import java.util.Set;

public final class PlayerInformationScreens {
    private PlayerInformationScreens() {
    }

    public static PlayerInformationScreen create(@Nullable Screen parent, PlayerView view) {
        return new PlayerInformationScreen(parent, PlayerInformation.collectAll(view));
    }

    public static PlayerInformationScreen create(@Nullable Screen parent, PlayerView view, Holder<InformationGroup> group) {
        return new PlayerInformationScreen(parent, PlayerInformation.collect(view, group), Set.of(group));
    }

    public static PlayerInformationScreen create(@Nullable Screen parent, PlayerView view, InformationContext context, Set<Holder<InformationGroup>> groups) {
        return new PlayerInformationScreen(parent, groups.isEmpty() ? PlayerInformation.collectAll(view, context) : PlayerInformation.collect(view, context, groups), groups);
    }
}
