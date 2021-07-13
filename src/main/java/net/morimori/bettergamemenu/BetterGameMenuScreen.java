package net.morimori.bettergamemenu;

import net.fabricmc.loader.api.FabricLoader;
import net.minecraft.SharedConstants;
import net.minecraft.Util;
import net.minecraft.client.gui.components.AbstractWidget;
import net.minecraft.client.gui.components.Button;
import net.minecraft.client.gui.components.ImageButton;
import net.minecraft.client.gui.screens.*;
import net.minecraft.client.gui.screens.achievement.StatsScreen;
import net.minecraft.client.gui.screens.advancements.AdvancementsScreen;
import net.minecraft.client.gui.screens.multiplayer.JoinMultiplayerScreen;
import net.minecraft.client.multiplayer.ServerData;
import net.minecraft.network.chat.TranslatableComponent;
import net.minecraft.realms.RealmsBridge;
import net.minecraft.resources.ResourceLocation;
import net.morimori.bettergamemenu.integration.ModMenuIntegration;

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
        Button backButton = this.addButton(new Button(this.width / 2 - 102, this.height / 4 + 24 + -16, 204, 20, new TranslatableComponent("menu.returnToGame"), (buttonx) -> {
            this.minecraft.setScreen(null);
            this.minecraft.mouseHandler.grabMouse();
        }));
        Button advncButton = this.addButton(new Button(this.width / 2 - 102, this.height / 4 + 48 + -16, 98, 20, new TranslatableComponent("gui.advancements"), (buttonx) -> {
            this.minecraft.setScreen(new AdvancementsScreen(this.minecraft.player.connection.getAdvancements()));
        }));
        Button stateButton = this.addButton(new Button(this.width / 2 + 4, this.height / 4 + 48 + -16, 98, 20, new TranslatableComponent("gui.stats"), (buttonx) -> {
            this.minecraft.setScreen(new StatsScreen(this, this.minecraft.player.getStats()));
        }));
        String string = SharedConstants.getCurrentVersion().isStable() ? "https://aka.ms/javafeedback?ref=game" : "https://aka.ms/snapshotfeedback?ref=game";
        Button gfButton = this.addButton(new Button(this.width / 2 - 102, this.height / 4 + 72 + -16, 98, 20, new TranslatableComponent("menu.sendFeedback"), (buttonx) -> {
            this.minecraft.setScreen(new ConfirmLinkScreen((bl) -> {
                if (bl) {
                    Util.getPlatform().openUri(string);
                }

                this.minecraft.setScreen(this);
            }, string, true));
        }));
        gfButton.active = gfButton.visible = !isRemoveGFARB();

        Button rbButton = this.addButton(new Button(this.width / 2 + 4, this.height / 4 + 72 + -16, 98, 20, new TranslatableComponent("menu.reportBugs"), (buttonx) -> {
            this.minecraft.setScreen(new ConfirmLinkScreen((bl) -> {
                if (bl) {
                    Util.getPlatform().openUri("https://aka.ms/snapshotbugs?ref=game");
                }

                this.minecraft.setScreen(this);
            }, "https://aka.ms/snapshotbugs?ref=game", true));
        }));
        rbButton.active = rbButton.visible = !isRemoveGFARB();

        Button options = this.addButton(new Button(this.width / 2 - 102, this.height / 4 + 96 + -16, 98, 20, new TranslatableComponent("menu.options"), (buttonx) -> {
            this.minecraft.setScreen(new OptionsScreen(this, this.minecraft.options));
        }));
        Button shareToLan = (Button) this.addButton(new Button(this.width / 2 + 4, this.height / 4 + 96 + -16, 98, 20, new TranslatableComponent("menu.shareToLan"), (buttonx) -> {
            this.minecraft.setScreen(new ShareToLanScreen(this));
        }));

        if (isHideUnnecessaryShareToLan())
            shareToLan.visible = shareToLan.active = isNonOpenLan();
        else
            shareToLan.active = isNonOpenLan();

        Button button1 = (Button) this.addButton(new Button(this.width / 2 - 102, this.height / 4 + 120 + -16, 204, 20, new TranslatableComponent("menu.returnToMenu"), (buttonx) -> {
            boolean bl = this.minecraft.isLocalServer();
            boolean bl2 = this.minecraft.isConnectedToRealms();
            buttonx.active = false;
            this.minecraft.level.disconnect();
            if (bl) {
                this.minecraft.clearLevel(new GenericDirtMessageScreen(new TranslatableComponent("menu.savingLevel")));
            } else {
                this.minecraft.clearLevel();
            }

            if (bl) {
                this.minecraft.setScreen(new TitleScreen());
            } else if (bl2) {
                RealmsBridge realmsBridge = new RealmsBridge();
                realmsBridge.switchToRealms(new TitleScreen());
            } else {
                this.minecraft.setScreen(new JoinMultiplayerScreen(new TitleScreen()));
            }

        }));
        if (!this.minecraft.isLocalServer()) {
            button1.setMessage(new TranslatableComponent("menu.disconnect"));
        }

        boolean isDownButtonFlg = isHideUnnecessaryShareToLan() ? !isRemoveGFARB() && isHideUnnecessaryShareToLan() && isNonOpenLan() : !isRemoveGFARB();

        if (isModOptions()) {
            Button modButton = this.addButton(new Button(this.width / 2 + 4, this.height / 4 + (isDownButtonFlg ? 120 : 96) - 16, 98, 20, new TranslatableComponent("menu.modoption"), n -> ModMenuIntegration.openModScree(this)));
            modButton.active = modButton.visible = isModOptions();
        }

        Button rejoinButton = this.addButton(new ImageButton(button1.x + button1.getWidth() + 8, button1.y, 20, 20, 0, 0, 20, WIDGETS, n -> {
            String id = "";
            ServerData serverData = null;
            boolean bl = this.minecraft.isLocalServer();
            if (bl)
                id = minecraft.getSingleplayerServer().storageSource.getLevelId();
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

            if (bl) {
                try {
                    this.minecraft.loadLevel(id);
                } catch (Exception ex) {
                    this.minecraft.setScreen(new TitleScreen());
                }
            } else if (bl2) {
                RealmsBridge realmsBridge = new RealmsBridge();
                realmsBridge.switchToRealms(new TitleScreen());
            } else {
                try {
                    this.minecraft.setScreen(new ConnectScreen(this, this.minecraft, serverData));
                } catch (Exception ex) {
                    this.minecraft.setScreen(new JoinMultiplayerScreen(new TitleScreen()));
                }
            }

        }));
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
                for (AbstractWidget widget : this.buttons) {
                    widget.y -= 16;
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
