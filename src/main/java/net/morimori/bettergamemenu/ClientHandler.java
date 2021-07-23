package net.morimori.bettergamemenu;

import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.screens.PauseScreen;
import net.minecraft.client.gui.screens.TitleScreen;
import net.minecraft.client.player.LocalPlayer;
import net.minecraftforge.client.event.GuiOpenEvent;
import net.minecraftforge.client.event.GuiScreenEvent;
import net.minecraftforge.event.entity.EntityJoinWorldEvent;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.morimori.bettergamemenu.compat.FancyMenuCompat;
import net.morimori.bettergamemenu.compat.PackMenuCompat;
import net.morimori.bettergamemenu.gui.BetterGameMenuScreen;
import net.morimori.bettergamemenu.gui.JoinLastWorld;

public class ClientHandler {
    private static final Minecraft mc = Minecraft.getInstance();

    @SubscribeEvent
    public static void onOpenGUI(GuiOpenEvent e) {
        if (e.getGui() instanceof PauseScreen && ((PauseScreen) e.getGui()).showPauseMenu && !(e.getGui() instanceof BetterGameMenuScreen)) {
            e.setGui(new BetterGameMenuScreen());
        }

    }

    @SubscribeEvent
    public static void onGUIInit(GuiScreenEvent.InitGuiEvent.Post e) {
        if (!ClientConfig.EnableJoinLastWorldButton.get() || PackMenuCompat.isLoaded() || FancyMenuCompat.isLoaded())
            return;
        if (e.getGui() instanceof TitleScreen) {
            JoinLastWorld.onGui(e);
        }
    }

    @SubscribeEvent
    public static void onWorldJoin(EntityJoinWorldEvent e) {
        if (e.getWorld().isClientSide() && e.getEntity() instanceof LocalPlayer) {
            if (!ClientConfig.EnableJoinLastWorldButton.get() || PackMenuCompat.isLoaded() || FancyMenuCompat.isLoaded())
                return;
            if (mc.hasSingleplayerServer()) {
                JoinLastWorld.setLastSinglePlay(mc.getSingleplayerServer().storageSource);
            } else {
                JoinLastWorld.setLastMultiPlay(mc.getCurrentServer());
            }
        }
    }

}