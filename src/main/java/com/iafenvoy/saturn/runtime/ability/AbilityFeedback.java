package com.iafenvoy.saturn.runtime.ability;

import com.iafenvoy.saturn.Saturn;
import com.iafenvoy.saturn.runtime.resulting.Failure;
import net.minecraft.network.chat.Component;
import net.minecraft.resources.Identifier;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.entity.Entity;
import org.jetbrains.annotations.Nullable;

/**
 * The one table a refusal is read out of, whichever path refused: a press, a cast, a talisman and a cast that failed
 * on its last tick all name the same reason the pipeline told apart, so no caller invents its own wording.
 */
public final class AbilityFeedback {
    private AbilityFeedback() {
    }

    // A cast that only fails once its time is up has no press left to answer to, so the actor is told directly.
    public static void reportFailedCast(Entity actor, @Nullable Failure failure,
                                        @Nullable Identifier failedResource) {
        if (failure == null || !(actor instanceof ServerPlayer player)) return;
        Saturn.LOGGER.info("Refusing the finished cast for {}: {}{}", player.getGameProfile().name(), failure.name(),
                failedResource == null ? "" : " (" + failedResource + ")");
        player.sendSystemMessage(Component.translatable("actionbar.saturn.ability.cast_failed",
                failure.message(failedResource)), true);
    }
}
