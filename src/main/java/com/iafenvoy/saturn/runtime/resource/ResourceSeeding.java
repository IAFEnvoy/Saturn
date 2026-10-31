package com.iafenvoy.saturn.runtime.resource;

import com.iafenvoy.saturn.attachment.ResourceHolderAttachment;
import com.iafenvoy.saturn.data.resource.Resource;
import com.iafenvoy.saturn.registry.SaturnAttachments;
import com.iafenvoy.saturn.registry.SaturnDatapackRegistries;
import com.iafenvoy.saturn.registry.SaturnResourceKeys;
import com.iafenvoy.saturn.util.formula.FormulaContext;
import net.minecraft.core.Holder.Reference;
import net.minecraft.world.entity.LivingEntity;
import net.neoforged.bus.api.SubscribeEvent;
import net.neoforged.fml.common.EventBusSubscriber;
import net.neoforged.neoforge.event.entity.player.PlayerEvent.PlayerLoggedInEvent;

/**
 * Brings a body's whole resource attachment onto the pack's current definitions when it joins: an entry it has never
 * held is seeded from {@code default_value}, and an entry whose stored bounds the datapack has since changed is
 * rebound. A resource is otherwise only created by its first change, which leaves a fresh body unable to pay a cost it
 * writes a default for, and a save written under an older cap stuck on it.
 *
 * <p>The pass never invents a balance: an entry that already had room is only clamped into the current bounds.
 */
@EventBusSubscriber
public final class ResourceSeeding {
    private ResourceSeeding() {
    }

    @SubscribeEvent
    public static void onPlayerLogin(PlayerLoggedInEvent event) {
        seed(event.getEntity());
    }

    public static void seed(LivingEntity entity) {
        if (entity.level().isClientSide()) return;
        ResourceHolderAttachment holder = entity.getData(SaturnAttachments.RESOURCE_HOLDER);
        FormulaContext base = FormulaContext.of(entity);
        for (Reference<Resource> resource : SaturnDatapackRegistries.holders(entity.level().registryAccess(), SaturnResourceKeys.RESOURCE).toList()) {
            FormulaContext context = ResourceService.formulaContext(entity, resource, base);
            if (!holder.contains(resource)) ResourceService.initialize(holder, resource, context);
            else ResourceService.rebind(holder, resource, context);
        }
    }
}
