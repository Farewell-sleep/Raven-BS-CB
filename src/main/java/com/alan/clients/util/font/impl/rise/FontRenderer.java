package com.alan.clients.util.font.impl.rise;

import com.alan.clients.util.font.impl.rise.FontCharacter;
import com.alan.clients.util.font.impl.rise.GlyphCache;
import com.alan.clients.util.render.ColorUtil;
import java.awt.Color;
import java.awt.Font;
import java.awt.FontMetrics;
import java.awt.Graphics2D;
import java.awt.RenderingHints;
import java.awt.font.FontRenderContext;
import java.awt.geom.AffineTransform;
import java.awt.geom.Rectangle2D;
import java.awt.image.BufferedImage;
import java.lang.Character.UnicodeScript;
import java.nio.ByteBuffer;
import net.minecraft.client.Minecraft;
import net.minecraft.client.renderer.GlStateManager;
import net.minecraft.util.MathHelper;
import org.lwjgl.BufferUtils;
import org.lwjgl.opengl.GL11;

public class FontRenderer extends com.alan.clients.util.font.Font {
    private static final String ALPHABET = "ABCDEFGHOKLMNOPQRSTUVWXYZabcdefghijklmnopqrstuvwxyz";
    private static final String COLOR_CODE_CHARACTERS = "0123456789abcdefklmnor";
    private static final Color TRANSPARENT_COLOR = new Color(255, 255, 255, 0);
    private static final float SCALE = 0.5F;
    private static final float SCALE_INVERSE = 2.0F;
    private static final char COLOR_INVOKER = '§';
    private static final int[] COLOR_CODES = new int[32];
    private static final int LATIN_MAX_AMOUNT = 256;
    private static final int INTERNATIONAL_MAX_AMOUNT = 65535;
    private static final int MARGIN_WIDTH = 4;
    private static final int MASK = 255;
    private final Font font;
    private final boolean aII;
    private final float fontHeight;
    private final FontCharacter[] defaultCharacters = new FontCharacter[256];
    private final FontCharacter[] internationalCharacters = new FontCharacter[65535];
    private final FontCharacter[] boldCharacters = new FontCharacter[256];
    private boolean aIN = true;
    private boolean aIO = false;
    private GlyphCache aIP;
    private GlyphCache aIQ;
    private GlyphCache aIR;
    private GlyphCache aIS;

    public void a(GlyphCache var1) {
        this.aIP = var1;
    }

    public void b(GlyphCache var1) {
        this.aIQ = var1;
    }

    public void c(GlyphCache var1) {
        this.aIR = var1;
    }

    public void d(GlyphCache var1) {
        this.aIS = var1;
    }

    public FontRenderer(Font font, boolean var2, boolean var3, boolean var4) {
        calculateColorCodes();
        this.aIN = var3;
        this.font = font;
        this.aII = var2;
        this.fontHeight = (float)(
            font.getStringBounds("ABCDEFGHOKLMNOPQRSTUVWXYZabcdefghijklmnopqrstuvwxyz", new FontRenderContext(new AffineTransform(), var3, var2)).getHeight()
                / 2.0
        );
        this.fillCharacters(this.defaultCharacters, 0);
        this.fillCharacters(this.boldCharacters, 1);
        this.aIO = var4;
        if (this.aIO) {
            this.fillCharacters(this.internationalCharacters, 0);
        }
    }

    public FontRenderer(Font font, boolean var2, boolean var3) {
        calculateColorCodes();
        this.aIN = var3;
        this.font = font;
        this.aII = var2;
        this.fontHeight = (float)(
            font.getStringBounds("ABCDEFGHOKLMNOPQRSTUVWXYZabcdefghijklmnopqrstuvwxyz", new FontRenderContext(new AffineTransform(), var3, var2)).getHeight()
                / 2.0
        );
        this.fillCharacters(this.defaultCharacters, 0);
        this.fillCharacters(this.boldCharacters, 1);
    }

    public FontRenderer(Font font, boolean var2) {
        calculateColorCodes();
        this.font = font;
        this.aII = var2;
        this.fontHeight = (float)(
            font.getStringBounds("ABCDEFGHOKLMNOPQRSTUVWXYZabcdefghijklmnopqrstuvwxyz", new FontRenderContext(new AffineTransform(), true, var2)).getHeight()
                / 2.0
        );
        this.fillCharacters(this.defaultCharacters, 0);
        this.fillCharacters(this.boldCharacters, 1);
    }

