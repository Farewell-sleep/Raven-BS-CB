package com.alan.clients.module.impl.render;

import com.alan.clients.Client;
import com.alan.clients.module.Module;
import com.alan.clients.module.api.Category;
import com.alan.clients.module.api.ModuleInfo;
import com.alan.clients.newevent.Listener;
import com.alan.clients.newevent.annotations.EventLink;
import com.alan.clients.newevent.impl.render.Render2DEvent;
import com.alan.clients.value.impl.ModeValue;
import com.alan.clients.value.impl.SubMode;
import com.alan.clients.newevent.impl.input.GuiKeyEvent;
import com.alan.clients.util.shader.ShaderQueueType;
import org.lwjgl.input.Keyboard;
import rip.vantage.commons.util.time.StopWatch;

@ModuleInfo(aliases = "module.render.clickgui.name", description = "module.render.clickgui.description", category = Category.RENDER, keyBind = 54)
public final class ClickGUI extends Module {
    private final StopWatch openStopWatch = new StopWatch();
    public final ModeValue mode = new ModeValue("Mode", this).add(new SubMode("Modern")).add(new SubMode("Dropdown")).setDefault("Modern");
    @EventLink(value = 3)
    public final Listener<Render2DEvent> onRender2D = var1 -> {
        if (this.mode.wo().getName().equals("Modern")) {
            this.b(ShaderQueueType.REGULAR, 2).c(() -> Client.a.getStandardClickGUI().cj());
            this.b(ShaderQueueType.BLOOM, 3).c(() -> Client.a.getStandardClickGUI().ci());
        } else {
            this.b(ShaderQueueType.REGULAR, 2).c(() -> Client.a.z().cj());
            this.b(ShaderQueueType.BLOOM, 3).c(() -> Client.a.z().ci());
        }
    };
    @EventLink
    public final Listener<GuiKeyEvent> onGuiKey = var1 -> {
        if (this.openStopWatch.T(50L)) {
            if (var1.cO() == this.getKey()) {
                aEg.displayGuiScreen(null);
                if (aEg.currentScreen == null) {
                    aEg.setIngameFocus();
                }
            }
        }
    };

    public ClickGUI() {
    }

    @Override
    public void onEnable() {
        if (this.mode.wo().getName().equals("Modern")) {
            aEg.displayGuiScreen(Client.a.getStandardClickGUI());
        } else {
            aEg.displayGuiScreen(Client.a.z());
        }

        this.openStopWatch.aX();
    }

    @Override
    public void onDisable() {
        aEg.setIngameFocus();
        Keyboard.enableRepeatEvents(false);
        Client.a.e().c(Client.a.getStandardClickGUI());
        Client.a.e().c(Client.a.z());
        aMR.execute(() -> Client.a.getConfigManager().getConfigfile().write());
    }
}
