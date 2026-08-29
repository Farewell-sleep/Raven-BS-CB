package keystrokesmod.clickgui.rise;

import keystrokesmod.Raven;
import keystrokesmod.clickgui.rise.components.ModuleComponent;
import keystrokesmod.clickgui.rise.components.category.SidebarCategory;
import keystrokesmod.clickgui.rise.components.value.ValueComponent;
import keystrokesmod.clickgui.rise.screen.Screen;
import keystrokesmod.clickgui.rise.screen.impl.CategoryScreen;
import keystrokesmod.clickgui.rise.util.Animation;
import keystrokesmod.clickgui.rise.util.ColorUtil;
import keystrokesmod.clickgui.rise.util.Easing;
import keystrokesmod.clickgui.rise.util.GUIUtil;
import keystrokesmod.clickgui.rise.util.RenderUtil;
import keystrokesmod.clickgui.rise.util.StopWatch;
import keystrokesmod.clickgui.rise.util.Vector2d;
import keystrokesmod.clickgui.rise.util.Vector2f;
import keystrokesmod.module.Module;
import keystrokesmod.module.ModuleManager;
import keystrokesmod.module.impl.client.Gui;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.GuiScreen;
import net.minecraft.client.gui.ScaledResolution;
import net.minecraft.client.renderer.GlStateManager;
import org.lwjgl.input.Keyboard;
import org.lwjgl.opengl.GL11;

import java.awt.Color;
import java.text.Collator;
import java.util.ArrayList;
import java.util.concurrent.ConcurrentLinkedQueue;

public class RiseClickGUI extends GuiScreen {
    public static RiseClickGUI instance;

    public Vector2f axI = new Vector2f(-1.0F, -1.0F);
    public Vector2f position = new Vector2f(416.0F, 338.0F);
    public SidebarCategory sidebar = new SidebarCategory();
    public Screen axK = new CategoryScreen();
    public Screen axL = this.axK;
    public Screen axM = this.axK;
    public float axN;
    public float axO;
    public boolean dragging;
    public StopWatch axP = new StopWatch();
    public StopWatch rG = new StopWatch();
    public ConcurrentLinkedQueue<ModuleComponent> moduleList = new ConcurrentLinkedQueue<>();
    public Vector2f mouse;
    public double axS;
    public double axT;
    public int round = 12;
    Vector2d translate;
    public ValueComponent overlayPresent;
    public Vector2f moduleDefaultScale = new Vector2f(283.0F, 38.0F);
    public Animation scaleAnimation = new Animation(Easing.EASE_IN_EXPO, 300L);
    public Animation opacityAnimation = new Animation(Easing.EASE_IN_EXPO, 300L);

    public Module.category selectedCat = null;
    public float scroll = 0;

    public RiseClickGUI() {
        instance = this;
    }

    public void buildModules() {
        moduleList.clear();
        ArrayList<Module> all = new ArrayList<>(Raven.moduleManager.getModules());
        all.sort((a, b) -> Collator.getInstance().compare(a.getName(), b.getName()));
        all.forEach(m -> moduleList.add(new ModuleComponent(m)));
    }

    @Override
    public void initGui() {
        instance = this;
        if (moduleList == null || moduleList.isEmpty()) buildModules();

        round = 12;
        scaleAnimation.setValue(0.0);
        ScaledResolution sr = new ScaledResolution(mc);
        axM = axK;
        axP.reset();
        axP.setMillis(System.currentTimeMillis() - 150L);
        Keyboard.enableRepeatEvents(true);
        rG.reset();
        axK.aT();

        if (axI.x < 0 || axI.y < 0 || axI.x + position.x > sr.getScaledWidth() || axI.y + position.y > sr.getScaledHeight()) {
            axI.x = sr.getScaledWidth() / 2f - position.x / 2f;
            axI.y = sr.getScaledHeight() / 2f - position.y / 2f;
        }
    }

    @Override
    public void onGuiClosed() {
        Keyboard.enableRepeatEvents(false);
        dragging = false;
    }

    @Override
    public boolean doesGuiPauseGame() { return false; }

    @Override
    public void drawScreen(int mouseX, int mouseY, float partialTicks) {
        this.mouse = new Vector2f(mouseX, mouseY);
        render();
        super.drawScreen(mouseX, mouseY, partialTicks);
    }

