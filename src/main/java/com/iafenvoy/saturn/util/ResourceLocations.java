package com.iafenvoy.saturn.util;

import net.minecraft.resources.ResourceLocation;

public final class ResourceLocations {
    //? forge {
    /*@SuppressWarnings("removal")
     *///?}
    public static ResourceLocation id(String namespace, String id) {
        //? >=1.21 {
        return ResourceLocation.fromNamespaceAndPath(namespace, id);
        //?} else {
        /*return new ResourceLocation(namespace, id);
         *///?}
    }

    //? forge {
    /*@SuppressWarnings("removal")
     *///?}
    public static ResourceLocation tryParse(String id) {
        try {
            //? >=1.21 {
            return ResourceLocation.parse(id);
            //?} else {
            /*return new ResourceLocation(id);
             *///?}
        } catch (Exception e) {
            return null;
        }
    }
}
