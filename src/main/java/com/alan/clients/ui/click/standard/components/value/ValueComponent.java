package com.alan.clients.ui.click.standard.components.value;

import com.alan.clients.util.interfaces.InstanceAccess;
import com.alan.clients.util.vector.Vector2d;
import com.alan.clients.value.Value;
import lombok.Generated;

public abstract class ValueComponent implements InstanceAccess {
    public double height = 14.0;
    public Vector2d position;
    public Value<?> value;
    public int ayD = 255;

    public ValueComponent(Value<?> var1) {
        this.value = var1;
    }

    public abstract void draw(Vector2d position, int var2, int var3, float var4);

    public abstract boolean e(int var1, int var2, int var3);

    public abstract void pz();

    public abstract void released();

    public abstract void key(char var1, int var2);

    @Generated
    public double getHeight() {
        return this.height;
    }

    @Generated
    public Vector2d getPosition() {
        return this.position;
    }

    @Generated
    public Value<?> getValue() {
        return this.value;
    }

    @Generated
    public int pT() {
        return this.ayD;
    }

    @Generated
    public void U(int var1) {
        this.ayD = var1;
    }
}
