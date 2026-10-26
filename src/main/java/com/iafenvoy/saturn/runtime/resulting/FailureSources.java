package com.iafenvoy.saturn.runtime.resulting;

import com.iafenvoy.saturn.Saturn;
import com.iafenvoy.saturn.registry.SaturnResourceKeys;
import net.minecraft.resources.Identifier;

/**
 * The sources Saturn names in its own refusals, in one place: the datapack registry whose definitions can refuse, so a
 * module enum points at the same constant the key {@code failure.<namespace>.<path>.<name>} is built from.
 *
 * <p>{@link #COMMON} is the one for a refusal any module can answer with, and it is what the interface defaults to. A
 * dependent mod adds its own sources the same way - {@code FailureSource.of(itsOwnKey)} - and its own lang keys under
 * {@code failure.<its namespace>...}; nothing here has to know about them.
 */
public final class FailureSources {
    public static final FailureSource COMMON = new FailureSource(Identifier.fromNamespaceAndPath(Saturn.MOD_ID, "common"));
    public static final FailureSource ABILITY = FailureSource.of(SaturnResourceKeys.ABILITY);
    public static final FailureSource RESOURCE = FailureSource.of(SaturnResourceKeys.RESOURCE);

    private FailureSources() {
    }
}
