package com.iafenvoy.saturn.screen.wheel.content;

import com.iafenvoy.saturn.api.wheel.AbilityWheelTooltip;
import com.iafenvoy.saturn.api.wheel.WheelEntryKind;
import com.iafenvoy.saturn.api.wheel.WheelMenuEntry;
import com.iafenvoy.saturn.attachment.AbilityAttachment;
import com.iafenvoy.saturn.data.IconReference;
import com.iafenvoy.saturn.data.ability.Ability;
import com.iafenvoy.saturn.data.ability.CooldownSource;
import com.iafenvoy.saturn.data.ability.Togglable;
import com.iafenvoy.saturn.data.cost.Cost;
import com.iafenvoy.saturn.data.storage.builtin.CooldownDataStorage;
import com.iafenvoy.saturn.registry.SaturnAttachments;
import com.iafenvoy.saturn.runtime.ability.AbilityStorage;
import com.iafenvoy.saturn.runtime.wheel.WheelEntryKinds;
import com.iafenvoy.saturn.screen.wheel.WheelDuration;
import com.iafenvoy.saturn.util.DefinitionText;
import com.iafenvoy.saturn.util.HolderHelper;
import com.iafenvoy.saturn.util.OptionalBoolean;
import com.iafenvoy.saturn.util.formula.FormulaContext;
import net.minecraft.ChatFormatting;
import net.minecraft.core.Holder;
import net.minecraft.network.chat.Component;
import net.minecraft.resources.Identifier;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import org.jspecify.annotations.Nullable;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.OptionalDouble;

public record AbilityWheelEntry(Holder<Ability> ability, @Nullable ItemStack carrier,
                                OptionalBoolean state) implements WheelMenuEntry {
    private static final int ACCENT_ON = 0xFF7BD37B;
    private static final int ACCENT_OFF = 0xFF8A8F9A;
    private static final int ACCENT_READY = 0xFFB08CE8;
    private static final int ACCENT_PLAIN = 0xFFFFD24A;

    @Override
    public WheelEntryKind kind() {
        return WheelEntryKinds.ABILITY;
    }

    @Override
    public Identifier id() {
        return HolderHelper.id(this.ability);
    }

    @Override
    public Component title() {
        return DefinitionText.nameOrId(this.ability);
    }

    @Override
    public Optional<IconReference> icon() {
        return this.ability.value().icon();
    }

    @Override
    public int accentColor() {
        if (!(this.ability.value().type() instanceof Togglable)) return ACCENT_PLAIN;
        return this.state.isPresent() ? (this.state.getAsBoolean() ? ACCENT_ON : ACCENT_OFF) : ACCENT_READY;
    }

    @Override
    public long cooldownTicks(@Nullable Player player) {
        if (player == null) return 0L;
        AbilityAttachment holder = player.getExistingData(SaturnAttachments.ABILITY_HOLDER).orElse(null);
        if (holder == null) return 0L;
        return AbilityStorage.cooldownRemaining(holder, this.ability, player.level().getGameTime());
    }

    // The length the last use actually got: the declared cooldown field can be overridden by a stored
    // saturn:cooldown, and only the payment knows the number the display has to draw the sheet against.
    @Override
    public long cooldownLength(@Nullable Player player) {
        if (player == null) return 0L;
        AbilityAttachment holder = player.getExistingData(SaturnAttachments.ABILITY_HOLDER).orElse(null);
        if (holder == null) return 0L;
        OptionalDouble duration = AbilityStorage.get(holder, this.ability, CooldownDataStorage.class)
                .map(CooldownDataStorage::duration).orElseGet(OptionalDouble::empty);
        return duration.isPresent() ? Math.round(duration.getAsDouble()) : 0L;
    }

    @Override
    public List<Component> tooltip(@Nullable Player player) {
        List<Component> lines = new ArrayList<>(6);
        lines.add(this.kind().displayName().copy().withStyle(ChatFormatting.GRAY));
        lines.add(this.title().copy().withStyle(ChatFormatting.WHITE));
        List<Cost> costs = this.ability.value().costs();
        if (player != null && !costs.isEmpty())
            lines.add(Component.translatable("wheel.saturn.tooltip.cost", WheelTooltips.costs(costs, player)));
        this.state.ifPresent(on -> lines.add(Component.translatable(
                on ? "wheel.saturn.tooltip.state_on" : "wheel.saturn.tooltip.state_off")));
        if (player == null) return lines;
        FormulaContext context = FormulaContext.of(player);
        double cooldown = this.ability.value().type() instanceof CooldownSource source ? source.cooldown().evaluate(context) : 0.0D;
        if (cooldown > 0.0D)
            lines.add(Component.translatable("wheel.saturn.tooltip.cooldown", WheelDuration.seconds(cooldown)));
        double castTime = this.ability.value().castTime().evaluate(context);
        if (castTime > 0.0D)
            lines.add(Component.translatable("wheel.saturn.tooltip.cast_time", WheelDuration.seconds(castTime)));
        for (AbilityWheelTooltip provider : AbilityWheelTooltips.providers())
            provider.append(player, this.ability, this.carrier, lines);
        return lines;
    }

}
