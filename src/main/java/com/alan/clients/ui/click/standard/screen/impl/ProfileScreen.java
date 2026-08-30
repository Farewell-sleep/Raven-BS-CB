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
import keystrokesmod.utility.profile.Profile;
import org.lwjgl.input.Keyboard;

import java.awt.Color;
import java.awt.Desktop;
import java.util.ArrayList;
import java.util.ConcurrentModificationException;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;

/**
 * Profiles 页面（Raven BS 版）：
 *  - Profile name 输入框 + Create profile / Load profiles / Open folder
 *  - Profiles 横条列表；左键加载，右键在横条下方展开模块设置信息
 */
public final class ProfileScreen implements Screen, InstanceAccess {
    public static boolean azE;
    public ScrollUtil scrollUtil = new ScrollUtil();

    private String profileNameInput = "";
    private boolean profileNameFocused = false;
    private final Vector2f profileNameBox = new Vector2f(0, 0);
    private final Vector2f profileNameSize = new Vector2f(240, 24);

    private final List<Vector2f> buttonPos = new ArrayList<>();
    private final List<Float> buttonWidth = new ArrayList<>();
    private final String[] buttonLabels = {"Create profile", "Load profiles", "Open folder"};

    private final List<Vector2f> rowPos = new ArrayList<>();
    private final List<Profile> rowProfile = new ArrayList<>();
    private final List<Float> rowHeight = new ArrayList<>();

    private final Map<Profile, ModuleComponent> componentMap = new HashMap<>();
    private final Set<Profile> expandedSet = new HashSet<>();

    private final List<Vector2f> actionBtnPos = new ArrayList<>();
    private final List<Profile> actionBtnProfile = new ArrayList<>();
    private final List<Integer> actionBtnType = new ArrayList<>();

