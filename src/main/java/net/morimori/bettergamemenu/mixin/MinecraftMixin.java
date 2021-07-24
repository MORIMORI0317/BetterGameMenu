package net.morimori.bettergamemenu.mixin;

import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.screens.PauseScreen;
import net.minecraft.client.gui.screens.Screen;
import net.morimori.bettergamemenu.gui.BetterGameMenuScreen;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.ModifyVariable;

@Mixin(Minecraft.class)
public abstract class MinecraftMixin {

    @ModifyVariable(method = "setScreen", ordinal = 0, at = @At("HEAD"))
    public Screen modScreen(Screen screen) {
        if (screen instanceof PauseScreen && ((PauseScreen) screen).showPauseMenu && !(screen instanceof BetterGameMenuScreen))
            return new BetterGameMenuScreen();
        return screen;
    }
}
