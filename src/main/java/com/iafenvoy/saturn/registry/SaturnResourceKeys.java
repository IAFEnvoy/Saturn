package com.iafenvoy.saturn.registry;

import com.iafenvoy.saturn.Saturn;
import com.iafenvoy.saturn.data.ability.Ability;
import com.iafenvoy.saturn.data.ability.AbilityType;
import com.iafenvoy.saturn.data.ability.TargetSelector;
import com.iafenvoy.saturn.data.action.BiEntityAction;
import com.iafenvoy.saturn.data.action.BlockAction;
import com.iafenvoy.saturn.data.action.EntityAction;
import com.iafenvoy.saturn.data.action.ItemAction;
import com.iafenvoy.saturn.data.condition.*;
import com.iafenvoy.saturn.data.cost.Cost;
import com.iafenvoy.saturn.data.item.IncompleteMarker;
import com.iafenvoy.saturn.data.resource.Resource;
import com.iafenvoy.saturn.data.resource.ResourceValueProvider;
import com.iafenvoy.saturn.data.resourcebar.ResourceBarContext;
import com.iafenvoy.saturn.data.resourcebar.ResourceBarRenderData;
import com.iafenvoy.saturn.data.resourcebar.ResourceBarVisibility;
import com.iafenvoy.saturn.data.storage.DataStorage;
import com.iafenvoy.saturn.data.timeline.TimelineEntry;
import com.iafenvoy.saturn.data.trigger.Trigger;
import com.iafenvoy.saturn.data.trigger.TriggerRule;
import com.iafenvoy.saturn.util.formula.FormulaVariable;
import com.iafenvoy.saturn.util.formula.NumberProvider;
import com.iafenvoy.saturn.util.matcher.ItemMatcher.Entry;
import com.mojang.serialization.MapCodec;
import net.minecraft.core.Registry;
import net.minecraft.resources.Identifier;
import net.minecraft.resources.ResourceKey;
import net.objecthunter.exp4j.function.Function;

/**
 * Every registry key Saturn owns, kept separate from the registry instances and the registration events so codecs
 * and runtime services can read registry identity without depending on a registration class.
 *
 * <p>The namespace is {@code saturn}: a data pack writes its entries under {@code data/<namespace>/saturn/<registry>/}.
 */
public final class SaturnResourceKeys {
    // Builtin registries
    public static final ResourceKey<Registry<MapCodec<? extends AbilityType>>> ABILITY_TYPE = create("ability_type");
    public static final ResourceKey<Registry<MapCodec<? extends TargetSelector>>> ABILITY_TARGET_SELECTOR_TYPE = create("ability_target_selector_type");
    public static final ResourceKey<Registry<MapCodec<? extends Cost>>> COST_TYPE = create("cost_type");
    public static final ResourceKey<Registry<MapCodec<? extends DataStorage>>> DATA_STORAGE_TYPE = create("data_storage_type");
    public static final ResourceKey<Registry<MapCodec<? extends Trigger>>> TRIGGER_TYPE = create("trigger_type");
    public static final ResourceKey<Registry<MapCodec<? extends NumberProvider>>> NUMBER_PROVIDER_TYPE = create("number_provider_type");
    public static final ResourceKey<Registry<Function>> FORMULA_FUNCTION = create("formula_function");
    public static final ResourceKey<Registry<FormulaVariable>> FORMULA_VARIABLE = create("formula_variable");
    public static final ResourceKey<Registry<MapCodec<? extends ResourceValueProvider>>> RESOURCE_VALUE_PROVIDER_TYPE = create("resource_value_provider_type");
    public static final ResourceKey<Registry<MapCodec<? extends EntityAction>>> ENTITY_ACTION_TYPE = create("entity_action_type");
    public static final ResourceKey<Registry<MapCodec<? extends BiEntityAction>>> BI_ENTITY_ACTION_TYPE = create("bi_entity_action_type");
    public static final ResourceKey<Registry<MapCodec<? extends BlockAction>>> BLOCK_ACTION_TYPE = create("block_action_type");
    public static final ResourceKey<Registry<MapCodec<? extends ItemAction>>> ITEM_ACTION_TYPE = create("item_action_type");
    public static final ResourceKey<Registry<MapCodec<? extends EntityCondition>>> ENTITY_CONDITION_TYPE = create("entity_condition_type");
    public static final ResourceKey<Registry<MapCodec<? extends BiEntityCondition>>> BI_ENTITY_CONDITION_TYPE = create("bi_entity_condition_type");
    public static final ResourceKey<Registry<MapCodec<? extends BlockCondition>>> BLOCK_CONDITION_TYPE = create("block_condition_type");
    public static final ResourceKey<Registry<MapCodec<? extends ItemCondition>>> ITEM_CONDITION_TYPE = create("item_condition_type");
    public static final ResourceKey<Registry<MapCodec<? extends DamageCondition>>> DAMAGE_CONDITION_TYPE = create("damage_condition_type");
    public static final ResourceKey<Registry<MapCodec<? extends ResourceBarRenderData>>> RESOURCE_BAR_RENDER_DATA_TYPE = create("resource_bar_render_data_type");
    public static final ResourceKey<Registry<ResourceBarContext>> RESOURCE_BAR_CONTEXT = create("resource_bar_context");
    public static final ResourceKey<Registry<MapCodec<? extends ResourceBarVisibility>>> RESOURCE_BAR_VISIBILITY_TYPE = create("resource_bar_visibility_type");
    public static final ResourceKey<Registry<MapCodec<? extends Entry>>> ITEM_MATCHER_ENTRY_TYPE = create("item_matcher_entry_type");
    public static final ResourceKey<Registry<MapCodec<? extends TimelineEntry>>> TIMELINE_ENTRY_TYPE = create("timeline_entry_type");

    // Datapack registries
    public static final ResourceKey<Registry<Resource>> RESOURCE = create("resource");
    public static final ResourceKey<Registry<Ability>> ABILITY = create("ability");
    public static final ResourceKey<Registry<TriggerRule>> TRIGGER = create("trigger");
    public static final ResourceKey<Registry<IncompleteMarker>> INCOMPLETE = create("incomplete");

    private static <T> ResourceKey<Registry<T>> create(String path) {
        return ResourceKey.createRegistryKey(Identifier.fromNamespaceAndPath(Saturn.MOD_ID, path));
    }
}
