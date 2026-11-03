package com.iafenvoy.saturn.profiling;

import com.iafenvoy.saturn.Saturn;
import com.iafenvoy.saturn.registry.SaturnRegistries;
import net.minecraft.server.MinecraftServer;
import net.neoforged.bus.api.EventPriority;
import net.neoforged.bus.api.SubscribeEvent;
import net.neoforged.fml.common.EventBusSubscriber;
import net.neoforged.neoforge.event.server.ServerAboutToStartEvent;
import net.neoforged.neoforge.event.server.ServerStoppedEvent;
import net.neoforged.neoforge.event.tick.ServerTickEvent;
import net.neoforged.neoforge.registries.RegisterEvent;

import java.util.concurrent.CompletableFuture;

// Only the owning server thread is sampled; worker threads need their own verified tick boundaries.
@EventBusSubscriber
public final class ServerProfiling {
    private static final ProfilerScope NOOP = () -> {
    };
    private static volatile Session current;

    private ServerProfiling() {
    }

    public static boolean enabled(MinecraftServer server) {
        Session session = current;
        return session != null && session.server == server && session.profiler.enabled();
    }

    public static boolean setEnabled(MinecraftServer server, boolean enabled) {
        Session session = current;
        if (session == null || session.server != server || !server.isSameThread()) return false;
        if (session.profiler.enabled() != enabled) {
            session.profiler.setEnabled(enabled);
            session.active = 0L;
            session.epoch++;
        }
        return true;
    }

    public static CompletableFuture<ProfilerWindowSnapshot> snapshot(MinecraftServer server) {
        Session session = current;
        return session != null && session.server == server ? session.profiler.snapshot()
                : CompletableFuture.completedFuture(ProfilerWindowSnapshot.EMPTY);
    }

    public static ProfilerScope scope(ProfilerSource source) {
        Session session = current;
        if (session == null || !session.profiler.enabled() || !session.server.isSameThread()) return NOOP;
        return session.scope(source);
    }

    // After every mod has claimed its sources: the registry is what turns a claim into a synced numeric id.
    @SubscribeEvent
    public static void registerSources(RegisterEvent event) {
        event.register(SaturnRegistries.PROFILER_SOURCE.key(), ProfilerSource::install);
    }

    @SubscribeEvent(priority = EventPriority.HIGHEST)
    public static void onServerStarting(ServerAboutToStartEvent event) {
        Session previous = current;
        current = new Session(event.getServer());
        setEnabled(event.getServer(), Saturn.isDevMode());
        if (previous != null) previous.profiler.close();
    }

    @SubscribeEvent(priority = EventPriority.LOWEST)
    public static void onServerTick(ServerTickEvent.Post event) {
        Session session = current;
        if (session == null || session.server != event.getServer()) return;
        session.profiler.flushTick();
        session.active = 0L;
        session.epoch++;
    }

    @SubscribeEvent(priority = EventPriority.LOWEST)
    public static void onServerStopped(ServerStoppedEvent event) {
        Session session = current;
        if (session == null || session.server != event.getServer()) return;
        current = null;
        session.profiler.close();
    }

    private static final class Session {
        private final MinecraftServer server;
        private final Profiler profiler = new Profiler();
        private long active;
        private long epoch;

        private Session(MinecraftServer server) {
            this.server = server;
            if (ProfilerSource.count() > ProfilerSource.MAX_SOURCES)
                throw new IllegalStateException("At most " + ProfilerSource.MAX_SOURCES + " profiling sources are supported");
        }

        // Reentrant calls share their outermost source; named children also open their parent when entered alone.
        private ProfilerScope scope(ProfilerSource source) {
            long bit = 1L << source.index();
            if ((this.active & bit) != 0L) return NOOP;
            ProfilerSource parentSource = source.parent();
            ProfilerScope parent = parentSource == null ? NOOP : this.scope(parentSource);
            this.active |= bit;
            ProfilerScope sample = this.profiler.scope(source);
            long opened = this.epoch;
            return new ProfilerScope() {
                private boolean closed;

                @Override
                public void close() {
                    if (this.closed || !Session.this.server.isSameThread()) return;
                    this.closed = true;
                    try {
                        sample.close();
                    } finally {
                        if (Session.this.epoch == opened) Session.this.active &= ~bit;
                        parent.close();
                    }
                }
            };
        }
    }
}
