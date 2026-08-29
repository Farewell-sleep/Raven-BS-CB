package com.alan.clients.util.vector;

import lombok.Generated;

public class Vector3d {
    public double x;
    public double y;
    public double z;

    public Vector3d(double var1, double var3, double var5) {
        this.x = var1;
        this.y = var3;
        this.z = var5;
    }

    public Vector3d v(double var1, double var3, double var5) {
        return new Vector3d(this.x + var1, this.y + var3, this.z + var5);
    }

    public Vector3d e(Vector3d var1) {
        return this.v(var1.x, var1.y, var1.z);
    }

    public Vector3d w(double var1, double var3, double var5) {
        return this.v(-var1, -var3, -var5);
    }

    public Vector3d subtract(Vector3d var1) {
        return this.v(-var1.x, -var1.y, -var1.z);
    }

    public double wg() {
        return Math.sqrt(this.x * this.x + this.y * this.y + this.z * this.z);
    }

    public Vector3d wh() {
        double d0 = this.wg();
        return d0 == 0.0 ? new Vector3d(0.0, 0.0, 0.0) : new Vector3d(this.x / d0, this.y / d0, this.z / d0);
    }

    public double getX() {
        return this.x;
    }

    public double getY() {
        return this.y;
    }

    public double getZ() {
        return this.z;
    }

    public Vector3d ag(double var1) {
        return new Vector3d(this.x * var1, this.y * var1, this.z * var1);
    }

    public double g(Vector3d var1) {
        return Math.sqrt(Math.pow(var1.x - this.x, 2.0) + Math.pow(var1.y - this.y, 2.0) + Math.pow(var1.z - this.z, 2.0));
    }

    @Override
    public boolean equals(Object var1) {
        if (!(var1 instanceof Vector3d)) {
            return false;
        }
        Vector3d aka = (Vector3d) var1;
        return Math.floor(this.x) == Math.floor(aka.x) && Math.floor(this.y) == Math.floor(aka.y) && Math.floor(this.z) == Math.floor(aka.z);
    }

    @Generated
    public void setX(double var1) {
        this.x = var1;
    }

    @Generated
    public void setY(double var1) {
        this.y = var1;
    }

    @Generated
    public void setZ(double var1) {
        this.z = var1;
    }
}
