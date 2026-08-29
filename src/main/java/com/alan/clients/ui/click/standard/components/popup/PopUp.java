package com.alan.clients.ui.click.standard.components.popup;

import com.alan.clients.Client;
import com.alan.clients.ui.click.standard.RiseClickGUI;
import com.alan.clients.util.interfaces.InstanceAccess;
import com.alan.clients.util.render.RenderUtil;
import com.alan.clients.util.vector.Vector2f;
import java.awt.Color;

public class PopUp implements InstanceAccess {
    public Vector2f scale;

    public PopUp() {
    }

    public void draw() {
        RiseClickGUI riseclickgui = Client.a.getStandardClickGUI();
        double d0 = riseclickgui.axI.x;
        double d1 = riseclickgui.axI.y;
        double d2 = riseclickgui.position.x;
        double d3 = riseclickgui.position.y;
        if (this.scale != null) {
            RenderUtil.dropShadow(
                60, (float)(d0 + d2 / 2.0 - this.scale.x / 2.0F), (float)(d1 + d3 / 2.0 - this.scale.y / 2.0F), this.scale.x, this.scale.y, 50.0, 34.0
            );
            RenderUtil.roundedRectangle(
                d0 + d2 / 2.0 - this.scale.x / 2.0F, d1 + d3 / 2.0 - this.scale.y / 2.0F, this.scale.x, this.scale.y, 9.0, new Color(0, 0, 0, 230)
            );
        }
    }
}
