package keystrokesmod.module.impl.render;

import keystrokesmod.module.Module;
import keystrokesmod.module.setting.impl.ButtonSetting;
import keystrokesmod.module.setting.impl.SliderSetting;
import keystrokesmod.utility.RenderUtils;
import keystrokesmod.utility.Utils;
import keystrokesmod.utility.font.FontManager;
import keystrokesmod.utility.font.RavenFontRenderer;
import net.minecraft.client.Minecraft;
import net.minecraftforge.fml.common.eventhandler.SubscribeEvent;
import net.minecraftforge.fml.common.gameevent.TickEvent;

import java.awt.Color;

public class WaterMark extends Module {

    private static final String[] MODES = {"Exhibition", "Modern", "Simple", "WeedHack"};
    private static final String[] FONT_OPTIONS = FontManager.getHudFontOptions();

    private final SliderSetting mode;
    private final SliderSetting font;
    private final SliderSetting fontSize;
    private final ButtonSetting shadow;
    private final ButtonSetting rainbow;
    private final ButtonSetting showFPS;
    private final ButtonSetting showPing;

    public WaterMark() {
        super("WaterMark", category.render);
        this.registerSetting(mode = new SliderSetting("Mode", 0, MODES));
        this.registerSetting(font = new SliderSetting("Font", 0, FONT_OPTIONS));
        this.registerSetting(fontSize = new SliderSetting("Scale", 1.0, 0.5, 2.0, 0.1));
        this.registerSetting(shadow = new ButtonSetting("Shadow", true));
        this.registerSetting(rainbow = new ButtonSetting("Rainbow", true));
        this.registerSetting(showFPS = new ButtonSetting("Show FPS", true));
        this.registerSetting(showPing = new ButtonSetting("Show Ping", true));
    }

    @Override
    public String getInfo() {
        return MODES[(int) mode.getInput()];
    }

    @SubscribeEvent
    public void onRenderTick(TickEvent.RenderTickEvent event) {
        if (event.phase != TickEvent.Phase.END || !Utils.nullCheck()) return;
        if (mc.currentScreen != null || mc.gameSettings.showDebugInfo) return;

        RavenFontRenderer fr = getFontRenderer();

        switch ((int) mode.getInput()) {
            case 0:
                renderExhibition(fr);
                break;
            case 1:
                renderModern(fr);
                break;
            case 2:
                renderSimple(fr);
                break;
            case 3:
                renderWeedHack(fr);
                break;
        }
    }

    private RavenFontRenderer getFontRenderer() {
        String family = FONT_OPTIONS[(int) font.getInput()];
        return FontManager.getHudRenderer(family, (float) fontSize.getInput());
    }

    private int getRainbowColor(long time, int offset) {
        float hue = ((time + offset) % 3000) / 3000.0f;
        return Color.HSBtoRGB(hue, 0.7f, 1.0f);
    }

    private int getPing() {
        if (mc.thePlayer != null && mc.thePlayer.sendQueue != null
                && mc.thePlayer.sendQueue.getPlayerInfo(mc.thePlayer.getUniqueID()) != null) {
            return mc.thePlayer.sendQueue.getPlayerInfo(mc.thePlayer.getUniqueID()).getResponseTime();
        }
        return 0;
    }

    private void drawString(RavenFontRenderer fr, String text, float x, float y, int color) {
        if (shadow.isToggled()) {
            fr.drawStringWithShadow(text, x, y, color);
        } else {
            fr.drawString(text, x, y, color);
        }
    }

    private void renderExhibition(RavenFontRenderer fr) {
        int fps = Minecraft.getDebugFPS();
        int ping = getPing();

        String firstChar = "R";
        String restText = "aven BS ";
        String fpsText = fps + "FPS";
        String pingText = ping + "ms";

        float x = 2.0f;
        float y = 2.0f;

        long time = System.currentTimeMillis();
        int accentColor = rainbow.isToggled() ? getRainbowColor(time, 0) : new Color(85, 85, 255).getRGB();
        int whiteColor = 0xFFFFFFFF;
        int grayColor = 0xFFAAAAAA;

        drawString(fr, firstChar, x, y, accentColor);
        float currentX = x + fr.getStringWidth(firstChar);

        drawString(fr, restText, currentX, y, whiteColor);
        currentX += fr.getStringWidth(restText);

        if (showFPS.isToggled()) {
            drawString(fr, "[", currentX, y, grayColor);
            currentX += fr.getStringWidth("[");
            drawString(fr, fpsText, currentX, y, whiteColor);
            currentX += fr.getStringWidth(fpsText);
            drawString(fr, "]", currentX, y, grayColor);
            currentX += fr.getStringWidth("] ");
        }

        if (showPing.isToggled()) {
            drawString(fr, "[", currentX, y, grayColor);
            currentX += fr.getStringWidth("[");
            drawString(fr, pingText, currentX, y, whiteColor);
            currentX += fr.getStringWidth(pingText);
            drawString(fr, "]", currentX, y, grayColor);
        }
    }

    private void renderModern(RavenFontRenderer fr) {
        String text = "Raven BS";
        float x = 4.0f;
        float y = 4.0f;
        long time = System.currentTimeMillis();

        char[] characters = text.toCharArray();
        float currentX = x;

        for (int i = 0; i < characters.length; i++) {
            String charStr = String.valueOf(characters[i]);
            int color = rainbow.isToggled() ? getRainbowColor(time, i * 100) : 0xFFFFFFFF;
            drawString(fr, charStr, currentX, y, color);
            currentX += fr.getStringWidth(charStr);
        }
    }

    private void renderSimple(RavenFontRenderer fr) {
        String text = "Raven BS";
        int color = rainbow.isToggled() ? getRainbowColor(System.currentTimeMillis(), 0) : 0xFFFFFFFF;
        drawString(fr, text, 4.0f, 4.0f, color);
    }

    private void renderWeedHack(RavenFontRenderer fr) {
        String text = "Raven BS";
        float x = 4.0f;
        float y = 4.0f;
        float textWidth = fr.getStringWidth(text);
        float boxWidth = textWidth + 10;
        float boxHeight = fr.getFontHeight() + 8;

        RenderUtils.drawRoundedRectangle(x, y, x + boxWidth, y + boxHeight, 3, new Color(22, 22, 22, 220).getRGB());

        float textY = y + (boxHeight - fr.getFontHeight()) / 2.0f;
        drawString(fr, text, x + 5, textY, 0xFFFFFFFF);

        float gradientWidth = boxWidth - 6;
        for (int i = 0; i < gradientWidth; i++) {
            float ratio = i / gradientWidth;
            int r = (int) (255 * (1 - ratio));
            int g = (int) (255 * ratio);
            int b = 0;
            RenderUtils.drawRect(x + 3 + i, y + boxHeight - 2, x + 3 + i + 1, y + boxHeight - 1, new Color(r, g, b).getRGB());
        }
    }
}
