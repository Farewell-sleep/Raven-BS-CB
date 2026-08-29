package com.alan.clients.util.dragging;

import com.alan.clients.util.MCPCompat;
import com.alan.clients.util.interfaces.InstanceAccess;
import com.alan.clients.util.vector.Vector2d;
import net.minecraft.client.gui.ScaledResolution;

public class Mouse implements InstanceAccess {
    public Mouse() {
    }

    public static Vector2d getMouse() {
        ScaledResolution scaledresolution = MCPCompat.scaledResolution();
        int i = org.lwjgl.input.Mouse.getX() * scaledresolution.getScaledWidth() / aEg.displayWidth;
        int j = scaledresolution.getScaledHeight() - org.lwjgl.input.Mouse.getY() * scaledresolution.getScaledHeight() / aEg.displayHeight - 1;
        return new Vector2d(i, j);
    }
}
