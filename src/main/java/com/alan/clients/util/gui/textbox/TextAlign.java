package com.alan.clients.util.gui.textbox;

public enum TextAlign {
    LEFT,
    CENTER;

    private static final TextAlign[] $VALUES = tK();

    TextAlign() {
    }

    private static TextAlign[] tK() {
        return new TextAlign[]{LEFT, CENTER};
    }
}
