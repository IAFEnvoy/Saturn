package com.iafenvoy.saturn.data.action.builtin.entity.meta;

import com.iafenvoy.saturn.data.action.EntityAction;
import com.iafenvoy.saturn.data.condition.EntityCondition;
import com.iafenvoy.saturn.data.context.action.EntityActionContext;
import com.iafenvoy.saturn.data.context.condition.EntityConditionContext;
import com.mojang.serialization.Codec;
import com.mojang.serialization.MapCodec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import org.jspecify.annotations.NonNull;

import java.util.List;

public record IfElseListAction(List<Entry> actions) implements EntityAction {
    public static final MapCodec<IfElseListAction> CODEC = RecordCodecBuilder.mapCodec(i -> i.group(
            Entry.CODEC.listOf().fieldOf("actions").forGetter(IfElseListAction::actions)
    ).apply(i, IfElseListAction::new));

    public IfElseListAction {
        actions = List.copyOf(actions);
    }

    @Override
    public void execute(@NonNull EntityActionContext ctx) {
        for (Entry entry : this.actions) {
            if (entry.condition.test(ctx.copyTo(new EntityConditionContext(ctx.entity(), ctx.formula())))) {
                entry.action.execute(ctx);
                break;
            }
        }
    }

    @Override
    public @NonNull MapCodec<IfElseListAction> codec() {
        return CODEC;
    }

    public record Entry(EntityCondition condition, EntityAction action) {
        public static final Codec<Entry> CODEC = RecordCodecBuilder.create(i -> i.group(
                EntityCondition.CODEC.fieldOf("condition").forGetter(Entry::condition),
                EntityAction.CODEC.fieldOf("action").forGetter(Entry::action)
        ).apply(i, Entry::new));
    }
}
