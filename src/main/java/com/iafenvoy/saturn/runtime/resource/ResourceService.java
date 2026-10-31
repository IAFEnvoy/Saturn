package com.iafenvoy.saturn.runtime.resource;

import com.iafenvoy.saturn.attachment.ResourceHolderAttachment;
import com.iafenvoy.saturn.attachment.ResourceHolderAttachment.Audit;
import com.iafenvoy.saturn.data.resource.Resource;
import com.iafenvoy.saturn.registry.SaturnDatapackRegistries;
import com.iafenvoy.saturn.registry.SaturnResourceKeys;
import com.iafenvoy.saturn.util.formula.FormulaContext;
import com.iafenvoy.saturn.util.formula.FormulaContexts;
import com.iafenvoy.saturn.util.formula.NumberProvider;
import net.minecraft.core.Holder;
import net.minecraft.core.HolderLookup.Provider;
import net.minecraft.resources.Identifier;
import net.minecraft.world.entity.LivingEntity;
import org.jetbrains.annotations.Nullable;

import java.util.Optional;

/**
 * Applies resource definition bounds consistently for initialization, changes and passive regeneration.
 */
public final class ResourceService {
    private ResourceService() {
    }

    public static Result initialize(ResourceHolderAttachment holder, Holder<Resource> resource, FormulaContext context) {
        Resource definition = resource.value();
        if (holder.contains(resource)) return Result.unchanged(holder.get(resource));
        Bounds bounds = bounds(definition, context);
        if (bounds == null) return Result.invalid();
        double value = definition.defaultValue().evaluate(context);
        if (!Double.isFinite(value)) return Result.invalid();
        double clamped = clamp(value, bounds);
        holder.set(resource, clamped, bounds.min(), bounds.max(), -1L, "initialize");
        return Result.changed(clamped);
    }

    public static Result initialize(ResourceHolderAttachment holder, Identifier id, FormulaContext context) {
        return resource(context.registries(), id)
                .map(resource -> initialize(holder, resource, context)).orElse(Result.invalid());
    }

    public static Result change(ResourceHolderAttachment holder, Holder<Resource> resource, double amount, FormulaContext context) {
        Resource definition = resource.value();
        if (!Double.isFinite(amount)) return Result.invalid();
        Result initialized = initialize(holder, resource, context);
        if (!initialized.valid()) return initialized;
        Bounds bounds = bounds(definition, context);
        if (bounds == null) return Result.invalid();
        double value = clamp(holder.get(resource) + amount, bounds);
        if (Double.compare(value, holder.get(resource)) == 0) return Result.unchanged(value);
        holder.set(resource, value, bounds.min(), bounds.max(), holder.audit(resource).lastChangedTick(), "resource_change");
        return Result.changed(value);
    }

    public static Result change(ResourceHolderAttachment holder, Identifier id, double amount, FormulaContext context) {
        return resource(context.registries(), id)
                .map(resource -> change(holder, resource, amount, context)).orElse(Result.invalid());
    }

    // An id is resolved through the same access the formula reads, never through a running server: a context
    // built off a client has no server to reach for, and one that carries none resolves nothing.
    private static Optional<Holder<Resource>> resource(@Nullable Provider access, Identifier id) {
        return access == null ? Optional.empty() : SaturnDatapackRegistries.holder(access, SaturnResourceKeys.RESOURCE, id);
    }

    public static Result regenerate(ResourceHolderAttachment holder, Holder<Resource> resource, NumberProvider regen,
                                    long elapsedTicks, FormulaContext context) {
        if (elapsedTicks < 0L) throw new IllegalArgumentException("Elapsed ticks cannot be negative");
        double amount = regen.evaluate(context);
        if (!Double.isFinite(amount)) return Result.invalid();
        return change(holder, resource, amount * elapsedTicks, context);
    }

    // absorbed_aura is zero when the player has no realm stage in this resource's chain.
    public static FormulaContext formulaContext(LivingEntity entity, Holder<Resource> resource, FormulaContext base) {
        return FormulaContexts.forEntity(entity, base).withResource(resource);
    }

    public static FormulaContext formulaContext(LivingEntity entity, Identifier resource, FormulaContext base) {
        return resource(entity.level().registryAccess(), resource)
                .map(value -> formulaContext(entity, value, base)).orElse(base);
    }

    public static Optional<Bounds> resolveBounds(Resource definition, FormulaContext context) {
        double min = definition.min().evaluate(context);
        double max = definition.max().evaluate(context);
        return Double.isFinite(min) && Double.isFinite(max) && min <= max ? Optional.of(new Bounds(min, max)) : Optional.empty();
    }

    private static Bounds bounds(Resource definition, FormulaContext context) {
        return resolveBounds(definition, context).orElse(null);
    }

    /**
     * Brings a value already on the attachment back onto the bounds the pack defines now. The stored bounds travel
     * with every write, so a datapack whose cap changed since the last write leaves a stale one behind; that is
     * refreshed here. A value that was written while the cap was zero could never have had headroom and is seeded
     * from the default again - every other value is only clamped into the new bounds, never refilled.
     */
    public static Result rebind(ResourceHolderAttachment holder, Holder<Resource> resource, FormulaContext context) {
        if (!holder.contains(resource)) return Result.invalid();
        Resource definition = resource.value();
        Bounds bounds = bounds(definition, context);
        if (bounds == null) return Result.invalid();
        Audit previous = holder.audit(resource);
        double current = holder.get(resource);
        double value = clamp(current, bounds);
        if (previous.maxSnapshot() <= 0.0D && bounds.max() > 0.0D) {
            double seeded = definition.defaultValue().evaluate(context);
            if (Double.isFinite(seeded)) value = clamp(seeded, bounds);
        }
        boolean sameBounds = Double.compare(previous.minSnapshot(), bounds.min()) == 0
                && Double.compare(previous.maxSnapshot(), bounds.max()) == 0;
        if (sameBounds && Double.compare(value, current) == 0) return Result.unchanged(value);
        holder.set(resource, value, bounds.min(), bounds.max(), previous.lastChangedTick(), "rebind");
        return Result.changed(value);
    }

    private static double clamp(double value, Bounds bounds) {
        return Math.max(bounds.min(), Math.min(bounds.max(), value));
    }

    public record Bounds(double min, double max) {
    }

    public record Result(boolean valid, boolean changed, double value) {
        private static Result invalid() {
            return new Result(false, false, Double.NaN);
        }

        private static Result changed(double value) {
            return new Result(true, true, value);
        }

        private static Result unchanged(double value) {
            return new Result(true, false, value);
        }
    }
}
