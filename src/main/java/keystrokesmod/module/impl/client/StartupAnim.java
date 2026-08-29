package keystrokesmod.module.impl.client;

import keystrokesmod.module.Module;
import keystrokesmod.ui.StartupAnimation;
import keystrokesmod.utility.Utils;
import net.minecraft.client.gui.GuiMainMenu;
import net.minecraftforge.fml.common.eventhandler.SubscribeEvent;
import net.minecraftforge.fml.common.gameevent.TickEvent;

public class StartupAnim extends Module {

    private boolean shown = false;
    private int tickCounter = 0;

    public StartupAnim() {
        super("Startup Animation", category.client);
        this.hidden = true;
    }

    @Override
    public void onEnable() {
        shown = false;
        tickCounter = 0;
    }

    @SubscribeEvent
    public void onClientTick(TickEvent.ClientTickEvent event) {
        if (event.phase != TickEvent.Phase.END) return;
        if (shown) return;
        if (!Utils.nullCheck()) return;
        if (mc.theWorld != null) return;

        tickCounter++;
        if (tickCounter < 5) return;

        if (mc.currentScreen instanceof GuiMainMenu) {
            mc.displayGuiScreen(new StartupAnimation());
            shown = true;
        }
    }
}
