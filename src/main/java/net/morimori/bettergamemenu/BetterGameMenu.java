package net.morimori.bettergamemenu;

import me.shedaniel.autoconfig.AutoConfig;
import me.shedaniel.autoconfig.serializer.Toml4jConfigSerializer;
import net.fabricmc.api.ClientModInitializer;

//Add translation by Eric-01. Thank You!
public class BetterGameMenu implements ClientModInitializer {
    public static final String MODID = "bettergamemenu";
    public static BGMConfig CONFIG;

    @Override
    public void onInitializeClient() {
        CONFIG = AutoConfig.register(BGMConfig.class, Toml4jConfigSerializer::new).getConfig();
    }
}
