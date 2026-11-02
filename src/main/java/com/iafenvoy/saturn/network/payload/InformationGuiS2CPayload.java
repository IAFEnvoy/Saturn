package com.iafenvoy.saturn.network.payload;

import com.iafenvoy.saturn.Saturn;
import com.iafenvoy.saturn.data.information.InformationGroup;
import com.iafenvoy.saturn.data.information.model.InformationDocument;
import net.minecraft.core.Holder;
import net.minecraft.network.RegistryFriendlyByteBuf;
import net.minecraft.network.codec.ByteBufCodecs;
import net.minecraft.network.codec.StreamCodec;
import net.minecraft.network.protocol.common.custom.CustomPacketPayload;
import net.minecraft.resources.Identifier;
import org.jspecify.annotations.NonNull;

import java.util.List;
import java.util.Set;

/**
 * Sends evaluated rows and the group selection to the command executor, without exposing a raw entity snapshot.
 */
public record InformationGuiS2CPayload(InformationDocument document,
                                       Set<Holder<InformationGroup>> selectedGroups) implements CustomPacketPayload {
    public static final Type<InformationGuiS2CPayload> TYPE = new Type<>(Identifier.fromNamespaceAndPath(Saturn.MOD_ID, "information_gui"));
    public static final StreamCodec<RegistryFriendlyByteBuf, InformationGuiS2CPayload> STREAM_CODEC = StreamCodec.composite(
            ByteBufCodecs.fromCodecWithRegistries(InformationDocument.CODEC), InformationGuiS2CPayload::document,
            ByteBufCodecs.fromCodecWithRegistries(InformationGroup.CODEC.listOf().xmap(Set::copyOf, List::copyOf)), InformationGuiS2CPayload::selectedGroups,
            InformationGuiS2CPayload::new);

    public InformationGuiS2CPayload {
        selectedGroups = Set.copyOf(selectedGroups);
    }

    @Override
    public @NonNull Type<InformationGuiS2CPayload> type() {
        return TYPE;
    }
}
