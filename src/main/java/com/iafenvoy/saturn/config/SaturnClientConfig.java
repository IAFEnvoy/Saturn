package com.iafenvoy.saturn.config;

/**
 * The client settings the resource-bar renderers read.
 *
 * <p>Saturn keeps its client settings as plain values rather than driving a configuration library: a dependent mod owns
 * the screens and the file format its players edit, and only hands the value in. Replace the defaults here at client
 * setup, or wrap this holder from your own config.
 */
public final class SaturnClientConfig {
    public static final SaturnClientConfig INSTANCE = new SaturnClientConfig();

    public final ResourceBars resourceBars = new ResourceBars();

    private SaturnClientConfig() {
    }

    /**
     * Where the two icon slots of a bar go: on both sides of the fill, or in the middle of it.
     */
    public enum ResourceBarIconLayout {
        SIDES,
        CENTER
    }

    public static final class ResourceBars {
        public final Value<Boolean> showNames = new Value<>(true);
        public final Value<ResourceBarIconLayout> iconLayout = new Value<>(ResourceBarIconLayout.SIDES);

        private ResourceBars() {
        }
    }

    /**
     * One settable value. A dependent mod's config screen is what writes it.
     */
    public static final class Value<T> {
        private volatile T value;

        public Value(T value) {
            this.value = value;
        }

        public T getValue() {
            return this.value;
        }

        public void setValue(T value) {
            this.value = value;
        }
    }
}
