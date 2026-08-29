package keystrokesmod.clickgui;

import keystrokesmod.module.Module;
import keystrokesmod.module.ModuleManager;
import keystrokesmod.module.impl.client.Gui;
import keystrokesmod.module.setting.Setting;
import keystrokesmod.module.setting.impl.ButtonSetting;
import keystrokesmod.module.setting.impl.KeySetting;
import keystrokesmod.module.setting.impl.SliderSetting;
import keystrokesmod.utility.Utils;
import keystrokesmod.utility.font.RavenFontRenderer;
import keystrokesmod.utility.shader.BlurUtils;
import keystrokesmod.utility.shader.RoundedUtils;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.GuiScreen;
import net.minecraft.client.gui.ScaledResolution;
import net.minecraft.client.renderer.GlStateManager;
import net.minecraft.util.MathHelper;
import org.lwjgl.input.Keyboard;
import org.lwjgl.input.Mouse;
import org.lwjgl.opengl.GL11;

import java.awt.Color;
import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * Liquid Glass ClickGUI - inspired by OpenMyau LiquidGlass + Rise MainMenu.
 * Multi-panel horizontal layout, frosted glass blur (no refraction distortion),
 * large continuous-curvature corners, soft shadows, hairline borders.
 */
public class LiquidGlassClickGui extends GuiScreen {
    private static final Minecraft mc = Minecraft.getMinecraft();

    // Layout
    private static final float PANEL_W = 130;
    private static final float HEADER_H = 30;
    private static final float RADIUS = 16;
    private static final float MODULE_ROW_H = 18;
    private static final float SETTING_ROW_H = 16;
    private static final int MAX_PANEL_CONTENT_H = 240;
    private static final float PANEL_GAP = 10;

    // Colors - Liquid Glass
    private static final Color GLASS_BG = new Color(30, 30, 42, 140);
    private static final Color GLASS_BORDER = new Color(255, 255, 255, 40);
    private static final Color SHADOW1 = new Color(0, 0, 0, 70);
    private static final Color SHADOW2 = new Color(0, 0, 0, 40);
    private static final Color SHADOW3 = new Color(0, 0, 0, 20);
    private static final Color TEXT_MAIN = new Color(235, 235, 245, 240);
    private static final Color TEXT_FAINT = new Color(150, 150, 170, 200);
    private static final Color ACCENT = new Color(100, 180, 255, 240);
    private static final Color ENABLED = new Color(80, 220, 140, 240);
    private static final Color HOVER_WASH = new Color(255, 255, 255, 22);
    private static final Color SETTING_BG = new Color(0, 0, 0, 50);
    private static final Color SEARCH_BG = new Color(30, 30, 42, 160);

    // State
    private final List<Panel> panels = new ArrayList<>();
    private final StringBuilder search = new StringBuilder();
    private boolean searchFocused;
    private float searchX, searchY, searchW = 200, searchH = 22;

    // Animation
    private long openTime;
    private long lastFrame;

    // Key binding
    private KeySetting bindingKey;
    private String bindingModule;

    // Slider drag
    private SliderSetting draggingSlider;
    private String draggingModule;

    private static final Module.category[] CATEGORIES = {
            Module.category.combat,
            Module.category.movement,
            Module.category.player,
            Module.category.world,
            Module.category.render,
            Module.category.minigames,
            Module.category.other,
            Module.category.client
    };

    public LiquidGlassClickGui() {
    }

    @Override
    public void initGui() {
        super.initGui();
        openTime = System.currentTimeMillis();
        lastFrame = System.currentTimeMillis();
        Keyboard.enableRepeatEvents(true);
        initPanels();
    }

    private void initPanels() {
        panels.clear();
        float px = 14;
        float py = 48;
        int idx = 0;
        for (Module.category cat : CATEGORIES) {
            List<Module> mods = getModules(cat);
            if (mods.isEmpty()) continue;
            Panel p = new Panel(capitalize(cat.name()), mods, idx);
            p.x = px;
            p.y = py;
            panels.add(p);
            px += PANEL_W + PANEL_GAP;
            idx++;
        }
        searchX = width / 2f - searchW / 2f;
        searchY = 12;
    }

    private List<Module> getModules(Module.category cat) {
        List<Module> list = new ArrayList<>();
        for (Module m : ModuleManager.modules) {
            if (m.moduleCategory() == cat && !m.isHidden()) {
                list.add(m);
            }
        }
        list.sort((a, b) -> a.getName().compareToIgnoreCase(b.getName()));
        return list;
    }

