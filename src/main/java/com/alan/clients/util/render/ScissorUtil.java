package com.alan.clients.util.render;

import net.minecraft.client.gui.ScaledResolution;
import org.lwjgl.opengl.GL11;

public class ScissorUtil {
    public ScissorUtil() {
    }

    public static void hK() {
        GL11.glEnable(3089);
    }

    public static void disable() {
        GL11.glDisable(3089);
    }

    public static void scissor(ScaledResolution resolution, double var1, double var3, double var5, double var7) {
        if (var1 + var5 != var1 && var3 + var7 != var3 && !(var1 < 0.0) && !(var3 + var7 < 0.0)) {
            int i = resolution.getScaleFactor();
            GL11.glScissor(
                (int)Math.round(var1 * i), (int)Math.round((resolution.getScaledHeight() - (var3 + var7)) * i), (int)Math.round(var5 * i), (int)Math.round(var7 * i)
            );
        }
    }
}