    public static void calculateColorCodes() {
        for (int i = 0; i < 32; i++) {
            int j = (i >> 3 & 1) * 85;
            int k = (i >> 2 & 1) * 170 + j;
            int l = (i >> 1 & 1) * 170 + j;
            int i1 = (i & 1) * 170 + j;
            if (i == 6) {
                k += 85;
            }

            if (i >= 16) {
                k /= 4;
                l /= 4;
                i1 /= 4;
            }

            COLOR_CODES[i] = (k & 0xFF) << 16 | (l & 0xFF) << 8 | i1 & 0xFF;
        }
    }

    public void fillCharacters(FontCharacter[] var1, int var2) {
        Font font = this.font.deriveFont(var2);
        Graphics2D graphics2d = (Graphics2D)new BufferedImage(1, 1, 2).getGraphics();
        FontMetrics fontmetrics = graphics2d.getFontMetrics(font);

        for (int i = 0; i < var1.length; i++) {
            char c0 = (char)i;
            Rectangle2D rectangle2d = fontmetrics.getStringBounds(c0 + "", graphics2d);
            BufferedImage bufferedimage = new BufferedImage(
                MathHelper.ceiling_float_int((float)rectangle2d.getWidth()) + 8, MathHelper.ceiling_float_int((float)rectangle2d.getHeight()), 2
            );
            Graphics2D graphics2d1 = (Graphics2D)bufferedimage.getGraphics();
            graphics2d1.setFont(font);
            int j = bufferedimage.getWidth();
            int k = bufferedimage.getHeight();
            graphics2d1.setColor(TRANSPARENT_COLOR);
            graphics2d1.fillRect(0, 0, j, k);
            this.setRenderHints(graphics2d1);
            graphics2d1.drawString(c0 + "", 4, font.getSize());
            int l = GL11.glGenTextures();
            this.uploadTexture(l, bufferedimage, j, k);
            var1[i] = new FontCharacter(l, j, k);
        }
    }

    public void setRenderHints(Graphics2D renderHints) {
        renderHints.setColor(Color.WHITE);
        if (this.aIN) {
            renderHints.setRenderingHint(RenderingHints.KEY_TEXT_ANTIALIASING, RenderingHints.VALUE_TEXT_ANTIALIAS_ON);
            renderHints.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
        }

        renderHints.setRenderingHint(RenderingHints.KEY_RENDERING, RenderingHints.VALUE_RENDER_QUALITY);
        renderHints.setRenderingHint(
            RenderingHints.KEY_FRACTIONALMETRICS, this.aII ? RenderingHints.VALUE_FRACTIONALMETRICS_ON : RenderingHints.VALUE_FRACTIONALMETRICS_OFF
        );
    }

    public void uploadTexture(int var1, BufferedImage image, int var3, int var4) {
        int[] aint = image.getRGB(0, 0, var3, var4, new int[var3 * var4], 0, var3);
        ByteBuffer bytebuffer = BufferUtils.createByteBuffer(var3 * var4 * 4);

        for (int i = 0; i < var4; i++) {
            for (int j = 0; j < var3; j++) {
                int k = aint[j + i * var3];
                bytebuffer.put((byte)(k >> 16 & 0xFF));
                bytebuffer.put((byte)(k >> 8 & 0xFF));
                bytebuffer.put((byte)(k & 0xFF));
                bytebuffer.put((byte)(k >> 24 & 0xFF));
            }
        }

        bytebuffer.flip();
        GlStateManager.bindTexture(var1);
        GL11.glTexParameteri(3553, 10241, 9728);
        GL11.glTexParameteri(3553, 10240, 9728);
        GL11.glTexImage2D(3553, 0, 6408, var3, var4, 0, 6408, 5121, bytebuffer);
    }

    @Override
    public int a(String var1, double var2, double var4, int var6) {
        return this.b(var1, var2, var4, var6, false);
    }

    @Override
    public int drawString(String var1, double var2, double var4, int var6) {
        return this.b(var1, var2 - (this.getStringWidth(var1) >> 1), var4, var6, false);
    }

