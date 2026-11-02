package com.iafenvoy.saturn.api.playerview;

import com.iafenvoy.saturn.view.model.PlayerViewMeta;
import com.iafenvoy.saturn.view.model.ViewValue;

/** A detached player snapshot. Reading a field never creates an attachment or resolves a live entity. */
public interface PlayerView {
    PlayerViewMeta meta();

    /** Returns an independent decoded value, or an explicit reason why the value is unavailable. */
    <T> ViewValue<T> read(PlayerViewField<T> field);
}
