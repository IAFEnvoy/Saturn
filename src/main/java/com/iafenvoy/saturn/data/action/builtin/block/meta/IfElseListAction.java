package com.iafenvoy.saturn.data.action.builtin.block.meta;

import com.iafenvoy.saturn.data.action.BlockAction;
import com.iafenvoy.saturn.data.condition.BlockCondition;
import com.iafenvoy.saturn.data.context.action.BlockActionContext;
import com.iafenvoy.saturn.data.context.condition.BlockConditionContext;
import com.mojang.serialization.Codec;
import com.mojang.serialization.MapCodec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import org.jspecify.annotations.NonNull;

import java.util.List;

public record IfElseListAction(List<Entry> actions) implements BlockAction {
    public static final MapCodec<IfElseListAction> CODEC = RecordCodecBuilder.mapCodec(i -> i.group(
            Entry.CODEC.listOf().fieldOf("actions").forGetter(IfElseListAction::actions)
    ).apply(i, IfElseListAction::new));

    public IfElseListAction {
        actions = List.copyOf(actions);
    }

    @Override
    public void execute(@NonNull BlockActionContext ctx) {
        for (Entry entry : this.actions) {
            if (entry.condition.test(ctx.copyTo(new BlockConditionContext(ctx.level(), ctx.pos(), ctx.formula())))) {
                entry.action.execute(ctx);
                break;
            }
        }
    }

    @Override
    public @NonNull MapCodec<IfElseListAction> codec() {
        return CODEC;
    }

    public record Entry(BlockCondition condition, BlockAction action) {
        public static final Codec<Entry> CODEC = RecordCodecBuilder.create(i -> i.group(
                BlockCondition.CODEC.fieldOf("condition").forGetter(Entry::condition),
                BlockAction.CODEC.fieldOf("action").forGetter(Entry::action)
        ).apply(i, Entry::new));
    }
}
