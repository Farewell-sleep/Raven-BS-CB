package com.alan.clients.util.shader.impl;

import com.alan.clients.util.shader.base.RiseShaderProgram;
import com.alan.clients.util.shader.base.ShaderUniforms;
import java.awt.Color;
import net.minecraft.client.Minecraft;
import net.minecraft.client.renderer.GlStateManager;

public final class TriRGQShader {
    private final RiseShaderProgram aQq = new RiseShaderProgram("trirgq.glsl", "vertex.vsh");

    public TriRGQShader() {
    }

    public void a(
        float var1,
        float var2,
        float var3,
        float var4,
        float var5,
        Color color,
        Color var7,
        Color var8,
        boolean var9,
        boolean var10,
        boolean var11,
        boolean var12,
        boolean var13
    ) {
        int i = this.aQq.getProgramId();
        this.aQq.rt();
        ShaderUniforms.uniform2f(i, "u_size", var3, var4);
        ShaderUniforms.uniform1f(i, "u_radius", var5);
        ShaderUniforms.uniform4f(i, "u_first_color", color.getRed() / 255.0F, color.getGreen() / 255.0F, color.getBlue() / 255.0F, color.getAlpha() / 255.0F);
        ShaderUniforms.uniform4f(i, "u_second_color", var7.getRed() / 255.0F, var7.getGreen() / 255.0F, var7.getBlue() / 255.0F, var7.getAlpha() / 255.0F);
        ShaderUniforms.uniform4f(i, "u_third_color", var8.getRed() / 255.0F, var8.getGreen() / 255.0F, var8.getBlue() / 255.0F, var8.getAlpha() / 255.0F);
        ShaderUniforms.uniform1i(i, "u_direction", var9 ? 1 : 0);
        ShaderUniforms.uniform1f(i, "u_time", (float)(System.currentTimeMillis() - Minecraft.getMinecraft().getSystemTime()) / 1000.0F);
        ShaderUniforms.uniform4f(i, "u_edges", var10 ? 1.0F : 0.0F, var11 ? 1.0F : 0.0F, var12 ? 1.0F : 0.0F, var13 ? 1.0F : 0.0F);
        GlStateManager.enableBlend();
        GlStateManager.blendFunc(770, 771);
        RiseShaderProgram.drawQuad(var1, var2, var3, var4);
        GlStateManager.disableBlend();
        RiseShaderProgram.stop();
    }

    public void draw(double var1, double var3, double var5, double var7, double var9, Color color, Color var12, boolean var13) {
        this.a((float)var1, (float)var3, (float)var5, (float)var7, (float)var9, color, var12, color, var13, true, true, true, true);
    }
}
