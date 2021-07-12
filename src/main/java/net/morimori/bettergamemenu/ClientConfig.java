package net.morimori.bettergamemenu;

import net.minecraftforge.common.ForgeConfigSpec;
import net.minecraftforge.fml.ModLoadingContext;
import net.minecraftforge.fml.config.ModConfig;
import org.apache.commons.lang3.tuple.Pair;

public class ClientConfig {
    public static ForgeConfigSpec.ConfigValue<Boolean> EnableModOptions;
    public static ForgeConfigSpec.ConfigValue<Boolean> ShowNotificationModUpdate;
    public static ForgeConfigSpec.ConfigValue<Boolean> RenderModListBackground;
    public static ForgeConfigSpec.ConfigValue<Boolean> EnableRFARB;
    public static ForgeConfigSpec.ConfigValue<Boolean> EnableHideUnnecessaryShareToLan;
    public static ForgeConfigSpec.ConfigValue<Boolean> EnableRejoinButton;

    public static void init() {
        Pair<ConfigLoder, ForgeConfigSpec> client_config = new ForgeConfigSpec.Builder().configure(ConfigLoder::new);
        ModLoadingContext.get().registerConfig(ModConfig.Type.CLIENT, client_config.getRight());
    }

    public static class ConfigLoder {
        public ConfigLoder(ForgeConfigSpec.Builder builder) {
            builder.push("General");
            builder.push("Mod Options Button");
            EnableModOptions = builder.define("Enable mod options button", true);
            ShowNotificationModUpdate = builder.define("Show notification mod update", true);
            RenderModListBackground = builder.define("Render mod list background", true);
            builder.pop();
            EnableRFARB = builder.define("Remove feedback and report bugs button", true);
            EnableHideUnnecessaryShareToLan = builder.define("Hide unnecessary share lan button", true);
            EnableRejoinButton = builder.define("Enable rejoin button", true);
            builder.pop();
        }
    }
}