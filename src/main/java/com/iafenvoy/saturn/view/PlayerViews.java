package com.iafenvoy.saturn.view;

import com.iafenvoy.saturn.Saturn;
import com.iafenvoy.saturn.api.playerview.*;
import com.iafenvoy.saturn.view.model.*;
import net.minecraft.nbt.NbtOps;
import net.minecraft.resources.Identifier;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.entity.player.Player;

import java.util.*;
import java.util.function.BiConsumer;

/** Parses caller-supplied data only. Saturn neither loads player files nor persists or synchronizes these views. */
public final class PlayerViews {
    private static final Map<Identifier, Registration<Player>> CONTRIBUTORS = new LinkedHashMap<>();
    private static final Map<Identifier, Registration<SavedPlayerViewContext>> READERS = new LinkedHashMap<>();

    static {
        VanillaPlayerViewReader vanilla = new VanillaPlayerViewReader();
        registerContributor(vanilla);
        registerReader(vanilla);
        SaturnPlayerViewReader saturn = new SaturnPlayerViewReader();
        registerContributor(saturn);
        registerReader(saturn);
    }

    private PlayerViews() {
    }

    public static synchronized void registerContributor(PlayerViewContributor contributor) {
        Set<PlayerViewField<?>> fields = Set.copyOf(contributor.fields());
        validate(contributor.id(), fields, CONTRIBUTORS.values().stream().map(Registration::fields).toList());
        if (CONTRIBUTORS.putIfAbsent(contributor.id(), new Registration<>(contributor.id(), fields, contributor::capture)) != null)
            throw new IllegalArgumentException("Duplicate player view contributor: " + contributor.id());
    }

    public static synchronized void registerReader(SavedPlayerViewReader reader) {
        Set<PlayerViewField<?>> fields = Set.copyOf(reader.fields());
        validate(reader.id(), fields, READERS.values().stream().map(Registration::fields).toList());
        if (READERS.putIfAbsent(reader.id(), new Registration<>(reader.id(), fields, reader::read)) != null)
            throw new IllegalArgumentException("Duplicate saved player reader: " + reader.id());
    }

    public static PlayerViewSnapshot parse(Player player) {
        requireThread(player);
        long now = System.currentTimeMillis();
        return parse(player, new PlayerViewMeta(PlayerViewRef.current(player.getUUID()), UUID.randomUUID(),
                PlayerViewMeta.SourceKind.LIVE, Optional.of(now), Optional.of(player.level().getGameTime()), now));
    }

    public static PlayerViewSnapshot parse(Player player, PlayerViewMeta meta) {
        requireThread(player);
        if (!meta.ref().accountId().equals(player.getUUID())) throw new IllegalArgumentException("Player view account mismatch");
        PlayerViewSnapshot.Builder builder = PlayerViewSnapshot.builder(meta, player.registryAccess().createSerializationContext(NbtOps.INSTANCE));
        List<Registration<Player>> contributors;
        synchronized (PlayerViews.class) {
            contributors = List.copyOf(CONTRIBUTORS.values());
        }
        for (Registration<Player> contributor : contributors) {
            ScopedWriter writer = new ScopedWriter(builder, contributor.fields());
            try {
                contributor.parse.accept(player, writer);
            } catch (RuntimeException failure) {
                Saturn.LOGGER.warn("Failed to parse player view contributor {}", contributor.id(), failure);
                writer.failUnwritten();
            }
        }
        return builder.build();
    }

    public static PlayerViewSnapshot parse(SavedPlayerViewContext context) {
        return parse(context, Optional.empty());
    }

