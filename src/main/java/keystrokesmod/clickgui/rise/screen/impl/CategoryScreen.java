package keystrokesmod.clickgui.rise.screen.impl;

import keystrokesmod.Raven;
import keystrokesmod.clickgui.rise.RiseClickGUI;
import keystrokesmod.clickgui.rise.components.ModuleComponent;
import keystrokesmod.clickgui.rise.screen.Screen;
import keystrokesmod.clickgui.rise.util.Vector2d;
import keystrokesmod.module.Module;

import java.util.ArrayList;
import java.util.stream.Collectors;

public final class CategoryScreen implements Screen {
    public ArrayList<ModuleComponent> relevantModules;
    public Module.category category;
    public float scroll = 0;
    public float scrollTarget = 0;

    public CategoryScreen() {}

    @Override
    public void onRender(int mouseX, int mouseY, float partialTicks) {
        RiseClickGUI gui = RiseClickGUI.instance;
        if (gui == null || category == null) return;

        // Smooth scroll
        scroll += (scrollTarget - scroll) * 0.2f;

        double d0 = gui.axI.y + 7 + scroll;
        double totalH = 0;
        for (ModuleComponent mc : relevantModules) {
            mc.draw(new Vector2d(gui.axI.x + gui.sidebar.aym + 8, d0), mouseX, mouseY, partialTicks);
            d0 += mc.scale.y + 7;
            totalH += mc.scale.y + 7;
        }

        float maxScroll = (float) Math.max(0, totalH - gui.position.y + 14);
        scrollTarget = Math.max(-maxScroll, Math.min(0, scrollTarget));
    }

    @Override
    public void onKey(char typedChar, int keyCode) {
        if (relevantModules != null) for (ModuleComponent mc : relevantModules) mc.key(typedChar, keyCode);
    }

    @Override
    public void f(int mouseX, int mouseY, int mouseButton) {
        if (relevantModules != null) for (ModuleComponent mc : relevantModules) mc.click(mouseX, mouseY, mouseButton);
    }

    @Override
    public void oG() {
        if (category != null && relevantModules != null) for (ModuleComponent mc : relevantModules) mc.release();
    }

    @Override
    public void pY() {
        if (category != null && relevantModules != null) for (ModuleComponent mc : relevantModules) {}
    }

    @Override
    public void aT() {
        RiseClickGUI gui = RiseClickGUI.instance;
        if (gui == null) return;
        this.category = gui.selectedCat;
        if (this.category != null) {
            this.relevantModules = gui.moduleList.stream()
                    .filter(mc -> mc.getModule().moduleCategory() == this.category)
                    .collect(Collectors.toCollection(ArrayList::new));
        }
        scroll = 0; scrollTarget = 0;
    }

    @Override
    public boolean pZ() { return true; }

    @Override
    public boolean qa() { return true; }
}
