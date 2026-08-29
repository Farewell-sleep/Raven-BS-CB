package com.alan.clients.ui.click.standard.components.value.impl;

import com.alan.clients.util.render.RenderUtil;
import com.alan.clients.util.vector.Vector2d;
import com.alan.clients.util.vector.Vector2f;
import com.alan.clients.value.Value;
import com.alan.clients.value.impl.ColorValue;
import com.alan.clients.ui.click.standard.components.value.ValueComponent;
import com.alan.clients.ui.click.standard.UIColors;
import com.alan.clients.util.gui.GUIUtil;
import com.alan.clients.util.localization.Localization;
import com.alan.clients.util.render.ColorUtil;
import com.alan.clients.util.font.FontManager;
import com.alan.clients.util.font.FontWeight;
import java.awt.Color;
import net.minecraft.client.gui.GuiScreen;
import net.minecraft.util.MathHelper;

public class ColorValueComponent extends ValueComponent {
    private static final double ayT = 5.0;
    private boolean ayU = false;
    private boolean ayV;
    private boolean ayW;
    private final float ayX = 10.0F;
    private final float ayY = 0.5F;
    private float ayZ = 0.0F;
    private double aza = 90.0;
    private double azb = 80.0;
    private Vector2f azc = new Vector2f(1.0F, 1.0F);
    private Color azd = Color.RED;

    public ColorValueComponent(Value<?> var1) {
        super(var1);
    }

    @Override
    public void draw(Vector2d position, int var2, int var3, float var4) {
        this.position = position;
        this.aza = 105.0;
        this.azb = 120.0;
        ColorValue colorvalue = (ColorValue)this.value;
        String s = Localization.ce(this.value.getName());
        float f = FontManager.MAIN.a(16, FontWeight.REGULAR).getStringWidth(s) + 4;
        FontManager.MAIN.a(16, FontWeight.REGULAR).a(s, this.position.x, this.position.y, UIColors.SECONDARY_TEXT.pW());
        RenderUtil.roundedRectangle(this.position.x + f, this.position.y, 15.0, 7.0, 2.5, colorvalue.wo());
        this.height = this.ayU ? 110.0 : 15.0;
        if (this.ayU) {
            double d0 = this.position.x + 0.5 + 10.0 + f + 8.0;
            double d1 = this.position.y + 0.5;
            RenderUtil.dropShadow(10, (float)d0, (float)d1, (float)this.aza, (float)this.azb - 15.0F, 40.0, this.getStandardClickGUI().round * 2);
            RenderUtil.roundedRectangle(d0 - 0.5, this.position.y, this.aza, this.azb - 15.0, this.getStandardClickGUI().round - 3, UIColors.SECONDARY.pV());
            RenderUtil.roundedRectangle(d0, d1, this.aza - 1.0, this.azb - 1.0 - 15.0, this.getStandardClickGUI().round - 3, UIColors.BACKGROUND.pV());
            double d2 = this.azb * 0.55;
            RenderUtil.a(d0, d1, this.aza - 1.0, d2, 7.0, Color.WHITE, this.azd, false);
            RenderUtil.a(d0 - 0.5, d1, this.aza - 1.0 + 1.0, d2 + 0.5, 0.5, Color.BLACK, new Color(0, 0, 0, 0), true);
            double d3 = 8.5;
            double d4 = d0 + d3;
            double d5 = d1 + d3 + d2 - 5.0;
            double d6 = this.aza - d3 * 2.0;
            RenderUtil.rainbowRectangle(d4, d5 + 2.5, d6, this.getStandardClickGUI().round - 5);
            RenderUtil.dropShadow(
                30,
                (float)(d0 + d3),
                (float)(d1 + d2 + d3 + d3 + this.getStandardClickGUI().round - 11.0),
                15.0,
                15.5,
                40.0,
                this.getStandardClickGUI().round / 2.0F
            );
            RenderUtil.roundedRectangle(d0 + d3, d1 + d2 + d3 + d3 + this.getStandardClickGUI().round - 11.0, 15.0, 15.5, 3.5, colorvalue.wo());
            if (this.ayV) {
                this.azc = new Vector2f((float)(var2 - d0), (float)(var3 - d1));
                this.azc.x = MathHelper.clamp_float(this.azc.x, 0.0F, (float)this.aza);
                this.azc.y = MathHelper.clamp_float(this.azc.y, 0.0F, (float)d2);
                Color color = ColorUtil.a(new Color(0, 0, 0, 0), ColorUtil.a(this.azd, Color.WHITE, this.azc.x / this.aza), this.azc.y / d2);
                colorvalue.n(color);
            } else if (this.ayW) {
                this.ayZ = (float)(var2 - d4);
                this.ayZ = MathHelper.clamp_float(this.ayZ, 0.0F, (float)d6);
                this.azd = Color.getHSBColor((float)(this.ayZ / d6), 1.0F, 1.0F);
                Color color2 = ColorUtil.a(new Color(0, 0, 0, 0), ColorUtil.a(this.azd, Color.WHITE, this.azc.x / this.aza), this.azc.y / d2);
                colorvalue.n(color2);
            }

            RenderUtil.roundedRectangle(
                d4 + this.ayZ - this.getStandardClickGUI().round / 2.0F + 0.5,
                d5 + 0.5,
                this.getStandardClickGUI().round - 1,
                this.getStandardClickGUI().round - 1,
                this.getStandardClickGUI().round / 3.0F + 1.0F,
                this.azd
            );
            RenderUtil.roundedOutlineRectangle(
                d4 + this.ayZ - this.getStandardClickGUI().round / 2.0F + 0.5,
                d5 + 0.5,
                this.getStandardClickGUI().round - 1,
                this.getStandardClickGUI().round - 1,
                this.getStandardClickGUI().round / 5.0F + 1.0F,
                1.0,
                Color.BLACK
            );
            if (this.azc.x != -1.0F && this.azc.y != -1.0F) {
                RenderUtil.roundedRectangle(d0 - 1.0 + this.azc.x - 2.5, d1 - 1.0 + this.azc.y - 2.5, 7.0, 7.0, 3.5, Color.WHITE);
                RenderUtil.roundedRectangle(d0 - 0.5 + this.azc.x - 2.5, d1 - 0.5 + this.azc.y - 2.5, 6.0, 6.0, 3.0, Color.BLACK);
                RenderUtil.roundedRectangle(d0 + this.azc.x - 2.5, d1 + this.azc.y - 2.5, 5.0, 5.0, 2.5, colorvalue.wo());
            }

            Color color1 = colorvalue.wo();
            double d7 = d0 + d3 * 2.0 + 15.0;
            double d8 = d1 + d2 + d3 + d3 + this.getStandardClickGUI().round - 11.0;
            FontManager.MAIN.a(17, FontWeight.REGULAR).drawString(color1.getRed() + "", d7 + d3, d8, UIColors.SECONDARY_TEXT.pW());
            FontManager.MAIN.a(17, FontWeight.REGULAR).drawString(color1.getGreen() + "", d7 + 30.0, d8, UIColors.SECONDARY_TEXT.pW());
            FontManager.MAIN.a(17, FontWeight.REGULAR).drawString(color1.getBlue() + "", d7 + d3 * 6.0, d8, UIColors.SECONDARY_TEXT.pW());
            double d9 = d8 + 13.0;
            FontManager.MAIN
                .a(13, FontWeight.REGULAR)
                .a(String.format("#%02X%02X%02X", color1.getRed(), color1.getGreen(), color1.getBlue()), d7, d9, new Color(55, 59, 61).hashCode());
        }
    }

