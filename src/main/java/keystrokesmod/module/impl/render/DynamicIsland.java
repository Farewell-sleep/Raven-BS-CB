package keystrokesmod.module.impl.render;

import keystrokesmod.module.Module;
import keystrokesmod.module.setting.impl.ButtonSetting;
import keystrokesmod.module.setting.impl.ColorSetting;
import keystrokesmod.module.setting.impl.SliderSetting;
import keystrokesmod.utility.Utils;
import keystrokesmod.utility.font.FontManager;
import keystrokesmod.utility.font.RavenFontRenderer;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.ScaledResolution;
import net.minecraftforge.fml.common.eventhandler.SubscribeEvent;
import net.minecraftforge.fml.common.gameevent.TickEvent;
import org.lwjgl.opengl.GL11;

import java.awt.Color;

public class DynamicIsland extends Module {

    private static final Minecraft mc = Minecraft.getMinecraft();

    private final SliderSetting font;
    private final SliderSetting fontSize;
    private final ColorSetting accentColor;
    private final ButtonSetting textShadow;
    private final ButtonSetting showServer;

    private static final String[] FONT_OPTIONS = FontManager.getHudFontOptions();

    public DynamicIsland() {
        super("DynamicIsland", category.render);
        this.registerSetting(font = new SliderSetting("Font", 0, FONT_OPTIONS));
        this.registerSetting(fontSize = new SliderSetting("Scale", 1.0, 0.5, 2.0, 0.1));
        this.registerSetting(accentColor = new ColorSetting("Accent Color", 255, 80, 80));
        this.registerSetting(textShadow = new ButtonSetting("Text Shadow", true));
        this.registerSetting(showServer = new ButtonSetting("Show Server", true));
    }

    @SubscribeEvent
    public void onRenderTick(TickEvent.RenderTickEvent event) {
        if (event.phase != TickEvent.Phase.END || !Utils.nullCheck()) return;
        if (mc.currentScreen != null || mc.gameSettings.showDebugInfo) return;
        if (!isEnabled()) return;

        ScaledResolution sr = new ScaledResolution(mc);
        RavenFontRenderer fr = getFontRenderer();

        String username = mc.thePlayer.getName();
        int ping = getPing();
        int fps = Minecraft.getDebugFPS();
        String server = getServerIP();

        String clientName = "Raven BS";
        String separator = "  \u00b7  ";

        String fullText;
        if (showServer.isToggled()) {
            fullText = clientName + separator + username + separator + ping + "ms to " + server + separator + fps + "fps";
        } else {
            fullText = clientName + separator + username + separator + ping + "ms" + separator + fps + "fps";
        }

        float textWidth = fr.getStringWidth(fullText);
        float width = textWidth + 28f;
        float height = 26f;
        float x = sr.getScaledWidth() / 2f - width / 2f;
        float y = 6f;

        drawBackground(x, y, width, height);

        float textY = y + (height - fr.getFontHeight()) / 2f;
        float startX = x + 14f;

        int accent = accentColor.getColor();
        int white = 0xFFFFFFFF;

        float cx = startX;

        drawString(fr, clientName, cx, textY, accent);
        cx += fr.getStringWidth(clientName);

        drawString(fr, separator, cx, textY, 0x888888);
        cx += fr.getStringWidth(separator);

        drawString(fr, username, cx, textY, white);
        cx += fr.getStringWidth(username);

        drawString(fr, separator, cx, textY, 0x888888);
        cx += fr.getStringWidth(separator);

        String pingStr = ping + "ms";
        drawString(fr, pingStr, cx, textY, accent);
        cx += fr.getStringWidth(pingStr);

        if (showServer.isToggled()) {
            String toServer = " to " + server;
            drawString(fr, toServer, cx, textY, white);
            cx += fr.getStringWidth(toServer);
        }

        drawString(fr, separator, cx, textY, 0x888888);
        cx += fr.getStringWidth(separator);

        String fpsStr = fps + "fps";
        drawString(fr, fpsStr, cx, textY, white);
    }

    private void drawString(RavenFontRenderer fr, String text, float x, float y, int color) {
        if (textShadow.isToggled()) {
            fr.drawStringWithShadow(text, x, y, color);
        } else {
            fr.drawString(text, x, y, color);
        }
    }

    private void drawBackground(float x, float y, float w, float h) {
        float radius = 10f;

        GL11.glPushMatrix();
        GL11.glEnable(GL11.GL_BLEND);
        GL11.glDisable(GL11.GL_TEXTURE_2D);
        GL11.glBlendFunc(GL11.GL_SRC_ALPHA, GL11.GL_ONE_MINUS_SRC_ALPHA);
        GL11.glEnable(GL11.GL_LINE_SMOOTH);

        Color accent = new Color(accentColor.getColor(), true);

        GL11.glColor4f(0, 0, 0, 0.55f);
        drawRoundedRect(x, y, w, h, radius);

        GL11.glColor4f(accent.getRed() / 255f, accent.getGreen() / 255f, accent.getBlue() / 255f, 0.15f);
        drawRoundedRect(x - 1, y - 1, w + 2, h + 2, radius + 1);

        GL11.glColor4f(1, 1, 1, 0.08f);
        drawRoundedRect(x + 0.5f, y + 0.5f, w - 1f, h - 1f, radius - 0.5f);

        GL11.glEnable(GL11.GL_TEXTURE_2D);
        GL11.glDisable(GL11.GL_BLEND);
        GL11.glPopMatrix();
    }

    private void drawRoundedRect(float x, float y, float w, float h, float radius) {
        GL11.glBegin(GL11.GL_POLYGON);
        for (int i = 0; i <= 90; i += 3) {
            double angle = Math.toRadians(i);
            GL11.glVertex2d(x + radius + Math.cos(angle) * radius, y + radius + Math.sin(angle) * radius);
        }
        for (int i = 90; i <= 180; i += 3) {
            double angle = Math.toRadians(i);
            GL11.glVertex2d(x + w - radius + Math.cos(angle) * radius, y + radius + Math.sin(angle) * radius);
        }
        for (int i = 180; i <= 270; i += 3) {
            double angle = Math.toRadians(i);
            GL11.glVertex2d(x + w - radius + Math.cos(angle) * radius, y + h - radius + Math.sin(angle) * radius);
        }
        for (int i = 270; i <= 360; i += 3) {
            double angle = Math.toRadians(i);
            GL11.glVertex2d(x + radius + Math.cos(angle) * radius, y + h - radius + Math.sin(angle) * radius);
        }
        GL11.glEnd();
    }

    private RavenFontRenderer getFontRenderer() {
        String family = FONT_OPTIONS[(int) font.getInput()];
        return FontManager.getHudRenderer(family, (float) fontSize.getInput());
    }

    private int getPing() {
        try {
            if (mc.thePlayer == null || mc.getNetHandler() == null) return 0;
            if (mc.thePlayer.sendQueue != null
                    && mc.thePlayer.sendQueue.getPlayerInfo(mc.thePlayer.getUniqueID()) != null) {
                return mc.thePlayer.sendQueue.getPlayerInfo(mc.thePlayer.getUniqueID()).getResponseTime();
            }
        } catch (Exception ignored) {}
        return 0;
    }

    private String getServerIP() {
        try {
            if (mc.theWorld != null) {
                if (mc.isIntegratedServerRunning()) {
                    return "SinglePlayer";
                } else if (mc.getCurrentServerData() != null) {
                    return mc.getCurrentServerData().serverIP;
                }
            }
        } catch (Exception ignored) {}
        return "SinglePlayer";
    }
}
