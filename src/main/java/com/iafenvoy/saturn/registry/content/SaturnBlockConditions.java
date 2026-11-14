package com.iafenvoy.saturn.registry.content;

import com.iafenvoy.saturn.Saturn;
import com.iafenvoy.saturn.compat.kubejs.type.condition.JsBlockCondition;
import com.iafenvoy.saturn.data.condition.AlwaysCondition;
import com.iafenvoy.saturn.data.condition.BlockCondition;
import com.iafenvoy.saturn.data.condition.NeverCondition;
import com.iafenvoy.saturn.data.condition.SimpleConditions;
import com.iafenvoy.saturn.data.condition.builtin.block.*;
import com.iafenvoy.saturn.data.condition.builtin.block.meta.*;
import com.iafenvoy.saturn.registry.SaturnRegistries;
import com.mojang.serialization.MapCodec;
import net.minecraft.core.Direction;
import net.minecraft.world.level.biome.Biome;
import net.minecraft.world.level.block.LiquidBlockContainer;
import net.neoforged.fml.ModList;
import net.neoforged.neoforge.registries.DeferredRegister;

import java.util.Arrays;

/**
 * The built-in {@code block_condition_type} entries.
 */
public final class SaturnBlockConditions {
    public static final DeferredRegister<MapCodec<? extends BlockCondition>> REGISTRY = DeferredRegister.create(SaturnRegistries.BLOCK_CONDITION_TYPE, Saturn.MOD_ID);

    static {
        REGISTRY.register("block_state", () -> BlockStateCondition.CODEC);
        REGISTRY.register("in_snow", () -> SimpleConditions.createBlock(ctx -> ctx.level().precipitationAt(ctx.pos()) == Biome.Precipitation.SNOW));
        REGISTRY.register("in_thunderstorm", () -> SimpleConditions.createBlock(ctx -> ctx.level().isThundering() && ctx.level().isRainingAt(ctx.pos())));
        REGISTRY.register("nbt", () -> NbtCondition.CODEC);
        REGISTRY.register("air", () -> SimpleConditions.createBlock(ctx -> ctx.level().getBlockState(ctx.pos()).isAir()));
        REGISTRY.register("attachable", () -> SimpleConditions.createBlock(ctx -> Arrays.stream(Direction.values()).anyMatch(direction -> ctx.level().getBlockState(ctx.pos().relative(direction)).isFaceSturdy(ctx.level(), ctx.pos().relative(direction), direction.getOpposite()))));
        REGISTRY.register("block_entity", () -> SimpleConditions.createBlock(ctx -> ctx.level().getBlockEntity(ctx.pos()) != null));
        REGISTRY.register("exposed_to_sky", () -> SimpleConditions.createBlock(ctx -> ctx.level().canSeeSky(ctx.pos())));
        REGISTRY.register("in_rain", () -> SimpleConditions.createBlock(ctx -> ctx.level().isRainingAt(ctx.pos())));
        REGISTRY.register("light_blocking", () -> SimpleConditions.createBlock(ctx -> ctx.level().getBlockState(ctx.pos()).canOcclude()));
        REGISTRY.register("raining", () -> SimpleConditions.createBlock(ctx -> ctx.level().isRaining()));
        REGISTRY.register("replaceable", () -> SimpleConditions.createBlock(ctx -> ctx.level().getBlockState(ctx.pos()).canBeReplaced()));
        REGISTRY.register("thundering", () -> SimpleConditions.createBlock(ctx -> ctx.level().isThundering()));
        REGISTRY.register("water_loggable", () -> SimpleConditions.createBlock(ctx -> ctx.level().getBlockState(ctx.pos()).getBlock() instanceof LiquidBlockContainer));
        REGISTRY.register("constant", () -> ConstantCondition.CODEC);
        REGISTRY.register("fluid", () -> FluidIdCondition.CODEC);
        REGISTRY.register("always", () -> AlwaysCondition.CODEC);
        REGISTRY.register("never", () -> NeverCondition.CODEC);
        REGISTRY.register("and", () -> AndBlockCondition.CODEC);
        REGISTRY.register("or", () -> OrCondition.CODEC);
        REGISTRY.register("not", () -> NotCondition.CODEC);
        REGISTRY.register("chance", () -> ChanceCondition.CODEC);
        REGISTRY.register("offset", () -> OffsetCondition.CODEC);
        // KubeJS is compile-only: without the mod present the bridge classes must not be touched.
        if (ModList.get().isLoaded("kubejs")) REGISTRY.register("js", () -> JsBlockCondition.CODEC);
        REGISTRY.register("block_id", () -> BlockIdCondition.CODEC);
        REGISTRY.register("block_tag", () -> BlockTagCondition.CODEC);
        REGISTRY.register("biome_tag", () -> BiomeTagBlockCondition.CODEC);
        REGISTRY.register("hardness", () -> HardnessCondition.CODEC);
        REGISTRY.register("height", () -> HeightCondition.CODEC);
        REGISTRY.register("light_level", () -> LightLevelCondition.CODEC);
        REGISTRY.register("slipperiness", () -> SlipperinessCondition.CODEC);
        REGISTRY.register("blast_resistance", () -> BlastResistanceCondition.CODEC);
        REGISTRY.register("movement_blocking", () -> MovementBlockingCondition.CODEC);
        REGISTRY.register("adjacent", () -> AdjacentCondition.CODEC);
    }

    private SaturnBlockConditions() {
    }
}
