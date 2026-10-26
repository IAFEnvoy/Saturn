package com.iafenvoy.saturn.registry;

import com.iafenvoy.saturn.Saturn;
import com.iafenvoy.saturn.screen.hud.HudManager;
import com.mojang.blaze3d.platform.InputConstants;
import com.mojang.blaze3d.platform.InputConstants.Type;
import net.minecraft.client.KeyMapping;
import net.minecraft.client.KeyMapping.Category;
import net.minecraft.client.Minecraft;
import net.minecraft.resources.Identifier;
import net.neoforged.api.distmarker.Dist;
import net.neoforged.bus.api.SubscribeEvent;
import net.neoforged.fml.common.EventBusSubscriber;
import net.neoforged.neoforge.client.event.ClientTickEvent.Post;
import net.neoforged.neoforge.client.event.RegisterKeyMappingsEvent;
import org.lwjgl.glfw.GLFW;

import java.util.ArrayList;
import java.util.LinkedList;
import java.util.List;
import java.util.function.Consumer;

/**
 * The client key the framework itself owns: the HUD layout editor.
 *
 * <p>The editor is Saturn's own screen, so its mapping is wired here. Every other client key is the dependent mod's:
 * it makes one through a {@link KeyMappingHolder} (registered along with this one) or wraps one of its own with
 * {@link KeyMappingHolder#standalone}, and polls or hangs a callback on it with
 * {@link KeyMappingHolder#onStateChange}. Saturn never sends a packet on its own.
 */
@EventBusSubscriber(Dist.CLIENT)
public final class SaturnKeyMappings {
    private static final Category CATEGORY = new Category(Identifier.fromNamespaceAndPath(Saturn.MOD_ID, "general"));

    public static final KeyMappingHolder HUD_LAYOUT = new KeyMappingHolder("key.saturn.hud_layout", Type.KEYSYM, InputConstants.KEY_RSHIFT, CATEGORY);

    static {
        HUD_LAYOUT.onStateChange(pressed -> {
            if (pressed && Minecraft.getInstance().screen == null) HudManager.openEditor();
        });
    }

    private SaturnKeyMappings() {
    }

    @SubscribeEvent
    public static void register(RegisterKeyMappingsEvent event) {
        event.registerCategory(CATEGORY);
        KeyMappingHolder.HOLDERS.stream().map(KeyMappingHolder::get).forEach(event::register);
    }

    @SubscribeEvent
    public static void tick(Post event) {
        KeyMappingHolder.HOLDERS.forEach(KeyMappingHolder::tick);
    }

    public static final class KeyMappingHolder {
        private static final List<KeyMappingHolder> HOLDERS = new LinkedList<>();
        private final KeyMapping keyBinding;
        private final List<Consumer<Boolean>> callbacks = new ArrayList<>();
        private boolean pressed;

        public KeyMappingHolder(String name, Type type, int value, Category category) {
            this(new KeyMapping(name, type, value, category));
        }

        // The order is what a category sorts by: without it KeyMapping#compareTo uses the translated name, which
        // puts "Slot 10" before "Slot 2".
        public KeyMappingHolder(String name, Type type, int value, Category category, int order) {
            this(new KeyMapping(name, type, value, category, order));
        }

        public KeyMappingHolder(KeyMapping keyBinding) {
            this(keyBinding, true);
        }

        private KeyMappingHolder(KeyMapping keyBinding, boolean register) {
            this.keyBinding = keyBinding;
            if (register) HOLDERS.add(this);
        }

        public static KeyMappingHolder standalone(KeyMapping keyBinding) {
            return new KeyMappingHolder(keyBinding, false);
        }

        public KeyMapping get() {
            return this.keyBinding;
        }

        public boolean isDown() {
            return this.keyBinding.isDown();
        }

        // Straight from the input device: setScreen releases every mapping, and grabMouse's setAll() then
        // re-presses the ones still held, so the mapping's own state lies while a screen is up.
        public boolean isPhysicallyDown() {
            InputConstants.Key bound = this.keyBinding.getKey();
            int value = bound.getValue();
            if (value == InputConstants.UNKNOWN.getValue()) return false;
            Minecraft minecraft = Minecraft.getInstance();
            if (bound.getType() == InputConstants.Type.MOUSE)
                return GLFW.glfwGetMouseButton(minecraft.getWindow().handle(), value) == GLFW.GLFW_PRESS;
            return InputConstants.isKeyDown(minecraft.getWindow(), value);
        }

        public boolean consumeClick() {
            return this.keyBinding.consumeClick();
        }

        public void onStateChange(Consumer<Boolean> callback) {
            this.callbacks.add(callback);
        }

        public void tick() {
            boolean current = this.keyBinding.isDown();
            if (current != this.pressed) {
                this.callbacks.forEach(callback -> callback.accept(current));
                this.pressed = current;
            }
        }
    }
}
