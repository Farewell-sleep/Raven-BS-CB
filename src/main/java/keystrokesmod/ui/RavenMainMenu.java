package keystrokesmod.ui;

import keystrokesmod.clickgui.LiquidGlassClickGui;
import keystrokesmod.module.impl.client.CustomMainMenu;
import keystrokesmod.module.impl.client.Gui;
import keystrokesmod.utility.font.RavenFontRenderer;
import keystrokesmod.utility.shader.RoundedUtils;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.GuiMultiplayer;
import net.minecraft.client.gui.GuiOptions;
import net.minecraft.client.gui.GuiScreen;
import net.minecraft.client.gui.GuiSelectWorld;
import net.minecraft.client.renderer.GlStateManager;
import org.lwjgl.input.Keyboard;
import org.lwjgl.opengl.GL11;

import java.awt.Color;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

/**
 * Rise 6.9.5 style main menu - exact color match.
 * Buttons: dark purple-gray bg + magenta-to-indigo gradient border + indigo text.
 */
public class RavenMainMenu extends GuiScreen {
    private static final Minecraft mc = Minecraft.getMinecraft();

    // Rise MenuColors exact values
    private static final Color COL_BTN_BG = new Color(83, 69, 105, 56);
    private static final Color COL_BORDER_1 = new Color(186, 41, 171, 128); // magenta
    private static final Color COL_BORDER_2 = new Color(48, 53, 97, 128);  // deep indigo
    private static final Color COL_TEXT = new Color(89, 99, 188, 255);     // indigo
    private static final Color COL_TEXT_HOVER = new Color(140, 150, 230, 255);

    private final List<MenuButton> buttons = new ArrayList<>();
    private long openTime;
    private long lastFrame;

    private static class MenuButton {
        String label;
        float x, y, w, h;
        Runnable action;
        float hoverAnim;
        long hoverStart;
        boolean lastHovered;

        MenuButton(String label, float x, float y, float w, float h, Runnable action) {
            this.label = label; this.x = x; this.y = y; this.w = w; this.h = h; this.action = action;
            this.hoverStart = System.currentTimeMillis();
        }

        void draw(int mouseX, int mouseY, float dt, float globalAlpha) {
            boolean hovered = mouseX >= x && mouseX <= x + w && mouseY >= y && mouseY <= y + h;
            if (hovered != lastHovered) { hoverStart = System.currentTimeMillis(); lastHovered = hovered; }
            hoverAnim += ((hovered ? 1 : 0) - hoverAnim) * Math.min(1, dt * 8f);
            float radius = 5f; // Rise uses 5px radius

            // Background - dark purple-gray, brighter on hover
            int bgAlpha = (int)(56 + hoverAnim * 40);
            RoundedUtils.drawRound(x, y, w, h, radius,
                    new Color(COL_BTN_BG.getRed(), COL_BTN_BG.getGreen(), COL_BTN_BG.getBlue(),
                            (int)(bgAlpha * globalAlpha)));

            // Border - purple (Rise gradient border simplified)
            drawBorder(x, y, w, h, radius,
                    new Color(120, 80, 160, (int)(100 * globalAlpha)));

            // Text - indigo, brighter on hover
            RavenFontRenderer sf = Gui.getClickGuiHeaderFontRenderer();
            int tw = sf.getStringWidth(label);
            Color textCol = hovered ? COL_TEXT_HOVER : COL_TEXT;
            sf.drawStringWithShadow(label, x + (w - tw) / 2f, y + (h - sf.getFontHeight()) / 2f,
                    new Color(textCol.getRed(), textCol.getGreen(), textCol.getBlue(),
                            (int)(textCol.getAlpha() * globalAlpha)).getRGB());
        }

        boolean clicked(int mouseX, int mouseY, int button) {
            if (button == 0 && mouseX >= x && mouseX <= x + w && mouseY >= y && mouseY <= y + h) {
                action.run(); return true;
            }
            return false;
        }
    }

