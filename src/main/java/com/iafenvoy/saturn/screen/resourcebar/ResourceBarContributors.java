package com.iafenvoy.saturn.screen.resourcebar;

import com.iafenvoy.saturn.data.resource.ResourceBar.Anchor;
import com.iafenvoy.saturn.data.resourcebar.ResourceBarContext.Layout;
import net.minecraft.world.entity.LivingEntity;

import java.util.List;
import java.util.concurrent.CopyOnWriteArrayList;

/**
 * The contributors to the resource columns and rows, in registration order. A dependent mod registers from its own
 * client setup; nothing here touches a world, so registering early is harmless.
 */
public final class ResourceBarContributors {
    private static final List<ResourceBarContributor> CONTRIBUTORS = new CopyOnWriteArrayList<>();

    private ResourceBarContributors() {
    }

    public static void register(ResourceBarContributor contributor) {
        if (contributor != null) CONTRIBUTORS.add(contributor);
    }

    static void collect(LivingEntity entity, Layout layout, Anchor anchor, List<ResourceBarRenderState> bars) {
        for (ResourceBarContributor contributor : CONTRIBUTORS) contributor.collect(entity, layout, anchor, bars);
    }
}
