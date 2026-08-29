package keystrokesmod.clickgui.myau;

import keystrokesmod.module.Module;
import keystrokesmod.module.ModuleManager;
import keystrokesmod.module.setting.Setting;
import keystrokesmod.module.setting.impl.ButtonSetting;
import keystrokesmod.module.setting.impl.SliderSetting;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.FontRenderer;
import net.minecraft.client.gui.Gui;
import net.minecraft.client.gui.GuiScreen;
import net.minecraft.client.gui.ScaledResolution;
import org.lwjgl.input.Keyboard;
import org.lwjgl.input.Mouse;
import org.lwjgl.opengl.GL11;

import java.awt.Color;
import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;

public class MyauClickGui extends GuiScreen {

    private static MyauClickGui instance;
    private final Minecraft mc = Minecraft.getMinecraft();
    private final ArrayList<CategoryPanel> panels = new ArrayList<>();
    private Module expandedModule = null;
    private Module bindingModule = null;
    private long openTime = 0;

    private static final int PANEL_WIDTH = 100;
    private static final int HEADER_HEIGHT = 18;
    private static final int MODULE_HEIGHT = 16;
    private static final int SETTING_HEIGHT = 14;
    private static final int MAX_PANEL_HEIGHT = 320;

    public MyauClickGui() {
        instance = this;
        buildPanels();
    }

    public static MyauClickGui getInstance() {
        if (instance == null) instance = new MyauClickGui();
        return instance;
    }

    private void buildPanels() {
        panels.clear();
        Module.category[] categories = {
                Module.category.combat, Module.category.movement,
                Module.category.player, Module.category.render,
                Module.category.world, Module.category.minigames,
                Module.category.fun, Module.category.other,
                Module.category.client, Module.category.profiles,
                Module.category.scripts
        };

        int x = 10;
        for (Module.category cat : categories) {
            List<Module> mods = new ArrayList<>();
            for (Module m : ModuleManager.modules) {
                if (m.moduleCategory() == cat && !m.hidden) {
                    mods.add(m);
                }
            }
            if (mods.isEmpty()) continue;
            mods.sort(Comparator.comparing(Module::getName));
            CategoryPanel panel = new CategoryPanel(cat.name(), mods, x, 30);
            panels.add(panel);
            x += PANEL_WIDTH + 6;
        }
    }

    @Override
    public void initGui() {
        openTime = System.currentTimeMillis();
        super.initGui();
    }

    @Override
    public void drawScreen(int mouseX, int mouseY, float partialTicks) {
        drawDefaultBackground();
        drawRect(0, 0, width, height, new Color(0, 0, 0, 80).getRGB());

        drawDynamicIsland();

        FontRenderer fr = mc.fontRendererObj;

        for (CategoryPanel panel : panels) {
            panel.draw(mouseX, mouseY, fr);
        }

        if (bindingModule != null) {
            String text = "Press a key for " + bindingModule.getName() + " (ESC to clear)";
            int tw = fr.getStringWidth(text);
            drawRoundedRect(width / 2 - tw / 2 - 8, height / 2 - 12, width / 2 + tw / 2 + 8, height / 2 + 12, 6, new Color(20, 20, 20, 230).getRGB());
            fr.drawStringWithShadow(text, width / 2 - tw / 2, height / 2 - 4, 0xFFFFFF);
        }

        int wheel = Mouse.getDWheel();
        if (wheel != 0) {
            int dir = wheel > 0 ? -1 : 1;
            for (CategoryPanel panel : panels) {
                panel.onScroll(mouseX, mouseY, dir);
            }
        }
    }

