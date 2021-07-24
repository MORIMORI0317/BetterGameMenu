package net.morimori.bettergamemenu.gui;

import com.mojang.realmsclient.RealmsMainScreen;
import net.fabricmc.loader.api.FabricLoader;
import net.minecraft.SharedConstants;
import net.minecraft.Util;
import net.minecraft.client.gui.components.AbstractWidget;
import net.minecraft.client.gui.components.Button;
import net.minecraft.client.gui.components.ImageButton;
import net.minecraft.client.gui.components.Widget;
import net.minecraft.client.gui.screens.*;
import net.minecraft.client.gui.screens.achievement.StatsScreen;
import net.minecraft.client.gui.screens.advancements.AdvancementsScreen;
import net.minecraft.client.gui.screens.multiplayer.JoinMultiplayerScreen;
import net.minecraft.client.multiplayer.ServerData;
import net.minecraft.client.multiplayer.resolver.ServerAddress;
import net.minecraft.network.chat.TranslatableComponent;
import net.minecraft.resources.ResourceLocation;
import net.morimori.bettergamemenu.BetterGameMenu;
import net.morimori.bettergamemenu.integration.ModMenuIntegration;
import net.morimori.bettergamemenu.utils.WorldUtils;

public class BetterGameMenuScreen extends PauseScreen {
    private static final ResourceLocation WIDGETS = new ResourceLocation(BetterGameMenu.MODID, "textures/gui/widgets.png");

    public BetterGameMenuScreen() {
        super(true);
    }

    @Override
    protected void init() {
        createButtons();
    }

