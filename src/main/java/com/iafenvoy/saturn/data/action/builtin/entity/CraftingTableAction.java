package com.iafenvoy.saturn.data.action.builtin.entity;

import com.iafenvoy.saturn.data.action.EntityAction;
import com.iafenvoy.saturn.data.context.action.EntityActionContext;
import com.mojang.serialization.MapCodec;
import net.minecraft.network.chat.Component;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.stats.Stats;
import net.minecraft.world.SimpleMenuProvider;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.inventory.ContainerLevelAccess;
import net.minecraft.world.inventory.CraftingMenu;
import org.jspecify.annotations.NonNull;

public enum CraftingTableAction implements EntityAction {
    INSTANCE;
    public static final MapCodec<CraftingTableAction> CODEC = MapCodec.unit(INSTANCE);

    @Override
    public void execute(@NonNull EntityActionContext ctx) {
        if (ctx.entity() instanceof ServerPlayer player) {
            player.openMenu(new SimpleMenuProvider((syncId, inventory, owner) -> new CraftingMenu(syncId, inventory, ContainerLevelAccess.create(owner.level(), owner.blockPosition())) {
                @Override
                public boolean stillValid(@NonNull Player viewer) {
                    return true;
                }
            }, Component.translatable("container.crafting")));
            player.awardStat(Stats.INTERACT_WITH_CRAFTING_TABLE);
        }
    }

    @Override
    public @NonNull MapCodec<CraftingTableAction> codec() {
        return CODEC;
    }
}
