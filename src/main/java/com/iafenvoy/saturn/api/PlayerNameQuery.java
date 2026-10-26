package com.iafenvoy.saturn.api;

import java.util.UUID;

/**
 * How the client half of the name lookup asks the server about an id the connection's player list does not list.
 *
 * <p>Saturn sends no packets of its own, so the question belongs to the dependent mod: install one here from your
 * mod setup, send your own payload from {@link #ask(UUID)}, and answer it by calling
 * {@link com.iafenvoy.saturn.util.ClientPlayerNames#remember(UUID, java.util.Optional)} when the reply arrives. The
 * default ignores the question, which leaves every lookup local to the players this client can already see.
 */
public interface PlayerNameQuery {
    PlayerNameQuery NONE = id -> {
    };

    void ask(UUID id);

    static PlayerNameQuery get() {
        return Installed.CURRENT;
    }

    static void set(PlayerNameQuery query) {
        Installed.CURRENT = query == null ? NONE : query;
    }

    final class Installed {
        private static volatile PlayerNameQuery CURRENT = NONE;

        private Installed() {
        }
    }
}
