package com.iafenvoy.saturn.view.model;

import java.util.Objects;

/** Viewer formatting only. Permissions and field filtering belong to the host's server query handler. */
public record InformationContext(String language) {
    public static final InformationContext DEFAULT = new InformationContext("en_us");

    public InformationContext {
        Objects.requireNonNull(language);
    }
}