    @Override
    public void drawScreen(int mouseX, int mouseY, float partialTicks) {
        long now = System.currentTimeMillis();
        float dt = Math.min(50, now - lastFrame) / 1000f;
        lastFrame = now;

        float openProgress = Math.min(1, (now - openTime) / 350f);
        float ease = easeOutCubic(openProgress);

        // Dark backdrop
        drawRect(0, 0, width, height, new Color(5, 5, 10, (int)(160 * ease)).getRGB());

        // Ambient glow blobs
        drawGlowBlob(width * 0.12f, height * 0.1f, width * 0.25f, height * 0.22f,
                new Color(34, 197, 94, 28), ease);
        drawGlowBlob(width * 0.7f, height * 0.65f, width * 0.28f, height * 0.28f,
                new Color(125, 211, 252, 24), ease);

        // Frosted glass panels (pure translucent, no blur distortion)
        // (BlurUtils removed — it caused white overlay artifacts)

        // Title capsule (top-left)
        drawCapsule(12, 8, 110, 28, ease);
        RavenFontRenderer hf = Gui.getClickGuiHeaderFontRenderer();
        hf.drawStringWithShadow("Raven BS", 22, 13, TEXT_MAIN.getRGB());
        RavenFontRenderer sf = Gui.getClickGuiSettingFontRenderer();
        sf.drawString("Liquid Glass", 22, 13 + hf.getFontHeight() - 2, ACCENT.getRGB());

        // Search capsule (top-center)
        drawSearchBar(mouseX, ease);

        // Panels
        GlStateManager.pushMatrix();
        for (Panel p : panels) {
            p.draw(mouseX, mouseY, dt, ease);
        }
        GlStateManager.popMatrix();

        // Slider drag update
        if (draggingSlider != null) {
            updateSliderDrag(mouseX);
        }
    }

    private void drawGlowBlob(float cx, float cy, float w, float h, Color color, float alpha) {
        if (alpha < 0.05f) return;
        GlStateManager.pushMatrix();
        GlStateManager.enableBlend();
        GlStateManager.disableAlpha();
        GlStateManager.disableTexture2D();
        GlStateManager.blendFunc(GL11.GL_SRC_ALPHA, GL11.GL_ONE_MINUS_SRC_ALPHA);
        GL11.glBegin(GL11.GL_TRIANGLE_FAN);
        Color c = new Color(color.getRed(), color.getGreen(), color.getBlue(),
                (int)(color.getAlpha() * alpha));
        setGLColor(c);
        GL11.glVertex2f(cx, cy);
        for (int i = 0; i <= 32; i++) {
            double ang = Math.toRadians(i * 360.0 / 32);
            float rx = w / 2f, ry = h / 2f;
            setGLColor(new Color(c.getRed(), c.getGreen(), c.getBlue(), 0));
            GL11.glVertex2f(cx + (float)(Math.cos(ang) * rx), cy + (float)(Math.sin(ang) * ry));
        }
        GL11.glEnd();
        GlStateManager.enableTexture2D();
        GlStateManager.enableAlpha();
        GlStateManager.disableBlend();
        GlStateManager.popMatrix();
    }

    private void setGLColor(Color c) {
        GlStateManager.color(c.getRed() / 255f, c.getGreen() / 255f, c.getBlue() / 255f, c.getAlpha() / 255f);
    }

    private void drawCapsule(float x, float y, float w, float h, float alpha) {
        // Glass background
        RoundedUtils.drawRound(x, y, w, h, h / 2f,
                new Color(GLASS_BG.getRed(), GLASS_BG.getGreen(), GLASS_BG.getBlue(),
                        (int)(GLASS_BG.getAlpha() * alpha)));
        // Shadow
        RoundedUtils.drawRound(x, y + 2, w, h, h / 2f,
                new Color(0, 0, 0, (int)(50 * alpha)));
        // Border
        drawRoundedBorder(x, y, w, h, h / 2f,
                new Color(255, 255, 255, (int)(GLASS_BORDER.getAlpha() * alpha)));
    }

    private void drawSearchBar(int mouseX, float alpha) {
        boolean hovered = isHovered(mouseX, (int)searchY, searchX, searchW, searchH);
        Color bg = searchFocused ? new Color(40, 40, 58, (int)(200 * alpha))
                : new Color(SEARCH_BG.getRed(), SEARCH_BG.getGreen(), SEARCH_BG.getBlue(),
                        (int)(SEARCH_BG.getAlpha() * alpha));

        // Shadow
        RoundedUtils.drawRound(searchX, searchY + 2, searchW, searchH, searchH / 2f,
                new Color(0, 0, 0, (int)(50 * alpha)));
        // Background
        RoundedUtils.drawRound(searchX, searchY, searchW, searchH, searchH / 2f, bg);
        // Border
        drawRoundedBorder(searchX, searchY, searchW, searchH, searchH / 2f,
                searchFocused ? new Color(100, 180, 255, (int)(120 * alpha))
                        : new Color(255, 255, 255, (int)(GLASS_BORDER.getAlpha() * alpha)));

        // Search icon (magnifying glass - simple circle + handle)
        float ix = searchX + 12, iy = searchY + searchH / 2f;
        drawCircle(ix, iy - 1, 3.5f, new Color(150, 150, 170, (int)(180 * alpha)));
        drawCircle(ix + 3, iy + 2.5f, 1.2f, new Color(150, 150, 170, (int)(180 * alpha)));

        // Text
        RavenFontRenderer sf = Gui.getClickGuiSettingFontRenderer();
        String text = search.length() == 0 ? "Search modules..." : search.toString();
        int textColor = search.length() == 0
                ? new Color(120, 120, 140, (int)(160 * alpha)).getRGB()
                : new Color(TEXT_MAIN.getRed(), TEXT_MAIN.getGreen(), TEXT_MAIN.getBlue(),
                        (int)(TEXT_MAIN.getAlpha() * alpha)).getRGB();
        sf.drawString(text, searchX + 24, searchY + 6, textColor);

        // Cursor
        if (searchFocused && (System.currentTimeMillis() / 500) % 2 == 0) {
            float cx = searchX + 24 + sf.getStringWidth(search.toString());
            drawRect((int)cx, (int)(searchY + 5), 1, sf.getFontHeight() + 2,
                    new Color(200, 200, 220, (int)(200 * alpha)).getRGB());
        }
    }

