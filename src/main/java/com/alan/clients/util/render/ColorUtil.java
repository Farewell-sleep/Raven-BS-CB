package com.alan.clients.util.render;

import com.alan.clients.Client;
import com.alan.clients.util.math.MathUtil;
import com.alan.clients.util.vector.Vector2d;
import com.alan.clients.ui.theme.ThemeManager;
import com.alan.clients.util.font.Font;
import java.awt.Color;
import org.lwjgl.opengl.GL11;

public final class ColorUtil {
    private ColorUtil() {
    }

    public static void glColor(int var0) {
        float f = (var0 >> 24 & 0xFF) / 255.0F;
        float f1 = (var0 >> 16 & 0xFF) / 255.0F;
        float f2 = (var0 >> 8 & 0xFF) / 255.0F;
        float f3 = (var0 & 0xFF) / 255.0F;
        GL11.glColor4f(f1, f2, f3, f);
    }

    public static void glColor(Color color) {
        GL11.glColor4f(color.getRed() / 255.0F, color.getGreen() / 255.0F, color.getBlue() / 255.0F, color.getAlpha() / 255.0F);
    }

    public static Color brighter(Color color, float var1) {
        return new Color(
            Math.max((int)(color.getRed() * var1), 0), Math.max((int)(color.getGreen() * var1), 0), Math.max((int)(color.getBlue() * var1), 0), color.getAlpha()
        );
    }

    public static Color darker(Color color, float var1) {
        int i = color.getRed();
        int j = color.getGreen();
        int k = color.getBlue();
        int l = color.getAlpha();
        int i1 = (int)(1.0F / (1.0F - var1));
        if (i == 0 && j == 0 && k == 0) {
            return new Color(i1, i1, i1, l);
        }

        if (i > 0 && i < i1) {
            i = i1;
        }

        if (j > 0 && j < i1) {
            j = i1;
        }

        if (k > 0 && k < i1) {
            k = i1;
        }

        return new Color(Math.min((int)(i / var1), 255), Math.min((int)(j / var1), 255), Math.min((int)(k / var1), 255), l);
    }

    public static Color withRed(Color color, int var1) {
        return new Color(var1, color.getGreen(), color.getBlue());
    }

    public static Color withGreen(Color color, int var1) {
        return new Color(color.getRed(), var1, color.getBlue());
    }

    public static Color withBlue(Color color, int var1) {
        return new Color(color.getRed(), color.getGreen(), var1);
    }

    public static Color withAlpha(Color color, int var1) {
        return var1 == color.getAlpha() ? color : new Color(color.getRed(), color.getGreen(), color.getBlue(), (int)MathUtil.c(0.0, 255.0, var1));
    }

    public static Color a(Color color, Color var1, double var2) {
        double d0 = 1.0 - var2;
        int i = (int)(color.getRed() * var2 + var1.getRed() * d0);
        int j = (int)(color.getGreen() * var2 + var1.getGreen() * d0);
        int k = (int)(color.getBlue() * var2 + var1.getBlue() * d0);
        return new Color(i, j, k);
    }

    public static double k(Vector2d vector2d) {
        return Math.sin(System.currentTimeMillis() / 600.0 + vector2d.getX() * 0.005 + vector2d.getY() * 0.06) * 0.5 + 0.5;
    }

    public static Color aB(int var0) {
        return Color.getHSBColor((float)(Math.ceil((System.currentTimeMillis() + var0) / 10.0) % 360.0 / 360.0), 0.6F, 1.0F);
    }

    public static void a(Font var0, String var1, double var2, double var4, boolean var6) {
        float f = 0.0F;
        ThemeManager adu = Client.a.getThemeManager();

        for (int i = 0; i < var1.length(); i++) {
            String s = String.valueOf(var1.charAt(i));
            Color color = a(adu.getTheme().rA(), adu.getTheme().rB(), Math.sin(i * 0.095) * 0.5 + 0.5);
            if (var6) {
                var0.b(s, var2 + f, var4, color.getRGB());
            } else {
                var0.a(s, var2 + f, var4, color.getRGB());
            }

            f += var0.getStringWidth(s) + 0.5F;
        }
    }

    public static void a(Font var0, String var1, double var2, double var4) {
        a(var0, var1, var2, var4, true);
    }
}
