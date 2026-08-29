package com.alan.clients.util.gui;

import com.alan.clients.util.interfaces.InstanceAccess;
import com.alan.clients.util.math.MathUtil;
import com.alan.clients.util.render.ColorUtil;
import com.alan.clients.util.render.RenderUtil;
import com.alan.clients.util.vector.Vector2d;
import java.awt.Color;
import lombok.Generated;
import org.lwjgl.input.Mouse;
import rip.vantage.commons.util.time.StopWatch;

public class ScrollUtil implements InstanceAccess {
    public double aJc;
    public double scroll;
    public double aJd = 25.0;
    public StopWatch rG = new StopWatch();
    public StopWatch aJe = new StopWatch();
    public boolean aJf;
    public boolean dj;
    public boolean aJg;
    private boolean RE;

    public ScrollUtil() {
    }

    public void qx() {
        this.E(true);
    }

    public void E(boolean var1) {
        if (this.aJe.T(50L)) {
            float f = var1 ? Mouse.getDWheel() * (this.RE ? -1 : 1) : 0.0F;
            double d0 = 30.0;
            this.dj = f != 0.0F;
            this.aJc = Math.min(Math.max(this.aJc + f / 2.0F, this.aJd - (f == 0.0F ? 0.0 : d0)), f == 0.0F ? 0.0 : d0);
            this.aJe.aX();
        }

        for (int i = 0; i < this.rG.getElapsedTime(); i++) {
            this.scroll = MathUtil.m(this.scroll, this.aJc, 0.01F);
        }

        this.aJg = Math.abs(this.scroll - this.aJc) > 0.5;
        this.rG.aX();
    }

    public void a(Vector2d vector2d, double var2) {
        double d0 = (this.RE ? this.tF() - this.tE() : this.tE()) / this.tF();
        double d1 = var2 - this.tF() / (this.tF() - var2) * var2;
        this.aJf = d1 < var2;
        if (this.aJf) {
            double d2 = vector2d.x;
            double d3 = vector2d.y + var2 * d0 - d1 * d0;
            Color color = ColorUtil.withAlpha(Color.WHITE, 60);
            RenderUtil.roundedRectangle(d2, d3, 1.0, d1, 0.5, color);
        }
    }

    public void aX() {
        this.scroll = 0.0;
        this.aJc = 0.0;
    }

    @Generated
    public double tD() {
        return this.aJc;
    }

    @Generated
    public double tE() {
        return this.scroll;
    }

    @Generated
    public double tF() {
        return this.aJd;
    }

    @Generated
    public StopWatch lN() {
        return this.rG;
    }

    @Generated
    public StopWatch tG() {
        return this.aJe;
    }

    @Generated
    public boolean tH() {
        return this.aJf;
    }

    @Generated
    public boolean bd() {
        return this.dj;
    }

    @Generated
    public boolean tI() {
        return this.aJg;
    }

    @Generated
    public boolean tJ() {
        return this.RE;
    }

    @Generated
    public void U(double var1) {
        this.aJc = var1;
    }

    @Generated
    public void setScroll(double scroll) {
        this.scroll = scroll;
    }

    @Generated
    public void V(double var1) {
        this.aJd = var1;
    }

    @Generated
    public void c(StopWatch var1) {
        this.rG = var1;
    }

    @Generated
    public void d(StopWatch var1) {
        this.aJe = var1;
    }

    @Generated
    public void F(boolean var1) {
        this.aJf = var1;
    }

    @Generated
    public void c(boolean var1) {
        this.dj = var1;
    }

    @Generated
    public void G(boolean var1) {
        this.aJg = var1;
    }

    @Generated
    public void H(boolean var1) {
        this.RE = var1;
    }
}
