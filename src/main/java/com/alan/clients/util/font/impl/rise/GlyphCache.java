package com.alan.clients.util.font.impl.rise;

import com.alan.clients.util.font.impl.rise.FontCharacter;
import java.awt.Color;
import java.awt.Font;
import java.awt.Graphics2D;
import java.awt.RenderingHints;
import java.awt.geom.Rectangle2D;
import java.awt.image.BufferedImage;
import java.nio.ByteBuffer;
import java.util.concurrent.ConcurrentHashMap;
import net.minecraft.client.renderer.GlStateManager;
import net.minecraft.util.MathHelper;
import org.lwjgl.BufferUtils;
import org.lwjgl.opengl.Display;
import org.lwjgl.opengl.GL11;
import org.lwjgl.opengl.GLContext;

public final class GlyphCache {
    private static final Color aIU = new Color(255, 255, 255, 0);
    private static final int aIV = 4;
    private static final int aIW = 255;
    private final Font aIX;
    private final boolean aIY;
    private final boolean bl2;
    private final ConcurrentHashMap<Integer, Float> aJa = new ConcurrentHashMap();
    private final ConcurrentHashMap<Integer, FontCharacter> aJb = new ConcurrentHashMap();

    public GlyphCache(Font font, boolean bl, boolean bl2) {
        this.aIX = font;
        this.aIY = bl;
        this.bl2 = bl2;
    }

    public float j(char c2) {
        char c3 = c2;
        return this.aJa.computeIfAbsent(Integer.valueOf(c3), n2 -> Float.valueOf(this.k(c2))).floatValue();
    }

    public void a(char c2, float f2, float f3) {
        char c3 = c2;
        FontCharacter age2 = this.aJb.get(Integer.valueOf(c3));
        if (age2 == null) {
            age2 = this.l(c2);
            if (age2 == null) {
                return;
            }
            FontCharacter age3 = this.aJb.putIfAbsent(Integer.valueOf(c3), age2);
            if (age3 != null) {
                age2 = age3;
            }
        }
        age2.render(f2, f3);
    }

    private float k(char c2) {
        if (!this.aIX.canDisplay(c2)) {
            return 0.0f;
        }
        Graphics2D graphics2D = (Graphics2D)new BufferedImage(1, 1, 2).getGraphics();
        graphics2D.setFont(this.aIX);
        this.a(graphics2D);
        int n2 = MathHelper.ceiling_float_int((float)((float)graphics2D.getFontMetrics(this.aIX).getStringBounds(String.valueOf(c2), graphics2D).getWidth())) + 8;
        return Math.max(0.0f, (float)(n2 - 8));
    }

    private FontCharacter l(char c2) {
        if (!GlyphCache.tA()) {
            return null;
        }
        if (!this.aIX.canDisplay(c2)) {
            return null;
        }
        Graphics2D graphics2D = (Graphics2D)new BufferedImage(1, 1, 2).getGraphics();
        graphics2D.setFont(this.aIX);
        this.a(graphics2D);
        Rectangle2D rectangle2D = graphics2D.getFontMetrics(this.aIX).getStringBounds(String.valueOf(c2), graphics2D);
        BufferedImage bufferedImage = new BufferedImage(MathHelper.ceiling_float_int((float)((float)rectangle2D.getWidth())) + 8, MathHelper.ceiling_float_int((float)((float)rectangle2D.getHeight())), 2);
        Graphics2D graphics2D2 = (Graphics2D)bufferedImage.getGraphics();
        graphics2D2.setFont(this.aIX);
        this.a(graphics2D2);
        int width = bufferedImage.getWidth();
        int height = bufferedImage.getHeight();
        graphics2D2.setColor(aIU);
        graphics2D2.fillRect(0, 0, width, height);
        graphics2D2.setColor(Color.WHITE);
        graphics2D2.drawString(String.valueOf(c2), 4, this.aIX.getSize());
        int glGenTextures = GL11.glGenTextures();
        this.a(glGenTextures, bufferedImage, width, height);
        return new FontCharacter(glGenTextures, width, height);
    }

    private static boolean tA() {
        try {
            if (!Display.isCreated()) {
                return false;
            }
            GLContext.getCapabilities();
            return true;
        }
        catch (Throwable throwable) {
            return false;
        }
    }

    private void a(Graphics2D graphics2D) {
        graphics2D.setColor(Color.WHITE);
        if (this.bl2) {
            graphics2D.setRenderingHint(RenderingHints.KEY_TEXT_ANTIALIASING, RenderingHints.VALUE_TEXT_ANTIALIAS_ON);
            graphics2D.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
        }
        graphics2D.setRenderingHint(RenderingHints.KEY_RENDERING, RenderingHints.VALUE_RENDER_QUALITY);
        graphics2D.setRenderingHint(RenderingHints.KEY_FRACTIONALMETRICS, this.aIY ? RenderingHints.VALUE_FRACTIONALMETRICS_ON : RenderingHints.VALUE_FRACTIONALMETRICS_OFF);
    }

    private void a(int n2, BufferedImage bufferedImage, int n3, int n4) {
        int[] nArray = bufferedImage.getRGB(0, 0, n3, n4, new int[n3 * n4], 0, n3);
        ByteBuffer byteBuffer = BufferUtils.createByteBuffer(n3 * n4 * 4);
        for (int i2 = 0; i2 < n4; ++i2) {
            for (int i3 = 0; i3 < n3; ++i3) {
                int n5 = nArray[i3 + i2 * n3];
                byteBuffer.put((byte)(n5 >> 16 & 0xFF));
                byteBuffer.put((byte)(n5 >> 8 & 0xFF));
                byteBuffer.put((byte)(n5 & 0xFF));
                byteBuffer.put((byte)(n5 >> 24 & 0xFF));
            }
        }
        byteBuffer.flip();
        GlStateManager.bindTexture(n2);
        GL11.glTexParameteri(3553, 10241, 9728);
        GL11.glTexParameteri(3553, 10240, 9728);
        GL11.glTexImage2D(3553, 0, 6408, n3, n4, 0, 6408, 5121, byteBuffer);
    }
}
