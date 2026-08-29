package com.alan.clients.ui.click.standard.components;

import com.alan.clients.util.render.ColorUtil;
import com.alan.clients.util.render.RenderUtil;
import com.alan.clients.util.vector.Vector2f;
import java.awt.Color;
import java.util.ArrayList;
import lombok.Generated;
import org.lwjgl.opengl.GL11;

public class CurveRenderer {
    private ArrayList<float[]> aze;
    private Vector2f axI;
    private Color amH;

    public void qx() {
        GL11.glPushMatrix();

        for (int i = 1; i <= 5; i++) {
            GL11.glLineWidth(i);
            RenderUtil.rt();
            RenderUtil.color(ColorUtil.withAlpha(this.amH, (int)(this.amH.getAlpha() / i / 1.2)));
            GL11.glBegin(3);

            for (float[] afloat : this.aze) {
                GL11.glVertex2d(this.axI.getX() + afloat[0], this.axI.getY() + afloat[1]);
            }

            GL11.glEnd();
            RenderUtil.stop();
        }

        double d0 = 5.5;

        for (float[] afloat1 : this.aze) {
            RenderUtil.roundedRectangle(this.axI.getX() + afloat1[0] - d0 / 2.0, this.axI.getY() + afloat1[1] - d0 / 2.0, d0, d0, d0 / 2.0, this.amH);
        }

        GL11.glPopMatrix();
    }

    public void pY() {
        GL11.glPushMatrix();
        double d0 = 5.5;

        for (float[] afloat : this.aze) {
            RenderUtil.roundedRectangle(this.axI.getX() + afloat[0] - d0 / 2.0, this.axI.getY() + afloat[1] - d0 / 2.0, d0, d0, d0 / 2.0, this.amH);
        }

        GL11.glPopMatrix();
    }

    @Generated
    public void d(ArrayList<float[]> var1) {
        this.aze = var1;
    }

    @Generated
    public void i(Vector2f vec2) {
        this.axI = vec2;
    }

    @Generated
    public void setColor(Color color) {
        this.amH = color;
    }

    @Generated
    public ArrayList<float[]> qy() {
        return this.aze;
    }

    @Generated
    public Vector2f oW() {
        return this.axI;
    }

    @Generated
    public Color getColor() {
        return this.amH;
    }

    @Generated
    public CurveRenderer(ArrayList<float[]> var1, Vector2f vec2, Color color) {
        this.aze = var1;
        this.axI = vec2;
        this.amH = color;
    }
}
