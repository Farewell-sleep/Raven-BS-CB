package com.alan.clients.newevent.impl.input;

import com.alan.clients.newevent.Event;

public class GuiKeyEvent implements Event {
    private final int keyCode;

    public GuiKeyEvent(int keyCode) {
        this.keyCode = keyCode;
    }

    public int cO() {
        return this.keyCode;
    }
}
