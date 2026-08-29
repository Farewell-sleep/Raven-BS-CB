package keystrokesmod.clickgui.modern;

import keystrokesmod.Raven;
import keystrokesmod.module.Module;
import keystrokesmod.module.setting.Setting;
import keystrokesmod.module.setting.impl.ButtonSetting;
import keystrokesmod.module.setting.impl.ColorSetting;
import keystrokesmod.module.setting.impl.DescriptionSetting;
import keystrokesmod.module.setting.impl.SliderSetting;
import keystrokesmod.utility.font.RavenFontRenderer;
import keystrokesmod.utility.shader.RoundedUtils;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.GuiScreen;
import net.minecraft.client.renderer.GlStateManager;
import org.lwjgl.input.Keyboard;
import org.lwjgl.input.Mouse;
import org.lwjgl.opengl.GL11;

import java.awt.Color;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

public class ModernClickGUI extends GuiScreen {
    private static final Minecraft mc = Minecraft.getMinecraft();

    private static final Color COL_PANEL_BG = new Color(30, 25, 45, 220);
    private static final Color COL_CATEGORY_HEADER = new Color(50, 40, 75, 230);
    private static final Color COL_MODULE_BG = new Color(40, 33, 58, 180);
    private static final Color COL_MODULE_HOVER = new Color(60, 50, 85, 200);
    private static final Color COL_MODULE_ENABLED = new Color(70, 55, 110, 220);
    private static final Color COL_ACCENT = new Color(120, 80, 180, 255);
    private static final Color COL_TEXT = new Color(180, 185, 220, 255);
    private static final Color COL_TEXT_DIM = new Color(130, 135, 170, 255);
    private static final Color COL_SETTING_BG = new Color(35, 28, 50, 200);
    private static final Color COL_BINDING = new Color(180, 80, 80, 220);

    private final List<CategoryPanel> panels = new ArrayList<>();
    private long openTime;
    private long lastFrame;
    private float scrollY = 0;
    private float totalHeight = 0;
    private Module bindingModule = null;

    private static class CategoryPanel {
        Module.category category;
        float x, y, w;
        List<ModuleButton> moduleButtons = new ArrayList<>();

        CategoryPanel(Module.category category, float x, float y, float w) {
            this.category = category;
            this.x = x; this.y = y; this.w = w;
            for (Module m : Raven.moduleManager.inCategory(category)) {
                moduleButtons.add(new ModuleButton(m));
            }
        }

        float getHeight() {
            float h = 26f;
            for (ModuleButton mb : moduleButtons) {
                h += mb.getHeight();
            }
            return h;
        }
    }

    private static class ModuleButton {
        Module module;
        float hoverAnim = 0f;
        boolean expanded = false;
        float expandAnim = 0f;
        boolean lastHovered = false;
        List<SettingRow> settingRows = new ArrayList<>();

        ModuleButton(Module module) {
            this.module = module;
            for (Setting s : module.getSettings()) {
                if (s instanceof DescriptionSetting) continue;
                settingRows.add(new SettingRow(s));
            }
        }

        float getHeight() {
            float base = 22f;
            if (expanded || expandAnim > 0.02f) {
                float settingsH = 0;
                for (SettingRow r : settingRows) {
                    settingsH += r.getHeight();
                }
                base += settingsH * expandAnim + 4f;
            }
            return base;
        }
    }

    private static class SettingRow {
        Setting setting;
        boolean dragging = false;

        SettingRow(Setting setting) {
            this.setting = setting;
        }

        float getHeight() {
            if (setting instanceof SliderSetting) return 36f;
            return 26f;
        }
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
        Module.category[] cats = Module.category.values();
        float panelW = 150;
        float startX = 10;
        float startY = 30;
        float gap = 8;

        float currentY = startY;
        for (Module.category cat : cats) {
            List<Module> mods = Raven.moduleManager.inCategory(cat);
            if (mods.isEmpty()) continue;
            CategoryPanel panel = new CategoryPanel(cat, startX, currentY, panelW);
            panels.add(panel);
            currentY += panel.getHeight() + gap;
        }
        totalHeight = currentY - startY;
    }

