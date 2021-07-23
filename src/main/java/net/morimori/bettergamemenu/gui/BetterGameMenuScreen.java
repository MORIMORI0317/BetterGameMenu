package net.morimori.bettergamemenu.gui;

import com.mojang.blaze3d.vertex.PoseStack;
import com.mojang.realmsclient.RealmsMainScreen;
import net.minecraft.SharedConstants;
import net.minecraft.Util;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.components.AbstractWidget;
import net.minecraft.client.gui.components.Button;
import net.minecraft.client.gui.components.ImageButton;
import net.minecraft.client.gui.components.Widget;
import net.minecraft.client.gui.screens.*;
import net.minecraft.client.gui.screens.achievement.StatsScreen;
import net.minecraft.client.gui.screens.advancements.AdvancementsScreen;
import net.minecraft.client.gui.screens.multiplayer.JoinMultiplayerScreen;
import net.minecraft.client.multiplayer.ServerData;
import net.minecraft.network.chat.Component;
import net.minecraft.network.chat.TranslatableComponent;
import net.minecraftforge.client.gui.NotificationModUpdateScreen;
import net.minecraftforge.fmlclient.gui.screen.ModListScreen;
import net.morimori.bettergamemenu.BetterGameMenu;
import net.morimori.bettergamemenu.ClientConfig;
import net.morimori.bettergamemenu.utils.WorldUtils;

public class BetterGameMenuScreen extends PauseScreen {
    private NotificationModUpdateScreen modUpdateNotification;

    public BetterGameMenuScreen() {
        super(true);
    }

    @Override
    protected void init() {
        createButtons();
    }

