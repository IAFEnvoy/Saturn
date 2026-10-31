package com.iafenvoy.saturn.screen.resourcebar;

import com.iafenvoy.saturn.data.resource.ResourceBar.Anchor;
import com.iafenvoy.saturn.data.resourcebar.ResourceBarContext.Layout;
import net.minecraft.world.entity.LivingEntity;

import java.util.List;

/**
 * Bars a dependent mod answers with itself, for the pools that are not a {@code saturn:resource}: the framework
 * gathers them once per column per frame and orders them with the resource-declared ones, so a contributed bar sorts,
 * stacks and anchors exactly like a declared bar.
 *
 * <p>The columns and rows hand over the layout they are filling and the side they are on, so one contributor can
 * answer for the self HUD only, or for a target row only.
 */
public interface ResourceBarContributor {
    /**
     * Adds this mod's bars for {@code entity}; leaving the list untouched is a complete answer.
     */
    void collect(LivingEntity entity, Layout layout, Anchor anchor, List<ResourceBarRenderState> bars);
}
