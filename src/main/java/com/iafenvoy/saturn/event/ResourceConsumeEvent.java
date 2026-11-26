package com.iafenvoy.saturn.event;

import com.iafenvoy.saturn.attachment.ResourceHolderAttachment;
import net.minecraft.resources.Identifier;
import net.minecraft.world.entity.Entity;
import net.neoforged.bus.api.Event;
import net.neoforged.bus.api.ICancellableEvent;
import org.jetbrains.annotations.Nullable;

import java.util.Collections;
import java.util.LinkedHashMap;
import java.util.Map;

/**
 * Server-side notification around the simulation and real deduction of common resources.
 */
public abstract class ResourceConsumeEvent extends Event {
    private final @Nullable Entity entity;
    private final ResourceHolderAttachment holder;
    private final Map<Identifier, Double> amounts;

    protected ResourceConsumeEvent(ResourceHolderAttachment holder, Map<Identifier, Double> amounts) {
        this(null, holder, amounts);
    }

    protected ResourceConsumeEvent(@Nullable Entity entity, ResourceHolderAttachment holder, Map<Identifier, Double> amounts) {
        this.entity = entity;
        this.holder = holder.copy();
        this.amounts = new LinkedHashMap<>(amounts);
        this.amounts.forEach(ResourceConsumeEvent::validate);
    }

    public ResourceHolderAttachment getHolder() {
        return this.holder.copy();
    }

    public @Nullable Entity getEntity() {
        return this.entity;
    }

    public Map<Identifier, Double> getAmounts() {
        return Collections.unmodifiableMap(this.amounts);
    }

    protected void setAmount(Identifier id, double amount) {
        validate(id, amount);
        if (amount == 0.0D) this.amounts.remove(id);
        else this.amounts.put(id, amount);
    }

    private static void validate(Identifier id, double amount) {
        if (id == null || !Double.isFinite(amount) || amount < 0.0D)
            throw new IllegalArgumentException("Resource event costs must be finite and non-negative");
    }

    public static final class Pre extends ResourceConsumeEvent implements ICancellableEvent {
        public Pre(ResourceHolderAttachment holder, Map<Identifier, Double> amounts) {
            super(holder, amounts);
        }

        public Pre(Entity entity, ResourceHolderAttachment holder, Map<Identifier, Double> amounts) {
            super(entity, holder, amounts);
        }

        // Widened here so a listener outside this package can change what is paid; a post event stays read-only.
        @Override
        public void setAmount(Identifier id, double amount) {
            super.setAmount(id, amount);
        }
    }

    public static final class Post extends ResourceConsumeEvent {
        public Post(ResourceHolderAttachment holder, Map<Identifier, Double> amounts) {
            super(holder, amounts);
        }

        public Post(Entity entity, ResourceHolderAttachment holder, Map<Identifier, Double> amounts) {
            super(entity, holder, amounts);
        }
    }
}
