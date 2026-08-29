package com.alan.clients.ui.click.standard.screen.impl;

import com.alan.clients.ui.click.standard.RiseClickGUI;
import com.alan.clients.ui.click.standard.screen.Screen;
import com.alan.clients.util.interfaces.InstanceAccess;
import com.alan.clients.util.vector.Vector2f;

public final class HomeScreen implements Screen, InstanceAccess {
    public HomeScreen() {
    }

    @Override
    public void onRender(int var1, int var2, float var3) {
        RiseClickGUI riseclickgui = this.getStandardClickGUI();
        new Vector2f(riseclickgui.axI.x + 20.0F, riseclickgui.axI.y + 20.0F);
        new Vector2f(riseclickgui.axI.x + riseclickgui.position.x / 2.0F, riseclickgui.axI.y + riseclickgui.position.y - 14.0F);
    }
}
