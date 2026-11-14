package com.iafenvoy.saturn.util;

import com.mojang.serialization.Codec;
import net.minecraft.advancements.AdvancementHolder;
import net.minecraft.advancements.AdvancementNode;
import net.minecraft.resources.Identifier;
import net.minecraft.server.ServerAdvancementManager;
import net.minecraft.util.StringRepresentable;
import org.jspecify.annotations.NonNull;

import java.util.LinkedHashSet;
import java.util.List;
import java.util.Locale;
import java.util.Set;

/**
 * Matches the selection modes of vanilla advancement grant/revoke commands.
 */
public enum AdvancementSelection implements StringRepresentable {
    ONLY(false, false),
    THROUGH(true, true),
    FROM(false, true),
    UNTIL(true, false),
    EVERYTHING(false, false);

    public static final Codec<AdvancementSelection> CODEC = StringRepresentable.fromEnum(AdvancementSelection::values);
    private final boolean parents;
    private final boolean children;

    AdvancementSelection(boolean parents, boolean children) {
        this.parents = parents;
        this.children = children;
    }

    public List<AdvancementHolder> select(ServerAdvancementManager manager, Identifier id) {
        if (this == EVERYTHING) return List.copyOf(manager.getAllAdvancements());
        AdvancementHolder holder = manager.get(id);
        if (holder == null) return List.of();
        AdvancementNode node = manager.tree().get(holder);
        if (node == null) return List.of(holder);
        Set<AdvancementHolder> result = new LinkedHashSet<>();
        if (this.parents)
            for (AdvancementNode parent = node.parent(); parent != null; parent = parent.parent())
                result.add(parent.holder());
        result.add(holder);
        if (this.children) addChildren(node, result);
        return List.copyOf(result);
    }

    private static void addChildren(AdvancementNode node, Set<AdvancementHolder> result) {
        for (AdvancementNode child : node.children()) {
            result.add(child.holder());
            addChildren(child, result);
        }
    }

    @Override
    public @NonNull String getSerializedName() {
        return this.name().toLowerCase(Locale.ROOT);
    }
}
