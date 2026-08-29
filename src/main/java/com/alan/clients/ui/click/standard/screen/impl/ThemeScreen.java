package com.alan.clients.ui.click.standard.screen.impl;

import com.alan.clients.Client;
import com.alan.clients.util.interfaces.InstanceAccess;
import com.alan.clients.util.vector.Vector2d;
import com.alan.clients.util.vector.Vector2f;
import com.alan.clients.ui.click.standard.components.theme.ThemeComponent;
import com.alan.clients.ui.click.standard.components.theme.ThemeKeyColorComponent;
import com.alan.clients.ui.click.standard.screen.Screen;
import com.alan.clients.ui.theme.Themes;
import com.alan.clients.ui.theme.KeyColors;
import com.alan.clients.util.gui.GUIUtil;
import com.alan.clients.util.gui.ScrollUtil;
import com.alan.clients.util.localization.Localization;
import com.alan.clients.util.font.FontManager;
import com.alan.clients.util.font.FontWeight;
import java.awt.Color;
import java.util.ArrayList;
import java.util.Objects;

public class ThemeScreen implements Screen, InstanceAccess {
    private final ArrayList<ThemeComponent> allThemes = new ArrayList<>();
    private ArrayList<ThemeComponent> visibleThemes = new ArrayList<>();
    private final ArrayList<ThemeKeyColorComponent> colors = new ArrayList<>();
    private final ScrollUtil azW = new ScrollUtil();
    private ThemeKeyColorComponent selectedColor = null;

    public ThemeScreen() {
        for (Themes themes : Themes.values()) {
            this.allThemes.add(new ThemeComponent(themes));
        }

        for (KeyColors adw : KeyColors.values()) {
            this.colors.add(new ThemeKeyColorComponent(adw));
        }
    }

    @Override
    public void onRender(int var1, int var2, float var3) {
        this.azW.qx();
        double d0 = Math.ceil(this.visibleThemes.size() / 3.0);
        this.azW.V(-57.0 * Math.max(0.0, d0 - 3.0));
        double d1 = 7.0;
        double d2 = this.getStandardClickGUI().getScale().getX() + this.getStandardClickGUI().getPosition().getX() - 4.0F;
        double d3 = this.getStandardClickGUI().getScale().getY() + d1;
        this.azW.a(new Vector2d(d2, d3), this.getStandardClickGUI().position.y - d1 * 2.0);
        Vector2f vector2f = this.getStandardClickGUI().getScale();
        Vector2f vector2f1 = this.getStandardClickGUI().getPosition();
        double d4 = this.getStandardClickGUI().getSidebar().aym;
        double d5 = vector2f.getY() + 44.0F + this.azW.tE();
        double d6 = (vector2f1.getX() - d4 - 29.0) / 3.0;
        double d7 = (vector2f1.getX() - d4 - 43.0) / 5.0;
        FontManager.MAIN
            .a(16, FontWeight.REGULAR)
            .drawCenteredString(
                Localization.ce("ui.themes.text"),
                vector2f.getX() + vector2f1.getX() - 20.0F,
                vector2f.getY() + 20.0F + this.azW.tE(),
                new Color(255, 255, 255, 128).getRGB()
            );

        for (int i = 0; i < this.colors.size(); i++) {
            ThemeKeyColorComponent abk = this.colors.get(i);
            abk.draw(vector2f.getX() + d4 + 7.0 + (7.0 + d7) * (i % 5), d5 + Math.floor(i / 5.0) * 24.0, d7, this.selectedColor != null && this.selectedColor.equals(abk));
            abk.getDimAnimation().Q(this.selectedColor != null && !this.selectedColor.equals(abk) ? 0.0 : 1.0);
            abk.getBloomAnimation().Q(Objects.equals(this.selectedColor, abk) ? 1.0 : 0.0);
        }

        for (int j = 0; j < this.visibleThemes.size(); j++) {
            ThemeComponent abjx = this.visibleThemes.get(j);
            abjx.getXAnimation().Q(vector2f.getX() + d4 + 7.0 + (7.0 + d6) * (j % 3));
            abjx.getYAnimation().Q(vector2f.getY() + 44.0F + Math.floor(j / 3.0) * 57.0 + 60.0);
        }

        for (ThemeComponent abj : this.allThemes) {
            if (abj.getOpacityAnimation().getValue() > 0.0) {
                abj.draw(this.azW.tE(), d6);
            }

            abj.getOpacityAnimation().Q(this.visibleThemes.contains(abj) ? 255.0 : 0.0);
        }
    }

    @Override
    public void f(int var1, int var2, int var3) {
        if (var3 <= 0) {
            for (ThemeComponent abj : this.visibleThemes) {
                if (GUIUtil.c(abj.pL().getX(), abj.pL().getY(), abj.pL().getZ(), 50.0, var1, var2)) {
                    Client.a.getThemeManager().a(abj.getActiveTheme());
                }
            }

            for (ThemeKeyColorComponent abk : this.colors) {
                if (GUIUtil.c(abk.pL().getX(), abk.pL().getY(), abk.pL().getZ(), 17.0, var1, var2)) {
                    if (this.selectedColor == abk) {
                        this.selectedColor = null;
                    } else {
                        this.selectedColor = abk;
                    }

                    this.qv();
                }
            }
        }
    }

    private void qv() {
        this.visibleThemes.clear();
        if (this.selectedColor == null) {
            this.visibleThemes = new ArrayList<>(this.allThemes);
            this.visibleThemes.forEach(var0 -> var0.getOpacityAnimation().Q(255.0));
        } else {
            for (ThemeComponent abj : this.allThemes) {
                if (abj.getActiveTheme().getKeyColors().contains(this.selectedColor.getColor())) {
                    this.visibleThemes.add(abj);
                    abj.getOpacityAnimation().Q(255.0);
                }
            }
        }
    }

    @Override
    public void aT() {
        this.allThemes.forEach(var0 -> var0.getOpacityAnimation().setValue(255.0));
        this.visibleThemes = new ArrayList<>(this.allThemes);
        this.selectedColor = null;
        this.azW.aX();
        this.qw();
    }

    public void qw() {
        Vector2f vector2f = this.getStandardClickGUI().getScale();
        Vector2f vector2f1 = this.getStandardClickGUI().getPosition();
        double d0 = this.getStandardClickGUI().getSidebar().aym;
        double d1 = (vector2f1.getX() - d0 - 29.0) / 3.0;

        for (int i = 0; i < this.visibleThemes.size(); i++) {
            ThemeComponent abj = this.visibleThemes.get(i);
            abj.getXAnimation().setValue(vector2f.getX() + d0 + 7.0 + (7.0 + d1) * (i % 3));
            abj.getYAnimation().setValue(vector2f.getY() + 44.0F + Math.floor(i / 3.0) * 57.0 + 60.0);
        }
    }
}
