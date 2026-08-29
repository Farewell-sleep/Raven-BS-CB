package keystrokesmod.mixin.impl.gui;

import keystrokesmod.module.ModuleManager;
import keystrokesmod.module.impl.render.ClientTheme;
import keystrokesmod.utility.font.FontManager;
import keystrokesmod.utility.font.RavenFontRenderer;
import keystrokesmod.utility.render.MainMenuShaderRenderer;
import net.minecraft.client.gui.GuiButton;
import net.minecraft.client.gui.GuiMainMenu;
import net.minecraft.client.gui.GuiScreen;
import net.minecraft.client.renderer.GlStateManager;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

import java.awt.Color;

@Mixin(value = GuiMainMenu.class, priority = 1983)
public abstract class MixinGuiMainMenu extends GuiScreen {

    @Inject(method = "drawScreen", at = @At("HEAD"), cancellable = true)
    public void onDrawScreen(int mouseX, int mouseY, float partialTicks, CallbackInfo ci) {
        if (ModuleManager.clientTheme == null || !ModuleManager.clientTheme.isEnabled()
                || ClientTheme.mainMenu == null || !ClientTheme.mainMenu.isToggled()) {
            return;
        }

        MainMenuShaderRenderer.renderBackground(this);

        int buttonStartY = this.height / 2 - 40;
        int titleY = buttonStartY - 70;
        RavenFontRenderer titleFont = FontManager.getFontRenderer("Tenacity", 40);

        int tw = titleFont.getStringWidth("Raven BS");
        titleFont.drawStringWithShadow("Raven BS", this.width / 2f - tw / 2f, titleY,
                new Color(255, 255, 255, 200).getRGB());

        super.drawScreen(mouseX, mouseY, partialTicks);
        ci.cancel();
    }

    @Inject(method = "initGui", at = @At("HEAD"), cancellable = true)
    public void onInitGui(CallbackInfo ci) {
        if (ModuleManager.clientTheme == null || !ModuleManager.clientTheme.isEnabled()
                || ClientTheme.mainMenu == null || !ClientTheme.mainMenu.isToggled()) {
            return;
        }

        int buttonStartY = this.height / 2 - 40;
        int buttonWidth = 200;
        int buttonHeight = 20;
        int buttonSpacing = 24;

        this.buttonList.add(new GuiButton(1, this.width / 2 - buttonWidth / 2, buttonStartY, buttonWidth, buttonHeight, "Singleplayer"));
        this.buttonList.add(new GuiButton(2, this.width / 2 - buttonWidth / 2, buttonStartY + buttonSpacing, buttonWidth, buttonHeight, "Multiplayer"));

        int splitButtonWidth = 98;
        this.buttonList.add(new GuiButton(0, this.width / 2 - splitButtonWidth - 1, buttonStartY + buttonSpacing * 2, splitButtonWidth, buttonHeight, "Options"));
        this.buttonList.add(new GuiButton(4, this.width / 2 + 1, buttonStartY + buttonSpacing * 2, splitButtonWidth, buttonHeight, "Quit Game"));

        this.mc.setConnectedToRealms(false);
        ci.cancel();
    }
}
