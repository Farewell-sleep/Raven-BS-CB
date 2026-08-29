package com.alan.clients.ui.click.standard;

import com.alan.clients.Client;
import com.alan.clients.module.Module;
import com.alan.clients.module.api.Category;
import com.alan.clients.module.impl.render.ClickGUI;
import com.alan.clients.newevent.Listener;
import com.alan.clients.newevent.annotations.EventLink;
import com.alan.clients.newevent.impl.render.AlphaEvent;
import com.alan.clients.util.animation.Animation;
import com.alan.clients.util.animation.Easing;
import com.alan.clients.util.render.RenderUtil;
import com.alan.clients.util.vector.Vector2d;
import com.alan.clients.util.vector.Vector2f;
import com.alan.clients.ui.click.standard.components.ModuleComponent;
import com.alan.clients.ui.click.standard.components.category.SidebarCategory;
import com.alan.clients.ui.click.standard.components.value.ValueComponent;
import com.alan.clients.ui.click.standard.components.value.impl.BoundsNumberValueComponent;
import com.alan.clients.ui.click.standard.components.value.impl.NumberValueComponent;
import com.alan.clients.ui.click.standard.components.value.impl.StringValueComponent;
import com.alan.clients.ui.click.standard.UIColors;
import com.alan.clients.ui.click.standard.screen.Screen;
import com.alan.clients.ui.click.standard.screen.impl.SearchScreen;
import com.alan.clients.ui.click.standard.screen.impl.ThemeScreen;
import com.alan.clients.util.gui.GUIUtil;
import com.alan.clients.util.gui.textbox.TextBox;
import com.alan.clients.util.ime.PinyinInputHandler;
import com.alan.clients.util.ime.PinyinImeState;
import com.alan.clients.util.interfaces.ExecutorAccess;
import com.alan.clients.util.interfaces.InstanceAccess;
import com.alan.clients.util.MCPCompat;
import com.alan.clients.util.render.ColorUtil;
import com.alan.clients.util.shader.base.ShaderRenderType;
import com.alan.clients.util.shader.impl.AlphaShader;
import com.alan.clients.util.font.FontManager;
import com.alan.clients.util.font.FontWeight;
import com.alan.clients.util.shader.ShaderRenderQueue;
import com.alan.clients.util.shader.ShaderQueueType;
import java.awt.Color;
import java.lang.reflect.Field;
import java.text.Collator;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import java.util.concurrent.ConcurrentLinkedQueue;
import lombok.Generated;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.GuiScreen;
import net.minecraft.client.gui.ScaledResolution;
import net.minecraft.client.renderer.GlStateManager;
import org.lwjgl.input.Keyboard;
import org.lwjgl.opengl.GL11;
import rip.vantage.commons.util.time.StopWatch;

public class RiseClickGUI extends GuiScreen implements InstanceAccess, ExecutorAccess {
    public keystrokesmod.module.Module bindingModule;
    public boolean bindingMode;
    public Vector2f axI = new Vector2f(-1.0F, -1.0F);
    public Vector2f position = new Vector2f(416.0F, 338.0F);
    public SidebarCategory sidebar = new SidebarCategory();
    public Screen axK = Category.SEARCH.getClickGUIScreen();
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
    public double axU;
    public int round = 7;
    Vector2d translate;
    public ValueComponent overlayPresent;
    public Vector2f moduleDefaultScale = new Vector2f(283.0F, 38.0F);
    public Animation scaleAnimation = new Animation(Easing.EASE_IN_EXPO, 300L);
    public Animation opacityAnimation = new Animation(Easing.EASE_IN_EXPO, 300L);
    private final PinyinInputHandler aya = new PinyinInputHandler();
    private TextBox ayb;
    ShaderRenderQueue ayc = new ShaderRenderQueue(new AlphaShader());
    @EventLink(value = 0)
    public final Listener<AlphaEvent> onAlpha = var1 -> {
        if (this.axS <= 0.99) {
            this.oT();
        }
    };

    public RiseClickGUI() {
    }

    public boolean a(TextBox textBox) {
        if (!PinyinImeState.isEnabled()) {
            return false;
        }

        if (textBox == null) {
            return false;
        }

        if (textBox != this.ayb) {
            return false;
        }

        if (!this.aya.uc()) {
            return false;
        }

        String s = this.aya.uo();
        return s != null && !s.isEmpty();
    }

