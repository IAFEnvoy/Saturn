package com.iafenvoy.saturn.data.context.action;

import com.iafenvoy.saturn.data.context.Context;
import com.iafenvoy.saturn.util.formula.FormulaContext;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.phys.Vec3;
import org.jetbrains.annotations.Nullable;

/** An entity action with a formula context and an optional activation origin. */
public class EntityActionContext extends Context {
    private final Entity entity;
    private final FormulaContext formula;

    public EntityActionContext(Entity entity, FormulaContext formula) {
        this(entity, formula, null);
    }

    public EntityActionContext(Entity entity, FormulaContext formula, @Nullable Vec3 origin) {
        this.entity = entity;
        this.formula = formula;
        this.origin(origin);
    }

    public Entity entity() {
        return this.entity;
    }

    public Vec3 position() {
        return this.origin().orElseGet(this.entity::position);
    }

    // Eyes, not position: the height a projectile leaves an entity by, which an item's own place has no equivalent
    // of.
    public Vec3 launchPosition() {
        return this.origin().orElseGet(this.entity::getEyePosition);
    }

    @Override
    public FormulaContext formula() {
        return this.formula;
    }
}
