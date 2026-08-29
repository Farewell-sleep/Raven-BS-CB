package com.alan.clients.util;

import com.alan.clients.util.interfaces.InstanceAccess;
import com.alan.clients.util.vector.Vector2d;
import org.lwjgl.input.Mouse;

public class MouseUtil implements InstanceAccess {
    public MouseUtil() {
    }

    public static boolean isHovered(double var0, double var2, double var4, double var6, int var8, int var9) {
        return var8 >= var0 && var8 < var0 + var4 && var9 >= var2 && var9 < var2 + var6;
    }

    public static boolean e(double var0, double var2, double var4, double var6) {
        Vector2d vector2d = rU();
        return vector2d.x >= var0 && vector2d.x < var0 + var4 && vector2d.y >= var2 && vector2d.y < var2 + var6;
    }

    public static Vector2d rU() {
        int i = MCPCompat.scaledResolution().getScaledWidth();
        int j = MCPCompat.scaledResolution().getScaledHeight();
        int k = Mouse.getX() * i / aEg.displayWidth;
        int l = j - Mouse.getY() * j / aEg.displayHeight - 1;
        return new Vector2d(k, l);
    }
}