    private void drawCircle(float cx, float cy, float r, Color color) {
        GlStateManager.enableBlend();
        GlStateManager.disableTexture2D();
        GlStateManager.blendFunc(GL11.GL_SRC_ALPHA, GL11.GL_ONE_MINUS_SRC_ALPHA);
        setGLColor(color);
        GL11.glBegin(GL11.GL_TRIANGLE_FAN);
        GL11.glVertex2d(cx, cy);
        for (int i = 0; i <= 20; i++) {
            double ang = Math.toRadians(i * 360.0 / 20);
            GL11.glVertex2d(cx + Math.cos(ang) * r, cy + Math.sin(ang) * r);
        }
        GL11.glEnd();
        GlStateManager.enableTexture2D();
        GlStateManager.disableBlend();
        GlStateManager.color(1, 1, 1, 1);
    }

    private void drawRoundedBorder(float x, float y, float w, float h, float r, Color color) {
        GlStateManager.enableBlend();
        GlStateManager.blendFunc(GL11.GL_SRC_ALPHA, GL11.GL_ONE_MINUS_SRC_ALPHA);
        drawRect((int)(x + r), (int)y, (int)(w - r * 2), 1, color.getRGB());
        drawRect((int)(x + r), (int)(y + h - 1), (int)(w - r * 2), 1, color.getRGB());
        drawRect((int)x, (int)(y + r), 1, (int)(h - r * 2), color.getRGB());
        drawRect((int)(x + w - 1), (int)(y + r), 1, (int)(h - r * 2), color.getRGB());
        GlStateManager.disableBlend();
    }

    private boolean isHovered(int mouseX, int mouseY, float x, float w, float h) {
        return mouseX >= x && mouseX <= x + w && mouseY >= searchY && mouseY <= searchY + h;
    }

    private float easeOutCubic(float t) {
        return 1 - (float) Math.pow(1 - t, 3);
    }

    private String capitalize(String s) {
        if (s == null || s.isEmpty()) return s;
        return s.substring(0, 1).toUpperCase() + s.substring(1).toLowerCase();
    }

    private void updateSliderDrag(int mouseX) {
        if (draggingSlider == null) return;
        // Find the panel and module to get bounds
        for (Panel p : panels) {
            for (ModuleEntry e : p.entries) {
                if (e.module.getName().equals(draggingModule)) {
                    float x = p.x + 8;
                    float w = PANEL_W - 20;
                    double pct = (mouseX - x - 4) / (double) (w - 8);
                    pct = Math.max(0, Math.min(1, pct));
                    double val = draggingSlider.getMin() + pct * (draggingSlider.getMax() - draggingSlider.getMin());
                    draggingSlider.setValueWithEvent(val);
                    return;
                }
            }
        }
    }

    // ========== Panel ==========
    private class Panel {
        final String name;
        final List<ModuleEntry> entries = new ArrayList<>();
        float x, y;
        boolean opened = true;
        boolean dragging;
        float dragOffX, dragOffY;
        int scroll;
        float animScroll;
        String filter = "";
        final int staggerIndex;
        long hoverStart;
        boolean lastHovered;
        boolean pressed;
        long pressStart;
        long releaseStart;
        long openStart;

        Panel(String name, List<Module> modules, int staggerIndex) {
            this.name = name;
            this.staggerIndex = staggerIndex;
            this.hoverStart = System.currentTimeMillis();
            this.releaseStart = System.currentTimeMillis();
            this.openStart = System.currentTimeMillis() - 500;
            for (Module m : modules) {
                entries.add(new ModuleEntry(m, this));
            }
        }

        void setFilter(String f) {
            String nf = f == null ? "" : f.trim().toLowerCase();
            if (!nf.equals(filter)) {
                filter = nf;
                scroll = 0;
                animScroll = 0;
            }
        }

        List<ModuleEntry> visibleEntries() {
            if (filter.isEmpty()) return entries;
            List<ModuleEntry> out = new ArrayList<>();
            for (ModuleEntry e : entries) {
                if (e.module.getName().toLowerCase().contains(filter)) {
                    out.add(e);
                }
            }
            return out;
        }

        int getContentHeight() {
            int h = 0;
            for (ModuleEntry e : visibleEntries()) {
                h += e.getHeight();
            }
            return h;
        }

