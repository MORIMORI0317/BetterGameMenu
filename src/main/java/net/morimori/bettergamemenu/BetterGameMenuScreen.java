package net.morimori.bettergamemenu;

import com.mojang.blaze3d.matrix.MatrixStack;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.advancements.AdvancementsScreen;
import net.minecraft.client.gui.screen.*;
import net.minecraft.client.gui.widget.Widget;
import net.minecraft.client.gui.widget.button.Button;
import net.minecraft.client.gui.widget.button.ImageButton;
import net.minecraft.client.multiplayer.ServerData;
import net.minecraft.realms.RealmsBridgeScreen;
import net.minecraft.util.ResourceLocation;
import net.minecraft.util.SharedConstants;
import net.minecraft.util.Util;
import net.minecraft.util.text.TranslationTextComponent;
import net.minecraftforge.client.gui.NotificationModUpdateScreen;
import net.minecraftforge.fml.client.gui.screen.ModListScreen;

public class BetterGameMenuScreen extends IngameMenuScreen {
    private static final ResourceLocation WIDGETS = new ResourceLocation(BetterGameMenu.MODID, "textures/gui/widgets.png");
    private NotificationModUpdateScreen modUpdateNotification;

    public BetterGameMenuScreen() {
        super(true);
    }

    @Override
    protected void init() {
        createButtons();
    }

