package net.morimori.bettergamemenu.compat;

import net.minecraftforge.fml.ModList;

public class FancyMenuCompat {
    public static boolean isLoaded() {
        return ModList.get().isLoaded("fancymenu");
    }
}
