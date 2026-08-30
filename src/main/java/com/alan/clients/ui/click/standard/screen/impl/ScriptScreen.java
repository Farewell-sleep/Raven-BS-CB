package com.alan.clients.ui.click.standard.screen.impl;

import com.alan.clients.bridge.RiseModuleWrapper;
import com.alan.clients.ui.click.standard.RiseClickGUI;
import com.alan.clients.ui.click.standard.UIColors;
import com.alan.clients.ui.click.standard.components.ModuleComponent;
import com.alan.clients.ui.click.standard.components.value.ValueComponent;
import com.alan.clients.ui.click.standard.screen.Screen;
import com.alan.clients.util.font.FontManager;
import com.alan.clients.util.font.FontWeight;
import com.alan.clients.util.gui.GUIUtil;
import com.alan.clients.util.gui.ScrollUtil;
import com.alan.clients.util.interfaces.InstanceAccess;
import com.alan.clients.util.render.ColorUtil;
import com.alan.clients.util.render.RenderUtil;
import com.alan.clients.util.vector.Vector2d;
import com.alan.clients.util.vector.Vector2f;
import com.alan.clients.value.Value;
import keystrokesmod.Raven;
import keystrokesmod.module.Module;
import keystrokesmod.script.Manager;
import keystrokesmod.script.Script;
import org.lwjgl.Sys;
import org.lwjgl.input.Keyboard;

import java.awt.Color;
import java.awt.Desktop;
import java.net.URI;
import java.util.ArrayList;
import java.util.ConcurrentModificationException;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;

/**
 * Scripts 页面（Raven BS 版，整合 BS Manager 全部功能）：
 *  - Script name 输入框 + Create script / Load scripts / Open folder / View documentation
 *  - Privacy: Enable http requests / Enable websockets 开关
 *  - Scripts 横条列表；左键开关，右键在横条下方展开模块设置信息
 */
public final class ScriptScreen implements Screen, InstanceAccess {
    public static boolean azE;
    public ScrollUtil scrollUtil = new ScrollUtil();

    private String scriptNameInput = "";
    private boolean scriptNameFocused = false;
    private final Vector2f scriptNameBox = new Vector2f(0, 0);
    private final Vector2f scriptNameSize = new Vector2f(240, 24);

    private static final String DOC_URL = "https://blowsy.gitbook.io/raven";

    // 按钮位置缓存
    private final List<Vector2f> buttonPos = new ArrayList<>();
    private final List<Float> buttonWidth = new ArrayList<>();
    private final String[] buttonLabels = {"Create script", "Load scripts", "Open folder", "View documentation"};

    // 横条位置缓存
    private final List<Vector2f> rowPos = new ArrayList<>();
    private final List<Module> rowModule = new ArrayList<>();
    private final List<Boolean> rowFailed = new ArrayList<>();
    private final List<Float> rowHeight = new ArrayList<>();

    private final Map<Module, ModuleComponent> componentMap = new HashMap<>();
    private final Set<Module> expandedSet = new HashSet<>();

