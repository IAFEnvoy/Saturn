package com.iafenvoy.saturn;

import com.iafenvoy.saturn.registry.SaturnAttachments;
import com.iafenvoy.saturn.registry.SaturnContent;
import com.iafenvoy.saturn.registry.SaturnLootRegistries;
import com.iafenvoy.saturn.registry.cache.RegistryCaches;
import com.mojang.logging.LogUtils;
import net.neoforged.bus.api.IEventBus;
import net.neoforged.bus.api.SubscribeEvent;
import net.neoforged.fml.common.EventBusSubscriber;
import net.neoforged.fml.common.Mod;
import net.neoforged.fml.event.lifecycle.FMLCommonSetupEvent;
import net.neoforged.fml.loading.FMLEnvironment;
import org.slf4j.Logger;

/**
 * The library mod: definitions a data pack writes (actions, conditions, costs, formulas, storage, timelines, triggers,
 * abilities, resources and their bars), the runtime that reads them, and the client framework that draws the bars.
 *
 * <p>A dependent mod depends on this jar and keeps its own content, its own attachments and its own services. What is
 * shared here is the vocabulary and the plumbing, never a gameplay rule.
 */
@Mod(Saturn.MOD_ID)
@EventBusSubscriber
public final class Saturn {
    public static final String MOD_ID = "saturn";
    public static final Logger LOGGER = LogUtils.getLogger();

    public Saturn(IEventBus bus) {
        SaturnAttachments.REGISTRY.register(bus);
        SaturnLootRegistries.register(bus);
        SaturnContent.register(bus);
    }

    /**
     * Constructs every cache into {@code RegistryCache}'s rebuild list. That event only rebuilds what is already
     * registered there, and nothing else touches that class before a world loads, so a run without this call leaves
     * all of them empty for the whole session.
     */
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
        return !FMLEnvironment.isProduction() || Boolean.getBoolean("saturn.debug");
    }
}