    private void createButtons() {
        Button backButton = this.addRenderableWidget(new Button(this.width / 2 - 102, this.height / 4 + 24 + -16, 204, 20, new TranslatableComponent("menu.returnToGame"), (buttonx) -> {
            this.minecraft.setScreen(null);
            this.minecraft.mouseHandler.grabMouse();
        }));
        Button advncButton = this.addRenderableWidget(new Button(this.width / 2 - 102, this.height / 4 + 48 + -16, 98, 20, new TranslatableComponent("gui.advancements"), (buttonx) -> {
            this.minecraft.setScreen(new AdvancementsScreen(this.minecraft.player.connection.getAdvancements()));
        }));
        Button stateButton = this.addRenderableWidget(new Button(this.width / 2 + 4, this.height / 4 + 48 + -16, 98, 20, new TranslatableComponent("gui.stats"), (buttonx) -> {
            this.minecraft.setScreen(new StatsScreen(this, this.minecraft.player.getStats()));
        }));
        String string = SharedConstants.getCurrentVersion().isStable() ? "https://aka.ms/javafeedback?ref=game" : "https://aka.ms/snapshotfeedback?ref=game";
        Button gfButton = this.addRenderableWidget(new Button(this.width / 2 - 102, this.height / 4 + 72 + -16, 98, 20, new TranslatableComponent("menu.sendFeedback"), (buttonx) -> {
            this.minecraft.setScreen(new ConfirmLinkScreen((bl) -> {
                if (bl) {
                    Util.getPlatform().openUri(string);
                }

                this.minecraft.setScreen(this);
            }, string, true));
        }));
        gfButton.active = gfButton.visible = !isRemoveGFARB();

        Button rbButton = this.addRenderableWidget(new Button(this.width / 2 + 4, this.height / 4 + 72 + -16, 98, 20, new TranslatableComponent("menu.reportBugs"), (buttonx) -> {
            this.minecraft.setScreen(new ConfirmLinkScreen((bl) -> {
                if (bl) {
                    Util.getPlatform().openUri("https://aka.ms/snapshotbugs?ref=game");
                }

                this.minecraft.setScreen(this);
            }, "https://aka.ms/snapshotbugs?ref=game", true));
        }));
        rbButton.active = rbButton.visible = !isRemoveGFARB();

        Button options = this.addRenderableWidget(new Button(this.width / 2 - 102, this.height / 4 + 96 + -16, 98, 20, new TranslatableComponent("menu.options"), (buttonx) -> {
            this.minecraft.setScreen(new OptionsScreen(this, this.minecraft.options));
        }));
        Button shareToLan = (Button) this.addRenderableWidget(new Button(this.width / 2 + 4, this.height / 4 + 96 + -16, 98, 20, new TranslatableComponent("menu.shareToLan"), (buttonx) -> {
            this.minecraft.setScreen(new ShareToLanScreen(this));
        }));

        if (isHideUnnecessaryShareToLan())
            shareToLan.visible = shareToLan.active = isNonOpenLan();
        else
            shareToLan.active = isNonOpenLan();

        Button button1 = this.addRenderableWidget(new Button(this.width / 2 - 102, this.height / 4 + 120 + -16, 204, 20, new TranslatableComponent("menu.returnToMenu"), (buttonx) -> {
            boolean bl = this.minecraft.isLocalServer();
            boolean bl2 = this.minecraft.isConnectedToRealms();
            buttonx.active = false;
            this.minecraft.level.disconnect();
            if (bl) {
                this.minecraft.clearLevel(new GenericDirtMessageScreen(new TranslatableComponent("menu.savingLevel")));
            } else {
                this.minecraft.clearLevel();
            }

            TitleScreen titleScreen = new TitleScreen();
            if (bl) {
                this.minecraft.setScreen(titleScreen);
            } else if (bl2) {
                this.minecraft.setScreen(new RealmsMainScreen(titleScreen));
            } else {
                this.minecraft.setScreen(new JoinMultiplayerScreen(titleScreen));
            }
        }));
        if (!this.minecraft.isLocalServer()) {
            button1.setMessage(new TranslatableComponent("menu.disconnect"));
        }

        boolean isDownButtonFlg = isHideUnnecessaryShareToLan() ? !isRemoveGFARB() && isHideUnnecessaryShareToLan() && isNonOpenLan() : !isRemoveGFARB();

        if (isModOptions()) {
            Button modButton = this.addRenderableWidget(new Button(this.width / 2 + 4, this.height / 4 + (isDownButtonFlg ? 120 : 96) - 16, 98, 20, new TranslatableComponent("menu.modoption"), n -> ModMenuIntegration.openModScree(this)));
            modButton.active = modButton.visible = isModOptions();
        }

        Button rejoinButton = this.addRenderableWidget(new ImageButton(button1.x + button1.getWidth() + 8, button1.y, 20, 20, this.minecraft.isLocalServer() ? 0 : 20, 0, 20, WIDGETS, 256, 256, n -> {
            String id = "";
            ServerData serverData = null;
            boolean bl = this.minecraft.isLocalServer();
            if (bl)
                id = WorldUtils.getWorldId();
            boolean bl2 = this.minecraft.isConnectedToRealms();
            if (!bl && !bl2)
                serverData = this.minecraft.getCurrentServer();

            n.active = false;
            this.minecraft.level.disconnect();
            if (bl) {
                this.minecraft.clearLevel(new GenericDirtMessageScreen(new TranslatableComponent("menu.savingLevel")));
            } else {
                this.minecraft.clearLevel();
            }

            TitleScreen titleScreen = new TitleScreen();
            if (bl) {
                try {
                    WorldUtils.load(id);
                } catch (Exception ex) {
                    this.minecraft.setScreen(titleScreen);
                }
            } else if (bl2) {
                this.minecraft.setScreen(new RealmsMainScreen(titleScreen));
            } else {
                try {
                    WorldUtils.joinServer(new JoinMultiplayerScreen(titleScreen), serverData);
                } catch (Exception ex) {
                    this.minecraft.setScreen(new JoinMultiplayerScreen(titleScreen));
                }
            }

        }, (button, poseStack, x, y) -> this.renderTooltip(poseStack, this.minecraft.font.split(new TranslatableComponent("gui.button.rejoin"), Math.max(this.width / 2 - 43, 170)), x, y), new TranslatableComponent("gui.button.rejoin")));
        rejoinButton.active = rejoinButton.visible = isRejoinButton();
        if (isModOptions()) {
            //    if (isShowUpdate())
            //        modUpdateNotification = initModUpdate(modButton);
            shareToLan.x = width / 2 - 102;
            shareToLan.setWidth(204);

            if (isRemoveGFARB())
                shareToLan.y -= 24;

            if (!isModMenuCompat() && FabricLoader.getInstance().isModLoaded("modmenu")) {
                shareToLan.y -= 24;
            }

            if (isDownButtonFlg) {
                options.y += 24;
                rejoinButton.y += 24;
                button1.y += 24;
                for (Widget widget : this.renderables) {
                    if (widget instanceof AbstractWidget) {
                        ((AbstractWidget) widget).y -= 16;
                    }
                }
            }
        } else {
            if (isHideUnnecessaryShareToLan() && !isNonOpenLan())
                options.setWidth(204);
        }

    }

    private boolean isRemoveGFARB() {
        return BetterGameMenu.CONFIG.enableRFARB;
    }

    private boolean isModOptions() {
        return BetterGameMenu.CONFIG.enableModOptions && FabricLoader.getInstance().isModLoaded("modmenu") && isModMenuCompat();
    }

    private boolean isShowUpdate() {
        return isModOptions() && false;
    }

    private boolean isHideUnnecessaryShareToLan() {
        return BetterGameMenu.CONFIG.enableHideUnnecessaryShareToLan;
    }

    private boolean isNonOpenLan() {
        return this.minecraft.hasSingleplayerServer() && !this.minecraft.getSingleplayerServer().isPublished();
    }

    private boolean isRejoinButton() {
        return BetterGameMenu.CONFIG.enableRejoinButton;
    }

    private boolean isModMenuCompat() {
        return BetterGameMenu.CONFIG.enableModMenuCompat;
    }
}
