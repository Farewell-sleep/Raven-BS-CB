package com.alan.clients.ui.click.standard.components.value.impl;

import com.alan.clients.util.render.RenderUtil;
import com.alan.clients.util.vector.Vector2d;
import com.alan.clients.value.Value;
import com.alan.clients.value.impl.BooleanValue;
import com.alan.clients.ui.click.standard.components.value.ValueComponent;
import com.alan.clients.ui.click.standard.UIColors;
import com.alan.clients.util.gui.GUIUtil;
import com.alan.clients.util.localization.Localization;
import com.alan.clients.util.render.ColorUtil;
import com.alan.clients.util.font.FontManager;
import com.alan.clients.util.font.FontWeight;
import rip.vantage.commons.util.time.StopWatch;

public class BooleanValueComponent extends ValueComponent {
    private final StopWatch ayE = new StopWatch();
    private double axp;

    public BooleanValueComponent(Value<?> var1) {
        super(var1);
    }

    @Override
    public void draw(Vector2d position, int var2, int var3, float var4) {
        this.position = position;
        BooleanValue booleanvalue = (BooleanValue)this.value;
        String s = Localization.ce(this.value.getName());
        FontManager.MAIN.a(16, FontWeight.REGULAR).a(s, this.position.x, this.position.y, UIColors.SECONDARY_TEXT.Z(this.ayD));
        double d0 = this.position.x + FontManager.MAIN.a(16, FontWeight.REGULAR).getStringWidth(s) + 3.0;
        if (booleanvalue.wo()) {
            this.axp = Math.min(5.0, this.axp + (float)this.ayE.getElapsedTime() / 20.0F);
        } else {
            this.axp = Math.max(0.0, this.axp - (float)this.ayE.getElapsedTime() / 20.0F);
        }

        RenderUtil.roundedRectangle(d0 - 2.5 + 5.0, this.position.y - 2.5 + 2.5, 5.0, 5.0, 2.5, UIColors.BACKGROUND.Y(this.ayD));
        if (this.axp != 0.0) {
            RenderUtil.roundedRectangle(
                d0 - this.axp / 2.0 + 5.0, this.position.y - this.axp / 2.0 + 2.5, this.axp, this.axp, this.axp / 2.0, ColorUtil.withAlpha(this.rz().rA(), this.ayD)
            );
        }

        this.ayE.aX();
    }

    @Override
    public boolean e(int var1, int var2, int var3) {
        if (this.position == null) {
            return false;
        }
        BooleanValue booleanvalue = (BooleanValue)this.value;
        if (GUIUtil.c(this.position.x, this.position.y - 3.5, this.getStandardClickGUI().width - 70, this.height, var1, var2)) {
            booleanvalue.setValue(!booleanvalue.wo());
            return true;
        }
        return false;
    }

    @Override
    public void pz() {
    }

    @Override
    public void released() {
        if (this.position != null) {
            String s = Localization.ce(this.value.getName());
            RenderUtil.roundedRectangle(
                this.position.x + FontManager.MAIN.a(16, FontWeight.REGULAR).getStringWidth(s) + 2.0 - this.axp / 2.0 + 4.0,
                this.position.y - this.axp / 2.0 + 2.5,
                this.axp,
                this.axp,
                this.axp / 2.0,
                ColorUtil.withAlpha(this.rz().rA(), this.ayD)
            );
        }
    }

    @Override
    public void key(char var1, int var2) {
    }
}
