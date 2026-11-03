package com.iafenvoy.saturn.profiling;

import com.iafenvoy.saturn.registry.SaturnRegistries;
import net.minecraft.resources.Identifier;
import net.neoforged.neoforge.registries.RegisterEvent;
import org.jetbrains.annotations.Nullable;

import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

/**
 * One bucket a scope can be opened under, named by a registry id. The registry is synced, so {@link #index()}, which
 * addresses a source's column in a sample array, means the same thing on both sides.
 *
 * <p>Sources are claimed during mod setup ({@link #register(String, String, ProfilerSource)}) and registered into
 * {@link SaturnRegistries#PROFILER_SOURCE} once the registry event runs. Two claims of one id are one bucket, first
 * claim wins; the list is read at server start, so a source claimed later is simply not sampled.
 */
public final class ProfilerSource {
    public static final int MAX_SOURCES = Long.SIZE;
    private static final Map<Identifier, ProfilerSource> DECLARED = new LinkedHashMap<>();
    private final Identifier key;
    private final @Nullable Identifier parentKey;
    private volatile int index = -1;
    private volatile @Nullable ProfilerSource parent;

    private ProfilerSource(Identifier key, @Nullable Identifier parentKey) {
        this.key = key;
        this.parentKey = parentKey;
    }

    public static ProfilerSource register(String modId, String path) {
        return register(modId, path, null);
    }

    /**
     * Claims {@code modId:path}; the parent is named rather than held so a nested bucket does not depend on its
     * parent having been claimed first.
     */
    public static synchronized ProfilerSource register(String modId, String path, @Nullable ProfilerSource parent) {
        if (modId == null || modId.isBlank()) throw new IllegalArgumentException("A profiling source needs a mod id");
        if (path == null || path.isBlank()) throw new IllegalArgumentException("A profiling source needs an id");
        Identifier key = Identifier.fromNamespaceAndPath(modId, path);
        ProfilerSource existing = DECLARED.get(key);
        if (existing != null) return existing;
        ProfilerSource created = new ProfilerSource(key, parent == null ? null : parent.key);
        DECLARED.put(key, created);
        return created;
    }

    // Called from the register event, after every mod has claimed its sources and in the order they claimed them.
    static synchronized void install(RegisterEvent.RegisterHelper<ProfilerSource> registry) {
        DECLARED.values().forEach(source -> registry.register(source.key, source));
    }

    public Identifier key() {
        return this.key;
    }

    public String id() {
        return this.key.toString();
    }

    public String path() {
        return this.key.getPath();
    }

    // The synced numeric id, and the column a sample array of this source stores it in. Read once per scope, so it
    // is cached rather than looked up in the registry every time.
    public int index() {
        int value = this.index;
        if (value < 0) this.index = value = SaturnRegistries.PROFILER_SOURCE.getId(this);
        return value;
    }

    public @Nullable ProfilerSource parent() {
        ProfilerSource known = this.parent;
        // Not cached before the claim is registered: a lookup that ran too early must not hide the parent forever.
        if (known != null || this.parentKey == null || this.index() < 0) return known;
        return this.parent = SaturnRegistries.PROFILER_SOURCE.getValue(this.parentKey);
    }

    public String translationKey() {
        String namespace = this.key.getNamespace();
        return namespace + ".profiling." + namespace + "." + this.path().replace('/', '.');
    }

    // What this bucket counts, one line, shown while the pointer rests on a row's source label.
    public String tooltipKey() {
        return this.translationKey() + ".tooltip";
    }

    public static @Nullable ProfilerSource byPath(String path) {
        String wanted = path.startsWith("server/") ? path.substring("server/".length()) : path;
        for (ProfilerSource source : all())
            if (source.path().equals(wanted)) return source;
        return null;
    }

    // Registry order, which is numeric id order: the same order a snapshot lists its columns in.
    public static List<ProfilerSource> all() {
        return SaturnRegistries.PROFILER_SOURCE.stream().toList();
    }

    public static int count() {
        return SaturnRegistries.PROFILER_SOURCE.size();
    }
}
