package com.iafenvoy.saturn.util;

import net.minecraft.client.Minecraft;
import net.minecraft.client.multiplayer.ClientPacketListener;
import net.minecraft.client.multiplayer.PlayerInfo;

import java.util.Optional;
import java.util.UUID;

/**
 * The client half of {@link PlayerNames}: the players the current connection lists, and nobody else. Kept apart so a
 * dedicated server never loads it, where {@code Minecraft} does not exist.
 */
public final class ClientPlayerNames {
    private ClientPlayerNames() {
    }

    public static Optional<String> lookup(UUID id) {
        ClientPacketListener connection = Minecraft.getInstance().getConnection();
        if (connection == null) return Optional.empty();
        PlayerInfo info = connection.getPlayerInfo(id);
        return info == null ? Optional.empty() : Optional.of(info.getProfile().name());
    }
}
