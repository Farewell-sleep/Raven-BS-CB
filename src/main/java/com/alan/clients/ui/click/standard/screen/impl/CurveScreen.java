package com.alan.clients.ui.click.standard.screen.impl;

import com.alan.clients.ui.click.standard.RiseClickGUI;
import com.alan.clients.ui.click.standard.UIColors;
import com.alan.clients.ui.click.standard.components.CurveRenderer;
import com.alan.clients.ui.click.standard.components.value.ValueComponent;
import com.alan.clients.ui.click.standard.screen.Screen;
import com.alan.clients.util.gui.textbox.TextBox;
import com.alan.clients.util.interfaces.InstanceAccess;
import com.alan.clients.util.render.RenderUtil;
import com.alan.clients.util.vector.Vector2d;
import com.alan.clients.util.vector.Vector2f;
import java.lang.reflect.Field;
import java.util.ArrayList;
import java.util.Iterator;
import lombok.Generated;

public final class CurveScreen implements Screen, InstanceAccess {
    private RiseClickGUI H;
    private CurveRenderer azx = new CurveRenderer(new ArrayList<>(), new Vector2f(0.0F, 0.0F), this.rz().rA());
    private CurveRenderer azy = new CurveRenderer(new ArrayList<>(), new Vector2f(0.0F, 0.0F), this.rz().rA());
    private float azz = 10.0F;
    public ArrayList<ValueComponent> cj = new ArrayList<>();

    public CurveScreen() {
    }

    @Override
    public void aT() {
    }

    @Override
    public void onRender(int var1, int var2, float var3) {
        Vector2f vector2f = new Vector2f(this.H.getScale()).h(this.azz, 0.0F);
        RenderUtil.roundedRectangle(vector2f.getX(), vector2f.getY() + this.azz, this.H.position.x - this.azz * 2.0F, 100.0, this.H.getRound(), UIColors.SECONDARY.pV());
        this.azx.setColor(this.rz().rA());
        this.azx.i(new Vector2f(vector2f.h(this.azz, 0.0F)));
        this.azx.qx();
        this.azy.setColor(this.rz().rB());
        this.azy.i(new Vector2f(vector2f.h(this.azz, 0.0F)));
        this.azy.qx();
        Vector2f vector2f1 = vector2f.h(0.0F, 100.0F);
        Vector2f vector2f2 = vector2f1.h(0.0F, this.azz * 2.0F);
        RenderUtil.roundedRectangle(
            vector2f2.getX(),
            vector2f2.getY(),
            this.H.position.x - this.azz * 2.0F,
            this.H.getPosition().getY() - (vector2f2.getY() - this.H.getScale().getY()) - this.azz,
            this.H.getRound(),
            UIColors.SECONDARY.pV()
        );
        Vector2f vector2f3 = vector2f2.h(0.0F, this.azz);

        for (ValueComponent valueComponent : this.pA()) {
            if (valueComponent.getValue() == null || valueComponent.getValue().getHideIf() == null || !valueComponent.getValue().getHideIf().getAsBoolean()) {
                valueComponent.U(200);
                valueComponent.draw(new Vector2d(vector2f3.x + 1.0F + this.azz + (valueComponent.getValue().getHideIf() == null ? 0 : 10), vector2f3.y), var1, var2, var3);
                vector2f3 = vector2f3.h(0.0F, (float)valueComponent.getHeight());
            }
        }
    }

    @Override
    public void onKey(char var1, int var2) {
        Iterator iterator = this.pA().iterator();

        while (iterator.hasNext()) {
            ((ValueComponent)iterator.next()).key(var1, var2);
        }
    }

    @Override
    public void f(int var1, int var2, int var3) {
        for (ValueComponent valueComponent : this.pA()) {
            if ((valueComponent.getValue() == null || valueComponent.getValue().getHideIf() == null || !valueComponent.getValue().getHideIf().getAsBoolean()) && valueComponent.e(var1, var2, var3)) {
                break;
            }
        }
    }

    @Override
    public void oG() {
        Iterator iterator = this.pA().iterator();

        while (iterator.hasNext()) {
            ((ValueComponent)iterator.next()).pz();
        }
    }

    @Override
    public void pY() {
        new Vector2f(this.H.getScale());
        this.azx.pY();
        this.azy.pY();
    }

    @Override
    public boolean qa() {
        return true;
    }

    @Override
    public boolean pZ() {
        return this.cj.stream().noneMatch(var0 -> {
            for (Field field : var0.getClass().getDeclaredFields()) {
                if (field.getType().equals(TextBox.class)) {
                    try {
                        return ((TextBox)field.get(var0)).isSelected();
                    } catch (IllegalAccessException illegalaccessexception) {
                    }
                }
            }

            return false;
        });
    }

    @Generated
    @Override
    public RiseClickGUI getStandardClickGUI() {
        return this.H;
    }

    @Generated
    public CurveRenderer qb() {
        return this.azx;
    }

    @Generated
    public CurveRenderer qc() {
        return this.azy;
    }

    @Generated
    public float qd() {
        return this.azz;
    }

    @Generated
    public ArrayList<ValueComponent> pA() {
        return this.cj;
    }

    @Generated
    public void a(RiseClickGUI riseClickGUI) {
        this.H = riseClickGUI;
    }

    @Generated
    public void a(CurveRenderer var1) {
        this.azx = var1;
    }

    @Generated
    public void b(CurveRenderer var1) {
        this.azy = var1;
    }

    @Generated
    public void v(float var1) {
        this.azz = var1;
    }

    @Generated
    public void a(ArrayList<ValueComponent> valueComponents) {
        this.cj = valueComponents;
    }
}
