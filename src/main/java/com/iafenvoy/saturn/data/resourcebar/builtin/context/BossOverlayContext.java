package com.iafenvoy.saturn.data.resourcebar.builtin.context;

import com.iafenvoy.saturn.data.resource.Resource;
import com.iafenvoy.saturn.data.resourcebar.ResourceBarContext;
import com.iafenvoy.saturn.util.DefinitionText;
import net.minecraft.core.Holder;
import net.minecraft.network.chat.Component;
import net.minecraft.world.entity.LivingEntity;

import java.util.Optional;

public enum BossOverlayContext implements ResourceBarContext {
    INSTANCE;

    @Override
    public Optional<Values> extract(LivingEntity entity, Holder<Resource> resource) {
        return ResourceBarContext.extractStored(entity, resource);
    }

    @Override
    public Component name(Holder<Resource> resource) {
        return Component.translatable("hud.saturn.resource_bar.boss_overlay", DefinitionText.nameOrId(resource));
    }

    @Override
    public Layout layout() {
        return Layout.BOSS_OVERLAY;
    }

}
