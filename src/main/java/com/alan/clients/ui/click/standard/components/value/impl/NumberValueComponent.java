package com.alan.clients.ui.click.standard.components.value.impl;

import com.alan.clients.util.render.RenderUtil;
import com.alan.clients.util.vector.Vector2d;
import com.alan.clients.value.Value;
import com.alan.clients.value.impl.NumberValue;
import com.alan.clients.ui.click.standard.components.value.ValueComponent;
import com.alan.clients.ui.click.standard.UIColors;
import com.alan.clients.util.gui.GUIUtil;
import com.alan.clients.util.gui.textbox.TextAlign;
import com.alan.clients.util.gui.textbox.TextBox;
import com.alan.clients.util.localization.Localization;
import com.alan.clients.util.math.MathUtil;
import com.alan.clients.util.render.ColorUtil;
import com.alan.clients.util.font.FontManager;
import com.alan.clients.util.font.FontWeight;
import rip.vantage.commons.util.time.StopWatch;

public class NumberValueComponent extends ValueComponent {
    private static final double azf = 100.0;
    private final double azg = 5.0;
    private final StopWatch azh = new StopWatch();
    public boolean azi;
    private double azj;
    private double azk;
    private double azl;
    private boolean ayQ;
    private float ayR;
    public final TextBox azm = new TextBox(
        new Vector2d(0.0, 0.0),
        FontManager.MAIN.a(16, FontWeight.REGULAR),
        UIColors.SECONDARY_TEXT.pV(),
        TextAlign.LEFT,
        ((NumberValue)this.value).getDefaultValue().toString().replace(".0", ""),
        45.0F,
        "1234567890."
    );

    public NumberValueComponent(Value<?> var1) {
        super(var1);
        this.pU();
    }

    public void pU() {
        NumberValue numbervalue = (NumberValue)this.value;
        this.azj = Math.min(
            Math.max(
                0.0, (-numbervalue.getMin().doubleValue() + numbervalue.wo().doubleValue()) / (-numbervalue.getMin().doubleValue() + numbervalue.getMax().doubleValue())
            ),
            1.0
        );
    }

    @Override
    public void draw(Vector2d position, int var2, int var3, float var4) {
        this.position = position;
        NumberValue numbervalue = (NumberValue)this.value;
        String s = String.valueOf(numbervalue.wo().doubleValue());
        String s1 = Localization.ce(this.value.getName());
        float f = FontManager.MAIN.a(16, FontWeight.REGULAR).getStringWidth(s1) + 7;
        if (s.endsWith(".0")) {
            s = s.replace(".0", "");
        }

        this.ayQ = GUIUtil.c(this.position.x + f - 5.0, this.position.y - 3.5, 110.0, this.height, var2, var3);
        if (this.ayQ) {
            this.ayR = Math.min(1.0F, this.ayR + (float)this.azh.getElapsedTime() / 200.0F);
        } else {
            this.ayR = Math.max(0.0F, this.ayR - (float)this.azh.getElapsedTime() / 200.0F);
        }

        FontManager.MAIN.a(16, FontWeight.REGULAR).a(s1, this.position.x, this.position.y, UIColors.SECONDARY_TEXT.Z(this.ayD));
        this.azm.setPosition(new Vector2d(this.position.x + f + 105.0, this.position.y));
        if (!this.azm.isSelected()) {
            this.azm.bW(s);
        }

        this.azm.setWidth(20.0F);
        this.azm.setColor(ColorUtil.withAlpha(this.azm.getColor(), this.ayD));
        this.azm.draw();
        RenderUtil.roundedRectangle(this.position.x + f, this.position.y + 1.5, 100.0, 2.0, 1.0, UIColors.BACKGROUND.Y(this.ayD));
        this.azk = this.position.x + f;
        if (this.getStandardClickGUI().axS < 0.8) {
            this.azi = false;
        }

        if (this.azi) {
            this.azj = var2 - this.azk;
            this.azj /= 100.0;
            this.azj = Math.max(Math.min(this.azj, 1.0), 0.0);
            numbervalue.n(numbervalue.getMin().doubleValue() + (numbervalue.getMax().doubleValue() - numbervalue.getMin().doubleValue()) * this.azj);
            numbervalue.n(MathUtil.m(numbervalue.wo().doubleValue(), numbervalue.getDecimalPlaces().floatValue()));
        }

        for (int i = 0; i <= this.azh.getElapsedTime(); i++) {
            this.azl = (this.azl * 29.0 + this.azj) / 30.0;
        }

        RenderUtil.roundedRectangle(this.azk + this.azl * 100.0 - 2.5, this.position.y, 5.0, 5.0, 2.5, ColorUtil.withAlpha(this.rz().rA(), this.ayD));
        this.azh.aX();
    }

    @Override
    public boolean e(int var1, int var2, int var3) {
        if (this.position == null) {
            return false;
        }

        boolean flag = var3 == 0;
        if (flag && this.ayQ) {
            this.azi = true;
            return true;
        }
        this.azm.click(var1, var2, var3);
        return false;
    }

    @Override
    public void pz() {
        this.azi = false;
    }

    @Override
    public void released() {
        if (this.position != null) {
            RenderUtil.roundedRectangle(this.azk + this.azl * 100.0 - 2.5, this.position.y, 5.0, 5.0, 2.5, ColorUtil.withAlpha(this.rz().rA(), this.ayD));
        }
    }

    @Override
    public void key(char var1, int var2) {
        if (var2 == 28) {
            NumberValue numbervalue = (NumberValue)this.value;
            if (this.azm.getText().isEmpty()) {
                numbervalue.n(numbervalue.getDefaultValue());
            } else {
                double text = Double.parseDouble(this.azm.getText());
                numbervalue.n(text);
            }

            this.azm.setSelected(false);
            this.pU();
        } else {
            this.azm.key(var1, var2);
        }
    }
}
