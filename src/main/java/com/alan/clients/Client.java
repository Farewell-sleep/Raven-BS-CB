package com.alan.clients;

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

    public static final String b = "Raven BS";
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
