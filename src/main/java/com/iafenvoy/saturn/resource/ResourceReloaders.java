package com.iafenvoy.saturn.resource;

//? fabric {
import net.fabricmc.fabric.api.resource.v1.ResourceLoader;
//?}
import net.minecraft.resources.Identifier;
import net.minecraft.server.packs.PackType;
import net.minecraft.server.packs.resources.ResourceManagerReloadListener;

import java.util.EnumMap;
import java.util.LinkedHashMap;
import java.util.Map;
import java.util.function.BiConsumer;
import java.util.function.Consumer;

public final class ResourceReloaders {
    private static final EnumMap<PackType, ReloaderHolder> HOLDERS = new EnumMap<>(PackType.class);

    public static ReloaderHolder get(PackType type) {
        return HOLDERS.computeIfAbsent(type, ReloaderHolder::new);
    }

    public static class ReloaderHolder {
        private final Map<Identifier, ResourceManagerReloadListener> listeners = new LinkedHashMap<>();
        private final PackType type;

        public ReloaderHolder(PackType type) {
            this.type = type;
        }

        public void register(Identifier id, ResourceManagerReloadListener listener) {
            this.listeners.put(id, listener);
            //? fabric {
            ResourceLoader.get(this.type).registerReloadListener(id, listener);
            //?}
        }

        public void visit(BiConsumer<Identifier, ResourceManagerReloadListener> visitor) {
            this.listeners.forEach(visitor);
        }

        public void visitListener(Consumer<ResourceManagerReloadListener> visitor) {
            this.listeners.values().forEach(visitor);
        }
    }
}
