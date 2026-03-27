package com.iafenvoy.saturn.registry;

import net.fabricmc.fabric.api.client.particle.v1.ParticleProviderRegistry;
import net.minecraft.client.gui.screens.MenuScreens;
import net.minecraft.client.gui.screens.Screen;
import net.minecraft.client.gui.screens.inventory.MenuAccess;
import net.minecraft.client.particle.ParticleProvider;
import net.minecraft.client.renderer.blockentity.BlockEntityRendererProvider;
import net.minecraft.client.renderer.blockentity.state.BlockEntityRenderState;
import net.minecraft.client.renderer.entity.EntityRendererProvider;
import net.minecraft.core.particles.ParticleOptions;
import net.minecraft.core.particles.ParticleType;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.inventory.AbstractContainerMenu;
import net.minecraft.world.inventory.MenuType;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.entity.BlockEntityType;
//? fabric {
import net.minecraft.client.renderer.blockentity.BlockEntityRenderers;
import net.minecraft.client.renderer.entity.EntityRenderers;
import org.jspecify.annotations.NullMarked;
//?} else neoforge {
/*import net.minecraft.client.particle.ParticleResources;
import net.neoforged.neoforge.client.event.EntityRenderersEvent;
import net.neoforged.neoforge.client.event.RegisterMenuScreensEvent;
import net.neoforged.neoforge.client.event.RegisterParticleProvidersEvent;
import org.jspecify.annotations.NullMarked;
*///?}

import java.util.function.Supplier;

//? fabric {
@SuppressWarnings("unused")
 //?} else {
/*@SuppressWarnings({"rawtypes", "unused"})
*///?}
public final class RendererRegistry {
    //? neoforge {
    /*private static final ForgeLikeEventRegistrar<EntityType, EntityRendererProvider> ENTITY_RENDERERS = new ForgeLikeEventRegistrar<>(EntityRenderersEvent.RegisterRenderers.class, EntityRenderersEvent.RegisterRenderers::registerEntityRenderer);
    private static final ForgeLikeEventRegistrar<BlockEntityType, BlockEntityRendererProvider> BLOCK_ENTITY_RENDERERS = new ForgeLikeEventRegistrar<>(EntityRenderersEvent.RegisterRenderers.class, EntityRenderersEvent.RegisterRenderers::registerBlockEntityRenderer);
    private static final ForgeLikeEventRegistrar<ParticleType, ParticleProvider> SPECIAL_PARTICLE_PROVIDERS = new ForgeLikeEventRegistrar<>(RegisterParticleProvidersEvent.class, RegisterParticleProvidersEvent::registerSpecial);
    private static final ForgeLikeEventRegistrar<ParticleType, ParticleResources.SpriteParticleRegistration> SPRITE_PARTICLE_PROVIDERS = new ForgeLikeEventRegistrar<>(RegisterParticleProvidersEvent.class, RegisterParticleProvidersEvent::registerSpriteSet);
    private static final ForgeLikeEventRegistrar<MenuType, MenuScreens.ScreenConstructor> SCREEN_FACTORIES = new ForgeLikeEventRegistrar<>(RegisterMenuScreensEvent.class, RegisterMenuScreensEvent::register);
    *///?}

    public static <T extends Entity> void registerEntityRenderer(Supplier<EntityType<? extends T>> type, EntityRendererProvider<T> provider) {
        //? fabric {
        EntityRenderers.register(type.get(), provider);
         //?} else {
        /*ENTITY_RENDERERS.register(type, provider);
        *///?}
    }

    public static <T extends BlockEntity, S extends BlockEntityRenderState> void registerEntityRenderer(Supplier<BlockEntityType<? extends T>> type, BlockEntityRendererProvider<T, S> provider) {
        //? fabric {
        BlockEntityRenderers.register(type.get(), provider);
         //?} else {
        /*BLOCK_ENTITY_RENDERERS.register(type, provider);
        *///?}
    }

    public <T extends ParticleOptions> void registerSpecialParticleProvider(Supplier<ParticleType<T>> type, ParticleProvider<T> provider) {
        //? fabric {
        ParticleProviderRegistry.getInstance().register(type.get(), provider);
         //?} else {
        /*SPECIAL_PARTICLE_PROVIDERS.register(type, provider);
        *///?}
    }

    public <T extends ParticleOptions> void registerSpriteParticleProvider(Supplier<ParticleType<T>> type, /*? neoforge {*//*ParticleResources.SpriteParticleRegistration<T>*//*?} else {*/ParticleProviderRegistry.PendingParticleProvider<T>/*?}*/ sprite) {
        //? fabric {
        ParticleProviderRegistry.getInstance().register(type.get(), sprite);
         //?} else {
        /*SPRITE_PARTICLE_PROVIDERS.register(type, sprite);
        *///?}
    }

    @NullMarked
    public static <T extends AbstractContainerMenu, U extends Screen & MenuAccess<T>> void registerScreenFactory(Supplier<MenuType<? extends T>> type, MenuScreens.ScreenConstructor<T, U> factory) {
        //? neoforge {
        /*SCREEN_FACTORIES.register(type, factory);
        *///?} else {
        MenuScreens.register(type.get(), factory);
         //?}
    }
}