    @Override
    public void initGui() {
        super.initGui();
        openTime = System.currentTimeMillis();
        lastFrame = System.currentTimeMillis();
        Keyboard.enableRepeatEvents(false);
        initButtons();
    }

    private void initButtons() {
        buttons.clear();
        // Rise dimensions: 180x24, gap 6, half-width 87
        float bw = 180, bh = 24, gap = 6;
        float bx = width / 2f - bw / 2f;
        float by = height / 2f - 12 - 3 - 12; // Rise: j - 12 - 3 - 12

        buttons.add(new MenuButton("Singleplayer", bx, by, bw, bh,
                () -> mc.displayGuiScreen(new GuiSelectWorld(this))));
        buttons.add(new MenuButton("Multiplayer", bx, by + (bh + gap), bw, bh,
                () -> mc.displayGuiScreen(new GuiMultiplayer(this))));
        // Half-width buttons side by side
        float halfW = (bw - gap) / 2f;
        buttons.add(new MenuButton("Options", bx, by + (bh + gap) * 2, halfW, bh,
                () -> mc.displayGuiScreen(new GuiOptions(this, mc.gameSettings))));
        buttons.add(new MenuButton("ClickGUI", bx + halfW + gap, by + (bh + gap) * 2, halfW, bh,
                () -> {
                    if (Gui.liquidGlassClickGui == null) Gui.liquidGlassClickGui = new LiquidGlassClickGui();
                    mc.displayGuiScreen(Gui.liquidGlassClickGui);
                }));
    }

    @Override
    public void drawScreen(int mouseX, int mouseY, float partialTicks) {
        long now = System.currentTimeMillis();
        float dt = Math.min(50, now - lastFrame) / 1000f;
        lastFrame = now;
        float ease = easeOutQuint(Math.min(1, (now - openTime) / 600f));
        float time = (now - openTime) / 1000f;
        float animSpeed = CustomMainMenu.animationSpeed != null ? (float)CustomMainMenu.animationSpeed.getInput() : 1f;

        // Pure black background
        drawRect(0, 0, width, height, 0xFF000000);

        // Aurora gradient glow (Rise style - flowing blue/purple/pink)
        if (CustomMainMenu.backgroundStyle != null && (int)CustomMainMenu.backgroundStyle.getInput() == 0) {
            drawAurora(time * animSpeed, ease);
        }

        // Title - Rise uses 64px font, centered, slide down animation
        float titleY = height / 2f - 100 + (1 - ease) * 30f;
        RavenFontRenderer titleFont = Gui.getClickGuiHeaderFontRenderer();
        GlStateManager.pushMatrix();
        float titleScale = 3.5f;
        GlStateManager.translate(width / 2f, titleY, 0);
        GlStateManager.scale(titleScale, titleScale, 1);
        GlStateManager.translate(-width / 2f, -titleY, 0);
        int tw = titleFont.getStringWidth("Raven BS");
        titleFont.drawStringWithShadow("Raven BS", width / 2f - tw / 2f, titleY,
                new Color(200, 200, 220, (int)(230 * ease)).getRGB());
        GlStateManager.popMatrix();

        // Buttons
        for (MenuButton b : buttons) b.draw(mouseX, mouseY, dt, ease);

        // Bottom-right credits (Rise style)
        RavenFontRenderer sf = Gui.getClickGuiSettingFontRenderer();
        String c1 = "Made with liquid glass";
        String c2 = "Raven BS Client";
        sf.drawString(c1, width - sf.getStringWidth(c1) - 10, height - 24,
                new Color(100, 100, 120, (int)(130 * ease)).getRGB());
        sf.drawString(c2, width - sf.getStringWidth(c2) - 10, height - 12,
                new Color(80, 80, 100, (int)(100 * ease)).getRGB());
    }

