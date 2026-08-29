package keystrokesmod.clickgui.rise.components.category;

import keystrokesmod.clickgui.rise.RiseClickGUI;
import keystrokesmod.clickgui.rise.UIColors;
import keystrokesmod.clickgui.rise.util.ColorUtil;
import keystrokesmod.clickgui.rise.util.GUIUtil;
import keystrokesmod.clickgui.rise.util.RenderUtil;
import keystrokesmod.module.Module;
import keystrokesmod.module.impl.client.Gui;
import keystrokesmod.utility.font.RavenFontRenderer;

import java.awt.Color;

public class CategoryComponent {
    private final Module.category category;
    private float x, y;

    public CategoryComponent(Module.category category) {
        this.category = category;
    }

    public void render(double y, double sidebarWidth, int alpha, Object currentScreen) {
        RiseClickGUI gui = RiseClickGUI.instance;
        if (gui == null) return;
        this.y = (float) y;
        this.x = gui.axI.x + 6;

        boolean active = gui.selectedCat == category;
        boolean hovered = GUIUtil.c(this.x - 6, this.y, (float) sidebarWidth - 12, 20, gui.mouse != null ? (int) gui.mouse.x : 0, gui.mouse != null ? (int) gui.mouse.y : 0);

        if (active) {
            RenderUtil.roundedRectangle(this.x - 6, this.y, (float) sidebarWidth - 12, 20, 6, new Color(255, 255, 255, 20));
            RenderUtil.roundedRectangle(this.x - 12, this.y + 3, 3, 14, 2, new Color(96, 165, 250));
        } else if (hovered) {
            RenderUtil.roundedRectangle(this.x - 6, this.y, (float) sidebarWidth - 12, 20, 6, new Color(255, 255, 255, 8));
        }

        RavenFontRenderer sf = Gui.getClickGuiSettingFontRenderer();
        String name = catName(category);
        int textColor = active ? Color.WHITE.getRGB() : (hovered ? Color.WHITE.getRGB() : ColorUtil.withAlpha(Color.WHITE, 130).getRGB());
        sf.drawStringWithShadow(name, this.x + 4, this.y + 6, ColorUtil.withAlpha(new Color(textColor), alpha).getRGB());
    }

    public void click(int mouseX, int mouseY, int button) {
        RiseClickGUI gui = RiseClickGUI.instance;
        if (gui == null) return;
        if (GUIUtil.c(this.x - 6, this.y, 100, 20, mouseX, mouseY)) {
            gui.selectedCat = (gui.selectedCat == category) ? null : category;
            gui.scroll = 0;
        }
    }

    public void bloom(int alpha) {}
    public void release() {}

    private static String catName(Module.category c) {
        return c.name().substring(0, 1).toUpperCase() + c.name().substring(1).toLowerCase();
    }
}
