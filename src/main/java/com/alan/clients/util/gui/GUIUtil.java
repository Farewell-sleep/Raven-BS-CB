package com.alan.clients.util.gui;

import com.alan.clients.util.vector.Vector2d;
import com.alan.clients.util.vector.Vector2f;
import lombok.Generated;

public final class GUIUtil {
    public static boolean c(double var0, double var2, double var4, double var6, double var8, double var10) {
        return var8 > var0 && var8 < var0 + var4 && var10 > var2 && var10 < var2 + var6;
    }

    public static boolean mouseOver(Vector2d vector2d, Vector2d var1, double var2, double var4) {
        return var2 > vector2d.x && var2 < vector2d.x + var1.x && var4 > vector2d.y && var4 < vector2d.y + var1.y;
    }

    public static boolean a(Vector2f vec2, Vector2f var1, double var2, double var4) {
        return var2 > vec2.x && var2 < vec2.x + var1.x && var4 > vec2.y && var4 < vec2.y + var1.y;
    }

    public static boolean a(Vector2f vec2, Vector2f var1, Vector2d vector2d) {
        return vector2d.x > vec2.x && vector2d.x < vec2.x + var1.x && vector2d.y > vec2.y && vector2d.y < vec2.y + var1.y;
    }

    @Generated
    private GUIUtil() {
        throw new UnsupportedOperationException("This is a utility class and cannot be instantiated");
    }
}
