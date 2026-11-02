package com.iafenvoy.saturn.view;

import com.iafenvoy.saturn.api.playerview.PlayerView;
import com.iafenvoy.saturn.api.playerview.PlayerViewField;
import com.iafenvoy.saturn.view.model.*;
import net.minecraft.network.chat.Component;
import net.minecraft.resources.Identifier;

import java.util.List;
import java.util.Optional;
import java.util.Set;
import java.util.function.Function;

final class BuiltinPlayerInformation {
    private BuiltinPlayerInformation() {
    }

    static void register() {
        row("name", PlayerInformation.BASIC, 0, PlayerViewFields.PROFILE, p -> new InformationValue.Text(p.displayName()));
        PlayerInformation.register(id("health"), PlayerInformation.BASIC, 10, (view, context) -> {
            ViewValue<Double> health = view.read(PlayerViewFields.HEALTH);
            ViewValue<Double> max = view.read(PlayerViewFields.MAX_HEALTH);
            InformationValue value = health.value().map(current -> max.value()
                    .<InformationValue>map(maximum -> new InformationValue.Meter(current, 0, maximum))
                    .orElseGet(() -> new InformationValue.Number(current)))
                    .orElseGet(() -> new InformationValue.Missing(health.missingReason().orElseThrow()));
            return Optional.of(new InformationRowContent(label("health"), value, Set.of(PlayerViewFields.HEALTH.id(), PlayerViewFields.MAX_HEALTH.id())));
        });
        row("food", PlayerInformation.BASIC, 20, PlayerViewFields.FOOD, f -> new InformationValue.Number(f.level()));
        row("experience", PlayerInformation.BASIC, 30, PlayerViewFields.EXPERIENCE, e -> new InformationValue.Number(e.level()));
        row("dimension", PlayerInformation.BASIC, 40, PlayerViewFields.LOCATION, l -> text(l.dimension().toString()));
        row("game_mode", PlayerInformation.BASIC, 50, PlayerViewFields.GAME_MODE,
                mode -> new InformationValue.Text(Component.translatableWithFallback("gameMode." + mode, mode)));
        row("absorption", PlayerInformation.STATUS, 0, PlayerViewFields.ABSORPTION, InformationValue.Number::new);
        row("air", PlayerInformation.STATUS, 10, PlayerViewFields.AIR, value -> new InformationValue.Number(value));
        row("effects", PlayerInformation.STATUS, 20, PlayerViewFields.EFFECTS, effects -> new InformationValue.Values(effects.stream()
                .<InformationValue>map(effect -> new InformationValue.Text(Component.translatable("info.saturn.effect_entry",
                        effect.id().toString(), effect.amplifier() + 1, effect.duration() < 0
                                ? Component.translatable("info.saturn.permanent")
                                : Component.translatable("info.saturn.duration_ticks", effect.duration())))).toList()));
        row("attributes", PlayerInformation.STATUS, 30, PlayerViewFields.ATTRIBUTES, attributes -> new InformationValue.Table(
                List.of(column("name"), column("base"), column("value")),
                attributes.stream().map(attribute -> attribute.id().toString()).toList(), attributes.stream().map(attribute -> List.of(
                        text(attribute.id().toString()), new InformationValue.Number(attribute.base()),
                        attribute.effective().<InformationValue>map(InformationValue.Number::new)
                                .orElseGet(() -> new InformationValue.Missing(MissingReason.NOT_CAPTURED)))).toList()));
        row("resources", PlayerInformation.STATUS, 40, PlayerViewFields.RESOURCES, resources -> new InformationValue.Table(
                List.of(column("name"), column("value")), resources.stream().map(resource -> resource.id().toString()).toList(),
                resources.stream().map(resource -> List.<InformationValue>of(text(resource.id().toString()),
                        resource.minimum().isPresent() && resource.maximum().isPresent()
                                ? new InformationValue.Meter(resource.value(), resource.minimum().get(), resource.maximum().get())
                                : new InformationValue.Number(resource.value()))).toList()));
        row("abilities", PlayerInformation.STATUS, 50, PlayerViewFields.ABILITIES,
                abilities -> new InformationValue.Values(abilities.stream().<InformationValue>map(ability -> text(ability.toString())).toList()));
        row("equipment", PlayerInformation.EQUIPMENT, 0, PlayerViewFields.EQUIPMENT, InformationValue.Items::new);
        row("inventory", PlayerInformation.EQUIPMENT, 10, PlayerViewFields.INVENTORY, InformationValue.Items::new);
        row("ender_chest", PlayerInformation.EQUIPMENT, 20, PlayerViewFields.ENDER_CHEST, InformationValue.Items::new);
        row("carried_item", PlayerInformation.EQUIPMENT, 30, PlayerViewFields.CARRIED_ITEM, InformationValue.Items::new);
    }

    private static <T> void row(String name, InfomationGroup group, int order, PlayerViewField<T> field, Function<T, InformationValue> display) {
        PlayerInformation.register(id(name), group, order, (view, context) -> Optional.of(content(view, name, field, display)));
    }

    private static <T> InformationRowContent content(PlayerView view, String name, PlayerViewField<T> field, Function<T, InformationValue> display) {
        ViewValue<T> value = view.read(field);
        return new InformationRowContent(label(name), value.value().map(display)
                .orElseGet(() -> new InformationValue.Missing(value.missingReason().orElseThrow())), Set.of(field.id()));
    }

    private static InformationValue.Text text(String value) {
        return new InformationValue.Text(Component.literal(value));
    }

    private static Component label(String name) {
        return Component.translatable("info.saturn.field." + name);
    }

    private static InformationValue.Column column(String name) {
        return new InformationValue.Column(name, label(name));
    }

    private static Identifier id(String path) {
        return Identifier.fromNamespaceAndPath("saturn", path);
    }
}
