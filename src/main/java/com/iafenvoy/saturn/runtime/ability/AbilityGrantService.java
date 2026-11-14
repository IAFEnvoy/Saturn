package com.iafenvoy.saturn.runtime.ability;

import com.google.common.collect.SetMultimap;
import com.iafenvoy.saturn.attachment.AbilityAttachment;
import com.iafenvoy.saturn.data.ability.Ability;
import com.iafenvoy.saturn.event.CollectAbilityGrantsEvent;
import com.iafenvoy.saturn.profiling.ProfilerScope;
import com.iafenvoy.saturn.profiling.ServerProfiling;
import com.iafenvoy.saturn.registry.SaturnAttachments;
import com.iafenvoy.saturn.registry.SaturnProfilerSources;
import com.iafenvoy.saturn.runtime.Sources;
import net.minecraft.core.Holder;
import net.minecraft.resources.Identifier;
import net.minecraft.world.entity.LivingEntity;
import net.neoforged.neoforge.common.NeoForge;

import java.util.LinkedHashSet;
import java.util.Map;
import java.util.Set;

// Reconciles collected sources without touching independently granted sources.
public final class AbilityGrantService {
    private AbilityGrantService() {
    }

    public static Result recalculate(LivingEntity entity) {
        try (ProfilerScope _ = ServerProfiling.scope(SaturnProfilerSources.ABILITY_RECONCILE)) {
            // Grants are state, so only the server rebuilds them; a client reads what it was sent.
            if (entity.level().isClientSide()) return new Result(0, 0);
            SetMultimap<Identifier, Holder<Ability>> desired = NeoForge.EVENT_BUS.post(new CollectAbilityGrantsEvent(entity)).getDesired();
            AbilityAttachment abilities = entity.getData(SaturnAttachments.ABILITY_HOLDER);
            Result result = reconcile(abilities, desired);
            AbilityEventBridge.rebuildTriggerSubscriptions(entity);
            return result;
        }
    }

    public static Result reconcile(AbilityAttachment abilities, SetMultimap<Identifier, Holder<Ability>> desired) {
        Set<Identifier> sources = new LinkedHashSet<>(abilities.managedSources());
        // Existing saves predate explicit ownership; the old framework prefix is reserved for collected grants.
        abilities.sources().allSources().stream().filter(Sources::isGranted).forEach(sources::add);
        sources.addAll(desired.keySet());
        int granted = 0;
        int revoked = 0;
        // Add before releasing so a move between sources cannot destroy a still-granted ability's state.
        for (Map.Entry<Identifier, Holder<Ability>> entry : desired.entries())
            if (abilities.grant(entry.getValue(), entry.getKey())) granted++;
        for (Identifier source : sources) {
            Set<Holder<Ability>> wanted = desired.get(source);
            for (Holder<Ability> held : abilities.sources().keysHeldBy(source))
                if (!wanted.contains(held)) revoked++;
            abilities.reconcileSource(source, wanted);
        }
        abilities.setManagedSources(desired.keySet());
        return new Result(granted, revoked);
    }

    public record Result(int granted, int revoked) {
    }
}
