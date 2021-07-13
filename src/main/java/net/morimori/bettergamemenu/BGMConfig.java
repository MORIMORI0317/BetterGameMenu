package net.morimori.bettergamemenu;

import me.shedaniel.autoconfig.ConfigData;
import me.shedaniel.autoconfig.annotation.Config;

@Config(name = BetterGameMenu.MODID)
public class BGMConfig implements ConfigData {
    public boolean enableModOptions = true;
    public boolean enableRFARB = true;
    public boolean enableHideUnnecessaryShareToLan = true;
    public boolean enableRejoinButton = true;
    public boolean enableModMenuCompat = true;
}
