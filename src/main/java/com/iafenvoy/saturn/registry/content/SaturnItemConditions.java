package com.iafenvoy.saturn.registry.content;

import com.iafenvoy.saturn.Saturn;
import com.iafenvoy.saturn.compat.kubejs.type.condition.JsItemCondition;
import com.iafenvoy.saturn.data.condition.AlwaysCondition;
import com.iafenvoy.saturn.data.condition.ItemCondition;
import com.iafenvoy.saturn.data.condition.NeverCondition;
import com.iafenvoy.saturn.data.condition.SimpleConditions;
import com.iafenvoy.saturn.data.condition.builtin.item.*;
import com.iafenvoy.saturn.data.condition.builtin.item.meta.*;
import com.iafenvoy.saturn.registry.SaturnRegistries;
import com.mojang.serialization.MapCodec;
import net.minecraft.core.component.DataComponents;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.world.item.BlockItem;
import net.minecraft.world.item.crafting.RecipeType;
import net.minecraft.world.item.crafting.SingleRecipeInput;
import net.neoforged.fml.ModList;
import net.neoforged.neoforge.registries.DeferredRegister;

/**
 * The built-in {@code item_condition_type} entries.
 */
public final class SaturnItemConditions {
    public static final DeferredRegister<MapCodec<? extends ItemCondition>> REGISTRY = DeferredRegister.create(SaturnRegistries.ITEM_CONDITION_TYPE, Saturn.MOD_ID);

    static {
        REGISTRY.register("smeltable", () -> SimpleConditions.createItem(ctx -> ctx.holder().level() instanceof ServerLevel level && level.recipeAccess().getRecipeFor(RecipeType.SMELTING, new SingleRecipeInput(ctx.stack()), level).isPresent()));
        REGISTRY.register("block_item", () -> SimpleConditions.createItem(ctx -> ctx.stack().getItem() instanceof BlockItem));
        REGISTRY.register("empty", () -> SimpleConditions.createItem(ctx -> ctx.stack().isEmpty()));
        REGISTRY.register("enchantable", () -> SimpleConditions.createItem(ctx -> ctx.stack().isEnchantable()));
        REGISTRY.register("food", () -> SimpleConditions.createItem(ctx -> ctx.stack().has(DataComponents.FOOD)));
        REGISTRY.register("is_damageable", () -> SimpleConditions.createItem(ctx -> ctx.stack().isDamageableItem()));
        REGISTRY.register("constant", () -> ConstantCondition.CODEC);
        REGISTRY.register("always", () -> AlwaysCondition.CODEC);
        REGISTRY.register("never", () -> NeverCondition.CODEC);
        REGISTRY.register("and", () -> AndItemCondition.CODEC);
        REGISTRY.register("or", () -> OrCondition.CODEC);
        REGISTRY.register("not", () -> NotCondition.CODEC);
        REGISTRY.register("chance", () -> ChanceCondition.CODEC);
        // KubeJS is compile-only: without the mod present the bridge classes must not be touched.
        if (ModList.get().isLoaded("kubejs")) REGISTRY.register("js", () -> JsItemCondition.CODEC);
        REGISTRY.register("item_id", () -> ItemIdCondition.CODEC);
        REGISTRY.register("item_tag", () -> ItemTagCondition.CODEC);
        REGISTRY.register("item_matcher", () -> ItemMatcherCondition.CODEC);
        REGISTRY.register("amount", () -> AmountCondition.CODEC);
        REGISTRY.register("fuel", () -> FuelCondition.CODEC);
        REGISTRY.register("is_equipable", () -> IsEquipableCondition.CODEC);
        REGISTRY.register("relative_durability", () -> RelativeDurabilityCondition.CODEC);
        REGISTRY.register("armor_value", () -> ArmorValueCondition.CODEC);
        REGISTRY.register("durability", () -> DurabilityCondition.CODEC);
        REGISTRY.register("on_cooldown", () -> OnCooldownCondition.CODEC);
        REGISTRY.register("ingredient", () -> IngredientCondition.CODEC);
        REGISTRY.register("tool_ability", () -> ToolAbilityCondition.CODEC);
        REGISTRY.register("base_enchantment", () -> BaseEnchantmentCondition.CODEC);
        REGISTRY.register("has_component", () -> HasComponentCondition.CODEC);
        REGISTRY.register("component", () -> ComponentCondition.CODEC);
    }

    private SaturnItemConditions() {
    }
}
