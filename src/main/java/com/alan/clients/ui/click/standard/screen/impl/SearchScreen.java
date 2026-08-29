package com.alan.clients.ui.click.standard.screen.impl;

import com.alan.clients.Client;
import com.alan.clients.ui.click.standard.RiseClickGUI;
import com.alan.clients.util.interfaces.InstanceAccess;
import com.alan.clients.util.vector.Vector2d;
import com.alan.clients.ui.click.standard.components.ModuleComponent;
import com.alan.clients.ui.click.standard.UIColors;
import com.alan.clients.ui.click.standard.screen.Screen;
import com.alan.clients.util.gui.ScrollUtil;
import com.alan.clients.util.gui.textbox.TextAlign;
import com.alan.clients.util.gui.textbox.TextBox;
import com.alan.clients.util.localization.Localization;
import com.alan.clients.util.render.ColorUtil;
import com.alan.clients.util.font.FontManager;
import com.alan.clients.util.font.FontWeight;
import java.awt.Color;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Iterator;
import lombok.Generated;
import rip.vantage.commons.util.time.StopWatch;

public final class SearchScreen
implements Screen,
InstanceAccess {
    public final TextBox azR = new TextBox(new Vector2d(200.0, 200.0), FontManager.MAIN.a(20, FontWeight.REGULAR), Color.WHITE, TextAlign.CENTER, Localization.ce("ui.search.text"), 150.0f);
    private final StopWatch azS = new StopWatch();
    public ScrollUtil scrollUtil = new ScrollUtil();
    public ArrayList<ModuleComponent> relevantModules = new ArrayList();
    private double axT = 255.0;
    private double azC;
    private double azD;
    private boolean typedWhileOpen;

    @Override
    public void onRender(int n2, int n3, float f2) {
        double d2;
        RiseClickGUI riseClickGUI = this.getStandardClickGUI();
        this.axT = this.scrollUtil.tD() < 0.0 ? (this.axT -= (double)(this.azS.getElapsedTime() * 4L)) : (this.axT += (double)(this.azS.getElapsedTime() * 4L));
        this.axT = Math.min(Math.max(0.0, this.axT), 255.0);
        this.azR.setColor(ColorUtil.withAlpha(UIColors.TEXT.pV(), (int)this.axT));
        Vector2d vector2d = new Vector2d((double)riseClickGUI.axI.x + riseClickGUI.sidebar.aym + ((double)riseClickGUI.position.x - riseClickGUI.sidebar.aym) / 2.0, (float)((double)(riseClickGUI.axI.y + 17.0f) + this.scrollUtil.tE()));
        this.azR.setPosition(vector2d);
        String string = this.azR.aJm;
        if (riseClickGUI.a(this.azR)) {
            this.azR.aJm = "";
        }
        this.azR.draw();
        this.azR.aJm = string;
        this.scrollUtil.qx();
        this.azD = d2 = (double)(riseClickGUI.axI.y + 35.0f) + this.scrollUtil.tE();
        double d3 = 0.0;
        Iterator<ModuleComponent> iterator = this.relevantModules.iterator();
        while (true) {
            if (!iterator.hasNext()) {
                this.azC = d2;
                this.scrollUtil.V(-d3 + (double)riseClickGUI.position.y - 37.0);
                double d4 = 7.0;
                double d5 = riseClickGUI.getScale().getX() + riseClickGUI.getPosition().getX() - 4.0f;
                double d6 = (double)riseClickGUI.getScale().getY() + d4;
                this.scrollUtil.a(new Vector2d(d5, d6 + 28.0), (double)this.getStandardClickGUI().position.y - d4 * 2.0 - 28.0);
                this.azS.aX();
                return;
            }
            ModuleComponent moduleComponent = iterator.next();
            moduleComponent.draw(new Vector2d((double)riseClickGUI.axI.x + riseClickGUI.sidebar.aym + 8.0, d2), n2, n3, f2);
            d2 += (double)(moduleComponent.scale.y + 7.0f);
            d3 += (double)(moduleComponent.scale.y + 7.0f);
        }
    }

    @Override
    public void onKey(char c2, int n2) {
        if (n2 == 208 || n2 == 200) {
            return;
        }
        if (!(this.typedWhileOpen || this.getStandardClickGUI().oV() || c2 == '\u0000' || Character.isISOControl(c2))) {
            this.typedWhileOpen = true;
            this.setSearchBarText("");
        }
        if (!this.getStandardClickGUI().oV()) {
            this.azR.setSelected(true);
            this.azR.key(c2, n2);
            this.scrollUtil.U(0.0);
        }
        this.relevantModules = this.getRelevantModules(this.azR.getText());
        Iterator<ModuleComponent> iterator = this.getRelevantModules().iterator();
        while (iterator.hasNext()) {
            iterator.next().key(c2, n2);
        }
    }

    @Override
    public void f(int n2, int n3, int n4) {
        Iterator<ModuleComponent> iterator = this.relevantModules.iterator();
        while (iterator.hasNext()) {
            iterator.next().click(n2, n3, n4);
        }
        this.azR.click(n2, n3, n4);
    }

    @Override
    public void oG() {
        Iterator<ModuleComponent> iterator = this.getRelevantModules().iterator();
        while (iterator.hasNext()) {
            iterator.next().pz();
        }
    }

    @Override
    public void pY() {
        Iterator<ModuleComponent> iterator = this.getRelevantModules().iterator();
        while (iterator.hasNext()) {
            iterator.next().ci();
        }
    }

    @Override
    public void aT() {
        this.relevantModules = this.getRelevantModules(this.azR.getText());
        this.typedWhileOpen = false;
    }

    public ArrayList<ModuleComponent> getRelevantModules(String string) {
        ArrayList<ModuleComponent> arrayList = new ArrayList<ModuleComponent>();
        ArrayList<String> arrayList2 = new ArrayList<String>(Arrays.asList(string.toLowerCase().split(" ")));
        arrayList2.add(string.toLowerCase().replaceAll(" ", ""));
        for (String string2 : arrayList2) {
            for (ModuleComponent moduleComponent : Client.a.getStandardClickGUI().getModuleList()) {
                String[] stringArray = moduleComponent.getModule().getAliases();
                int length = stringArray.length;
                for (int i2 = 0; i2 < length; ++i2) {
                    if (!stringArray[i2].toLowerCase().replaceAll(" ", "").contains(string2) || arrayList.contains(moduleComponent)) continue;
                    arrayList.add(moduleComponent);
                }
            }
        }
        return arrayList;
    }

    public void setSearchBarText(String string) {
        this.azR.bW(string);
        this.relevantModules = this.getRelevantModules(this.azR.getText());
    }

    @Generated
    public TextBox getSearchBar() {
        return this.azR;
    }

    @Generated
    public StopWatch lN() {
        return this.azS;
    }

    @Generated
    public ScrollUtil getScrollUtil() {
        return this.scrollUtil;
    }

    @Generated
    public ArrayList<ModuleComponent> getRelevantModules() {
        return this.relevantModules;
    }

    @Generated
    public double pj() {
        return this.axT;
    }

    @Generated
    public double qg() {
        return this.azC;
    }

    @Generated
    public double qh() {
        return this.azD;
    }

    @Generated
    public boolean isTypedWhileOpen() {
        return this.typedWhileOpen;
    }

    @Generated
    public void setScrollUtil(ScrollUtil scrollUtil) {
        this.scrollUtil = scrollUtil;
    }

    @Generated
    public void setRelevantModules(ArrayList<ModuleComponent> arrayList) {
        this.relevantModules = arrayList;
    }

    @Generated
    public void J(double d2) {
        this.axT = d2;
    }

    @Generated
    public void H(double d2) {
        this.azC = d2;
    }

    @Generated
    public void I(double d2) {
        this.azD = d2;
    }

    @Generated
    public void setTypedWhileOpen(boolean typedWhileOpen) {
        this.typedWhileOpen = typedWhileOpen;
    }
}
