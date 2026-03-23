package com.iafenvoy.saturn.render;

import com.mojang.blaze3d.vertex.PoseStack;
import net.minecraft.client.renderer.MultiBufferSource;
import net.minecraft.world.item.ItemDisplayContext;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.ItemLike;
import net.neoforged.api.distmarker.Dist;
import net.neoforged.api.distmarker.OnlyIn;

import java.util.HashMap;
import java.util.Map;

@FunctionalInterface
@OnlyIn(Dist.CLIENT)
public interface DynamicItemRenderer {
    Map<ItemLike, DynamicItemRenderer> RENDERERS = new HashMap<>();

    void render(ItemStack stack, ItemDisplayContext context, PoseStack poseStack, MultiBufferSource bufferSource, int light, int overlay);
}
