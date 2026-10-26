/**
 * Contracts for other mods: the interfaces an addon implements or asks about live here instead of inside the
 * module that happens to consume them, so depending on the framework's extension points does not mean depending
 * on its internals.
 *
 * <p>Only contracts live here. An implementation - a service, a manager, a runtime helper - stays in its own
 * package, and a caller reaches it through that class rather than through a copy of it kept here.
 */
package com.iafenvoy.saturn.api;
