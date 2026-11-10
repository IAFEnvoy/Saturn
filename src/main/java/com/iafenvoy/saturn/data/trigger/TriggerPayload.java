package com.iafenvoy.saturn.data.trigger;

import com.iafenvoy.saturn.Saturn;

/**
 * Keys for the payload values a trigger carries beyond the fixed context fields: the objects a formula cannot
 * hold - holders, dimension keys, entity collections. Numbers travel as formula values instead.
 */
public final class TriggerPayload {
    // The Identifier of the whole advancement, including hidden and recipe advancements.
    public static final String ADVANCEMENT = Saturn.MOD_ID + ":advancement";
    public static final String POTION = Saturn.MOD_ID + ":potion";
    public static final String DIMENSION_FROM = Saturn.MOD_ID + ":dimension_from";
    public static final String DIMENSION_TO = Saturn.MOD_ID + ":dimension_to";
    public static final String PARENT = Saturn.MOD_ID + ":parent";
    public static final String PARTNER = Saturn.MOD_ID + ":partner";
    public static final String CHILD = Saturn.MOD_ID + ":child";
    public static final String LIGHTNING = Saturn.MOD_ID + ":lightning";
    public static final String BYSTANDERS = Saturn.MOD_ID + ":bystanders";
    public static final String EFFECT_SOURCE = Saturn.MOD_ID + ":effect_source";
    public static final String ITEMS = Saturn.MOD_ID + ":items";
    // The place a polled trigger measures from (levitation start, fall start, lava entry, nether entry).
    public static final String START_POSITION = Saturn.MOD_ID + ":start_position";
    public static final String BLOCK_STATE = Saturn.MOD_ID + ":block_state";
    public static final String CHANGED_ITEM = Saturn.MOD_ID + ":changed_item";
    // Vanilla's durability criterion reads the damage the stack carried when its own hook ran; a poll can only
    // recover that from the snapshot taken before the change.
    public static final String PREVIOUS_ITEM = Saturn.MOD_ID + ":previous_item";
    public static final String CAUSE = Saturn.MOD_ID + ":cause";
    public static final String THROWER = Saturn.MOD_ID + ":thrower";

    private TriggerPayload() {
    }
}
