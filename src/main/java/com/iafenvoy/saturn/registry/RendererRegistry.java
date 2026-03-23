package com.iafenvoy.saturn.registry;

import com.iafenvoy.saturn.render.ArmorRenderer;
import com.iafenvoy.saturn.render.DynamicItemRenderer;
import net.minecraft.client.gui.screens.MenuScreens;
import net.minecraft.client.gui.screens.Screen;
import net.minecraft.client.gui.screens.inventory.MenuAccess;
import net.minecraft.client.particle.ParticleProvider;
import net.minecraft.client.renderer.blockentity.BlockEntityRendererProvider;
import net.minecraft.client.renderer.entity.EntityRendererProvider;
import net.minecraft.core.particles.ParticleOptions;
import net.minecraft.core.particles.ParticleType;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.inventory.AbstractContainerMenu;
import net.minecraft.world.inventory.MenuType;
import net.minecraft.world.level.ItemLike;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.entity.BlockEntityType;
//? fabric {
/*import net.fabricmc.fabric.api.client.particle.v1.ParticleFactoryRegistry;
import net.fabricmc.fabric.api.client.rendering.v1.EntityRendererRegistry;
import net.fabricmc.fabric.impl.client.rendering.BlockEntityRendererRegistryImpl;
*///?} else neoforge {
import com.iafenvoy.saturn.Platform;
import com.iafenvoy.saturn.Saturn;
import com.iafenvoy.saturn.bus.SaturnBuses;
import net.neoforged.bus.api.Event;
import net.neoforged.neoforge.client.event.EntityRenderersEvent;
import net.neoforged.neoforge.client.event.RegisterMenuScreensEvent;
import net.neoforged.neoforge.client.event.RegisterParticleProvidersEvent;
import org.apache.commons.lang3.function.TriConsumer;

import java.util.HashMap;
import java.util.Map;
import java.util.function.BiConsumer;
//?}

import java.util.Arrays;
import java.util.function.Supplier;

//? fabric {
//@SuppressWarnings("unused")
//?} else {
@SuppressWarnings({"rawtypes", "unused"})
//?}
public final class RendererRegistry {
    // render type, model predicate

    //? !fabric {
    private static final Registrar<EntityType, EntityRendererProvider> ENTITY_RENDERERS = new Registrar<>(EntityRenderersEvent.RegisterRenderers.class, EntityRenderersEvent.RegisterRenderers::registerEntityRenderer);
    private static final Registrar<BlockEntityType, BlockEntityRendererProvider> BLOCK_ENTITY_RENDERERS = new Registrar<>(EntityRenderersEvent.RegisterRenderers.class, EntityRenderersEvent.RegisterRenderers::registerBlockEntityRenderer);
    private static final Registrar<ParticleType, ParticleProvider> SPECIAL_PARTICLE_PROVIDERS = new Registrar<>(RegisterParticleProvidersEvent.class, RegisterParticleProvidersEvent::registerSpecial);
    private static final Registrar<ParticleType, ParticleProvider.Sprite> SPRITE_PARTICLE_PROVIDERS = new Registrar<>(RegisterParticleProvidersEvent.class, RegisterParticleProvidersEvent::registerSprite);
    private static final Registrar<MenuType, MenuScreens.ScreenConstructor> SCREEN_FACTORIES = new Registrar<>(RegisterMenuScreensEvent.class, RegisterMenuScreensEvent::register);
    //?}

    public static <T extends Entity> void registerEntityRenderer(Supplier<EntityType<? extends T>> type, EntityRendererProvider<T> provider) {
        //? fabric {
        /*EntityRendererRegistry.register(type.get(), provider);
         *///?} else {
        ENTITY_RENDERERS.register(type, provider);
        //?}
    }

    public static <T extends BlockEntity> void registerEntityRenderer(Supplier<BlockEntityType<? extends T>> type, BlockEntityRendererProvider<T> provider) {
        //? fabric {
        /*BlockEntityRendererRegistryImpl.register(type.get(), provider);
         *///?} else {
        BLOCK_ENTITY_RENDERERS.register(type, provider);
        //?}
    }

    public <T extends ParticleOptions> void registerSpecialParticleProvider(Supplier<ParticleType<T>> type, ParticleProvider<T> provider) {
        //? fabric {
        /*ParticleFactoryRegistry.getInstance().register(type.get(), provider);
         *///?} else {
        SPECIAL_PARTICLE_PROVIDERS.register(type, provider);
        //?}
    }

    public <T extends ParticleOptions> void registerSpriteParticleProvider(Supplier<ParticleType<T>> type, ParticleProvider.Sprite<T> sprite) {
        //? fabric {
        /*ParticleFactoryRegistry.getInstance().register(type.get(), sprite::createParticle);
         *///?} else {
        SPRITE_PARTICLE_PROVIDERS.register(type, sprite);
        //?}
    }

    public void registerDynamicItemRenderer(DynamicItemRenderer renderer, ItemLike... items) {
        Arrays.stream(items).forEach(item -> DynamicItemRenderer.RENDERERS.put(item, renderer));
    }

    public void registerArmorRenderer(ArmorRenderer<?> renderer, ItemLike... items) {
        Arrays.stream(items).forEach(item -> ArmorRenderer.RENDERERS.put(item, renderer));
    }

    public static <H extends AbstractContainerMenu, S extends Screen & MenuAccess<H>> void registerScreenFactory(Supplier<MenuType<? extends H>> type, MenuScreens.ScreenConstructor<H, S> factory) {
        //? fabric {
        /*MenuScreens.register(type.get(), factory);
         *///?} else {
        SCREEN_FACTORIES.register(type, factory);
        //?}
    }

    //? !fabric {
    private static class Registrar<K, V> {
        private final Map<Supplier<? extends K>, V> data = new HashMap<>();
        private boolean registered = false;

        public <E extends Event> Registrar(Class<E> eventClass, TriConsumer<E, K, V> registerFunc) {
            SaturnBuses.findBus(Saturn.MOD_ID).addListener(eventClass, e -> this.visitRegister((k, v) -> registerFunc.accept(e, k, v)));
        }

        public void register(Supplier<? extends K> supplier, V value) {
            if (this.registered) {
                if (Platform.isDevelopmentEnvironment())
                    throw new IllegalStateException("This Registrar has posted register! Call this earlier.");
                else Saturn.LOGGER.warn("This Registrar has posted register! Some rendering may missing!");
            }
            this.data.put(supplier, value);
        }

        public void visitRegister(BiConsumer<K, V> visitor) {
            for (Map.Entry<Supplier<? extends K>, V> entry : this.data.entrySet())
                visitor.accept(entry.getKey().get(), entry.getValue());
            this.registered = true;
        }
    }
    //?}
}