    public void oS() {
        this.moduleList.clear();
        System.out.println("PRE RMC");
        if (!Client.a.getSecurityManager().nN()) {
            System.out.println("RMC");
            ArrayList arraylist = Client.a.g().getAll();
            arraylist.sort((var0, var1) -> Collator.getInstance().compare(((Module)var0).getName(), ((Module)var1).getName()));
            arraylist.forEach(var1 -> this.moduleList.add(new ModuleComponent((Module)var1)));
        }
    }

    @Override
    public void initGui() {
        if (this.moduleList == null || this.moduleList.isEmpty()) {
            this.oS();
        }

        aMR.execute(
            () -> {
                this.round = 12;
                this.scaleAnimation.reset();
                this.scaleAnimation.setValue(0.0);
                ScaledResolution scaledresolution = MCPCompat.scaledResolution();
                this.axM = this.axK;
                this.axP.aX();
                this.axP.setMillis(System.currentTimeMillis() - 150L);
                Keyboard.enableRepeatEvents(true);
                this.rG.aX();
                this.axK.aT();
                if (this.axI.x < 0.0F
                    || this.axI.y < 0.0F
                    || this.axI.x + this.position.x > scaledresolution.getScaledWidth()
                    || this.axI.y + this.position.y > scaledresolution.getScaledHeight()) {
                    this.axI.x = scaledresolution.getScaledWidth() / 2.0F - this.position.x / 2.0F;
                    this.axI.y = scaledresolution.getScaledHeight() / 2.0F - this.position.y / 2.0F;
                }

                this.moduleList.forEach(var0 -> var0.getValueList().forEach(var0x -> {
                    if (var0x instanceof NumberValueComponent) {
                        ((NumberValueComponent)var0x).pU();
                    } else if (var0x instanceof BoundsNumberValueComponent) {
                        ((BoundsNumberValueComponent)var0x).pU();
                    }
                }));
            }
        );
        if (Client.a.getSecurityManager().nN()) {
            this.moduleList.clear();
        }
    }

    @Override
    public void onGuiClosed() {
        Keyboard.enableRepeatEvents(false);
        this.dragging = false;
    }

    @Override
    public boolean doesGuiPauseGame() {
        return false;
    }

    public void cj() {
        this.position = new Vector2f(400.0F, 300.0F);
        if (this.axS > 0.99) {
            this.oT();
        } else {
            ((AlphaShader)this.ayc.dU()).setAlpha((float)this.axT);
            this.ayc.c(this::oT);
            this.ayc.a(ShaderRenderType.OVERLAY);
            this.ayc.clear();
        }
    }

    public void startKeyBind(keystrokesmod.module.Module module) {
        this.bindingModule = module;
        this.bindingMode = true;
    }

