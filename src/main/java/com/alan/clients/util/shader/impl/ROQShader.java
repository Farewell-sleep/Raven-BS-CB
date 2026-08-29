package com.alan.clients.util.shader.impl;

import com.alan.clients.util.shader.base.RiseShaderProgram;
import com.alan.clients.util.shader.base.ShaderUniforms;
import java.awt.Color;
import net.minecraft.client.renderer.GlStateManager;

public class ROQShader {
    private final RiseShaderProgram program = new RiseShaderProgram("roq.glsl", "vertex.vsh");

    public ROQShader() {
    }

    public void draw(float var1, float var2, float var3, float var4, float var5, float var6, Color color) {
        int i = this.program.getProgramId();
        this.program.rt();
        ShaderUniforms.uniform2f(i, "u_size", var3, var4);
        ShaderUniforms.uniform1f(i, "u_radius", var5);
        ShaderUniforms.uniform1f(i, "u_border_size", var6);
        ShaderUniforms.uniform4f(i, "u_color", color.getRed() / 255.0F, color.getGreen() / 255.0F, color.getBlue() / 255.0F, color.getAlpha() / 255.0F);
        GlStateManager.enableBlend();
        GlStateManager.blendFunc(770, 771);
        RiseShaderProgram.drawQuad(var1, var2, var3, var4);
        GlStateManager.disableBlend();
        RiseShaderProgram.stop();
    }

    public void b(double var1, double var3, double var5, double var7, double var9, double var11, Color color) {
        this.draw((float)var1, (float)var3, (float)var5, (float)var7, (float)var9, (float)var11, color);
    }
}
