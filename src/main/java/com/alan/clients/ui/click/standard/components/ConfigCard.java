package com.alan.clients.ui.click.standard.components;

import com.alan.clients.ui.click.standard.UIColors;
import com.alan.clients.util.render.ColorUtil;
import com.alan.clients.util.animation.Animation;
import com.alan.clients.util.animation.Easing;
import com.alan.clients.util.font.FontManager;
import com.alan.clients.util.font.FontWeight;
import com.alan.clients.util.gui.GUIUtil;
import com.alan.clients.util.interfaces.InstanceAccess;
import com.alan.clients.util.render.RenderUtil;
import com.alan.clients.util.vector.Vector2f;
import com.alan.clients.util.dragging.Mouse;
import com.alan.clients.util.font.Font;
import lombok.Generated;
import rip.vantage.commons.util.time.StopWatch;

public class ConfigCard implements InstanceAccess {
    private String title;
    private String azZ;
    private Runnable aAa;
    private keystrokesmod.module.Module bindModule;
    public static final int STATE_NORMAL = 0;
    public static final int STATE_SELECTED = 1;
    public static final int STATE_ERROR = 2;
    private int state = 0;
    private Runnable onRightClick;
    private Vector2f alh = new Vector2f(86.450005F, 86.450005F);
    private Animation aAb = new Animation(Easing.LINEAR, 200L);
    private Animation aAc = new Animation(Easing.EASE_OUT_EXPO, 500L);
    private Vector2f axI;
    private StopWatch asY;
    private Font aAd = FontManager.MAIN.a(20, FontWeight.REGULAR);

    public ConfigCard(String var1, String var2) {
        this.title = truncate(this.aAd, var2, 86.450005F - 20);
        this.azZ = var1;
        this.aAa = null;
    }

    public ConfigCard(String var1, String var2, Runnable runnable) {
        String s = org.apache.commons.lang3.StringUtils.capitalize(var2);
        this.title = truncate(this.aAd, s, 86.450005F - 20);
        this.aAa = runnable;
        this.azZ = var1;
    }

    public void j(Vector2f vec2) {
        this.axI = new Vector2f(vec2.x, vec2.y);
        if (!(this.axI.x + this.alh.x < this.getStandardClickGUI().axI.x + this.getStandardClickGUI().sidebar.aym)
            && !(this.axI.x > this.getStandardClickGUI().axI.x + this.getStandardClickGUI().sidebar.aym + this.getStandardClickGUI().position.x)) {
            this.aAb.Q(this.qz() ? 75.0 : 0.0);
            this.aAc.Q(this.qz() ? 5.0 : 0.0);
            RenderUtil.roundedRectangle(this.axI.x, this.axI.y, this.alh.x, this.alh.y, 8.0, UIColors.OVERLAY.pV());
            if (this.state == STATE_SELECTED) {
                RenderUtil.roundedRectangle(this.axI.x, this.axI.y, this.alh.x, this.alh.y, 8.0, ColorUtil.withAlpha(new java.awt.Color(66, 135, 245), 110));
                RenderUtil.roundedRectangle(this.axI.x + 1.0, this.axI.y + 1.0, this.alh.x - 2.0, this.alh.y - 2.0, 7.0, ColorUtil.withAlpha(new java.awt.Color(66, 135, 245), 30));
            } else if (this.state == STATE_ERROR) {
                RenderUtil.roundedRectangle(this.axI.x, this.axI.y, this.alh.x, this.alh.y, 8.0, ColorUtil.withAlpha(new java.awt.Color(255, 70, 70), 70));
            }
            RenderUtil.roundedRectangle(vec2.x, vec2.y, this.alh.x, this.alh.y, 8.0, UIColors.OVERLAY.Y((int)this.aAb.getValue()));
            this.axI.y = this.axI.y + (this.alh.y / 2.0F - this.aAd.height() / 2.0F + 1.0F - 10 / 4.0F);
            this.aAd.drawString(this.title, this.axI.x + this.alh.x / 2.0F, this.axI.y, this.getTitleColor());
            this.axI.y = this.axI.y + (this.aAd.height() + 10 / 2.0F);
            this.k(this.axI);
            this.axI = new Vector2f(vec2.x, vec2.y);
        }
    }

