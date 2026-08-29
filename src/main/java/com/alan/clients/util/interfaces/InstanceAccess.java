package com.alan.clients.util.interfaces;

import com.alan.clients.Client;
import com.alan.clients.component.Component;
import com.alan.clients.module.Module;
import com.alan.clients.ui.click.standard.RiseClickGUI;
import com.google.gson.Gson;
import com.alan.clients.ui.theme.Themes;
import com.alan.clients.util.shader.ShaderRenderQueue;
import com.alan.clients.util.shader.ShaderRenderManager;
import com.alan.clients.util.shader.ShaderQueueType;
import net.minecraft.client.Minecraft;

public interface InstanceAccess {
    Minecraft aEg = Minecraft.getMinecraft();

    default Client rN() {
        return Client.a;
    }

    default ShaderRenderManager u() {
        return this.rN().u();
    }

    default RiseClickGUI getStandardClickGUI() {
        return this.rN().getStandardClickGUI();
    }

    default ShaderRenderQueue b(ShaderQueueType var1) {
        return this.u().a(var1);
    }

    default ShaderRenderQueue b(ShaderQueueType var1, int var2) {
        return this.u().a(var1, var2);
    }

    default <T extends Component> T d(Class<T> type) {
        return this.rN().h().b(type);
    }

    default Themes rz() {
        return this.rN().getThemeManager().getTheme();
    }

    default <T extends Module> T e(Class<T> type) {
        return this.rN().g().c(type);
    }

    default Gson A() {
        return this.rN().A();
    }

    default Minecraft rO() {
        return Minecraft.getMinecraft();
    }
}
