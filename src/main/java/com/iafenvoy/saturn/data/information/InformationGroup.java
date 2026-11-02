package com.iafenvoy.saturn.data.information;

import com.iafenvoy.saturn.data.information.model.InfomationGroup;
import com.iafenvoy.saturn.data.information.model.InformationValue;
import com.iafenvoy.saturn.registry.SaturnResourceKeys;
import com.iafenvoy.saturn.util.codec.MiscCodecs;
import com.iafenvoy.saturn.util.codec.RegistryCodecs;
import com.mojang.datafixers.util.Either;
import com.mojang.serialization.Codec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import net.minecraft.core.Holder;
import net.minecraft.network.chat.Component;
import net.minecraft.resources.Identifier;
import net.minecraft.tags.TagKey;

import java.util.List;

/**
 * Groups own membership; each entry selects one registered row or an entire row tag.
 */
public record InformationGroup(Component title, int order,
                               List<Either<Holder<InformationRow>, TagKey<InformationRow>>> rows) {
    public static final Codec<InformationGroup> CODEC = RecordCodecBuilder.create(i -> i.group(
            MiscCodecs.TRANSLATABLE_COMPONENT.fieldOf("title").forGetter(InformationGroup::title),
            Codec.INT.optionalFieldOf("order", 0).forGetter(InformationGroup::order),
            RegistryCodecs.holderOrTag(SaturnResourceKeys.INFORMATION_ROW).listOf().fieldOf("rows").forGetter(InformationGroup::rows)
    ).apply(i, InformationGroup::new));

    public InformationGroup {
        title = new InformationValue.Text(title).text();
        rows = rows.stream().distinct().toList();
    }

    @Override
    public Component title() {
        return new InformationValue.Text(this.title).text();
    }

    public InfomationGroup resolve(Identifier id) {
        return new InfomationGroup(id, this.title, this.order);
    }
}
