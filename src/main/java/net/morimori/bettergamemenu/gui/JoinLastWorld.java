package net.morimori.bettergamemenu.gui;

import com.google.gson.Gson;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import net.minecraft.ChatFormatting;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.components.Button;
import net.minecraft.client.gui.components.events.GuiEventListener;
import net.minecraft.client.multiplayer.ServerData;
import net.minecraft.network.chat.Component;
import net.minecraft.network.chat.TextComponent;
import net.minecraft.network.chat.TranslatableComponent;
import net.minecraft.world.level.GameType;
import net.minecraft.world.level.storage.LevelResource;
import net.minecraft.world.level.storage.LevelStorageException;
import net.minecraft.world.level.storage.LevelStorageSource;
import net.minecraftforge.client.event.GuiScreenEvent;
import net.morimori.bettergamemenu.BetterGameMenu;
import net.morimori.bettergamemenu.utils.WorldUtils;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.Base64;
import java.util.Date;
import java.util.List;
import java.util.Optional;

public class JoinLastWorld {
    private static final File BGMFolder = new File("bettergamemenu");
    private static final Gson GSON = new Gson();
    private static final Minecraft mc = Minecraft.getInstance();
    private static final DateFormat DATE_FORMAT = new SimpleDateFormat();
    private static boolean rejoindServer = false;

    public static void onGui(GuiScreenEvent.InitGuiEvent.Post e) {
        Button singlePlayer = extractionButton(e.getWidgetList(), "menu.singleplayer", 0);
        Button multiPlayer = extractionButton(e.getWidgetList(), "menu.multiplayer", 1);

        if (singlePlayer != null) {
            LastSinglePlayData last = getLastSinglePlay();
            boolean flg = false;
            if (last != null) {
                try {
                    flg = mc.getLevelSource().getLevelList().stream().anyMatch(m -> m.getLevelId().equals(last.id) && last.path != null && last.path.toFile().exists());
                } catch (LevelStorageException levelStorageException) {
                    levelStorageException.printStackTrace();
                }
            }
            boolean finalFlg = flg;
            Button lastB = new LastJoinButton(last != null ? last.icon() : null, singlePlayer.x + singlePlayer.getWidth() + 8, singlePlayer.y, 20, 20, 0, 40, 20, BetterGameMenu.WIDGETS, n -> {
                if (!finalFlg)
                    return;
                WorldUtils.load(last.id);
            }, (button, poseStack, x, y) -> {
                TextComponent component = new TextComponent(last.name());
                component.append("\n").append(new TextComponent(last.id()).withStyle(ChatFormatting.GRAY));
                component.append("\n").append(new TextComponent(DATE_FORMAT.format(new Date(last.lastPlayed))).withStyle(ChatFormatting.GRAY));
                component.append("\n").append(last.gameType().getLongDisplayName().copy().withStyle(ChatFormatting.GRAY));
                e.getGui().renderTooltip(poseStack, mc.font.split(component, Math.max(e.getGui().width / 2 - 43, 170)), x, y);
            }, new TranslatableComponent("narrator.button.lastjoinsingleplay"));

            lastB.active = lastB.visible = flg;
            e.addWidget(lastB);
            ((List<GuiEventListener>) e.getGui().children()).add(lastB);
        }

        if (multiPlayer != null) {
            LastMultiPlayData last = getLastMultiPlay();
            Button lastB = new LastJoinButton(last != null ? last.icon() : null, multiPlayer.x + multiPlayer.getWidth() + 8, multiPlayer.y, 20, 20, 20, 40, 20, BetterGameMenu.WIDGETS, n -> {
                if (last == null)
                    return;
                rejoindServer = true;
                WorldUtils.joinServer(e.getGui(), new ServerData(last.name(), last.ip(), last.lan()));
            }, (button, poseStack, x, y) -> {
                TextComponent component = new TextComponent(last.name());
                component.append("\n").append(new TextComponent(last.ip()).withStyle(ChatFormatting.GRAY));
                if (last.lan())
                    component.append(new TextComponent(" (Lan)").withStyle(ChatFormatting.GRAY));
                if (last.motd != null)
                    component.append("\n").append(last.motd);
                e.getGui().renderTooltip(poseStack, mc.font.split(component, Math.max(e.getGui().width / 2 - 43, 170)), x, y);
            }, new TranslatableComponent("narrator.button.lastjoinmultiplay"));

            lastB.active = lastB.visible = last != null;
            e.addWidget(lastB);
            ((List<GuiEventListener>) e.getGui().children()).add(lastB);
        }
    }

