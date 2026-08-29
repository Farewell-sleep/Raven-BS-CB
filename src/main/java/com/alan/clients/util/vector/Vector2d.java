package com.alan.clients.util.vector;

import lombok.Generated;

public final class Vector2d {
    public double x;
    public double y;

    public Vector2d() {
    }

    public Vector2d(double var1, double var3) {
        this.x = var1;
        this.y = var3;
    }

    public Vector2d offset(double var1, double var3) {
        return new Vector2d(this.x + var1, this.y + var3);
    }

    public Vector2d offset(Vector2d vector2d) {
        return this.offset(vector2d.x, vector2d.y);
    }

    @Generated
    public double getX() {
        return this.x;
    }

    @Generated
    public double getY() {
        return this.y;
    }

    @Generated
    public void setX(double var1) {
        this.x = var1;
    }

    @Generated
    public void setY(double var1) {
        this.y = var1;
    }
}
