package com.iafenvoy.saturn.registry;

import com.iafenvoy.saturn.Saturn;
import com.iafenvoy.saturn.attachment.AbilityAttachment;
import com.iafenvoy.saturn.attachment.ResourceHolderAttachment;
import com.iafenvoy.saturn.attachment.TriggerCooldownAttachment;
import com.iafenvoy.saturn.util.ChangeTrackable;
import com.mojang.serialization.MapCodec;
import net.minecraft.network.RegistryFriendlyByteBuf;
import net.minecraft.network.codec.ByteBufCodecs;
import net.minecraft.network.codec.StreamCodec;
import net.minecraft.world.entity.Entity;
import net.neoforged.bus.api.SubscribeEvent;
import net.neoforged.fml.common.EventBusSubscriber;
import net.neoforged.neoforge.attachment.AttachmentType;
import net.neoforged.neoforge.event.tick.EntityTickEvent.Post;
import net.neoforged.neoforge.registries.DeferredHolder;
import net.neoforged.neoforge.registries.DeferredRegister;
import net.neoforged.neoforge.registries.NeoForgeRegistries;

import java.util.ArrayList;
import java.util.List;
import java.util.function.Supplier;

/**
 * The entity attachments Saturn owns: the numeric resources of a body and the abilities it was granted.
 *
 * <p>Only state that belongs to a mechanism Saturn itself runs lives here. A module of a dependent mod keeps its own
 * attachments, so the reset one module performs cannot reach another's; a dependent that needs a store of its own
 * registers it with {@code NeoForgeRegistries.ATTACHMENT_TYPES} under its own namespace.
 */
@EventBusSubscriber
public final class SaturnAttachments {
    public static final DeferredRegister<AttachmentType<?>> REGISTRY = DeferredRegister.create(NeoForgeRegistries.ATTACHMENT_TYPES, Saturn.MOD_ID);
    private static final List<Supplier<? extends AttachmentType<?>>> SYNCED_ENTITY_ATTACHMENTS = new ArrayList<>();

    public static final DeferredHolder<AttachmentType<?>, AttachmentType<ResourceHolderAttachment>> RESOURCE_HOLDER = entity("resource_holder", ResourceHolderAttachment::new, ResourceHolderAttachment.CODEC);
    public static final DeferredHolder<AttachmentType<?>, AttachmentType<AbilityAttachment>> ABILITY_HOLDER = entity("ability_holder", AbilityAttachment::new, AbilityAttachment.CODEC);
    // Server-owned and never synced: nothing on the client draws a rule's cooldown.
    public static final DeferredHolder<AttachmentType<?>, AttachmentType<TriggerCooldownAttachment>> TRIGGER_COOLDOWNS = entityServerOnly("trigger_cooldowns", TriggerCooldownAttachment::new, TriggerCooldownAttachment.CODEC);

    @SubscribeEvent
    public static void flushDirtyAttachments(Post event) {
        Entity entity = event.getEntity();
        if (entity.level().isClientSide()) return;
        for (Supplier<? extends AttachmentType<?>> type : SYNCED_ENTITY_ATTACHMENTS) {
            AttachmentType<?> rawType = type.get();
            if (entity.getExistingData(rawType).orElse(null) instanceof ChangeTrackable attachment && attachment.checkDirty())
                entity.syncData(rawType);
        }
    }

    // Saved and copied on death but never sent to the client.
    private static <T> DeferredHolder<AttachmentType<?>, AttachmentType<T>> entityServerOnly(String name, Supplier<T> factory, MapCodec<T> codec) {
        return REGISTRY.register(name, () -> AttachmentType.builder(factory).serialize(codec).copyOnDeath().build());
    }

    private static <T extends ChangeTrackable> DeferredHolder<AttachmentType<?>, AttachmentType<T>> entity(String name, Supplier<T> factory, MapCodec<T> codec) {
        StreamCodec<RegistryFriendlyByteBuf, T> streamCodec = ByteBufCodecs.fromCodecWithRegistries(codec.codec());
        DeferredHolder<AttachmentType<?>, AttachmentType<T>> holder = REGISTRY.register(name, () -> AttachmentType.builder(factory).serialize(codec).sync(streamCodec).copyOnDeath().build());
        SYNCED_ENTITY_ATTACHMENTS.add(holder);
        return holder;
    }
}
