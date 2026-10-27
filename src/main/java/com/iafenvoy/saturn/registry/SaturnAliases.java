package com.iafenvoy.saturn.registry;

import com.iafenvoy.saturn.Saturn;
import com.iafenvoy.saturn.api.AliasManager;
import com.iafenvoy.saturn.data.resource.Resource;
import com.iafenvoy.saturn.data.resourcebar.ResourceBarContext;
import com.mojang.serialization.MapCodec;
import net.minecraft.core.Holder;
import net.minecraft.core.Registry;
import net.minecraft.network.chat.Component;
import net.minecraft.resources.Identifier;
import net.minecraft.world.entity.LivingEntity;
import net.neoforged.bus.api.EventPriority;
import net.neoforged.bus.api.SubscribeEvent;
import net.neoforged.fml.common.EventBusSubscriber;
import net.neoforged.fml.event.lifecycle.FMLCommonSetupEvent;
import net.neoforged.neoforge.registries.DeferredHolder;
import net.neoforged.neoforge.registries.DeferredRegister;
import net.neoforged.neoforge.registries.RegisterEvent;

import java.util.*;

/**
 * The old ids of the built-in entries, so data written before the split keeps resolving.
 *
 * <p>A content mod adds its own namespace from its constructor, before the registration event fires:
 *
 * <pre>{@code
 * public MyMod(IEventBus bus) {
 *     SaturnAliases.add("mxt");                      // mxt:<path> becomes a second id of saturn:<path>
 *     SaturnAliases.set((namespace, id) -> ...);     // or a custom AliasManager when a path changed too
 * }
 * }</pre>
 *
 * <p>{@link RegisterEvent} is handled at {@link EventPriority#LOWEST} so every register of ours has already added
 * its entries and bound its holders. For each entry the old id is then registered a second time with the same
 * value. The value is wrapped first: a registry throws {@code Adding duplicate value} when handed the same object
 * under two ids, so a codec is re-created through {@code xmap} with identity functions, and the resource-bar context
 * registry gets a delegating instance.
 *
 * <p>The formula function and formula variable registries are deliberately **not** aliased: both are resolved by
 * name (a function by {@code Function#getName}, a variable by its {@code names} and {@code prefixes}), so a second
 * id adds no lookup path - it only puts two entries with the same name into one registry, which the formula index
 * refuses. Old data that writes {@code mxt:round} or {@code mxt:caster_} needs no alias, because those names never
 * went through a registry id in the first place.
 *
 * <p>An id the registry already holds is left alone, which is what keeps a content mod's own entries and a second
 * namespace's aliases from colliding. Attachments are deliberately not aliased: an attachment id is stored per
 * entity, so a second id would be a second attachment rather than a renamed one.
 */
@EventBusSubscriber
public final class SaturnAliases {
    private static final Set<String> NAMESPACES = new LinkedHashSet<>();
    private static volatile AliasManager manager = AliasManager.SAME_PATH;
    private static volatile boolean registered;

    private SaturnAliases() {
    }

    public static void set(AliasManager aliasManager) {
        manager = aliasManager == null ? AliasManager.SAME_PATH : aliasManager;
    }

    /**
     * Adds the aliases of one content namespace. Call it from the mod constructor: the ids are registered during
     * the registration event, which is already over by the time setup runs.
     */
    public static void add(String namespace) {
        if (namespace == null || namespace.isBlank() || namespace.equals(Saturn.MOD_ID)) return;
        if (registered)
            Saturn.LOGGER.warn("Saturn aliases for {} were added after the registration event and are ignored", namespace);
        NAMESPACES.add(namespace);
    }

    @SubscribeEvent(priority = EventPriority.LOWEST)
    public static void onRegister(RegisterEvent event) {
        Set<String> namespaces = Set.copyOf(NAMESPACES);
        if (namespaces.isEmpty()) return;
        for (DeferredRegister<?> register : registers()) alias(event, register, namespaces);
    }

    // Setup is after every registration event, so a namespace added from here on would never be aliased.
    @SubscribeEvent
    public static void onSetup(FMLCommonSetupEvent event) {
        registered = true;
    }

    private static <T> void alias(RegisterEvent event, DeferredRegister<T> register, Set<String> namespaces) {
        if (!register.getRegistryKey().equals(event.getRegistryKey())) return;
        // Both are resolved by name, not by registry id, so a second id adds no lookup path and would put two
        // entries with one name into the registry.
        if (register.getRegistryKey().equals(SaturnResourceKeys.FORMULA_FUNCTION)
                || register.getRegistryKey().equals(SaturnResourceKeys.FORMULA_VARIABLE)) return;
        Registry<T> registry = event.getRegistry(register.getRegistryKey());
        AliasManager aliasManager = manager;
        if (registry == null) return;
        for (DeferredHolder<T, ? extends T> entry : register.getEntries()) {
            Identifier id = entry.getId();
            for (String namespace : namespaces) {
                Optional<Identifier> oldId = aliasManager.alias(namespace, id);
                if (oldId.isEmpty() || oldId.get().equals(id)) continue;
                // A registry cannot take the same key twice, and a content mod may still own that id itself.
                if (registry.containsKey(oldId.get())) continue;
                // One wrapper per id: a registry also rejects one value registered under two keys.
                T aliased = wrap(entry.get());
                event.register(register.getRegistryKey(), oldId.get(), () -> aliased);
            }
        }
    }

    // Distinct object, same behaviour: a registry rejects one value registered under two ids.
    @SuppressWarnings("unchecked")
    private static <T> T wrap(T value) {
        if (value instanceof MapCodec<?> codec)
            return (T) codec.xmap(java.util.function.Function.identity(), java.util.function.Function.identity());
        if (value instanceof ResourceBarContext context) return (T) new AliasedContext(context);
        return value;
    }

    // Everything data-pack-facing: the built-in vocabulary registries plus the two vanilla loot registries. The
    // attachment registry is on purpose not in this list.
    private static List<DeferredRegister<?>> registers() {
        List<DeferredRegister<?>> registers = new ArrayList<>(SaturnContent.registers());
        registers.add(SaturnLootRegistries.CONDITIONS);
        registers.add(SaturnLootRegistries.FUNCTIONS);
        return registers;
    }

    private record AliasedContext(ResourceBarContext delegate) implements ResourceBarContext {
        @Override
        public Optional<Values> extract(LivingEntity entity, Holder<Resource> resource) {
            return this.delegate.extract(entity, resource);
        }

        @Override
        public Component name(Holder<Resource> resource) {
            return this.delegate.name(resource);
        }

        @Override
        public Layout layout() {
            return this.delegate.layout();
        }
    }
}