    @Override
    public void drawScreen(int mouseX, int mouseY, float partialTicks) {
        long now = System.currentTimeMillis();
        float dt = Math.min(50, now - lastFrame) / 1000f;
        lastFrame = now;
        float ease = easeOutQuint(Math.min(1, (now - openTime) / 400f));
        float time = (now - openTime) / 1000f;

        drawDefaultBackground();
        drawAuroraBg(time);

        // Title
        RavenFontRenderer headerFont = getHeaderFont();
        GlStateManager.pushMatrix();
        float titleScale = 1.8f;
        GlStateManager.translate(12, 8, 0);
        GlStateManager.scale(titleScale, titleScale, 1);
        headerFont.drawStringWithShadow("Raven BS", 0, 0,
                new Color(200, 200, 230, (int)(230 * ease)).getRGB());
        GlStateManager.popMatrix();

        // Scroll
        int wheel = Mouse.getDWheel();
        if (wheel != 0) scrollY -= wheel * 0.15f;
        float maxScroll = Math.max(0, totalHeight - height + 50);
        scrollY = Math.max(0, Math.min(scrollY, maxScroll));

        // Update expand animations and recalculate positions
        updatePanelPositions(dt);

        // Scissor for scroll area
        GlStateManager.pushMatrix();
        GlStateManager.translate(0, -scrollY, 0);

        for (CategoryPanel p : panels) {
            drawPanel(p, mouseX, mouseY + (int)scrollY, dt, ease);
        }

        GlStateManager.popMatrix();

        // Binding overlay
        if (bindingModule != null) {
            String text = "Press a key to bind " + bindingModule.getName() + " (ESC to clear)";
            int tw = mc.fontRendererObj.getStringWidth(text);
            drawRect(width / 2 - tw / 2 - 10, height - 40, width / 2 + tw / 2 + 10, height - 18,
                    new Color(40, 30, 60, 230).getRGB());
            mc.fontRendererObj.drawStringWithShadow(text, width / 2 - tw / 2, height - 34,
                    new Color(220, 200, 220).getRGB());
        }
    }

    private void updatePanelPositions(float dt) {
        float startY = 30;
        float gap = 8;
        float currentY = startY;

        for (CategoryPanel p : panels) {
            p.y = currentY;
            // Update expand animations
            for (ModuleButton mb : p.moduleButtons) {
                mb.expandAnim += ((mb.expanded ? 1f : 0f) - mb.expandAnim) * Math.min(1, dt * 8f);
            }
            currentY += p.getHeight() + gap;
        }
        totalHeight = currentY - startY;
    }

    private void drawPanel(CategoryPanel p, int mouseX, int mouseY, float dt, float globalAlpha) {
        float panelH = p.getHeight();

        // Panel background
        RoundedUtils.drawRound(p.x, p.y, p.w, panelH, 6f,
                new Color(COL_PANEL_BG.getRed(), COL_PANEL_BG.getGreen(), COL_PANEL_BG.getBlue(),
                        (int)(COL_PANEL_BG.getAlpha() * globalAlpha)));

        // Category header
        RoundedUtils.drawRound(p.x, p.y, p.w, 24f, 6f,
                new Color(COL_CATEGORY_HEADER.getRed(), COL_CATEGORY_HEADER.getGreen(), COL_CATEGORY_HEADER.getBlue(),
                        (int)(COL_CATEGORY_HEADER.getAlpha() * globalAlpha)));

        // Header bottom line
        drawRect((int)p.x + 6, (int)p.y + 23, (int)p.x + (int)p.w - 6, (int)p.y + 24,
                new Color(COL_ACCENT.getRed(), COL_ACCENT.getGreen(), COL_ACCENT.getBlue(),
                        (int)(100 * globalAlpha)).getRGB());

        // Category name
        RavenFontRenderer font = getSettingFont();
        String catName = capitalize(p.category.name());
        font.drawStringWithShadow(catName, p.x + 8, p.y + 7,
                new Color(COL_TEXT.getRed(), COL_TEXT.getGreen(), COL_TEXT.getBlue(),
                        (int)(COL_TEXT.getAlpha() * globalAlpha)).getRGB());

        // Modules
        float moduleY = p.y + 26f;
        for (ModuleButton mb : p.moduleButtons) {
            drawModuleButton(mb, p.x + 4, moduleY, p.w - 8, mouseX, mouseY, dt, globalAlpha);
            moduleY += mb.getHeight();
        }
    }

