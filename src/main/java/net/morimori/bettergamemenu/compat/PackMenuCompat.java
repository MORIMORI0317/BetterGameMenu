package net.morimori.bettergamemenu.compat;

import net.minecraftforge.fml.ModList;

public class PackMenuCompat {
    public static boolean isLoaded() {
        return ModList.get().isLoaded("packmenu");
    }
}