    public void oT() {
        if (this.mouse != null) {
            Minecraft minecraft = Minecraft.getMinecraft();
            int i = (int)this.mouse.x;
            int j = (int)this.mouse.y;
            float f = MCPCompat.renderPartialTicks();
            if (this.dragging) {
                if (this.axK instanceof ThemeScreen) {
                    ((ThemeScreen)this.axK).qw();
                }

                this.axI.x = i + this.axN;
                this.axI.y = j + this.axO;
            }

            this.opacityAnimation.setEasing(minecraft.currentScreen == Client.a.getStandardClickGUI() ? Easing.EASE_OUT_EXPO : Easing.LINEAR);
            this.opacityAnimation.setDuration(minecraft.currentScreen == Client.a.getStandardClickGUI() ? 300L : 100L);
            this.opacityAnimation.Q(minecraft.currentScreen == Client.a.getStandardClickGUI() ? 1.0 : 0.0);
            this.axT = this.opacityAnimation.getValue();
            this.scaleAnimation.setEasing(minecraft.currentScreen == Client.a.getStandardClickGUI() ? Easing.EASE_OUT_EXPO : Easing.LINEAR);
            this.scaleAnimation.Q(minecraft.currentScreen == Client.a.getStandardClickGUI() ? 1.0 : 0.0);
            this.axS = this.scaleAnimation.getValue();
            if (minecraft.currentScreen == Client.a.getStandardClickGUI() && this.axS == 0.0) {
                this.axS = 0.01;
            }

            if (this.axS == 0.0) {
                com.alan.clients.module.impl.render.ClickGUI riseClickGuiModule = Client.a.g().c(ClickGUI.class);
                if (riseClickGuiModule != null) {
                    riseClickGuiModule.setEnabled(false);
                }
            } else {
                this.translate = new Vector2d((this.axI.x + this.position.x / 2.0F) * (1.0 - this.axS), (this.axI.y + this.position.y / 2.0F) * (1.0 - this.axS));
                Runnable runnable = () -> {
                    GlStateManager.pushMatrix();
                    if (this.axS != 1.0) {
                        GlStateManager.translate(this.translate.x, this.translate.y, 0.0);
                        GlStateManager.scale(this.axS, this.axS, 0.0);
                    }
                };
                runnable.run();
                this.b(ShaderQueueType.BLOOM, 2).c(runnable);
                if (this.axS > 0.993) {
                    RenderUtil.dropShadow(18, this.axI.x, this.axI.y, this.position.x, this.position.y, 30.0, this.round * 1.3);
                }

                RenderUtil.roundedRectangle(this.axI.x, this.axI.y, this.position.x, this.position.y, this.round, UIColors.BACKGROUND.pV());
                Runnable runnable1 = () -> {
                    GL11.glEnable(3089);
                    byte b0 = 1;
                    RenderUtil.g(
                        this.axI.x * this.axS + this.translate.x + b0,
                        this.axI.y * this.axS + this.translate.y + b0,
                        this.position.x * this.axS - b0 * 2,
                        this.position.y * this.axS - b0 * 2
                    );
                };
                runnable1.run();
                this.b(ShaderQueueType.BLOOM, 2).c(runnable1);
                Runnable runnable2 = () -> {
                    GL11.glPushMatrix();
                    GL11.glTranslated(0.0, 0.0, 0.0);
                };
                this.b(ShaderQueueType.BLOOM, 2).c(runnable2);
                short short1 = 200;
                try {
                    (this.axL = this.axP.T(short1) ? this.axK : this.axM).onRender(i, j, f);
                } catch (Throwable renderError) {
                    if (!RISE_GUI_RENDER_ERROR_LOGGED) {
                        RISE_GUI_RENDER_ERROR_LOGGED = true;
                        renderError.printStackTrace();
                    }
                    try {
                        GL11.glPopMatrix();
                    } catch (Throwable ignored) {
                    }
                    try {
                        GL11.glPopAttrib();
                    } catch (Throwable ignored) {
                    }
                }
                if (PinyinImeState.isEnabled()) {
                    double d0 = this.axI.x * this.axS + this.translate.x + 1.0;
                    double d1 = this.axI.y * this.axS + this.translate.y + 1.0;
                    double d2 = this.position.x * this.axS - 2.0;
                    double d3 = this.position.y * this.axS - 2.0;
                    TextBox textBox = this.oU();
                    if (textBox != this.ayb) {
                        this.aya.aX();
                        this.ayb = textBox;
                    }

                    if (textBox != null && this.aya.uc()) {
                        String s = this.aya.uo();
                        List list = this.aya.up();
                        if (s != null && !s.isEmpty()) {
                            float f1 = textBox.tL();
                            float f2 = textBox.tM();
                            FontManager.MAIN.a(16, FontWeight.REGULAR).a(s, f1, f2, ColorUtil.withAlpha(Color.WHITE, 210).getRGB());
                            RenderUtil.d(
                                f1, f2 + FontManager.MAIN.a(16, FontWeight.REGULAR).height() + 1.0F, FontManager.MAIN.a(16, FontWeight.REGULAR).getStringWidth(s), 1.0, ColorUtil.withAlpha(Color.WHITE, 140)
                            );
                            if (list != null && !list.isEmpty()) {
                                StringBuilder stringbuilder = new StringBuilder();
                                int i1 = Math.min(9, list.size());
                                int j1 = this.aya.un();

                                for (int k1 = 0; k1 < i1; k1++) {
                                    if (k1 > 0) {
                                        stringbuilder.append("  ");
                                    }

                                    String s1 = k1 + 1 + "." + (String)list.get(k1);
                                    if (k1 == j1) {
                                        stringbuilder.append('[').append(s1).append(']');
                                    } else {
                                        stringbuilder.append(s1);
                                    }
                                }

                                stringbuilder.append(this.aya.ut());
                                String s2 = stringbuilder.toString();
                                double d5 = 6.0;
                                double d6 = FontManager.MAIN.a(16, FontWeight.REGULAR).getStringWidth(s2) + d5 * 2.0;
                                double d7 = FontManager.MAIN.a(16, FontWeight.REGULAR).height() + d5 * 2.0 - 5.0;
                                double d8 = f1 - 2.0F;
                                double d9 = f2 + FontManager.MAIN.a(16, FontWeight.REGULAR).height() + 6.0F;
                                double d10 = 8.0;
                                Color color = ColorUtil.withAlpha(Color.WHITE, 255);
                                this.b(ShaderQueueType.BLUR).c(() -> RenderUtil.roundedRectangle(d8, d9, d6, d7, d10, color));
                                this.b(ShaderQueueType.BLOOM, 2).c(() -> {
                                    GL11.glDisable(3089);
                                    RenderUtil.roundedRectangle(d8, d9, d6, d7, d10 + 2.0, this.rz().rE());
                                    GL11.glEnable(3089);
                                    RenderUtil.g(d0, d1, d2, d3);
                                });
                                boolean flag1 = GL11.glIsEnabled(3089);
                                if (flag1) {
                                    GL11.glDisable(3089);
                                }

                                RenderUtil.roundedRectangle(d8, d9, d6, d7, d10, ColorUtil.withAlpha(UIColors.BACKGROUND.pV(), (int)Math.min(220.0, this.axT * 255.0)));
                                FontManager.MAIN.a(16, FontWeight.REGULAR).a(s2, d8 + d5, d9 + d5, ColorUtil.withAlpha(Color.WHITE, 240).getRGB());
                                if (flag1) {
                                    GL11.glEnable(3089);
                                    RenderUtil.g(d0, d1, d2, d3);
                                }
                            }
                        }
                    }
                }

                int k = 255
                    - (int)Math.max(
                        0.0F,
                        Math.min(
                            255.0F,
                            this.axP.getElapsedTime() < short1 ? 255.0F - (float)this.axP.getElapsedTime() * (255.0F / short1) : (float)(this.axP.getElapsedTime() - short1) * (255.0F / short1)
                        )
                    );
                if (this.axP.getElapsedTime() <= short1 * 2) {
                    RenderUtil.roundedRectangle(this.axI.x, this.axI.y, this.position.x, this.position.y, this.round, UIColors.BACKGROUND.Y(k));
                }

                try {
                    this.sidebar.pF();
                } catch (Throwable renderError) {
                    if (!RISE_GUI_RENDER_ERROR_LOGGED) {
                        RISE_GUI_RENDER_ERROR_LOGGED = true;
                        renderError.printStackTrace();
                    }
                    try {
                        GL11.glPopMatrix();
                    } catch (Throwable ignored) {
                    }
                    try {
                        GL11.glPopAttrib();
                    } catch (Throwable ignored) {
                    }
                }

                for (int l = 0; l <= 8; l++) {
                    double d4 = l * 50;
                    RenderUtil.c(this.axI.x + this.sidebar.aym - d4 / 2.0, this.axI.y + this.position.y / 2.0F - d4 / 2.0, d4, ColorUtil.withAlpha(this.rz().rA(), 1));
                }

                this.sidebar.renderSidebar(i, j);
                Runnable runnable3 = () -> {
                    GL11.glDisable(3089);
                    GlStateManager.popMatrix();
                };
                runnable3.run();
                this.b(ShaderQueueType.BLOOM, 2).c(runnable3);
                Runnable runnable4 = GL11::glPopMatrix;
                this.b(ShaderQueueType.BLOOM, 2).c(runnable4);
                this.rG.aX();
            }
        }
    }

