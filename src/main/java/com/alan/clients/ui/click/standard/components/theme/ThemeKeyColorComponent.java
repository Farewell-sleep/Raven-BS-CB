package com.alan.clients.ui.click.standard.components.theme;

import com.alan.clients.ui.theme.KeyColors;
import com.alan.clients.util.animation.Animation;
import com.alan.clients.util.animation.Easing;
import com.alan.clients.util.interfaces.InstanceAccess;
import com.alan.clients.util.render.ColorUtil;
import com.alan.clients.util.render.RenderUtil;
import com.alan.clients.util.vector.Vector3d;
import com.alan.clients.util.shader.ShaderQueueType;
import java.awt.Color;
import lombok.Generated;

public class ThemeKeyColorComponent implements InstanceAccess {
    private final KeyColors color;
    private Vector3d ayu = new Vector3d(0.0, 0.0, 0.0);
    private final Animation dimAnimation = new Animation(Easing.EASE_OUT_QUINT, 500L);
    private final Animation bloomAnimation = new Animation(Easing.EASE_OUT_QUINT, 500L);

    public void draw(double var1, double var3, double var5, boolean var7) {
        this.dimAnimation.getValue();
        RenderUtil.roundedRectangle(var1, var3, var5, 17.0, 5.0, new Color(18, 21, 30));
        RenderUtil.roundedRectangle(var1 + 0.5, var3 + 0.5, var5 - 1.0, 16.0, 4.0, this.color.getColor());
        RenderUtil.roundedRectangle(var1, var3, var5, 17.0, 5.0, new Color(25, 25, 25, (int)((1.0 - this.dimAnimation.getValue()) * 128.0)));
        this.b(ShaderQueueType.BLOOM).c(() -> {
            RenderUtil.roundedRectangle(var1, var3, var5, 17.0, 5.0, new Color(18, 21, 30, (int)(this.bloomAnimation.getValue() * 255.0)));
            RenderUtil.roundedRectangle(var1 + 0.5, var3 + 0.5, var5 - 1.0, 16.0, 4.0, ColorUtil.withAlpha(this.color.getColor(), (int)(this.bloomAnimation.getValue() * 255.0)));
        });
        this.ayu = new Vector3d(var1, var3, var5);
    }

    @Generated
    public KeyColors getColor() {
        return this.color;
    }

    @Generated
    public Vector3d pL() {
        return this.ayu;
    }

    @Generated
    public Animation getDimAnimation() {
        return this.dimAnimation;
    }

    @Generated
    public Animation getBloomAnimation() {
        return this.bloomAnimation;
    }

    @Generated
    public ThemeKeyColorComponent(KeyColors var1) {
        this.color = var1;
    }
}
