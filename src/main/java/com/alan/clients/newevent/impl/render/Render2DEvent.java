package com.alan.clients.newevent.impl.render;

import com.alan.clients.newevent.Event;

public class Render2DEvent implements Event {
    private final float partialTicks;

    public Render2DEvent(float partialTicks) {
        this.partialTicks = partialTicks;
    }

    public float getPartialTicks() {
        return this.partialTicks;
    }
}
