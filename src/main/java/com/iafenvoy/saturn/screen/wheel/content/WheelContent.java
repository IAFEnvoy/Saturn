package com.iafenvoy.saturn.screen.wheel.content;

import com.iafenvoy.saturn.api.wheel.WheelEntryKind;
import com.iafenvoy.saturn.api.wheel.WheelEntryProvider;
import com.iafenvoy.saturn.api.wheel.WheelMenuEntry;
import com.iafenvoy.saturn.api.wheel.WheelSource;
import com.iafenvoy.saturn.attachment.WheelLayoutAttachment;
import com.iafenvoy.saturn.data.ability.Ability;
import com.iafenvoy.saturn.network.payload.WheelLayoutC2SPayload;
import com.iafenvoy.saturn.registry.SaturnAttachments;
import com.iafenvoy.saturn.runtime.ability.AbilityActivationService;
import com.iafenvoy.saturn.runtime.wheel.*;
import net.minecraft.core.Holder;
import net.minecraft.resources.Identifier;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import net.neoforged.neoforge.client.network.ClientPacketDistributor;
import org.jspecify.annotations.Nullable;

import java.util.*;

public final class WheelContent {
    private static final Map<Identifier, WheelEntryProvider> PROVIDERS = new LinkedHashMap<>();

    static {
        register(WheelEntryKinds.ABILITY, new WheelEntryProvider() {
            @Override
            public List<WheelMenuEntry> options(Player player) {
                return abilities(player, WheelSourceTypes.CONFIGURED, true);
            }

            @Override
            public List<WheelMenuEntry> available(Player player) {
                return pool(player);
            }
        });
    }

    private WheelContent() {
    }

    public static synchronized void register(WheelEntryKind kind, WheelEntryProvider provider) {
        PROVIDERS.putIfAbsent(kind.id(), provider);
    }

    private static synchronized Map<Identifier, WheelEntryProvider> providers() {
        return new LinkedHashMap<>(PROVIDERS);
    }

    public static List<WheelMenuEntry> options(@Nullable Player player) {
        return collect(player, true);
    }

    private static List<WheelMenuEntry> collect(@Nullable Player player, boolean options) {
        if (player == null) return List.of();
        Map<WheelSlot, WheelMenuEntry> entries = new LinkedHashMap<>();
        providers().forEach((kind, provider) -> {
            for (WheelMenuEntry entry : options ? provider.options(player) : provider.available(player))
                if (entry != null && entry.kind().id().equals(kind))
                    entries.putIfAbsent(WheelSlot.of(entry.kind(), entry.id()), entry);
        });
        return List.copyOf(entries.values());
    }

    public static List<WheelMenuEntry> entries(@Nullable Player player, WheelSource source) {
        if (player == null) return List.of();
        if (!source.configured()) return abilities(player, source, false);
        Map<WheelSlot, WheelMenuEntry> found = new HashMap<>();
        for (WheelMenuEntry entry : collect(player, false)) found.put(WheelSlot.of(entry.kind(), entry.id()), entry);
        List<WheelMenuEntry> slots = new ArrayList<>(WheelLayout.SLOTS);
        for (WheelSlot slot : layoutFor(player).slots()) slots.add(found.get(slot));
        return Collections.unmodifiableList(slots);
    }

    public static List<WheelMenuEntry> pool(@Nullable Player player) {
        return player == null ? List.of() : abilities(player, WheelSourceTypes.CONFIGURED, false);
    }

    private static List<WheelMenuEntry> abilities(Player player, WheelSource source, boolean editable) {
        Map<Holder<Ability>, ItemStack> carriers = WheelSources.carriers(player, source);
        List<Holder<Ability>> abilities = source.configured() ? WheelSources.abilities(player) : WheelSources.abilities(player, source);
        List<WheelMenuEntry> result = new ArrayList<>();
        for (Holder<Ability> ability : abilities) {
            if (editable && WheelSources.handOnly(player, ability)) continue;
            result.add(new AbilityWheelEntry(ability, carriers.get(ability), AbilityActivationService.state(player, ability)));
        }
        return List.copyOf(result);
    }

    public static WheelLayout layoutFor(@Nullable Player player) {
        return player == null ? WheelLayout.EMPTY : player.getExistingData(SaturnAttachments.WHEEL_LAYOUT)
                .flatMap(WheelLayoutAttachment::layout).orElse(WheelLayout.EMPTY);
    }

    public static void save(WheelLayout layout) {
        ClientPacketDistributor.sendToServer(new WheelLayoutC2SPayload(layout));
        WheelSelectionSync.republish();
    }
}
