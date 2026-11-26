package com.iafenvoy.saturn.framework;

import com.iafenvoy.saturn.api.AbilityHooks;
import com.iafenvoy.saturn.data.ability.Ability;
import com.iafenvoy.saturn.data.action.NoOpAction;
import com.iafenvoy.saturn.data.cost.Cost;
import com.iafenvoy.saturn.data.cost.CostPayment;
import com.iafenvoy.saturn.data.cost.CostTarget;
import com.iafenvoy.saturn.data.cost.CostTransaction;
import com.iafenvoy.saturn.data.cost.Debit;
import com.iafenvoy.saturn.data.cost.context.CostContext;
import com.iafenvoy.saturn.data.timeline.TimelineContext;
import com.iafenvoy.saturn.data.timeline.TimelineJump;
import com.iafenvoy.saturn.data.timeline.TimelineState;
import com.iafenvoy.saturn.registry.SaturnRegistries;
import com.iafenvoy.saturn.runtime.resulting.Result;
import com.iafenvoy.saturn.runtime.resulting.failures.CostFailure;
import com.iafenvoy.saturn.screen.wheel.WheelConfigurationLayout;
import com.iafenvoy.saturn.util.formula.FormulaContext;
import com.iafenvoy.saturn.util.formula.number.Constant;
import com.mojang.serialization.MapCodec;
import net.minecraft.core.*;
import net.minecraft.resources.Identifier;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.item.ItemStack;

import java.util.*;

public final class FrameworkVerification {
    private static int checks;

    static void main(String[] args) {
        timelines();
        defaults();
        hooks();
        wheelLayout();
        settlements();
        System.out.println("Framework verification: " + checks + " checks passed");
    }

    private static void wheelLayout() {
        for (int[] size : new int[][]{{320, 240}, {426, 240}, {640, 360}, {200, 150}}) {
            WheelConfigurationLayout layout = WheelConfigurationLayout.of(size[0], size[1]);
            check(layout.bottom() - layout.top() >= 22, "wheel pool visible");
            check(layout.bottom() < layout.slotY(), "wheel pool separated from slots");
            check(layout.slotY(11) + 22 <= size[1] - 34, "wheel slots above button");
            for (int i = 0; i < 12; i++) check(layout.slotAt(layout.x(i) + 1, layout.slotY(i) + 1) == i, "wheel slot hit " + i);
            int scroll = layout.maximumScroll(200);
            check(scroll > 0, "wheel pool scrollable");
            check(layout.optionAt(layout.left() + 1, layout.top() - 1, scroll, 200) == -1, "wheel scissor clips hit");
            check(layout.optionAt(layout.left() + 23, layout.top() + 1, 0, 200) == -1, "wheel cell gap not clickable");
            check(layout.optionAt(layout.x(199) + 1, layout.top() + 199 / layout.columns() * 24 - scroll + 1, scroll, 200) == 199,
                    "wheel last row reachable");
        }
    }

    private static void settlements() {
        List<String> calls = new ArrayList<>();
        CostPayment payment = CostPayment.of(CostContext.of(null, "verification"));
        check(payment.price(List.of(new VerificationCost("refused", calls, true),
                new VerificationCost("paid", calls, false))).isEmpty(), "bill prices both fees");
        CostPayment.Settlement settlement = payment.settle().orThrow();
        check(calls.equals(List.of("check:refused", "check:paid", "pay:refused", "pay:paid")), "both passes keep order");
        check(settlement.issues().equals(List.of(new CostPayment.DeductionIssue("script refused", CostFailure.SCRIPT_REJECTED))),
                "real failure is visible to callers");
        check(settlement.debits().size() == 1 && settlement.debits().getFirst().amount() == 1.0D, "known debit retained");
        check(payment.pay().orThrow().equals(settlement.debits()) && calls.size() == 4, "legacy projection never charges again");
    }

    private record VerificationCost(String id, List<String> calls, boolean refuse) implements Cost {
        @Override
        public Result<Transaction> prepare(CostContext context) {
            return Result.success(new CostTransaction(new CostTarget.Script(this.id, 0, null), 1.0D) {
                @Override
                protected Result<List<Debit>> perform(boolean simulate) {
                    VerificationCost.this.calls.add((simulate ? "check:" : "pay:") + VerificationCost.this.id);
                    if (simulate) return took(List.of());
                    if (VerificationCost.this.refuse) return refused(CostFailure.SCRIPT_REJECTED);
                    return took(Debit.of(this.target(), this.amount()));
                }

                @Override
                protected CostTransaction replacing(double amount) {
                    throw new UnsupportedOperationException();
                }
            });
        }

        @Override
        public MapCodec<? extends Cost> codec() {
            throw new UnsupportedOperationException();
        }
    }

