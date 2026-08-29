package com.alan.clients.ui.click.standard.screen.impl;

import com.alan.clients.ui.click.standard.components.language.LanguageComponent;
import com.alan.clients.util.interfaces.InstanceAccess;
import com.alan.clients.util.render.RenderUtil;
import com.alan.clients.util.vector.Vector2f;
import com.alan.clients.ui.click.standard.UIColors;
import com.alan.clients.ui.click.standard.screen.Screen;
import com.alan.clients.util.gui.ScrollUtil;
import com.alan.clients.util.localization.Locale;
import com.alan.clients.util.localization.Localization;
import com.alan.clients.util.render.ScissorUtil;
import com.alan.clients.util.font.FontManager;
import com.alan.clients.util.font.FontWeight;
import java.awt.Color;
import java.util.ArrayList;
import java.util.Iterator;
import net.minecraft.client.gui.ScaledResolution;
import org.lwjgl.opengl.GL11;

public class LanguageScreen implements Screen, InstanceAccess {
    private final ArrayList<LanguageComponent> languages = new ArrayList<>();
    private final ScrollUtil azQ = new ScrollUtil();

    public LanguageScreen() {
        for (Locale locale : Locale.values()) {
            this.languages.add(new LanguageComponent(locale, Localization.a("language_local", locale), Localization.a("language_english", locale)));
        }
    }

    @Override
    public void onRender(int var1, int var2, float var3) {
        this.azQ.qx();
        Vector2f vector2f = this.getStandardClickGUI().getScale();
        Vector2f vector2f1 = this.getStandardClickGUI().getPosition();
        double d0 = this.getStandardClickGUI().getSidebar().aym;
        double d1 = vector2f.getX() + d0;
        double d2 = vector2f.getY() + 40.0;
        double d3 = vector2f1.x - d0;
        double d4 = Math.max(0.0, vector2f1.y - 40.0);
        GL11.glPushAttrib(524288);
        ScissorUtil.hK();
        ScissorUtil.scissor(new ScaledResolution(aEg), d1, d2, d3, d4);

        for (int i = 0; i < this.languages.size(); i++) {
            this.languages.get(i).draw((i + 1) * 46 + this.azQ.tE());
        }

        ScissorUtil.disable();
        GL11.glPopAttrib();
        RenderUtil.a(
            vector2f.getX() + d0, vector2f.getY(), vector2f1.x - d0, 40.0, this.getStandardClickGUI().round, UIColors.BACKGROUND.pV(), true, true, false, false
        );
        FontManager.MAIN
            .a(16, FontWeight.REGULAR)
            .drawCenteredString(Localization.ce("ui.language.text"), vector2f.getX() + vector2f1.getX() - 20.0F, vector2f.getY() + 20.0F, new Color(255, 255, 255, 128).getRGB());
        this.azQ.V(-2000.0);
    }

    @Override
    public void f(int var1, int var2, int var3) {
        Iterator iterator = this.languages.iterator();

        while (iterator.hasNext()) {
            ((LanguageComponent)iterator.next()).click(var1, var2);
        }
    }
}