        boolean isHovered(int mouseX, int mouseY) {
            float ch = Math.min(getContentHeight(), MAX_PANEL_CONTENT_H);
            return mouseX >= x && mouseX <= x + PANEL_W && mouseY >= y && mouseY <= y + HEADER_H + ch;
        }

        void draw(int mouseX, int mouseY, float dt, float globalEase) {
            long now = System.currentTimeMillis();
            long enterStart = openTime + staggerIndex * 60L;
            float enter = Math.min(1, Math.max(0, (now - enterStart) / 350f));
            float enterEase = easeOutCubic(enter) * globalEase;
            float rise = (1 - enterEase) * 18f;

            // Hover animation
            boolean hovered = isHovered(mouseX, mouseY);
            if (hovered != lastHovered) {
                hoverStart = now;
                lastHovered = hovered;
            }
            float hoverEase = hovered ? Math.min(1, (now - hoverStart) / 200f) : 1 - Math.min(1, (now - hoverStart) / 200f);

            // Press
            float pressEase;
            if (pressed) {
                pressEase = Math.min(1, (now - pressStart) / 150f);
            } else {
                pressEase = 1 - Math.min(1, (now - releaseStart) / 150f);
            }

            // Open/close
            float openProgress = opened ? Math.min(1, (now - openStart) / 250f) : 1 - Math.min(1, (now - openStart) / 250f);
            float contentH = Math.max(0, Math.min(getContentHeight(), MAX_PANEL_CONTENT_H) * openProgress);
            float panelH = HEADER_H + contentH + 4;

            float scale = 1 + hoverEase * 0.012f - pressEase * 0.03f;
            float midX = x + PANEL_W / 2f, midY = y + panelH / 2f;
            float drawY = y + rise;

            GlStateManager.pushMatrix();
            GlStateManager.translate(midX, midY, 0);
            GlStateManager.scale(scale, scale, 1);
            GlStateManager.translate(-midX, -midY, 0);

            int alpha = (int)(enterEase * 255);

            // Soft shadows (3 layers)
            RoundedUtils.drawRound(x, drawY + 2, PANEL_W, panelH, RADIUS,
                    new Color(0, 0, 0, (int)(SHADOW1.getAlpha() * enterEase)));
            RoundedUtils.drawRound(x, drawY + 4, PANEL_W, panelH, RADIUS,
                    new Color(0, 0, 0, (int)(SHADOW2.getAlpha() * enterEase)));
            RoundedUtils.drawRound(x, drawY + 6, PANEL_W, panelH, RADIUS,
                    new Color(0, 0, 0, (int)(SHADOW3.getAlpha() * enterEase)));

            // Glass body
            RoundedUtils.drawRound(x, drawY, PANEL_W, panelH, RADIUS,
                    new Color(GLASS_BG.getRed(), GLASS_BG.getGreen(), GLASS_BG.getBlue(),
                            (int)(GLASS_BG.getAlpha() * enterEase)));

            // Hover wash
            if (hoverEase > 0.02f) {
                RoundedUtils.drawRound(x, drawY, PANEL_W, panelH, RADIUS,
                        new Color(255, 255, 255, (int)(HOVER_WASH.getAlpha() * hoverEase * enterEase)));
            }

            // Hairline border
            drawRoundedBorder(x + 0.5f, drawY + 0.5f, PANEL_W - 1, panelH - 1, RADIUS,
                    new Color(255, 255, 255, (int)(GLASS_BORDER.getAlpha() * enterEase)));

            // Header text
            RavenFontRenderer sf = Gui.getClickGuiSettingFontRenderer();
            int headerColor = new Color(TEXT_MAIN.getRed(), TEXT_MAIN.getGreen(), TEXT_MAIN.getBlue(), alpha).getRGB();
            sf.drawStringWithShadow(name, x + 12, drawY + 10, headerColor);

            // Chevron
            drawChevron(x + PANEL_W - 16, drawY + 11, openProgress > 0.5f, alpha);

            // Content
            if (contentH > 1) {
                int maxScroll = Math.max(0, getContentHeight() - MAX_PANEL_CONTENT_H);
                if (scroll > maxScroll) scroll = maxScroll;
                if (scroll < 0) scroll = 0;
                animScroll += (scroll - animScroll) * Math.min(1, dt * 12f);

                int cy = (int)(drawY + HEADER_H + 2 - animScroll);
                int bottom = (int)(drawY + HEADER_H + contentH);

                ScaledResolution sr = new ScaledResolution(mc);
                double sf2 = sr.getScaleFactor();
                GL11.glEnable(GL11.GL_SCISSOR_TEST);
                GL11.glScissor((int)((x + 3) * sf2),
                        (int)((sr.getScaledHeight() - bottom) * sf2),
                        (int)((PANEL_W - 6) * sf2),
                        (int)((contentH + 2) * sf2));

                for (ModuleEntry e : visibleEntries()) {
                    e.setY(cy);
                    boolean rowHover = mouseX >= x + 4 && mouseX <= x + PANEL_W - 4
                            && mouseY >= cy && mouseY <= cy + MODULE_ROW_H;
                    e.draw(mouseX, mouseY, rowHover, alpha);
                    cy += e.getHeight();
                }

                GL11.glDisable(GL11.GL_SCISSOR_TEST);

                // Scrollbar
                if (getContentHeight() > MAX_PANEL_CONTENT_H && maxScroll > 0) {
                    float thumbH = Math.max(16, MAX_PANEL_CONTENT_H * MAX_PANEL_CONTENT_H / (float)getContentHeight());
                    float thumbY = drawY + HEADER_H + 2 + animScroll * (MAX_PANEL_CONTENT_H - thumbH) / maxScroll;
                    RoundedUtils.drawRound(x + PANEL_W - 5, thumbY, 2, thumbH, 1,
                            new Color(255, 255, 255, (int)(80 * enterEase)));
                }
            }

            GlStateManager.popMatrix();

            // Drag
            if (dragging) {
                x = mouseX - dragOffX;
                y = mouseY - dragOffY;
            }
        }

