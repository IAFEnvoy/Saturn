package com.iafenvoy.saturn.data.cost;

import com.iafenvoy.saturn.attachment.ResourceHolderAttachment;
import com.iafenvoy.saturn.data.resource.Resource;
import com.iafenvoy.saturn.registry.SaturnRegistries;
import com.iafenvoy.saturn.util.DefinitionText;
import com.iafenvoy.saturn.util.HolderHelper;
import com.iafenvoy.saturn.util.matcher.ItemMatcher;
import com.iafenvoy.saturn.util.matcher.builtin.ItemEntry;
import com.iafenvoy.saturn.util.matcher.builtin.TagEntry;
import net.minecraft.core.Holder;
import net.minecraft.network.chat.Component;
import net.minecraft.network.chat.MutableComponent;
import net.minecraft.tags.TagKey;
import net.minecraft.world.Container;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStackTemplate;
import org.jetbrains.annotations.Nullable;

import java.util.List;
import java.util.UUID;

/**
 * The physical store a bound fee comes out of, used for duplicate diagnostics and re-entrancy guards.
 * One resource names one account balance; pools, banks and accounts remain separate targets.
 *
 * <p>A dependent mod that charges a store Saturn does not know about adds its own target: this interface is open, so a
 * store is expressed as a record of its own, and the two passes of its {@code Cost.Transaction} are what it
 * implements. Saturn only recognises the targets below when it reports or diagnoses a bill.
 */
public interface CostTarget {
    /**
     * One resource balance of one account.
     */
    record Account(@Nullable ResourceHolderAttachment account, Holder<Resource> resource) implements CostTarget {
        @Override
        public String label() {
            return HolderHelper.id(this.resource).toString();
        }

        @Override
        public CostDisplay display(double amount) {
            return CostDisplay.text(Component.translatableWithFallback(DefinitionText.key(this.resource), this.label())
                    .append(" × " + CostDisplay.number(amount)));
        }
    }

    /**
     * One demand on one container, identified by the items it accepts. Different matchers remain distinct targets;
     * duplicate diagnostics do not detect arbitrary overlaps between them.
     */
    record Items(@Nullable Container container, List<ItemMatcher.Entry> entries) implements CostTarget {
        public Items {
            entries = List.copyOf(entries);
        }

        @Override
        public String label() {
            return "an item demand";
        }

        @Override
        public CostDisplay display(double amount) {
            double count = Math.ceil(amount);
            if (this.entries.size() == 1 && this.entries.getFirst() instanceof ItemEntry(Item item))
                return CostDisplay.item(new ItemStackTemplate(item), count);
            MutableComponent alternatives = Component.empty();
            for (ItemMatcher.Entry entry : this.entries) {
                if (!alternatives.getSiblings().isEmpty()) alternatives.append(" / ");
                alternatives.append(describe(entry));
            }
            return CostDisplay.text(Component.translatable("saturn.cost.any_of", alternatives)
                    .append(" × " + CostDisplay.number(count)));
        }

        private static Component describe(ItemMatcher.Entry entry) {
            return switch (entry) {
                case ItemEntry(Item item) -> new ItemStackTemplate(item).create().getHoverName();
                case TagEntry(TagKey<Item> tag) -> Component.literal("#" + tag.location());
                default -> Component.translatable("saturn.cost.custom_item",
                        String.valueOf(SaturnRegistries.ITEM_MATCHER_ENTRY_TYPE.getKey(entry.codec())));
            };
        }
    }

    /**
     * One script call, kept distinct by its entry number. Duplicate diagnostics compare script id and payer instead;
     * the re-entrancy guard is the payer and the script together, so one payer's call is never mistaken for another's.
     */
    record Script(String id, long entry, @Nullable UUID payer) implements CostTarget {
        @Override
        public String label() {
            return "script " + this.id;
        }

        @Override
        public Object guard() {
            return new Scope(this.payer, this.id);
        }

        /**
         * Who is really running one script: the script's own id and the payer it was bound to.
         */
        record Scope(@Nullable UUID payer, String id) {
        }
    }

    /**
     * How this target is named in a log line; also the fallback name when no custom display is provided.
     */
    String label();

    /**
     * Presents a bound amount without evaluating formulas, testing payment or reading the store. Implementations
     * describe their own channel; consumers only collect the returned item previews and text.
     */
    default CostDisplay display(double amount) {
        return CostDisplay.text(Component.literal(this.label() + " × " + CostDisplay.number(amount)));
    }

    /**
     * What the re-entrancy guard holds while this target is being written. Equal guards are the same store: two fees of
     * one payment that would write one store must not run inside each other. It is the target itself except where the
     * identity is deliberately per-entry and the underlying store is not - a script call, whose entry number keeps two
     * calls apart but must not hide a call from itself.
     */
    default Object guard() {
        return this;
    }
}
