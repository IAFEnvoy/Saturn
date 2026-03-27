package com.iafenvoy.saturn.screen;

import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.network.chat.Component;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.MenuProvider;
import net.minecraft.world.entity.player.Inventory;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.inventory.AbstractContainerMenu;
import net.minecraft.world.inventory.MenuType;
//? fabric {
import io.netty.buffer.Unpooled;
import net.fabricmc.fabric.api.menu.v1.ExtendedMenuType;
import net.minecraft.network.codec.ByteBufCodecs;
import java.util.function.Function;
//?} else neoforge {
/*import net.neoforged.neoforge.common.extensions.IMenuTypeExtension;
 *///?}
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.function.Consumer;

@SuppressWarnings("unused")
public final class Menus {
    public static <T extends AbstractContainerMenu> MenuType<T> ofExtended(ExtendedMenuTypeFactory<T> factory) {
        //? fabric {
        return new ExtendedMenuType<>((syncId, inventory, data) -> {
            FriendlyByteBuf buf = new FriendlyByteBuf(Unpooled.wrappedBuffer(data));
            T menu = factory.create(syncId, inventory, buf);
            buf.release();
            return menu;
        }, ByteBufCodecs.BYTE_ARRAY.mapStream(Function.identity()));
        //?} else neoforge {
        /*return IMenuTypeExtension.create(factory::create);
         *///?}
    }

    public static void openExtended(ServerPlayer player, MenuProvider provider, Consumer<FriendlyByteBuf> bufWriter) {
        openExtended(player, new ExtendedMenuProvider() {
            @Override
            public void writeExtraData(FriendlyByteBuf buf) {
                bufWriter.accept(buf);
            }

            @Override
            public @NotNull Component getDisplayName() {
                return provider.getDisplayName();
            }

            @Nullable
            @Override
            public AbstractContainerMenu createMenu(int i, @NotNull Inventory inventory, @NotNull Player player) {
                return provider.createMenu(i, inventory, player);
            }
        });
    }

    public static void openExtended(ServerPlayer player, ExtendedMenuProvider provider) {
        //? fabric {
        player.openMenu(provider);
        //?} else neoforge {
        /*player.openMenu(provider, provider::writeExtraData);
        *///?}
    }

    public static void openMenu(ServerPlayer player, MenuProvider provider) {
        player.openMenu(provider);
    }

    @FunctionalInterface
    public interface ExtendedMenuTypeFactory<T extends AbstractContainerMenu> {
        T create(int id, Inventory inventory, FriendlyByteBuf buf);
    }
}
