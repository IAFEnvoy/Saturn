package com.iafenvoy.saturn.profiling;

import com.mojang.serialization.Codec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import net.minecraft.network.RegistryFriendlyByteBuf;
import net.minecraft.network.codec.ByteBufCodecs;
import net.minecraft.network.codec.StreamCodec;

public record ProfilerFrame(long sequence, boolean enabled, ProfilerWindowSnapshot window) {
    public static final ProfilerFrame EMPTY = new ProfilerFrame(0L, false, ProfilerWindowSnapshot.EMPTY);
    public static final Codec<ProfilerFrame> CODEC = RecordCodecBuilder.create(instance -> instance.group(
            Codec.LONG.fieldOf("sequence").forGetter(ProfilerFrame::sequence),
            Codec.BOOL.fieldOf("enabled").forGetter(ProfilerFrame::enabled),
            ProfilerWindowSnapshot.CODEC.fieldOf("window").forGetter(ProfilerFrame::window)
    ).apply(instance, ProfilerFrame::new));
    public static final StreamCodec<RegistryFriendlyByteBuf, ProfilerFrame> STREAM_CODEC = StreamCodec.composite(
            ByteBufCodecs.VAR_LONG, ProfilerFrame::sequence,
            ByteBufCodecs.BOOL, ProfilerFrame::enabled,
            ProfilerWindowSnapshot.STREAM_CODEC, ProfilerFrame::window,
            ProfilerFrame::new);
}
