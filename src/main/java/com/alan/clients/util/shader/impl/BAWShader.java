package com.alan.clients.util.shader.impl;

import com.alan.clients.util.shader.base.RiseShader;
import com.alan.clients.util.shader.base.RiseShaderProgram;
import com.alan.clients.util.shader.base.ShaderRenderType;
import java.util.List;
import net.minecraft.client.gui.ScaledResolution;
import net.minecraft.client.renderer.GlStateManager;
import net.minecraft.client.shader.Framebuffer;
import org.lwjgl.opengl.Display;

public class BAWShader extends RiseShader {
    private final RiseShaderProgram aPW = new RiseShaderProgram("baw.frag", "vertex.vsh");
    private Framebuffer aPX = new Framebuffer(aEg.displayWidth, aEg.displayHeight, true);

    public BAWShader() {
    }

    @Override
    public void a(ShaderRenderType var1, float var2, List<Runnable> runnables) {
        if (Display.isVisible()) {
            if (var1 == ShaderRenderType.OVERLAY) {
                this.update();
                this.aPW.getProgramId();
                new ScaledResolution(aEg);
                GlStateManager.enableBlend();
                GlStateManager.blendFunc(770, 771);
                GlStateManager.disableAlpha();
                aEg.getFramebuffer().bindFramebuffer(true);
                this.aPW.rt();
                RiseShaderProgram.vN();
                RiseShaderProgram.stop();
            }
        }
    }

    @Override
    public void update() {
        this.setActive(true);
        if (aEg.displayWidth == this.aPX.framebufferWidth && aEg.displayHeight == this.aPX.framebufferHeight) {
            this.aPX.framebufferClear();
        } else {
            this.aPX.deleteFramebuffer();
            this.aPX = new Framebuffer(aEg.displayWidth, aEg.displayHeight, true);
        }
    }
}
