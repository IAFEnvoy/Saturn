package com.iafenvoy.saturn.registry.deferred;

import net.minecraft.core.Holder;
import net.minecraft.core.Registry;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.core.registries.Registries;
import net.minecraft.resources.ResourceKey;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.item.BlockItem;
import net.minecraft.world.item.Item;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.state.BlockBehaviour;
import net.minecraft.tags.TagKey;
//? !fabric {
import com.iafenvoy.saturn.bus.SaturnBuses;
import net.neoforged.neoforge.registries.RegisterEvent;
//?}
import org.jetbrains.annotations.Nullable;

import java.util.*;
import java.util.function.Function;
import java.util.function.Supplier;

@SuppressWarnings("unused")
public class DeferredRegistrar<T> {
    private final ResourceKey<? extends Registry<T>> registryKey;
    private final String namespace;
    private final Map<DeferredHolder<T, ? extends T>, Supplier<? extends T>> entries = new LinkedHashMap<>();
    private final Set<DeferredHolder<T, ? extends T>> entriesView;
    private boolean registered = false;

    public static <T> DeferredRegistrar<T> create(Registry<T> registry, String namespace) {
        return new DeferredRegistrar<>(registry.key(), namespace);
    }

    public static <T> DeferredRegistrar<T> create(ResourceKey<? extends Registry<T>> key, String namespace) {
        return new DeferredRegistrar<>(key, namespace);
    }

    public static <B> DeferredRegistrar<B> create(ResourceLocation registryName, String modid) {
        return new DeferredRegistrar<>(ResourceKey.createRegistryKey(registryName), modid);
    }

    public static Items createItems(String modid) {
        return new Items(modid);
    }

    public static Blocks createBlocks(String modid) {
        return new Blocks(modid);
    }

    protected DeferredRegistrar(ResourceKey<? extends Registry<T>> registryKey, String namespace) {
        this.entriesView = Collections.unmodifiableSet(this.entries.keySet());
        this.registryKey = Objects.requireNonNull(registryKey);
        this.namespace = Objects.requireNonNull(namespace);
    }

    public <I extends T> DeferredHolder<T, I> register(String name, Supplier<? extends I> sup) {
        return this.register(name, key -> sup.get());
    }

    public <I extends T> DeferredHolder<T, I> register(String name, Function<ResourceLocation, ? extends I> func) {
        if (this.registered)
            throw new IllegalStateException("Cannot register new entries since this registrar has posted register.");
        else {
            Objects.requireNonNull(name);
            Objects.requireNonNull(func);
            ResourceLocation key = ResourceLocation.fromNamespaceAndPath(this.namespace, name);
            DeferredHolder<T, I> ret = this.createHolder(this.registryKey, key);
            if (this.entries.putIfAbsent(ret, () -> func.apply(key)) != null)
                throw new IllegalArgumentException("Duplicate registration " + name);
            else return ret;
        }
    }

    protected <I extends T> DeferredHolder<T, I> createHolder(ResourceKey<? extends Registry<T>> registryKey, ResourceLocation key) {
        return DeferredHolder.create(registryKey, key);
    }

    public TagKey<T> createTagKey(String path) {
        Objects.requireNonNull(path);
        return this.createTagKey(ResourceLocation.fromNamespaceAndPath(this.namespace, path));
    }

    public TagKey<T> createTagKey(ResourceLocation location) {
        Objects.requireNonNull(location);
        return TagKey.create(this.registryKey, location);
    }

    public void register() {
        //? fabric {
        /*Registry<T> registry = this.getRegistry();
        Objects.requireNonNull(registry);
        for (Map.Entry<DeferredHolder<T, ? extends T>, Supplier<? extends T>> e : this.entries.entrySet()) {
            Registry.register(registry, e.getKey().getId(), e.getValue().get());
            e.getKey().bind(false);
        }
        this.registered = true;
        *///?} else {
        SaturnBuses.findBus(this.namespace).addListener(this::addEntries);
        //?}
    }

    //? !fabric {
    private void addEntries(RegisterEvent event) {
        if (event.getRegistryKey().equals(this.registryKey)) {
            this.registered = true;
            Registry<T> registry = event.getRegistry(this.registryKey);
            Objects.requireNonNull(registry);
            for (Map.Entry<DeferredHolder<T, ? extends T>, Supplier<? extends T>> e : this.entries.entrySet()) {
                event.register(this.registryKey, e.getKey().getId(), () -> e.getValue().get());
                e.getKey().bind(false);
            }
        }
    }
    //?}