    public void render() {
        if (mouse == null) return;
        Minecraft mc = Minecraft.getMinecraft();
        int i = (int) mouse.x;
        int j = (int) mouse.y;

        // Mouse wheel
        int wheel = org.lwjgl.input.Mouse.getEventDWheel();
        if (wheel != 0 && axK instanceof CategoryScreen) {
            ((CategoryScreen) axK).scrollTarget -= Math.signum(wheel) * 40f;
        }

        if (dragging) {
            axI.x = i + axN;
            axI.y = j + axO;
        }

        // Animations
        scaleAnimation.setEasing(Easing.EASE_OUT_EXPO);
        scaleAnimation.Q(1.0);
        axS = scaleAnimation.getValue();
        if (axS == 0.0) axS = 0.01;

        opacityAnimation.setEasing(Easing.EASE_OUT_EXPO);
        opacityAnimation.Q(1.0);
        axT = opacityAnimation.getValue();

        if (axS > 0) {
            translate = new Vector2d((axI.x + position.x / 2f) * (1 - axS), (axI.y + position.y / 2f) * (1 - axS));

            GlStateManager.pushMatrix();
            if (axS != 1.0) {
                GlStateManager.translate(translate.x, translate.y, 0);
                GlStateManager.scale(axS, axS, 0);
            }

            // Shadow
            if (axS > 0.993) {
                RenderUtil.dropShadow(18, axI.x, axI.y, position.x, position.y, 30, round * 1.3);
            }

            // Panel background
            RenderUtil.roundedRectangle(axI.x, axI.y, position.x, position.y, round, UIColors.BACKGROUND.pV());

            // Scissor for content
            GL11.glEnable(GL11.GL_SCISSOR_TEST);
            RenderUtil.g(axI.x + 1, axI.y + 1, position.x - 2, position.y - 2);

            short short1 = 200;
            axL = axP.T(short1) ? axK : axM;
            axL.onRender(i, j, 1.0f);

            // Fade in overlay
            int k = 255 - (int) Math.max(0, Math.min(255,
                    axP.getElapsedTime() < short1 ? 255f - axP.getElapsedTime() * (255f / short1)
                            : (axP.getElapsedTime() - short1) * (255f / short1)));
            if (axP.getElapsedTime() <= short1 * 2) {
                RenderUtil.roundedRectangle(axI.x, axI.y, position.x, position.y, round, UIColors.BACKGROUND.Y(k));
            }

            sidebar.pF();

            // Accent glow circles
            for (int l = 0; l <= 8; l++) {
                double d4 = l * 50;
                RenderUtil.c(axI.x + sidebar.aym - d4 / 2.0, axI.y + position.y / 2f - d4 / 2.0, d4,
                        new Color(96, 165, 250, 1));
            }

            sidebar.renderSidebar(i, j);

            GL11.glDisable(GL11.GL_SCISSOR_TEST);
            GlStateManager.popMatrix();
            rG.reset();
        }
    }

    @Override
    public void mouseClicked(int mouseX, int mouseY, int mouseButton) {
        if (GUIUtil.c(axI.x, axI.y, position.x, 15, mouseX, mouseY) && overlayPresent == null) {
            axN = axI.x - mouseX;
            axO = axI.y - mouseY;
            dragging = true;
        } else if (GUIUtil.c(axI.getX(), axI.getY(), position.getX(), position.getY(), mouseX, mouseY)) {
            if (overlayPresent == null) {
                sidebar.clickSidebar(mouseX, mouseY, mouseButton);
            }
            axK.f(mouseX, mouseY, mouseButton);
        }
        overlayPresent = null;
    }

    @Override
    protected void mouseReleased(int mouseX, int mouseY, int state) {
        dragging = false;
        axK.oG();
    }

    @Override
    protected void keyTyped(char typedChar, int keyCode) {
        // Type to search -> switch to category (simplified)
        if ("abcdefghijklmnopqrstuvwxyz1234567890 ".contains(String.valueOf(typedChar).toLowerCase()) && axK.pZ()) {
            // For now, just pass to screen
        }
        axK.onKey(typedChar, keyCode);

        if (keyCode == Keyboard.KEY_ESCAPE || keyCode == ModuleManager.getModule(Gui.class).getKeycode()) {
            mc.displayGuiScreen(null);
            if (mc.currentScreen == null) mc.setIngameFocus();
        }
    }

    public void switchScreen(Module.category category) {
        selectedCat = category;
        axK.aT();
        axP.reset();
    }
}
