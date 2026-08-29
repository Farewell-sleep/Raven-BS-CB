package keystrokesmod.utility.render;

import keystrokesmod.module.impl.render.ClientTheme;
import keystrokesmod.utility.shader.ShaderUtils;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.GuiScreen;
import net.minecraft.client.renderer.GlStateManager;

public class MainMenuShaderRenderer {
    private static ShaderUtils shader;
    private static int currentShaderMode = -1;
    private static final long startTime = System.currentTimeMillis();

    public static void renderBackground(GuiScreen gui) {
        final int width = gui.width;
        final int height = gui.height;
        if (width <= 0 || height <= 0) return;

        Minecraft mc = Minecraft.getMinecraft();
        if (mc == null) return;

        int shaderMode = 3;
        if (ClientTheme.backgroundMode != null) {
            shaderMode = (int) ClientTheme.backgroundMode.getInput();
        }

        if (shader == null || currentShaderMode != shaderMode) {
            if (shader != null) {
                try { shader.unload(); } catch (Exception ignored) {}
                shader = null;
            }
            String shaderPath;
            switch (shaderMode) {
                case 0: shaderPath = "keystrokesmod:shaders/main_menu_background_flow.frag"; break;
                case 1: shaderPath = "keystrokesmod:shaders/main_menu_background_rise.frag"; break;
                case 2: shaderPath = "keystrokesmod:shaders/main_menu_background_nexus.frag"; break;
                case 3: default: shaderPath = "keystrokesmod:shaders/main_menu_background_aurora.frag"; break;
            }
            try {
                shader = new ShaderUtils(shaderPath, "keystrokesmod:shaders/vertex.vsh");
                currentShaderMode = shaderMode;
            } catch (Exception e) {
                e.printStackTrace();
                shader = null;
                currentShaderMode = -1;
                return;
            }
        }

        if (shader == null) return;

        float time = (System.currentTimeMillis() - startTime) / 1000f;

        try {
            GlStateManager.disableAlpha();
            GlStateManager.disableTexture2D();
            GlStateManager.enableBlend();
            GlStateManager.tryBlendFuncSeparate(770, 771, 1, 0);
            GlStateManager.disableDepth();
            GlStateManager.depthMask(false);

            shader.init();
            shader.setUniformf("resolution", (float) mc.displayWidth, (float) mc.displayHeight);
            shader.setUniformf("time", time);
            ShaderUtils.drawQuads(0, 0, width, height);
            shader.unload();

            GlStateManager.depthMask(true);
            GlStateManager.enableDepth();
            GlStateManager.disableBlend();
            GlStateManager.enableTexture2D();
            GlStateManager.enableAlpha();
            GlStateManager.resetColor();
        } catch (Exception e) {
            e.printStackTrace();
            shader = null;
            currentShaderMode = -1;
            try {
                GlStateManager.resetColor();
                GlStateManager.enableTexture2D();
                GlStateManager.enableAlpha();
            } catch (Exception ignored) {}
        }
    }
}