    private void drawModuleButton(ModuleButton mb, float x, float y, float w, int mouseX, int mouseY, float dt, float globalAlpha) {
        boolean hovered = mouseX >= x && mouseX <= x + w && mouseY >= y && mouseY <= y + 20;
        mb.hoverAnim += ((hovered ? 1 : 0) - mb.hoverAnim) * Math.min(1, dt * 8f);

        boolean enabled = mb.module.isEnabled();
        boolean isBinding = bindingModule == mb.module;

        Color bg = isBinding ? COL_BINDING : (enabled ? COL_MODULE_ENABLED : (hovered ? COL_MODULE_HOVER : COL_MODULE_BG));
        RoundedUtils.drawRound(x, y, w, 20f, 4f,
                new Color(bg.getRed(), bg.getGreen(), bg.getBlue(), (int)(bg.getAlpha() * globalAlpha)));

        // Accent bar on left when enabled
        if (enabled) {
            RoundedUtils.drawRound(x, y + 3, 3f, 14f, 1.5f,
                    new Color(COL_ACCENT.getRed(), COL_ACCENT.getGreen(), COL_ACCENT.getBlue(),
                            (int)(200 * globalAlpha)));
        }

        // Text
        RavenFontRenderer font = getSettingFont();
        Color textCol = enabled ? new Color(220, 220, 250) : COL_TEXT;
        String displayName = isBinding ? "[BINDING]" : mb.module.getName();
        font.drawStringWithShadow(displayName, x + (enabled ? 10 : 8), y + 5,
                new Color(textCol.getRed(), textCol.getGreen(), textCol.getBlue(),
                        (int)(textCol.getAlpha() * globalAlpha)).getRGB());

        // Keybind indicator
        if (mb.module.getKeycode() != 0 && !isBinding) {
            String keyName = Keyboard.getKeyName(mb.module.getKeycode());
            if (keyName != null) {
                int kw = font.getStringWidth(keyName);
                font.drawStringWithShadow(keyName, x + w - kw - 6, y + 5,
                        new Color(COL_TEXT_DIM.getRed(), COL_TEXT_DIM.getGreen(), COL_TEXT_DIM.getBlue(),
                                (int)(COL_TEXT_DIM.getAlpha() * globalAlpha)).getRGB());
            }
        }

        // Expand arrow
        if (!mb.settingRows.isEmpty()) {
            String arrow = mb.expanded ? "-" : "+";
            font.drawStringWithShadow(arrow, x + w - 16, y + 5,
                    new Color(COL_TEXT_DIM.getRed(), COL_TEXT_DIM.getGreen(), COL_TEXT_DIM.getBlue(),
                            (int)(COL_TEXT_DIM.getAlpha() * globalAlpha)).getRGB());
        }

        // Settings (expanded)
        if (mb.expandAnim > 0.02f && !mb.settingRows.isEmpty()) {
            GlStateManager.pushMatrix();
            GlStateManager.translate(x + 4, y + 22, 0);
            GlStateManager.scale(1, mb.expandAnim, 1);
            float sy = 0;
            for (SettingRow r : mb.settingRows) {
                drawSettingRow(r, 0, sy, w - 8, mouseX - (int)x - 4, mouseY - (int)y - 22, dt);
                sy += r.getHeight();
            }
            GlStateManager.popMatrix();
        }
    }