    /** A supplied recorded view takes precedence; readers only fill envelopes that are completely absent. */
    public static PlayerViewSnapshot parse(SavedPlayerViewContext context, Optional<PlayerViewSnapshot> recorded) {
        recorded.ifPresent(view -> {
            if (!view.meta().ref().equals(context.meta().ref())) throw new IllegalArgumentException("Recorded player view reference mismatch");
        });
        PlayerViewSnapshot.Builder builder = recorded.map(PlayerViewSnapshot::toBuilder)
                .orElseGet(() -> PlayerViewSnapshot.builder(context.meta(), context.registries().createSerializationContext(NbtOps.INSTANCE)));
        List<Registration<SavedPlayerViewContext>> readers;
        synchronized (PlayerViews.class) {
            readers = List.copyOf(READERS.values());
        }
        for (Registration<SavedPlayerViewContext> reader : readers) {
            Set<PlayerViewField<?>> absent = new LinkedHashSet<>(reader.fields());
            absent.removeIf(builder::contains);
            if (absent.isEmpty()) continue;
            ScopedWriter writer = new ScopedWriter(builder, Set.copyOf(reader.fields()), Set.copyOf(absent));
            try {
                reader.parse.accept(context, writer);
            } catch (RuntimeException failure) {
                Saturn.LOGGER.warn("Failed to parse saved player view reader {}", reader.id(), failure);
                writer.failUnwritten();
            }
        }
        return builder.build();
    }

    private static void validate(Identifier id, Set<PlayerViewField<?>> fields, List<Set<PlayerViewField<?>>> existing) {
        Objects.requireNonNull(id);
        for (PlayerViewField<?> field : Set.copyOf(fields)) {
            PlayerViewFields.requireRegistered(field);
            if (existing.stream().anyMatch(owned -> owned.contains(field)))
                throw new IllegalArgumentException("Player view field already has a parser: " + field.id());
        }
    }

    private static void requireThread(Player player) {
        if (player instanceof ServerPlayer serverPlayer && !serverPlayer.level().getServer().isSameThread())
            throw new IllegalStateException("Parse a server player on the server thread");
    }

    private record Registration<T>(Identifier id, Set<PlayerViewField<?>> fields, BiConsumer<T, PlayerViewWriter> parse) {
    }

    private static final class ScopedWriter implements PlayerViewWriter {
        private final PlayerViewSnapshot.Builder builder;
        private final Set<PlayerViewField<?>> owned;
        private final Set<PlayerViewField<?>> writable;
        private final Set<PlayerViewField<?>> written = new HashSet<>();

        private ScopedWriter(PlayerViewSnapshot.Builder builder, Set<PlayerViewField<?>> owned) {
            this(builder, owned, owned);
        }

        private ScopedWriter(PlayerViewSnapshot.Builder builder, Set<PlayerViewField<?>> owned, Set<PlayerViewField<?>> writable) {
            this.builder = builder;
            this.owned = owned;
            this.writable = writable;
        }

        @Override
        public <T> void put(PlayerViewField<T> field, T value, ValueOrigin origin) {
            if (!this.claim(field)) return;
            try {
                this.builder.put(field, value, origin);
            } catch (RuntimeException failure) {
                this.builder.missing(field, MissingReason.INVALID);
                Saturn.LOGGER.warn("Invalid parsed player view field {}", field.id(), failure);
            }
        }

        @Override
        public void missing(PlayerViewField<?> field, MissingReason reason) {
            if (this.claim(field)) this.builder.missing(field, reason);
        }

        @Override
        public boolean contains(PlayerViewField<?> field) {
            this.requireOwned(field);
            return this.builder.contains(field);
        }

        private boolean claim(PlayerViewField<?> field) {
            this.requireOwned(field);
            if (!this.writable.contains(field)) return false;
            if (!this.written.add(field)) throw new IllegalStateException("Player view field written twice: " + field.id());
            return true;
        }

        private void requireOwned(PlayerViewField<?> field) {
            if (!this.owned.contains(field)) throw new IllegalArgumentException("Parser does not own field: " + field.id());
        }

        private void failUnwritten() {
            for (PlayerViewField<?> field : this.writable)
                if (!this.written.contains(field)) this.missing(field, MissingReason.INVALID);
        }
    }
}
