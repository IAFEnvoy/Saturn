package com.iafenvoy.saturn.screen.opener;

import com.iafenvoy.saturn.Saturn;
import com.iafenvoy.saturn.registry.SaturnRegistries;
import com.iafenvoy.saturn.screen.information.InformationOpenScreenData;
import com.iafenvoy.saturn.screen.information.PlayerInformationScreen;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.screens.Screen;
import net.minecraft.network.RegistryFriendlyByteBuf;
import net.minecraft.network.codec.StreamCodec;
import org.jetbrains.annotations.ApiStatus;

import java.util.LinkedHashMap;
import java.util.Map;
import java.util.function.Function;

public final class ClientScreenDispatcher {
    private static final Map<StreamCodec<RegistryFriendlyByteBuf, ? extends OpenScreenData>, Function<OpenScreenData, Screen>> SCREEN_CONSTRUCTORS = new LinkedHashMap<>();

    @SuppressWarnings({"unchecked", "rawtypes"})
    public static <T extends OpenScreenData> void register(StreamCodec<RegistryFriendlyByteBuf, T> type, Function<T, Screen> screenConstructor) {
        SCREEN_CONSTRUCTORS.put(type, (Function) screenConstructor);
    }

    @ApiStatus.Internal
    public static void onPacket(OpenScreenData data) {
        Function<OpenScreenData, Screen> constructor = SCREEN_CONSTRUCTORS.get(data.streamCodec());
        if (constructor == null)
            Saturn.LOGGER.error("No screen constructor registered for {}", SaturnRegistries.OPEN_SCREEN_DATA.getKey(data.streamCodec()));
        else {
            Screen screen = constructor.apply(data);
            if (screen == null) Saturn.LOGGER.error("Screen constructor returned null for {}", data.streamCodec());
            else Minecraft.getInstance().setScreen(screen);
        }
    }

    static {
        register(InformationOpenScreenData.STREAM_CODEC, data -> new PlayerInformationScreen(null, data.document(), data.selectedGroups()));
    }
}
