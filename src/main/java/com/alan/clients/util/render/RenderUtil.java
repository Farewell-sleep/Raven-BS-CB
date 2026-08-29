package com.alan.clients.util.render;

import com.alan.clients.util.MCPCompat;
import com.alan.clients.Client;
import com.alan.clients.newevent.impl.other.AttackEvent;
import com.alan.clients.util.interfaces.InstanceAccess;
import com.alan.clients.util.render.ColorUtil;
import com.alan.clients.util.shader.RiseShaders;
import com.alan.clients.util.vector.Vector3d;
import java.awt.Color;
import lombok.Generated;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.Gui;
import net.minecraft.client.gui.ScaledResolution;
import net.minecraft.client.renderer.GlStateManager;
import net.minecraft.client.renderer.OpenGlHelper;
import net.minecraft.client.renderer.RenderHelper;
import net.minecraft.client.renderer.culling.Frustum;
import net.minecraft.client.renderer.entity.RenderManager;
import net.minecraft.client.shader.Framebuffer;
import net.minecraft.entity.Entity;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.item.ItemStack;
import net.minecraft.util.AxisAlignedBB;
import net.minecraft.util.ResourceLocation;
import net.minecraft.util.Vec3;
import org.lwjgl.opengl.GL11;

public final class RenderUtil implements InstanceAccess {
    private static final Frustum FRUSTUM = new Frustum();
    private static final RenderManager RENDER_MANAGER = aEg.getRenderManager();
    public static final int GENERIC_SCALE = 22;

    public static Vector3d vH() {
        return new Vector3d(-MCPCompat.renderPosX(), -MCPCompat.renderPosY(), -MCPCompat.renderPosZ());
    }

    public static void a(ResourceLocation location, float var1, float var2, int var3, int var4, int var5, int var6, int var7, int var8, float var9, float var10) {
        Minecraft.getMinecraft().getTextureManager().bindTexture(location);
        GL11.glTexParameteri(3553, 10242, 33071);
        GL11.glTexParameteri(3553, 10243, 33071);
        GL11.glTexParameteri(3553, 10241, 9728);
        GL11.glTexParameteri(3553, 10240, 9728);
        GlStateManager.disableDepth();
        GlStateManager.depthMask(false);
        GlStateManager.enableBlend();
        GlStateManager.tryBlendFuncSeparate(770, 771, 1, 0);
        GlStateManager.enableAlpha();
        GlStateManager.enableRescaleNormal();
        GlStateManager.color(1.0F, 1.0F, 1.0F, 1.0F);
        float f = (float)var3 / var7;
        float f1 = (float)var4 / var8;
        float f2 = (float)(var3 + var5) / var7;
        float f3 = (float)(var4 + var6) / var8;
        GL11.glBegin(7);
        GL11.glTexCoord2f(f, f1);
        GL11.glVertex2f(var1, var2);
        GL11.glTexCoord2f(f, f3);
        GL11.glVertex2f(var1, var2 + var10);
        GL11.glTexCoord2f(f2, f3);
        GL11.glVertex2f(var1 + var9, var2 + var10);
        GL11.glTexCoord2f(f2, f1);
        GL11.glVertex2f(var1 + var9, var2);
        GL11.glEnd();
        GlStateManager.depthMask(true);
        GlStateManager.enableDepth();
    }

    public static void rt() {
        GlStateManager.enableBlend();
        GlStateManager.blendFunc(770, 771);
        GlStateManager.disableTexture2D();
        GlStateManager.disableCull();
        GlStateManager.disableAlpha();
        GlStateManager.disableDepth();
    }

    public static void stop() {
        GlStateManager.enableDepth();
        GlStateManager.enableAlpha();
        GlStateManager.enableCull();
        GlStateManager.enableTexture2D();
        GlStateManager.disableBlend();
        GlStateManager.resetColor();
    }

    public static void d(double var0, double var2, double var4, double var6, Color var8) {
        rt();
        if (var8 != null) {
            ColorUtil.glColor(var8);
        }

        GL11.glBegin(7);
        GL11.glVertex2d(var0, var2);
        GL11.glVertex2d(var0 + var4, var2);
        GL11.glVertex2d(var0 + var4, var2 + var6);
        GL11.glVertex2d(var0, var2 + var6);
        GL11.glEnd();
        stop();
    }