    private void createButtons() {

        this.addButton(new Button(this.width / 2 - 102, this.height / 4 + 24 + -16, 204, 20, new TranslationTextComponent("menu.returnToGame"), (p_213070_1_) -> {
            this.minecraft.setScreen((Screen) null);
            this.minecraft.mouseHandler.grabMouse();
        }));
        this.addButton(new Button(this.width / 2 - 102, this.height / 4 + 48 + -16, 98, 20, new TranslationTextComponent("gui.advancements"), (p_213065_1_) -> {
            this.minecraft.setScreen(new AdvancementsScreen(this.minecraft.player.connection.getAdvancements()));
        }));
        this.addButton(new Button(this.width / 2 + 4, this.height / 4 + 48 + -16, 98, 20, new TranslationTextComponent("gui.stats"), (p_213066_1_) -> {
            this.minecraft.setScreen(new StatsScreen(this, this.minecraft.player.getStats()));
        }));

        String s = SharedConstants.getCurrentVersion().isStable() ? "https://aka.ms/javafeedback?ref=game" : "https://aka.ms/snapshotfeedback?ref=game";
        Button gfButton = this.addButton(new Button(this.width / 2 - 102, this.height / 4 + 72 + -16, 98, 20, new TranslationTextComponent("menu.sendFeedback"), (p_213072_2_) -> {
            this.minecraft.setScreen(new ConfirmOpenLinkScreen((p_213069_2_) -> {
                if (p_213069_2_) {
                    Util.getPlatform().openUri(s);
                }

                this.minecraft.setScreen(this);
            }, s, true));
        }));

        gfButton.active = gfButton.visible = !isRemoveGFARB();

        Button rbButton = this.addButton(new Button(this.width / 2 + 4, this.height / 4 + 72 + -16, 98, 20, new TranslationTextComponent("menu.reportBugs"), (p_213063_1_) -> {
            this.minecraft.setScreen(new ConfirmOpenLinkScreen((p_213064_1_) -> {
                if (p_213064_1_) {
                    Util.getPlatform().openUri("https://aka.ms/snapshotbugs?ref=game");
                }

                this.minecraft.setScreen(this);
            }, "https://aka.ms/snapshotbugs?ref=game", true));
        }));

        rbButton.active = rbButton.visible = !isRemoveGFARB();

        Button options = this.addButton(new Button(this.width / 2 - 102, this.height / 4 + 96 + -16, 98, 20, new TranslationTextComponent("menu.options"), (p_213071_1_) -> {
            this.minecraft.setScreen(new OptionsScreen(this, this.minecraft.options));
        }));
        Button shareToLan = this.addButton(new Button(this.width / 2 + 4, this.height / 4 + 96 + -16, 98, 20, new TranslationTextComponent("menu.shareToLan"), (p_213068_1_) -> {
            this.minecraft.setScreen(new ShareToLanScreen(this));
        }));

        if (isHideUnnecessaryShareToLan())
            shareToLan.visible = shareToLan.active = isNonOpenLan();
        else
            shareToLan.active = isNonOpenLan();

        Button button1 = this.addButton(new Button(this.width / 2 - 102, this.height / 4 + 120 + -16, 204, 20, new TranslationTextComponent("menu.returnToMenu"), (p_213067_1_) -> {
            boolean flag = this.minecraft.isLocalServer();
            boolean flag1 = this.minecraft.isConnectedToRealms();
            p_213067_1_.active = false;
            this.minecraft.level.disconnect();
            if (flag) {
                this.minecraft.clearLevel(new DirtMessageScreen(new TranslationTextComponent("menu.savingLevel")));
            } else {
                this.minecraft.clearLevel();
            }

            if (flag) {
                this.minecraft.setScreen(new MainMenuScreen());
            } else if (flag1) {
                RealmsBridgeScreen realmsbridgescreen = new RealmsBridgeScreen();
                realmsbridgescreen.switchToRealms(new MainMenuScreen());
            } else {
                this.minecraft.setScreen(new MultiplayerScreen(new MainMenuScreen()));
            }

        }));
        if (!this.minecraft.isLocalServer()) {
            button1.setMessage(new TranslationTextComponent("menu.disconnect"));
        }

        boolean isDownButtonFlg = isHideUnnecessaryShareToLan() ? !isRemoveGFARB() && isHideUnnecessaryShareToLan() && isNonOpenLan() : !isRemoveGFARB();

        Button modButton = this.addButton(new Button(this.width / 2 + 4, this.height / 4 + (isDownButtonFlg ? 120 : 96) - 16, 98, 20, new TranslationTextComponent("menu.modoption"), (n) -> Minecraft.getInstance().setScreen(new ModListScreen(this))));
        modButton.active = modButton.visible = isModOptions();

        Button rejoinButton = this.addButton(new ImageButton(button1.x + button1.getWidth() + 8, button1.y, 20, 20, 0, 0, 20, WIDGETS, n -> {
            String id = "";
            ServerData serverData = null;
            boolean flag = this.minecraft.isLocalServer();

            if (flag)
                id = getMinecraft().getSingleplayerServer().storageSource.getLevelId();

            boolean flag1 = this.minecraft.isConnectedToRealms();

            if (!flag && !flag1)
                serverData = this.minecraft.getCurrentServer();

            n.active = false;
            this.minecraft.level.disconnect();

            if (flag) {
                this.minecraft.clearLevel(new DirtMessageScreen(new TranslationTextComponent("menu.savingLevel")));
            } else {
                this.minecraft.clearLevel();
            }

            if (flag) {
                try {
                    this.minecraft.loadLevel(id);
                } catch (Exception ex) {
                    this.minecraft.setScreen(new MainMenuScreen());
                }
            } else if (flag1) {
                RealmsBridgeScreen realmsbridgescreen = new RealmsBridgeScreen();
                realmsbridgescreen.switchToRealms(new MainMenuScreen());
            } else {
                try {
                    this.minecraft.setScreen(new ConnectingScreen(this, this.minecraft, serverData));
                } catch (Exception ex) {
                    this.minecraft.setScreen(new MultiplayerScreen(new MainMenuScreen()));
                }
            }
        }));
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
                button1.y += 24;
                for (Widget widget : this.buttons) {
                    widget.y -= 16;
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
    public void render(MatrixStack matrixStack, int mouseX, int mouseY, float partialTicks) {
        super.render(matrixStack, mouseX, mouseY, partialTicks);
        if (isShowUpdate() && modUpdateNotification != null)
            modUpdateNotification.render(matrixStack, mouseX, mouseY, partialTicks);
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
