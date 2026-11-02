package com.iafenvoy.saturn.api.playerview;

import com.iafenvoy.saturn.view.model.PlayerViewRequest;
import com.iafenvoy.saturn.view.model.PlayerViewResult;

import java.util.concurrent.CompletionStage;

/** Host-owned transport. The server must authorize the actual requester before returning a filtered snapshot. */
@FunctionalInterface
public interface PlayerViewAccess {
    CompletionStage<PlayerViewResult> query(PlayerViewRequest request);
}
