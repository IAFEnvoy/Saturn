package com.iafenvoy.saturn.mixin;

import com.iafenvoy.saturn.runtime.hold.ItemUseCycles;
import com.llamalad7.mixinextras.injector.wrapmethod.WrapMethod;
import com.llamalad7.mixinextras.injector.wrapoperation.Operation;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.InteractionResult;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.Level;
import org.spongepowered.asm.mixin.Mixin;

/**
 * The click half of a use cycle. It wraps the stack rather than {@code Item#use}, because a stack whose item
 * overrides {@code use} - or overrides it and never calls {@code super} - would otherwise never reach this: every
 * click passes through the stack, whatever the item answers.
 */
@Mixin(ItemStack.class)
public abstract class ItemStackUseCycleMixin {
    @WrapMethod(method = "use")
    private InteractionResult saturn$startCycle(Level level, Player player, InteractionHand hand, Operation<InteractionResult> original) {
        return ItemUseCycles.use(player, hand, () -> original.call(level, player, hand));
    }
}
