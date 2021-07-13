package net.morimori.bettergamemenu.mixin;

import com.terraformersmc.modmenu.event.ModMenuEventHandler;
import net.minecraft.client.gui.screens.Screen;
import net.morimori.bettergamemenu.BetterGameMenu;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(ModMenuEventHandler.class)
public class ModMenuEventHandlerMixin {
    @Inject(method = "afterGameMenuScreenInit", at = @At("HEAD"), cancellable = true, remap = false)
    private static void init(Screen screen, CallbackInfo ci) {
        if (BetterGameMenu.CONFIG.enableModMenuCompat)
            ci.cancel();
    }
}
