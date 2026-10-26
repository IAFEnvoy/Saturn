package com.iafenvoy.saturn.data.cost.builtin;

import com.iafenvoy.saturn.data.cost.Cost;
import com.iafenvoy.saturn.data.cost.CostTransaction;
import com.iafenvoy.saturn.data.cost.context.CostContext;
import com.iafenvoy.saturn.data.resource.Resource;
import com.iafenvoy.saturn.runtime.resulting.ResultWithData;
import com.iafenvoy.saturn.runtime.resulting.failures.CostFailure;
import com.iafenvoy.saturn.util.formula.NumberProvider;
import com.mojang.serialization.MapCodec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import net.minecraft.core.Holder;

/**
 * Takes an amount of one datapack resource from the payer's own account; the write itself is {@link ResourceChannel}.
 */
public record ResourceCost(Holder<Resource> resource, NumberProvider amount) implements Cost {
    public static final MapCodec<ResourceCost> CODEC = RecordCodecBuilder.mapCodec(i -> i.group(
            Resource.CODEC.fieldOf("resource").forGetter(ResourceCost::resource),
            NumberProvider.CODEC.fieldOf("amount").forGetter(ResourceCost::amount)
    ).apply(i, ResourceCost::new));

    @Override
    public ResultWithData<Transaction> prepare(CostContext context) {
        double value = ResourceChannel.evaluate(context, this.resource(), this.amount());
        if (value <= 0.0D) return ResultWithData.failure(null, CostFailure.INVALID_AMOUNT);
        CostTransaction transaction = ResourceChannel.transaction(context, this.resource(), value);
        return ResultWithData.success(transaction);
    }

    @Override
    public MapCodec<ResourceCost> codec() {
        return CODEC;
    }
}
