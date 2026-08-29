package com.alan.clients.bridge;

import com.alan.clients.module.api.Category;
import com.alan.clients.module.api.ModuleInfo;
import com.alan.clients.module.api.manager.ModuleManager;
import keystrokesmod.Raven;

import java.lang.reflect.Proxy;
import java.util.HashMap;
import java.util.Map;

/**
 * 把 BS 模块注册到 Rise ModuleManager（数据桥接层）。
 */
public class RiseModuleBridge {

    private static final Map<keystrokesmod.module.Module, RiseModuleWrapper> WRAPPER_CACHE = new HashMap<>();

    public static void build(ModuleManager mm) {
        for (keystrokesmod.module.Module bs : Raven.moduleManager.getModules()) {
            if (bs == null) continue;
            try {
                RiseModuleWrapper wrapper = WRAPPER_CACHE.get(bs);
                if (wrapper == null) {
                    wrapper = new RiseModuleWrapper(bs);
                    WRAPPER_CACHE.put(bs, wrapper);
                }
                mm.add(wrapper);
            } catch (Throwable t) {
                t.printStackTrace();
            }
        }
    }

    public static ModuleInfo of(String name, String desc, Category cat) {
        return (ModuleInfo) Proxy.newProxyInstance(
                ModuleInfo.class.getClassLoader(),
                new Class[]{ModuleInfo.class},
                (proxy, method, args) -> {
                    String m = method.getName();
                    if (m.equals("aliases")) return new String[]{name};
                    if (m.equals("description")) return desc;
                    if (m.equals("category")) return cat;
                    if (m.equals("keyBind")) return 0;
                    if (m.equals("autoEnabled")) return false;
                    if (m.equals("allowDisable")) return true;
                    if (m.equals("annotationType")) return ModuleInfo.class;
                    if (m.equals("toString")) return "@ModuleInfo(" + name + ")";
                    if (m.equals("hashCode")) return System.identityHashCode(proxy);
                    if (m.equals("equals")) return proxy == args[0];
                    return method.getDefaultValue();
                });
    }

    public static Category mapCategory(keystrokesmod.module.Module.category c) {
        if (c == null) return Category.PLAYER;
        switch (c) {
            case combat: return Category.COMBAT;
            case movement: return Category.MOVEMENT;
            case player: return Category.PLAYER;
            case world: return Category.WORLD;
            case render: return Category.RENDER;
            case minigames: return Category.MINIGAMES;
            case fun: return Category.FUN;
            case other: return Category.OTHER;
            case client: return Category.CLIENT;
            case profiles: return Category.PROFILES;
            case scripts: return Category.SCRIPTS;
            default: return Category.PLAYER;
        }
    }
}
