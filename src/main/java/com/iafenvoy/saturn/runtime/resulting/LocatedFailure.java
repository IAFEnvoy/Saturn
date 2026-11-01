package com.iafenvoy.saturn.runtime.resulting;

import net.minecraft.resources.Identifier;

import java.util.Optional;

/**
 * A refusal that also names the resource it was about. The reason stays the same constant, so a caller that only
 * wants to know why reads it exactly like a plain constant.
 */
public record LocatedFailure(Failure reason, Identifier id) implements Failure {
    @Override
    public String name() {
        return this.reason.name();
    }

    @Override
    public FailureSource source() {
        return this.reason.source();
    }

    @Override
    public Optional<Identifier> resource() {
        return Optional.of(this.id);
    }
}
