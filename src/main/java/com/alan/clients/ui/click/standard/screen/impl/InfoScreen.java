package com.alan.clients.ui.click.standard.screen.impl;

import com.alan.clients.Client;
import com.alan.clients.ui.click.standard.screen.Screen;
import com.alan.clients.util.font.FontManager;
import com.alan.clients.util.interfaces.InstanceAccess;
import com.alan.clients.util.render.ScissorUtil;
import com.alan.clients.util.vector.Vector2f;
import com.alan.clients.util.font.FontWeight;
import java.awt.Color;
import net.minecraft.client.gui.ScaledResolution;

public class InfoScreen implements Screen, InstanceAccess {
    public InfoScreen() {
    }

    @Override
    public void onRender(int var1, int var2, float var3) {
        Vector2f vector2f = this.getStandardClickGUI().getScale();
        Vector2f vector2f1 = this.getStandardClickGUI().getPosition();
        double d0 = this.getStandardClickGUI().getSidebar().aym;
        FontManager.MAIN.a(32, FontWeight.REGULAR).a(Client.b, vector2f.getX() + d0 + 20.0, vector2f.getY() + 20.0F, Color.WHITE.getRGB());
        FontManager.MAIN
            .a(16, FontWeight.REGULAR)
            .a(
                "v16",
                vector2f.getX() + d0 + 20.0 + FontManager.MAIN.a(32, FontWeight.REGULAR).getStringWidth(Client.b),
                vector2f.getY() + 18.0F,
                new Color(255, 255, 255, 100).getRGB()
            );
        FontManager.MAIN.a(17, FontWeight.REGULAR).a("16.1", vector2f.getX() + d0 + 20.0, vector2f.getY() + 50.0F, new Color(255, 255, 255, 164).getRGB());
        double now = System.currentTimeMillis() / 1000.0 % 45.0 * 11.0;
        ScissorUtil.hK();
        ScissorUtil.scissor(new ScaledResolution(aEg), vector2f.getX() + d0, vector2f.getY() + 90.0F, vector2f1.getX(), vector2f1.getY() - 175.0F);
        FontManager.MAIN
            .a(17, FontWeight.REGULAR)
            .a(qt(), vector2f.getX() + d0 + 20.0, vector2f.getY() + 100.0F - now + (vector2f1.getY() - 175.0F), new Color(164, 164, 164, 64).getRGB());
        FontManager.MAIN
            .a(17, FontWeight.REGULAR)
            .a(qu(), vector2f.getX() + d0 + 155.0, vector2f.getY() + 100.0F - now + (vector2f1.getY() - 175.0F), new Color(164, 164, 164, 64).getRGB());
        ScissorUtil.disable();
    }

    private static String qt() {
        return "Raven BS v16.1\n\nDesigned and built by Alan and Hazsi.\n\nAdditional Development\n  -> Nicklas, Tecnio, Patrick, Strikeless\n\nUI/UX Design\n  -> Hazsi, Alan\n\nRendering/OpenGL\n  -> Patrick, Strikeless\n\nProtection and Obfuscation\n  -> Alan, NyanCatForEver\n\nScripting Documentation\n  -> Alice\n\nLocalization\n-> a cat (Romanian)\n-> Alice (Russian)\n-> Bx2 (Arabic)\n-> El Gatito Grande (Portuguese)\n-> days (Hebrew)\n-> Duits/Jess (Indonesian)\n-> fan87 (Chinese Traditional)\n-> Grekgamer13 (Greek)\n-> Jb (Austrian)\n-> kinja (Polish)\n-> I_only_die_twice (Swedish)\n\nSpecial Thanks\n  -> Auth and Error as contributing to development\n  -> Config and script makers\n  -> You, as a user of Rise. Thank you, on behalf of all of us!\n";
    }

    private static String qu() {
        return "\n\n\n\n\n\n\n\n\n\n\n\n\n\n\n\n\n\n\n\n-> im ray (Latvian)\n-> Novus (Spanish)\n-> Mongrall (Hungarian)\n-> MOON (Italian)\n-> Stimular (Czech)\n-> Tapludeforfair (French)\n-> toastedwaffles (Vietnamese)\n-> trollo (German)\n-> Velcola (Norwegian)\n-> whoistinywifi (Thai)\n-> YK_FCZ (Chinese Simplified)\n";
    }
}