    private static boolean RISE_GUI_RENDER_ERROR_LOGGED = false;

    @Override
    public void drawScreen(int var1, int var2, float var3) {
        this.mouse = new Vector2f(var1, var2);
        try {
            this.oT();
        } catch (Throwable renderError) {
            if (!RISE_GUI_RENDER_ERROR_LOGGED) {
                RISE_GUI_RENDER_ERROR_LOGGED = true;
                renderError.printStackTrace();
            }
        }
        if (this.bindingMode) {
            ScaledResolution sr = new ScaledResolution(aEg);
            float w = sr.getScaledWidth();
            float h = sr.getScaledHeight();
            String prompt = this.bindingModule != null
                ? "Press a key to bind " + this.bindingModule.getName() + "... (Esc to cancel)"
                : "Press a key... (Esc to cancel)";
            com.alan.clients.util.render.RenderUtil.roundedRectangle(w / 2.0F - 160.0F, h - 60.0F, 320.0F, 30.0F, 6.0F, new Color(20, 20, 25, 200));
            com.alan.clients.util.font.FontManager.MAIN.a(16, com.alan.clients.util.font.FontWeight.REGULAR)
                .a(prompt, w / 2.0F - 150.0F, h - 52.0F, Color.WHITE.getRGB());
        }
    }