    @Override
    public void onRender(int var1, int var2, float var3) {
        RiseClickGUI gui = this.getStandardClickGUI();
        double baseX = gui.axI.x + gui.sidebar.aym + 10.0;
        double baseY = gui.axI.y + this.scrollUtil.tE() + 10.0;
        double contentW = gui.position.x - gui.sidebar.aym - 20.0;

        // ===== 输入框行 =====
        FontManager.MAIN.a(15, FontWeight.REGULAR).a("Script name", (float) baseX, (float) baseY, Color.WHITE.getRGB());
        this.scriptNameBox.x = (float) baseX;
        this.scriptNameBox.y = (float) (baseY + 16);
        RenderUtil.roundedRectangle(this.scriptNameBox.x, this.scriptNameBox.y, this.scriptNameSize.x, this.scriptNameSize.y, 5.0,
            this.scriptNameFocused ? UIColors.BACKGROUND.pV() : UIColors.OVERLAY.pV());
        String display = this.scriptNameInput.isEmpty() && !this.scriptNameFocused
            ? "Type a script name..."
            : this.scriptNameInput + (this.scriptNameFocused ? "_" : "");
        FontManager.MAIN.a(14, FontWeight.REGULAR).a(display, this.scriptNameBox.x + 8, this.scriptNameBox.y + 5,
            this.scriptNameInput.isEmpty() && !this.scriptNameFocused ? UIColors.TRINARY_TEXT.pW() : Color.WHITE.getRGB());

        // ===== 按钮行 =====
        this.buttonPos.clear();
        this.buttonWidth.clear();
        double bx = baseX;
        double by = baseY + 48;
        for (int i = 0; i < this.buttonLabels.length; i++) {
            String label = this.buttonLabels[i];
            float bw = FontManager.MAIN.a(14, FontWeight.REGULAR).getStringWidth(label) + 22.0F;
            this.buttonPos.add(new Vector2f((float) bx, (float) by));
            this.buttonWidth.add(bw);
            boolean hover = GUIUtil.c((float) bx, (float) by, bw, 22.0F, var1, var2);
            RenderUtil.roundedRectangle(bx, by, bw, 22.0, 5.0, hover ? UIColors.BACKGROUND.pV() : UIColors.OVERLAY.pV());
            FontManager.MAIN.a(14, FontWeight.REGULAR).a(label, (float) (bx + 11.0), (float) (by + 5.0), Color.WHITE.getRGB());
            bx += bw + 6.0;
        }

        // ===== Privacy 开关行 =====
        double py = baseY + 80;
        FontManager.MAIN.a(15, FontWeight.BOLD).a("Privacy", (float) baseX, (float) py, Color.WHITE.getRGB());
        this.renderToggle(baseX, py + 18, "Enable http requests", Manager.enableHttpRequests != null && Manager.enableHttpRequests.isToggled(), var1, var2);
        this.renderToggle(baseX + 200, py + 18, "Enable websockets", Manager.enableWebSockets != null && Manager.enableWebSockets.isToggled(), var1, var2);

        // ===== 横条列表 =====
        this.rowPos.clear();
        this.rowModule.clear();
        this.rowFailed.clear();
        this.rowHeight.clear();
        double ly = baseY + 124;
        this.scrollUtil.qx();
        try {
            if (Raven.scriptManager != null) {
                for (Map.Entry<Script, Module> entry : Raven.scriptManager.scripts.entrySet()) {
                    Script script = entry.getKey();
                    Module module = entry.getValue();
                    if (script == null || module == null || module.getName() == null) continue;
                    boolean failed = script.error;
                    boolean enabled = module.isEnabled();
                    boolean expanded = !failed && this.expandedSet.contains(module);
                    double rowH = 32.0;

                    this.rowPos.add(new Vector2f((float) baseX, (float) ly));
                    this.rowModule.add(module);
                    this.rowFailed.add(failed);

                    RenderUtil.roundedRectangle(baseX, ly, contentW, 32.0, 6.0, UIColors.OVERLAY.pV());
                    boolean rowHover = GUIUtil.c((float) baseX, (float) ly, (float) contentW, 32.0F, var1, var2);
                    if (rowHover) {
                        RenderUtil.roundedRectangle(baseX, ly, contentW, 32.0, 6.0, ColorUtil.withAlpha(Color.BLACK, 25));
                    }
                    int nameColor = failed ? new Color(255, 80, 80).getRGB()
                        : enabled ? this.rz().getAccentColor(new Vector2d(0, ly / 5)).getRGB()
                        : Color.WHITE.getRGB();
                    FontManager.MAIN.a(18, FontWeight.REGULAR).a(module.getName(), (float) (baseX + 8), (float) (ly + 6), nameColor);
                    String desc = failed ? "Failed to load" : (enabled ? "Enabled" : "Disabled");
                    FontManager.MAIN.a(13, FontWeight.REGULAR).a(desc, (float) (baseX + 8), (float) (ly + 20),
                        failed ? new Color(255, 120, 120).getRGB() : UIColors.TRINARY_TEXT.pW());

                    // 展开设置
                    if (expanded) {
                        ModuleComponent mc = this.componentMap.get(module);
                        if (mc == null) {
                            mc = new ModuleComponent(new RiseModuleWrapper(module));
                            this.componentMap.put(module, mc);
                        }
                        float sy = (float) (ly + 32.0 + 1.0);
                        for (ValueComponent vc : mc.getValueList()) {
                            Value<?> value = vc.getValue();
                            if (value != null && (value.getHideIf() != null && value.getHideIf().getAsBoolean()
                                || value.getBooleanSupplier() != null && value.getBooleanSupplier().getAsBoolean())) {
                                continue;
                            }
                            vc.draw(new Vector2d(baseX + 6.0, sy), var1, var2, var3);
                            sy += vc.getHeight();
                        }
                        rowH = (sy - ly) - 1.0;
                    }

                    this.rowHeight.add((float) rowH);
                    ly += rowH + 4.0;
                }
            }
        } catch (ConcurrentModificationException ignored) {
        }

        this.scrollUtil.V(-(ly - this.scrollUtil.tE() - gui.axI.y) + gui.position.y - 7.0);
        this.scrollUtil.a(new Vector2d(gui.getScale().x + gui.getPosition().x - 4.0, gui.getScale().y + 7.0), gui.position.y - 14.0);
    }

    private void renderToggle(double x, double y, String label, boolean state, int mouseX, int mouseY) {
        boolean hover = GUIUtil.c((float) x, (float) y, 180.0F, 18.0F, mouseX, mouseY);
        RenderUtil.roundedRectangle(x, y, 32.0, 16.0, 8.0, state ? this.rz().rA() : UIColors.OVERLAY.pV());
        RenderUtil.roundedRectangle(state ? x + 17 : x + 2, y + 2, 12.0, 12.0, 6.0, Color.WHITE);
        FontManager.MAIN.a(14, FontWeight.REGULAR).a(label, (float) (x + 40), (float) (y + 2),
            hover ? Color.WHITE.getRGB() : UIColors.SECONDARY_TEXT.pW());
    }

