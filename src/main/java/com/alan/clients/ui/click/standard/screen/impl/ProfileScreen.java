package com.alan.clients.ui.click.standard.screen.impl;

import com.alan.clients.ui.click.standard.RiseClickGUI;
import com.alan.clients.ui.click.standard.UIColors;
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
import keystrokesmod.Raven;
import keystrokesmod.module.Module;
import keystrokesmod.utility.profile.Profile;
import org.lwjgl.input.Keyboard;

import java.awt.Color;
import java.awt.Desktop;
import java.util.ArrayList;
import java.util.ConcurrentModificationException;
import java.util.List;

/**
 * Profiles 页面（Raven BS 版）：
 *  - Profile name 输入框 + Create profile / Load profiles / Open folder
 *  - Profiles 横条列表（combat 模块列表风格）；左键加载，右键展开信息 + 绑定
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

    private Module selectedModule;

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
        double ly = baseY + 84;
        this.scrollUtil.qx();
        try {
            if (Raven.profileManager != null) {
                for (Profile profile : Raven.profileManager.profiles) {
                    if (profile == null || profile.getName() == null) continue;
                    boolean isCurrent = Raven.currentProfile != null && Raven.currentProfile.getName() != null
                        && Raven.currentProfile.getName().equals(profile.getName());
                    this.rowPos.add(new Vector2f((float) baseX, (float) ly));
                    this.rowProfile.add(profile);

                    RenderUtil.roundedRectangle(baseX, ly, contentW, 32.0, 6.0, UIColors.OVERLAY.pV());
                    boolean rowHover = GUIUtil.c((float) baseX, (float) ly, (float) contentW, 32.0F, var1, var2);
                    if (rowHover) {
                        RenderUtil.roundedRectangle(baseX, ly, contentW, 32.0, 6.0, ColorUtil.withAlpha(Color.BLACK, 25));
                    }
                    int nameColor = isCurrent ? this.rz().getAccentColor(new Vector2d(0, ly / 5)).getRGB() : Color.WHITE.getRGB();
                    FontManager.MAIN.a(18, FontWeight.REGULAR).a(profile.getName(), (float) (baseX + 8), (float) (ly + 6), nameColor);
                    FontManager.MAIN.a(13, FontWeight.REGULAR).a(isCurrent ? "Current profile" : "Click to load", (float) (baseX + 8), (float) (ly + 20),
                        isCurrent ? new Color(120, 180, 255).getRGB() : UIColors.TRINARY_TEXT.pW());
                    ly += 36.0;
                }
            }
        } catch (ConcurrentModificationException ignored) {
        }

        this.scrollUtil.V(-(ly - this.scrollUtil.tE() - gui.axI.y) + gui.position.y - 7.0);
        this.scrollUtil.a(new Vector2d(gui.getScale().x + gui.getPosition().x - 4.0, gui.getScale().y + 7.0), gui.position.y - 14.0);

        this.renderDetailPanel(gui, var1, var2);
    }

    private void renderDetailPanel(RiseClickGUI gui, int mouseX, int mouseY) {
        if (this.selectedModule == null) return;
        double px = gui.axI.x + gui.sidebar.aym + gui.position.x - 270.0;
        double py = gui.axI.y + 20.0;
        double pw = 260.0;
        double ph = 108.0;
        RenderUtil.roundedRectangle(px, py, pw, ph, 8.0, UIColors.OVERLAY.pV());
        RenderUtil.roundedRectangle(px, py, pw, ph, 8.0, UIColors.OVERLAY.Y(60));

        String name = this.selectedModule.getName();
        FontManager.MAIN.a(20, FontWeight.BOLD).a(name == null ? "Module" : name, (float) (px + 10.0), (float) (py + 8.0), Color.WHITE.getRGB());

        boolean enabled = this.selectedModule.isEnabled();
        int stateColor = enabled ? new Color(120, 180, 255).getRGB() : Color.WHITE.getRGB();
        FontManager.MAIN.a(15, FontWeight.REGULAR).a(enabled ? "Enabled" : "Disabled", (float) (px + 10.0), (float) (py + 34.0), stateColor);

        int key = this.selectedModule.getKeycode();
        String keyStr = key == 0 ? "NONE" : Keyboard.getKeyName(key);
        FontManager.MAIN.a(15, FontWeight.REGULAR).a("Bind: " + keyStr, (float) (px + 10.0), (float) (py + 56.0), Color.WHITE.getRGB());

        boolean bindHover = GUIUtil.c((float) (px + 10.0), (float) (py + 74.0), 90.0F, 20.0F, mouseX, mouseY);
        RenderUtil.roundedRectangle(px + 10.0, py + 74.0, 90.0, 20.0, 5.0, bindHover ? UIColors.BACKGROUND.pV() : UIColors.BACKGROUND.Y(70));
        FontManager.MAIN.a(14, FontWeight.REGULAR).a("Click to bind", (float) (px + 15.0), (float) (py + 79.0), Color.WHITE.getRGB());
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

        // 详情面板绑定按钮
        if (this.selectedModule != null) {
            RiseClickGUI gui = this.getStandardClickGUI();
            double px = gui.axI.x + gui.sidebar.aym + gui.position.x - 270.0;
            double ppy = gui.axI.y + 20.0;
            if (GUIUtil.c((float) (px + 10.0), (float) (ppy + 74.0), 90.0F, 20.0F, var1, var2) && var3 == 0) {
                gui.startKeyBind(this.selectedModule);
                return;
            }
        }

        // 横条列表
        for (int i = 0; i < this.rowPos.size(); i++) {
            Vector2f pos = this.rowPos.get(i);
            double contentW = this.getStandardClickGUI().position.x - this.getStandardClickGUI().sidebar.aym - 20.0;
            if (GUIUtil.c(pos.x, pos.y, (float) contentW, 32.0F, var1, var2)) {
                Profile profile = this.rowProfile.get(i);
                if (var3 == 0) {
                    Raven.profileManager.loadProfile(profile.getName());
                } else if (var3 == 1 && profile.getModule() != null) {
                    this.selectedModule = profile.getModule();
                }
                return;
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
