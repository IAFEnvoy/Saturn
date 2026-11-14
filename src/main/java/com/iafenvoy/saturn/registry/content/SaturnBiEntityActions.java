package com.iafenvoy.saturn.registry.content;

import com.iafenvoy.saturn.Saturn;
import com.iafenvoy.saturn.compat.kubejs.type.action.JsBiEntityAction;
import com.iafenvoy.saturn.data.action.BiEntityAction;
import com.iafenvoy.saturn.data.action.NoOpAction;
import com.iafenvoy.saturn.data.action.SimpleActions;
import com.iafenvoy.saturn.data.action.builtin.SendMessageAction;
import com.iafenvoy.saturn.data.action.builtin.bientity.*;
import com.iafenvoy.saturn.data.action.builtin.bientity.meta.*;
import com.iafenvoy.saturn.registry.SaturnRegistries;
import com.mojang.serialization.MapCodec;
import net.minecraft.world.entity.TamableAnimal;
import net.minecraft.world.entity.animal.Animal;
import net.minecraft.world.entity.player.Player;
import net.neoforged.fml.ModList;
import net.neoforged.neoforge.registries.DeferredRegister;

/**
 * The built-in {@code bi_entity_action_type} entries.
 */
public final class SaturnBiEntityActions {
    public static final DeferredRegister<MapCodec<? extends BiEntityAction>> REGISTRY = DeferredRegister.create(SaturnRegistries.BI_ENTITY_ACTION_TYPE, Saturn.MOD_ID);

    static {
        REGISTRY.register("tame", () -> SimpleActions.createBiEntity(ctx -> {
            if (!ctx.target().level().isClientSide() && ctx.actor() instanceof Player player && ctx.target() instanceof TamableAnimal animal)
                animal.tame(player);
        }));
        REGISTRY.register("set_in_love", () -> SimpleActions.createBiEntity(ctx -> {
            if (!ctx.target().level().isClientSide() && ctx.target() instanceof Animal animal)
                animal.setInLove(ctx.actor() instanceof Player player ? player : null);
        }));
        REGISTRY.register("for_range", () -> ForRangeAction.CODEC);
        REGISTRY.register("if_else_list", () -> IfElseListAction.CODEC);
        REGISTRY.register("side", () -> SideAction.CODEC);
        REGISTRY.register("while", () -> WhileAction.CODEC);
        REGISTRY.register("invert", () -> InvertAction.CODEC);
        REGISTRY.register("damage_target", () -> DamageTargetAction.CODEC);
        REGISTRY.register("no_op", () -> NoOpAction.CODEC);
        REGISTRY.register("sequence", () -> SequenceBiEntityAction.CODEC);
        REGISTRY.register("chance", () -> ChanceAction.CODEC);
        REGISTRY.register("if_else", () -> IfElseAction.CODEC);
        REGISTRY.register("choice", () -> ChoiceAction.CODEC);
        REGISTRY.register("actor_action", () -> ActorAction.CODEC);
        REGISTRY.register("target_action", () -> TargetAction.CODEC);
        REGISTRY.register("send_message", () -> SendMessageAction.CODEC);
        // KubeJS is compile-only: without the mod present the bridge classes must not be touched.
        if (ModList.get().isLoaded("kubejs")) REGISTRY.register("js", () -> JsBiEntityAction.CODEC);
        REGISTRY.register("mount", () -> SimpleActions.createBiEntity(ctx -> ctx.actor().startRiding(ctx.target())));
        REGISTRY.register("heal_target", () -> HealTargetBiEntityAction.CODEC);
        REGISTRY.register("transfer_resource", () -> TransferResourceBiEntityAction.CODEC);
        REGISTRY.register("add_velocity", () -> AddVelocityAction.CODEC);
        REGISTRY.register("teleport", () -> TeleportAction.CODEC);
    }

    private SaturnBiEntityActions() {
    }
}
