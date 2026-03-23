package com.iafenvoy.saturn.render;

import com.mojang.blaze3d.vertex.PoseStack;
import com.mojang.blaze3d.vertex.VertexConsumer;
import com.mojang.math.Axis;
import net.minecraft.client.model.HumanoidModel;
import net.minecraft.client.model.Model;
import net.minecraft.client.model.PlayerModel;
import net.minecraft.client.player.AbstractClientPlayer;
import net.minecraft.client.renderer.MultiBufferSource;
import net.minecraft.client.renderer.RenderType;
import net.minecraft.client.renderer.entity.ItemRenderer;
import net.minecraft.client.renderer.texture.OverlayTexture;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.EquipmentSlot;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.ItemLike;
import net.neoforged.api.distmarker.Dist;
import net.neoforged.api.distmarker.OnlyIn;

import java.util.Arrays;
import java.util.HashMap;

@OnlyIn(Dist.CLIENT)
public interface ArmorRenderer<T extends LivingEntity> {
    HashMap<ItemLike, ArmorRenderer<? extends LivingEntity>> RENDERERS = new HashMap<>();

    HumanoidModel<T> getHumanoidArmorModel(LivingEntity living, ItemStack stack, EquipmentSlot slot, HumanoidModel<T> defaultModel);

    ResourceLocation getArmorTexture(ItemStack stack, Entity entity, EquipmentSlot slot);

    default void render(PoseStack matrices, MultiBufferSource vertexConsumers, LivingEntity entity, EquipmentSlot slot, int light, ItemStack stack, HumanoidModel<T> defaultModel) {
        HumanoidModel<T> armorModel = this.getHumanoidArmorModel(entity, stack, slot, defaultModel);
        defaultModel.copyPropertiesTo(armorModel);
        armorModel.head.visible = slot == EquipmentSlot.HEAD;
        armorModel.hat.visible = slot == EquipmentSlot.HEAD;
        armorModel.body.visible = slot == EquipmentSlot.CHEST;
        armorModel.leftArm.visible = slot == EquipmentSlot.CHEST;
        armorModel.rightArm.visible = slot == EquipmentSlot.CHEST;
        armorModel.leftLeg.visible = slot == EquipmentSlot.LEGS || slot == EquipmentSlot.FEET;
        armorModel.rightLeg.visible = slot == EquipmentSlot.LEGS || slot == EquipmentSlot.FEET;
        VertexConsumer consumer = vertexConsumers.getBuffer(RenderType.armorCutoutNoCull(this.getArmorTexture(stack, entity, slot)));
        armorModel.renderToBuffer(matrices, consumer, light, OverlayTexture.NO_OVERLAY, -1);
    }
    static void renderPart(PoseStack matrices, MultiBufferSource vertexConsumers, int light, ItemStack stack, Model model, ResourceLocation texture) {
        VertexConsumer vertexConsumer = ItemRenderer.getArmorFoilBuffer(vertexConsumers, RenderType.armorCutoutNoCull(texture), stack.hasFoil());
        model.renderToBuffer(matrices, vertexConsumer, light, OverlayTexture.NO_OVERLAY, -1);
    }

    //From trinkets
    static void translateToChest(PoseStack matrices, PlayerModel<AbstractClientPlayer> model, AbstractClientPlayer player) {
        if (player.isCrouching() && !model.riding && !player.isSwimming()) {
            matrices.translate(0.0F, 0.2F, 0.0F);
            matrices.mulPose(Axis.XP.rotation(model.body.xRot));
        }
        matrices.mulPose(Axis.YP.rotation(model.body.yRot));
        matrices.translate(0.0F, 0.4F, -0.16F);
    }
}
