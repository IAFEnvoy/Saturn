package com.iafenvoy.saturn.util.codec;

import com.mojang.serialization.Codec;
import it.unimi.dsi.fastutil.booleans.BooleanArrayList;
import it.unimi.dsi.fastutil.booleans.BooleanList;
import it.unimi.dsi.fastutil.ints.Int2ObjectMap;
import it.unimi.dsi.fastutil.ints.Int2ObjectOpenHashMap;
import it.unimi.dsi.fastutil.longs.LongArrayList;
import it.unimi.dsi.fastutil.longs.LongList;
import it.unimi.dsi.fastutil.objects.*;

import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.function.Function;

/**
 * Codec adapters for the immutable collections produced by datapack codecs.
 */
public final class CollectionCodecs {
    public static <K, V> Codec<Map<K, V>> map(Codec<K> keyCodec, Codec<V> valueCodec) {
        return TolerantMapCodec.create(keyCodec, valueCodec);
    }

    public static <T> Codec<List<T>> list(Codec<T> elementCodec) {
        return TolerantListCodec.create(elementCodec);
    }

    public static <T> Codec<Set<T>> set(Codec<T> elementCodec) {
        return elementCodec.listOf().xmap(Set::copyOf, List::copyOf);
    }

    public static <K> Codec<Object2DoubleMap<K>> doubleMap(Codec<K> keyCodec) {
        return map(keyCodec, Codec.DOUBLE).xmap(Object2DoubleOpenHashMap::new, Function.identity());
    }

    public static <K> Codec<Object2IntMap<K>> intMap(Codec<K> keyCodec) {
        return map(keyCodec, Codec.INT).xmap(Object2IntOpenHashMap::new, Function.identity());
    }

    public static <K> Codec<Object2LongMap<K>> longMap(Codec<K> keyCodec) {
        return map(keyCodec, Codec.LONG).xmap(Object2LongOpenHashMap::new, Function.identity());
    }

    public static <V> Codec<Int2ObjectMap<V>> intObjectMap(Codec<V> valueCodec) {
        return map(Codec.STRING.comapFlatMap(MiscCodecs::parseInt, String::valueOf), valueCodec).xmap(Int2ObjectOpenHashMap::new, Function.identity());
    }

    public static Codec<List<LongList>> longMatrix(int maxRows, int maxColumns) {
        return Codec.LONG.sizeLimitedListOf(maxColumns).sizeLimitedListOf(maxRows).xmap(
                rows -> rows.stream().<LongList>map(LongArrayList::new).toList(),
                matrix -> matrix.stream().map(row -> row.longStream().boxed().toList()).toList());
    }

    public static Codec<BooleanList> booleans(int maxSize) {
        return Codec.BOOL.sizeLimitedListOf(maxSize).xmap(BooleanArrayList::new, values -> values);
    }
}
