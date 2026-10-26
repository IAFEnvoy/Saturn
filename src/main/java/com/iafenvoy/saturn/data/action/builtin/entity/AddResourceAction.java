package com.iafenvoy.saturn.data.action.builtin.entity;

import com.iafenvoy.saturn.data.action.EntityAction;
import com.iafenvoy.saturn.data.context.action.EntityActionContext;
import com.iafenvoy.saturn.data.resource.Resource;
import com.iafenvoy.saturn.registry.SaturnAttachments;
import com.iafenvoy.saturn.runtime.resource.ResourceService;
import com.iafenvoy.saturn.util.HolderHelper;
import com.iafenvoy.saturn.util.formula.FormulaContext;
import com.iafenvoy.saturn.util.formula.NumberProvider;
import com.mojang.serialization.MapCodec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import net.minecraft.core.Holder;
import net.minecraft.resources.Identifier;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.LivingEntity;
import org.jspecify.annotations.NonNull;

/**
 * Adds a finite signed amount to a server-owned resource attachment.
 */
public record AddResourceAction(Holder<Resource> resource,
                                NumberProvider amount) implements EntityAction {
    public static final MapCodec<AddResourceAction> CODEC = RecordCodecBuilder.mapCodec(i -> i.group(
            Resource.CODEC.fieldOf("resource").forGetter(AddResourceAction::resource),
            NumberProvider.CODEC.fieldOf("amount").forGetter(AddResourceAction::amount)
    ).apply(i, AddResourceAction::new));

    @Override
    public void execute(@NonNull EntityActionContext ctx) {
        Entity entity = ctx.entity();
        FormulaContext context = ctx.formula();
        double amount = this.amount.evaluate(context);
        if (!Double.isFinite(amount)) return;
        Identifier id = HolderHelper.id(this.resource);
        FormulaContext resourceContext = entity instanceof LivingEntity living
                ? ResourceService.formulaContext(living, id, context)
                : context;
        ResourceService.change(entity.getData(SaturnAttachments.RESOURCE_HOLDER), id, amount, resourceContext);
    }

    @Override
    public @NonNull MapCodec<AddResourceAction> codec() {
        return CODEC;
    }
}
