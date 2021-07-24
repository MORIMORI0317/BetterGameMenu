package net.morimori.bettergamemenu.mixin;

import net.fabricmc.loader.api.FabricLoader;
import net.minecraft.client.gui.screens.Screen;
import net.minecraft.client.gui.screens.TitleScreen;
import net.minecraft.network.chat.Component;
import net.morimori.bettergamemenu.BetterGameMenu;
import net.morimori.bettergamemenu.gui.JoinLastWorld;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(TitleScreen.class)
public class MixinTitleScreen extends Screen {
    protected MixinTitleScreen(Component component) {
        super(component);
    }

    @Inject(method = "init", at = @At("TAIL"), cancellable = true)
    private void init(CallbackInfo ci) {
        if (!BetterGameMenu.CONFIG.enableJoinLastWorldButton)
            return;

        JoinLastWorld.onGui((TitleScreen) (Object) this, this::addRenderableWidget);
    }
}