    public static void E(EntityLivingBase living) {
        Client.a.e().d(new AttackEvent(living));
        if (!aEg.thePlayer.isSwingInProgress
            || aEg.thePlayer.swingProgressInt >= MCPCompat.armSwingAnimationEnd(aEg.thePlayer) / 2
            || aEg.thePlayer.swingProgressInt < 0) {
            aEg.thePlayer.swingProgressInt = -1;
            aEg.thePlayer.isSwingInProgress = true;
        }

        if (aEg.thePlayer.fallDistance > 0.0F) {
            aEg.thePlayer.onCriticalHit(living);
        }
    }

    public static void rainbowRectangle(double var0, double var2, double var4, double var6) {
        rt();
        GL11.glBegin(7);

        for (double d0 = var0; d0 <= var0 + var4; d0 += 0.5) {
            color(Color.getHSBColor((float)((d0 - var0) / var4), 1.0F, 1.0F));
            GL11.glVertex2d(d0, var2);
            GL11.glVertex2d(d0 + 0.5, var2);
            GL11.glVertex2d(d0 + 0.5, var2 + var6);
            GL11.glVertex2d(d0, var2 + var6);
        }

        GL11.glEnd();
        stop();
    }

    public static void rectangle(double var0, double var2, double var4, double var6) {
        d(var0, var2, var4, var6, null);
    }

    public static void e(double var0, double var2, double var4, double var6, Color var8) {
        d(var0 - var4 / 2.0, var2 - var6 / 2.0, var4, var6, var8);
    }

    public static void centeredRectangle(double var0, double var2, double var4, double var6) {
        d(var0 - var4 / 2.0, var2 - var6 / 2.0, var4, var6, null);
    }

    public static void horizontalGradient(double var0, double var2, double var4, double var6, Color var8, Color var9) {
        rt();
        GlStateManager.alphaFunc(516, 0.0F);
        GL11.glShadeModel(7425);
        GL11.glBegin(7);
        ColorUtil.glColor(var8);
        GL11.glVertex2d(var0, var2);
        GL11.glVertex2d(var0 + var4, var2);
        ColorUtil.glColor(var9);
        GL11.glVertex2d(var0 + var4, var2 + var6);
        GL11.glVertex2d(var0, var2 + var6);
        GL11.glEnd();
        GL11.glShadeModel(7424);
        stop();
    }

    public static void begin(int var0) {
        GL11.glBegin(var0);
    }

    public static void gradientCentered(double var0, double var2, double var4, double var6, Color var8, Color var9) {
        var0 -= var4 / 2.0;
        var2 -= var6 / 2.0;
        horizontalGradient(var0, var2, var4, var6, var8, var9);
    }

    public static void horizontalCenteredGradient(double var0, double var2, double var4, double var6, Color var8, Color var9) {
        rt();
        GL11.glShadeModel(7425);
        GL11.glBegin(7);
        ColorUtil.glColor(var8);
        GL11.glVertex2d(var0, var2);
        GL11.glVertex2d(var0, var2 + var6);
        ColorUtil.glColor(var9);
        GL11.glVertex2d(var0 + var4, var2 + var6);
        GL11.glVertex2d(var0 + var4, var2);
        GL11.glEnd();
        GL11.glShadeModel(7424);
        stop();
    }

    public static void verticalGradient(double var0, double var2, double var4, double var6, Color var8, Color var9) {
        horizontalCenteredGradient(var0 - var4 / 2.0, var2 - var6 / 2.0, var4, var6, var8, var9);
    }

    public static void image(ResourceLocation location, float var1, float var2, float var3, float var4, Color var5) {
        GL11.glTexParameteri(3553, 10241, 9728);
        GL11.glTexParameteri(3553, 10240, 9728);
        GlStateManager.enableBlend();
        GlStateManager.enableAlpha();
        GlStateManager.alphaFunc(516, 0.0F);
        color(var5);
        OpenGlHelper.glBlendFunc(770, 771, 1, 0);
        aEg.getTextureManager().bindTexture(location);
        Gui.drawModalRectWithCustomSizedTexture((int)var1, (int)var2, 0.0F, 0.0F, (int)var3, (int)var4, var3, var4);
        GlStateManager.resetColor();
        GlStateManager.disableBlend();
    }

    public static void a(int var0, float var1, float var2, float var3, float var4, Color var5) {
        GL11.glTexParameteri(3553, 10241, 9728);
        GL11.glTexParameteri(3553, 10240, 9728);
        GlStateManager.enableBlend();
        GlStateManager.enableAlpha();
        GlStateManager.alphaFunc(516, 0.0F);
        color(var5);
        OpenGlHelper.glBlendFunc(770, 771, 1, 0);
        GlStateManager.bindTexture(var0);
        Gui.drawModalRectWithCustomSizedTexture((int)var1, (int)var2, 0.0F, 0.0F, (int)var3, (int)var4, var3, var4);
        GlStateManager.resetColor();
        GlStateManager.disableBlend();
    }

