package net.morimori.bettergamemenu.utils;

import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.screens.ConnectScreen;
import net.minecraft.client.gui.screens.GenericDirtMessageScreen;
import net.minecraft.client.gui.screens.Screen;
import net.minecraft.client.multiplayer.ServerData;
import net.minecraft.client.multiplayer.resolver.ServerAddress;
import net.minecraft.network.chat.TranslatableComponent;

public class WorldUtils {
    private static final Minecraft mc = Minecraft.getInstance();

    public static void load(String id) {
        mc.forceSetScreen(new GenericDirtMessageScreen(new TranslatableComponent("selectWorld.data_read")));
        mc.loadLevel(id);
    }

    public static String getWorldId() {
        return mc.getSingleplayerServer().storageSource.getLevelId();
    }

    public static void joinServer(Screen last, ServerData serverData) {
        ConnectScreen.startConnecting(last, mc, ServerAddress.parseString(serverData.ip), serverData);
    }

}
