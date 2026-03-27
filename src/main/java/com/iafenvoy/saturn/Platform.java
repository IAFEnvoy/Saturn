package com.iafenvoy.saturn;

//? fabric {
import net.fabricmc.api.EnvType;
import net.fabricmc.loader.api.FabricLoader;
//?} else neoforge {

/*import net.neoforged.fml.ModList;
import net.neoforged.fml.loading.FMLEnvironment;
*///?}

@SuppressWarnings("unused")
public final class Platform {
    public static Loader getLoader() {
        //? fabric {
        return Loader.FABRIC;
         //?} else neoforge {
        /*return Loader.NEOFORGE;
        *///?}
    }

    public static boolean isFabric() {
        return getLoader() == Loader.FABRIC;
    }

    public static boolean isNeoForge() {
        return getLoader() == Loader.NEOFORGE;
    }

    public static Side getSide() {
        //? fabric {
        return FabricLoader.getInstance().getEnvironmentType() == EnvType.CLIENT ? Side.CLIENT : Side.SERVER;
         //?} else {
        /*return FMLEnvironment.getDist().isClient() ? Side.CLIENT : Side.SERVER;
        *///?}
    }

    public static boolean isDevelopmentEnvironment() {
        //? fabric {
        return FabricLoader.getInstance().isDevelopmentEnvironment();
         //?} else {
        /*return !FMLEnvironment.isProduction();
        *///?}
    }

    public static boolean isModLoaded(String modId) {
        //? fabric {
        return FabricLoader.getInstance().isModLoaded(modId);
         //?} else {
        /*return ModList.get().isLoaded(modId);
        *///?}
    }

    public enum Loader {
        FABRIC, NEOFORGE
    }

    public enum Side {
        CLIENT, SERVER;

        public boolean isClient() {
            return this == CLIENT;
        }

        public boolean isServer() {
            return this == SERVER;
        }
    }
}