    public static void image(ResourceLocation location, double var1, double var3, double var5, double var7, Color var9) {
        image(location, (float)var1, (float)var3, (float)var5, (float)var7, var9);
    }

    public static void image(ResourceLocation location, float var1, float var2, float var3, float var4) {
        image(location, var1, var2, var3, var4, Color.WHITE);
    }

    public static void image(ResourceLocation location, double var1, double var3, double var5, double var7) {
        image(location, (float)var1, (float)var3, (float)var5, (float)var7);
    }

    public static void dropShadow(int var0, double var1, double var3, double var5, double var7, double var9, double var11) {
        GlStateManager.alphaFunc(516, 0.0F);
        GlStateManager.enableBlend();
        GlStateManager.enableAlpha();

        for (float f = 0.0F; f <= var0 / 2.0F; f += 0.5F) {
            roundedRectangle(var1 - f / 2.0F, var3 - f / 2.0F, var5 + f, var7 + f, var11, new Color(0, 0, 0, (int)Math.max(0.5, (var9 - f * 1.2) / 5.5)));
        }
    }

    public static void f(double var0, double var2, double var4, double var6) {
        GL11.glColor4d(var0, var2, var4, var6);
    }

    public static void r(double var0, double var2, double var4) {
        f(var0, var2, var4, 1.0);
    }

    public static void color(Color var0) {
        if (var0 == null) {
            var0 = Color.white;
        }

        f(var0.getRed() / 255.0F, var0.getGreen() / 255.0F, var0.getBlue() / 255.0F, var0.getAlpha() / 255.0F);
    }

    public static void color(Color var0, int var1) {
        if (var0 == null) {
            var0 = Color.white;
        }

        f(var0.getRed() / 255.0F, var0.getGreen() / 255.0F, var0.getBlue() / 255.0F, 0.5);
    }

    public static void a(double var0, double var2, double var4, double var6, boolean var8, Color var9) {
        var4 /= 2.0;
        rt();
        if (var9 != null) {
            color(var9);
        }

        if (!var8) {
            GL11.glLineWidth(2.0F);
        }

        GL11.glEnable(2848);
        begin(var8 ? 6 : 3);

        for (double d0 = 0.0; d0 <= var6 / 4.0; d0++) {
            double d1 = d0 * 4.0 * (Math.PI * 2) / 360.0;
            GL11.glVertex2d(var0 + var4 * Math.cos(d1) + var4, var2 + var4 * Math.sin(d1) + var4);
        }

        vI();
        GL11.glDisable(2848);
        stop();
    }

    public static void a(double var0, double var2, double var4, int var6, boolean var7) {
        a(var0, var2, var4, var6, var7, null);
    }

    public static void a(double var0, double var2, double var4, int var6, Color var7) {
        a(var0, var2, var4, var6, true, var7);
    }

    public static void a(double var0, double var2, double var4, int var6) {
        a(var0, var2, var4, var6, true, null);
    }

    public static void polygonCentered(double var0, double var2, double var4, int var6, boolean var7, Color var8) {
        var0 -= var4 / 2.0;
        var2 -= var4 / 2.0;
        a(var0, var2, var4, var6, var7, var8);
    }

    public static void b(double var0, double var2, double var4, int var6, boolean var7) {
        var0 -= var4 / 2.0;
        var2 -= var4 / 2.0;
        a(var0, var2, var4, var6, var7, null);
    }

    public static void b(double var0, double var2, double var4, int var6, Color var7) {
        var0 -= var4 / 2.0;
        var2 -= var4 / 2.0;
        a(var0, var2, var4, var6, true, var7);
    }

    public static void b(double var0, double var2, double var4, int var6) {
        var0 -= var4 / 2.0;
        var2 -= var4 / 2.0;
        a(var0, var2, var4, var6, true, null);
    }

    public static void a(double var0, double var2, double var4, boolean var6, Color var7) {
        a(var0, var2, var4, 3.0, var6, var7);
    }

    public static void b(double var0, double var2, double var4, boolean var6) {
        a(var0, var2, var4, 3, var6);
    }

    public static void a(double var0, double var2, double var4, Color var6) {
        a(var0, var2, var4, 3, var6);
    }

