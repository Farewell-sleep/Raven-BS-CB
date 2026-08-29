# -*- coding: utf-8 -*-
import os
base = r"C:\Games\raven-bs\src\main\java\com\alan\clients"

files = {}

files["Client.java"] = '''package com.alan.clients;

import com.alan.clients.component.ComponentManager;
import com.alan.clients.module.api.manager.ModuleManager;
import com.alan.clients.newevent.Event;
import com.alan.clients.newevent.bus.impl.EventBus;
import com.alan.clients.security.SecurityFeatureManager;
import com.alan.clients.ui.click.standard.RiseClickGUI;
import com.alan.clients.ui.theme.ThemeManager;
import com.alan.clients.util.file.config.ConfigManager;
import com.alan.clients.util.localization.Locale;
import com.alan.clients.util.shader.ShaderRenderManager;
import com.google.gson.Gson;
import com.google.gson.GsonBuilder;

/**
 * Rise 客户端主类的精简实现 —— 仅为 clickgui 移植提供数据/管理访问点。
 * 由 BS 初始化时填充，clickgui 相关对象懒加载。
 */
public enum Client {
    a;

    public static final String b = "Rise";
    public final Gson K = new GsonBuilder().setPrettyPrinting().create();

    private EventBus<Event> eventBus;
    private ModuleManager moduleManager;
    private SecurityFeatureManager securityManager;
    private ThemeManager themeManager;
    private ConfigManager configManager;
    private Locale locale = Locale.EN_US;
    private ShaderRenderManager shaderRenderManager;
    private ComponentManager componentManager;
    private RiseClickGUI standardClickGUI;

    public synchronized ModuleManager g() {
        if (moduleManager == null) {
            moduleManager = new ModuleManager();
            try {
                moduleManager.init();
            } catch (Throwable t) {
                t.printStackTrace();
            }
        }
        return moduleManager;
    }

    public EventBus<Event> e() {
        if (eventBus == null) eventBus = new EventBus<>();
        return eventBus;
    }

    public SecurityFeatureManager getSecurityManager() {
        if (securityManager == null) securityManager = new SecurityFeatureManager();
        return securityManager;
    }

    public ConfigManager getConfigManager() {
        if (configManager == null) configManager = new ConfigManager();
        return configManager;
    }

    public ThemeManager getThemeManager() {
        if (themeManager == null) themeManager = new ThemeManager();
        return themeManager;
    }

    public Locale getLocale() {
        return locale;
    }

    public void setLocale(Locale l) {
        if (l != null) locale = l;
    }

    public void a(Locale l) {
        if (l != null) locale = l;
    }

    public Gson A() {
        return K;
    }

    public ShaderRenderManager u() {
        if (shaderRenderManager == null) shaderRenderManager = new ShaderRenderManager();
        return shaderRenderManager;
    }

    public ComponentManager h() {
        if (componentManager == null) componentManager = new ComponentManager();
        return componentManager;
    }

    public RiseClickGUI getStandardClickGUI() {
        if (standardClickGUI == null) standardClickGUI = new RiseClickGUI();
        return standardClickGUI;
    }

    public RiseClickGUI z() {
        return getStandardClickGUI();
    }

    public void a(RiseClickGUI gui) {
        standardClickGUI = gui;
    }

    public ModuleManager getModuleManager() {
        return g();
    }
}
'''

files["bridge/RiseModuleBridge.java"] = '''package com.alan.clients.bridge;

import com.alan.clients.module.api.Category;
import com.alan.clients.module.api.ModuleInfo;
import com.alan.clients.module.api.manager.ModuleManager;
import keystrokesmod.Raven;

import java.lang.reflect.Proxy;

/**
 * 把 BS 模块注册到 Rise ModuleManager（数据桥接层）。
 */
public class RiseModuleBridge {

    public static void build(ModuleManager mm) {
        for (keystrokesmod.module.Module bs : Raven.moduleManager.getModules()) {
            if (bs == null) continue;
            try {
                RiseModuleWrapper wrapper = new RiseModuleWrapper(bs);
                mm.register(wrapper.getClass(), wrapper);
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
            case render: return Category.RENDER;
            case world: return Category.EXPLOIT;
            case minigames: return Category.GHOST;
            case fun: return Category.GHOST;
            case client: return Category.RENDER;
            case profiles: return Category.SCRIPT;
            case scripts: return Category.SCRIPT;
            default: return Category.PLAYER;
        }
    }
}
'''

