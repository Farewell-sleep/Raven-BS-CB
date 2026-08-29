package keystrokesmod.ui;

import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.GuiScreen;
import net.minecraft.client.gui.ScaledResolution;
import org.lwjgl.opengl.GL11;

import java.awt.Color;

public class StartupAnimation extends GuiScreen {

    private final Minecraft mc = Minecraft.getMinecraft();
    private long startTime = -1;
    private boolean finished = false;

    private static final long FADE_IN = 600L;
    private static final long PROGRESS = 1800L;
    private static final long FADE_OUT = 500L;
    private static final long TOTAL = FADE_IN + PROGRESS + FADE_OUT;

    @Override
    public void initGui() {
        startTime = System.currentTimeMillis();
        finished = false;
    }

    @Override
    public void drawScreen(int mouseX, int mouseY, float partialTicks) {
        if (startTime == -1) startTime = System.currentTimeMillis();

        long elapsed = System.currentTimeMillis() - startTime;
        ScaledResolution sr = new ScaledResolution(mc);
        int sw = sr.getScaledWidth();
        int sh = sr.getScaledHeight();

        drawRect(0, 0, sw, sh, 0xFF000000);

        float textAlpha, progress, collapse = 0f;

        if (elapsed < FADE_IN) {
            textAlpha = easeOutCubic(elapsed / (float) FADE_IN);
            progress = 0f;
        } else if (elapsed < FADE_IN + PROGRESS) {
            textAlpha = 1f;
            progress = easeOutCubic((elapsed - FADE_IN) / (float) PROGRESS);
        } else if (elapsed < TOTAL) {
            float p = (elapsed - FADE_IN - PROGRESS) / (float) FADE_OUT;
            textAlpha = 1f - easeInCubic(p);
            progress = 1f;
            collapse = easeInOutCubic(p);
        } else {
            if (!finished) {
                finished = true;
                mc.displayGuiScreen(null);
            }
            return;
        }

        String text = "Starting Raven";
        int tw = mc.fontRendererObj.getStringWidth(text);
        float tx = (sw - tw) / 2f;
        float ty = sh / 2f - 30f;
        int tc = new Color(255, 255, 255, (int) (textAlpha * 255)).getRGB();
        mc.fontRendererObj.drawStringWithShadow(text, tx, ty, tc);

        int barW = Math.min(sw / 3, 240);
        int barH = 6;
        float bx = (sw - barW) / 2f;
        float by = sh / 2f + 10f;
        float filled = barW * progress;

        if (collapse > 0) {
            float c = barW * collapse * 0.5f;
            bx += c;
            filled = Math.max(0, filled - c * 2);
            if (filled <= 0) return;
        }

        drawRoundedRect(bx, by, barW, barH, barH / 2f, new Color(255, 255, 255, (int) (30 * (1f - collapse))).getRGB());
        if (filled > 0) {
            drawRoundedRect(bx, by, filled, barH, barH / 2f, new Color(255, 255, 255, (int) (255 * (1f - collapse))).getRGB());
        }
    }

    private void drawRoundedRect(float x, float y, float w, float h, float r, int color) {
        if (w <= 0 || h <= 0) return;
        if (w < r * 2) r = w / 2;
        if (h < r * 2) r = h / 2;

        GL11.glPushMatrix();
        GL11.glEnable(GL11.GL_BLEND);
        GL11.glDisable(GL11.GL_TEXTURE_2D);
        GL11.glBlendFunc(GL11.GL_SRC_ALPHA, GL11.GL_ONE_MINUS_SRC_ALPHA);
        GL11.glEnable(GL11.GL_LINE_SMOOTH);

        float a = (float) (color >> 24 & 255) / 255f;
        float rr = (float) (color >> 16 & 255) / 255f;
        float gg = (float) (color >> 8 & 255) / 255f;
        float bb = (float) (color & 255) / 255f;
        GL11.glColor4f(rr, gg, bb, a);

        GL11.glBegin(GL11.GL_POLYGON);
        for (int i = 0; i <= 90; i += 3) {
            double ang = Math.toRadians(i);
            GL11.glVertex2d(x + r + Math.cos(ang) * r, y + r + Math.sin(ang) * r);
        }
        for (int i = 90; i <= 180; i += 3) {
            double ang = Math.toRadians(i);
            GL11.glVertex2d(x + w - r + Math.cos(ang) * r, y + r + Math.sin(ang) * r);
        }
        for (int i = 180; i <= 270; i += 3) {
            double ang = Math.toRadians(i);
            GL11.glVertex2d(x + w - r + Math.cos(ang) * r, y + h - r + Math.sin(ang) * r);
        }
        for (int i = 270; i <= 360; i += 3) {
            double ang = Math.toRadians(i);
            GL11.glVertex2d(x + r + Math.cos(ang) * r, y + h - r + Math.sin(ang) * r);
        }
        GL11.glEnd();

        GL11.glEnable(GL11.GL_TEXTURE_2D);
        GL11.glDisable(GL11.GL_BLEND);
        GL11.glPopMatrix();
    }

    private float easeOutCubic(float t) { return 1f - (float) Math.pow(1f - t, 3); }
    private float easeInCubic(float t) { return t * t * t; }
    private float easeInOutCubic(float t) {
        return t < 0.5f ? 4f * t * t * t : 1f - (float) Math.pow(-2f * t + 2f, 3) / 2f;
    }

    @Override
    public boolean doesGuiPauseGame() { return false; }
}