    @Override
    public boolean e(int var1, int var2, int var3) {
        if (this.position == null) {
            return false;
        }

        float f = FontManager.MAIN.a(16, FontWeight.REGULAR).getStringWidth(Localization.ce(this.value.getName())) + 4;
        this.ayV = this.ayU && GUIUtil.c(this.position.x + 10.0 + f, this.position.y, this.aza, this.azb * 0.55, var1, var2);
        this.ayW = this.ayU && GUIUtil.c(this.position.x + 10.0 + f, this.position.y + this.azb * 0.55, this.aza, 20.0, var1, var2);
        double d0 = this.position.x + 14.5 + f;
        double d1 = this.position.y + 0.5;
        double d2 = d0 + 32.0;
        double d3 = d1 + this.azb - 40.0 + this.getStandardClickGUI().round;
        if (GUIUtil.c(d2, d3, 60.0, 10.0, var1, var2)) {
            Color color = (Color)this.value.wo();
            GuiScreen.setClipboardString(color.getRed() + ", " + color.getBlue() + ", " + color.getGreen());
        } else if (GUIUtil.c(d2, d3 + 13.0, 60.0, 10.0, var1, var2)) {
            Color color1 = (Color)this.value.wo();
            GuiScreen.setClipboardString(String.format("#%02X%02X%02X", color1.getRed(), color1.getGreen(), color1.getBlue()));
        }

        this.ayU = (this.getStandardClickGUI().overlayPresent == null || this.ayU)
            && (
                this.ayV
                    || GUIUtil.c(this.position.x + 10.0 + f, this.position.y + this.azb * 0.55, this.aza, 52.0, var1, var2)
                    || !this.ayU && GUIUtil.c(this.position.x, this.position.y - 3.5, this.getStandardClickGUI().width - 70, this.height, var1, var2)
            );
        return false;
    }

    @Override
    public void pz() {
        this.ayV = this.ayW = false;
    }

    @Override
    public void released() {
    }

    @Override
    public void key(char var1, int var2) {
    }
}
