package com.iafenvoy.saturn.view.model;

import com.mojang.serialization.Codec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import net.minecraft.core.HolderLookup;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.nbt.NbtOps;
import net.minecraft.network.chat.Component;
import net.minecraft.network.chat.ComponentSerialization;
import net.minecraft.resources.Identifier;
import net.minecraft.world.item.ItemStack;

import java.util.Optional;

public record PlayerViewItem(Identifier id, int count, Component name, CompoundTag data) {
    public static final Codec<PlayerViewItem> CODEC = RecordCodecBuilder.create(i -> i.group(
            Identifier.CODEC.fieldOf("id").forGetter(PlayerViewItem::id),
            Codec.intRange(0, Integer.MAX_VALUE).fieldOf("count").forGetter(PlayerViewItem::count),
            ComponentSerialization.CODEC.fieldOf("name").forGetter(PlayerViewItem::name),
            CompoundTag.CODEC.fieldOf("data").forGetter(PlayerViewItem::data)
    ).apply(i, PlayerViewItem::new));

    public PlayerViewItem {
        if (count < 0) throw new IllegalArgumentException("Negative item count");
        name = ViewComponents.copy(name);
        data = data.copy();
    }

    @Override
    public Component name() {
        return ViewComponents.copy(this.name);
    }

    @Override
    public CompoundTag data() {
        return this.data.copy();
    }

    public Optional<ItemStack> stack(HolderLookup.Provider registries) {
        return ItemStack.CODEC.parse(registries.createSerializationContext(NbtOps.INSTANCE), this.data.copy()).result();
    }

    public static PlayerViewItem capture(ItemStack stack, HolderLookup.Provider registries) {
        CompoundTag data = (CompoundTag) ItemStack.CODEC.encodeStart(registries.createSerializationContext(NbtOps.INSTANCE), stack).getOrThrow();
        return new PlayerViewItem(Identifier.parse(data.getString("id").orElseThrow()), stack.getCount(), stack.getHoverName(), data);
    }

    public static Optional<PlayerViewItem> saved(CompoundTag data, HolderLookup.Provider registries) {
        Optional<Identifier> id = data.getString("id").map(Identifier::tryParse);
        if (id.isEmpty()) return Optional.empty();
        Optional<Component> customName = data.getCompound("components").map(components -> components.get("minecraft:custom_name"))
                .flatMap(raw -> ComponentSerialization.CODEC.parse(registries.createSerializationContext(NbtOps.INSTANCE), raw).result());
        return Optional.of(new PlayerViewItem(id.get(), data.getInt("count").orElseGet(() -> data.getInt("Count").orElse(1)),
                customName.orElseGet(() -> Component.literal(id.get().toString())), data));
    }
}
