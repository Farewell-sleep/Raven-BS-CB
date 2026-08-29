package com.alan.clients.ui.click.standard.components;

import com.alan.clients.module.Module;
import com.alan.clients.ui.click.standard.RiseClickGUI;
import com.alan.clients.ui.click.standard.screen.impl.SearchScreen;
import com.alan.clients.util.animation.Animation;
import com.alan.clients.util.animation.Easing;
import com.alan.clients.util.interfaces.InstanceAccess;
import com.alan.clients.util.render.RenderUtil;
import com.alan.clients.util.vector.Vector2d;
import com.alan.clients.util.vector.Vector2f;
import com.alan.clients.value.Value;
import com.alan.clients.ui.click.standard.components.value.ValueComponent;
import com.alan.clients.ui.click.standard.UIColors;
import com.alan.clients.util.gui.GUIUtil;
import com.alan.clients.util.localization.Localization;
import com.alan.clients.util.render.ColorUtil;
import com.alan.clients.util.font.FontManager;
import com.alan.clients.util.font.FontWeight;
import com.alan.clients.ui.click.standard.components.value.impl.BoundsNumberValueComponent;
import com.alan.clients.ui.click.standard.components.value.impl.KeyBindComponent;
import com.alan.clients.ui.click.standard.components.value.impl.NumberValueComponent;
import java.awt.Color;
import java.util.ArrayList;
import java.util.Iterator;
import lombok.Generated;
import rip.vantage.commons.util.time.StopWatch;

public class ModuleComponent implements InstanceAccess {
    public Module module;
    public Vector2f scale = this.getStandardClickGUI().getModuleDefaultScale();
    public boolean expanded;
    public ArrayList<ValueComponent> valueList = new ArrayList<>();
    public Vector2d position;
    public double opacity;
    public StopWatch rG = new StopWatch();
    public Animation axx = new Animation(Easing.LINEAR, 50L);
    public Animation aye = new Animation(Easing.LINEAR, 200L);
    public Animation ayf = new Animation(Easing.LINEAR, 5000L);
    public boolean mouseDown;

    public ModuleComponent(Module module) {
        this.module = module;
        this.aye.setValue(this.scale.y);
        this.ayf.setValue(0.0);
        module.getAllValues().forEach(var1x -> {
            ValueComponent valueComponent = var1x.wl();
            if (valueComponent != null) {
                this.valueList.add(valueComponent);
            }
        });
        if (module instanceof com.alan.clients.bridge.RiseModuleWrapper) {
            keystrokesmod.module.Module bs = ((com.alan.clients.bridge.RiseModuleWrapper) module).bs();
            this.valueList.add(0, new KeyBindComponent(bs));
        }
    }

    public void draw(Vector2d position, int var2, int var3, float var4) {
        this.position = position;
        float f = 38.0F;
        float f1 = f;
        boolean flag = position != null
            && !(position.y + this.scale.y < this.getStandardClickGUI().axI.y)
            && !(position.y > this.getStandardClickGUI().axI.y + this.getStandardClickGUI().position.y);
        if (flag) {
            RiseClickGUI riseclickgui = this.getStandardClickGUI();
            RenderUtil.roundedRectangle(position.x, position.y, this.scale.x, this.scale.y, 6.0, UIColors.OVERLAY.pV());
            Color color = UIColors.TEXT.Y(this.module.isEnabled() ? 255 : 200);
            boolean flag1 = GUIUtil.c(position.x, position.y, this.scale.x, this.scale.y, var2, var3);
            this.axx.Q(flag1 ? (this.mouseDown ? 35.0 : 20.0) : 0.0);
            RenderUtil.roundedRectangle(position.x, position.y, this.scale.x, this.scale.y, 6.0, ColorUtil.withAlpha(Color.BLACK, (int)this.axx.getValue()));
            if (riseclickgui.pa() instanceof SearchScreen) {
                FontManager.MAIN
                    .a(15, FontWeight.REGULAR)
                    .a(
                        "(" + Localization.ce(this.module.getModuleInfo().category().getName()) + ")",
                        (float)(position.getX() + FontManager.MAIN.a(20, FontWeight.REGULAR).getStringWidth(this.module.getName()) + 10.0),
                        (float)position.getY() + 10.0F,
                        ColorUtil.withAlpha(color, 64).hashCode()
                    );
            }

            FontManager.MAIN
                .a(20, FontWeight.REGULAR)
                .a(
                    this.module.getName(),
                    (float)position.x + 6.0F,
                    (float)position.y + 8.0F,
                    this.module.isEnabled() ? this.rz().getAccentColor(new Vector2d(0.0, position.y / 5.0)).getRGB() : color.getRGB()
                );
            FontManager.MAIN.a(15, FontWeight.REGULAR).a(Localization.ce(this.module.getModuleInfo().description()), (float)position.x + 6.0F, (float)position.y + 25.0F, ColorUtil.withAlpha(color, 70).hashCode());
            this.scale = new Vector2f(this.getStandardClickGUI().moduleDefaultScale.x, f1);
        }

        if (!this.aye.isFinished() || this.expanded) {
            for (ValueComponent valueComponent : this.getValueList()) {
                Value<?> value = valueComponent.getValue();
                if (value != null && (value.getHideIf() != null && value.getHideIf().getAsBoolean() || value.getBooleanSupplier() != null && value.getBooleanSupplier().getAsBoolean())) {
                    continue;
                }
                valueComponent.U(valueComponent.position == null ? 0 : (valueComponent.position.y < position.y + this.aye.getValue() + 15.0 ? (int)this.ayf.getValue() : 0));
                valueComponent.U(value == null || value.getHideIf() == null ? valueComponent.pT() : Math.max(valueComponent.pT() - 40, 0));
                if (flag) {
                    valueComponent.draw(
                        new Vector2d(position.x + 6.0 + (value == null || value.getHideIf() == null ? 0 : 10) + (value == null || value.getBooleanSupplier() == null ? 0 : 10), (float)(position.y + f1 + 1.0)),
                        var2,
                        var3,
                        var4
                    );
                }

                f1 = (float)(f1 + valueComponent.getHeight());
            }

            f1--;
        }

        this.aye.setDuration(Math.min((long)f1 * 3L, 450L));
        this.aye.setEasing(Easing.EASE_OUT_EXPO);
        this.aye.Q(this.expanded ? f1 : f);
        this.scale.y = (float)this.aye.getValue();
        this.ayf.setDuration(this.expanded ? this.aye.getDuration() / 2L : this.aye.getDuration() / 3L);
        this.ayf.Q(this.expanded ? 255.0 : 0.0);
    }