    private void createButtons() {
        this.addRenderableWidget(new Button(this.width / 2 - 102, this.height / 4 + 24 + -16, 204, 20, new TranslatableComponent("menu.returnToGame"), (p_96337_) -> {
            this.minecraft.setScreen((Screen) null);
            this.minecraft.mouseHandler.grabMouse();
        }));
        this.addRenderableWidget(new Button(this.width / 2 - 102, this.height / 4 + 48 + -16, 98, 20, new TranslatableComponent("gui.advancements"), (p_96335_) -> {
            this.minecraft.setScreen(new AdvancementsScreen(this.minecraft.player.connection.getAdvancements()));
        }));
        this.addRenderableWidget(new Button(this.width / 2 + 4, this.height / 4 + 48 + -16, 98, 20, new TranslatableComponent("gui.stats"), (p_96333_) -> {
            this.minecraft.setScreen(new StatsScreen(this, this.minecraft.player.getStats()));
        }));

        String s = SharedConstants.getCurrentVersion().isStable() ? "https://aka.ms/javafeedback?ref=game" : "https://aka.ms/snapshotfeedback?ref=game";
        Button gfButton = this.addRenderableWidget(new Button(this.width / 2 - 102, this.height / 4 + 72 + -16, 98, 20, new TranslatableComponent("menu.sendFeedback"), (p_96318_) -> {
            this.minecraft.setScreen(new ConfirmLinkScreen((p_169337_) -> {
                if (p_169337_) {
                    Util.getPlatform().openUri(s);
                }

                this.minecraft.setScreen(this);
            }, s, true));
        }));

        gfButton.active = gfButton.visible = !isRemoveGFARB();

        Button rbButton = this.addRenderableWidget(new Button(this.width / 2 + 4, this.height / 4 + 72 + -16, 98, 20, new TranslatableComponent("menu.reportBugs"), (p_96331_) -> {
            this.minecraft.setScreen(new ConfirmLinkScreen((p_169339_) -> {
                if (p_169339_) {
                    Util.getPlatform().openUri("https://aka.ms/snapshotbugs?ref=game");
                }

                this.minecraft.setScreen(this);
            }, "https://aka.ms/snapshotbugs?ref=game", true));
        }));
        rbButton.active = rbButton.visible = !isRemoveGFARB();

        Button options = this.addRenderableWidget(new Button(this.width / 2 - 102, this.height / 4 + 96 + -16, 98, 20, new TranslatableComponent("menu.options"), (p_96323_) -> {
            this.minecraft.setScreen(new OptionsScreen(this, this.minecraft.options));
        }));
        Button shareToLan = this.addRenderableWidget(new Button(this.width / 2 + 4, this.height / 4 + 96 + -16, 98, 20, new TranslatableComponent("menu.shareToLan"), (p_96321_) -> {
            this.minecraft.setScreen(new ShareToLanScreen(this));
        }));

        if (isHideUnnecessaryShareToLan())
            shareToLan.visible = shareToLan.active = isNonOpenLan();
        else
            shareToLan.active = isNonOpenLan();

        Component component = this.minecraft.isLocalServer() ? new TranslatableComponent("menu.returnToMenu") : new TranslatableComponent("menu.disconnect");
        Button retunToMenu = this.addRenderableWidget(new Button(this.width / 2 - 102, this.height / 4 + 120 + -16, 204, 20, component, (p_96315_) -> {
            boolean flag = this.minecraft.isLocalServer();
            boolean flag1 = this.minecraft.isConnectedToRealms();
            p_96315_.active = false;
            this.minecraft.level.disconnect();
            if (flag) {
                this.minecraft.clearLevel(new GenericDirtMessageScreen(new TranslatableComponent("menu.savingLevel")));
            } else {
                this.minecraft.clearLevel();
            }

            TitleScreen titlescreen = new TitleScreen();
            if (flag) {
                this.minecraft.setScreen(titlescreen);
            } else if (flag1) {
                this.minecraft.setScreen(new RealmsMainScreen(titlescreen));
            } else {
                this.minecraft.setScreen(new JoinMultiplayerScreen(titlescreen));
            }

        }));

        boolean isDownButtonFlg = isHideUnnecessaryShareToLan() ? !isRemoveGFARB() && isHideUnnecessaryShareToLan() && isNonOpenLan() : !isRemoveGFARB();

        Button modButton = this.addRenderableWidget(new Button(this.width / 2 + 4, this.height / 4 + (isDownButtonFlg ? 120 : 96) - 16, 98, 20, new TranslatableComponent("menu.modoption"), (n) -> Minecraft.getInstance().setScreen(new ModListScreen(this))));
        modButton.active = modButton.visible = isModOptions();


        Button rejoinButton = this.addRenderableWidget(new ImageButton(retunToMenu.x + retunToMenu.getWidth() + 8, retunToMenu.y, 20, 20, this.minecraft.isLocalServer() ? 0 : 20, 0, 20, BetterGameMenu.WIDGETS, 256, 256, n -> {
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
        }, (button, poseStack, x, y) -> {
            this.renderTooltip(poseStack, this.minecraft.font.split(new TranslatableComponent("gui.button.rejoin"), Math.max(this.width / 2 - 43, 170)), x, y);
        }, new TranslatableComponent("gui.button.rejoin")));
        rejoinButton.active = rejoinButton.visible = isRejoinButton();

        if (isModOptions()) {
            if (isShowUpdate())
                modUpdateNotification = initModUpdate(modButton);
            shareToLan.x = width / 2 - 102;
            shareToLan.setWidth(204);
            if (isRemoveGFARB())
                shareToLan.y -= 24;
            if (isDownButtonFlg) {
                options.y += 24;
                rejoinButton.y += 24;
                retunToMenu.y += 24;
                for (Widget widget : this.renderables) {
                    if (widget instanceof AbstractWidget)
                        ((AbstractWidget) widget).y -= 16;
                }
            }
        } else {
            if (isHideUnnecessaryShareToLan() && !isNonOpenLan())
                options.setWidth(204);
        }
    }

    private NotificationModUpdateScreen initModUpdate(Button modButton) {
        NotificationModUpdateScreen notificationModUpdateScreen = new NotificationModUpdateScreen(modButton);
        notificationModUpdateScreen.resize(this.getMinecraft(), this.width, this.height);
        notificationModUpdateScreen.init();
        return notificationModUpdateScreen;
    }

    @Override
    public void render(PoseStack poseStack, int mouseX, int mouseY, float partialTicks) {
        super.render(poseStack, mouseX, mouseY, partialTicks);
        if (isShowUpdate() && modUpdateNotification != null)
            modUpdateNotification.render(poseStack, mouseX, mouseY, partialTicks);
    }

    private boolean isRemoveGFARB() {
        return ClientConfig.EnableRFARB.get();
    }

    private boolean isModOptions() {
        return ClientConfig.EnableModOptions.get();
    }

    private boolean isShowUpdate() {
        return isModOptions() && ClientConfig.ShowNotificationModUpdate.get();
    }

    private boolean isHideUnnecessaryShareToLan() {
        return ClientConfig.EnableHideUnnecessaryShareToLan.get();
    }

    private boolean isNonOpenLan() {
        return this.minecraft.hasSingleplayerServer() && !this.minecraft.getSingleplayerServer().isPublished();
    }

    private boolean isRejoinButton() {
        return ClientConfig.EnableRejoinButton.get();
    }
}
