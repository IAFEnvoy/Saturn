package com.iafenvoy.saturn;

import com.iafenvoy.saturn.config.SaturnConfig;
import com.iafenvoy.saturn.gametest.InformationTests;
import com.iafenvoy.saturn.registry.SaturnAttachments;
import com.iafenvoy.saturn.registry.SaturnContent;
import com.iafenvoy.saturn.registry.SaturnLootRegistries;
import com.iafenvoy.saturn.registry.SaturnOpenScreenData;
import com.iafenvoy.saturn.registry.cache.RegistryCaches;
import com.mojang.logging.LogUtils;
import net.neoforged.bus.api.IEventBus;
import net.neoforged.bus.api.SubscribeEvent;
import net.neoforged.fml.ModContainer;
import net.neoforged.fml.common.EventBusSubscriber;
import net.neoforged.fml.common.Mod;
import net.neoforged.fml.config.ModConfig;
import net.neoforged.fml.event.lifecycle.FMLCommonSetupEvent;
import net.neoforged.fml.loading.FMLEnvironment;
import org.slf4j.Logger;

@Mod(Saturn.MOD_ID)
@EventBusSubscriber
public final class Saturn {
    public static final String MOD_ID = "saturn";
    public static final Logger LOGGER = LogUtils.getLogger();

    public Saturn(IEventBus bus, ModContainer container) {
        container.registerConfig(ModConfig.Type.COMMON, SaturnConfig.COMMON_SPEC);
        container.registerConfig(ModConfig.Type.CLIENT, SaturnConfig.CLIENT_SPEC);

        SaturnAttachments.REGISTRY.register(bus);
        SaturnContent.register(bus);
        SaturnLootRegistries.CONDITIONS.register(bus);
        SaturnLootRegistries.FUNCTIONS.register(bus);
        SaturnOpenScreenData.REGISTRY.register(bus);

        InformationTests.register(bus);
    }

    @SubscribeEvent
    public static void onInit(FMLCommonSetupEvent event) {
        RegistryCaches.init();
    }

    /**
     * Whether this process may print the diagnostics only a developer reads, which is what
     * {@link com.iafenvoy.saturn.profiling.LoggerDispatcher} asks. Always on in a development build; a release
     * build can open it with {@code -Dsaturn.debug=true}.
     */
    public static boolean isDevMode() {
        return !FMLEnvironment.isProduction() || Boolean.getBoolean(MOD_ID + ".debug");
    }
}
