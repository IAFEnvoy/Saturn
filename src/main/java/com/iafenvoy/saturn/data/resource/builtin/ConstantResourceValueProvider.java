package com.iafenvoy.saturn.data.resource.builtin;

import com.iafenvoy.saturn.attachment.ResourceHolderAttachment;
import com.iafenvoy.saturn.data.resource.Resource;
import com.iafenvoy.saturn.data.resource.ResourceValueProvider;
import com.iafenvoy.saturn.util.formula.FormulaContext;
import com.iafenvoy.saturn.util.formula.NumberProvider;
import com.mojang.serialization.MapCodec;
import net.minecraft.core.Holder;

public record ConstantResourceValueProvider(NumberProvider value) implements ResourceValueProvider {
    public static final MapCodec<ConstantResourceValueProvider> CODEC = NumberProvider.CODEC.fieldOf("value")
            .xmap(ConstantResourceValueProvider::new, ConstantResourceValueProvider::value);

    @Override
    public double resolve(ResourceHolderAttachment holder, Holder<Resource> resource, FormulaContext context) {
        return this.value.evaluate(context);
    }

    @Override
    public MapCodec<ConstantResourceValueProvider> codec() {
        return CODEC;
    }
}