        private void drawChevron(float cx, float cy, boolean down, int alpha) {
            GlStateManager.enableBlend();
            GlStateManager.disableTexture2D();
            GlStateManager.blendFunc(GL11.GL_SRC_ALPHA, GL11.GL_ONE_MINUS_SRC_ALPHA);
            GlStateManager.color(1, 1, 1, 0.5f * (alpha / 255f));
            GL11.glLineWidth(1.5f);
            GL11.glBegin(GL11.GL_LINE_STRIP);
            if (down) {
                GL11.glVertex2d(cx, cy);
                GL11.glVertex2d(cx + 3, cy + 3);
                GL11.glVertex2d(cx + 6, cy);
            } else {
                GL11.glVertex2d(cx, cy + 3);
                GL11.glVertex2d(cx + 3, cy);
                GL11.glVertex2d(cx + 6, cy + 3);
            }
            GL11.glEnd();
            GL11.glLineWidth(1f);
            GlStateManager.enableTexture2D();
            GlStateManager.disableBlend();
            GlStateManager.color(1, 1, 1, 1);
        }

        void mouseDown(int mouseX, int mouseY, int button) {
            boolean headerHover = mouseX >= x && mouseX <= x + PANEL_W && mouseY >= y && mouseY <= y + HEADER_H;
            if (headerHover) {
                pressed = true;
                pressStart = System.currentTimeMillis();
                if (button == 0) {
                    dragging = true;
                    dragOffX = mouseX - x;
                    dragOffY = mouseY - y;
                } else if (button == 1) {
                    opened = !opened;
                    openStart = System.currentTimeMillis();
                }
                return;
            }
            if (!opened) return;
            int contentTop = (int)(y + HEADER_H);
            int contentBottom = (int)(y + HEADER_H + Math.min(getContentHeight(), MAX_PANEL_CONTENT_H));
            if (mouseY < contentTop || mouseY > contentBottom) return;
            int cy = contentTop + 2 - (int)animScroll;
            for (ModuleEntry e : visibleEntries()) {
                int eh = e.getHeight();
                if (mouseY >= cy && mouseY <= cy + eh) {
                    e.mouseDown(mouseX, mouseY, button);
                    return;
                }
                cy += eh;
            }
        }

        void mouseReleased(int mouseX, int mouseY, int button) {
            if (pressed) {
                pressed = false;
                releaseStart = System.currentTimeMillis();
            }
            dragging = false;
            if (!opened) return;
            int contentTop = (int)(y + HEADER_H);
            int contentBottom = (int)(y + HEADER_H + Math.min(getContentHeight(), MAX_PANEL_CONTENT_H));
            if (mouseY < contentTop || mouseY > contentBottom) return;
            int cy = contentTop + 2 - (int)animScroll;
            for (ModuleEntry e : visibleEntries()) {
                int eh = e.getHeight();
                if (mouseY >= cy && mouseY <= cy + eh) {
                    e.mouseReleased(mouseX, mouseY, button);
                    return;
                }
                cy += eh;
            }
        }

        void onScroll(int mouseX, int mouseY, int dir) {
            if (!opened) return;
            if (mouseX < x || mouseX > x + PANEL_W) return;
            int contentTop = (int)(y + HEADER_H);
            int contentBottom = (int)(y + HEADER_H + Math.min(getContentHeight(), MAX_PANEL_CONTENT_H));
            if (mouseY < contentTop || mouseY > contentBottom) return;
            scroll += dir * 22;
        }
    }

    // ========== ModuleEntry ==========
    private class ModuleEntry {
        final Module module;
        final Panel panel;
        int y;
        boolean expanded;
        float expandAnim;
        long expandStart;

        ModuleEntry(Module module, Panel panel) {
            this.module = module;
            this.panel = panel;
            this.expandStart = System.currentTimeMillis() - 500;
        }

        void setY(int y) {
            this.y = y;
        }

