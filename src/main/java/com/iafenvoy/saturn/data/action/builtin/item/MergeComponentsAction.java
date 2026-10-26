package com.iafenvoy.saturn.data.action.builtin.item;

import com.iafenvoy.saturn.data.action.ItemAction;
import com.iafenvoy.saturn.data.context.action.ItemActionContext;
import com.mojang.serialization.MapCodec;
import net.minecraft.core.component.DataComponentPatch;
import org.jspecify.annotations.NonNull;

/**
 * Merges a vanilla data-component patch into the acted item stack.
 */
public record MergeComponentsAction(DataComponentPatch components) implements ItemAction {
    public static final MapCodec<MergeComponentsAction> CODEC = DataComponentPatch.CODEC.fieldOf("components").xmap(MergeComponentsAction::new, MergeComponentsAction::components);

    @Override
    public void execute(@NonNull ItemActionContext ctx) {
        ctx.stack().applyComponents(this.components);
    }

    @Override
    public @NonNull MapCodec<MergeComponentsAction> codec() {
        return CODEC;
    }
}
