package com.iafenvoy.saturn.compat.kubejs.binding;

import com.google.gson.JsonObject;
import com.iafenvoy.saturn.compat.kubejs.callback.SaturnJsActionCallbacks;
import com.iafenvoy.saturn.compat.kubejs.codec.SaturnKubeJsDataCodec;
import com.iafenvoy.saturn.data.action.BiEntityAction;
import com.iafenvoy.saturn.data.action.BlockAction;
import com.iafenvoy.saturn.data.action.EntityAction;
import com.iafenvoy.saturn.data.action.ItemAction;
import com.iafenvoy.saturn.util.formula.FormulaContext;
import com.iafenvoy.saturn.util.function.QuadConsumer;
import com.iafenvoy.saturn.util.function.TriConsumer;
import dev.latvian.mods.kubejs.typings.Info;
import net.minecraft.core.BlockPos;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.Level;

/**
 * KubeJS registrations for the four intrinsic action dispatch types. Callbacks receive the
 * {@link FormulaContext} of the dispatch as their last argument, so the same event payload variables a data pack
 * action reads are available to the script.
 */
public final class SaturnKubeJsActionBindings {
    @Info("Registers an entity action. Datapack type: saturn:js")
    public void entity(String id, TriConsumer<Entity, JsonObject, FormulaContext> callback) {
        SaturnJsActionCallbacks.registerEntity(id, callback);
    }

    @Info("Registers a bi-entity action. Datapack type: saturn:js")
    public void biEntity(String id, QuadConsumer<Entity, Entity, JsonObject, FormulaContext> callback) {
        SaturnJsActionCallbacks.registerBiEntity(id, callback);
    }

    @Info("Registers a block action. Datapack type: saturn:js")
    public void block(String id, QuadConsumer<Level, BlockPos, JsonObject, FormulaContext> callback) {
        SaturnJsActionCallbacks.registerBlock(id, callback);
    }

    @Info("Registers an item action. Datapack type: saturn:js")
    public void item(String id, QuadConsumer<Entity, ItemStack, JsonObject, FormulaContext> callback) {
        SaturnJsActionCallbacks.registerItem(id, callback);
    }

    @Info("Decodes and executes any registered entity action definition.")
    public void executeEntity(Entity entity, JsonObject definition) {
        SaturnKubeJsDataCodec.decodeCached(EntityAction.CODEC, definition, entity.level().registryAccess())
                .execute(entity, FormulaContext.of(entity));
    }

    @Info("Decodes and executes any registered bi-entity action definition.")
    public void executeBiEntity(Entity actor, Entity target, JsonObject definition) {
        SaturnKubeJsDataCodec.decodeCached(BiEntityAction.CODEC, definition, actor.level().registryAccess())
                .execute(actor, target, FormulaContext.of(actor));
    }

    @Info("Decodes and executes any registered block action definition.")
    public void executeBlock(Level level, BlockPos pos, JsonObject definition) {
        SaturnKubeJsDataCodec.decodeCached(BlockAction.CODEC, definition, level.registryAccess())
                .execute(level, pos, FormulaContext.of(level));
    }

    @Info("Decodes and executes any registered item action definition.")
    public void executeItem(Entity holder, ItemStack stack, JsonObject definition) {
        SaturnKubeJsDataCodec.decodeCached(ItemAction.CODEC, definition, holder.level().registryAccess())
                .execute(holder, stack, FormulaContext.of(holder));
    }
}
