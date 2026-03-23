package com.iafenvoy.saturn.registry.deferred;

import com.mojang.datafixers.util.Either;
import net.minecraft.core.Holder;
import net.minecraft.core.HolderOwner;
import net.minecraft.core.Registry;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.resources.ResourceKey;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.tags.TagKey;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.Locale;
import java.util.Objects;
import java.util.Optional;
import java.util.function.Predicate;
import java.util.function.Supplier;
import java.util.stream.Stream;

@SuppressWarnings("unused")
public class DeferredHolder<R, T extends R> implements Holder<R>, Supplier<T> {
    protected final ResourceKey<R> key;
    private @Nullable Holder<R> holder = null;

    public static <R, T extends R> DeferredHolder<R, T> create(ResourceKey<? extends Registry<R>> registryKey, ResourceLocation valueName) {
        return create(ResourceKey.create(registryKey, valueName));
    }

    public static <R, T extends R> DeferredHolder<R, T> create(ResourceLocation registryName, ResourceLocation valueName) {
        return create(ResourceKey.createRegistryKey(registryName), valueName);
    }

    public static <R, T extends R> DeferredHolder<R, T> create(ResourceKey<R> key) {
        return new DeferredHolder<>(key);
    }

    protected DeferredHolder(ResourceKey<R> key) {
        this.key = Objects.requireNonNull(key);
        this.bind(false);
    }

    @SuppressWarnings("unchecked")
    @Override
    public @NotNull T value() {
        this.bind(true);
        if (this.holder == null) throw new NullPointerException("Trying to access unbound value: " + this.key);
        else return (T) this.holder.value();
    }

    @Override
    public T get() {
        return this.value();
    }

    public Optional<T> asOptional() {
        return this.isBound() ? Optional.of(this.value()) : Optional.empty();
    }

    @SuppressWarnings("unchecked")
    protected @Nullable Registry<R> getRegistry() {
        return (Registry<R>) BuiltInRegistries.REGISTRY.get(this.key.registry());
    }

    protected final void bind(boolean throwOnMissingRegistry) {
        if (this.holder == null) {
            Registry<R> registry = this.getRegistry();
            if (registry != null) this.holder = registry.getHolder(this.key).orElse(null);
            else if (throwOnMissingRegistry) {
                String var10002 = String.valueOf(this);
                throw new IllegalStateException("Registry not present for " + var10002 + ": " + this.key.registry());
            }

        }
    }

    public ResourceLocation getId() {
        return this.key.location();
    }

    @Override
    public ResourceKey<R> getKey() {
        return this.key;
    }

    @Override
    public boolean equals(Object obj) {
        if (this == obj) return true;
        else if (obj instanceof Holder<?> h)
            return h.kind() == Kind.REFERENCE && h.unwrapKey().map(x -> x.equals(this.key)).orElse(false);
        return false;
    }

    @Override
    public int hashCode() {
        return this.key.hashCode();
    }

    @Override
    public String toString() {
        return String.format(Locale.ENGLISH, "DeferredHolder{%s}", this.key);
    }

    @Override
    public boolean isBound() {
        this.bind(false);
        return this.holder != null && this.holder.isBound();
    }

    @Override
    public boolean is(ResourceLocation id) {
        return id.equals(this.key.location());
    }

    @Override
    public boolean is(@NotNull ResourceKey<R> key) {
        return key == this.key;
    }

    @Override
    public boolean is(Predicate<ResourceKey<R>> filter) {
        return filter.test(this.key);
    }

    @Override
    public boolean is(@NotNull TagKey<R> tag) {
        this.bind(false);
        return this.holder != null && this.holder.is(tag);
    }

    @Override
    @Deprecated
    public boolean is(@NotNull Holder<R> holder) {
        this.bind(false);
        return this.holder != null && this.holder.is(holder);
    }

    @Override
    public @NotNull Stream<TagKey<R>> tags() {
        this.bind(false);
        return this.holder != null ? this.holder.tags() : Stream.empty();
    }

    @Override
    public @NotNull Either<ResourceKey<R>, R> unwrap() {
        return Either.left(this.key);
    }

    @Override
    public @NotNull Optional<ResourceKey<R>> unwrapKey() {
        return Optional.of(this.key);
    }

    @Override
    public Holder.@NotNull Kind kind() {
        return Kind.REFERENCE;
    }

    @Override
    public boolean canSerializeIn(HolderOwner<R> owner) {
        this.bind(false);
        return this.holder != null && this.holder.canSerializeIn(owner);
    }
}