    public void ci() {
        this.translate = new Vector2d((this.axI.x + this.position.x / 2.0F) * (1.0 - this.axS), (this.axI.y + this.position.y / 2.0F) * (1.0 - this.axS));
        GlStateManager.pushMatrix();
        if (this.axS != 1.0) {
            GlStateManager.translate(this.translate.x, this.translate.y, 0.0);
            GlStateManager.scale(this.axS, this.axS, 0.0);
        }

        GL11.glEnable(3089);
        RenderUtil.g(this.axI.x * this.axS + this.translate.x, this.axI.y * this.axS + this.translate.y, this.position.x * this.axS, (this.position.y - 4.0F) * this.axS);
        this.axL.pY();
        this.sidebar.preRenderClickGUI();
        GL11.glDisable(3089);
        GlStateManager.popMatrix();
    }

    @Override
    public void mouseClicked(int var1, int var2, int var3) {
        if (this.bindingMode) {
            if (var3 == 0 || var3 == 1) {
                if (this.bindingModule != null) {
                    this.bindingModule.setBind(1000 + var3);
                }
                this.bindingMode = false;
            }
            return;
        }
        if (GUIUtil.c(this.axI.x, this.axI.y, this.position.x, 15.0, var1, var2) && this.overlayPresent == null) {
            this.axN = this.axI.x - var1;
            this.axO = this.axI.y - var2;
            this.dragging = true;
        } else if (GUIUtil.c(this.axI.getX(), this.axI.getY(), this.position.getX(), this.position.getY(), var1, var2)) {
            if (this.overlayPresent == null) {
                this.sidebar.clickSidebar(var1, var2, var3);
            }

            this.axK.f(var1, var2, var3);
        }

        this.overlayPresent = null;
    }

    @Override
    protected void mouseReleased(int var1, int var2, int var3) {
        this.dragging = false;
        this.axK.oG();
    }

    @Override
    protected void keyTyped(char var1, int var2) {
        if (this.bindingMode) {
            if (var2 == 1) {
                this.bindingMode = false;
            } else {
                if (this.bindingModule != null) {
                    this.bindingModule.setBind(var2);
                }
                this.bindingMode = false;
            }
            return;
        }
        if (PinyinImeState.isEnabled()) {
            TextBox textBox = this.oU();
            if (textBox != this.ayb) {
                this.aya.aX();
                this.ayb = textBox;
            }

            if (textBox != null && this.aya.a(textBox, var1, var2)) {
                return;
            }
        }

        if ("abcdefghijklmnopqrstuvwxyz1234567890 ".contains(String.valueOf(var1).toLowerCase()) && this.axK.pZ() && !this.getStandardClickGUI().oV()) {
            this.switchScreen(Category.SEARCH);
        }

        try {
            super.keyTyped(var1, var2);
        } catch (java.io.IOException ignored) {
        }

        this.axK.onKey(var1, var2);
    }

    private TextBox oU() {
        try {
            if (this.axK instanceof SearchScreen && !this.oV()) {
                return ((SearchScreen) this.axK).azR;
            }

            try {
                if ("com.alan.clients.ui.click.standard.screen.impl.ConfigScreen".equals(this.axK.getClass().getName())) {
                    Field field = this.axK.getClass().getDeclaredField("searchBar");
                    field.setAccessible(true);
                    Object object = field.get(this.axK);
                    if (object instanceof TextBox && !this.oV()) {
                        return (TextBox)object;
                    }
                }
            } catch (Throwable throwable) {
            }

            for (ModuleComponent moduleComponent : this.moduleList) {
                for (ValueComponent valueComponent : moduleComponent.getValueList()) {
                    if (valueComponent instanceof StringValueComponent && ((StringValueComponent) valueComponent).azo != null && ((StringValueComponent) valueComponent).azo.selected) {
                        return ((StringValueComponent) valueComponent).azo;
                    }
                }
            }
        } catch (Throwable throwable1) {
        }

        return null;
    }

