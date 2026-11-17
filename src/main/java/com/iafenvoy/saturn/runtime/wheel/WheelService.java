package com.iafenvoy.saturn.runtime.wheel;

import com.iafenvoy.saturn.api.wheel.WheelEntryKind;
import com.iafenvoy.saturn.api.wheel.WheelSource;
import com.iafenvoy.saturn.data.ability.Ability;
import com.iafenvoy.saturn.registry.SaturnAttachments;
import com.iafenvoy.saturn.registry.SaturnDatapackRegistries;
import com.iafenvoy.saturn.registry.SaturnResourceKeys;
import com.iafenvoy.saturn.runtime.ability.AbilityActivationService;
import com.iafenvoy.saturn.runtime.resulting.Result;
import com.iafenvoy.saturn.runtime.resulting.failures.AbilityFailure;
import com.iafenvoy.saturn.util.OptionalBoolean;
import net.minecraft.core.Holder;
import net.minecraft.network.chat.Component;
import net.minecraft.resources.Identifier;
import net.minecraft.server.level.ServerPlayer;
import org.jspecify.annotations.Nullable;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.OptionalInt;

public final class WheelService {
    public static final int MAX_ARMED = 4096;

    private WheelService() {
    }

    public static WheelLayout sanitize(ServerPlayer player, @Nullable WheelLayout submitted) {
        if (submitted == null) return WheelLayout.EMPTY;
        List<WheelSlot> slots = new ArrayList<>(WheelLayout.SLOTS);
        for (WheelSlot slot : submitted.slots())
            slots.add(!slot.isEmpty() && slot.kind().exists(player.level().registryAccess(), slot.id()) ? slot : WheelSlot.EMPTY);
        return new WheelLayout(slots);
    }

    public static OptionalInt sanitizeArmed(@Nullable Integer submitted) {
        return submitted == null || submitted < 0 || submitted > MAX_ARMED ? OptionalInt.empty() : OptionalInt.of(submitted);
    }

    public static boolean trigger(ServerPlayer player, @Nullable WheelSource source, @Nullable WheelEntryKind kind, @Nullable Identifier id) {
        return trigger(player, source, kind, id, Optional.empty());
    }

    public static boolean trigger(ServerPlayer player, @Nullable WheelSource source, @Nullable WheelEntryKind kind,
                                  @Nullable Identifier id, Optional<Boolean> enabled) {
        if (source == null || kind == null || id == null) return false;
        if (!kind.exists(player.level().registryAccess(), id) || !WheelSources.offers(player, source, kind, id)) {
            player.sendSystemMessage(Component.translatable("actionbar.saturn.wheel.stale_entry"), true);
            return false;
        }
        return enabled.map(aBoolean -> kind.directed(player, source, id, aBoolean)).orElseGet(() -> kind.trigger(player, source, id));
    }

    public static boolean press(ServerPlayer player, WheelSource source, Identifier id) {
        Holder<Ability> ability = SaturnDatapackRegistries.holder(player.registryAccess(), SaturnResourceKeys.ABILITY, id).orElse(null);
        if (!AbilityActivationService.togglable(ability)) return refuse(player, AbilityFailure.UNAVAILABLE.message());
        return activate(player, source, ability);
    }

    public static boolean directed(ServerPlayer player, WheelSource source, Identifier id, boolean wanted) {
        Holder<Ability> ability = SaturnDatapackRegistries.holder(player.registryAccess(), SaturnResourceKeys.ABILITY, id).orElse(null);
        if (!AbilityActivationService.togglable(ability) || !player.getData(SaturnAttachments.ABILITY_HOLDER).has(ability))
            return refuse(player, AbilityFailure.NOT_GRANTED.message());
        OptionalBoolean state = AbilityActivationService.state(player, ability);
        if (state.isEmpty()) return refuse(player, AbilityFailure.UNAVAILABLE.message());
        return state.getAsBoolean() == wanted || activate(player, source, ability);
    }

    private static boolean activate(ServerPlayer player, WheelSource source, Holder<Ability> ability) {
        if (!player.getData(SaturnAttachments.ABILITY_HOLDER).has(ability))
            return refuse(player, AbilityFailure.NOT_GRANTED.message());
        Result<Void> result = AbilityActivationService.activate(player, ability, WheelSources.carrier(player, source, ability).orElse(null));
        if (result instanceof Result.SuccessResult<?>) return true;
        return refuse(player, result.getFailure().orElseThrow().message());
    }

    private static boolean refuse(ServerPlayer player, Component reason) {
        player.sendSystemMessage(Component.translatable("actionbar.saturn.wheel.use_failed", reason), true);
        return false;
    }
}