    public void key(char var1, int var2) {
        if (this.position != null
            && !(this.position.y + this.scale.y < this.getStandardClickGUI().axI.y)
            && !(this.position.y > this.getStandardClickGUI().axI.y + this.getStandardClickGUI().position.y)) {
            if (this.isExpanded()) {
                Iterator iterator = this.getValueList().iterator();

                while (iterator.hasNext()) {
                    ((ValueComponent)iterator.next()).key(var1, var2);
                }
            }
        }
    }

    public void click(int var1, int var2, int var3) {
        if (this.position != null
            && !(this.position.y + this.scale.y < this.getStandardClickGUI().axI.y)
            && !(this.position.y > this.getStandardClickGUI().axI.y + this.getStandardClickGUI().position.y)) {
            Vector2f vector2f = this.getStandardClickGUI().axI;
            Vector2f vector2f1 = this.getStandardClickGUI().position;
            boolean flag = var3 == 0;
            boolean flag1 = var3 == 1;
            boolean flag2 = GUIUtil.c(vector2f.x, vector2f.y, vector2f1.x, vector2f1.y, var1, var2);
            if (GUIUtil.c(this.position.x, this.position.y, this.scale.x, this.getStandardClickGUI().moduleDefaultScale.getY() - 3.0F, var1, var2) && this.getStandardClickGUI().overlayPresent == null) {
                this.mouseDown = true;
                double d0 = 0.0;

                for (ValueComponent ablxx : this.valueList) {
                    d0 += ablxx.getHeight();
                }

                if (flag) {
                    if (flag2) {
                        this.module.toggle();
                    }
                } else if (flag1 && d0 != 0.0) {
                    this.expanded = !this.expanded;

                    for (ValueComponent valueComponent : this.getValueList()) {
                        if (valueComponent instanceof BoundsNumberValueComponent) {
                            BoundsNumberValueComponent abn = (BoundsNumberValueComponent) valueComponent;
                            abn.ayI = abn.ayJ = false;
                        } else if (valueComponent instanceof NumberValueComponent) {
                            ((NumberValueComponent)valueComponent).azi = false;
                        }
                    }
                }
            }

            if (this.isExpanded()) {
                for (ValueComponent ablx : this.getValueList()) {
                    Value<?> value = ablx.getValue();
                    if (value != null
                        && (value.getHideIf() != null && value.getHideIf().getAsBoolean() || value.getBooleanSupplier() != null && value.getBooleanSupplier().getAsBoolean())) {
                        continue;
                    }
                    if (ablx.e(var1, var2, var3)) {
                        break;
                    }
                }
            }
        }
    }

    public void ci() {
        if (this.position != null) {
            if (!(this.position.y + this.scale.y < this.getStandardClickGUI().axI.y)
                && !(this.position.y > this.getStandardClickGUI().axI.y + this.getStandardClickGUI().position.y)) {
                if (!this.aye.isFinished() || this.expanded) {
                    for (ValueComponent valueComponent : this.getValueList()) {
                        if (valueComponent.getValue() != null
                            && (valueComponent.getValue().getHideIf() == null || !valueComponent.getValue().getHideIf().getAsBoolean())
                            && (valueComponent.getValue().getBooleanSupplier() == null || !valueComponent.getValue().getBooleanSupplier().getAsBoolean())) {
                            valueComponent.released();
                        }
                    }
                }
            }
        }
    }

    public void pz() {
        this.mouseDown = false;
        if (this.isExpanded()) {
            Iterator iterator = this.getValueList().iterator();

            while (iterator.hasNext()) {
                ((ValueComponent)iterator.next()).pz();
            }
        }
    }

    @Generated
    public Module getModule() {
        return this.module;
    }

    @Generated
    public Vector2f getScale() {
        return this.scale;
    }

    @Generated
    public boolean isExpanded() {
        return this.expanded;
    }

    @Generated
    public ArrayList<ValueComponent> getValueList() {
        return this.valueList;
    }

    @Generated
    public Vector2d getPosition() {
        return this.position;
    }

    @Generated
    public double getOpacity() {
        return this.opacity;
    }

    @Generated
    public StopWatch lN() {
        return this.rG;
    }

    @Generated
    public Animation oL() {
        return this.axx;
    }

    @Generated
    public Animation pB() {
        return this.aye;
    }

    @Generated
    public Animation pC() {
        return this.ayf;
    }

    @Generated
    public boolean isMouseDown() {
        return this.mouseDown;
    }
}
