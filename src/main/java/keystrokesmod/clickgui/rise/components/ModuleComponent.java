package keystrokesmod.clickgui.rise.components;

import keystrokesmod.clickgui.rise.RiseClickGUI;
import keystrokesmod.clickgui.rise.components.value.ValueComponent;
import keystrokesmod.clickgui.rise.components.value.impl.BooleanValueComponent;
import keystrokesmod.clickgui.rise.components.value.impl.NumberValueComponent;
import keystrokesmod.clickgui.rise.util.Animation;
import keystrokesmod.clickgui.rise.util.ColorUtil;
import keystrokesmod.clickgui.rise.util.Easing;
import keystrokesmod.clickgui.rise.util.GUIUtil;
import keystrokesmod.clickgui.rise.util.RenderUtil;
import keystrokesmod.clickgui.rise.util.Vector2d;
import keystrokesmod.clickgui.rise.util.Vector2f;
import keystrokesmod.module.Module;
import keystrokesmod.module.impl.client.Gui;
import keystrokesmod.module.setting.Setting;
import keystrokesmod.module.setting.impl.ButtonSetting;
import keystrokesmod.module.setting.impl.KeySetting;
import keystrokesmod.module.setting.impl.SliderSetting;
import keystrokesmod.utility.font.RavenFontRenderer;

import java.awt.Color;
import java.util.ArrayList;

public class ModuleComponent {
    public final Module module;
    public Vector2f scale = new Vector2f(283, 38);
    public boolean expanded;
    public ArrayList<ValueComponent> valueList = new ArrayList<>();
    public Vector2d position;
    public Animation hoverAnim = new Animation(Easing.LINEAR, 50);
    public Animation expandAnim = new Animation(Easing.EASE_OUT_EXPO, 300);
    private boolean mouseDown;

    public ModuleComponent(Module module) {
        this.module = module;
        for (Setting s : module.getSettings()) {
            if (s instanceof ButtonSetting) valueList.add(new BooleanValueComponent((ButtonSetting) s));
            else if (s instanceof SliderSetting) valueList.add(new NumberValueComponent((SliderSetting) s));
        }
    }

    public void draw(Vector2d position, int mouseX, int mouseY, float partialTicks) {
        this.position = position;
        RiseClickGUI gui = RiseClickGUI.instance;
        if (gui == null) return;

        boolean visible = position.y + scale.y >= gui.axI.y && position.y <= gui.axI.y + gui.position.y;
        if (!visible) return;

        RavenFontRenderer hf = Gui.getClickGuiHeaderFontRenderer();
        RavenFontRenderer sf = Gui.getClickGuiSettingFontRenderer();

        // Base card
        RenderUtil.roundedRectangle(position.x, position.y, scale.x, 38, 6, new Color(0, 0, 0, 50));

        // Hover
        boolean hovered = GUIUtil.c(position.x, position.y, scale.x, 38, mouseX, mouseY);
        hoverAnim.Q(hovered ? (mouseDown ? 35 : 20) : 0);
        double hoverVal = hoverAnim.getValue();
        if (hoverVal > 0.01) {
            RenderUtil.roundedRectangle(position.x, position.y, scale.x, 38, 6, new Color(0, 0, 0, (int) hoverVal));
        }

        // Title
        int titleColor = module.isEnabled() ? new Color(96, 165, 250).getRGB() : Color.WHITE.getRGB();
        hf.drawStringWithShadow(module.getName(), (float) position.x + 6, (float) position.y + 8, titleColor);

        // Description
        String desc = module.getInfoUpdate();
        if (desc == null || desc.isEmpty()) desc = module.moduleCategory().name();
        sf.drawStringWithShadow(desc, (float) position.x + 6, (float) position.y + 25,
                ColorUtil.withAlpha(Color.WHITE, 70).getRGB());

        // Expand animation
        float baseH = 38;
        float extraH = 0;
        for (ValueComponent vc : valueList) extraH += vc.getHeight();
        expandAnim.Q(expanded ? extraH : 0);
        double expandVal = expandAnim.getValue();
        scale.y = baseH + (float) expandVal;

        // Values
        if (expandVal > 0.5 && !valueList.isEmpty()) {
            double vy = position.y + baseH + 1;
            for (ValueComponent vc : valueList) {
                vc.draw(new Vector2d(position.x + 6, vy), mouseX, mouseY, partialTicks);
                vy += vc.getHeight();
            }
        }
    }

    public void click(int mouseX, int mouseY, int mouseButton) {
        if (position == null) return;
        RiseClickGUI gui = RiseClickGUI.instance;
        if (gui == null) return;
        if (position.y + scale.y < gui.axI.y || position.y > gui.axI.y + gui.position.y) return;

        if (GUIUtil.c(position.x, position.y, scale.x, 38, mouseX, mouseY) && gui.overlayPresent == null) {
            mouseDown = true;
            if (mouseButton == 0) {
                module.toggle();
            } else if (mouseButton == 1 && !valueList.isEmpty()) {
                expanded = !expanded;
            }
            return;
        }

        if (expanded) {
            for (ValueComponent vc : valueList) {
                vc.click(mouseX, mouseY, mouseButton);
            }
        }
    }

    public void release() {
        mouseDown = false;
        for (ValueComponent vc : valueList) vc.release();
    }

    public void key(char typedChar, int keyCode) {
        if (expanded) for (ValueComponent vc : valueList) vc.key(typedChar, keyCode);
    }

    public Module getModule() { return module; }
    public ArrayList<ValueComponent> getValueList() { return valueList; }
    public boolean isExpanded() { return expanded; }
}
