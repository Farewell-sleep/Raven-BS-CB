package com.alan.clients.newevent.impl.other;

import com.alan.clients.module.Module;
import com.alan.clients.newevent.Event;

public class ModuleToggleEvent implements Event {
    private final Module module;

    public ModuleToggleEvent(Module module) {
        this.module = module;
    }

    public Module getModule() {
        return this.module;
    }
}