    private void drawDynamicIsland() {
        FontRenderer fr = mc.fontRendererObj;
        ScaledResolution sr = new ScaledResolution(mc);

        String clientName = "Raven BS";
        String playerName = mc.thePlayer != null ? mc.thePlayer.getName() : "Player";
        int fps = Minecraft.getDebugFPS();
        int ping = 0;
        if (mc.thePlayer != null && mc.thePlayer.sendQueue != null
                && mc.thePlayer.sendQueue.getPlayerInfo(mc.thePlayer.getUniqueID()) != null) {
            ping = mc.thePlayer.sendQueue.getPlayerInfo(mc.thePlayer.getUniqueID()).getResponseTime();
        }

        String leftText = clientName;
        String midText = playerName;
        String rightText = ping + "ms  " + fps + "fps";

        int padding = 14;
        int gap = 20;
        int totalWidth = fr.getStringWidth(leftText) + fr.getStringWidth(midText) + fr.getStringWidth(rightText) + padding * 2 + gap * 2;
        int islandHeight = 28;
        int islandX = (width - totalWidth) / 2;
        int islandY = 6;

        drawRoundedRect(islandX, islandY, islandX + totalWidth, islandY + islandHeight, 14, new Color(18, 18, 20, 220).getRGB());
        drawRoundedRect(islandX + 1, islandY + 1, islandX + totalWidth - 1, islandY + islandHeight - 1, 13, new Color(30, 30, 35, 180).getRGB());

        int textY = islandY + (islandHeight - fr.FONT_HEIGHT) / 2;
        int cx = islandX + padding;

        fr.drawStringWithShadow(leftText, cx, textY, new Color(100, 180, 255).getRGB());
        cx += fr.getStringWidth(leftText) + gap;

        fr.drawStringWithShadow(midText, cx, textY, 0xDDDDDD);
        cx += fr.getStringWidth(midText) + gap;

        fr.drawStringWithShadow(rightText, cx, textY, 0x999999);
    }

    @Override
    public void mouseClicked(int mouseX, int mouseY, int mouseButton) {
        for (CategoryPanel panel : panels) {
            panel.mouseClicked(mouseX, mouseY, mouseButton);
        }
    }

    @Override
    public void mouseReleased(int mouseX, int mouseY, int state) {
        for (CategoryPanel panel : panels) {
            panel.mouseReleased(mouseX, mouseY, state);
        }
    }

