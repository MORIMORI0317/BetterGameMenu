package net.morimori.bettergamemenu;

import net.minecraft.client.gui.screen.IngameMenuScreen;
import net.minecraftforge.client.event.GuiOpenEvent;
import net.minecraftforge.eventbus.api.SubscribeEvent;

public class ClientHandler {
    @SubscribeEvent
    public static void onOpenGUI(GuiOpenEvent e) {
        if (e.getGui() instanceof IngameMenuScreen && ((IngameMenuScreen) e.getGui()).showPauseMenu && !(e.getGui() instanceof BetterGameMenuScreen)) {
            e.setGui(new BetterGameMenuScreen());
        }
    }
}