    public void switchScreen(Category category) {
        if (!category.getClickGUIScreen().equals(this.axK)) {
            this.axM = this.getStandardClickGUI().axK;
            this.axK = category.getClickGUIScreen();
            this.axP.aX();
            this.axK.aT();
            SearchScreen searchScreen = (SearchScreen)Category.SEARCH.getClickGUIScreen();
            searchScreen.relevantModules = searchScreen.getRelevantModules(searchScreen.azR.getText());
        }
    }

    public void a(Screen screen) {
        if (!this.axK.getClass().getSimpleName().equals(screen.getClass().getSimpleName())) {
            this.axM = this.getStandardClickGUI().axK;
            this.axK = screen;
            this.axP.aX();
            this.axK.aT();
            SearchScreen searchScreen = (SearchScreen)Category.SEARCH.getClickGUIScreen();
            searchScreen.relevantModules = searchScreen.getRelevantModules(searchScreen.azR.getText());
        }
    }

    public boolean oV() {
        Iterator iterator = this.moduleList.iterator();

        while (iterator.hasNext()) {
            for (ValueComponent valueComponent : ((ModuleComponent)iterator.next()).getValueList()) {
                if (valueComponent instanceof StringValueComponent && valueComponent.position != null && ((StringValueComponent)valueComponent).azo.selected && !((StringValueComponent)valueComponent).azo.aJv.T(50L)) {
                    return true;
                }

                if (valueComponent instanceof NumberValueComponent && ((NumberValueComponent)valueComponent).azm.isSelected() && !((NumberValueComponent)valueComponent).azm.aJv.T(50L)) {
                    return true;
                }

                if (valueComponent instanceof BoundsNumberValueComponent && ((BoundsNumberValueComponent)valueComponent).ayS.isSelected() && !((BoundsNumberValueComponent)valueComponent).ayS.aJv.T(50L)) {
                    return true;
                }
            }
        }

        return false;
    }

    @Generated
    public Vector2f getScale() {
        return this.axI;
    }

    @Generated
    public Vector2f getPosition() {
        return this.position;
    }

    @Generated
    public SidebarCategory getSidebar() {
        return this.sidebar;
    }

    @Generated
    public Screen getScreen() {
        return this.axK;
    }

    @Generated
    public Screen pa() {
        return this.axL;
    }

    @Generated
    public Screen pb() {
        return this.axM;
    }

    @Generated
    public float pc() {
        return this.axN;
    }

    @Generated
    public float pd() {
        return this.axO;
    }

    @Generated
    public boolean isDragging() {
        return this.dragging;
    }

    @Generated
    public StopWatch pf() {
        return this.axP;
    }

    @Generated
    public StopWatch lN() {
        return this.rG;
    }

    @Generated
    public ConcurrentLinkedQueue<ModuleComponent> getModuleList() {
        return this.moduleList;
    }

    @Generated
    public Vector2f getMouse() {
        return this.mouse;
    }

    @Generated
    public double pi() {
        return this.axS;
    }

    @Generated
    public double pj() {
        return this.axT;
    }

    @Generated
    public double pk() {
        return this.axU;
    }

    @Generated
    public int getRound() {
        return this.round;
    }

    @Generated
    public Vector2d getTranslate() {
        return this.translate;
    }

    @Generated
    public ValueComponent getOverlayPresent() {
        return this.overlayPresent;
    }

    @Generated
    public Vector2f getModuleDefaultScale() {
        return this.moduleDefaultScale;
    }

    @Generated
    public Animation getScaleAnimation() {
        return this.scaleAnimation;
    }

    @Generated
    public Animation getOpacityAnimation() {
        return this.opacityAnimation;
    }

    @Generated
    public PinyinInputHandler pq() {
        return this.aya;
    }

    @Generated
    public TextBox getTextBox() {
        return this.ayb;
    }

    @Generated
    public ShaderRenderQueue ps() {
        return this.ayc;
    }

    @Generated
    public Listener<AlphaEvent> getOnAlpha() {
        return this.onAlpha;
    }
}
