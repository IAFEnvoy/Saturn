package com.iafenvoy.saturn.data.action.builtin.bientity.meta;

import com.iafenvoy.saturn.data.action.BiEntityAction;
import com.iafenvoy.saturn.data.condition.BiEntityCondition;
import com.iafenvoy.saturn.data.context.action.BiEntityActionContext;
import com.iafenvoy.saturn.data.context.condition.BiEntityConditionContext;
import com.mojang.serialization.Codec;
import com.mojang.serialization.MapCodec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import org.jspecify.annotations.NonNull;

import java.util.List;

public record IfElseListAction(List<Entry> actions) implements BiEntityAction {
    public static final MapCodec<IfElseListAction> CODEC = RecordCodecBuilder.mapCodec(i -> i.group(
            Entry.CODEC.listOf().fieldOf("actions").forGetter(IfElseListAction::actions)
    ).apply(i, IfElseListAction::new));

    public IfElseListAction {
        actions = List.copyOf(actions);
    }

    @Override
    public void execute(@NonNull BiEntityActionContext ctx) {
        for (Entry entry : this.actions) {
            if (entry.condition.test(ctx.copyTo(new BiEntityConditionContext(ctx.actor(), ctx.target(), ctx.formula())))) {
                entry.action.execute(ctx);
                break;
            }
        }
    }

    @Override
    public @NonNull MapCodec<IfElseListAction> codec() {
        return CODEC;
    }

    public record Entry(BiEntityCondition condition, BiEntityAction action) {
        public static final Codec<Entry> CODEC = RecordCodecBuilder.create(i -> i.group(
                BiEntityCondition.CODEC.fieldOf("condition").forGetter(Entry::condition),
                BiEntityAction.CODEC.fieldOf("action").forGetter(Entry::action)
        ).apply(i, Entry::new));
    }
}
