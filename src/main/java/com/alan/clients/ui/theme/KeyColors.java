package com.alan.clients.ui.theme;

import java.awt.Color;
import lombok.Generated;

public enum KeyColors {
    RED(new Color(255, 50, 50)),
    ORANGE(new Color(255, 128, 50)),
    YELLOW(new Color(255, 255, 50)),
    LIME(new Color(128, 255, 50)),
    DARK_GREEN(new Color(50, 128, 50)),
    AQUA(new Color(50, 200, 255)),
    DARK_BLUE(new Color(50, 100, 200)),
    PURPLE(new Color(128, 50, 255)),
    PINK(new Color(255, 128, 255)),
    GRAY(new Color(100, 100, 110));

    private final Color color;
    private static final KeyColors[] $VALUES = rM();

    @Generated
    public Color getColor() {
        return this.color;
    }

    @Generated
    KeyColors(Color color) {
        this.color = color;
    }

    private static KeyColors[] rM() {
        return new KeyColors[]{RED, ORANGE, YELLOW, LIME, DARK_GREEN, AQUA, DARK_BLUE, PURPLE, PINK, GRAY};
    }
}
