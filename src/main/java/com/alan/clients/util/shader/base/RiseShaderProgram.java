package com.alan.clients.util.shader.base;

import com.alan.clients.util.MCPCompat;
import com.alan.clients.util.interfaces.InstanceAccess;
import com.alan.clients.util.shader.ShaderUtil;
import net.minecraft.client.gui.ScaledResolution;
import org.lwjgl.opengl.GL11;
import org.lwjgl.opengl.GL20;

public class RiseShaderProgram implements InstanceAccess {
    private final int programId;

    public RiseShaderProgram(String var1, String var2) {
        this.programId = ShaderUtil.createShader(var1, var2);
    }

    public static void drawQuad(double var0, double var2, double var4, double var6) {
        GL11.glBegin(7);
        GL11.glTexCoord2f(0.0F, 0.0F);
        GL11.glVertex2d(var0, var2 + var6);
        GL11.glTexCoord2f(1.0F, 0.0F);
        GL11.glVertex2d(var0 + var4, var2 + var6);
        GL11.glTexCoord2f(1.0F, 1.0F);
        GL11.glVertex2d(var0 + var4, var2);
        GL11.glTexCoord2f(0.0F, 1.0F);
        GL11.glVertex2d(var0, var2);
        GL11.glEnd();
    }

    public static void vN() {
        ScaledResolution scaledresolution = MCPCompat.scaledResolution();
        drawQuad(0.0, 0.0, scaledresolution.getScaledWidth_double(), scaledresolution.getScaledHeight_double());
    }

    public void rt() {
        GL20.glUseProgram(this.programId);
    }

    public static void stop() {
        GL20.glUseProgram(0);
    }

    public int getProgramId() {
        return this.programId;
    }
}
