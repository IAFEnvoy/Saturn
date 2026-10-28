package com.iafenvoy.saturn.data.ability;

import com.iafenvoy.saturn.attachment.AbilityAttachment;
import com.iafenvoy.saturn.attachment.ResourceHolderAttachment;
import com.iafenvoy.saturn.data.storage.DataStorageContext;
import com.iafenvoy.saturn.util.formula.FormulaContext;
import net.minecraft.core.Holder;
import net.minecraft.world.entity.LivingEntity;

/**
 * What a type's lifecycle hooks are handed: the holder the ability is granted to, everything the runtime already
 * looked up for this pass, and the tick it is running on. A hook that needs the definition reads it from the holder.
 */
public record AbilityContext(LivingEntity holder, Holder<Ability> ability, AbilityAttachment abilities,
                             ResourceHolderAttachment resources, FormulaContext formula, long gameTime) {
    public Ability definition() {
        return this.ability.value();
    }

    // What stored values tick on: they see the body, the formula and the tick, never the ability that happens to own
    // them, so the same kinds serve content that is not an ability.
    public DataStorageContext storage() {
        return new DataStorageContext(this.holder, this.formula, this.gameTime);
    }
}
