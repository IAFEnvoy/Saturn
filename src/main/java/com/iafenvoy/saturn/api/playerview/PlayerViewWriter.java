package com.iafenvoy.saturn.api.playerview;

import com.iafenvoy.saturn.view.model.MissingReason;
import com.iafenvoy.saturn.view.model.ValueOrigin;

/** A contributor's bounded writer. Values are encoded immediately instead of retaining mutable references. */
public interface PlayerViewWriter {
    <T> void put(PlayerViewField<T> field, T value, ValueOrigin origin);

    void missing(PlayerViewField<?> field, MissingReason reason);

    boolean contains(PlayerViewField<?> field);

    default <T> void put(PlayerViewField<T> field, T value) {
        this.put(field, value, ValueOrigin.CAPTURED);
    }
}
