package com.iafenvoy.saturn.data.action.builtin.entity;

import com.iafenvoy.saturn.data.action.EntityAction;
import com.iafenvoy.saturn.data.context.action.EntityActionContext;
import com.iafenvoy.saturn.util.AdvancementSelection;
import com.iafenvoy.saturn.util.codec.MiscCodecs;
import com.mojang.serialization.Codec;
import com.mojang.serialization.MapCodec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import net.minecraft.advancements.AdvancementHolder;
import net.minecraft.advancements.AdvancementProgress;
import net.minecraft.resources.Identifier;
import net.minecraft.server.level.ServerPlayer;
import org.jspecify.annotations.NonNull;

import java.util.List;

public record GrantAdvancementAction(Identifier advancement, List<String> criterion,
                                     AdvancementSelection selection) implements EntityAction {
    public static final MapCodec<GrantAdvancementAction> CODEC = RecordCodecBuilder.mapCodec(i -> i.group(
            Identifier.CODEC.fieldOf("advancement").forGetter(GrantAdvancementAction::advancement),
            MiscCodecs.combineCodec(Codec.STRING).lenientOptionalFieldOf("criterion", List.of()).forGetter(GrantAdvancementAction::criterion),
            AdvancementSelection.CODEC.lenientOptionalFieldOf("selection", AdvancementSelection.ONLY).forGetter(GrantAdvancementAction::selection)
    ).apply(i, GrantAdvancementAction::new));

    public GrantAdvancementAction {
        criterion = List.copyOf(criterion);
    }

    @Override
    public void execute(@NonNull EntityActionContext ctx) {
        if (!(ctx.entity() instanceof ServerPlayer player)) return;
        for (AdvancementHolder holder : this.selection.select(player.level().getServer().getAdvancements(), this.advancement)) {
            if (this.criterion.isEmpty()) {
                AdvancementProgress progress = player.getAdvancements().getOrStartProgress(holder);
                for (String criterion : progress.getRemainingCriteria())
                    player.getAdvancements().award(holder, criterion);
            } else for (String criterion : this.criterion) player.getAdvancements().award(holder, criterion);
        }
    }

    @Override
    public @NonNull MapCodec<GrantAdvancementAction> codec() {
        return CODEC;
    }
}
