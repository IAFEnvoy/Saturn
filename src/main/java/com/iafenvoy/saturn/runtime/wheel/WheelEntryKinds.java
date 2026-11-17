package com.iafenvoy.saturn.runtime.wheel;

import com.iafenvoy.saturn.Saturn;
import com.iafenvoy.saturn.api.wheel.WheelEntryKind;
import com.iafenvoy.saturn.runtime.wheel.builtin.AbilityKind;
import com.iafenvoy.saturn.runtime.wheel.builtin.EmptyKind;
import com.mojang.serialization.Codec;
import io.netty.buffer.ByteBuf;
import net.minecraft.network.codec.ByteBufCodecs;
import net.minecraft.network.codec.StreamCodec;
import net.minecraft.resources.Identifier;

import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;

public final class WheelEntryKinds {
    public static final WheelEntryKind EMPTY = new EmptyKind();
    public static final WheelEntryKind ABILITY = new AbilityKind();
    public static final List<WheelEntryKind> BUILT_IN = List.of(EMPTY, ABILITY);
    public static final Codec<WheelEntryKind> CODEC = Codec.STRING.xmap(WheelEntryKinds::parse, kind -> kind.id().toString());
    public static final StreamCodec<ByteBuf, WheelEntryKind> STREAM_CODEC = ByteBufCodecs.STRING_UTF8.map(WheelEntryKinds::parse, kind -> kind.id().toString());
    private static final Map<Identifier, WheelEntryKind> REGISTERED = new LinkedHashMap<>();

    static {
        for (WheelEntryKind kind : BUILT_IN) register(kind);
    }

    public static synchronized void register(WheelEntryKind kind) {
        REGISTERED.putIfAbsent(kind.id(), kind);
    }

    public static synchronized Optional<WheelEntryKind> byId(Identifier id) {
        return Optional.ofNullable(REGISTERED.get(id));
    }

    public static synchronized List<WheelEntryKind> known() {
        return List.copyOf(REGISTERED.values());
    }

    public static WheelEntryKind parse(String value) {
        Identifier id = Identifier.tryParse(value.indexOf(':') >= 0 ? value : Saturn.MOD_ID + ":" + value);
        return id == null ? EMPTY : byId(id).orElse(EMPTY);
    }
}
