package net.morimori.bettergamemenu.utils;

import com.mojang.blaze3d.platform.NativeImage;
import com.mojang.blaze3d.systems.RenderSystem;
import com.mojang.blaze3d.vertex.*;
import com.mojang.math.Matrix4f;
import net.minecraft.client.Minecraft;
import net.minecraft.client.renderer.GameRenderer;
import net.minecraft.client.renderer.texture.DynamicTexture;
import net.minecraft.client.renderer.texture.MissingTextureAtlasSprite;
import net.minecraft.resources.ResourceLocation;

import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.util.HashMap;
import java.util.Map;
import java.util.UUID;

public class RenderUtils {
    private static final Minecraft mc = Minecraft.getInstance();
    private static final Map<UUID, ResourceLocation> NATIVE_TEXTURES = new HashMap<>();

    public static ResourceLocation getNativeTextureOnly(UUID id, byte[] data) {
        if (NATIVE_TEXTURES.containsKey(id))
            return NATIVE_TEXTURES.get(id);
        NativeImage nativeimage = MissingTextureAtlasSprite.getTexture().getPixels();
        try {
            nativeimage = NativeImage.read(new ByteArrayInputStream(data));
        } catch (IOException e) {
            e.printStackTrace();
        }
        DynamicTexture dynamictexture = new DynamicTexture(nativeimage);
        ResourceLocation location = mc.getTextureManager().register("native_texture", dynamictexture);
        NATIVE_TEXTURES.put(id, location);
        return location;
    }

    public static void drawTexture(ResourceLocation location, PoseStack poseStack, float x, float y, float textureStartX, float textureStartY, float textureFinishWidth, float textureFinishHeight, float textureSizeX, float textureSizeY) {
        poseStack.pushPose();
        RenderSystem.setShader(GameRenderer::getPositionTexShader);
        RenderSystem.setShaderColor(1.0F, 1.0F, 1.0F, 1.0F);
        RenderSystem.setShaderTexture(0, location);
        fBlit(poseStack, x, y, textureStartX, textureStartY, textureFinishWidth, textureFinishHeight, textureSizeX, textureSizeY);
        poseStack.popPose();
    }

    private static void fBlit(PoseStack poseStack, float ix, float iy, float tsx, float tsy, float tw, float th, float tssx, float tssy) {
        Matrix4f matrix4f = poseStack.last().pose();
        float x = ix;
        float y = ix + tw;
        float w = iy;
        float h = iy + th;
        float u1 = tsx / tssx;
        float u2 = (tsx + tw) / tssx;
        float v1 = tsy / tssy;
        float v2 = (tsy + th) / tssy;
        RenderSystem.setShader(GameRenderer::getPositionTexShader);
        BufferBuilder bufferBuilder = Tesselator.getInstance().getBuilder();
        bufferBuilder.begin(VertexFormat.Mode.QUADS, DefaultVertexFormat.POSITION_TEX);
        bufferBuilder.vertex(matrix4f, x, h, 0).uv(u1, v2).endVertex();
        bufferBuilder.vertex(matrix4f, y, h, 0).uv(u2, v2).endVertex();
        bufferBuilder.vertex(matrix4f, y, w, 0).uv(u2, v1).endVertex();
        bufferBuilder.vertex(matrix4f, x, w, 0).uv(u1, v1).endVertex();
        bufferBuilder.end();
        BufferUploader.end(bufferBuilder);
    }
}
