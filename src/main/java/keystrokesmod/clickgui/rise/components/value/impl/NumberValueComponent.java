package keystrokesmod.clickgui.rise.components.value.impl;

import keystrokesmod.clickgui.rise.components.value.ValueComponent;
import keystrokesmod.clickgui.rise.util.ColorUtil;
import keystrokesmod.clickgui.rise.util.GUIUtil;
import keystrokesmod.clickgui.rise.util.RenderUtil;
import keystrokesmod.clickgui.rise.util.Vector2d;
import keystrokesmod.module.impl.client.Gui;
import keystrokesmod.module.setting.impl.SliderSetting;
import keystrokesmod.utility.font.RavenFontRenderer;

import java.awt.Color;

public class NumberValueComponent extends ValueComponent {
    private final SliderSetting setting;
    private boolean dragging = false;

    public NumberValueComponent(SliderSetting setting) {
        this.setting = setting;
        this.height = 26;
    }

    @Override
    public void draw(Vector2d position, int mouseX, int mouseY, float partialTicks) {
        this.position = position;
        RavenFontRenderer sf = Gui.getClickGuiSettingFontRenderer();

        if (setting.getOptions() != null && setting.getOptions().length > 0) {
            // Mode selector
            String cur = setting.getOptions()[(int) setting.getInput()];
            sf.drawStringWithShadow(setting.getName(), (float) position.x, (float) position.y + 2,
                    ColorUtil.withAlpha(Color.WHITE, 130).getRGB());
            sf.drawStringWithShadow(cur, (float) (position.x + 283 - 28 - sf.getStringWidth(cur)),
                    (float) position.y + 2, Color.WHITE.getRGB());
            this.height = 18;
        } else {
            // Slider
            sf.drawStringWithShadow(setting.getName(), (float) position.x, (float) position.y,
                    ColorUtil.withAlpha(Color.WHITE, 130).getRGB());
            String val = String.format("%.1f", setting.getInput());
            sf.drawStringWithShadow(val, (float) (position.x + 283 - 28 - sf.getStringWidth(val)),
                    (float) position.y, Color.WHITE.getRGB());

            float x = (float) position.x;
            float y = (float) position.y + 11;
            float w = 283 - 28;
            float h = 3;
            RenderUtil.roundedRectangle(x, y, w, h, 2, new Color(42, 44, 55));
            float pct = (float) ((setting.getInput() - setting.getMin()) / (setting.getMax() - setting.getMin()));
            RenderUtil.roundedRectangle(x, y, w * pct, h, 2, new Color(96, 165, 250));
            RenderUtil.c(x + w * pct, y + 1.5f, 4, new Color(96, 165, 250));

            if (dragging) {
                float npct = Math.max(0, Math.min(1, (mouseX - x) / w));
                setting.setValue(setting.getMin() + npct * (setting.getMax() - setting.getMin()));
            }
            this.height = 26;
        }
    }

    @Override
    public void click(int mouseX, int mouseY, int mouseButton) {
        if (position == null) return;
        if (setting.getOptions() != null && setting.getOptions().length > 0) {
            if (GUIUtil.c(position.x, position.y, 283, height, mouseX, mouseY)) {
                int idx = (int) setting.getInput();
                setting.setValue((idx + 1) % setting.getOptions().length);
            }
        } else {
            float x = (float) position.x;
            float y = (float) position.y + 11;
            float w = 283 - 28;
            if (mouseX >= x && mouseX <= x + w && mouseY >= y - 6 && mouseY <= y + 8) {
                dragging = true;
                float pct = Math.max(0, Math.min(1, (mouseX - x) / w));
                setting.setValue(setting.getMin() + pct * (setting.getMax() - setting.getMin()));
            }
        }
    }

    @Override
    public void release() { dragging = false; }
    @Override
    public void key(char typedChar, int keyCode) {}
    public void pU() {}
}