    public static void s(double var0, double var2, double var4) {
        a(var0, var2, var4, 3);
    }

    public static void b(double var0, double var2, double var4, boolean var6, Color var7) {
        var0 -= var4 / 2.0;
        var2 -= var4 / 2.0;
        a(var0, var2, var4, 3.0, var6, var7);
    }

    public static void c(double var0, double var2, double var4, boolean var6) {
        var0 -= var4 / 2.0;
        var2 -= var4 / 2.0;
        a(var0, var2, var4, 3, var6);
    }

    public static void b(double var0, double var2, double var4, Color var6) {
        var0 -= var4 / 2.0;
        var2 -= var4 / 2.0;
        a(var0, var2, var4, 3, var6);
    }

    public static void t(double var0, double var2, double var4) {
        var0 -= var4 / 2.0;
        var2 -= var4 / 2.0;
        a(var0, var2, var4, 3);
    }

    public static void a(double var0, double var2, int var4, ItemStack stack) {
        if (stack != null) {
            GlStateManager.pushMatrix();
            GlStateManager.enableRescaleNormal();
            GlStateManager.enableBlend();
            GlStateManager.tryBlendFuncSeparate(770, 771, 1, 0);
            RenderHelper.enableGUIStandardItemLighting();
            GlStateManager.translate((float)var0, (float)var2, (float)var4);
            aEg.getRenderItem().renderItemIntoGUI(stack, 0, 0);
            GlStateManager.translate((float)-var0, (float)-var2, (float)-var4);
            GlStateManager.disableRescaleNormal();
            GlStateManager.disableBlend();
            RenderHelper.disableStandardItemLighting();
            GlStateManager.popMatrix();
        }
    }

    public static void a(double var0, double var2, double var4, double var6, double var8, Color var10, Color var11, boolean var12) {
        RiseShaders.RGQ_SHADER.draw(var0, var2, var4, var6, var8, var10, var11, var12);
    }

    public static void b(double var0, double var2, double var4, double var6, double var8, Color var10, Color var11, boolean var12) {
        RiseShaders.RGQ_TEST_SHADER.draw(var0, var2, var4, var6, var8, var10, var11, var12);
    }

    public static void a(
        double var0,
        double var2,
        double var4,
        double var6,
        double var8,
        Color var10,
        Color var11,
        boolean var12,
        boolean var13,
        boolean var14,
        boolean var15,
        boolean var16
    ) {
        RiseShaders.RGQ_TEST_SHADER.a((float)var0, (float)var2, (float)var4, (float)var6, (float)var8, var10, var11, var12, var13, var14, var15, var16);
    }

    public static void a(
        double var0,
        double var2,
        double var4,
        double var6,
        double var8,
        Color var10,
        Color var11,
        Color var12,
        boolean var13,
        boolean var14,
        boolean var15,
        boolean var16,
        boolean var17
    ) {
        RiseShaders.TRI_RGQ_SHADER.a((float)var0, (float)var2, (float)var4, (float)var6, (float)var8, var10, var11, var12, var13, var14, var15, var16, var17);
    }

    public static void b(
        double var0,
        double var2,
        double var4,
        double var6,
        double var8,
        Color var10,
        Color var11,
        boolean var12,
        boolean var13,
        boolean var14,
        boolean var15,
        boolean var16
    ) {
        RiseShaders.RGQ_SHADER.c(var0, var2, var4, var6, var8, var10, var11, var12, var13, var14, var15, var16);
    }

    private static boolean ROUNDED_RECT_FALLBACK_LOGGED = false;

    public static void roundedRectangle(double var0, double var2, double var4, double var6, double var8, Color var10) {
        try {
            RiseShaders.RQ_SHADER.draw((float)var0, (float)var2, (float)var4, (float)var6, (float)var8, var10);
        } catch (Throwable shaderError) {
            if (!ROUNDED_RECT_FALLBACK_LOGGED) {
                ROUNDED_RECT_FALLBACK_LOGGED = true;
                shaderError.printStackTrace();
            }
            f(var0, var2, var4, var6, var10);
        }
    }

    public static void a(
        double var0, double var2, double var4, double var6, double var8, Color var10, boolean var11, boolean var12, boolean var13, boolean var14
    ) {
        RiseShaders.RQ_SHADER.a((float)var0, (float)var2, (float)var4, (float)var6, (float)var8, var10, var11, var12, var13, var14);
    }

