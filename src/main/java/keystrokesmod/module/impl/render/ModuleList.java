package keystrokesmod.module.impl.render;

import keystrokesmod.module.Module;
import keystrokesmod.module.ModuleManager;
import keystrokesmod.module.setting.impl.ButtonSetting;
import keystrokesmod.module.setting.impl.ColorSetting;
import keystrokesmod.module.setting.impl.SliderSetting;
import keystrokesmod.utility.Utils;
import keystrokesmod.utility.font.FontManager;
import keystrokesmod.utility.font.RavenFontRenderer;
import net.minecraft.client.gui.ScaledResolution;
import net.minecraftforge.fml.common.eventhandler.SubscribeEvent;
import net.minecraftforge.fml.common.gameevent.TickEvent;
import org.lwjgl.opengl.GL11;

import java.awt.Color;
import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;

public class ModuleList extends Module {
    private static final String[] COLOR_MODES = new String[]{"Rainbow", "Static", "Gradient"};
    private static final String[] FONT_OPTIONS = FontManager.getHudFontOptions();

    public static SliderSetting colorMode;
    public static ColorSetting staticColor;
    public static ColorSetting gradientColor1;
    public static ColorSetting gradientColor2;
    public static SliderSetting font;
    public static SliderSetting fontSize;
    public static SliderSetting rainbowSpeed;
    public static ButtonSetting modernStyle;
    public static ButtonSetting textShadow;
    public static ButtonSetting lowercase;
    public static ButtonSetting showInfo;

    private long rainbowOffset = 0;

    public ModuleList() {
        super("ArrayList", category.render);
        this.registerSetting(colorMode = new SliderSetting("Color mode", 0, COLOR_MODES));
        this.registerSetting(staticColor = new ColorSetting("Color", 85, 85, 255));
        this.registerSetting(gradientColor1 = new ColorSetting("Color 1", 85, 85, 255));
        this.registerSetting(gradientColor2 = new ColorSetting("Color 2", 255, 85, 255));
        this.registerSetting(rainbowSpeed = new SliderSetting("Rainbow speed", 5.0, 0.5, 20.0, 0.5));
        this.registerSetting(font = new SliderSetting("Font", 0, FONT_OPTIONS));
        this.registerSetting(fontSize = new SliderSetting("Scale", 1.0, 0.5, 2.0, 0.1));
        this.registerSetting(modernStyle = new ButtonSetting("Modern style", true));
        this.registerSetting(textShadow = new ButtonSetting("Text shadow", true));
        this.registerSetting(lowercase = new ButtonSetting("Lowercase", false));
        this.registerSetting(showInfo = new ButtonSetting("Show info", true));
    }

    @Override
    public void guiUpdate() {
        int mode = (int) colorMode.getInput();
        staticColor.setVisible(mode == 1, this);
        gradientColor1.setVisible(mode == 2, this);
        gradientColor2.setVisible(mode == 2, this);
        rainbowSpeed.setVisible(mode == 0, this);
    }

    @Override
    public void onEnable() {
        guiUpdate();
        rainbowOffset = System.currentTimeMillis();
    }

    private String getFontName() {
        int idx = (int) Math.max(0, Math.min(FONT_OPTIONS.length - 1, font.getInput()));
        return FONT_OPTIONS[idx];
    }

    private RavenFontRenderer getFontRenderer() {
        return FontManager.getHudRenderer(getFontName(), (float) fontSize.getInput());
    }

    private List<Module> getEnabledModules() {
        List<Module> list = new ArrayList<>();
        for (Module m : ModuleManager.modules) {
            if (m.isEnabled() && m != this && !m.isHidden()) {
                list.add(m);
            }
        }
        return list;
    }

    private String getModuleName(Module module) {
        String name = module.getName();
        if (lowercase.isToggled()) {
            name = name.toLowerCase();
        }
        return name;
    }

