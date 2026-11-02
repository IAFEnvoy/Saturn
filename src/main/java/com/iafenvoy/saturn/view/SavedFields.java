package com.iafenvoy.saturn.view;

import com.iafenvoy.saturn.api.playerview.PlayerViewField;
import com.iafenvoy.saturn.api.playerview.PlayerViewWriter;
import com.iafenvoy.saturn.view.model.MissingReason;
import com.iafenvoy.saturn.view.model.ValueOrigin;

import java.util.function.Supplier;

final class SavedFields {
    private SavedFields() {
    }

    static <T> void read(PlayerViewWriter writer, PlayerViewField<T> field, boolean present, Supplier<T> parse) {
        if (!present || writer.contains(field)) return;
        T value;
        try {
            value = parse.get();
        } catch (RuntimeException ignored) {
            writer.missing(field, MissingReason.INVALID);
            return;
        }
        writer.put(field, value, ValueOrigin.SAVED);
    }
}
