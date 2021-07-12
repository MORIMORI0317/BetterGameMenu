package net.morimori.bettergamemenu;

import net.minecraftforge.common.MinecraftForge;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.fml.event.lifecycle.FMLClientSetupEvent;
import net.minecraftforge.fml.javafmlmod.FMLJavaModLoadingContext;

//Add translation by Eric-01. Thank You!
@Mod(BetterGameMenu.MODID)
public class BetterGameMenu {
    public static final String MODID = "bettergamemenu";

    public BetterGameMenu() {
        ClientConfig.init();
        FMLJavaModLoadingContext.get().getModEventBus().addListener(this::doClientStuff);
    }

    private void doClientStuff(final FMLClientSetupEvent event) {
        MinecraftForge.EVENT_BUS.register(ClientHandler.class);
    }
}
