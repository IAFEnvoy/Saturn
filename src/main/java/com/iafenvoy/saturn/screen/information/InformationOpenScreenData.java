package com.iafenvoy.saturn.screen.information;

import com.iafenvoy.saturn.data.information.InformationGroup;
import com.iafenvoy.saturn.data.information.model.InformationDocument;
import com.iafenvoy.saturn.screen.opener.OpenScreenData;
import com.iafenvoy.saturn.util.codec.MiscStreamCodecs;
import net.minecraft.core.Holder;
import net.minecraft.network.RegistryFriendlyByteBuf;
import net.minecraft.network.codec.ByteBufCodecs;
import net.minecraft.network.codec.StreamCodec;

import java.util.Set;

public record InformationOpenScreenData(InformationDocument document,
                                        Set<Holder<InformationGroup>> selectedGroups) implements OpenScreenData {
    public static final StreamCodec<RegistryFriendlyByteBuf, InformationOpenScreenData> STREAM_CODEC = StreamCodec.composite(
            ByteBufCodecs.fromCodecWithRegistries(InformationDocument.CODEC), InformationOpenScreenData::document,
            InformationGroup.STREAM_CODEC.apply(MiscStreamCodecs.set()), InformationOpenScreenData::selectedGroups,
            InformationOpenScreenData::new);

    @Override
    public StreamCodec<RegistryFriendlyByteBuf, ? extends OpenScreenData> streamCodec() {
        return STREAM_CODEC;
    }
}
