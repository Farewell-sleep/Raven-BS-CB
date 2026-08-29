package com.alan.clients.ui.click.standard.screen.impl;

import com.alan.clients.Client;
import com.alan.clients.module.api.Category;
import com.alan.clients.ui.click.standard.RiseClickGUI;
import com.alan.clients.ui.click.standard.components.ModuleComponent;
import com.alan.clients.ui.click.standard.screen.Screen;
import com.alan.clients.util.interfaces.InstanceAccess;
import com.alan.clients.util.vector.Vector2d;
import com.alan.clients.util.gui.ScrollUtil;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.stream.Collectors;
import lombok.Generated;
import rip.vantage.commons.util.time.StopWatch;

public final class CategoryScreen implements Screen, InstanceAccess {
    private final StopWatch azA = new StopWatch();
    public ScrollUtil scrollUtil = new ScrollUtil();
    public ArrayList<ModuleComponent> relevantModules;
    public Category category;
    private double azC;
    private double azD;

    public CategoryScreen() {
    }

    @Override
    public void onRender(int var1, int var2, float var3) {
        if (this.category != null) {
            RiseClickGUI riseclickgui = this.getStandardClickGUI();
            this.scrollUtil.qx();
            double d0 = riseclickgui.axI.y + 7.0F + this.scrollUtil.tE();
            this.azD = d0;
            double d1 = 0.0;

            for (ModuleComponent moduleComponent : this.relevantModules) {
                moduleComponent.draw(new Vector2d(riseclickgui.axI.x + riseclickgui.sidebar.aym + 8.0, d0), var1, var2, var3);
                d0 += moduleComponent.scale.y + 7.0F;
                d1 += moduleComponent.scale.y + 7.0F;
            }

            this.azC = d0;
            double d2 = 7.0;
            double d3 = riseclickgui.getScale().getX() + riseclickgui.getPosition().getX() - 4.0F;
            double d4 = riseclickgui.getScale().getY() + d2;
            this.scrollUtil.a(new Vector2d(d3, d4), this.getStandardClickGUI().position.y - d2 * 2.0);
            this.scrollUtil.V(-d1 + riseclickgui.position.y - 7.0);
            this.azA.aX();
        }
    }

    @Override
    public void onKey(char var1, int var2) {
        Iterator iterator = this.getRelevantModules().iterator();

        while (iterator.hasNext()) {
            ((ModuleComponent)iterator.next()).key(var1, var2);
        }
    }

    @Override
    public void f(int var1, int var2, int var3) {
        if (this.relevantModules != null) {
            Iterator iterator = this.relevantModules.iterator();

            while (iterator.hasNext()) {
                ((ModuleComponent)iterator.next()).click(var1, var2, var3);
            }
        }
    }

    @Override
    public void oG() {
        if (this.category != null) {
            Iterator iterator = this.getRelevantModules().iterator();

            while (iterator.hasNext()) {
                ((ModuleComponent)iterator.next()).pz();
            }
        }
    }

    @Override
    public void pY() {
        if (this.category != null) {
            Iterator iterator = this.getRelevantModules().iterator();

            while (iterator.hasNext()) {
                ((ModuleComponent)iterator.next()).ci();
            }
        }
    }

    @Override
    public void aT() {
        this.category = this.oH();
        if (this.category != null) {
            this.relevantModules = Client.a
                .getStandardClickGUI()
                .getModuleList()
                .stream()
                .filter(var1 -> var1.getModule().getModuleInfo().category() == this.category)
                .collect(Collectors.toCollection(ArrayList::new));
        }
    }

    private Category oH() {
        for (Category category : Category.values()) {
            if (category.getClickGUIScreen() == this.getStandardClickGUI().getScreen()) {
                return category;
            }
        }

        return null;
    }

    @Generated
    public StopWatch lN() {
        return this.azA;
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
    public double qg() {
        return this.azC;
    }

    @Generated
    public double qh() {
        return this.azD;
    }

    @Generated
    public void setScrollUtil(ScrollUtil scrollUtil) {
        this.scrollUtil = scrollUtil;
    }

    @Generated
    public void b(ArrayList<ModuleComponent> moduleComponents) {
        this.relevantModules = moduleComponents;
    }

    @Generated
    public void b(Category category) {
        this.category = category;
    }

    @Generated
    public void H(double var1) {
        this.azC = var1;
    }

    @Generated
    public void I(double var1) {
        this.azD = var1;
    }
}
