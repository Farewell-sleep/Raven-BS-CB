package keystrokesmod.clickgui.rise.util;

import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.ScaledResolution;
import org.lwjgl.opengl.GL11;

import java.awt.Color;

public class RenderUtil {

    public static void roundedRectangle(double x, double y, double w, double h, double radius, Color color) {
        drawRoundedRect((float) x, (float) y, (float) w, (float) h, (float) radius, color.getRGB());
    }

    public static void roundedRectangle(float x, float y, float w, float h, float radius, Color color) {
        drawRoundedRect(x, y, w, h, radius, color.getRGB());
    }

    public static void drawRoundedRect(float x, float y, float w, float h, float radius, int color) {
        float a = (color >> 24 & 0xFF) / 255f;
        float r = (color >> 16 & 0xFF) / 255f;
        float g = (color >> 8 & 0xFF) / 255f;
        float b = (color & 0xFF) / 255f;

        boolean blend = GL11.glIsEnabled(GL11.GL_BLEND);
        GL11.glEnable(GL11.GL_BLEND);
        GL11.glDisable(GL11.GL_TEXTURE_2D);
        GL11.glBlendFunc(GL11.GL_SRC_ALPHA, GL11.GL_ONE_MINUS_SRC_ALPHA);
        GL11.glColor4f(r, g, b, a);

        GL11.glBegin(GL11.GL_QUADS);
        GL11.glVertex2f(x + radius, y);
        GL11.glVertex2f(x + w - radius, y);
        GL11.glVertex2f(x + w - radius, y + h);
        GL11.glVertex2f(x + radius, y + h);
        GL11.glEnd();

        GL11.glBegin(GL11.GL_QUADS);
        GL11.glVertex2f(x, y + radius);
        GL11.glVertex2f(x + radius, y + radius);
        GL11.glVertex2f(x + radius, y + h - radius);
        GL11.glVertex2f(x, y + h - radius);
        GL11.glEnd();

        GL11.glBegin(GL11.GL_QUADS);
        GL11.glVertex2f(x + w - radius, y + radius);
        GL11.glVertex2f(x + w, y + radius);
        GL11.glVertex2f(x + w, y + h - radius);
        GL11.glVertex2f(x + w - radius, y + h - radius);
        GL11.glEnd();

        drawCorner(x + radius, y + radius, radius, 180, 270);
        drawCorner(x + w - radius, y + radius, radius, 270, 360);
        drawCorner(x + radius, y + h - radius, radius, 90, 180);
        drawCorner(x + w - radius, y + h - radius, radius, 0, 90);

        GL11.glEnable(GL11.GL_TEXTURE_2D);
        if (!blend) GL11.glDisable(GL11.GL_BLEND);
    }

    private static void drawCorner(float cx, float cy, float radius, float startAngle, float endAngle) {
        int segments = 8;
        GL11.glBegin(GL11.GL_TRIANGLE_FAN);
        GL11.glVertex2f(cx, cy);
        for (int i = 0; i <= segments; i++) {
            double angle = Math.toRadians(startAngle + (endAngle - startAngle) * i / segments);
            GL11.glVertex2f(cx + (float) Math.cos(angle) * radius, cy + (float) Math.sin(angle) * radius);
        }
        GL11.glEnd();
    }

    public static void dropShadow(int passes, float x, float y, float w, float h, double alpha, double radius) {
        // Simplified shadow: draw a few layered dark rounded rects
        for (int i = 3; i >= 1; i--) {
            float expand = i * 2f;
            int a = (int) (alpha * (1 - i / 4f) * 0.3f);
            drawRoundedRect(x - expand, y - expand + 2, w + expand * 2, h + expand * 2,
                    (float) radius + expand, new Color(0, 0, 0, a).getRGB());
        }
    }

    public static void horizontalCenteredGradient(double x, double y, double w, double h, Color from, Color to) {
        // Left half: from -> transparent, right half: transparent -> from
        drawRoundedRect((float) x, (float) y, (float) (w / 2), (float) h, 0, from.getRGB());
        drawRoundedRect((float) (x + w / 2), (float) y, (float) (w / 2), (float) h, 0, to.getRGB());
    }

    public static void c(float cx, float cy, float radius, Color color) {
        drawCircle(cx, cy, radius, color.getRGB());
    }

    public static void c(double cx, double cy, double radius, Color color) {
        drawCircle((float) cx, (float) cy, (float) radius, color.getRGB());
    }

    public static void drawCircle(float cx, float cy, float radius, int color) {
        float a = (color >> 24 & 0xFF) / 255f;
        float r = (color >> 16 & 0xFF) / 255f;
        float g = (color >> 8 & 0xFF) / 255f;
        float b = (color & 0xFF) / 255f;

        boolean blend = GL11.glIsEnabled(GL11.GL_BLEND);
        GL11.glEnable(GL11.GL_BLEND);
        GL11.glDisable(GL11.GL_TEXTURE_2D);
        GL11.glBlendFunc(GL11.GL_SRC_ALPHA, GL11.GL_ONE_MINUS_SRC_ALPHA);
        GL11.glColor4f(r, g, b, a);

        int segments = 20;
        GL11.glBegin(GL11.GL_TRIANGLE_FAN);
        GL11.glVertex2f(cx, cy);
        for (int i = 0; i <= segments; i++) {
            double angle = 2 * Math.PI * i / segments;
            GL11.glVertex2f(cx + (float) Math.cos(angle) * radius, cy + (float) Math.sin(angle) * radius);
        }
        GL11.glEnd();

        GL11.glEnable(GL11.GL_TEXTURE_2D);
        if (!blend) GL11.glDisable(GL11.GL_BLEND);
    }

    public static void d(float x, float y, float w, float h, Color color) {
        drawRoundedRect(x, y, w, h, 0, color.getRGB());
    }

    public static void d(double x, double y, double w, double h, Color color) {
        drawRoundedRect((float) x, (float) y, (float) w, (float) h, 0, color.getRGB());
    }

    // Scissor
    public static void g(float x, float y, float w, float h) {
        Minecraft mc = Minecraft.getMinecraft();
        ScaledResolution sr = new ScaledResolution(mc);
        int scale = sr.getScaleFactor();
        GL11.glEnable(GL11.GL_SCISSOR_TEST);
        GL11.glScissor((int) (x * scale), mc.displayHeight - (int) ((y + h) * scale),
                (int) (w * scale), (int) (h * scale));
    }

    public static void g(double x, double y, double w, double h) {
        g((float) x, (float) y, (float) w, (float) h);
    }
}
