package com.iafenvoy.saturn.runtime.ability;

import com.iafenvoy.saturn.attachment.AbilityAttachment;
import com.iafenvoy.saturn.attachment.ResourceHolderAttachment;
import com.iafenvoy.saturn.data.ability.Ability;
import com.iafenvoy.saturn.data.ability.Togglable;
import com.iafenvoy.saturn.data.ability.ToggleContext;
import com.iafenvoy.saturn.profiling.ProfilerScope;
import com.iafenvoy.saturn.profiling.ServerProfiling;
import com.iafenvoy.saturn.registry.SaturnAttachments;
import com.iafenvoy.saturn.registry.SaturnProfilerSources;
import com.iafenvoy.saturn.runtime.resulting.Failure;
import com.iafenvoy.saturn.runtime.resulting.Result;
import com.iafenvoy.saturn.runtime.resulting.failures.AbilityFailure;
import com.iafenvoy.saturn.util.OptionalBoolean;
import net.minecraft.core.Holder;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.item.ItemStack;
import org.jetbrains.annotations.Nullable;

import java.util.Optional;

/**
 * The one press entry point. The client, the command and the script bridge all come through here, so "what happens
 * when this is pressed" has a single answer and a single place the shared gate is applied.
 *
 * <p>The ability may be carried by the pressing item or merely granted to the holder; a type that acts on an item
 * says so by refusing a press with no carrier.
 */
public final class AbilityActivationService {
    private AbilityActivationService() {
    }

    public static Result<Void> activate(LivingEntity holder, Holder<Ability> ability, @Nullable ItemStack carrier) {
        try (ProfilerScope _ = ServerProfiling.scope(SaturnProfilerSources.ABILITY_CAST)) {
            if (holder == null || ability == null || !(ability.value().type() instanceof Togglable togglable))
                return Result.failure(AbilityFailure.UNAVAILABLE);
            // A press writes state and world, so only the server answers one; reading the state stays two-sided.
            if (holder.level().isClientSide()) return Result.failure(AbilityFailure.UNAVAILABLE);
            ToggleContext context = new ToggleContext(holder, carrier, ability);
            // Nothing is paid for a press that cannot happen: the read-only precondition is asked before the gate.
            Optional<Failure> refusal = togglable.canActivate(context);
            if (refusal.isPresent()) return Result.failure(refusal.get());
            if (!togglable.gated(context)) return togglable.activate(context);
            Optional<Failure> gate = AbilityService.gate(context);
            return gate.<Result<Void>>map(Result::failure).orElseGet(() -> togglable.activate(context));
        }
    }

    // Read-only, and safe on either side: the state an implementation reports is part of its declaration.
    public static OptionalBoolean state(LivingEntity holder, Holder<Ability> ability) {
        if (holder == null || ability == null || !(ability.value().type() instanceof Togglable togglable))
            return OptionalBoolean.empty();
        return togglable.state(new ToggleContext(holder, null, ability));
    }

    public static boolean togglable(Holder<Ability> ability) {
        return ability != null && ability.value().type() instanceof Togglable;
    }

    // The cast path an saturn:active press takes; kept here so the type itself stays a plain data record.
    public static Result<Void> cast(ToggleContext context) {
        try (ProfilerScope _ = ServerProfiling.scope(SaturnProfilerSources.ABILITY_CAST)) {
            LivingEntity holder = context.holder();
            AbilityAttachment abilities = holder.getData(SaturnAttachments.ABILITY_HOLDER);
            ResourceHolderAttachment resources = holder.getData(SaturnAttachments.RESOURCE_HOLDER);
            Result<AbilityService.UseData> result = AbilityService.use(context.ability(), holder, abilities, resources,
                    holder.level().getGameTime(), context.formula());
            return result instanceof Result.FailureResult<?> ? Result.failure(result.getFailure().orElseThrow()) : Result.success();
        }
    }
}
