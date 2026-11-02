package com.iafenvoy.saturn.api.playerview;

import com.iafenvoy.saturn.view.model.InformationContext;
import com.iafenvoy.saturn.view.model.InformationRowContent;

import java.util.Optional;

/** Produces one logical row. Empty hides the row; a Missing value explicitly displays unavailable information. */
@FunctionalInterface
public interface PlayerInformationRowProvider {
    Optional<InformationRowContent> collect(PlayerView view, InformationContext context);
}