    private String getModuleInfo(Module module) {
        if (!showInfo.isToggled()) return "";
        String info = module.getInfoUpdate();
        if (info != null && !info.isEmpty()) {
            return info;
        }
        return "";
    }

    private int getColor(int index, int total) {
        int mode = (int) colorMode.getInput();
        if (mode == 0) {
            long time = System.currentTimeMillis() - rainbowOffset;
            float speed = (float) rainbowSpeed.getInput() * 100f;
            float hue = (time / speed + index * 0.08f) % 1.0f;
            return Color.HSBtoRGB(hue, 0.7f, 1.0f);
        } else if (mode == 1) {
            return staticColor.getColor();
        } else {
            float t = total > 1 ? (float) index / (total - 1) : 0f;
            Color c1 = new Color(gradientColor1.getColor(), true);
            Color c2 = new Color(gradientColor2.getColor(), true);
            int r = (int) (c1.getRed() + (c2.getRed() - c1.getRed()) * t);
            int g = (int) (c1.getGreen() + (c2.getGreen() - c1.getGreen()) * t);
            int b = (int) (c1.getBlue() + (c2.getBlue() - c1.getBlue()) * t);
            return new Color(r, g, b).getRGB();
        }
    }

    @SubscribeEvent
    public void onRenderTick(TickEvent.RenderTickEvent event) {
        if (event.phase != TickEvent.Phase.END || !Utils.nullCheck()) {
            return;
        }
        if (mc.currentScreen != null || mc.gameSettings.showDebugInfo) {
            return;
        }

        ScaledResolution sr = new ScaledResolution(mc);
        int screenWidth = sr.getScaledWidth();

        RavenFontRenderer f = getFontRenderer();
        int fontHeight = f.getFontHeight();

        List<Module> modules = getEnabledModules();
        modules.sort(Comparator.comparingInt(m -> -f.getStringWidth(getModuleName(m) + " " + getModuleInfo(m))));

        int total = modules.size();

        if (modernStyle.isToggled()) {
            renderModern(modules, f, fontHeight, screenWidth, total);
        } else {
            renderClassic(modules, f, fontHeight, screenWidth, total);
        }
    }

    private void renderModern(List<Module> modules, RavenFontRenderer f, int fontHeight, int screenWidth, int total) {
        float y = 2;
        int padding = 6;
        int itemHeight = fontHeight + padding * 2;
        int gap = 2;
        int rightMargin = 3;

        for (int i = 0; i < total; i++) {
            Module module = modules.get(i);
            String name = getModuleName(module);
            String info = getModuleInfo(module);
            int accentColor = getColor(i, total);

            int nameWidth = f.getStringWidth(name);
            int infoWidth = info.isEmpty() ? 0 : f.getStringWidth(" " + info);
            int totalWidth = nameWidth + infoWidth;

            int itemWidth = totalWidth + padding * 2 + 8;
            float x = screenWidth - itemWidth - rightMargin;

            drawModernItem(x, y, itemWidth, itemHeight, accentColor);

            float textX = x + padding;
            float textY = y + padding;

            if (textShadow.isToggled()) {
                f.drawStringWithShadow(name, textX, textY, 0xFFFFFFFF);
                if (!info.isEmpty()) {
                    f.drawStringWithShadow(" " + info, textX + nameWidth, textY, 0xFFAAAAAA);
                }
            } else {
                f.drawString(name, textX, textY, 0xFFFFFFFF);
                if (!info.isEmpty()) {
                    f.drawString(" " + info, textX + nameWidth, textY, 0xFFAAAAAA);
                }
            }

            y += itemHeight + gap;
        }
    }

