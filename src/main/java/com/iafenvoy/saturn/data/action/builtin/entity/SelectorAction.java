package com.iafenvoy.saturn.data.action.builtin.entity;

import com.iafenvoy.saturn.Saturn;
import com.iafenvoy.saturn.data.action.BiEntityAction;
import com.iafenvoy.saturn.data.action.EntityAction;
import com.iafenvoy.saturn.data.condition.BiEntityCondition;
import com.iafenvoy.saturn.data.context.action.EntityActionContext;
import com.iafenvoy.saturn.util.DatapackCommands;
import com.mojang.brigadier.StringReader;
import com.mojang.brigadier.exceptions.CommandSyntaxException;
import com.mojang.serialization.Codec;
import com.mojang.serialization.MapCodec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import net.minecraft.commands.CommandSourceStack;
import net.minecraft.commands.arguments.EntityArgument;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.world.entity.Entity;
import org.jspecify.annotations.NonNull;

public record SelectorAction(String selector, BiEntityAction biEntityAction,
                             BiEntityCondition biEntityCondition) implements EntityAction {
    public static final MapCodec<SelectorAction> CODEC = RecordCodecBuilder.mapCodec(i -> i.group(
            Codec.STRING.fieldOf("selector").forGetter(SelectorAction::selector),
            BiEntityAction.CODEC.fieldOf("bientity_action").forGetter(SelectorAction::biEntityAction),
            BiEntityCondition.optionalCodec("bientity_condition").forGetter(SelectorAction::biEntityCondition)
    ).apply(i, SelectorAction::new));

    @Override
    public void execute(@NonNull EntityActionContext ctx) {
        if (!(ctx.entity().level() instanceof ServerLevel level)) return;
        try {
            CommandSourceStack source = DatapackCommands.source(ctx.entity(), level, ctx.position());
            for (Entity target : EntityArgument.entities().parse(new StringReader(this.selector), source).findEntities(source))
                if (this.biEntityCondition.test(ctx.entity(), target, ctx))
                    this.biEntityAction.execute(ctx.entity(), target, ctx);
        } catch (CommandSyntaxException error) {
            Saturn.LOGGER.warn("Invalid entity selector in saturn:selector_action: {}", this.selector, error);
        }
    }

    @Override
    public @NonNull MapCodec<SelectorAction> codec() {
        return CODEC;
    }
}
