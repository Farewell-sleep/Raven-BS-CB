package keystrokesmod.clickgui.rise.components.value.impl;

import keystrokesmod.clickgui.rise.components.value.ValueComponent;
import keystrokesmod.clickgui.rise.util.ColorUtil;
import keystrokesmod.clickgui.rise.util.GUIUtil;
import keystrokesmod.clickgui.rise.util.RenderUtil;
import keystrokesmod.clickgui.rise.util.Vector2d;
import keystrokesmod.module.setting.impl.ButtonSetting;
import keystrokesmod.module.impl.client.Gui;
import keystrokesmod.utility.font.RavenFontRenderer;

import java.awt.Color;

public class BooleanValueComponent extends ValueComponent {
    private final ButtonSetting setting;

    public BooleanValueComponent(ButtonSetting setting) {
        this.setting = setting;
        this.height = 18;
    }

    @Override
    public void draw(Vector2d position, int mouseX, int mouseY, float partialTicks) {
        this.position = position;
        RavenFontRenderer sf = Gui.getClickGuiSettingFontRenderer();
        sf.drawStringWithShadow(setting.getName(), (float) position.x, (float) position.y + 2,
                ColorUtil.withAlpha(Color.WHITE, 130).getRGB());

        float sw = 22, sh = 10;
        float sx = (float) (position.x + 283 - 28 - sw);
        float sy = (float) position.y + 3;
        RenderUtil.roundedRectangle(sx, sy, sw, sh, sh / 2f,
                setting.isToggled() ? new Color(96, 165, 250) : new Color(60, 60, 75));
        float kx = setting.isToggled() ? sx + sw - sh + 1 : sx + 1;
        RenderUtil.c(kx + sh / 2f - 1, sy + sh / 2f, sh / 2f - 1.5f, Color.WHITE);
    }

    @Override
    public void click(int mouseX, int mouseY, int mouseButton) {
        if (position != null && GUIUtil.c(position.x, position.y, 283, height, mouseX, mouseY)) {
            setting.toggle();
        }
    }

    @Override
    public void release() {}
    @Override
    public void key(char typedChar, int keyCode) {}
}
