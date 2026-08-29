package com.alan.clients.ui.click.standard.components.value.impl;

import com.alan.clients.util.render.RenderUtil;
import com.alan.clients.util.vector.Vector2d;
import com.alan.clients.value.Value;
import com.alan.clients.value.impl.BoundsNumberValue;
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
import java.awt.Color;
import rip.vantage.commons.util.time.StopWatch;

public class BoundsNumberValueComponent extends ValueComponent {
    private final double ayF = 100.0;
    private final StopWatch ayG = new StopWatch();
    private final double ayH = 5.0;
    public boolean ayI;
    public boolean ayJ;
    private double ayK;
    private double ayL;
    private double ayM;
    private double ayN;
    private double ayO;
    private double ayP;
    private boolean ayQ;
    private float ayR;
    public final TextBox ayS = new TextBox(
        new Vector2d(0.0, 0.0),
        FontManager.MAIN.a(16, FontWeight.REGULAR),
        UIColors.SECONDARY_TEXT.pV(),
        TextAlign.LEFT,
        ((BoundsNumberValue)this.value).getDefaultValue().toString().replace(".0", "") + " " + ((BoundsNumberValue)this.value).wB().toString().replace(".0", ""),
        100.0F,
        "1234567890. "
    );

    public BoundsNumberValueComponent(Value<?> var1) {
        super(var1);
        this.pU();
    }

    public void pU() {
        BoundsNumberValue boundsnumbervalue = (BoundsNumberValue)this.value;
        double d0 = (-boundsnumbervalue.wx().doubleValue() + boundsnumbervalue.wo().doubleValue())
            / (-boundsnumbervalue.wx().doubleValue() + boundsnumbervalue.wy().doubleValue());
        double d1 = (-boundsnumbervalue.wx().doubleValue() + boundsnumbervalue.wA().doubleValue())
            / (-boundsnumbervalue.wx().doubleValue() + boundsnumbervalue.wy().doubleValue());
        this.ayK = Math.min(Math.max(d0, 0.0), 1.0);
        this.ayL = Math.min(Math.max(d1, 0.0), 1.0);
    }