        int getHeight() {
            int settings = getVisibleSettings().size();
            float ea = expanded ? Math.min(1, (System.currentTimeMillis() - expandStart) / 200f)
                    : 1 - Math.min(1, (System.currentTimeMillis() - expandStart) / 200f);
            return (int)(MODULE_ROW_H + settings * SETTING_ROW_H * ea);
        }

        List<Setting> getVisibleSettings() {
            List<Setting> list = new ArrayList<>();
            for (Setting s : module.getSettings()) {
                if (s.visible && (s instanceof ButtonSetting || s instanceof SliderSetting || s instanceof KeySetting)) {
                    list.add(s);
                }
            }
            return list;
        }

        void draw(int mouseX, int mouseY, boolean rowHover, int panelAlpha) {
            RavenFontRenderer sf = Gui.getClickGuiSettingFontRenderer();
            float px = panel.x;

            // Hover background
            if (rowHover) {
                RoundedUtils.drawRound(px + 3, y, PANEL_W - 6, MODULE_ROW_H - 2, 5,
                        new Color(255, 255, 255, 18));
            }

            // Enabled indicator (left bar)
            if (module.isEnabled()) {
                RoundedUtils.drawRound(px + 4, y + 3, 2, MODULE_ROW_H - 6, 1, ENABLED);
            }

            // Module name
            int nameColor = module.isEnabled()
                    ? new Color(TEXT_MAIN.getRed(), TEXT_MAIN.getGreen(), TEXT_MAIN.getBlue(), panelAlpha).getRGB()
                    : new Color(TEXT_FAINT.getRed(), TEXT_FAINT.getGreen(), TEXT_FAINT.getBlue(), panelAlpha).getRGB();
            sf.drawString(module.getName(), px + 10, y + 4, nameColor);

            // Info suffix
            String info = module.getInfoUpdate();
            if (info != null && !info.isEmpty()) {
                float infoX = px + 10 + sf.getStringWidth(module.getName()) + 4;
                sf.drawString(info, infoX, y + 4,
                        new Color(ACCENT.getRed(), ACCENT.getGreen(), ACCENT.getBlue(), panelAlpha).getRGB());
            }

            // Toggle switch (right side)
            float sw = 22, sh = 9;
            float sx = px + PANEL_W - sw - 8;
            float sy = y + (MODULE_ROW_H - sh) / 2f;
            RoundedUtils.drawRound(sx, sy, sw, sh, sh / 2f,
                    module.isEnabled() ? new Color(80, 200, 140, 200) : new Color(60, 60, 75, 200));
            float knobX = module.isEnabled() ? sx + sw - sh + 0.5f : sx + 0.5f;
            RoundedUtils.drawRound(knobX, sy + 0.5f, sh - 1, sh - 1, (sh - 1) / 2f, Color.WHITE);

            // Expand chevron (if has settings)
            List<Setting> settings = getVisibleSettings();
            if (!settings.isEmpty()) {
                float ea = expanded ? Math.min(1, (System.currentTimeMillis() - expandStart) / 200f)
                        : 1 - Math.min(1, (System.currentTimeMillis() - expandStart) / 200f);
                float cx = sx - 10, cy = y + MODULE_ROW_H / 2f - 1;
                GlStateManager.pushMatrix();
                GlStateManager.translate(cx, cy, 0);
                GlStateManager.rotate(90 * ea, 0, 0, 1);
                sf.drawString(">", -2, 0, new Color(150, 150, 170, panelAlpha).getRGB());
                GlStateManager.popMatrix();
            }

            // Settings (expanded)
            float ea = expanded ? Math.min(1, (System.currentTimeMillis() - expandStart) / 200f)
                    : 1 - Math.min(1, (System.currentTimeMillis() - expandStart) / 200f);
            if (ea > 0.05f && !settings.isEmpty()) {
                int setY = y + (int)MODULE_ROW_H;
                for (int i = 0; i < settings.size(); i++) {
                    Setting s = settings.get(i);
                    float sy2 = setY + i * SETTING_ROW_H;
                    // Setting bg
                    RoundedUtils.drawRound(px + 6, sy2, PANEL_W - 12, SETTING_ROW_H - 2, 4,
                            new Color(0, 0, 0, (int)(60 * ea)));

                    if (s instanceof ButtonSetting) {
                        drawButtonSetting((ButtonSetting)s, px + 10, sy2, PANEL_W - 24, panelAlpha, module);
                    } else if (s instanceof SliderSetting) {
                        drawSliderSetting((SliderSetting)s, px + 10, sy2, PANEL_W - 24, panelAlpha, mouseX);
                    } else if (s instanceof KeySetting) {
                        drawKeySetting((KeySetting)s, px + 10, sy2, PANEL_W - 24, panelAlpha, module);
                    }
                }
            }
        }

