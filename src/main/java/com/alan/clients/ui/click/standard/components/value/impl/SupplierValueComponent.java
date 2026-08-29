package com.alan.clients.ui.click.standard.components.value.impl;

import com.alan.clients.util.render.RenderUtil;
import com.alan.clients.util.vector.Vector2d;
import com.alan.clients.value.Value;
import com.alan.clients.ui.click.standard.components.value.ValueComponent;
import com.alan.clients.util.MouseUtil;
import com.alan.clients.util.render.ColorUtil;
import com.alan.clients.util.math.MathOperation;
import java.awt.Color;
import java.util.ArrayList;

public class SupplierValueComponent
extends ValueComponent {
    private final ArrayList<Vector2d> aze = new ArrayList();
    private Vector2d YM = null;

    public SupplierValueComponent(Value<?> value) {
        super(value);
        this.aze.clear();
        for (int i2 = 0; i2 <= 250; i2 += 10) {
            this.aze.add(new Vector2d(i2, 45.0));
        }
    }

    @Override
    public void draw(Vector2d vector2d, int n2, int n3, float f2) {
        this.position = vector2d;
        this.position.setX(this.position.getX() + 2.5);
        if (this.YM != null) {
            this.YM.setY((double)n3 - vector2d.getY());
            this.YM.setX(Math.min(Math.max(0.0, this.YM.getX()), this.height * 5.0));
            this.YM.setY(Math.min(Math.max(0.0, this.YM.getY()), this.height - 7.0));
        }
        this.cj();
        this.height = 100.0;
    }

    private void cj() {
        Color color = ColorUtil.withAlpha(this.rz().rA(), this.pT());
        Color color2 = ColorUtil.withAlpha(this.rz().rB(), this.pT());
        this.aze.forEach(vector2d -> RenderUtil.c(this.position.getX() + vector2d.getX(), this.position.getY() + vector2d.getY(), 2.5, color));
        for (float f2 = 0.0f; f2 <= 1.0f; f2 += 0.1f) {
            ArrayList<Vector2d> arrayList = new ArrayList<Vector2d>(this.aze);
            ArrayList<Vector2d> arrayList2 = new ArrayList<Vector2d>();
            ArrayList<Vector2d> arrayList3 = new ArrayList<Vector2d>();
            while (arrayList.size() > 1) {
                int n2 = 0;
                while ((double)n2 < Math.ceil((float)(arrayList.size() - 1) / 2.0f)) {
                    this.a(arrayList, arrayList2, arrayList3, f2, n2);
                    n2 += 2;
                }
                int n3 = arrayList.size() - 2;
                while ((double)n3 >= Math.ceil((float)(arrayList.size() - 1) / 2.0f)) {
                    this.a(arrayList, arrayList2, arrayList3, f2, n3);
                    n3 -= 2;
                }
                arrayList.addAll(arrayList3);
                arrayList3.clear();
                arrayList.removeAll(arrayList2);
                arrayList2.clear();
            }
            RenderUtil.c(arrayList.get(0).getX() + this.position.getX(), arrayList.get(0).getY() + this.position.getY(), 1.5, ColorUtil.withAlpha(color2, Math.min(200, color2.getAlpha())));
        }
    }

    private void a(ArrayList<Vector2d> arrayList, ArrayList<Vector2d> arrayList2, ArrayList<Vector2d> arrayList3, float f2, int n2) {
        Vector2d vector2d = arrayList.get(n2);
        Vector2d vector2d2 = arrayList.get(n2 + 1);
        arrayList2.add(vector2d);
        arrayList2.add(vector2d2);
        arrayList3.add(new Vector2d(vector2d.getX() + (vector2d2.getX() - vector2d.getX()) * (double)f2, vector2d.getY() + (vector2d2.getY() - vector2d.getY()) * (double)f2));
    }

    @Override
    public boolean e(int n2, int n3, int n4) {
        if (this.position == null) {
            return false;
        }
        if (!MouseUtil.e(this.position.getX(), this.position.getY(), 400.0, this.height)) {
            return false;
        }
        ArrayList<Vector2d> arrayList = new ArrayList<Vector2d>(this.aze);
        arrayList.sort((vector2d, vector2d2) -> (int)(MathOperation.EUCLIDEAN_DISTANCE.a(vector2d.getX() + this.position.getX() - (double)n2, vector2d.getY() + this.position.getY() - (double)n3) - MathOperation.EUCLIDEAN_DISTANCE.a(vector2d2.getX() + this.position.getX() - (double)n2, vector2d2.getY() + this.position.getY() - (double)n3)));
        this.YM = (Vector2d)arrayList.stream().findFirst().get();
        return true;
    }

    @Override
    public void pz() {
        this.YM = null;
    }

    @Override
    public void released() {
        this.cj();
    }

    @Override
    public void key(char c2, int n2) {
        if (this.position == null) {
            return;
        }
    }
}