    private void drawSettingRow(SettingRow r, float x, float y, float w, int mouseX, int mouseY, float dt) {
        RavenFontRenderer font = getSettingFont();

        if (r.setting instanceof ButtonSetting) {
            ButtonSetting bs = (ButtonSetting) r.setting;
            boolean hovered = mouseX >= x && mouseX <= x + w && mouseY >= y && mouseY <= y + 22;
            RoundedUtils.drawRound(x, y, w, 22f, 3f,
                    hovered ? new Color(50, 42, 70, 200) : COL_SETTING_BG);

            font.drawStringWithShadow(bs.getName(), x + 6, y + 5, COL_TEXT.getRGB());

            // Toggle
            float toggleX = x + w - 32;
            float toggleY = y + 5;
            RoundedUtils.drawRound(toggleX, toggleY, 26, 12, 6f,
                    bs.isToggled() ? COL_ACCENT : new Color(60, 55, 75, 200));
            float knobX = toggleX + (bs.isToggled() ? 14 : 2);
            RoundedUtils.drawRound(knobX, toggleY + 2, 8, 8, 4f, new Color(230, 230, 240));
            return;
        }

        if (r.setting instanceof SliderSetting) {
            SliderSetting ss = (SliderSetting) r.setting;
            RoundedUtils.drawRound(x, y, w, 32f, 3f, COL_SETTING_BG);

            String valStr;
            if (ss.getOptions() != null && ss.getOptions().length > 0) {
                valStr = ss.getOptions()[(int)ss.getInput()];
            } else {
                valStr = String.format("%.1f", ss.getInput()) + (ss.getSuffix() != null ? ss.getSuffix() : "");
            }
            font.drawStringWithShadow(ss.getName(), x + 6, y + 4, COL_TEXT.getRGB());
            font.drawStringWithShadow(valStr, x + w - 6 - font.getStringWidth(valStr), y + 4, COL_TEXT_DIM.getRGB());

            // Slider bar
            float barX = x + 6;
            float barY = y + 20;
            float barW = w - 12;
            float barH = 4;
            RoundedUtils.drawRound(barX, barY, barW, barH, 2f, new Color(50, 45, 65, 200));

            double min = ss.getMin();
            double max = ss.getMax();
            double progress = (ss.getInput() - min) / (max - min);
            float fillW = (float)(barW * progress);
            RoundedUtils.drawRound(barX, barY, fillW, barH, 2f, COL_ACCENT);

            // Knob
            float knobX = barX + fillW - 4;
            RoundedUtils.drawRound(knobX, barY - 3, 10, 10, 5f, new Color(220, 220, 240));
            return;
        }

        if (r.setting instanceof ColorSetting) {
            ColorSetting cs = (ColorSetting) r.setting;
            RoundedUtils.drawRound(x, y, w, 22f, 3f, COL_SETTING_BG);
            font.drawStringWithShadow(cs.getName(), x + 6, y + 5, COL_TEXT.getRGB());
            RoundedUtils.drawRound(x + w - 26, y + 4, 18, 14, 3f,
                    new Color(cs.getRed(), cs.getGreen(), cs.getBlue(), cs.getAlpha()));
        }
    }

    private void drawAuroraBg(float time) {
        float bx1 = width * 0.15f + (float)Math.sin(time * 0.3f) * 40f;
        float by1 = height * 0.25f + (float)Math.cos(time * 0.25f) * 30f;
        drawGlow(bx1, by1, width * 0.4f, height * 0.4f, new Color(40, 30, 80, 40));

        float bx2 = width * 0.8f + (float)Math.cos(time * 0.22f) * 50f;
        float by2 = height * 0.6f + (float)Math.sin(time * 0.3f) * 40f;
        drawGlow(bx2, by2, width * 0.35f, height * 0.45f, new Color(80, 30, 100, 35));
    }

