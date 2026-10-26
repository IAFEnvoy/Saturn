package com.iafenvoy.saturn.data.resource.builtin;

import com.iafenvoy.saturn.attachment.ResourceHolderAttachment;
import com.iafenvoy.saturn.data.resource.Resource;
import com.iafenvoy.saturn.data.resource.ResourceValueProvider;
import com.iafenvoy.saturn.util.formula.FormulaContext;
import com.mojang.serialization.MapCodec;
import net.minecraft.core.Holder;

public enum CurrentResourceValueProvider implements ResourceValueProvider {
    INSTANCE;

    public static final MapCodec<CurrentResourceValueProvider> CODEC = MapCodec.unit(INSTANCE);

    @Override
    public double resolve(ResourceHolderAttachment holder, Holder<Resource> resource, FormulaContext context) {
        return holder.get(resource);
    }

    @Override
    public MapCodec<CurrentResourceValueProvider> codec() {
        return CODEC;
    }
}
