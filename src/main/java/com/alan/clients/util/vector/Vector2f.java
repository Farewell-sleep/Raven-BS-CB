package com.alan.clients.util.vector;

import lombok.Generated;

public final class Vector2f {
    public float x;
    public float y;

    public Vector2f(Vector2f vec2) {
        this(vec2.x, vec2.y);
    }

    public Vector2f h(float var1, float var2) {
        return new Vector2f(this.x + var1, this.y + var2);
    }

    @Generated
    public float getX() {
        return this.x;
    }

    @Generated
    public float getY() {
        return this.y;
    }

    @Generated
    public void setX(float var1) {
        this.x = var1;
    }

    @Generated
    public void setY(float var1) {
        this.y = var1;
    }

    @Generated
    public Vector2f(float var1, float var2) {
        this.x = var1;
        this.y = var2;
    }
}