    public static void roundedOutlineRectangle(double var0, double var2, double var4, double var6, double var8, double var10, Color var12) {
        RiseShaders.ROQ_SHADER.b(var0, var2, var4, var6, var8, var10, var12);
    }

    public static void roundedOutlineGradientRectangle(double var0, double var2, double var4, double var6, double var8, double var10, Color var12, Color var13) {
        RiseShaders.ROGQ_SHADER.b(var0, var2, var4, var6, var8, var10, var12, var13);
    }

    public static void vI() {
        GL11.glEnd();
    }

    public static void c(double var0, double var2, double var4, Color var6) {
        roundedRectangle(var0 - var4, var2 - var4, var4 * 2.0, var4 * 2.0, var4, var6);
    }

    public static void f(double var0, double var2, double var4, double var6, Color var8) {
        ColorUtil.glColor(var8);
        GL11.glBegin(7);
        GL11.glVertex2d(var0, var2);
        GL11.glVertex2d(var0 + var4, var2);
        GL11.glVertex2d(var0 + var4, var2 + var6);
        GL11.glVertex2d(var0, var2 + var6);
        GL11.glEnd();
    }

    public static void g(double var0, double var2, double var4, double var6) {
        ScaledResolution scaledresolution = MCPCompat.scaledResolution();
        double scaleFactor = scaledresolution.getScaleFactor();
        var2 = scaledresolution.getScaledHeight() - var2;
        var0 *= scaleFactor;
        var2 *= scaleFactor;
        var4 *= scaleFactor;
        var6 *= scaleFactor;
        GL11.glEnable(3089);
        GL11.glScissor((int)var0, (int)(var2 - var6), (int)var4, (int)var6);
    }

    public static void vJ() {
        GL11.glDisable(3089);
    }

    public static void drawLine(double var0, double var2, double var4, double var6, double var8, double var10, Color var12, float var13) {
        aEg.getRenderManager();
        var0 -= MCPCompat.renderPosX();
        aEg.getRenderManager();
        var6 -= MCPCompat.renderPosX();
        aEg.getRenderManager();
        var2 -= MCPCompat.renderPosY();
        aEg.getRenderManager();
        var8 -= MCPCompat.renderPosY();
        aEg.getRenderManager();
        var4 -= MCPCompat.renderPosZ();
        aEg.getRenderManager();
        var10 -= MCPCompat.renderPosZ();
        GL11.glPushMatrix();
        GL11.glEnable(3042);
        GL11.glEnable(2848);
        GL11.glDisable(3553);
        GL11.glBlendFunc(770, 771);
        GL11.glEnable(3042);
        GL11.glLineWidth(var13);
        color(var12);
        GL11.glBegin(2);
        GL11.glVertex3d(var0, var2, var4);
        GL11.glVertex3d(var6, var8, var10);
        GL11.glEnd();
        GL11.glDisable(3042);
        GL11.glEnable(3553);
        GL11.glDisable(2848);
        GL11.glDisable(3042);
        GL11.glPopMatrix();
        color(Color.WHITE);
    }

    public static boolean isInViewFrustrum(Entity entity) {
        return isInViewFrustrum(entity.getEntityBoundingBox()) || entity.ignoreFrustumCheck;
    }

    public static boolean isInViewFrustrum(AxisAlignedBB box) {
        Entity entity = aEg.getRenderViewEntity();
        FRUSTUM.setPosition(entity.posX, entity.posY, entity.posZ);
        return FRUSTUM.isBoundingBoxInFrustum(box);
    }

    public static Framebuffer createFrameBuffer(Framebuffer framebuffer) {
        if (framebuffer != null && framebuffer.framebufferWidth == aEg.displayWidth && framebuffer.framebufferHeight == aEg.displayHeight) {
            return framebuffer;
        }

        if (framebuffer != null) {
            framebuffer.deleteFramebuffer();
        }

        return new Framebuffer(aEg.displayWidth, aEg.displayHeight, false);
    }

    public static Vec3 getRenderPos(double var0, double var2, double var4) {
        aEg.getRenderManager();
        var0 -= MCPCompat.renderPosX();
        aEg.getRenderManager();
        var2 -= MCPCompat.renderPosY();
        aEg.getRenderManager();
        var4 -= MCPCompat.renderPosZ();
        return new Vec3(var0, var2, var4);
    }

    public static void glVertex3D(Vec3 vec) {
        GL11.glVertex3d(vec.xCoord, vec.yCoord, vec.zCoord);
    }