    private void drawAurora(float time, float alpha) {
        // Large flowing radial gradients from left side (Rise aurora style)
        // Deep blue blob
        float bx1 = width * 0.12f + (float)Math.sin(time * 0.3f) * 50f;
        float by1 = height * 0.2f + (float)Math.cos(time * 0.25f) * 40f;
        drawGlow(bx1, by1, width * 0.55f, height * 0.55f, new Color(30, 50, 110, 70), alpha);

        // Purple blob
        float bx2 = width * 0.08f + (float)Math.cos(time * 0.22f) * 60f;
        float by2 = height * 0.5f + (float)Math.sin(time * 0.3f) * 50f;
        drawGlow(bx2, by2, width * 0.5f, height * 0.6f, new Color(110, 40, 140, 60), alpha);

        // Magenta/pink blob
        float bx3 = width * 0.1f + (float)Math.sin(time * 0.28f) * 45f;
        float by3 = height * 0.78f + (float)Math.cos(time * 0.2f) * 40f;
        drawGlow(bx3, by3, width * 0.45f, height * 0.5f, new Color(170, 40, 120, 55), alpha);

        // Subtle cyan accent
        float bx4 = width * 0.18f + (float)Math.cos(time * 0.35f) * 30f;
        float by4 = height * 0.35f + (float)Math.sin(time * 0.28f) * 30f;
        drawGlow(bx4, by4, width * 0.35f, height * 0.4f, new Color(40, 80, 130, 40), alpha);
    }

    private void drawGlow(float cx, float cy, float w, float h, Color color, float alpha) {
        if (alpha < 0.02f) return;
        GlStateManager.pushMatrix();
        GlStateManager.enableBlend();
        GlStateManager.disableAlpha();
        GlStateManager.disableTexture2D();
        GlStateManager.blendFunc(GL11.GL_SRC_ALPHA, GL11.GL_ONE_MINUS_SRC_ALPHA);
        GL11.glBegin(GL11.GL_TRIANGLE_FAN);
        setColor(new Color(color.getRed(), color.getGreen(), color.getBlue(),
                (int)(color.getAlpha() * alpha)));
        GL11.glVertex2f(cx, cy);
        for (int i = 0; i <= 48; i++) {
            double ang = Math.toRadians(i * 360.0 / 48);
            setColor(new Color(color.getRed(), color.getGreen(), color.getBlue(), 0));
            GL11.glVertex2f(cx + (float)(Math.cos(ang) * w / 2f), cy + (float)(Math.sin(ang) * h / 2f));
        }
        GL11.glEnd();
        GlStateManager.enableTexture2D();
        GlStateManager.enableAlpha();
        GlStateManager.disableBlend();
        GlStateManager.popMatrix();
    }

    private void setColor(Color c) {
        GlStateManager.color(c.getRed() / 255f, c.getGreen() / 255f, c.getBlue() / 255f, c.getAlpha() / 255f);
    }

    private static void drawBorder(float x, float y, float w, float h, float r, Color color) {
        GlStateManager.enableBlend();
        GlStateManager.blendFunc(GL11.GL_SRC_ALPHA, GL11.GL_ONE_MINUS_SRC_ALPHA);
        drawRect((int)(x + r), (int)y, (int)(w - r * 2), 1, color.getRGB());
        drawRect((int)(x + r), (int)(y + h - 1), (int)(w - r * 2), 1, color.getRGB());
        drawRect((int)x, (int)(y + r), 1, (int)(h - r * 2), color.getRGB());
        drawRect((int)(x + w - 1), (int)(y + r), 1, (int)(h - r * 2), color.getRGB());
        GlStateManager.disableBlend();
    }

    private float easeOutQuint(float t) {
        return 1 - (float) Math.pow(1 - t, 5);
    }

    @Override
    protected void mouseClicked(int mouseX, int mouseY, int mouseButton) throws IOException {
        for (MenuButton b : buttons) if (b.clicked(mouseX, mouseY, mouseButton)) return;
    }

    @Override
    protected void keyTyped(char typedChar, int keyCode) throws IOException { }

    @Override
    public boolean doesGuiPauseGame() { return false; }
}
