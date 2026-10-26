package com.iafenvoy.saturn.compat.kubejs.binding;

import com.google.gson.JsonObject;
import com.iafenvoy.saturn.compat.kubejs.callback.SaturnJsConditionCallbacks;
import com.iafenvoy.saturn.compat.kubejs.codec.SaturnKubeJsDataCodec;
import com.iafenvoy.saturn.data.condition.*;
import com.iafenvoy.saturn.util.formula.FormulaContext;
import com.iafenvoy.saturn.util.function.QuadPredicate;
import com.iafenvoy.saturn.util.function.TriPredicate;
import dev.latvian.mods.kubejs.typings.Info;
import net.minecraft.core.BlockPos;
import net.minecraft.world.damagesource.DamageSource;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.Level;

/**
 * KubeJS registrations for the five intrinsic condition dispatch types. Callbacks receive the
 * {@link FormulaContext} of the test as their last argument, so the same event payload variables a data pack
 * condition reads are available to the script.
 */
public final class SaturnKubeJsConditionBindings {
    @Info("Registers an entity condition. Datapack type: saturn:js")
    public void entity(String id, TriPredicate<Entity, JsonObject, FormulaContext> callback) {
        SaturnJsConditionCallbacks.registerEntity(id, callback);
    }

    @Info("Registers a bi-entity condition. Datapack type: saturn:js")
    public void biEntity(String id, QuadPredicate<Entity, Entity, JsonObject, FormulaContext> callback) {
        SaturnJsConditionCallbacks.registerBiEntity(id, callback);
    }

    @Info("Registers a block condition. Datapack type: saturn:js")
    public void block(String id, QuadPredicate<Level, BlockPos, JsonObject, FormulaContext> callback) {
        SaturnJsConditionCallbacks.registerBlock(id, callback);
    }

    @Info("Registers an item condition. Datapack type: saturn:js")
    public void item(String id, QuadPredicate<Entity, ItemStack, JsonObject, FormulaContext> callback) {
        SaturnJsConditionCallbacks.registerItem(id, callback);
    }

    @Info("Registers a damage condition. Datapack type: saturn:js")
    public void damage(String id, QuadPredicate<DamageSource, Double, JsonObject, FormulaContext> callback) {
        SaturnJsConditionCallbacks.registerDamage(id, callback);
    }

    @Info("Decodes and evaluates any registered entity condition definition.")
    public boolean testEntity(Entity entity, JsonObject definition) {
        return SaturnKubeJsDataCodec.decodeCached(EntityCondition.CODEC, definition, entity.level().registryAccess())
                .test(entity, FormulaContext.of(entity));
    }

    @Info("Decodes and evaluates any registered bi-entity condition definition.")
    public boolean testBiEntity(Entity actor, Entity target, JsonObject definition) {
        return SaturnKubeJsDataCodec.decodeCached(BiEntityCondition.CODEC, definition, actor.level().registryAccess())
                .test(actor, target, FormulaContext.of(actor));
    }

    @Info("Decodes and evaluates any registered block condition definition.")
    public boolean testBlock(Level level, BlockPos pos, JsonObject definition) {
        return SaturnKubeJsDataCodec.decodeCached(BlockCondition.CODEC, definition, level.registryAccess())
                .test(level, pos, FormulaContext.of(level));
    }

    @Info("Decodes and evaluates any registered item condition definition.")
    public boolean testItem(Entity holder, ItemStack stack, JsonObject definition) {
        return SaturnKubeJsDataCodec.decodeCached(ItemCondition.CODEC, definition, holder.level().registryAccess())
                .test(holder, stack, FormulaContext.of(holder));
    }

    @Info("Decodes and evaluates any registered damage condition definition.")
    public boolean testDamage(Level level, DamageSource source, float amount, JsonObject definition) {
        FormulaContext context = source.getEntity() == null ? FormulaContext.EMPTY : FormulaContext.of(source.getEntity());
        return SaturnKubeJsDataCodec.decodeCached(DamageCondition.CODEC, definition, level.registryAccess())
                .test(source, amount, context);
    }
}
