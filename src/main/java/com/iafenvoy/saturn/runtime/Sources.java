package com.iafenvoy.saturn.runtime;

import com.iafenvoy.saturn.Saturn;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.resources.Identifier;
import net.minecraft.world.entity.EquipmentSlot;
import net.minecraft.world.item.ItemStack;

/**
 * How a source id the framework itself produces is spelled. Grants are counted per source, so the shape lives in
 * one place rather than in whoever happens to be granting.
 *
 * <p>The two categories below are open: {@link Grant} and {@link Declaration} are records with a path, and a
 * dependent mod creates its own the same way Saturn creates its own. The constants Saturn ships are the ones its own
 * runtime uses; anything else is the content's business.
 */
public final class Sources {
    // Keep the persisted id while treating extension inventories as generic equipment.
    public static final Identifier EQUIPPED = Identifier.fromNamespaceAndPath(Saturn.MOD_ID, "curios_equipment");
    private static final String EQUIPMENT_PREFIX = "equipment/";
    private static final String GRANT_PREFIX = "grant/";

    private Sources() {
    }

    /**
     * The category a framework grant is counted under: {@code saturn:grant/<category>/<namespace>/<path>}.
     */
    public record Grant(String path) {
    }

    /**
     * The category a generated passive modifier's id names: which sort of thing declared it. It is part of a stored
     * modifier id, so a new category is a new kind of declaration rather than a rename.
     */
    public record Declaration(String path) {
        public static final Declaration ABILITY = new Declaration("ability");
    }

    // An empty slot names air.
    public static Identifier equipment(EquipmentSlot slot, ItemStack stack) {
        Identifier item = stack.isEmpty() ? Identifier.fromNamespaceAndPath("minecraft", "air")
                : BuiltInRegistries.ITEM.getKey(stack.getItem());
        return Identifier.fromNamespaceAndPath(Saturn.MOD_ID,
                EQUIPMENT_PREFIX + slot.getName() + "/" + item.getNamespace() + "/" + item.getPath());
    }

    public static Identifier granted(Grant kind, Identifier content) {
        return Identifier.fromNamespaceAndPath(Saturn.MOD_ID,
                GRANT_PREFIX + kind.path() + "/" + content.getNamespace() + "/" + content.getPath());
    }

    // Held in a hand, so the grant leaves with the item; a charm in a body slot has its own source and counts as
    // part of the body instead.
    public static boolean isEquipment(Identifier source) {
        return inMod(source) && source.getPath().startsWith(EQUIPMENT_PREFIX);
    }

    // Granted by a definition the body holds, as opposed to by an item it happens to carry.
    public static boolean isGranted(Identifier source) {
        return inMod(source) && source.getPath().startsWith(GRANT_PREFIX);
    }

    private static boolean inMod(Identifier source) {
        return source.getNamespace().equals(Saturn.MOD_ID);
    }
}
