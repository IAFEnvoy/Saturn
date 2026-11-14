package com.iafenvoy.saturn.data.action.builtin.item.meta;

import com.iafenvoy.saturn.data.action.ItemAction;
import com.iafenvoy.saturn.data.condition.ItemCondition;
import com.iafenvoy.saturn.data.context.action.ItemActionContext;
import com.iafenvoy.saturn.data.context.condition.ItemConditionContext;
import com.mojang.serialization.Codec;
import com.mojang.serialization.MapCodec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import org.jspecify.annotations.NonNull;

import java.util.List;

public record IfElseListAction(List<Entry> actions) implements ItemAction {
    public static final MapCodec<IfElseListAction> CODEC = RecordCodecBuilder.mapCodec(i -> i.group(
            Entry.CODEC.listOf().fieldOf("actions").forGetter(IfElseListAction::actions)
    ).apply(i, IfElseListAction::new));

    public IfElseListAction {
        actions = List.copyOf(actions);
    }

    @Override
    public void execute(@NonNull ItemActionContext ctx) {
        for (Entry entry : this.actions) {
            if (entry.condition.test(ctx.copyTo(new ItemConditionContext(ctx.holder(), ctx.stack(), ctx.formula())))) {
                entry.action.execute(ctx);
                break;
            }
        }
    }

    @Override
    public @NonNull MapCodec<IfElseListAction> codec() {
        return CODEC;
    }

    public record Entry(ItemCondition condition, ItemAction action) {
        public static final Codec<Entry> CODEC = RecordCodecBuilder.create(i -> i.group(
                ItemCondition.CODEC.fieldOf("condition").forGetter(Entry::condition),
                ItemAction.CODEC.fieldOf("action").forGetter(Entry::action)
        ).apply(i, Entry::new));
    }
}
