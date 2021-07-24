package net.morimori.bettergamemenu.gui;

import com.mojang.blaze3d.vertex.PoseStack;
import net.minecraft.client.gui.components.Button;
import net.minecraft.client.gui.components.ImageButton;
import net.minecraft.network.chat.Component;
import net.minecraft.resources.ResourceLocation;
import net.morimori.bettergamemenu.utils.RenderUtils;

import java.util.UUID;

public class LastJoinButton extends ImageButton {
    private final byte[] data;
    private final UUID id;

    public LastJoinButton(byte[] data, int p_94269_, int p_94270_, int p_94271_, int p_94272_, int p_94273_, int p_94274_, int p_94275_, ResourceLocation p_94276_, OnPress p_94277_, Button.OnTooltip tooltip, Component component) {
        super(p_94269_, p_94270_, p_94271_, p_94272_, p_94273_, p_94274_, p_94275_, p_94276_, 256, 256, p_94277_, tooltip, component);
        this.data = data;
        this.id = UUID.randomUUID();
    }

    @Override
    public void renderButton(PoseStack poseStack, int p_94283_, int p_94284_, float p_94285_) {
        super.renderButton(poseStack, p_94283_, p_94284_, p_94285_);
        if (data != null)
            RenderUtils.drawTexture(RenderUtils.getNativeTextureOnly(id, data), poseStack, x + 1, y + 1, 0, 0, 18, 18, 18, 18);
    }
}