    @Override
    public int drawCenteredString(String var1, double var2, double var4, int var6) {
        return this.b(var1, var2 - this.getStringWidth(var1), var4, var6, false);
    }

    @Override
    public int b(String var1, double var2, double var4, int var6) {
        return this.b(var1, var2, var4, var6, false);
    }

    public void drawCenteredStringWithShadow(String var1, float var2, float var3, int var4) {
        this.b(var1, var2 - (this.getStringWidth(var1) >> 1), var3, var4, false);
    }

    @Override
    public int b(String var1, double var2, double var4, int var6, boolean var7) {
        if (var1 == null) {
            return 0;
        }

        if (requiresInternationalFont(var1)) {
            return Minecraft.getMinecraft().fontRendererObj.drawString(var1, (float)var2, (float)var4, var6, var7);
        }

        FontCharacter[] aage = this.aIO ? this.internationalCharacters : this.defaultCharacters;
        double d0 = var2;
        GL11.glPushMatrix();
        GL11.glPushAttrib(1048575);
        GL11.glEnable(3553);
        GL11.glEnable(3042);
        GL11.glBlendFunc(770, 771);
        GL11.glScalef(0.5F, 0.5F, 0.5F);
        double d1 = var2 - 2.0;
        double d2 = var4 - 2.0;
        double d3 = d1 * 2.0;
        double d4 = d2 * 2.0;
        double d5 = d4 - this.fontHeight / 5.0F;
        double d6 = d3;
        ColorUtil.glColor(var7 ? Color.white.getRGB() : var6);
        String s = var1.replaceAll("§l", "");

        try {
            char[] achar = s.toCharArray();
            int i = (int)(this.height() * 2.0F);

            for (int j = 0; j < achar.length; j++) {
                char c0 = achar[j];
                if (c0 == '\n') {
                    d3 = d6;
                    d5 += i;
                } else if (c0 == 167 && j + 1 < achar.length) {
                    int k = "0123456789abcdefklmnor".indexOf(achar[++j]);
                    if (k >= 0 && k < COLOR_CODES.length) {
                        ColorUtil.glColor(new Color(COLOR_CODES[k]));
                    }
                } else {
                    char c1 = c0;
                    if (c1 >= 0 && c1 < aage.length) {
                        FontCharacter age = aage[c1];
                        if (age == null) {
                            if (this.aIP != null && g(c0)) {
                                this.aIP.a(c0, (float)d3, (float)d5);
                                d3 += this.aIP.j(c0);
                            } else if (this.aIR != null && h(c0)) {
                                this.aIR.a(c0, (float)d3, (float)d5);
                                d3 += this.aIR.j(c0);
                            } else if (this.aIS != null && i(c0)) {
                                this.aIS.a(c0, (float)d3, (float)d5);
                                d3 += this.aIS.j(c0);
                            } else if (this.aIQ != null) {
                                this.aIQ.a(c0, (float)d3, (float)d5);
                                d3 += this.aIQ.j(c0);
                            }
                        } else {
                            float f = age.getWidth();
                            age.render((float)d3, (float)d5);
                            d3 += f - 8.0F;
                        }
                    } else if (this.aIP != null && g(c0)) {
                        this.aIP.a(c0, (float)d3, (float)d5);
                        d3 += this.aIP.j(c0);
                    } else if (this.aIR != null && h(c0)) {
                        this.aIR.a(c0, (float)d3, (float)d5);
                        d3 += this.aIR.j(c0);
                    } else if (this.aIS != null && i(c0)) {
                        this.aIS.a(c0, (float)d3, (float)d5);
                        d3 += this.aIS.j(c0);
                    } else if (this.aIQ != null) {
                        this.aIQ.a(c0, (float)d3, (float)d5);
                        d3 += this.aIQ.j(c0);
                    }
                }
            }
        } catch (Exception exception) {
            exception.printStackTrace();
        }

        GL11.glDisable(3042);
        GL11.glDisable(3553);
        GlStateManager.bindTexture(0);
        GL11.glPopAttrib();
        GL11.glPopMatrix();
        return (int)(d3 - d0);
    }