    @Override
    public void keyTyped(char typedChar, int keyCode) {
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
            expandedModule = null;
            mc.displayGuiScreen(null);
        }
    }

    @Override
    public boolean doesGuiPauseGame() {
        return false;
    }

    @Override
    public void onGuiClosed() {
        bindingModule = null;
        expandedModule = null;
    }

    private void drawRoundedRect(int x, int y, int x2, int y2, int radius, int color) {
        GL11.glPushMatrix();
        GL11.glEnable(GL11.GL_BLEND);
        GL11.glDisable(GL11.GL_TEXTURE_2D);
        GL11.glBlendFunc(GL11.GL_SRC_ALPHA, GL11.GL_ONE_MINUS_SRC_ALPHA);
        GL11.glEnable(GL11.GL_LINE_SMOOTH);

        float a = (float) (color >> 24 & 255) / 255.0f;
        float r = (float) (color >> 16 & 255) / 255.0f;
        float g = (float) (color >> 8 & 255) / 255.0f;
        float b = (float) (color & 255) / 255.0f;
        GL11.glColor4f(r, g, b, a);

        GL11.glBegin(GL11.GL_POLYGON);
        for (int i = 0; i <= 90; i += 3) {
            double angle = Math.toRadians(i);
            GL11.glVertex2d(x + radius + Math.cos(angle) * radius, y + radius + Math.sin(angle) * radius);
        }
        for (int i = 90; i <= 180; i += 3) {
            double angle = Math.toRadians(i);
            GL11.glVertex2d(x2 - radius + Math.cos(angle) * radius, y + radius + Math.sin(angle) * radius);
        }
        for (int i = 180; i <= 270; i += 3) {
            double angle = Math.toRadians(i);
            GL11.glVertex2d(x2 - radius + Math.cos(angle) * radius, y2 - radius + Math.sin(angle) * radius);
        }
        for (int i = 270; i <= 360; i += 3) {
            double angle = Math.toRadians(i);
            GL11.glVertex2d(x + radius + Math.cos(angle) * radius, y2 - radius + Math.sin(angle) * radius);
        }
        GL11.glEnd();

        GL11.glEnable(GL11.GL_TEXTURE_2D);
        GL11.glDisable(GL11.GL_BLEND);
        GL11.glPopMatrix();
    }

    private class CategoryPanel {
        String name;
        List<Module> modules;
        int x, y;
        boolean dragging = false;
        boolean open = true;
        int dragX, dragY;
        int scroll = 0;
        float animation = 1.0f;

        CategoryPanel(String name, List<Module> modules, int x, int y) {
            this.name = name;
            this.modules = modules;
            this.x = x;
            this.y = y;
        }

        int getContentHeight() {
            int h = 0;
            int visibleCount = Math.min(modules.size(), MAX_PANEL_HEIGHT / MODULE_HEIGHT);
            for (int i = 0; i < visibleCount; i++) {
                int idx = i + scroll;
                if (idx >= modules.size()) break;
                Module mod = modules.get(idx);
                h += MODULE_HEIGHT;
                if (expandedModule == mod) {
                    h += getSettingsCount(mod) * SETTING_HEIGHT + 4;
                }
            }
            return h;
        }

        int getSettingsCount(Module mod) {
            int count = 1;
            for (Setting s : mod.getSettings()) {
                if (s instanceof ButtonSetting || s instanceof SliderSetting) {
                    if (s.visible) count++;
                }
            }
            return count;
        }

        void draw(int mouseX, int mouseY, FontRenderer fr) {
            if (dragging) {
                x = mouseX - dragX;
                y = mouseY - dragY;
            }

            int contentHeight = open ? getContentHeight() + 4 : 0;
            int totalHeight = HEADER_HEIGHT + contentHeight;

            drawRoundedRect(x - 1, y - 1, x + PANEL_WIDTH + 1, y + totalHeight + 1, 6, new Color(0, 0, 0, 160).getRGB());
            drawRoundedRect(x, y, x + PANEL_WIDTH, y + totalHeight, 5, new Color(22, 22, 26, 235).getRGB());

            boolean headerHover = isHovering(mouseX, mouseY, x, y, PANEL_WIDTH, HEADER_HEIGHT);
            int headerColor = headerHover ? new Color(45, 45, 52, 235).getRGB() : new Color(35, 35, 42, 235).getRGB();
            drawRoundedRect(x, y, x + PANEL_WIDTH, y + HEADER_HEIGHT, 5, headerColor);

            fr.drawStringWithShadow(name, x + 8, y + (HEADER_HEIGHT - fr.FONT_HEIGHT) / 2, 0xFFFFFF);
            String countStr = String.valueOf(modules.size());
            fr.drawStringWithShadow(countStr, x + PANEL_WIDTH - fr.getStringWidth(countStr) - 8, y + (HEADER_HEIGHT - fr.FONT_HEIGHT) / 2, 0x666666);

            if (open) {
                int contentY = y + HEADER_HEIGHT + 2;
                int curY = contentY;

                for (int i = 0; i < modules.size(); i++) {
                    if (curY - contentY >= MAX_PANEL_HEIGHT) break;
                    int idx = i + scroll;
                    if (idx >= modules.size()) break;
                    Module mod = modules.get(idx);

                    boolean modHover = isHovering(mouseX, mouseY, x, curY, PANEL_WIDTH, MODULE_HEIGHT);
                    boolean enabled = mod.isEnabled();
                    boolean expanded = expandedModule == mod;

                    int modBg;
                    if (enabled) {
                        modBg = modHover ? new Color(60, 140, 220, 160).getRGB() : new Color(50, 120, 200, 120).getRGB();
                    } else {
                        modBg = modHover ? new Color(50, 50, 58, 200).getRGB() : new Color(30, 30, 36, 160).getRGB();
                    }
                    drawRect(x + 2, curY, x + PANEL_WIDTH - 2, curY + MODULE_HEIGHT - 1, modBg);

                    if (enabled) {
                        drawRect(x + 2, curY, x + 4, curY + MODULE_HEIGHT - 1, new Color(80, 170, 255).getRGB());
                    }

                    int textColor = enabled ? 0xFFFFFF : 0xAAAAAA;
                    fr.drawStringWithShadow(mod.getName(), x + 8, curY + (MODULE_HEIGHT - fr.FONT_HEIGHT) / 2, textColor);

                    String info = mod.getInfo();
                    if (info != null && !info.isEmpty()) {
                        int iw = fr.getStringWidth(info);
                        fr.drawStringWithShadow(info, x + PANEL_WIDTH - iw - 8, curY + (MODULE_HEIGHT - fr.FONT_HEIGHT) / 2, 0x777777);
                    }

                    curY += MODULE_HEIGHT;

                    if (expanded) {
                        drawSettings(mod, x + 4, curY, PANEL_WIDTH - 8, fr, mouseX, mouseY);
                        curY += getSettingsCount(mod) * SETTING_HEIGHT + 4;
                    }
                }
            }
        }

        void drawSettings(Module mod, int sx, int sy, int sw, FontRenderer fr, int mouseX, int mouseY) {
            int cy = sy;

            String bindText = "Bind: " + (mod.getKeycode() == 0 ? "NONE" : Keyboard.getKeyName(mod.getKeycode()));
            boolean bindHover = isHovering(mouseX, mouseY, sx, cy, sw, SETTING_HEIGHT);
            if (bindHover) {
                drawRect(sx, cy, sx + sw, cy + SETTING_HEIGHT - 1, new Color(60, 60, 70, 180).getRGB());
            }
            fr.drawStringWithShadow(bindText, sx + 4, cy + 2, bindingModule == mod ? new Color(100, 180, 255).getRGB() : 0xCCCCCC);
            cy += SETTING_HEIGHT;

            for (Setting s : mod.getSettings()) {
                if (!s.visible) continue;
                if (s instanceof ButtonSetting) {
                    ButtonSetting bs = (ButtonSetting) s;
                    boolean hover = isHovering(mouseX, mouseY, sx, cy, sw, SETTING_HEIGHT);
                    if (hover) drawRect(sx, cy, sx + sw, cy + SETTING_HEIGHT - 1, new Color(60, 60, 70, 180).getRGB());

                    fr.drawStringWithShadow(bs.getName(), sx + 4, cy + 2, 0xBBBBBB);
                    int toggleX = sx + sw - 16;
                    int toggleColor = bs.isToggled() ? new Color(80, 170, 255).getRGB() : new Color(80, 80, 90).getRGB();
                    drawRoundedRect(toggleX, cy + 2, toggleX + 12, cy + SETTING_HEIGHT - 2, 3, toggleColor);
                    if (bs.isToggled()) {
                        drawRect(toggleX + 7, cy + 3, toggleX + 11, cy + SETTING_HEIGHT - 3, 0xFFFFFFFF);
                    } else {
                        drawRect(toggleX + 2, cy + 3, toggleX + 6, cy + SETTING_HEIGHT - 3, 0xFFCCCCCC);
                    }
                    cy += SETTING_HEIGHT;
                } else if (s instanceof SliderSetting) {
                    SliderSetting ss = (SliderSetting) s;
                    boolean hover = isHovering(mouseX, mouseY, sx, cy, sw, SETTING_HEIGHT);
                    if (hover) drawRect(sx, cy, sx + sw, cy + SETTING_HEIGHT - 1, new Color(60, 60, 70, 180).getRGB());

                    String valStr;
                    if (ss.isString) {
                        valStr = ss.getOptions()[(int) ss.getInput()];
                    } else {
                        double val = ss.getInput();
                        valStr = val == Math.rint(val) ? String.valueOf((int) val) : String.valueOf(Math.round(val * 100.0) / 100.0);
                    }
                    String label = ss.getName() + ": " + valStr;
                    fr.drawStringWithShadow(label, sx + 4, cy + 2, 0xBBBBBB);

                    if (!ss.isString) {
                        int barX = sx + 4;
                        int barW = sw - 8;
                        int barY = cy + SETTING_HEIGHT - 3;
                        double ratio = (ss.getInput() - ss.getMin()) / (ss.getMax() - ss.getMin());
                        drawRect(barX, barY, barX + barW, barY + 1, new Color(60, 60, 70).getRGB());
                        drawRect(barX, barY, barX + (int) (barW * ratio), barY + 1, new Color(80, 170, 255).getRGB());
                    }
                    cy += SETTING_HEIGHT;
                }
            }
        }

        void mouseClicked(int mouseX, int mouseY, int button) {
            if (isHovering(mouseX, mouseY, x, y, PANEL_WIDTH, HEADER_HEIGHT)) {
                if (button == 0) {
                    dragging = true;
                    dragX = mouseX - x;
                    dragY = mouseY - y;
                } else if (button == 1) {
                    open = !open;
                }
                return;
            }

            if (!open) return;

            int contentY = y + HEADER_HEIGHT + 2;
            int curY = contentY;

            for (int i = 0; i < modules.size(); i++) {
                if (curY - contentY >= MAX_PANEL_HEIGHT) break;
                int idx = i + scroll;
                if (idx >= modules.size()) break;
                Module mod = modules.get(idx);

                if (isHovering(mouseX, mouseY, x, curY, PANEL_WIDTH, MODULE_HEIGHT)) {
                    if (button == 0) {
                        mod.toggle();
                    } else if (button == 1) {
                        if (expandedModule == mod) {
                            expandedModule = null;
                        } else {
                            expandedModule = mod;
                        }
                    }
                    return;
                }
                curY += MODULE_HEIGHT;

                if (expandedModule == mod) {
                    int settingsH = getSettingsCount(mod) * SETTING_HEIGHT + 4;
                    if (isHovering(mouseX, mouseY, x + 4, curY, PANEL_WIDTH - 8, settingsH)) {
                        handleSettingClick(mod, mouseX, mouseY, x + 4, curY, PANEL_WIDTH - 8, button);
                        return;
                    }
                    curY += settingsH;
                }
            }
        }

        void handleSettingClick(Module mod, int mouseX, int mouseY, int sx, int sy, int sw, int button) {
            int cy = sy;

            if (isHovering(mouseX, mouseY, sx, cy, sw, SETTING_HEIGHT)) {
                if (button == 0) {
                    bindingModule = mod;
                }
                return;
            }
            cy += SETTING_HEIGHT;

            for (Setting s : mod.getSettings()) {
                if (!s.visible) continue;
                if (s instanceof ButtonSetting) {
                    if (isHovering(mouseX, mouseY, sx, cy, sw, SETTING_HEIGHT)) {
                        if (button == 0) {
                            ((ButtonSetting) s).toggle();
                        }
                        return;
                    }
                    cy += SETTING_HEIGHT;
                } else if (s instanceof SliderSetting) {
                    if (isHovering(mouseX, mouseY, sx, cy, sw, SETTING_HEIGHT)) {
                        SliderSetting ss = (SliderSetting) s;
                        if (button == 0) {
                            if (ss.isString) {
                                int idx = (int) ss.getInput();
                                idx = (idx + 1) % ss.getOptions().length;
                                ss.setValue(idx);
                            } else {
                                int barX = sx + 4;
                                int barW = sw - 8;
                                double ratio = (double) (mouseX - barX) / barW;
                                ratio = Math.max(0, Math.min(1, ratio));
                                double val = ss.getMin() + ratio * (ss.getMax() - ss.getMin());
                                ss.setValue(val);
                            }
                        } else if (button == 1) {
                            if (ss.isString) {
                                int idx = (int) ss.getInput();
                                idx = (idx - 1 + ss.getOptions().length) % ss.getOptions().length;
                                ss.setValue(idx);
                            }
                        }
                        return;
                    }
                    cy += SETTING_HEIGHT;
                }
            }
        }

        void mouseReleased(int mouseX, int mouseY, int state) {
            dragging = false;
        }

        void onScroll(int mouseX, int mouseY, int dir) {
            if (!open) return;
            if (!isHovering(mouseX, mouseY, x, y + HEADER_HEIGHT, PANEL_WIDTH, MAX_PANEL_HEIGHT)) return;
            scroll += dir;
            scroll = Math.max(0, Math.min(scroll, Math.max(0, modules.size() - MAX_PANEL_HEIGHT / MODULE_HEIGHT)));
        }

        boolean isHovering(int mouseX, int mouseY, int x, int y, int w, int h) {
            return mouseX >= x && mouseX <= x + w && mouseY >= y && mouseY <= y + h;
        }
    }
}
