package myaupp.module;

import java.util.LinkedHashMap;

/**
 * ModuleManager —— 按名字/类型注册与查找模块。
 */
public class ModuleManager {
    public final LinkedHashMap<Class<?>, Module> modules = new LinkedHashMap<>();
    private final LinkedHashMap<String, Module> byName = new LinkedHashMap<>();

    public Module getModule(String string) {
        if (string == null) {
            return null;
        }
        Module direct = byName.get(string.toLowerCase());
        if (direct != null) {
            return direct;
        }
        return this.modules.values().stream().filter(mD -> mD.getName().equalsIgnoreCase(string)).findFirst().orElse(null);
    }

    public Module getModule(Class<?> clazz) {
        return this.modules.get(clazz);
    }

    public void register(Module module) {
        if (module == null) {
            return;
        }
        this.modules.put(module.getClass(), module);
        this.byName.put(module.getName().toLowerCase(), module);
    }

    public void playSound() {
    }
}
