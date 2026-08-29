package keystrokesmod.module.impl.client;

import keystrokesmod.module.Module;
import keystrokesmod.module.setting.impl.SliderSetting;
import keystrokesmod.ui.RavenMainMenu;
import net.minecraft.client.gui.GuiMainMenu;
import net.minecraftforge.fml.common.eventhandler.SubscribeEvent;
import net.minecraftforge.fml.common.gameevent.TickEvent;

public class CustomMainMenu extends Module {
    private static final String[] BG_STYLES = new String[]{"Aurora", "Pure Black"};

    public static SliderSetting backgroundStyle;
    public static SliderSetting animationSpeed;

    public CustomMainMenu() {
        super("CustomMainMenu", category.client);
        this.registerSetting(backgroundStyle = new SliderSetting("Background", 0, BG_STYLES));
        this.registerSetting(animationSpeed = new SliderSetting("Animation speed", 1.0, 0.1, 3.0, 0.1));
    }

    @SubscribeEvent
    public void onClientTick(TickEvent.ClientTickEvent event) {
        if (event.phase != TickEvent.Phase.END) return;
        if (mc.currentScreen instanceof GuiMainMenu) {
            mc.displayGuiScreen(new RavenMainMenu());
        }
    }
}
