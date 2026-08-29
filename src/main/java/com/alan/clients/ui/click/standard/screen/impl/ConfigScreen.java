package com.alan.clients.ui.click.standard.screen.impl;

import com.alan.clients.Client;
import com.alan.clients.ui.click.standard.RiseClickGUI;
import com.alan.clients.ui.click.standard.components.ModuleComponent;
import com.alan.clients.ui.click.standard.components.value.ValueComponent;
import com.alan.clients.ui.click.standard.screen.Screen;
import com.alan.clients.util.font.FontManager;
import com.alan.clients.util.gui.textbox.TextAlign;
import com.alan.clients.util.gui.textbox.TextBox;
import com.alan.clients.util.interfaces.InstanceAccess;
import com.alan.clients.util.vector.Vector2d;
import com.alan.clients.ui.click.standard.UIColors;
import com.alan.clients.util.gui.ScrollUtil;
import com.alan.clients.util.font.FontWeight;
import com.alan.clients.ui.click.standard.components.value.impl.StringValueComponent;
import java.awt.Color;
import java.util.ArrayList;
import java.util.Iterator;
import lombok.Generated;
import rip.vantage.commons.util.time.StopWatch;

public final class ConfigScreen implements Screen, InstanceAccess {
    public final TextBox azM = new TextBox(new Vector2d(200.0, 200.0), FontManager.MAIN.a(20, FontWeight.REGULAR), Color.WHITE, TextAlign.CENTER, "Start typing to search...", 150.0F);
    private final StopWatch azN = new StopWatch();
    public ScrollUtil scrollUtil = new ScrollUtil();
    public ArrayList<ModuleComponent> relevantModules = new ArrayList<>();
    private double axT = 255.0;
    private double azC;
    private double azD;
    private boolean typedWhileOpen;

    public ConfigScreen() {
    }

    @Override
    public void onRender(int var1, int var2, float var3) {
        RiseClickGUI riseclickgui = this.getStandardClickGUI();
        if (this.scrollUtil.tD() < 0.0) {
            this.axT = this.axT - this.azN.getElapsedTime() * 4L;
        } else {
            this.axT = this.axT + this.azN.getElapsedTime() * 4L;
        }

        this.axT = Math.min(Math.max(0.0, this.axT), 255.0);
        this.azM.setColor(UIColors.TEXT.Y((int)this.axT));
        Vector2d vector2d = new Vector2d(
            riseclickgui.axI.x + riseclickgui.sidebar.aym + (riseclickgui.position.x - riseclickgui.sidebar.aym) / 2.0,
            (float)(riseclickgui.axI.y + 17.0F + this.scrollUtil.tE())
        );
        this.azM.setPosition(vector2d);
        this.azM.draw();
        this.scrollUtil.qx();
        double d0 = riseclickgui.axI.y + 35.0F + this.scrollUtil.tE();
        this.azD = d0;
        double d1 = 0.0;

        for (ModuleComponent moduleComponent : this.relevantModules) {
            moduleComponent.draw(new Vector2d(riseclickgui.axI.x + riseclickgui.sidebar.aym + 4.0, d0), var1, var2, var3);
            d0 += moduleComponent.scale.y + 5.0F;
            d1 += moduleComponent.scale.y + 5.0F;
        }

        this.azC = d0;
        this.scrollUtil.V(-d1 + riseclickgui.position.y - 37.0);
        this.azN.aX();
    }

    @Override
    public void onKey(char var1, int var2) {
        if (!this.typedWhileOpen && var1 != 0 && !Character.isISOControl(var1)) {
            this.typedWhileOpen = true;
            this.aH(" ");
        }

        if (!this.oV()) {
            this.azM.setSelected(true);
        }

        this.azM.key(var1, var2);
        this.scrollUtil.U(0.0);
        this.relevantModules = this.getRelevantModules(this.azM.getText());
        Iterator iterator = this.getRelevantModules().iterator();

        while (iterator.hasNext()) {
            ((ModuleComponent)iterator.next()).key(var1, var2);
        }
    }

    @Override
    public void f(int var1, int var2, int var3) {
        Iterator iterator = this.relevantModules.iterator();

        while (iterator.hasNext()) {
            ((ModuleComponent)iterator.next()).click(var1, var2, var3);
        }

        this.azM.click(var1, var2, var3);
    }

    @Override
    public void oG() {
        Iterator iterator = this.getRelevantModules().iterator();

        while (iterator.hasNext()) {
            ((ModuleComponent)iterator.next()).pz();
        }
    }

    @Override
    public void pY() {
        Iterator iterator = this.getRelevantModules().iterator();

        while (iterator.hasNext()) {
            ((ModuleComponent)iterator.next()).ci();
        }
    }

    @Override
    public void aT() {
        this.relevantModules = this.getRelevantModules(this.azM.getText());
        this.typedWhileOpen = false;
    }

    public ArrayList<ModuleComponent> getRelevantModules(String var1) {
        ArrayList arraylist = new ArrayList();

        for (ModuleComponent moduleComponent : Client.a.getStandardClickGUI().getModuleList()) {
            String[] astring = moduleComponent.getModule().getAliases();
            int i = astring.length;

            for (int j = 0; j < i; j++) {
                if (astring[j].toLowerCase().replaceAll(" ", "").contains(var1.toLowerCase().replaceAll(" ", ""))) {
                    arraylist.add(moduleComponent);
                    break;
                }
            }
        }

        return arraylist;
    }

    public void aH(String var1) {
        this.azM.bW(var1);
        this.relevantModules = this.getRelevantModules(this.azM.getText());
    }

    public boolean oV() {
        Iterator iterator = this.relevantModules.iterator();

        while (iterator.hasNext()) {
            for (ValueComponent valueComponent : ((ModuleComponent)iterator.next()).getValueList()) {
                if (valueComponent instanceof StringValueComponent && ((StringValueComponent)valueComponent).azo.selected) {
                    return true;
                }
            }
        }

        return false;
    }

    @Generated
    public TextBox getSearchBar() {
        return this.azM;
    }

    @Generated
    public StopWatch lN() {
        return this.azN;
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
    public void setModuleComponents(ArrayList<ModuleComponent> moduleComponents) {
        this.relevantModules = moduleComponents;
    }

    @Generated
    public void J(double var1) {
        this.axT = var1;
    }

    @Generated
    public void H(double var1) {
        this.azC = var1;
    }

    @Generated
    public void I(double var1) {
        this.azD = var1;
    }

    @Generated
    public void setTypedWhileOpen(boolean var1) {
        this.typedWhileOpen = var1;
    }
}
