package keystrokesmod.mixin.impl.gui;

import keystrokesmod.module.ModuleManager;
import keystrokesmod.module.impl.render.ClientTheme;
import keystrokesmod.utility.font.FontManager;
import keystrokesmod.utility.font.RavenFontRenderer;
import keystrokesmod.utility.shader.RoundedUtils;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.Gui;
import net.minecraft.client.gui.GuiButton;
import net.minecraft.client.renderer.GlStateManager;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

import java.awt.Color;

@Mixin(GuiButton.class)
public abstract class MixinGuiButton extends Gui {

    @Shadow public boolean visible;
    @Shadow protected boolean hovered;
    @Shadow public int xPosition;
    @Shadow public int yPosition;
    @Shadow public int width;
    @Shadow public int height;
    @Shadow public boolean enabled;
    @Shadow public String displayString;

    @Shadow protected abstract void mouseDragged(Minecraft p_mouseDragged_1_, int p_mouseDragged_2_, int p_mouseDragged_3_);

    private int hoverValue = 102;

    @Inject(method = "drawButton", at = @At("HEAD"), cancellable = true)
    public void onDrawButton(Minecraft minecraft, int mouseX, int mouseY, CallbackInfo ci) {
        if (ModuleManager.clientTheme == null || !ModuleManager.clientTheme.isEnabled()
                || ClientTheme.button == null || !ClientTheme.button.isToggled()) {
            return;
        }

        if (!this.visible) {
            ci.cancel();
            return;
        }

        this.hovered = mouseX >= this.xPosition && mouseY >= this.yPosition
                && mouseX < this.xPosition + this.width && mouseY < this.yPosition + this.height;

        // Smooth hover animation
        int fps = Minecraft.getDebugFPS();
        if (fps <= 0) fps = 60;
        fps = Math.max(10, Math.min(240, fps));
        double step = Math.min(4.0 * 150.0 / fps, 10.0);
        if (hovered) {
            hoverValue = (int) Math.min(hoverValue + step, 200);
        } else {
            hoverValue = (int) Math.max(hoverValue - step, 102);
        }

        float radius = 3.5f;

        // Shadow
        RoundedUtils.drawRound(xPosition - 2, yPosition - 2, width + 4, height + 4, radius + 1,
                new Color(0, 0, 0, 50));

        // Button background - dark with hover alpha
        Color bg = new Color(35, 37, 43, hoverValue);
        RoundedUtils.drawRound(xPosition, yPosition, width, height, radius, bg);

        // Border
        RoundedUtils.drawRound(xPosition, yPosition, width, 1, radius, new Color(255, 255, 255, 15));
        RoundedUtils.drawRound(xPosition, yPosition + height - 1, width, 1, radius, new Color(0, 0, 0, 80));

        this.mouseDragged(minecraft, mouseX, mouseY);

        // Text
        RavenFontRenderer font;
        if (ClientTheme.smoothFont != null && ClientTheme.smoothFont.isToggled()) {
            font = FontManager.getFontRenderer("Tenacity", 10);
        } else {
            font = FontManager.getFontRenderer("Minecraft", 20);
        }

        int textColor = this.enabled ? (this.hovered ? 0xFFFFA0 : 0xFFFFFF) : 0xA0A0A0;
        int tw = font.getStringWidth(displayString);
        font.drawStringWithShadow(displayString,
                xPosition + (width - tw) / 2f,
                yPosition + (height - font.getFontHeight()) / 2f,
                textColor);

        GlStateManager.color(1, 1, 1, 1);
        ci.cancel();
    }
}
