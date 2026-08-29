package keystrokesmod.module.impl.render;

import keystrokesmod.module.Module;
import keystrokesmod.module.setting.impl.ButtonSetting;
import keystrokesmod.module.setting.impl.SliderSetting;

public class ClientTheme extends Module {
    private static final String[] BG_MODES = new String[]{"Flow", "Rise", "Nexus", "Aurora"};

    public static ButtonSetting mainMenu;
    public static ButtonSetting button;
    public static ButtonSetting smoothFont;
    public static SliderSetting backgroundMode;

    public ClientTheme() {
        super("ClientTheme", category.render);
        this.registerSetting(mainMenu = new ButtonSetting("Main menu", true));
        this.registerSetting(button = new ButtonSetting("Button style", true));
        this.registerSetting(smoothFont = new ButtonSetting("Smooth font", true));
        this.registerSetting(backgroundMode = new SliderSetting("Background", 1, BG_MODES));
    }
}
