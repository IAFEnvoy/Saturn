package com.iafenvoy.saturn.data.condition.builtin.entity;

import com.iafenvoy.saturn.data.condition.EntityCondition;
import com.iafenvoy.saturn.data.context.condition.EntityConditionContext;
import com.mojang.serialization.Codec;
import com.mojang.serialization.MapCodec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import net.minecraft.util.StringRepresentable;
import net.minecraft.world.entity.player.Player;
import org.jspecify.annotations.NonNull;

import java.util.Locale;
import java.util.function.Predicate;

public record PlayerAbilityCondition(PlayerAbility ability) implements EntityCondition {
    public static final MapCodec<PlayerAbilityCondition> CODEC = RecordCodecBuilder.mapCodec(i -> i.group(
            PlayerAbility.CODEC.fieldOf("ability").forGetter(PlayerAbilityCondition::ability)
    ).apply(i, PlayerAbilityCondition::new));

    @Override
    public @NonNull MapCodec<? extends EntityCondition> codec() {
        return CODEC;
    }

    @Override
    public boolean test(@NonNull EntityConditionContext ctx) {
        return ctx.entity() instanceof Player player && this.ability.get(player);
    }

    public enum PlayerAbility implements StringRepresentable {
        FLYING(p -> p.getAbilities().flying),
        INSTABUILD(p -> p.getAbilities().instabuild),
        INVULNERABLE(p -> p.getAbilities().invulnerable),
        MAY_BUILD(p -> p.getAbilities().mayBuild),
        MAYFLY(Player::mayFly);
        public static final Codec<PlayerAbility> CODEC = StringRepresentable.fromEnum(PlayerAbility::values);
        private final Predicate<Player> getter;

        PlayerAbility(Predicate<Player> getter) {
            this.getter = getter;
        }

        public boolean get(Player player) {
            return this.getter.test(player);
        }

        @Override
        public @NonNull String getSerializedName() {
            return this.name().toLowerCase(Locale.ROOT);
        }
    }
}
