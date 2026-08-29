package keystrokesmod.clickgui.rise;

import java.awt.Color;

public enum UIColors {
    BACKGROUND(23, 26, 33, 254),
    SECONDARY(18, 20, 25),
    TEXT(255, 255, 255),
    SECONDARY_TEXT(255, 255, 255, 220),
    TRINARY_TEXT(255, 255, 255, 130),
    OVERLAY(0, 0, 0, 50);

    private final Color color;

    UIColors(int r, int g, int b, int a) { this.color = new Color(r, g, b, a); }
    UIColors(int r, int g, int b) { this.color = new Color(r, g, b, 255); }

    public Color pV() { return color; }
    public int pW() { return color.getRGB(); }
    public Color Y(int alpha) { return new Color(color.getRed(), color.getGreen(), color.getBlue(), alpha); }
    public int Z(int alpha) { return Y(alpha).getRGB(); }
}
