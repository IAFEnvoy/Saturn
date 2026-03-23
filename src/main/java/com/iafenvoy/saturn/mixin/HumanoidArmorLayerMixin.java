package com.iafenvoy.saturn.mixin;

import com.iafenvoy.saturn.render.ArmorRenderer;
import com.mojang.blaze3d.vertex.PoseStack;
import net.minecraft.client.model.HumanoidModel;
import net.minecraft.client.renderer.MultiBufferSource;
import net.minecraft.client.renderer.entity.RenderLayerParent;
import net.minecraft.client.renderer.entity.layers.HumanoidArmorLayer;
import net.minecraft.client.renderer.entity.layers.RenderLayer;
import net.minecraft.world.entity.EquipmentSlot;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.item.ItemStack;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(HumanoidArmorLayer.class)
public abstract class HumanoidArmorLayerMixin<T extends LivingEntity, M extends HumanoidModel<T>, A extends HumanoidModel<T>> extends RenderLayer<T, M> {
    public HumanoidArmorLayerMixin(RenderLayerParent<T, M> context) {
        super(context);
    }

    @Inject(method = /*? neoforge {*/"renderArmorPiece(Lcom/mojang/blaze3d/vertex/PoseStack;Lnet/minecraft/client/renderer/MultiBufferSource;Lnet/minecraft/world/entity/LivingEntity;Lnet/minecraft/world/entity/EquipmentSlot;ILnet/minecraft/client/model/HumanoidModel;FFFFFF)V"/*?} else {*//*"renderArmorPiece"*//*?}*/, at = @At("HEAD"), cancellable = true)
    private void onRenderArmor(PoseStack poseStack, MultiBufferSource bufferSource, LivingEntity livingEntity, EquipmentSlot slot, int packedLight, A model, /*? neoforge {*/float limbSwing, float limbSwingAmount, float partialTick, float ageInTicks, float netHeadYaw, float headPitch, /*?}*/CallbackInfo ci) {
        ItemStack stack = livingEntity.getItemBySlot(slot);
        @SuppressWarnings("unchecked")
        ArmorRenderer<T> renderer = (ArmorRenderer<T>) ArmorRenderer.RENDERERS.get(stack.getItem());
        if (renderer != null) {
            renderer.render(poseStack, bufferSource, livingEntity, slot, packedLight, stack, this.getParentModel());
            ci.cancel();
        }
    }
}