    @Override
    public void draw(Vector2d position, int var2, int var3, float var4) {
        this.position = position;
        BoundsNumberValue boundsnumbervalue = (BoundsNumberValue)this.value;
        String s = String.valueOf(boundsnumbervalue.wo().doubleValue());
        String s1 = String.valueOf(boundsnumbervalue.wA().doubleValue());
        if (s.endsWith(".0")) {
            s = s.replace(".0", "");
        }

        if (s1.endsWith(".0")) {
            s1 = s1.replace(".0", "");
        }

        String s2 = s + " " + s1;
        String s3 = Localization.ce(this.value.getName());
        float f = FontManager.MAIN.a(16, FontWeight.REGULAR).getStringWidth(s3) + 7;
        this.ayQ = GUIUtil.c(this.position.x + f - 5.0, this.position.y - 3.5, 110.0, this.height, var2, var3);
        if (this.ayQ) {
            this.ayR = Math.min(1.0F, this.ayR + (float)this.ayG.getElapsedTime() / 200.0F);
        } else {
            this.ayR = Math.max(0.0F, this.ayR - (float)this.ayG.getElapsedTime() / 200.0F);
        }

        FontManager.MAIN.a(16, FontWeight.REGULAR).a(s3, this.position.x, this.position.y, UIColors.SECONDARY_TEXT.Z(this.ayD));
        this.ayS.setPosition(new Vector2d(this.position.x + f + 105.0, this.position.y));
        if (!this.ayS.isSelected()) {
            this.ayS.bW(s2);
        }

        this.ayS.setWidth(20.0F);
        this.ayS.setColor(ColorUtil.withAlpha(this.ayS.getColor(), this.ayD));
        this.ayS.draw();
        RenderUtil.roundedRectangle(this.position.x + f, this.position.y + 1.5, 100.0, 2.0, 1.0, UIColors.BACKGROUND.Y(Math.min(this.ayD, UIColors.BACKGROUND.pV().getAlpha())));
        this.ayM = this.position.x + f;
        this.ayN = this.position.x + f;
        if (this.getStandardClickGUI().axS < 0.8) {
            this.ayI = this.ayJ = false;
        }

        if (this.ayI) {
            this.ayK = var2 - this.ayM;
            this.ayK /= 100.0;
            this.ayK = Math.max(Math.min(this.ayK, 1.0), 0.0);
            boundsnumbervalue.n(boundsnumbervalue.wx().doubleValue() + (boundsnumbervalue.wy().doubleValue() - boundsnumbervalue.wx().doubleValue()) * this.ayK);
            boundsnumbervalue.n(MathUtil.m(boundsnumbervalue.wo().doubleValue(), boundsnumbervalue.wz().doubleValue()));
            if (this.ayK > this.ayL) {
                this.ayL = this.ayK;
                boundsnumbervalue.a(
                    boundsnumbervalue.wx().doubleValue() + (boundsnumbervalue.wy().doubleValue() - boundsnumbervalue.wx().doubleValue()) * this.ayL
                );
                boundsnumbervalue.a(MathUtil.m(boundsnumbervalue.wA().doubleValue(), boundsnumbervalue.wz().doubleValue()));
            }
        } else if (this.ayJ) {
            this.ayL = var2 - this.ayN;
            this.ayL /= 100.0;
            this.ayL = Math.max(Math.min(this.ayL, 1.0), 0.0);
            boundsnumbervalue.a(boundsnumbervalue.wx().doubleValue() + (boundsnumbervalue.wy().doubleValue() - boundsnumbervalue.wx().doubleValue()) * this.ayL);
            boundsnumbervalue.a(MathUtil.m(boundsnumbervalue.wA().doubleValue(), boundsnumbervalue.wz().doubleValue()));
            if (this.ayL < this.ayK) {
                this.ayK = this.ayL;
                boundsnumbervalue.n(
                    boundsnumbervalue.wx().doubleValue() + (boundsnumbervalue.wy().doubleValue() - boundsnumbervalue.wx().doubleValue()) * this.ayK
                );
                boundsnumbervalue.n(MathUtil.m(boundsnumbervalue.wo().doubleValue(), boundsnumbervalue.wz().doubleValue()));
            }
        }

        for (int i = 0; i <= this.ayG.getElapsedTime(); i++) {
            this.ayO = (this.ayO * 29.0 + this.ayK) / 30.0;
            this.ayP = (this.ayP * 29.0 + this.ayL) / 30.0;
        }

        double d0 = this.ayM + this.ayO * 100.0;
        double d1 = this.ayN + this.ayP * 100.0;
        double d2 = d1 - d0;
        if (this.ayK != this.ayL) {
            RenderUtil.roundedRectangle(d0, this.position.y + 1.5, d2, 2.0, 1.0, ColorUtil.withAlpha(this.rz().rA(), Math.min(70, this.ayD)));
        }

        RenderUtil.roundedRectangle(d0 - 2.5, this.position.y, 5.0, 5.0, 2.5, ColorUtil.withAlpha(this.rz().rA(), this.ayD));
        if (this.ayK != this.ayL) {
            RenderUtil.roundedRectangle(d1 - 2.5, this.position.y, 5.0, 5.0, 2.5, ColorUtil.withAlpha(this.rz().rA(), this.ayD));
        }

        this.ayG.aX();
    }

    @Override
    public boolean e(int var1, int var2, int var3) {
        if (this.position == null) {
            return false;
        }

        boolean flag = var3 == 0;
        if (flag && this.ayQ) {
            double d0 = Math.abs(var1 - (this.ayM + this.ayO * 100.0));
            double d1 = Math.abs(var1 - (this.ayN + this.ayP * 100.0));
            if (d0 < d1) {
                this.ayI = true;
            } else {
                this.ayJ = true;
            }

            return true;
        }
        this.ayS.click(var1, var2, var3);
        return false;
    }

    @Override
    public void pz() {
        this.ayI = this.ayJ = false;
    }

    @Override
    public void released() {
        if (this.position != null) {
            double d0 = this.ayM + this.ayO * 100.0;
            double d1 = this.ayN + this.ayP * 100.0;
            Color color = ColorUtil.withAlpha(this.rz().rA(), this.ayD);
            RenderUtil.roundedRectangle(d0 - 2.5, this.position.y, 5.0, 5.0, 2.5, color);
            RenderUtil.roundedRectangle(d1 - 2.5, this.position.y, 5.0, 5.0, 2.5, color);
        }
    }

    @Override
    public void key(char var1, int var2) {
        if (var2 != 28) {
            this.ayS.key(var1, var2);
        } else {
            BoundsNumberValue boundsnumbervalue = (BoundsNumberValue)this.value;
            String[] astring = this.ayS.getText().split(" ");
            if (!this.ayS.isEmpty() && astring.length == 2 && !astring[0].replace(" ", "").isEmpty() && !astring[1].replace(" ", "").isEmpty()) {
                boundsnumbervalue.n(Double.parseDouble(astring[0]));
                boundsnumbervalue.a(Double.parseDouble(astring[1]));
            } else {
                boundsnumbervalue.n(boundsnumbervalue.getDefaultValue());
                boundsnumbervalue.a(boundsnumbervalue.wB());
            }

            this.ayS.setSelected(false);
            this.pU();
        }
    }
}
