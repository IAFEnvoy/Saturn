package com.iafenvoy.saturn.registry;

//? neoforge {
/*import com.iafenvoy.saturn.Platform;
import com.iafenvoy.saturn.Saturn;
import com.iafenvoy.saturn.bus.SaturnBuses;
import com.iafenvoy.saturn.util.function.Consumer3;
import net.neoforged.bus.api.Event;
import org.jetbrains.annotations.ApiStatus;

import java.util.HashMap;
import java.util.Map;
import java.util.function.BiConsumer;
import java.util.function.Supplier;

@ApiStatus.Internal
public final class ForgeLikeEventRegistrar<K, V> {
    private final Map<Supplier<? extends K>, V> data = new HashMap<>();
    private boolean registered = false;

    public <E extends Event> ForgeLikeEventRegistrar(Class<E> eventClass, Consumer3<E, K, V> registerFunc) {
        SaturnBuses.findBus(Saturn.MOD_ID).addListener(eventClass, e -> this.visitRegister((k, v) -> registerFunc.accept(e, k, v)));
    }

    public void register(Supplier<? extends K> supplier, V value) {
        if (this.registered) {
            if (Platform.isDevelopmentEnvironment())
                throw new IllegalStateException("This Registrar has posted register! Call this earlier.");
            else Saturn.LOGGER.warn("This Registrar has posted register! Something may missing!");
        }
        this.data.put(supplier, value);
    }

    public void visitRegister(BiConsumer<K, V> visitor) {
        for (Map.Entry<Supplier<? extends K>, V> entry : this.data.entrySet())
            visitor.accept(entry.getKey().get(), entry.getValue());
        this.registered = true;
    }
}
*/