    @Override
    public void onRender(int var1, int var2, float var3) {
        RiseClickGUI gui = this.getStandardClickGUI();
        double baseX = gui.axI.x + gui.sidebar.aym + 10.0;
        double baseY = gui.axI.y + this.scrollUtil.tE() + 10.0;
        double contentW = gui.position.x - gui.sidebar.aym - 20.0;

        // ===== 输入框行 =====
        FontManager.MAIN.a(15, FontWeight.REGULAR).a("Profile name", (float) baseX, (float) baseY, Color.WHITE.getRGB());
        this.profileNameBox.x = (float) baseX;
        this.profileNameBox.y = (float) (baseY + 16);
        RenderUtil.roundedRectangle(this.profileNameBox.x, this.profileNameBox.y, this.profileNameSize.x, this.profileNameSize.y, 5.0,
            this.profileNameFocused ? UIColors.BACKGROUND.pV() : UIColors.OVERLAY.pV());
        String display = this.profileNameInput.isEmpty() && !this.profileNameFocused
            ? "Type a profile name..."
            : this.profileNameInput + (this.profileNameFocused ? "_" : "");
        FontManager.MAIN.a(14, FontWeight.REGULAR).a(display, this.profileNameBox.x + 8, this.profileNameBox.y + 5,
            this.profileNameInput.isEmpty() && !this.profileNameFocused ? UIColors.TRINARY_TEXT.pW() : Color.WHITE.getRGB());

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

        // ===== 横条列表 =====
        this.rowPos.clear();
        this.rowProfile.clear();
        this.rowHeight.clear();
        this.actionBtnPos.clear();
        this.actionBtnProfile.clear();
        this.actionBtnType.clear();
        double ly = baseY + 84;
        this.scrollUtil.qx();
        try {
            if (Raven.profileManager != null) {
                for (Profile profile : Raven.profileManager.profiles) {
                    if (profile == null || profile.getName() == null) continue;
                    boolean isCurrent = Raven.currentProfile != null && Raven.currentProfile.getName() != null
                        && Raven.currentProfile.getName().equals(profile.getName());
                    boolean expanded = this.expandedSet.contains(profile);
                    double rowH = 32.0;

                    this.rowPos.add(new Vector2f((float) baseX, (float) ly));
                    this.rowProfile.add(profile);

                    // 横条背景
                    RenderUtil.roundedRectangle(baseX, ly, contentW, 32.0, 6.0, UIColors.OVERLAY.pV());
                    boolean rowHover = GUIUtil.c((float) baseX, (float) ly, (float) contentW, 32.0F, var1, var2);
                    if (rowHover) {
                        RenderUtil.roundedRectangle(baseX, ly, contentW, 32.0, 6.0, ColorUtil.withAlpha(Color.BLACK, 25));
                    }
                    int nameColor = isCurrent ? this.rz().getAccentColor(new Vector2d(0, ly / 5)).getRGB() : Color.WHITE.getRGB();
                    FontManager.MAIN.a(18, FontWeight.REGULAR).a(profile.getName(), (float) (baseX + 8), (float) (ly + 6), nameColor);
                    FontManager.MAIN.a(13, FontWeight.REGULAR).a(isCurrent ? "Current profile" : "Click to load", (float) (baseX + 8), (float) (ly + 20),
                        isCurrent ? new Color(120, 180, 255).getRGB() : UIColors.TRINARY_TEXT.pW());

                    // 展开设置
                    if (expanded) {
                        float sy = (float) (ly + 32.0 + 1.0);

                        // Save / Remove 按钮
                        String[] actionLabels = {"Save profile", "Remove profile"};
                        double abx = baseX + 6.0;
                        for (int ai = 0; ai < actionLabels.length; ai++) {
                            String label = actionLabels[ai];
                            float abw = FontManager.MAIN.a(14, FontWeight.REGULAR).getStringWidth(label) + 20.0F;
                            boolean aHover = GUIUtil.c((float) abx, sy, abw, 20.0F, var1, var2);
                            int abColor = ai == 0 ? new Color(80, 180, 120).getRGB() : new Color(200, 70, 70).getRGB();
                            RenderUtil.roundedRectangle(abx, sy, abw, 20.0, 5.0, aHover ? UIColors.BACKGROUND.pV() : UIColors.OVERLAY.pV());
                            FontManager.MAIN.a(14, FontWeight.REGULAR).a(label, (float) (abx + 10.0), (float) (sy + 4.0), abColor);
                            this.actionBtnPos.add(new Vector2f((float) abx, sy));
                            this.actionBtnProfile.add(profile);
                            this.actionBtnType.add(ai);
                            abx += abw + 6.0;
                        }
                        sy += 24.0;

                        // 模块设置
                        if (profile.getModule() != null) {
                            ModuleComponent mc = this.componentMap.get(profile);
                            if (mc == null) {
                                mc = new ModuleComponent(new RiseModuleWrapper(profile.getModule()));
                                this.componentMap.put(profile, mc);
                            }
                            for (ValueComponent vc : mc.getValueList()) {
                                Value<?> value = vc.getValue();
                                if (value != null && (value.getHideIf() != null && value.getHideIf().getAsBoolean()
                                    || value.getBooleanSupplier() != null && value.getBooleanSupplier().getAsBoolean())) {
                                    continue;
                                }
                                vc.draw(new Vector2d(baseX + 6.0, sy), var1, var2, var3);
                                sy += vc.getHeight();
                            }
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

    @Override
    public void onKey(char var1, int var2) {
        if (this.profileNameFocused) {
            if (var2 == Keyboard.KEY_BACK && !this.profileNameInput.isEmpty()) {
                this.profileNameInput = this.profileNameInput.substring(0, this.profileNameInput.length() - 1);
            } else if (var1 >= 32 && var1 < 127 && this.profileNameInput.length() < 32) {
                this.profileNameInput += var1;
            }
        }
    }

    @Override
    public void f(int var1, int var2, int var3) {
        // 输入框聚焦
        if (GUIUtil.c(this.profileNameBox.x, this.profileNameBox.y, this.profileNameSize.x, this.profileNameSize.y, var1, var2) && var3 == 0) {
            this.profileNameFocused = true;
            return;
        } else if (var3 == 0) {
            this.profileNameFocused = false;
        }

        // 按钮
        for (int i = 0; i < this.buttonLabels.length; i++) {
            if (i < this.buttonPos.size() && GUIUtil.c(this.buttonPos.get(i).x, this.buttonPos.get(i).y, this.buttonWidth.get(i), 22.0F, var1, var2) && var3 == 0) {
                this.onButton(i);
                return;
            }
        }

        // Action 按钮（Save / Remove）
        for (int i = 0; i < this.actionBtnPos.size(); i++) {
            Vector2f ap = this.actionBtnPos.get(i);
            Profile profile = this.actionBtnProfile.get(i);
            int type = this.actionBtnType.get(i);
            String label = type == 0 ? "Save profile" : "Remove profile";
            float abw = FontManager.MAIN.a(14, FontWeight.REGULAR).getStringWidth(label) + 20.0F;
            if (GUIUtil.c(ap.x, ap.y, abw, 20.0F, var1, var2) && var3 == 0) {
                if (type == 0) {
                    // Save profile：保存当前配置到该 profile
                    if (Raven.profileManager != null && profile != null) {
                        Raven.profileManager.saveProfile(profile);
                    }
                } else {
                    // Remove profile：删除该 profile
                    if (Raven.profileManager != null && profile != null) {
                        Raven.profileManager.deleteProfile(profile.getName());
                        this.expandedSet.remove(profile);
                        this.componentMap.remove(profile);
                        Raven.profileManager.loadProfiles();
                    }
                }
                return;
            }
        }

        // 横条列表
        for (int i = 0; i < this.rowPos.size(); i++) {
            Vector2f pos = this.rowPos.get(i);
            Profile profile = this.rowProfile.get(i);
            float h = i < this.rowHeight.size() ? this.rowHeight.get(i) : 32.0F;
            double contentW = this.getStandardClickGUI().position.x - this.getStandardClickGUI().sidebar.aym - 20.0;

            // 横条头部区域（32px）：左键加载，右键切换展开
            if (GUIUtil.c(pos.x, pos.y, (float) contentW, 32.0F, var1, var2)) {
                if (var3 == 0) {
                    Raven.profileManager.loadProfile(profile.getName());
                } else if (var3 == 1) {
                    if (this.expandedSet.contains(profile)) {
                        this.expandedSet.remove(profile);
                    } else {
                        this.expandedSet.add(profile);
                    }
                }
                return;
            }

            // 展开区域：把点击传给设置组件
            if (this.expandedSet.contains(profile) && h > 32.0F) {
                ModuleComponent mc = this.componentMap.get(profile);
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
                case 0: // Create profile
                    if (Raven.profileManager != null && !this.profileNameInput.trim().isEmpty()) {
                        Raven.profileManager.createProfile(this.profileNameInput.trim(), 0);
                        this.profileNameInput = "";
                        Raven.profileManager.loadProfiles();
                    }
                    break;
                case 1: // Load profiles
                    if (Raven.profileManager != null) {
                        Raven.profileManager.loadProfiles();
                    }
                    break;
                case 2: // Open folder
                    if (Raven.profileManager != null && Raven.profileManager.directory != null) {
                        Desktop.getDesktop().open(Raven.profileManager.directory);
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
    }
}
