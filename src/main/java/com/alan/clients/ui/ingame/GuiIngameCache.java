package com.alan.clients.ui.ingame;

import com.alan.clients.util.MCPCompat;
import com.alan.clients.util.interfaces.InstanceAccess;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.Gui;
import net.minecraft.client.gui.ScaledResolution;
import net.minecraft.client.renderer.GlStateManager;
import net.minecraft.client.renderer.OpenGlHelper;
import net.minecraft.client.renderer.Tessellator;
import net.minecraft.client.renderer.WorldRenderer;
import net.minecraft.client.renderer.vertex.DefaultVertexFormats;
import net.minecraft.client.shader.Framebuffer;
import org.lwjgl.opengl.GL11;

public class GuiIngameCache implements InstanceAccess {
    private static final Minecraft MC = Minecraft.getMinecraft();
    private static Framebuffer aBK;
    public static boolean dirty;
    private static ScaledResolution jY = new ScaledResolution(aEg);
    private static final Tessellator aBM = Tessellator.getInstance();
    private static final WorldRenderer aBN = aBM.getWorldRenderer();

    public GuiIngameCache() {
    }

    public static void renderGameOverlay(float var0) {
        if (jY.getScaledWidth() != aEg.displayWidth || jY.getScaledHeight() != aEg.displayHeight) {
            jY = new ScaledResolution(aEg);
        }

        OpenGlHelper.isFramebufferEnabled();
        int i = MC.displayWidth;
        int j = MC.displayHeight;
        GlStateManager.enableBlend();
        GlStateManager.color(1.0F, 1.0F, 1.0F, 1.0F);
        aEg.getTextureManager().bindTexture(Gui.icons);
        GlStateManager.enableBlend();
        if (MCPCompat.showCrosshair()) {
            GlStateManager.tryBlendFuncSeparate(775, 769, 1, 0);
            GlStateManager.enableAlpha();
            aEg.ingameGUI.drawTexturedModalRect(jY.getScaledWidth() / 2.0F - 7.0F, jY.getScaledHeight() / 2 - 7, 0, 0, 16, 16);
        }

        if (aBK != null) {
            GlStateManager.enableBlend();
            GlStateManager.enableAlpha();
            GlStateManager.tryBlendFuncSeparate(1, 771, 1, 771);
            GlStateManager.color(1.0F, 1.0F, 1.0F, 1.0F);
            aBK.bindFramebufferTexture();
            drawTexturedRect(0.0F, 0.0F, jY.getScaledWidth(), jY.getScaledHeight(), 0.0F, 1.0F, 1.0F, 0.0F, 9728);
            GlStateManager.tryBlendFuncSeparate(770, 771, 1, 0);
        }

        if (dirty) {
            aBK = refreshFramebuffer(aBK, i, j);
            aBK.framebufferClear();
            aBK.bindFramebuffer(false);
            GlStateManager.disableBlend();
            GlStateManager.tryBlendFuncSeparate(770, 771, 1, 771);
            GlStateManager.color(1.0F, 1.0F, 1.0F, 1.0F);
            GlStateManager.disableLighting();
            GlStateManager.disableFog();
            MC.ingameGUI.renderGameOverlay(var0);
            MC.getFramebuffer().bindFramebuffer(false);
            GlStateManager.enableBlend();
            dirty = false;
        }
    }

    public static void renderCrosshair(int var0, int var1) {
        MC.getTextureManager().bindTexture(Gui.icons);
        GlStateManager.enableBlend();
        GlStateManager.tryBlendFuncSeparate(775, 769, 1, 0);
        GlStateManager.enableAlpha();
        aBN.begin(7, DefaultVertexFormats.POSITION_TEX);
        aBN.pos(var0, var1 + 16, 100.0).tex(0.0, 0.0625).endVertex();
        aBN.pos(var0 + 16, var1 + 16, 100.0).tex(0.0625, 0.0625).endVertex();
        aBN.pos(var0 + 16, var1, 100.0).tex(0.0625, 0.0).endVertex();
        aBN.pos(var0, var1, 100.0).tex(0.0, 0.0).endVertex();
        aBM.draw();
    }

    public static void drawTexturedRect(float var0, float var1, float var2, float var3, float var4, float var5, float var6, float var7, int var8) {
        GlStateManager.enableTexture2D();
        GL11.glTexParameteri(3553, 10241, var8);
        GL11.glTexParameteri(3553, 10240, var8);
        aBN.begin(7, DefaultVertexFormats.POSITION_TEX);
        aBN.pos(var0, var1 + var3, 0.0).tex(var4, var7).endVertex();
        aBN.pos(var0 + var2, var1 + var3, 0.0).tex(var5, var7).endVertex();
        aBN.pos(var0 + var2, var1, 0.0).tex(var5, var6).endVertex();
        aBN.pos(var0, var1, 0.0).tex(var4, var6).endVertex();
        aBM.draw();
        GL11.glTexParameteri(3553, 10241, 9728);
        GL11.glTexParameteri(3553, 10240, 9728);
    }

    public static Framebuffer refreshFramebuffer(Framebuffer framebuffer, int var1, int var2) {
        if (framebuffer == null) {
            framebuffer = new Framebuffer(var1, var2, true);
            framebuffer.setFramebufferFilter(9728);
            framebuffer.framebufferColor[0] = 0.0F;
            framebuffer.framebufferColor[1] = 0.0F;
            framebuffer.framebufferColor[2] = 0.0F;
        } else if (framebuffer.framebufferWidth != var1 || framebuffer.framebufferHeight != var2) {
            framebuffer.createBindFramebuffer(var1, var2);
            framebuffer.setFramebufferFilter(9728);
        }

        aBK = framebuffer;
        return framebuffer;
    }
}
