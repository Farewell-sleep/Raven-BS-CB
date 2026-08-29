package keystrokesmod.helper;

import keystrokesmod.Raven;
import keystrokesmod.utility.IMinecraftInstance;
import keystrokesmod.utility.RenderUtils;
import keystrokesmod.utility.Utils;
import net.minecraftforge.fml.common.eventhandler.SubscribeEvent;
import net.minecraftforge.fml.common.gameevent.TickEvent;

public class DebugHelper implements IMinecraftInstance {
    public static boolean MIXIN;
    public static boolean BACKGROUND;

    @SubscribeEvent
    public void onRenderTick(TickEvent.RenderTickEvent ev) {
        if (!Raven.DEBUG || ev.phase != TickEvent.Phase.END || !Utils.nullCheck()) {
            return;
        }
        if (mc.currentScreen == null) {
            RenderUtils.renderBPS(true, true);
        }
    }

    public static void debugMixin(Object obj, String message) {
        if (!MIXIN) {
            return;
        }
        Utils.sendMessage("&d" + obj.getClass().getSimpleName() + "&7: " + message);
    }
}
