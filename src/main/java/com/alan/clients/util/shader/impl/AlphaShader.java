package com.alan.clients.util.shader.impl;

import com.alan.clients.util.shader.base.RiseShader;
import com.alan.clients.util.shader.base.RiseShaderProgram;
import com.alan.clients.util.shader.base.ShaderRenderType;
import com.alan.clients.util.shader.base.ShaderUniforms;
import java.util.List;
import lombok.Generated;
import net.minecraft.client.renderer.GlStateManager;
import net.minecraft.client.shader.Framebuffer;
import org.lwjgl.opengl.Display;

public class AlphaShader extends RiseShader {
    private final RiseShaderProgram alphaProgram = new RiseShaderProgram("alpha.frag", "vertex.vsh");
    private Framebuffer inputFramebuffer = new Framebuffer(aEg.displayWidth, aEg.displayHeight, true);
    private float alpha;

    public AlphaShader() {
    }

    @Override
    public void a(ShaderRenderType var1, float var2, List<Runnable> runnables) {
        if (Display.isVisible()) {
            if (var1 == ShaderRenderType.OVERLAY) {
                this.update();
                this.setActive(true);
                if (this.isActive()) {
                    this.inputFramebuffer.bindFramebuffer(true);
                    runnables.forEach(Runnable::run);
                    aEg.getFramebuffer().bindFramebuffer(true);
                    int i = this.alphaProgram.getProgramId();
                    this.alphaProgram.rt();
                    ShaderUniforms.uniform1i(i, "u_diffuse_sampler", 0);
                    ShaderUniforms.uniform1f(i, "u_alpha", this.alpha);
                    GlStateManager.enableBlend();
                    GlStateManager.blendFunc(770, 771);
                    GlStateManager.alphaFunc(516, 0.0F);
                    this.inputFramebuffer.bindFramebufferTexture();
                    RiseShaderProgram.vN();
                    GlStateManager.disableBlend();
                    RiseShaderProgram.stop();
                }
            }
        }
    }

    @Override
    public void update() {
        if (aEg.displayWidth == this.inputFramebuffer.framebufferWidth && aEg.displayHeight == this.inputFramebuffer.framebufferHeight) {
            this.inputFramebuffer.framebufferClear();
        } else {
            this.inputFramebuffer.deleteFramebuffer();
            this.inputFramebuffer = new Framebuffer(aEg.displayWidth, aEg.displayHeight, true);
        }

        this.inputFramebuffer.setFramebufferColor(0.0F, 0.0F, 0.0F, 0.0F);
    }

    @Generated
    public void setAlpha(float var1) {
        this.alpha = var1;
    }
}
