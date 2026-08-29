package com.alan.clients.ui.click.standard.components.language;

import com.alan.clients.Client;
import com.alan.clients.util.interfaces.InstanceAccess;
import com.alan.clients.util.render.RenderUtil;
import com.alan.clients.util.vector.Vector2d;
import com.alan.clients.util.vector.Vector2f;
import com.alan.clients.ui.click.standard.UIColors;
import com.alan.clients.util.gui.GUIUtil;
import com.alan.clients.util.localization.Locale;
import com.alan.clients.util.font.FontManager;
import com.alan.clients.util.font.FontWeight;
import lombok.Generated;
import net.minecraft.util.ResourceLocation;

public class LanguageComponent implements InstanceAccess {
    private final Locale locale;
    private final String ayr;
    private final String ays;
    private double lastY;

    public void draw(double var1) {
        Vector2f vector2f = this.getStandardClickGUI().getScale();
        double d0 = this.getStandardClickGUI().getSidebar().aym;
        RenderUtil.roundedRectangle(vector2f.getX() + d0 + 8.0, vector2f.getY() + var1, 285.0, 38.0, 6.0, UIColors.OVERLAY.pV());
        FontManager.MAIN
            .a(20, FontWeight.REGULAR)
            .a(
                this.ays,
                vector2f.getX() + d0 + 18.0,
                vector2f.getY() + var1 + 9.0,
                Client.a.getLocale().equals(this.locale) ? this.rz().getAccentColor(new Vector2d(0.0, vector2f.y / 5.0F)).getRGB() : UIColors.TEXT.pW()
            );
        FontManager.MAIN.a(17, FontWeight.REGULAR).a(this.ayr, vector2f.getX() + d0 + 18.0, vector2f.getY() + var1 + 24.0, UIColors.TEXT.Z(100));
        RenderUtil.image(
            new ResourceLocation("rise/icons/language/" + this.locale.getFile() + ".png"),
            vector2f.getX() + d0 + FontManager.MAIN.a(20, FontWeight.REGULAR).getStringWidth(this.ays) + 25.0,
            vector2f.getY() + var1 + 5.0,
            15.0,
            15.0
        );
        this.lastY = var1;
    }

    public void click(double var1, double var3) {
        Vector2f vector2f = this.getStandardClickGUI().getScale();
        double d0 = this.getStandardClickGUI().getSidebar().aym;
        if (GUIUtil.c(vector2f.getX() + d0 + 8.0, vector2f.getY() + this.lastY, 285.0, 38.0, var1, var3)) {
            Client.a.a(this.locale);
        }
    }

    @Generated
    public Locale getLocale() {
        return this.locale;
    }

    @Generated
    public String pH() {
        return this.ayr;
    }

    @Generated
    public String pI() {
        return this.ays;
    }

    @Generated
    public double getLastY() {
        return this.lastY;
    }

    @Generated
    public LanguageComponent(Locale locale, String var2, String var3) {
        this.locale = locale;
        this.ayr = var2;
        this.ays = var3;
    }
}