    @Override
    public void a(char var1, int var2, int var3, Color color) {
        FontCharacter[] aage = this.aIO ? this.internationalCharacters : this.defaultCharacters;
        if (var1 < aage.length && aage[var1] != null) {
            FontCharacter age = aage[var1];
            GlStateManager.color(color.getRed() / 255.0F, color.getGreen() / 255.0F, color.getBlue() / 255.0F, color.getAlpha() / 255.0F);
            age.render(var2, var3);
        }
    }

    @Override
    public int getStringWidth(String var1) {
        String s = var1.replaceAll("§l", "");
        if (requiresInternationalFont(s)) {
            return Minecraft.getMinecraft().fontRendererObj.getStringWidth(s);
        }

        FontCharacter[] aage = this.aIO ? this.internationalCharacters : this.defaultCharacters;
        int i = s.length();
        int j = 0;

        for (int k = 0; k < i; k++) {
            char c0 = s.charAt(k);
            if (c0 == 167) {
                k++;
            } else {
                char c1 = c0;
                if (c1 >= 0 && c1 < aage.length) {
                    FontCharacter age = aage[c1];
                    if (age == null) {
                        if (this.aIP != null && g(c0)) {
                            j = (int)(j + this.aIP.j(c0));
                        } else if (this.aIR != null && h(c0)) {
                            j = (int)(j + this.aIR.j(c0));
                        } else if (this.aIS != null && i(c0)) {
                            j = (int)(j + this.aIS.j(c0));
                        } else if (this.aIQ != null) {
                            j = (int)(j + this.aIQ.j(c0));
                        }
                    } else {
                        j = (int)(j + (age.getWidth() - 8.0F));
                    }
                } else if (this.aIP != null && g(c0)) {
                    j = (int)(j + this.aIP.j(c0));
                } else if (this.aIR != null && h(c0)) {
                    j = (int)(j + this.aIR.j(c0));
                } else if (this.aIS != null && i(c0)) {
                    j = (int)(j + this.aIS.j(c0));
                } else if (this.aIQ != null) {
                    j = (int)(j + this.aIQ.j(c0));
                }
            }
        }

        return j / 2;
    }

    @Override
    public float height() {
        return this.fontHeight;
    }

    private static boolean g(char var0) {
        char c0 = var0;
        if (UnicodeScript.of(c0) == UnicodeScript.HAN) {
            return true;
        } else if (c0 >= 12288 && c0 <= 12351) {
            return true;
        }
        return c0 >= '\uff00' && c0 <= '\uffef' ? true : c0 == 8226 || c0 == 183 || c0 == 8230 || c0 == 8211 || c0 == 8212 || c0 >= 8216 && c0 <= 8223;
    }

    private static boolean h(char var0) {
        char c0 = var0;
        if (c0 >= 12352 && c0 <= 12447) {
            return true;
        } else if (c0 >= 12448 && c0 <= 12543) {
            return true;
        }
        return c0 >= 12784 && c0 <= 12799 ? true : c0 >= '･' && c0 <= 'ﾟ';
    }

    private static boolean i(char var0) {
        char c0 = var0;
        if (c0 >= 4352 && c0 <= 4607) {
            return true;
        } else if (c0 >= 12592 && c0 <= 12687) {
            return true;
        } else if (c0 >= '가' && c0 <= '\ud7af') {
            return true;
        }
        return c0 >= 'ꥠ' && c0 <= '\ua97f' ? true : c0 >= 'ힰ' && c0 <= '\ud7ff';
    }

    private static boolean requiresInternationalFont(String var0) {
        if (var0 != null && !var0.isEmpty()) {
            for (int i = 0; i < var0.length(); i += Character.charCount(var0.codePointAt(i))) {
                int j = var0.codePointAt(i);
                if (j > 127 && !g((char)j) && !h((char)j) && !i((char)j)) {
                    UnicodeScript unicodescript = UnicodeScript.of(j);
                    if (unicodescript != UnicodeScript.CYRILLIC
                        && unicodescript != UnicodeScript.LATIN
                        && unicodescript != UnicodeScript.COMMON
                        && unicodescript != UnicodeScript.INHERITED) {
                        return true;
                    }
                }
            }

            return false;
        }
        return false;
    }
}
