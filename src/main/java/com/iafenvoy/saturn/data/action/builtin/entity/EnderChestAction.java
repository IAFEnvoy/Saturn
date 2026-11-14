package com.iafenvoy.saturn.data.action.builtin.entity;

import com.iafenvoy.saturn.data.action.EntityAction;
import com.iafenvoy.saturn.data.context.action.EntityActionContext;
import com.mojang.serialization.MapCodec;
import net.minecraft.network.chat.Component;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.stats.Stats;
import net.minecraft.world.SimpleMenuProvider;
import net.minecraft.world.inventory.ChestMenu;
import org.jspecify.annotations.NonNull;

public enum EnderChestAction implements EntityAction {
    INSTANCE;
    public static final MapCodec<EnderChestAction> CODEC = MapCodec.unit(INSTANCE);

    @Override
    public void execute(@NonNull EntityActionContext ctx) {
        if (ctx.entity() instanceof ServerPlayer player) {
            player.openMenu(new SimpleMenuProvider((syncId, inventory, owner) -> ChestMenu.threeRows(syncId, inventory, owner.getEnderChestInventory()), Component.translatable("container.enderchest")));
            player.awardStat(Stats.OPEN_ENDERCHEST);
        }
    }

    @Override
    public @NonNull MapCodec<EnderChestAction> codec() {
        return CODEC;
    }
}