    public static void drawBoundingBox(AxisAlignedBB box) {
        GL11.glBegin(7);
        glVertex3D(getRenderPos(box.minX, box.minY, box.minZ));
        glVertex3D(getRenderPos(box.minX, box.maxY, box.minZ));
        glVertex3D(getRenderPos(box.maxX, box.minY, box.minZ));
        glVertex3D(getRenderPos(box.maxX, box.maxY, box.minZ));
        glVertex3D(getRenderPos(box.maxX, box.minY, box.maxZ));
        glVertex3D(getRenderPos(box.maxX, box.maxY, box.maxZ));
        glVertex3D(getRenderPos(box.minX, box.minY, box.maxZ));
        glVertex3D(getRenderPos(box.minX, box.maxY, box.maxZ));
        vI();
        GL11.glBegin(7);
        glVertex3D(getRenderPos(box.maxX, box.maxY, box.minZ));
        glVertex3D(getRenderPos(box.maxX, box.minY, box.minZ));
        glVertex3D(getRenderPos(box.minX, box.maxY, box.minZ));
        glVertex3D(getRenderPos(box.minX, box.minY, box.minZ));
        glVertex3D(getRenderPos(box.minX, box.maxY, box.maxZ));
        glVertex3D(getRenderPos(box.minX, box.minY, box.maxZ));
        glVertex3D(getRenderPos(box.maxX, box.maxY, box.maxZ));
        glVertex3D(getRenderPos(box.maxX, box.minY, box.maxZ));
        vI();
        GL11.glBegin(7);
        glVertex3D(getRenderPos(box.minX, box.maxY, box.minZ));
        glVertex3D(getRenderPos(box.maxX, box.maxY, box.minZ));
        glVertex3D(getRenderPos(box.maxX, box.maxY, box.maxZ));
        glVertex3D(getRenderPos(box.minX, box.maxY, box.maxZ));
        glVertex3D(getRenderPos(box.minX, box.maxY, box.minZ));
        glVertex3D(getRenderPos(box.minX, box.maxY, box.maxZ));
        glVertex3D(getRenderPos(box.maxX, box.maxY, box.maxZ));
        glVertex3D(getRenderPos(box.maxX, box.maxY, box.minZ));
        vI();
        GL11.glBegin(7);
        glVertex3D(getRenderPos(box.minX, box.minY, box.minZ));
        glVertex3D(getRenderPos(box.maxX, box.minY, box.minZ));
        glVertex3D(getRenderPos(box.maxX, box.minY, box.maxZ));
        glVertex3D(getRenderPos(box.minX, box.minY, box.maxZ));
        glVertex3D(getRenderPos(box.minX, box.minY, box.minZ));
        glVertex3D(getRenderPos(box.minX, box.minY, box.maxZ));
        glVertex3D(getRenderPos(box.maxX, box.minY, box.maxZ));
        glVertex3D(getRenderPos(box.maxX, box.minY, box.minZ));
        vI();
        GL11.glBegin(7);
        glVertex3D(getRenderPos(box.minX, box.minY, box.minZ));
        glVertex3D(getRenderPos(box.minX, box.maxY, box.minZ));
        glVertex3D(getRenderPos(box.minX, box.minY, box.maxZ));
        glVertex3D(getRenderPos(box.minX, box.maxY, box.maxZ));
        glVertex3D(getRenderPos(box.maxX, box.minY, box.maxZ));
        glVertex3D(getRenderPos(box.maxX, box.maxY, box.maxZ));
        glVertex3D(getRenderPos(box.maxX, box.minY, box.minZ));
        glVertex3D(getRenderPos(box.maxX, box.maxY, box.minZ));
        vI();
        GL11.glBegin(7);
        glVertex3D(getRenderPos(box.minX, box.maxY, box.maxZ));
        glVertex3D(getRenderPos(box.minX, box.minY, box.maxZ));
        glVertex3D(getRenderPos(box.minX, box.maxY, box.minZ));
        glVertex3D(getRenderPos(box.minX, box.minY, box.minZ));
        glVertex3D(getRenderPos(box.maxX, box.maxY, box.minZ));
        glVertex3D(getRenderPos(box.maxX, box.minY, box.minZ));
        glVertex3D(getRenderPos(box.maxX, box.maxY, box.maxZ));
        glVertex3D(getRenderPos(box.maxX, box.minY, box.maxZ));
        vI();
    }

    @Generated
    private RenderUtil() {
        throw new UnsupportedOperationException("This is a utility class and cannot be instantiated");
    }
}