        private void drawButtonSetting(ButtonSetting bs, float x, float y, float w, int alpha, Module module) {
            RavenFontRenderer sf = Gui.getClickGuiSettingFontRenderer();
            sf.drawString(bs.getName(), x, y + 3,
                    new Color(TEXT_FAINT.getRed(), TEXT_FAINT.getGreen(), TEXT_FAINT.getBlue(), alpha).getRGB());
            if (bs.isMethodButton) {
                float bw = 36;
                RoundedUtils.drawRound(x + w - bw - 2, y + 2, bw, SETTING_ROW_H - 6, 4,
                        new Color(80, 140, 255, 180));
                sf.drawString("Run", x + w - bw + 6, y + 3, Color.WHITE.getRGB());
            } else {
                float sw = 18, sh = 7;
                float sx = x + w - sw - 2;
                float sy = y + (SETTING_ROW_H - sh) / 2f;
                RoundedUtils.drawRound(sx, sy, sw, sh, sh / 2f,
                        bs.isToggled() ? new Color(80, 200, 140, 200) : new Color(60, 60, 75, 200));
                float kx = bs.isToggled() ? sx + sw - sh + 0.5f : sx + 0.5f;
                RoundedUtils.drawRound(kx, sy + 0.5f, sh - 1, sh - 1, (sh - 1) / 2f, Color.WHITE);
            }
        }

        private void drawSliderSetting(SliderSetting ss, float x, float y, float w, int alpha, int mouseX) {
            RavenFontRenderer sf = Gui.getClickGuiSettingFontRenderer();
            if (ss.isString && ss.getOptions() != null) {
                sf.drawString(ss.getName(), x, y + 3,
                        new Color(TEXT_FAINT.getRed(), TEXT_FAINT.getGreen(), TEXT_FAINT.getBlue(), alpha).getRGB());
                int idx = (int) ss.getInput();
                String val = ss.getOptions()[MathHelper.clamp_int(idx, 0, ss.getOptions().length - 1)];
                float vw = sf.getStringWidth(val);
                float boxW = Math.max(40, vw + 10);
                RoundedUtils.drawRound(x + w - boxW - 2, y + 2, boxW, SETTING_ROW_H - 6, 4,
                        new Color(50, 50, 68, 200));
                sf.drawString(val, x + w - boxW + 2, y + 3,
                        new Color(ACCENT.getRed(), ACCENT.getGreen(), ACCENT.getBlue(), alpha).getRGB());
                sf.drawString("<", x + w - boxW - 6, y + 3,
                        new Color(TEXT_FAINT.getRed(), TEXT_FAINT.getGreen(), TEXT_FAINT.getBlue(), alpha).getRGB());
                sf.drawString(">", x + w - 4, y + 3,
                        new Color(TEXT_FAINT.getRed(), TEXT_FAINT.getGreen(), TEXT_FAINT.getBlue(), alpha).getRGB());
            } else {
                double val = ss.getInput();
                double min = ss.getMin(), max = ss.getMax();
                double pct = (val - min) / (max - min);
                String valStr = String.valueOf(ss.getInput()) +
                        (ss.getSuffix() != null && !ss.getSuffix().isEmpty() ? " " + ss.getSuffix() : "");
                sf.drawString(ss.getName(), x, y + 3,
                        new Color(TEXT_FAINT.getRed(), TEXT_FAINT.getGreen(), TEXT_FAINT.getBlue(), alpha).getRGB());
                sf.drawString(valStr, x + w - sf.getStringWidth(valStr) - 2, y + 3,
                        new Color(ACCENT.getRed(), ACCENT.getGreen(), ACCENT.getBlue(), alpha).getRGB());
                float trackY = y + SETTING_ROW_H - 5;
                float trackW = w - 6;
                RoundedUtils.drawRound(x + 3, trackY, trackW, 2, 1, new Color(50, 50, 65, 200));
                RoundedUtils.drawRound(x + 3, trackY, (float)(trackW * pct), 2, 1, ACCENT);
                RoundedUtils.drawRound(x + 3 + (float)(trackW * pct) - 3, trackY - 2, 6, 6, 3, Color.WHITE);
            }
        }

        private void drawKeySetting(KeySetting ks, float x, float y, float w, int alpha, Module module) {
            RavenFontRenderer sf = Gui.getClickGuiSettingFontRenderer();
            sf.drawString(ks.getName(), x, y + 3,
                    new Color(TEXT_FAINT.getRed(), TEXT_FAINT.getGreen(), TEXT_FAINT.getBlue(), alpha).getRGB());
            boolean binding = bindingKey == ks && bindingModule != null && bindingModule.equals(module.getName());
            String keyName = binding ? "..." : getKeyName(ks.getKey());
            float kw = Math.max(32, sf.getStringWidth(keyName) + 8);
            RoundedUtils.drawRound(x + w - kw - 2, y + 2, kw, SETTING_ROW_H - 6, 4,
                    binding ? new Color(255, 180, 80, 200) : new Color(50, 50, 68, 200));
            sf.drawString(keyName, x + w - kw + 2, y + 3,
                    binding ? Color.BLACK.getRGB() : new Color(200, 200, 220, alpha).getRGB());
        }

        private String getKeyName(int key) {
            if (key == 0) return "NONE";
            if (key >= 1000) {
                if (key == 1069) return "SCROLL UP";
                if (key == 1070) return "SCROLL DOWN";
                return "MOUSE " + (key - 1000);
            }
            return Keyboard.getKeyName(key);
        }

