package com.alan.clients.util.shader.impl;

import com.alan.clients.util.shader.base.RiseShaderProgram;
import com.alan.clients.util.shader.base.ShaderUniforms;
import com.alan.clients.util.shader.base.RiseShader;
import com.alan.clients.util.shader.base.ShaderRenderType;
import java.util.List;
import net.minecraft.client.renderer.GlStateManager;
import net.minecraft.client.renderer.RenderHelper;
import net.minecraft.client.renderer.entity.RendererLivingEntity;
import net.minecraft.client.shader.Framebuffer;
import org.lwjgl.opengl.Display;

public class OutlineShader extends RiseShader {
    private final RiseShaderProgram shaderProgram = new RiseShaderProgram("outline.frag", "vertex.vsh");
    private Framebuffer inputFramebuffer = new Framebuffer(aEg.displayWidth, aEg.displayHeight, true);

    public OutlineShader() {
    }

    @Override
    public void a(ShaderRenderType var1, float var2, List<Runnable> runnables) {
        if (Display.isVisible()) {
            switch (var1) {
                case CAMERA:
                    this.update();
                    this.setActive(!runnables.isEmpty());
                    if (this.isActive()) {
                        RendererLivingEntity.NAME_TAG_RANGE = 0.0F;
                        RendererLivingEntity.NAME_TAG_RANGE_SNEAK = 0.0F;
                        this.inputFramebuffer.bindFramebuffer(true);
                        runnables.forEach(Runnable::run);
                        aEg.getFramebuffer().bindFramebuffer(true);
                        RendererLivingEntity.NAME_TAG_RANGE = 64.0F;
                        RendererLivingEntity.NAME_TAG_RANGE_SNEAK = 32.0F;
                        RenderHelper.disableStandardItemLighting();
                                }
                    break;
                case OVERLAY:
                    this.setActive(this.isActive() || !runnables.isEmpty());
                    if (this.isActive()) {
                        this.inputFramebuffer.bindFramebuffer(true);
                        runnables.forEach(Runnable::run);
                        int i = this.shaderProgram.getProgramId();
                        aEg.getFramebuffer().bindFramebuffer(true);
                        this.shaderProgram.rt();
                        ShaderUniforms.uniform1i(i, "u_texture", 0);
                        ShaderUniforms.uniform1f(i, "u_radius", 1.0F);
                        ShaderUniforms.uniform2f(i, "u_texel_size", 1.0F / aEg.displayWidth, 1.0F / aEg.displayHeight);
                        GlStateManager.enableBlend();
                        GlStateManager.tryBlendFuncSeparate(770, 771, 1, 0);
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
        this.setActive(false);
        if (aEg.displayWidth == this.inputFramebuffer.framebufferWidth && aEg.displayHeight == this.inputFramebuffer.framebufferHeight) {
            this.inputFramebuffer.framebufferClear();
        } else {
            this.inputFramebuffer.deleteFramebuffer();
            this.inputFramebuffer = new Framebuffer(aEg.displayWidth, aEg.displayHeight, true);
        }
    }
}
