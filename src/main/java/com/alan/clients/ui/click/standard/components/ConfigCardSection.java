package com.alan.clients.ui.click.standard.components;

import com.alan.clients.ui.click.standard.RiseClickGUI;
import com.alan.clients.ui.click.standard.screen.impl.ScriptScreen;
import com.alan.clients.util.font.FontManager;
import com.alan.clients.util.font.FontWeight;
import com.alan.clients.util.gui.GUIUtil;
import com.alan.clients.util.interfaces.InstanceAccess;
import com.alan.clients.util.vector.Vector2f;
import com.alan.clients.util.dragging.Mouse;
import com.alan.clients.util.gui.ScrollUtil;
import java.awt.Color;
import java.util.ArrayList;
import java.util.Collection;
import java.util.ConcurrentModificationException;
import java.util.Iterator;
import lombok.Generated;
import org.jetbrains.annotations.NotNull;

public class ConfigCardSection extends ArrayList<ConfigCard> implements InstanceAccess {
    private String name;
    private static float aAe = 10.0F;
    private Vector2f axI;
    private ScrollUtil scrollUtil = new ScrollUtil();

    public ConfigCardSection(int var1, String var2) {
        super(var1);
        this.name = var2;
    }

    public ConfigCardSection(String var1) {
        this.name = var1;
    }

    public ConfigCardSection(@NotNull Collection<? extends ConfigCard> var1, String var2) {
        super(var1);
        this.name = var2;
    }

    public void j(Vector2f vec2) {
        try {
            RiseClickGUI riseclickgui = this.getStandardClickGUI();
            this.axI = new Vector2f(vec2.x, vec2.y);
            FontManager.MAIN.a(18, FontWeight.REGULAR).a(this.name, this.axI.x, this.axI.y, Color.WHITE.getRGB());
            String s = this.size() + "";
            FontManager.MAIN.a(18, FontWeight.REGULAR).a(s, this.axI.x + FontManager.MAIN.a(18, FontWeight.REGULAR).getStringWidth(this.name) + 10 / 2.0F, this.axI.y, this.rz().rA().getRGB());
            if (!this.isEmpty()) {
                this.axI.y = this.axI.y + (10 + FontManager.MAIN.a(18, FontWeight.REGULAR).height());

                for (ConfigCard aci : this) {
                    if (!(this.axI.y > riseclickgui.getScale().y + riseclickgui.getPosition().y + 14.0) && !(this.axI.y + aci.oX().y < riseclickgui.axI.y)) {
                        aci.j(this.axI);
                    }

                    this.axI.y = this.axI.y + (10 + aci.oX().y);
                }
            }

            this.axI = new Vector2f(vec2.x, vec2.y);
        } catch (ConcurrentModificationException concurrentmodificationexception) {
            concurrentmodificationexception.printStackTrace();
        }
    }

    public int getHeight() {
        return (int)((this.isEmpty() ? 0.0F : this.size() * (this.get(0).oX().getY() + 10.0F)) + 10 + FontManager.MAIN.a(18, FontWeight.REGULAR).height());
    }

    public void f(int var1, int var2, int var3) {
        Iterator iterator = this.iterator();

        while (iterator.hasNext()) {
            ((ConfigCard)iterator.next()).f(var1, var2, var3);
        }
    }

    public void qF() {
        if (this.axI != null) {
            if (this.qz()) {
                ScriptScreen.azE = false;
            }
        }
    }

    public boolean qz() {
        return this.isEmpty()
            ? false
            : GUIUtil.a(this.getStandardClickGUI().axI, this.getStandardClickGUI().position, Mouse.getMouse())
                && GUIUtil.a(
                    new Vector2f((float)(this.getStandardClickGUI().axI.x + this.getStandardClickGUI().sidebar.aym), this.axI.y),
                    new Vector2f((float)(this.getStandardClickGUI().position.x - this.getStandardClickGUI().sidebar.aym), this.get(0).oX().y + 20 + 10.0F),
                    Mouse.getMouse()
                );
    }

    public void init() {
        this.getScrollUtil().setScroll(0.0);
        this.getScrollUtil().U(0.0);
    }

    @Generated
    public String getName() {
        return this.name;
    }

    @Generated
    public Vector2f oW() {
        return this.axI;
    }

    @Generated
    public ScrollUtil getScrollUtil() {
        return this.scrollUtil;
    }

    @Generated
    public void setName(String name) {
        this.name = name;
    }

    @Generated
    public void i(Vector2f vec2) {
        this.axI = vec2;
    }

    @Generated
    public void setScrollUtil(ScrollUtil scrollUtil) {
        this.scrollUtil = scrollUtil;
    }
}
