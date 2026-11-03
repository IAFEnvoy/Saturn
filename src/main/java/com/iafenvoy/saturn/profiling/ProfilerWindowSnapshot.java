package com.iafenvoy.saturn.profiling;

import com.iafenvoy.saturn.registry.SaturnRegistries;
import com.iafenvoy.saturn.registry.SaturnResourceKeys;
import com.iafenvoy.saturn.util.codec.CollectionCodecs;
import com.iafenvoy.saturn.util.codec.MiscStreamCodecs;
import com.mojang.serialization.Codec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import it.unimi.dsi.fastutil.booleans.BooleanList;
import it.unimi.dsi.fastutil.longs.LongList;
import net.minecraft.network.RegistryFriendlyByteBuf;
import net.minecraft.network.codec.ByteBufCodecs;
import net.minecraft.network.codec.StreamCodec;

import java.util.Arrays;
import java.util.List;

// Instances own their lists; accessors expose scalar values and immutable metadata only.
public record ProfilerWindowSnapshot(long generation, long version, List<ProfilerSource> sources,
                                     List<Producer> producers, long droppedBatches) {
    public static final int MAX_PRODUCERS = 4;
    public static final ProfilerWindowSnapshot EMPTY = new ProfilerWindowSnapshot(0L, 0L, List.of(), List.of(), 0L);
    public static final Codec<ProfilerWindowSnapshot> CODEC = RecordCodecBuilder.create(instance -> instance.group(
            Codec.LONG.fieldOf("generation").forGetter(ProfilerWindowSnapshot::generation),
            Codec.LONG.fieldOf("version").forGetter(ProfilerWindowSnapshot::version),
            SaturnRegistries.PROFILER_SOURCE.byNameCodec().sizeLimitedListOf(ProfilerSource.MAX_SOURCES).fieldOf("sources").forGetter(ProfilerWindowSnapshot::sources),
            Producer.CODEC.sizeLimitedListOf(MAX_PRODUCERS).fieldOf("producers").forGetter(ProfilerWindowSnapshot::producers),
            Codec.LONG.fieldOf("dropped_batches").forGetter(ProfilerWindowSnapshot::droppedBatches)
    ).apply(instance, ProfilerWindowSnapshot::new));
    public static final StreamCodec<RegistryFriendlyByteBuf, ProfilerWindowSnapshot> STREAM_CODEC = StreamCodec.composite(
            ByteBufCodecs.VAR_LONG, ProfilerWindowSnapshot::generation,
            ByteBufCodecs.VAR_LONG, ProfilerWindowSnapshot::version,
            ByteBufCodecs.registry(SaturnResourceKeys.PROFILER_SOURCE).apply(ByteBufCodecs.list(ProfilerSource.MAX_SOURCES)), ProfilerWindowSnapshot::sources,
            Producer.STREAM_CODEC.apply(ByteBufCodecs.list(MAX_PRODUCERS)), ProfilerWindowSnapshot::producers,
            ByteBufCodecs.VAR_LONG, ProfilerWindowSnapshot::droppedBatches,
            ProfilerWindowSnapshot::new);

    public ProfilerWindowSnapshot(long generation, long version, List<ProfilerSource> sources, List<Producer> producers, long droppedBatches) {
        if (sources.stream().distinct().count() != sources.size())
            throw new IllegalArgumentException("Duplicate profiler source");
        this.generation = generation;
        this.version = version;
        this.sources = List.copyOf(sources);
        this.producers = List.copyOf(producers);
        this.droppedBatches = droppedBatches;
    }

    public record Producer(long id, String name, long firstTick, List<ProfilerSource> sources, List<LongList> samples,
                           BooleanList complete, long discardedSpans) {
        private static final int MAX_COLUMNS = ProfilerSource.MAX_SOURCES * 2;
        public static final Codec<Producer> CODEC = RecordCodecBuilder.create(instance -> instance.group(
                Codec.LONG.fieldOf("id").forGetter(Producer::id),
                Codec.STRING.fieldOf("name").forGetter(Producer::name),
                Codec.LONG.fieldOf("first_tick").forGetter(Producer::firstTick),
                SaturnRegistries.PROFILER_SOURCE.byNameCodec().sizeLimitedListOf(ProfilerSource.MAX_SOURCES).fieldOf("sources").forGetter(Producer::sources),
                CollectionCodecs.longMatrix(Profiler.WINDOW_TICKS, MAX_COLUMNS).fieldOf("samples").forGetter(Producer::samples),
                CollectionCodecs.booleans(Profiler.WINDOW_TICKS).fieldOf("complete").forGetter(Producer::complete),
                Codec.LONG.fieldOf("discarded_spans").forGetter(Producer::discardedSpans)
        ).apply(instance, Producer::new));
        public static final StreamCodec<RegistryFriendlyByteBuf, Producer> STREAM_CODEC = StreamCodec.composite(
                ByteBufCodecs.VAR_LONG, Producer::id,
                ByteBufCodecs.STRING_UTF8, Producer::name,
                ByteBufCodecs.VAR_LONG, Producer::firstTick,
                ByteBufCodecs.registry(SaturnResourceKeys.PROFILER_SOURCE).apply(ByteBufCodecs.list(ProfilerSource.MAX_SOURCES)), Producer::sources,
                MiscStreamCodecs.longMatrix(Profiler.WINDOW_TICKS, MAX_COLUMNS), Producer::samples,
                MiscStreamCodecs.booleans(Profiler.WINDOW_TICKS), Producer::complete,
                ByteBufCodecs.VAR_LONG, Producer::discardedSpans,
                Producer::new);

        // The rows arrive from a decoder as well as from the aggregator, so the shape a reader relies on is checked
        // once here instead of at every access.
        public Producer {
            samples = List.copyOf(samples);
            if (samples.size() != complete.size() || samples.size() > Profiler.WINDOW_TICKS)
                throw new IllegalArgumentException("Invalid profiler window");
            for (LongList row : samples)
                if (row.size() != sources.size() * 2 || row.longStream().anyMatch(value -> value < 0L))
                    throw new IllegalArgumentException("Invalid profiler samples");
        }

        public int sampleCount() {
            return this.samples.size();
        }

        public boolean complete(int sample) {
            return this.complete.getBoolean(sample);
        }

        public long nanos(int sample, ProfilerSource source) {
            return this.samples.get(sample).getLong(this.index(source) * 2);
        }

        public long hits(int sample, ProfilerSource source) {
            return this.samples.get(sample).getLong(this.index(source) * 2 + 1);
        }

        public Summary summary(ProfilerSource source) {
            return this.summarize(this.index(source) * 2, null);
        }

        private Summary summarize(int offset, long[] values) {
            int count = 0;
            long min = Long.MAX_VALUE, max = 0L;
            long hits = 0L;
            double total = 0D;
            for (int index = 0; index < this.samples.size(); index++) {
                if (!this.complete.getBoolean(index)) continue;
                LongList row = this.samples.get(index);
                long value = row.getLong(offset);
                if (values != null) values[count] = value;
                count++;
                min = Math.min(min, value);
                max = Math.max(max, value);
                total += value;
                hits += row.getLong(offset + 1);
            }
            return new Summary(count, count == 0 ? 0L : min, count == 0 ? 0D : total / count, max, hits);
        }

        // Include idle zero ticks; exclude missing or partial ticks. Percentiles use nearest ranks.
        public Statistics statistics(ProfilerSource source) {
            long[] values = new long[this.samples.size()];
            Summary summary = this.summarize(this.index(source) * 2, values);
            int count = summary.samples;
            if (count == 0) return new Statistics(0, 0L, 0D, 0L, 0L, 0L, 0L, 0L);
            Arrays.sort(values, 0, count);
            return new Statistics(count, summary.minNanos, summary.averageNanos, summary.maxNanos,
                    percentile(values, count, 0.50D), percentile(values, count, 0.95D),
                    percentile(values, count, 0.99D), summary.hits);
        }

        private int index(ProfilerSource source) {
            if (source.index() >= this.sources.size() || this.sources.get(source.index()) != source)
                throw new IllegalArgumentException("Source is not part of this snapshot");
            return source.index();
        }

        private static long percentile(long[] values, int count, double fraction) {
            return values[(int) Math.ceil(count * fraction) - 1];
        }
    }

    public record Statistics(int samples, long minNanos, double averageNanos, long maxNanos,
                             long medianNanos, long p95Nanos, long p99Nanos, long hits) {
    }

    public record Summary(int samples, long minNanos, double averageNanos, long maxNanos, long hits) {
    }
}
