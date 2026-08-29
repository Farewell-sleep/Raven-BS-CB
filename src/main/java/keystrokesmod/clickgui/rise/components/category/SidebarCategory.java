package keystrokesmod.clickgui.rise.components.category;

import keystrokesmod.Raven;
import keystrokesmod.clickgui.rise.RiseClickGUI;
import keystrokesmod.clickgui.rise.UIColors;
import keystrokesmod.clickgui.rise.util.Animation;
import keystrokesmod.clickgui.rise.util.ColorUtil;
import keystrokesmod.clickgui.rise.util.Easing;
import keystrokesmod.clickgui.rise.util.GUIUtil;
import keystrokesmod.clickgui.rise.util.RenderUtil;
import keystrokesmod.module.Module;
import keystrokesmod.module.impl.client.Gui;
import keystrokesmod.utility.font.RavenFontRenderer;

import java.awt.Color;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.stream.Collectors;

public final class SidebarCategory {
    private final List<CategoryComponent> categories;
    public double aym = 100.0;
    private double alpha = 0;
    private double hoverAlpha = 0;
    private boolean expanded = false;
    private long lastTime = 0;
    private Animation animation = new Animation(Easing.EASE_OUT_EXPO, 300);

    public SidebarCategory() {
        List<Module.category> cats = new ArrayList<>();
        for (Module.category cat : Module.category.values()) {
            if (!Raven.moduleManager.inCategory(cat).isEmpty()) cats.add(cat);
        }
        this.categories = cats.stream().map(CategoryComponent::new).collect(Collectors.toList());
    }

    public void pF() {
        RiseClickGUI gui = RiseClickGUI.instance;
        if (gui == null) return;
        Color color = UIColors.SECONDARY.Y((int) alpha);
        animation.Q(expanded ? 0 : -aym / 1.5);
        double animVal = animation.getValue();
        RenderUtil.roundedRectangle(gui.axI.x, gui.axI.y, aym + animVal, gui.position.y, gui.round, color);
        // Square off right edge
        RenderUtil.roundedRectangle(gui.axI.x + aym + animVal - gui.round, gui.axI.y, gui.round, gui.position.y, 0, color);
    }

    public void renderSidebar(float mouseX, float mouseY) {
        RiseClickGUI gui = RiseClickGUI.instance;
        if (gui == null) return;
        long now = System.currentTimeMillis();
        if (lastTime == 0) lastTime = now;

        boolean inSidebar = GUIUtil.c(gui.axI.x - 200, gui.axI.y, 310, gui.position.y, mouseX, mouseY);
        expanded = inSidebar;
        if (inSidebar) {
            alpha = Math.min(alpha + (now - lastTime) * 2L, 255.0);
        } else {
            alpha = Math.max(alpha - (float) (now - lastTime) * 1.5F, 0.0);
        }

        if (GUIUtil.c(gui.axI.x, gui.axI.y, hoverAlpha > 0 ? 70 : 10, gui.position.y, mouseX, mouseY)) {
            hoverAlpha = Math.min(hoverAlpha + (now - lastTime) * 2L, 255.0);
        } else {
            hoverAlpha = Math.max(hoverAlpha - (now - lastTime), 0.0);
        }
        lastTime = now;

        // Client name
        if (alpha > 50) {
            RavenFontRenderer hf = Gui.getClickGuiHeaderFontRenderer();
            RavenFontRenderer sf = Gui.getClickGuiSettingFontRenderer();
            float fx = gui.axI.x + 9;
            float fy = gui.axI.y + 24.75f - hf.getFontHeight() / 2f;
            hf.drawStringWithShadow("Raven", fx + 5, fy + 2, ColorUtil.withAlpha(Color.WHITE, (int) alpha).getRGB());
            sf.drawStringWithShadow("BS", fx + 5 + hf.getStringWidth("Raven "), fy,
                    ColorUtil.withAlpha(new Color(96, 165, 250), (int) Math.min(alpha, 200)).getRGB());
        }

        // Categories
        double itemY = 50;
        for (CategoryComponent cc : categories) {
            cc.render(gui.axI.y + itemY, aym + animation.getValue(), (int) alpha, null);
            itemY += 22;
        }
    }

    public void preRenderClickGUI() {
        for (CategoryComponent cc : categories) cc.bloom((int) alpha);
    }

    public void clickSidebar(float mouseX, float mouseY, int button) {
        if (alpha > 0) for (CategoryComponent cc : categories) cc.click((int) mouseX, (int) mouseY, button);
    }

    public void pE() {
        if (alpha > 0) for (CategoryComponent cc : categories) cc.release();
    }
}
