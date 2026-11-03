package com.iafenvoy.saturn.util.codec;

import io.netty.buffer.ByteBuf;
import it.unimi.dsi.fastutil.booleans.BooleanArrayList;
import it.unimi.dsi.fastutil.booleans.BooleanList;
import it.unimi.dsi.fastutil.longs.LongArrayList;
import it.unimi.dsi.fastutil.longs.LongList;
import net.minecraft.network.codec.ByteBufCodecs;
import net.minecraft.network.codec.StreamCodec;

import java.util.HashSet;
import java.util.List;
import java.util.Set;

public final class MiscStreamCodecs {
    public static <T extends Enum<T>> StreamCodec<ByteBuf, T> enumCodec(Class<T> clazz) {
        if (!clazz.isEnum()) throw new IllegalArgumentException("Class must be an enum");
        return ByteBufCodecs.idMapper(x -> clazz.getEnumConstants()[x], Enum::ordinal);
    }

    public static <B extends ByteBuf, V> StreamCodec.CodecOperation<B, V, Set<V>> set() {
        return set(Integer.MAX_VALUE);
    }

    public static <B extends ByteBuf, V> StreamCodec.CodecOperation<B, V, Set<V>> set(int maxSize) {
        return codec -> ByteBufCodecs.collection(HashSet::new, codec, maxSize);
    }

    // The bounds are what a decoder is allowed to allocate, not a promise about the data.
    public static StreamCodec<ByteBuf, List<LongList>> longMatrix(int maxRows, int maxColumns) {
        return ByteBufCodecs.VAR_LONG.apply(ByteBufCodecs.list(maxColumns))
                .<LongList>map(LongArrayList::new, row -> row.longStream().boxed().toList())
                .apply(ByteBufCodecs.list(maxRows));
    }

    public static StreamCodec<ByteBuf, BooleanList> booleans(int maxSize) {
        return ByteBufCodecs.BOOL.apply(ByteBufCodecs.list(maxSize)).map(BooleanArrayList::new, values -> values);
    }
}