    private static void timelines() {
        check(timeline(FormulaContext.EMPTY, 1).ticks(new Constant(20)) == 20, "timeline requires no host variable");
        check(timeline(FormulaContext.EMPTY.with("aura_tribulation_modifier", 0.5), 1).ticks(new Constant(20)) == 20,
                "host variable cannot implicitly scale a generic timeline");
        check(timeline(FormulaContext.EMPTY, 1.5).ticks(new Constant(20)) == 30, "explicit scale applies once");
        check(timeline(FormulaContext.EMPTY, 1.5).unscaledTicks(new Constant(20)) == 20, "deadline remains unscaled");
        check(timeline(FormulaContext.EMPTY, 0).ticks(new Constant(20)) == -1, "zero scale is invalid");
        check(timeline(FormulaContext.EMPTY, 1).ticks(new Constant(-1)) == -1, "negative duration is invalid");
        check(timeline(FormulaContext.EMPTY, Double.MAX_VALUE).ticks(new Constant(20)) == -1, "duration overflow is invalid");
    }

    private static TimelineContext timeline(FormulaContext formula, double scale) {
        return new TimelineContext(null, formula, 0, scale, 1, new TimelineState(), new TimelineJump());
    }

    private static void defaults() {
        long count = 0;
        for (Registry<?> registry : SaturnRegistries.all()) {
            if (!(registry instanceof DefaultedRegistry<?> defaulted)) continue;
            check(defaulted.getDefaultKey().getNamespace().equals("saturn"), "default namespace for " + registry.key().identifier());
            count++;
        }
        check(count == 24, "all defaulted registries checked");
        Registry.register(SaturnRegistries.ENTITY_ACTION_TYPE, Identifier.parse("saturn:no_op"), NoOpAction.CODEC);
        check(SaturnRegistries.ENTITY_ACTION_TYPE.getValue(Identifier.parse("verification:missing")) == NoOpAction.CODEC,
                "missing action has a real fallback");
        check(SaturnRegistries.ENTITY_ACTION_TYPE.byId(Integer.MAX_VALUE) == NoOpAction.CODEC, "unknown numeric id falls back");
        check(SaturnRegistries.TRIGGER_TYPE.getDefaultKey().equals(Identifier.parse("saturn:tick")), "trigger default names a built-in type");
    }

    private static void hooks() {
        Identifier first = Identifier.parse("verification:first");
        Identifier second = Identifier.parse("verification:second");
        List<Integer> callbacks = new ArrayList<>();
        AbilityHooks a = new FixtureHooks(2, true, 1, callbacks);
        AbilityHooks b = new FixtureHooks(3, false, 2, callbacks);
        try {
            AbilityHooks.register(first, a);
            AbilityHooks.register(second, b);
            check(AbilityHooks.get().damageMultiplier(null, null) == 6, "different owners' multipliers compose");
            check(!AbilityHooks.get().validate(null, null, FormulaContext.EMPTY), "any provider can deny an ability");
            check(!AbilityHooks.get().canUseItem(null, null), "any provider can deny an item");
            AbilityHooks.get().onCommitted(null, null);
            check(callbacks.equals(List.of(1, 2)), "all commit callbacks run in registration order");
            AbilityHooks.register(first, new FixtureHooks(4, true, 3, callbacks));
            check(AbilityHooks.get().damageMultiplier(null, null) == 12, "replacement affects only one owner");
            AbilityHooks.set(a);
            check(AbilityHooks.get().damageMultiplier(null, null) == 24, "legacy setter cannot replace named providers");
            AbilityHooks.set(null);
            check(AbilityHooks.get().damageMultiplier(null, null) == 12, "legacy removal preserves named providers");
            AbilityHooks.register(second, null);
            check(AbilityHooks.get().damageMultiplier(null, null) == 4, "one provider may be removed independently");
        } finally {
            AbilityHooks.set(null);
            AbilityHooks.register(first, null);
            AbilityHooks.register(second, null);
        }
        check(AbilityHooks.get() == AbilityHooks.NONE, "no providers restores no-op hooks");
    }

    private record FixtureHooks(double multiplier, boolean allowed, int order, List<Integer> callbacks) implements AbilityHooks {
        @Override
        public double damageMultiplier(LivingEntity actor, Holder<Ability> ability) { return this.multiplier; }

        @Override
        public boolean validate(Ability definition, Entity actor, FormulaContext context) { return this.allowed; }

        @Override
        public boolean canUseItem(LivingEntity entity, ItemStack stack) { return this.allowed; }

        @Override
        public void onCommitted(ServerPlayer player, Holder<Ability> ability) { this.callbacks.add(this.order); }
    }

    private static void check(boolean value, String message) {
        if (!value) throw new AssertionError(message);
        checks++;
    }
}
