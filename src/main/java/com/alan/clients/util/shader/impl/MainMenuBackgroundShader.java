package com.alan.clients.util.shader.impl;

import com.alan.clients.util.shader.base.RiseShader;
import com.alan.clients.util.shader.base.RiseShaderProgram;
import com.alan.clients.util.shader.base.ShaderRenderType;
import com.alan.clients.util.shader.base.ShaderUniforms;
import java.util.List;
import net.minecraft.client.gui.ScaledResolution;
import net.minecraft.client.renderer.GlStateManager;
import net.minecraft.client.shader.Framebuffer;
import org.lwjgl.opengl.Display;

public class MainMenuBackgroundShader extends RiseShader {
    private final RiseShaderProgram program = new RiseShaderProgram("main_menu/background.frag", "vertex.vsh");
    private Framebuffer tempFBO = new Framebuffer(aEg.displayWidth, aEg.displayHeight, true);

    public MainMenuBackgroundShader() {
    }

    @Override
    public void a(ShaderRenderType var1, float var2, List<Runnable> runnables) {
        if (Display.isVisible()) {
            if (var1 == ShaderRenderType.OVERLAY) {
                this.update();
                int i = this.program.getProgramId();
                new ScaledResolution(aEg);
                GlStateManager.enableBlend();
                GlStateManager.blendFunc(770, 771);
                GlStateManager.disableAlpha();
                aEg.getFramebuffer().bindFramebuffer(true);
                this.program.rt();
                ShaderUniforms.uniform2f(i, "resolution", (float)aEg.displayWidth, (float)aEg.displayHeight);
                ShaderUniforms.uniform1f(i, "time", (float)(System.currentTimeMillis() - aEg.getSystemTime()) / 1000.0F);
                RiseShaderProgram.vN();
                RiseShaderProgram.stop();
            }
        }
    }

    @Override
    public void update() {
        this.setActive(true);
        if (aEg.displayWidth == this.tempFBO.framebufferWidth && aEg.displayHeight == this.tempFBO.framebufferHeight) {
            this.tempFBO.framebufferClear();
        } else {
            this.tempFBO.deleteFramebuffer();
            this.tempFBO = new Framebuffer(aEg.displayWidth, aEg.displayHeight, true);
        }
    }
}