    public void f(int var1, int var2, int var3) {
        if (this.axI != null) {
            if (GUIUtil.a(this.axI, this.alh, var1, var2)) {
                if (var3 == 0 && this.aAa != null && this.state != STATE_ERROR) {
                    this.aAa.run();
                } else if (var3 == 1) {
                    if (this.onRightClick != null) {
                        this.onRightClick.run();
                    } else if (this.bindModule != null) {
                        this.getStandardClickGUI().startKeyBind(this.bindModule);
                    }
                }
            }
        }
    }

    public void setOnRightClick(Runnable onRightClick) {
        this.onRightClick = onRightClick;
    }

    public void setBindModule(keystrokesmod.module.Module module) {
        this.bindModule = module;
    }

    public boolean qz() {
        return GUIUtil.a(this.axI, this.alh, Mouse.getMouse().getX(), Mouse.getMouse().getY());
    }

    public void k(Vector2f vec2) {
        int col = this.state == STATE_ERROR ? new java.awt.Color(255, 70, 70).getRGB()
            : this.state == STATE_SELECTED ? new java.awt.Color(120, 180, 255).getRGB()
            : UIColors.TRINARY_TEXT.pW();
        FontManager.MAIN.a(16, FontWeight.REGULAR).drawString(this.azZ, vec2.x + this.alh.x / 2.0F, vec2.y, col);
    }

    private int getTitleColor() {
        if (this.state == STATE_ERROR) {
            return new java.awt.Color(255, 80, 80).getRGB();
        }
        if (this.state == STATE_SELECTED) {
            return new java.awt.Color(120, 180, 255).getRGB();
        }
        return UIColors.SECONDARY_TEXT.pW();
    }

    public void setState(int state) {
        this.state = state;
    }

    public int getState() {
        return this.state;
    }

    @Generated
    public String getTitle() {
        return this.title;
    }

    @Generated
    public String getAction() {
        return this.azZ;
    }

    @Generated
    public Runnable getRunnable() {
        return this.aAa;
    }

    @Generated
    public Vector2f oX() {
        return this.alh;
    }

    @Generated
    public Animation qB() {
        return this.aAb;
    }

    @Generated
    public Animation qC() {
        return this.aAc;
    }

    @Generated
    public Vector2f oW() {
        return this.axI;
    }

    @Generated
    public StopWatch qD() {
        return this.asY;
    }

    @Generated
    public Font qE() {
        return this.aAd;
    }

    @Generated
    public void setTitle(String var1) {
        this.title = var1;
    }

    @Generated
    public void setAction(String var1) {
        this.azZ = var1;
    }

    @Generated
    public void setRunnable(Runnable runnable) {
        this.aAa = runnable;
    }

    @Generated
    public void l(Vector2f vec2) {
        this.alh = vec2;
    }

    @Generated
    public void b(Animation animation) {
        this.aAb = animation;
    }

    @Generated
    public void c(Animation animation) {
        this.aAc = animation;
    }

    @Generated
    public void i(Vector2f vec2) {
        this.axI = vec2;
    }

    @Generated
    public void b(StopWatch var1) {
        this.asY = var1;
    }

    @Generated
    public void b(Font var1) {
        this.aAd = var1;
    }
    private static String truncate(Font font, String text, float maxWidth) {
        if (text == null) {
            return "";
        }
        if (font.getStringWidth(text) <= maxWidth) {
            return text;
        }
        String result = text;
        while (!result.isEmpty() && font.getStringWidth(result) > maxWidth) {
            result = result.substring(0, result.length() - 1);
        }
        return result;
    }

}
