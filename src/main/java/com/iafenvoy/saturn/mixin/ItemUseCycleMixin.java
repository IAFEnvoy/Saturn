package com.iafenvoy.saturn.mixin;

import com.iafenvoy.saturn.runtime.hold.ItemUseCycles;
import com.llamalad7.mixinextras.injector.ModifyReturnValue;
import com.llamalad7.mixinextras.injector.wrapmethod.WrapMethod;
import com.llamalad7.mixinextras.injector.wrapoperation.Operation;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.ItemUseAnimation;
import net.minecraft.world.level.Level;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;

/**
 * The part of a use cycle vanilla asks the item for once the gesture is live. The click itself is wrapped on the
 * stack ({@link ItemStackUseCycleMixin}), because an item may override {@code use} and never reach this class.
 */
@Mixin(Item.class)
public abstract class ItemUseCycleMixin {
    @ModifyReturnValue(method = "getUseDuration", at = @At("RETURN"))
    private int saturn$cycleDuration(int original, ItemStack itemStack, LivingEntity user) {
        return ItemUseCycles.duration(user, itemStack, original);
    }

    @ModifyReturnValue(method = "getUseAnimation", at = @At("RETURN"))
    private ItemUseAnimation saturn$cycleAnimation(ItemUseAnimation original, ItemStack itemStack) {
        return ItemUseCycles.animation(itemStack, original);
    }

    @WrapMethod(method = "finishUsingItem")
    private ItemStack saturn$finishCycle(ItemStack itemStack, Level level, LivingEntity entity, Operation<ItemStack> original) {
        return ItemUseCycles.finish(level, entity, itemStack, () -> original.call(itemStack, level, entity));
    }
}