    private void drawModernItem(float x, float y, float w, float h, int accentColor) {
        float radius = 4f;

        GL11.glPushMatrix();
        GL11.glEnable(GL11.GL_BLEND);
        GL11.glDisable(GL11.GL_TEXTURE_2D);
        GL11.glBlendFunc(GL11.GL_SRC_ALPHA, GL11.GL_ONE_MINUS_SRC_ALPHA);
        GL11.glEnable(GL11.GL_LINE_SMOOTH);

        GL11.glColor4f(0.08f, 0.08f, 0.1f, 0.65f);
        drawRoundedRect(x, y, w, h, radius);

        GL11.glColor4f(1f, 1f, 1f, 0.06f);
        drawRoundedRect(x + 0.5f, y + 0.5f, w - 1f, h - 1f, radius - 0.5f);

        Color accent = new Color(accentColor, true);
        GL11.glColor4f(accent.getRed() / 255f, accent.getGreen() / 255f, accent.getBlue() / 255f, 0.9f);
        drawRoundedRect(x + w - 3, y + 3, 2.5f, h - 6, 1.5f);

        GL11.glEnable(GL11.GL_TEXTURE_2D);
        GL11.glDisable(GL11.GL_BLEND);
        GL11.glPopMatrix();
    }

    private void drawRoundedRect(float x, float y, float w, float h, float r) {
        GL11.glBegin(GL11.GL_TRIANGLE_FAN);
        GL11.glVertex2f(x + r, y + r);
        for (int i = 0; i <= 8; i++) {
            double a = Math.PI + (Math.PI / 2 * i / 8);
            GL11.glVertex2d(x + r + Math.cos(a) * r, y + r + Math.sin(a) * r);
        }
        for (int i = 0; i <= 8; i++) {
            double a = Math.PI / 2 + (Math.PI / 2 * i / 8);
            GL11.glVertex2d(x + w - r + Math.cos(a) * r, y + r + Math.sin(a) * r);
        }
        for (int i = 0; i <= 8; i++) {
            double a = (Math.PI / 2 * i / 8);
            GL11.glVertex2d(x + w - r + Math.cos(a) * r, y + h - r + Math.sin(a) * r);
        }
        for (int i = 0; i <= 8; i++) {
            double a = -Math.PI / 2 + (Math.PI / 2 * i / 8);
            GL11.glVertex2d(x + r + Math.cos(a) * r, y + h - r + Math.sin(a) * r);
        }
        GL11.glEnd();
    }

    private void renderClassic(List<Module> modules, RavenFontRenderer f, int fontHeight, int screenWidth, int total) {
        int rowHeight = fontHeight + 3;
        float y = 2;
        int bgColor = new Color(0, 0, 0, 110).getRGB();

        for (int i = 0; i < total; i++) {
            Module module = modules.get(i);
            String name = getModuleName(module);
            String info = getModuleInfo(module);
            String text = info.isEmpty() ? name : name + " " + info;
            int textWidth = f.getStringWidth(text);
            int color = getColor(i, total);

            float x = screenWidth - textWidth - 3;

            GL11.glPushMatrix();
            GL11.glEnable(GL11.GL_BLEND);
            GL11.glDisable(GL11.GL_TEXTURE_2D);
            GL11.glColor4f(0f, 0f, 0f, 0.45f);
            GL11.glRectf(x - 2, y, screenWidth, y + rowHeight);
            GL11.glColor4f(1f, 1f, 1f, 1f);
            GL11.glEnable(GL11.GL_TEXTURE_2D);
            GL11.glDisable(GL11.GL_BLEND);
            GL11.glPopMatrix();

            if (textShadow.isToggled()) {
                f.drawStringWithShadow(name, x, y + 1, color);
                if (!info.isEmpty()) {
                    f.drawStringWithShadow(" " + info, x + f.getStringWidth(name), y + 1, 0xFFAAAAAA);
                }
            } else {
                f.drawString(name, x, y + 1, color);
                if (!info.isEmpty()) {
                    f.drawString(" " + info, x + f.getStringWidth(name), y + 1, 0xFFAAAAAA);
                }
            }

            y += rowHeight;
        }
    }
}