    public Collection<DeferredHolder<T, ? extends T>> getEntries() {
        return this.entriesView;
    }

    public ResourceKey<? extends Registry<T>> getRegistryKey() {
        return this.registryKey;
    }

    public ResourceLocation getRegistryName() {
        return this.registryKey.location();
    }

    @SuppressWarnings("unchecked")
    protected @Nullable Registry<T> getRegistry() {
        return (Registry<T>) BuiltInRegistries.REGISTRY.get(this.registryKey.location());
    }

    public String getNamespace() {
        return this.namespace;
    }


    public static class Blocks extends DeferredRegistrar<Block> {
        protected Blocks(String namespace) {
            super(Registries.BLOCK, namespace);
        }

        @SuppressWarnings("unchecked")
        @Override
        public <B extends Block> DeferredBlock<B> register(String name, Function<ResourceLocation, ? extends B> func) {
            return (DeferredBlock<B>) super.register(name, func);
        }

        @Override
        public <B extends Block> DeferredBlock<B> register(String name, Supplier<? extends B> sup) {
            return this.register(name, key -> sup.get());
        }

        public <B extends Block> DeferredBlock<B> registerBlock(String name, Function<BlockBehaviour.Properties, ? extends B> func, BlockBehaviour.Properties props) {
            return this.register(name, () -> func.apply(props));
        }

        public <B extends Block> DeferredBlock<B> registerBlock(String name, Function<BlockBehaviour.Properties, ? extends B> func) {
            return this.registerBlock(name, func, BlockBehaviour.Properties.of());
        }

        public DeferredBlock<Block> registerSimpleBlock(String name, BlockBehaviour.Properties props) {
            return this.registerBlock(name, Block::new, props);
        }

        public DeferredBlock<Block> registerSimpleBlock(String name) {
            return this.registerSimpleBlock(name, BlockBehaviour.Properties.of());
        }

        @Override
        protected <I extends Block> DeferredBlock<I> createHolder(ResourceKey<? extends Registry<Block>> registryKey, ResourceLocation key) {
            return DeferredBlock.createBlock(ResourceKey.create(registryKey, key));
        }
    }

    public static class Items extends DeferredRegistrar<Item> {
        protected Items(String namespace) {
            super(Registries.ITEM, namespace);
        }

        @SuppressWarnings("unchecked")
        @Override
        public <I extends Item> DeferredItem<I> register(String name, Function<ResourceLocation, ? extends I> func) {
            return (DeferredItem<I>) super.register(name, func);
        }

        @Override
        public <I extends Item> DeferredItem<I> register(String name, Supplier<? extends I> sup) {
            return this.register(name, key -> sup.get());
        }

        public DeferredItem<BlockItem> registerSimpleBlockItem(String name, Supplier<? extends Block> block, Item.Properties properties) {
            return this.register(name, key -> new BlockItem(block.get(), properties));
        }

        public DeferredItem<BlockItem> registerSimpleBlockItem(String name, Supplier<? extends Block> block) {
            return this.registerSimpleBlockItem(name, block, new Item.Properties());
        }

        public DeferredItem<BlockItem> registerSimpleBlockItem(Holder<Block> block, Item.Properties properties) {
            String var10001 = block.unwrapKey().orElseThrow().location().getPath();
            Objects.requireNonNull(block);
            return this.registerSimpleBlockItem(var10001, block::value, properties);
        }

        public DeferredItem<BlockItem> registerSimpleBlockItem(Holder<Block> block) {
            return this.registerSimpleBlockItem(block, new Item.Properties());
        }

        public <I extends Item> DeferredItem<I> registerItem(String name, Function<Item.Properties, ? extends I> func, Item.Properties props) {
            return this.register(name, () -> func.apply(props));
        }

        public <I extends Item> DeferredItem<I> registerItem(String name, Function<Item.Properties, ? extends I> func) {
            return this.registerItem(name, func, new Item.Properties());
        }

        public DeferredItem<Item> registerSimpleItem(String name, Item.Properties props) {
            return this.registerItem(name, Item::new, props);
        }

        public DeferredItem<Item> registerSimpleItem(String name) {
            return this.registerItem(name, Item::new, new Item.Properties());
        }

        @Override
        protected <I extends Item> DeferredItem<I> createHolder(ResourceKey<? extends Registry<Item>> registryKey, ResourceLocation key) {
            return DeferredItem.createItem(ResourceKey.create(registryKey, key));
        }
    }
}
