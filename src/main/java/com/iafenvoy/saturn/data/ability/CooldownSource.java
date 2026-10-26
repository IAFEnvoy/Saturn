package com.iafenvoy.saturn.data.ability;

import com.iafenvoy.saturn.util.formula.NumberProvider;

/**
 * A type whose activation is paid for, and which therefore carries a cooldown length. It is a separate interface
 * because a type that never pays never reads one, and the payment paths ask through it rather than through the class.
 */
public interface CooldownSource {
    NumberProvider cooldown();
}
