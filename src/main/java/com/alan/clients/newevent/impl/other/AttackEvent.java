package com.alan.clients.newevent.impl.other;

import com.alan.clients.newevent.Event;
import net.minecraft.entity.Entity;

public class AttackEvent implements Event {
    private final Entity target;

    public AttackEvent(Entity target) {
        this.target = target;
    }

    public Entity getTarget() {
        return this.target;
    }
}
