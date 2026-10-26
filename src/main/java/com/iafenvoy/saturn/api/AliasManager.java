package com.iafenvoy.saturn.api;

import net.minecraft.resources.Identifier;

import java.util.Optional;

/**
 * Decides which old ids a content namespace used for the built-in entries before they moved under {@code saturn}.
 *
 * <p>Saturn's built-in entries are registered under {@code saturn:}, so a data pack, a saved tag or another mod
 * that still names one as {@code mxt:chance} or {@code mymod:heal} would no longer resolve. Registry aliases are the
 * answer: {@code addAlias(oldId, saturnId)} makes the old spelling read as the new entry, without touching the file.
 *
 * <p>The default {@link #SAME_PATH} maps the same path into the caller's namespace, which is exactly what a move
 * from {@code mxt:} to {@code saturn:} looks like. Install a custom manager with
 * {@link com.iafenvoy.saturn.registry.SaturnAliases#set(AliasManager)} when a path was renamed as well.
 */
@FunctionalInterface
public interface AliasManager {
    /**
     * The old id of one built-in entry, or empty when this namespace never used that entry.
     *
     * @param namespace the content mod's own namespace, as passed to
     *                  {@link com.iafenvoy.saturn.registry.SaturnAliases#add(String)}
     * @param id        the id the entry is registered under, always in {@code saturn}
     */
    Optional<Identifier> alias(String namespace, Identifier id);

    /**
     * The shape of the split itself: the same path, in the content mod's namespace.
     */
    AliasManager SAME_PATH = (namespace, id) -> Optional.of(Identifier.fromNamespaceAndPath(namespace, id.getPath()));
}
