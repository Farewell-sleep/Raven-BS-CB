package com.alan.clients.module.api.manager;

import com.alan.clients.bridge.RiseModuleBridge;
import com.alan.clients.module.Module;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

public class ModuleManager {
    public final Map<Class<? extends Module>, Module> modules = new HashMap<>();
    private final ArrayList<Module> list = new ArrayList<>();

    public ArrayList<Module> getAll() {
        return new ArrayList<>(this.list);
    }

    public <T extends Module> T c(Class<T> type) {
        return (T) this.modules.get(type);
    }

    public <T extends Module> T get(String name) {
        for (Module m : this.list) {
            String[] aliases = m.getAliases();
            if (aliases != null) {
                for (String a : aliases) {
                    if (a != null && a.replace(" ", "").equalsIgnoreCase(name.replace(" ", ""))) return (T) m;
                }
            }
        }
        return null;
    }

    public void register(Class<? extends Module> type, Module module) {
        this.modules.put(type, module);
        if (!this.list.contains(module)) this.list.add(module);
    }

    public void add(Module module) {
        if (!this.list.contains(module)) this.list.add(module);
    }

    public void remove(Module module) {
        this.list.remove(module);
        this.modules.values().remove(module);
    }

    public void updateArraylistCache() {
    }

    public void init() {
        RiseModuleBridge.build(this);
    }
}
