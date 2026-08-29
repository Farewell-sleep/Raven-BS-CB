package com.alan.clients.util.shader.impl;

import com.alan.clients.module.impl.render.Interface;
import com.alan.clients.util.shader.base.RiseShaderProgram;
import com.alan.clients.util.shader.base.ShaderUniforms;
import com.alan.clients.util.shader.base.RiseShader;
import com.alan.clients.util.shader.base.ShaderRenderType;
import com.alan.clients.util.shader.kernel.GaussianKernel;
import java.nio.FloatBuffer;
import java.util.List;
import net.minecraft.client.renderer.GlStateManager;
import net.minecraft.client.renderer.RenderHelper;
import net.minecraft.client.renderer.entity.RendererLivingEntity;
import net.minecraft.client.shader.Framebuffer;
import org.lwjgl.BufferUtils;
import org.lwjgl.opengl.Display;
import org.lwjgl.opengl.GL13;

public class BloomShader extends RiseShader {
    private final RiseShaderProgram bloomProgram = new RiseShaderProgram("bloom.frag", "vertex.vsh");
    private Framebuffer inputFramebuffer = new Framebuffer(aEg.displayWidth, aEg.displayHeight, true);
    private Framebuffer outputFramebuffer = new Framebuffer(aEg.displayWidth, aEg.displayHeight, true);
    private GaussianKernel gaussianKernel = new GaussianKernel(0);
    private Interface amf;

    public BloomShader() {
    }

    @Override
    public void a(ShaderRenderType var1, float var2, List<Runnable> runnables) {
        if (Display.isVisible()) {
            boolean flag = System.getProperty("os.name").toLowerCase().contains("mac");
            switch (var1) {
                case CAMERA:
                    RendererLivingEntity.NAME_TAG_RANGE = 0.0F;
                    RendererLivingEntity.NAME_TAG_RANGE_SNEAK = 0.0F;
                    this.inputFramebuffer.bindFramebuffer(true);

                    for (Runnable runnable : runnables) {
                        runnable.run();
                    }

                    aEg.getFramebuffer().bindFramebuffer(true);
                    RendererLivingEntity.NAME_TAG_RANGE = 64.0F;
                    RendererLivingEntity.NAME_TAG_RANGE_SNEAK = 32.0F;
                    RenderHelper.disableStandardItemLighting();
                            break;
                case OVERLAY:
                    this.inputFramebuffer.bindFramebuffer(true);

                    for (Runnable runnable1 : runnables) {
                        runnable1.run();
                    }

                    if (this.amf == null) {
                        this.amf = this.e(Interface.class);
                    }

                    int i = this.amf != null ? this.amf.getBloomRadius() : 14;
                    float f = this.amf != null ? this.amf.getBloomCompression() : 2.0F;
                    int j = this.bloomProgram.getProgramId();
                    this.outputFramebuffer.bindFramebuffer(true);
                    this.bloomProgram.rt();
                    if (this.gaussianKernel.getSize() != i) {
                        this.gaussianKernel = new GaussianKernel(i);
                        this.gaussianKernel.uR();
                        FloatBuffer floatbuffer = BufferUtils.createFloatBuffer(i);
                        floatbuffer.put(this.gaussianKernel.getKernel());
                        floatbuffer.flip();
                        ShaderUniforms.uniform1f(j, "u_radius", (float)i);
                        ShaderUniforms.uniformFB(j, "u_kernel", floatbuffer);
                        ShaderUniforms.uniform1i(j, "u_diffuse_sampler", 0);
                        ShaderUniforms.uniform1i(j, "u_other_sampler", flag ? 3 : 16);
                    }

                    ShaderUniforms.uniform2f(j, "u_texel_size", 1.0F / aEg.displayWidth, 1.0F / aEg.displayHeight);
                    ShaderUniforms.uniform2f(j, "u_direction", f, 0.0F);
                    GlStateManager.enableBlend();
                    GlStateManager.blendFunc(1, 770);
                    GlStateManager.alphaFunc(516, 0.0F);
                    this.inputFramebuffer.bindFramebufferTexture();
                    RiseShaderProgram.vN();
                    aEg.getFramebuffer().bindFramebuffer(true);
                    GlStateManager.blendFunc(770, 771);
                    ShaderUniforms.uniform2f(j, "u_direction", 0.0F, f);
                    this.outputFramebuffer.bindFramebufferTexture();
                    GL13.glActiveTexture(flag ? 33987 : 34000);
                    this.inputFramebuffer.bindFramebufferTexture();
                    GL13.glActiveTexture(33984);
                    RiseShaderProgram.vN();
                    GlStateManager.disableBlend();
                    RiseShaderProgram.stop();
            }
        }
    }

    @Override
    public void update() {
        int i = aEg.displayWidth;
        int j = aEg.displayHeight;
        if (this.inputFramebuffer.framebufferWidth != i || this.inputFramebuffer.framebufferHeight != j) {
            this.inputFramebuffer.deleteFramebuffer();
            this.inputFramebuffer = new Framebuffer(aEg.displayWidth, aEg.displayHeight, true);
        } else {
            this.inputFramebuffer.framebufferClear();
        }

        if (this.outputFramebuffer.framebufferWidth != i || this.outputFramebuffer.framebufferHeight != j) {
            this.outputFramebuffer.deleteFramebuffer();
            this.outputFramebuffer = new Framebuffer(aEg.displayWidth, aEg.displayHeight, true);
        } else {
            this.outputFramebuffer.framebufferClear();
        }

        this.inputFramebuffer.setFramebufferColor(0.0F, 0.0F, 0.0F, 0.0F);
        this.outputFramebuffer.setFramebufferColor(0.0F, 0.0F, 0.0F, 0.0F);
    }
}
