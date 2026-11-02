package com.iafenvoy.saturn.screen.information;

import com.iafenvoy.saturn.api.playerview.PlayerView;
import com.iafenvoy.saturn.view.model.*;
import com.iafenvoy.saturn.view.PlayerInformation;
import net.minecraft.client.gui.screens.Screen;
import org.jetbrains.annotations.Nullable;

import java.util.Set;

public final class PlayerInformationScreens {
    private PlayerInformationScreens() {
    }

    public static PlayerInformationScreen create(@Nullable Screen parent, PlayerView view) {
        return new PlayerInformationScreen(parent, PlayerInformation.collectAll(view));
    }

    public static PlayerInformationScreen create(@Nullable Screen parent, PlayerView view, InfomationGroup group) {
        return new PlayerInformationScreen(parent, PlayerInformation.collect(view, group));
    }

    public static PlayerInformationScreen create(@Nullable Screen parent, PlayerView view, InformationContext context, Set<InfomationGroup> groups) {
        return new PlayerInformationScreen(parent, PlayerInformation.collect(view, context, groups));
    }
}