    public static void setLastMultiPlay(ServerData data) {
        if (rejoindServer) {
            rejoindServer = false;
            return;
        }
        String ip = data.ip;
        JsonObject jo = new JsonObject();

        jo.addProperty("Ip", ip);
        jo.addProperty("Name", data.name);
        jo.add("Motd", Component.Serializer.toJsonTree(data.motd));
        jo.addProperty("Icon", data.getIconB64());
        jo.addProperty("Lan", data.isLan());

        BGMFolder.mkdirs();

        try {
            Files.writeString(BGMFolder.toPath().resolve("lastmultiplay.json"), GSON.toJson(jo));
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public static LastMultiPlayData getLastMultiPlay() {
        File file = BGMFolder.toPath().resolve("lastmultiplay.json").toFile();
        if (file.exists()) {
            JsonObject jo = null;
            try {
                jo = GSON.fromJson(new FileReader(file), JsonObject.class);
            } catch (FileNotFoundException e) {
                e.printStackTrace();
            }
            if (jo == null)
                return null;

            String ip = jo.has("Ip") ? jo.get("Ip").getAsString() : null;
            String icon = jo.has("Icon") ? jo.get("Icon").getAsString() : null;
            String name = jo.has("Name") ? jo.get("Name").getAsString() : null;
            JsonElement motd = jo.has("Motd") ? jo.get("Motd").getAsJsonObject() : null;
            boolean lan = jo.has("Lan") && jo.get("Lan").getAsBoolean();
            byte[] data = icon != null ? Base64.getDecoder().decode(icon.getBytes(StandardCharsets.UTF_8)) : null;
            Component motda = Component.Serializer.fromJson(motd);
            return new LastMultiPlayData(ip, name, motda, data, lan);
        }
        return null;
    }

    public static void setLastSinglePlay(LevelStorageSource.LevelStorageAccess data) {
        String id = data.getLevelId();
        Optional<Path> icon = data.getIconFile();

        JsonObject jo = new JsonObject();
        jo.addProperty("Id", id);
        jo.addProperty("Name", data.getSummary().getLevelName());
        jo.addProperty("LastPlayed", data.getSummary().getLastPlayed());
        jo.addProperty("GameMode", data.getSummary().getGameMode().getId());
        jo.addProperty("Path", data.getLevelPath(LevelResource.ROOT).toString());
        if (icon.isPresent()) {
            try {
                if (icon.get().toFile().exists())
                    jo.addProperty("Icon", new String(Base64.getEncoder().encode(Files.readAllBytes(icon.get()))));
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
        BGMFolder.mkdirs();
        try {
            Files.writeString(BGMFolder.toPath().resolve("lastsingleplay.json"), GSON.toJson(jo));
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public static LastSinglePlayData getLastSinglePlay() {
        File file = BGMFolder.toPath().resolve("lastsingleplay.json").toFile();
        if (file.exists()) {
            JsonObject jo = null;
            try {
                jo = GSON.fromJson(new FileReader(file), JsonObject.class);
            } catch (FileNotFoundException e) {
                e.printStackTrace();
            }
            if (jo == null)
                return null;

            String id = jo.has("Id") ? jo.get("Id").getAsString() : null;
            String path = jo.has("Path") ? jo.get("Path").getAsString() : null;
            String icon = jo.has("Icon") ? jo.get("Icon").getAsString() : null;
            String name = jo.has("Name") ? jo.get("Name").getAsString() : null;
            long lastplayed = jo.has("LastPlayed") ? jo.get("LastPlayed").getAsLong() : 0;
            int gamemode = jo.has("GameMode") ? jo.get("GameMode").getAsInt() : 0;

            Path pathIn = path != null ? Paths.get(path) : null;
            byte[] data = icon != null ? Base64.getDecoder().decode(icon.getBytes(StandardCharsets.UTF_8)) : null;

            return new LastSinglePlayData(id, pathIn, data, name, lastplayed, GameType.byId(gamemode));
        }
        return null;
    }


    public static Button extractionButton(List<GuiEventListener> buttons, String name, int id) {

        Optional<GuiEventListener> button = buttons.stream().filter(n -> n instanceof Button && ((Button) n).getMessage() instanceof TranslatableComponent && name.equals(((TranslatableComponent) ((Button) n).getMessage()).getKey())).findFirst();

        if (button.isPresent()) {
            return (Button) button.get();
        }

        try {
            return (Button) buttons.get(id);
        } catch (Exception ex) {
            ex.printStackTrace();
        }

        return null;
    }

    public static record LastSinglePlayData(String id, Path path, byte[] icon, String name, long lastPlayed,
                                            GameType gameType) {
    }

    public static record LastMultiPlayData(String ip, String name, Component motd, byte[] icon, boolean lan) {
    }
}