    @Override
    public void onKey(char var1, int var2) {
        if (this.scriptNameFocused) {
            if (var2 == Keyboard.KEY_BACK && !this.scriptNameInput.isEmpty()) {
                this.scriptNameInput = this.scriptNameInput.substring(0, this.scriptNameInput.length() - 1);
            } else if (var1 >= 32 && var1 < 127 && this.scriptNameInput.length() < 32) {
                this.scriptNameInput += var1;
            }
        }
    }

    @Override
    public void f(int var1, int var2, int var3) {
        // 输入框聚焦
        if (GUIUtil.c(this.scriptNameBox.x, this.scriptNameBox.y, this.scriptNameSize.x, this.scriptNameSize.y, var1, var2) && var3 == 0) {
            this.scriptNameFocused = true;
            return;
        } else if (var3 == 0) {
            this.scriptNameFocused = false;
        }

        // 按钮
        for (int i = 0; i < this.buttonLabels.length; i++) {
            if (i < this.buttonPos.size() && GUIUtil.c(this.buttonPos.get(i).x, this.buttonPos.get(i).y, this.buttonWidth.get(i), 22.0F, var1, var2) && var3 == 0) {
                this.onButton(i);
                return;
            }
        }

        // Privacy 开关
        double baseX = this.getStandardClickGUI().axI.x + this.getStandardClickGUI().sidebar.aym + 10.0;
        double py = this.getStandardClickGUI().axI.y + this.scrollUtil.tE() + 10.0 + 98;
        if (GUIUtil.c((float) baseX, (float) py, 180.0F, 18.0F, var1, var2) && var3 == 0 && Manager.enableHttpRequests != null) {
            Manager.enableHttpRequests.toggle();
            return;
        }
        if (GUIUtil.c((float) (baseX + 200), (float) py, 180.0F, 18.0F, var1, var2) && var3 == 0 && Manager.enableWebSockets != null) {
            Manager.enableWebSockets.toggle();
            return;
        }

        // 横条列表
        for (int i = 0; i < this.rowPos.size(); i++) {
            Vector2f pos = this.rowPos.get(i);
            Module module = this.rowModule.get(i);
            boolean failed = this.rowFailed.get(i);
            float h = i < this.rowHeight.size() ? this.rowHeight.get(i) : 32.0F;
            double contentW = this.getStandardClickGUI().position.x - this.getStandardClickGUI().sidebar.aym - 20.0;

            // 横条头部区域（32px）：左键开关，右键切换展开
            if (GUIUtil.c(pos.x, pos.y, (float) contentW, 32.0F, var1, var2)) {
                if (var3 == 0 && !failed) {
                    module.toggle();
                    this.aT();
                } else if (var3 == 1 && !failed) {
                    if (this.expandedSet.contains(module)) {
                        this.expandedSet.remove(module);
                    } else {
                        this.expandedSet.add(module);
                    }
                }
                return;
            }

            // 展开区域：把点击传给设置组件
            if (!failed && this.expandedSet.contains(module) && h > 32.0F) {
                ModuleComponent mc = this.componentMap.get(module);
                if (mc != null && GUIUtil.c(pos.x, pos.y + 32.0F, (float) contentW, h - 32.0F, var1, var2)) {
                    for (ValueComponent vc : mc.getValueList()) {
                        Value<?> value = vc.getValue();
                        if (value != null && (value.getHideIf() != null && value.getHideIf().getAsBoolean()
                            || value.getBooleanSupplier() != null && value.getBooleanSupplier().getAsBoolean())) {
                            continue;
                        }
                        if (vc.e(var1, var2, var3)) break;
                    }
                    return;
                }
            }
        }
    }

    private void onButton(int i) {
        try {
            switch (i) {
                case 0: // Create script
                    if (Raven.scriptManager != null && !this.scriptNameInput.trim().isEmpty()) {
                        String created = Raven.scriptManager.createScript(this.scriptNameInput.trim());
                        if (created != null) {
                            this.scriptNameInput = "";
                            this.aT();
                        }
                    }
                    break;
                case 1: // Load scripts
                    if (Raven.scriptManager != null) {
                        Raven.scriptManager.loadScripts();
                        this.aT();
                    }
                    break;
                case 2: // Open folder
                    if (Raven.scriptManager != null && Raven.scriptManager.directory != null) {
                        Desktop.getDesktop().open(Raven.scriptManager.directory);
                    }
                    break;
                case 3: // View documentation
                    try {
                        Desktop.getDesktop().browse(new URI(DOC_URL));
                    } catch (Throwable t) {
                        Sys.openURL(DOC_URL);
                    }
                    break;
            }
        } catch (Throwable ignored) {
        }
    }

    @Override
    public void oG() {
    }

    @Override
    public void pY() {
    }

    @Override
    public void aT() {
        // 列表在 onRender 中动态构建，这里仅重置选中状态（如需）
    }
}
