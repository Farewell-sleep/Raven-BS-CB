package com.alan.clients.util.shader.impl;

import com.alan.clients.util.shader.base.RiseShaderProgram;
import com.alan.clients.util.shader.base.ShaderUniforms;
import java.awt.Color;
import net.minecraft.client.renderer.GlStateManager;

public class RQShader {
    private final RiseShaderProgram program = new RiseShaderProgram("rq.frag", "vertex.vsh");

    public RQShader() {
    }

    public void a(float var1, float var2, float var3, float var4, float var5, Color color, boolean var7, boolean var8, boolean var9, boolean var10) {
        int i = this.program.getProgramId();
        this.program.rt();
        ShaderUniforms.uniform2f(i, "u_size", var3, var4);
        ShaderUniforms.uniform1f(i, "u_radius", var5);
        ShaderUniforms.uniform4f(i, "u_color", color.getRed() / 255.0F, color.getGreen() / 255.0F, color.getBlue() / 255.0F, color.getAlpha() / 255.0F);
        ShaderUniforms.uniform4f(i, "u_edges", var7 ? 1.0F : 0.0F, var8 ? 1.0F : 0.0F, var9 ? 1.0F : 0.0F, var10 ? 1.0F : 0.0F);
        GlStateManager.enableBlend();
        RiseShaderProgram.drawQuad(var1, var2, var3, var4);
        GlStateManager.disableBlend();
        RiseShaderProgram.stop();
    }

    public void b(double var1, double var3, double var5, double var7, double var9, Color color, boolean var12, boolean var13, boolean var14, boolean var15) {
        this.a((float)var1, (float)var3, (float)var5, (float)var7, (float)var9, color, var12, var13, var14, var15);
    }

    public void draw(double var1, double var3, double var5, double var7, double var9, Color color) {
        this.a((float)var1, (float)var3, (float)var5, (float)var7, (float)var9, color, true, true, true, true);
    }
}