files["bridge/RiseModuleWrapper.java"] = '''package com.alan.clients.bridge;

import com.alan.clients.module.Module;
import com.alan.clients.module.api.Category;
import com.alan.clients.value.impl.BooleanValue;
import com.alan.clients.value.impl.ColorValue;
import com.alan.clients.value.impl.ListValue;
import com.alan.clients.value.impl.NumberValue;
import keystrokesmod.module.setting.Setting;
import keystrokesmod.module.setting.impl.ButtonSetting;
import keystrokesmod.module.setting.impl.ColorSetting;
import keystrokesmod.module.setting.impl.SliderSetting;

import java.awt.Color;

/**
 * 把一个 BS 模块包装成 Rise Module，使 Rise clickgui 能显示并操作 BS 模块。
 */
public class RiseModuleWrapper extends Module {
    private final keystrokesmod.module.Module bs;
    private final Category category;

    public RiseModuleWrapper(keystrokesmod.module.Module bs) {
        super(RiseModuleBridge.of(bs.getName(), bs.getName(), RiseModuleBridge.mapCategory(bs.moduleCategory())));
        this.bs = bs;
        this.category = RiseModuleBridge.mapCategory(bs.moduleCategory());
        this.buildValues();
    }

    public keystrokesmod.module.Module bs() {
        return this.bs;
    }

    public Category riseCategory() {
        return this.category;
    }

    private void buildValues() {
        try {
            for (Setting s : this.bs.getSettings()) {
                if (s == null) continue;
                if (s instanceof ButtonSetting) {
                    ButtonSetting b = (ButtonSetting) s;
                    if (b.isMethodButton) continue;
                    BooleanValue v = new BooleanValue(s.getName(), this, b.isToggled());
                    v.setValueChangeConsumer(val -> b.setEnabled(val));
                } else if (s instanceof SliderSetting) {
                    SliderSetting sl = (SliderSetting) s;
                    if (sl.isString) {
                        String[] options = sl.getOptions();
                        if (options == null || options.length == 0) continue;
                        ListValue<String> lv = new ListValue<>(s.getName(), this);
                        for (String opt : options) lv.add(opt);
                        int idx = (int) Math.round(sl.getInput());
                        if (idx < 0 || idx >= options.length) idx = 0;
                        lv.setDefault(options[idx]);
                        lv.setValueChangeConsumer(val -> {
                            for (int i = 0; i < options.length; i++) {
                                if (options[i].equals(val)) {
                                    sl.setValue(i);
                                    return;
                                }
                            }
                        });
                    } else {
                        NumberValue nv = new NumberValue(s.getName(), this, sl.getInput(), sl.getMin(), sl.getMax(), 2);
                        nv.setValueChangeConsumer(val -> sl.setValue(((Number) val).doubleValue()));
                    }
                } else if (s instanceof ColorSetting) {
                    ColorSetting cs = (ColorSetting) s;
                    ColorValue cv = new ColorValue(s.getName(), this, new Color(cs.getColor(), true));
                    cv.setValueChangeConsumer(color -> {
                        try {
                            cs.setColor(color.getRed(), color.getGreen(), color.getBlue(), color.getAlpha());
                        } catch (Throwable ignored) {
                        }
                    });
                }
            }
        } catch (Throwable ignored) {
        }
    }

    @Override
    public boolean isEnabled() {
        return this.bs.isEnabled();
    }

    @Override
    public void toggle() {
        this.bs.toggle();
    }

    @Override
    public void setEnabled(boolean enabled) {
        if (enabled) {
            if (!this.bs.isEnabled()) this.bs.enable();
        } else {
            if (this.bs.isEnabled()) this.bs.disable();
        }
    }

    @Override
    public void onEnable() {
    }

    @Override
    public void onDisable() {
    }
}
'''

for rel, content in files.items():
    p = os.path.join(base, rel)
    os.makedirs(os.path.dirname(p), exist_ok=True)
    with open(p, "w", encoding="utf-8", newline="\n") as f:
        f.write(content)
    print("wrote", rel)
print("DONE")
