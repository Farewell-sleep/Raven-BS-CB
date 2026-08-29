package com.alan.clients.ui.click.standard;

import java.awt.Color;

public enum UIColors {
    BACKGROUND(23, 26, 33, 254),
    SECONDARY(18, 20, 25),
    TEXT(255, 255, 255),
    SECONDARY_TEXT(255, 255, 255, 220),
    TRINARY_TEXT(255, 255, 255, 130),
    OVERLAY(0, 0, 0, 50);

    private final Color azv;
    private static final UIColors[] $VALUES = pX();

    UIColors(int var3, int var4, int var5, int var6) {
        this.azv = new Color(var3, var4, var5, var6);
    }

    UIColors(int var3, int var4, int var5) {
        this.azv = new Color(var3, var4, var5, 255);
    }

    public Color pV() {
        return this.azv;
    }

    public int pW() {
        return this.azv.getRGB();
    }

    public Color V(int var1) {
        return new Color(var1, this.azv.getGreen(), this.azv.getBlue(), this.azv.getAlpha());
    }

    public Color W(int var1) {
        return new Color(this.azv.getRed(), var1, this.azv.getBlue(), this.azv.getAlpha());
    }

    public Color X(int var1) {
        return new Color(this.azv.getRed(), this.azv.getGreen(), var1, this.azv.getAlpha());
    }

    public Color Y(int var1) {
        return new Color(this.azv.getRed(), this.azv.getGreen(), this.azv.getBlue(), var1);
    }

    public int Z(int var1) {
        return this.Y(var1).getRGB();
    }

    private static UIColors[] pX() {
        return new UIColors[]{BACKGROUND, SECONDARY, TEXT, SECONDARY_TEXT, TRINARY_TEXT, OVERLAY};
    }
}
