package com.alan.clients.util.shader.impl;

import com.alan.clients.module.impl.render.Interface;
import com.alan.clients.util.shader.base.RiseShaderProgram;
import com.alan.clients.util.shader.base.ShaderUniforms;
import com.alan.clients.util.shader.base.RiseShader;
import com.alan.clients.util.shader.base.ShaderRenderType;
import com.alan.clients.util.shader.kernel.GaussianKernel;
import java.nio.FloatBuffer;
import java.util.List;
import lombok.Generated;
import net.minecraft.client.renderer.GlStateManager;
import net.minecraft.client.shader.Framebuffer;
import org.lwjgl.BufferUtils;
import org.lwjgl.opengl.Display;
import org.lwjgl.opengl.GL13;

public class GaussianBlurShader extends RiseShader {
    private final RiseShaderProgram blurProgram = new RiseShaderProgram("blur.frag", "vertex.vsh");
    private Framebuffer inputFramebuffer = new Framebuffer(aEg.displayWidth, aEg.displayHeight, true);
    private Framebuffer outputFramebuffer = new Framebuffer(aEg.displayWidth, aEg.displayHeight, true);
    private GaussianKernel gaussianKernel = new GaussianKernel(0);
    public static final int aQd = 12;
    public static final float aQe = 3.0F;
    int aQf = 12;
    float aQg = 3.0F;
    private Interface amf;

    public GaussianBlurShader() {
    }

    @Override
    public void a(ShaderRenderType var1, float var2, List<Runnable> runnables) {
        if (Display.isVisible()) {
            if (this.amf == null) {
                this.amf = this.e(Interface.class);
            }

            int i = this.amf != null ? this.amf.getBlurRadius() : 12;
            float f = this.amf != null ? this.amf.getBlurCompression() : 3.0F;
            boolean flag = System.getProperty("os.name").toLowerCase().contains("mac");
            switch (var1) {
                case CAMERA:
                    this.inputFramebuffer.bindFramebuffer(true);
                    runnables.forEach(Runnable::run);
                    aEg.getFramebuffer().bindFramebuffer(true);
                    break;
                case OVERLAY:
                    this.inputFramebuffer.bindFramebuffer(true);
                    runnables.forEach(Runnable::run);
                    int j = this.blurProgram.getProgramId();
                    this.outputFramebuffer.bindFramebuffer(true);
                    this.blurProgram.rt();
                    if (this.gaussianKernel.getSize() != i) {
                        this.gaussianKernel = new GaussianKernel(i);
                        this.gaussianKernel.uR();
                        FloatBuffer floatbuffer = BufferUtils.createFloatBuffer(i);
                        floatbuffer.put(this.gaussianKernel.getKernel());
                        floatbuffer.flip();
                        ShaderUniforms.uniform1f(j, "u_radius", (float)i);
                        ShaderUniforms.uniformFB(j, "u_kernel", floatbuffer);
                        ShaderUniforms.uniform1i(j, "u_diffuse_sampler", 0);
                        ShaderUniforms.uniform1i(j, "u_other_sampler", flag ? 2 : 20);
                    }

                    ShaderUniforms.uniform2f(j, "u_texel_size", 1.0F / aEg.displayWidth, 1.0F / aEg.displayHeight);
                    ShaderUniforms.uniform2f(j, "u_direction", f, 0.0F);
                    GlStateManager.enableBlend();
                    GlStateManager.tryBlendFuncSeparate(770, 771, 1, 0);
                    GlStateManager.alphaFunc(516, 0.0F);
                    aEg.getFramebuffer().bindFramebufferTexture();
                    RiseShaderProgram.vN();
                    aEg.getFramebuffer().bindFramebuffer(true);
                    ShaderUniforms.uniform2f(j, "u_direction", 0.0F, f);
                    this.outputFramebuffer.bindFramebufferTexture();
                    GL13.glActiveTexture(flag ? 33986 : 34004);
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
        this.setActive(false);
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

    @Generated
    public void aF(int var1) {
        this.aQf = var1;
    }

    @Generated
    public int vQ() {
        return this.aQf;
    }

    @Generated
    public float vR() {
        return this.aQg;
    }

    @Generated
    public void I(float var1) {
        this.aQg = var1;
    }
}
