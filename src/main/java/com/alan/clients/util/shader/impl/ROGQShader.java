package com.alan.clients.util.shader.impl;

import com.alan.clients.util.shader.base.RiseShaderProgram;
import com.alan.clients.util.shader.base.ShaderUniforms;
import java.awt.Color;
import net.minecraft.client.renderer.GlStateManager;

public class ROGQShader {
    private final RiseShaderProgram program = new RiseShaderProgram("rogq.frag", "vertex.vsh");

    public ROGQShader() {
    }

    public void draw(float var1, float var2, float var3, float var4, float var5, float var6, Color color, Color var8) {
        int i = this.program.getProgramId();
        this.program.rt();
        ShaderUniforms.uniform2f(i, "u_size", var3, var4);
        ShaderUniforms.uniform1f(i, "u_radius", var5);
        ShaderUniforms.uniform1f(i, "u_border_size", var6);
        ShaderUniforms.uniform4f(i, "u_color_1", color.getRed() / 255.0F, color.getGreen() / 255.0F, color.getBlue() / 255.0F, color.getAlpha() / 255.0F);
        ShaderUniforms.uniform4f(i, "u_color_2", var8.getRed() / 255.0F, var8.getGreen() / 255.0F, var8.getBlue() / 255.0F, var8.getAlpha() / 255.0F);
        GlStateManager.enableBlend();
        GlStateManager.blendFunc(770, 771);
        RiseShaderProgram.drawQuad(var1, var2, var3, var4);
        GlStateManager.disableBlend();
        RiseShaderProgram.stop();
    }

    public void b(double var1, double var3, double var5, double var7, double var9, double var11, Color color, Color var14) {
        this.draw((float)var1, (float)var3, (float)var5, (float)var7, (float)var9, (float)var11, color, var14);
    }
}
