package net.morimori.bettergamemenu;

import net.minecraft.client.Minecraft;
import net.minecraft.client.player.LocalPlayer;
import net.morimori.bettergamemenu.gui.JoinLastWorld;

public class ClientHandler {
    private static final Minecraft mc = Minecraft.getInstance();

    public static void join(LocalPlayer player) {
        if (!BetterGameMenu.CONFIG.enableJoinLastWorldButton)
            return;
        if (mc.hasSingleplayerServer()) {
            JoinLastWorld.setLastSinglePlay(mc.getSingleplayerServer().storageSource);
        } else {
            JoinLastWorld.setLastMultiPlay(mc.getCurrentServer());
        }
    }
}
