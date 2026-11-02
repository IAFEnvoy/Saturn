package com.iafenvoy.saturn.view;

import com.iafenvoy.saturn.api.playerview.PlayerView;
import com.iafenvoy.saturn.api.playerview.PlayerViewField;
import com.iafenvoy.saturn.api.playerview.PlayerViewWriter;
import com.iafenvoy.saturn.view.model.*;
import com.mojang.datafixers.util.Pair;
import com.mojang.serialization.*;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import net.minecraft.nbt.*;
import net.minecraft.resources.Identifier;
import net.minecraft.resources.RegistryOps;

import java.util.*;
import java.util.function.Predicate;

/** Encoded, detached fields. Unknown envelopes survive a decode/encode cycle without loading their owning mod. */
public final class PlayerViewSnapshot implements PlayerView {
    private static final Codec<Envelope> ENVELOPE_CODEC = RecordCodecBuilder.create(i -> i.group(
            Codec.intRange(1, 1).fieldOf("version").forGetter(Envelope::version),
            PlayerViewMeta.CODEC.fieldOf("meta").forGetter(Envelope::meta),
            Codec.unboundedMap(Identifier.CODEC, Codec.PASSTHROUGH).fieldOf("fields").forGetter(Envelope::fields)
    ).apply(i, Envelope::new));
    public static final Codec<PlayerViewSnapshot> CODEC = new Codec<>() {
        @Override
        public <T> DataResult<Pair<PlayerViewSnapshot, T>> decode(DynamicOps<T> ops, T input) {
            return ENVELOPE_CODEC.decode(ops, input).map(pair -> {
                Envelope envelope = pair.getFirst();
                Map<Identifier, Tag> fields = new LinkedHashMap<>();
                envelope.fields.forEach((id, value) -> fields.put(id, value.convert(NbtOps.INSTANCE).getValue()));
                return Pair.of(new PlayerViewSnapshot(envelope.meta, fields, nbtOps(ops)), pair.getSecond());
            });
        }

        @Override
        public <T> DataResult<T> encode(PlayerViewSnapshot input, DynamicOps<T> ops, T prefix) {
            Map<Identifier, Dynamic<?>> fields = new LinkedHashMap<>();
            input.fields.forEach((id, value) -> fields.put(id, new Dynamic<>(NbtOps.INSTANCE, value.copy())));
            return ENVELOPE_CODEC.encode(new Envelope(1, input.meta, fields), ops, prefix);
        }
    };
    private final PlayerViewMeta meta;
    private final Map<Identifier, Tag> fields;
    private final DynamicOps<Tag> ops;

    private PlayerViewSnapshot(PlayerViewMeta meta, Map<Identifier, Tag> fields, DynamicOps<Tag> ops) {
        this.meta = Objects.requireNonNull(meta);
        Map<Identifier, Tag> copy = new LinkedHashMap<>();
        fields.forEach((id, value) -> copy.put(id, value.copy()));
        this.fields = Collections.unmodifiableMap(copy);
        this.ops = Objects.requireNonNull(ops);
    }

    @Override
    public PlayerViewMeta meta() {
        return this.meta;
    }

    @Override
    public <T> ViewValue<T> read(PlayerViewField<T> field) {
        Tag raw = this.fields.get(field.id());
        if (raw == null) return ViewValue.missing(MissingReason.NOT_CAPTURED);
        if (!(raw instanceof CompoundTag envelope)) return ViewValue.missing(MissingReason.INVALID);
        try {
            if (envelope.getInt("schema").orElse(-1) != field.schemaVersion())
                return ViewValue.missing(MissingReason.UNSUPPORTED);
            String state = envelope.getString("state").orElse("");
            if (state.equals("missing")) return MissingReason.CODEC.parse(this.ops, envelope.get("reason")).result()
                    .map(ViewValue::<T>missing).orElseGet(() -> ViewValue.missing(MissingReason.INVALID));
            if (!state.equals("known") || !envelope.contains("value")) return ViewValue.missing(MissingReason.INVALID);
            Optional<ValueOrigin> origin = ValueOrigin.CODEC.parse(this.ops, envelope.get("origin")).result();
            return origin.map(valueOrigin -> field.codec().parse(this.ops, envelope.get("value").copy()).result()
                    .map(value -> ViewValue.known(value, valueOrigin)).orElseGet(() -> ViewValue.missing(MissingReason.INVALID))).orElseGet(() -> ViewValue.missing(MissingReason.INVALID));
        } catch (RuntimeException ignored) {
            return ViewValue.missing(MissingReason.INVALID);
        }
    }

    public Set<Identifier> fieldIds() {
        return this.fields.keySet();
    }

    /** Filters the actual wire payload, including unknown extensions, before a host sends it to a viewer. */
    public PlayerViewSnapshot filter(Predicate<Identifier> allowed) {
        Map<Identifier, Tag> filtered = new LinkedHashMap<>();
        this.fields.forEach((id, value) -> {
            if (allowed.test(id)) filtered.put(id, value);
        });
        return new PlayerViewSnapshot(this.meta, filtered, this.ops);
    }

    public static Builder builder(PlayerViewMeta meta, DynamicOps<?> ops) {
        return new Builder(meta, nbtOps(ops));
    }

    public Builder toBuilder() {
        Builder builder = new Builder(this.meta, this.ops);
        this.fields.forEach((id, value) -> builder.fields.put(id, value.copy()));
        return builder;
    }

    private static DynamicOps<Tag> nbtOps(DynamicOps<?> ops) {
        return ops instanceof RegistryOps<?> registries ? registries.withParent(NbtOps.INSTANCE) : NbtOps.INSTANCE;
    }

    private record Envelope(int version, PlayerViewMeta meta, Map<Identifier, Dynamic<?>> fields) {
    }

    public static final class Builder implements PlayerViewWriter {
        private final PlayerViewMeta meta;
        private final DynamicOps<Tag> ops;
        private final Map<Identifier, Tag> fields = new LinkedHashMap<>();

        private Builder(PlayerViewMeta meta, DynamicOps<Tag> ops) {
            this.meta = Objects.requireNonNull(meta);
            this.ops = ops;
        }

        @Override
        public <T> void put(PlayerViewField<T> field, T value, ValueOrigin origin) {
            PlayerViewFields.requireRegistered(field);
            CompoundTag envelope = this.envelope(field, "known");
            envelope.put("value", field.codec().encodeStart(this.ops, Objects.requireNonNull(value)).getOrThrow().copy());
            envelope.put("origin", ValueOrigin.CODEC.encodeStart(this.ops, origin).getOrThrow());
            this.fields.put(field.id(), envelope);
        }

        @Override
        public void missing(PlayerViewField<?> field, MissingReason reason) {
            PlayerViewFields.requireRegistered(field);
            CompoundTag envelope = this.envelope(field, "missing");
            envelope.put("reason", MissingReason.CODEC.encodeStart(this.ops, reason).getOrThrow());
            this.fields.put(field.id(), envelope);
        }

        @Override
        public boolean contains(PlayerViewField<?> field) {
            return this.fields.containsKey(field.id());
        }

        public PlayerViewSnapshot build() {
            return new PlayerViewSnapshot(this.meta, this.fields, this.ops);
        }

        private CompoundTag envelope(PlayerViewField<?> field, String state) {
            CompoundTag envelope = new CompoundTag();
            envelope.putInt("schema", field.schemaVersion());
            envelope.putString("state", state);
            return envelope;
        }
    }
}
