package com.alan.clients.ui.click.standard.components.theme;

import com.alan.clients.ui.theme.Themes;
import com.alan.clients.util.animation.Animation;
import com.alan.clients.util.animation.Easing;
import com.alan.clients.util.font.FontManager;
import com.alan.clients.util.interfaces.InstanceAccess;
import com.alan.clients.util.render.ColorUtil;
import com.alan.clients.util.render.RenderUtil;
import com.alan.clients.util.vector.Vector3d;
import com.alan.clients.util.font.FontWeight;
import com.alan.clients.util.shader.ShaderQueueType;
import java.awt.Color;
import lombok.Generated;

public class ThemeComponent implements InstanceAccess {
    private final Themes activeTheme;
    private Vector3d ayu = new Vector3d(0.0, 0.0, 0.0);
    private final Animation xAnimation = new Animation(Easing.EASE_OUT_QUINT, 500L);
    private final Animation yAnimation = new Animation(Easing.EASE_OUT_QUINT, 500L);
    private final Animation opacityAnimation = new Animation(Easing.EASE_OUT_QUINT, 500L);
    private final Animation selectorAnimation = new Animation(Easing.EASE_OUT_QUINT, 500L);

    public void draw(double var1, double var3) {
        int i = (int)this.opacityAnimation.getValue();
        boolean flag = this.activeTheme.equals(this.rz());
        Color color = flag ? new Color(15, 19, 26, (int)this.opacityAnimation.getValue()) : new Color(18, 21, 30, i);
        double d0 = this.xAnimation.getValue();
        double d1 = this.yAnimation.getValue() + var1;
        RenderUtil.roundedRectangle(d0, d1, var3, 50.0, 10.0, color);
        if (this.activeTheme.isTriColor()) {
            RenderUtil.a(d0, d1, var3, 30.0, 9.0, ColorUtil.withAlpha(this.activeTheme.rA(), i), ColorUtil.withAlpha(this.activeTheme.rB(), i), ColorUtil.withAlpha(this.activeTheme.rC(), i), false, true, true, false, false);
        } else {
            RenderUtil.a(d0, d1, var3, 30.0, 9.0, ColorUtil.withAlpha(this.activeTheme.rA(), i), ColorUtil.withAlpha(this.activeTheme.rB(), i), false, true, true, false, false);
        }

        RenderUtil.d(d0, d1 + 30.0, var3, 10.0, color);
        FontManager.MAIN.a(16, FontWeight.REGULAR).drawString(this.activeTheme.getThemeName(), d0 + var3 / 2.0, d1 + 37.0, flag ? ColorUtil.withAlpha(this.rz().rA(), i).getRGB() : new Color(255, 255, 255, i).getRGB());
        this.selectorAnimation.Q(this.activeTheme.equals(this.rz()) ? 255.0 : 0.0);
        int j = (int)Math.min(this.selectorAnimation.getValue(), i);
        if (j > 0 && this.getStandardClickGUI().axS > 0.8) {
            this.b(ShaderQueueType.BLOOM, 3)
                .c(
                    () -> {
                        if (this.activeTheme.isTriColor()) {
                            RenderUtil.a(
                                d0,
                                d1,
                                var3,
                                30.0,
                                10.0,
                                ColorUtil.withAlpha(this.activeTheme.rA(), i),
                                ColorUtil.withAlpha(this.activeTheme.rB(), i),
                                ColorUtil.withAlpha(this.activeTheme.rC(), i),
                                false,
                                true,
                                true,
                                false,
                                false
                            );
                        } else {
                            RenderUtil.a(
                                d0 + 1.0, d1, var3 - 2.0, 30.0, 10.0, ColorUtil.withAlpha(this.activeTheme.rA(), j), ColorUtil.withAlpha(this.activeTheme.rB(), j), false, true, true, false, false
                            );
                        }

                        FontManager.MAIN.a(16, FontWeight.REGULAR).drawString(this.activeTheme.getThemeName(), d0 + var3 / 2.0, d1 + 37.0, ColorUtil.withAlpha(this.activeTheme.rA(), j).getRGB());
                    }
                );
        }

        this.ayu = new Vector3d(d0, d1, var3);
    }

    @Generated
    public Themes getActiveTheme() {
        return this.activeTheme;
    }

    @Generated
    public Vector3d pL() {
        return this.ayu;
    }

    @Generated
    public Animation getXAnimation() {
        return this.xAnimation;
    }

    @Generated
    public Animation getYAnimation() {
        return this.yAnimation;
    }

    @Generated
    public Animation getOpacityAnimation() {
        return this.opacityAnimation;
    }

    @Generated
    public Animation getSelectorAnimation() {
        return this.selectorAnimation;
    }

    @Generated
    public ThemeComponent(Themes themes) {
        this.activeTheme = themes;
    }
}