    private void drawGlow(float cx, float cy, float w, float h, Color color) {
        GlStateManager.pushMatrix();
        GlStateManager.enableBlend();
        GlStateManager.disableAlpha();
        GlStateManager.disableTexture2D();
        GlStateManager.blendFunc(GL11.GL_SRC_ALPHA, GL11.GL_ONE_MINUS_SRC_ALPHA);
        GL11.glBegin(GL11.GL_TRIANGLE_FAN);
        setColor(color);
        GL11.glVertex2f(cx, cy);
        for (int i = 0; i <= 32; i++) {
            double ang = Math.toRadians(i * 360.0 / 32);
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

    private String capitalize(String s) {
        if (s == null || s.isEmpty()) return s;
        return s.substring(0, 1).toUpperCase() + s.substring(1).toLowerCase();
    }

    private float easeOutQuint(float t) {
        return 1 - (float) Math.pow(1 - t, 5);
    }

    private RavenFontRenderer getHeaderFont() {
        return keystrokesmod.module.impl.client.Gui.getClickGuiHeaderFontRenderer();
    }

    private RavenFontRenderer getSettingFont() {
        return keystrokesmod.module.impl.client.Gui.getClickGuiSettingFontRenderer();
    }

    @Override
    protected void mouseClicked(int mouseX, int mouseY, int mouseButton) throws IOException {
        super.mouseClicked(mouseX, mouseY, mouseButton);

        // If binding, any click cancels
        if (bindingModule != null) {
            bindingModule = null;
            return;
        }

        int adjMouseY = mouseY + (int)scrollY;

        for (CategoryPanel p : panels) {
            float moduleY = p.y + 26f;
            for (ModuleButton mb : p.moduleButtons) {
                float bx = p.x + 4;
                float by = moduleY;
                float bw = p.w - 8;
                float bh = 20f;

                if (mouseX >= bx && mouseX <= bx + bw && adjMouseY >= by && adjMouseY <= by + bh) {
                    if (mouseButton == 0) {
                        // Left click: toggle module
                        mb.module.toggle();
                    } else if (mouseButton == 1) {
                        // Right click: bind key
                        bindingModule = mb.module;
                    } else if (mouseButton == 2) {
                        // Middle click: expand settings
                        if (!mb.settingRows.isEmpty()) {
                            mb.expanded = !mb.expanded;
                        }
                    }
                    return;
                }

                // Check setting clicks if expanded
                if (mb.expanded && mb.expandAnim > 0.5f) {
                    float sy = by + 22f;
                    for (SettingRow r : mb.settingRows) {
                        float rh = r.getHeight();
                        if (mouseX >= bx + 4 && mouseX <= bx + 4 + bw - 8 &&
                                adjMouseY >= sy && adjMouseY <= sy + rh) {
                            handleSettingClick(r, mb, mouseX - (int)bx - 4, adjMouseY - (int)sy, mouseButton);
                            return;
                        }
                        sy += rh;
                    }
                }

                moduleY += mb.getHeight();
            }
        }
    }

    private void handleSettingClick(SettingRow r, ModuleButton mb, int localX, int localY, int button) {
        if (r.setting instanceof ButtonSetting && button == 0) {
            ((ButtonSetting) r.setting).toggle();
        } else if (r.setting instanceof SliderSetting && button == 0) {
            r.dragging = true;
        }
    }

    @Override
    protected void mouseClickMove(int mouseX, int mouseY, int clickedMouseButton, long timeSinceLastClick) {
        super.mouseClickMove(mouseX, mouseY, clickedMouseButton, timeSinceLastClick);

        if (clickedMouseButton != 0) return;
        int adjMouseY = mouseY + (int)scrollY;

        for (CategoryPanel p : panels) {
            float moduleY = p.y + 26f;
            for (ModuleButton mb : p.moduleButtons) {
                if (mb.expanded && mb.expandAnim > 0.5f) {
                    float sy = moduleY + 22f;
                    for (SettingRow r : mb.settingRows) {
                        if (r.dragging && r.setting instanceof SliderSetting) {
                            SliderSetting ss = (SliderSetting) r.setting;
                            float barX = 6;
                            float barW = p.w - 8 - 12;
                            float relX = mouseX - (p.x + 4) - barX;
                            double progress = Math.max(0, Math.min(1, relX / barW));
                            double val = ss.getMin() + progress * (ss.getMax() - ss.getMin());
                            ss.setValue(val);
                            return;
                        }
                        sy += r.getHeight();
                    }
                }
                moduleY += mb.getHeight();
            }
        }
    }

    @Override
    protected void mouseReleased(int mouseX, int mouseY, int state) {
        super.mouseReleased(mouseX, mouseY, state);
        for (CategoryPanel p : panels) {
            for (ModuleButton mb : p.moduleButtons) {
                for (SettingRow r : mb.settingRows) {
                    r.dragging = false;
                }
            }
        }
    }

    @Override
    protected void keyTyped(char typedChar, int keyCode) throws IOException {
        if (bindingModule != null) {
            if (keyCode == Keyboard.KEY_ESCAPE) {
                bindingModule.setBind(0);
            } else {
                bindingModule.setBind(keyCode);
            }
            bindingModule = null;
            return;
        }

        if (keyCode == Keyboard.KEY_ESCAPE) {
            mc.displayGuiScreen(null);
            return;
        }
        super.keyTyped(typedChar, keyCode);
    }

    @Override
    public boolean doesGuiPauseGame() { return false; }
}