        void mouseDown(int mouseX, int mouseY, int button) {
            float px = panel.x;
            List<Setting> settings = getVisibleSettings();
            // Toggle switch area - left click toggles, right click expands settings
            float sw = 22, sh = 9;
            float sx = px + PANEL_W - sw - 8;
            float sy = y + (MODULE_ROW_H - sh) / 2f;
            if (mouseX >= sx && mouseX <= sx + sw && mouseY >= sy && mouseY <= sy + sh) {
                if (button == 0) {
                    module.toggle();
                } else if (button == 1 && !settings.isEmpty()) {
                    expanded = !expanded;
                    expandStart = System.currentTimeMillis();
                }
                return;
            }
            // Module row - both left and right click expand/collapse settings
            if (mouseX >= px + 3 && mouseX <= px + PANEL_W - 3
                    && mouseY >= y && mouseY <= y + MODULE_ROW_H) {
                if (!settings.isEmpty()) {
                    expanded = !expanded;
                    expandStart = System.currentTimeMillis();
                } else if (button == 0) {
                    module.toggle();
                }
                return;
            }
            // Settings
            if (expanded && mouseY > y + MODULE_ROW_H) {
                int setY = y + (int)MODULE_ROW_H;
                for (int i = 0; i < settings.size(); i++) {
                    Setting s = settings.get(i);
                    float sy2 = setY + i * SETTING_ROW_H;
                    if (mouseY >= sy2 && mouseY <= sy2 + SETTING_ROW_H) {
                        if (s instanceof ButtonSetting) {
                            ButtonSetting bs = (ButtonSetting) s;
                            if (bs.isMethodButton) {
                                bs.runMethod();
                            } else {
                                bs.toggle();
                            }
                            module.guiButtonToggled(bs);
                        } else if (s instanceof SliderSetting) {
                            SliderSetting ss = (SliderSetting) s;
                            if (ss.isString && ss.getOptions() != null) {
                                int idx = (int) ss.getInput();
                                if (mouseX < (panel.x + PANEL_W) / 2f) {
                                    idx = (idx - 1 + ss.getOptions().length) % ss.getOptions().length;
                                } else {
                                    idx = (idx + 1) % ss.getOptions().length;
                                }
                                ss.setValueWithEvent(idx);
                            } else {
                                draggingSlider = ss;
                                draggingModule = module.getName();
                            }
                        } else if (s instanceof KeySetting) {
                            KeySetting ks = (KeySetting) s;
                            if (bindingKey == ks && bindingModule != null && bindingModule.equals(module.getName())) {
                                bindingKey = null;
                                bindingModule = null;
                            } else {
                                bindingKey = ks;
                                bindingModule = module.getName();
                            }
                        }
                        return;
                    }
                }
            }
        }

        void mouseReleased(int mouseX, int mouseY, int button) {
            // nothing for now
        }
    }

    // ========== Input handling ==========
    @Override
    public void handleMouseInput() throws IOException {
        int wheel = Mouse.getEventDWheel();
        if (wheel != 0) {
            int dir = wheel > 0 ? -1 : 1;
            int mx = Mouse.getEventX() * width / mc.displayWidth;
            int my = height - Mouse.getEventY() * height / mc.displayHeight - 1;
            for (Panel p : panels) {
                p.onScroll(mx, my, dir);
            }
        }
        super.handleMouseInput();
    }

    @Override
    protected void mouseClicked(int mouseX, int mouseY, int mouseButton) {
        searchFocused = mouseX >= searchX && mouseX <= searchX + searchW
                && mouseY >= searchY && mouseY <= searchY + searchH;
        for (Panel p : panels) {
            p.mouseDown(mouseX, mouseY, mouseButton);
        }
    }

    @Override
    protected void mouseReleased(int mouseX, int mouseY, int state) {
        draggingSlider = null;
        draggingModule = null;
        for (Panel p : panels) {
            p.mouseReleased(mouseX, mouseY, state);
        }
    }

    @Override
    protected void keyTyped(char typedChar, int keyCode) throws IOException {
        if (bindingKey != null) {
            if (keyCode == Keyboard.KEY_ESCAPE) {
                bindingKey.setKey(0);
            } else {
                bindingKey.setKey(keyCode);
            }
            bindingKey = null;
            bindingModule = null;
            return;
        }
        if (keyCode == Keyboard.KEY_ESCAPE) {
            mc.displayGuiScreen(null);
            return;
        }
        if (!searchFocused) return;
        if (keyCode == Keyboard.KEY_BACK && search.length() > 0) {
            search.deleteCharAt(search.length() - 1);
            return;
        }
        if (typedChar >= 32 && typedChar < 127 && search.length() < 32) {
            search.append(typedChar);
        }
    }

    @Override
    public void onGuiClosed() {
        Keyboard.enableRepeatEvents(false);
        bindingKey = null;
        bindingModule = null;
        draggingSlider = null;
        draggingModule = null;
    }

    @Override
    public boolean doesGuiPauseGame() {
        return false;
    }
}
