package com.iafenvoy.saturn.data.action.builtin.entity;

import com.iafenvoy.saturn.data.ability.Ability;
import com.iafenvoy.saturn.data.action.EntityAction;
import com.iafenvoy.saturn.data.context.action.EntityActionContext;
import com.iafenvoy.saturn.data.storage.DataStorage;
import com.iafenvoy.saturn.profiling.LoggerDispatcher;
import com.iafenvoy.saturn.profiling.LoggingLevel;
import com.iafenvoy.saturn.registry.SaturnAttachments;
import com.iafenvoy.saturn.util.HolderHelper;
import com.mojang.logging.LogUtils;
import com.mojang.serialization.MapCodec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import net.minecraft.core.Holder;
import net.minecraft.world.entity.Entity;
import org.jspecify.annotations.NonNull;

public record ModifyAbilityStorageAction(Holder<Ability> ability, DataStorage value) implements EntityAction {
    // Runs wherever the definition that names it runs, which for a pulse type is every pulse.
    private static final LoggerDispatcher LOGGER = LoggerDispatcher.of(LogUtils.getLogger(), LoggingLevel.WARN, LoggingLevel.WARN);

    public static final MapCodec<ModifyAbilityStorageAction> CODEC = RecordCodecBuilder.mapCodec(i -> i.group(
            Ability.CODEC.fieldOf("ability").forGetter(ModifyAbilityStorageAction::ability),
            DataStorage.CODEC.fieldOf("value").forGetter(ModifyAbilityStorageAction::value)
    ).apply(i, ModifyAbilityStorageAction::new));

    @Override
    public void execute(@NonNull EntityActionContext ctx) {
        Entity entity = ctx.entity();
        if (entity.level().isClientSide()) return;
        Ability definition = this.ability.value();
        if (!definition.declares(this.value)) {
            LOGGER.log("{} declares no {}; nothing was written", HolderHelper.id(this.ability), this.value.getClass().getSimpleName());
            return;
        }
        entity.getData(SaturnAttachments.ABILITY_HOLDER).storage().set(this.ability, this.value, entity.level().getGameTime());
    }

    @Override
    public @NonNull MapCodec<ModifyAbilityStorageAction> codec() {
        return CODEC;
    }
}
