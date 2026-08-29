package keystrokesmod.module.impl.client;

import keystrokesmod.Raven;
import keystrokesmod.clickgui.LiquidGlassClickGui;
import keystrokesmod.clickgui.modern.ModernClickGUI;
import keystrokesmod.clickgui.myau.MyauClickGui;
import com.alan.clients.ui.click.standard.RiseClickGUI;
import keystrokesmod.module.Module;
import keystrokesmod.module.setting.impl.ButtonSetting;
import keystrokesmod.module.setting.impl.SliderSetting;
import keystrokesmod.utility.Utils;
import keystrokesmod.utility.font.FontManager;
import keystrokesmod.utility.font.RavenFontRenderer;

public class Gui extends Module {
    private static final String[] GUI_FONT_OPTIONS = FontManager.getHudFontOptions();
    private static final String[] CLICKGUI_STYLES = new String[]{"Raven", "Rise", "Myau", "Modern"};
    public static LiquidGlassClickGui liquidGlassClickGui;

    public static SliderSetting guiScale;
    public static SliderSetting font;
    public static SliderSetting backgroundBlur;
    public static SliderSetting scrollSpeed;
    public static SliderSetting clickguiStyle;
    public static ButtonSetting removePlayerModel;
    public static ButtonSetting darkBackground;
    public static ButtonSetting removeWatermark;
    public static ButtonSetting rainBowOutlines;

    public Gui() {
        super("Gui", category.client, 54);
        this.registerSetting(guiScale = new SliderSetting("Gui scale", "x", 1.0, 0.5, 2.0, 0.01));
        this.registerSetting(font = new SliderSetting("Font", 0, GUI_FONT_OPTIONS));
        this.registerSetting(clickguiStyle = new SliderSetting("ClickGUI style", 0, CLICKGUI_STYLES));
        this.registerSetting(backgroundBlur = new SliderSetting("Background blur", "%", 0, 0, 100, 1));
        this.registerSetting(scrollSpeed = new SliderSetting("Scroll speed", 20, 2, 90, 1));
        this.registerSetting(darkBackground = new ButtonSetting("Dark background", true));
        this.registerSetting(rainBowOutlines = new ButtonSetting("Rainbow outlines", true));
        this.registerSetting(removePlayerModel = new ButtonSetting("Remove player model", false));
        this.registerSetting(removeWatermark = new ButtonSetting("Remove watermark", false));
    }

    @Override
    public void onEnable() {
        if (Utils.nullCheck()) {
            int style = clickguiStyle != null ? (int) clickguiStyle.getInput() : 0;
            if (style == 1) {
                mc.displayGuiScreen(com.alan.clients.Client.a.getStandardClickGUI());
            } else if (style == 2) {
                mc.displayGuiScreen(new MyauClickGui());
            } else if (style == 3) {
                mc.displayGuiScreen(new ModernClickGUI());
            } else {
                if (mc.currentScreen != Raven.clickGui) {
                    mc.displayGuiScreen(Raven.clickGui);
                    Raven.clickGui.initMain();
                }
            }
        }
        this.disable();
    }

    public static String getSelectedFontName() {
        if (font == null) {
            return GUI_FONT_OPTIONS[0];
        }

        int index = (int) Math.max(0, Math.min(font.getOptions().length - 1, font.getInput()));
        return font.getOptions()[index];
    }

    public static RavenFontRenderer getClickGuiHeaderFontRenderer() {
        return FontManager.getClickGuiHeaderRenderer(getSelectedFontName());
    }

    public static RavenFontRenderer getClickGuiSettingFontRenderer() {
        return FontManager.getClickGuiSettingRenderer(getSelectedFontName());
    }

    public static float getClickGuiScale() {
        if (guiScale == null) {
            return 1.0F;
        }

        return (float) Math.max(0.5D, Math.min(2.0D, guiScale.getInput()));
    }
}
