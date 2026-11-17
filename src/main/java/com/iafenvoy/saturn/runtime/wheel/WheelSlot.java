package com.iafenvoy.saturn.runtime.wheel;

import com.iafenvoy.saturn.Saturn;
import com.iafenvoy.saturn.api.wheel.WheelEntryKind;
import com.mojang.serialization.Codec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import net.minecraft.network.RegistryFriendlyByteBuf;
import net.minecraft.network.codec.ByteBufCodecs;
import net.minecraft.network.codec.StreamCodec;
import net.minecraft.resources.Identifier;
import org.jspecify.annotations.NonNull;

import java.util.List;

public record WheelSlot(WheelEntryKind kind, Identifier id) {
    // Never looked up, and the server forces it onto empty slots.
    public static final Identifier EMPTY_ID = Identifier.fromNamespaceAndPath(Saturn.MOD_ID, "empty");
    public static final WheelSlot EMPTY = new WheelSlot(WheelEntryKinds.EMPTY, EMPTY_ID);
    public static final Codec<WheelSlot> CODEC = RecordCodecBuilder.create(i -> i.group(
            WheelEntryKinds.CODEC.fieldOf("kind").forGetter(WheelSlot::kind),
            Identifier.CODEC.fieldOf("id").forGetter(WheelSlot::id)
    ).apply(i, WheelSlot::new));
    public static final StreamCodec<RegistryFriendlyByteBuf, WheelSlot> STREAM_CODEC = StreamCodec.composite(
            WheelEntryKinds.STREAM_CODEC, WheelSlot::kind,
            Identifier.STREAM_CODEC, WheelSlot::id,
            WheelSlot::new);
    public static final Codec<List<WheelSlot>> LIST_CODEC = CODEC.listOf();
    public static final StreamCodec<RegistryFriendlyByteBuf, List<WheelSlot>> LIST_STREAM_CODEC =
            STREAM_CODEC.apply(ByteBufCodecs.list());

    public static WheelSlot of(WheelEntryKind kind, Identifier id) {
        return kind.holdsEntry() && id != null ? new WheelSlot(kind, id) : EMPTY;
    }

    public boolean isEmpty() {
        return !this.kind.holdsEntry();
    }

    @Override
    public boolean equals(Object other) {
        return other instanceof WheelSlot(
                WheelEntryKind kind1, Identifier id1
        ) && this.kind.is(kind1) && this.id.equals(id1);
    }

    @Override
    public int hashCode() {
        return 31 * this.kind.id().hashCode() + this.id.hashCode();
    }

    @Override
    public @NonNull String toString() {
        return "WheelSlot[" + this.kind.id() + " " + this.id + "]";
    }
}
