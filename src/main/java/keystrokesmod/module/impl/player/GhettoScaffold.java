package keystrokesmod.module.impl.player;

import keystrokesmod.module.Module;
import keystrokesmod.module.ModuleManager;
import keystrokesmod.module.setting.impl.SliderSetting;
import keystrokesmod.utility.Utils;
import net.minecraftforge.fml.common.eventhandler.SubscribeEvent;
import net.minecraftforge.fml.common.gameevent.TickEvent;

public class GhettoScaffold extends Module {

    private final SliderSetting delay;

    private long airStartTime = 0;
    private boolean wasInAir = false;

    public GhettoScaffold() {
        super("ghetto scaffold", category.player);
        this.registerSetting(delay = new SliderSetting("Delay", "ms", 0, 0, 1000, 50));
    }

    @Override
    public void onEnable() {
        airStartTime = 0;
        wasInAir = false;
    }

    @Override
    public void onDisable() {
        Module clutch = ModuleManager.getModule("Clutch");
        if (clutch != null && clutch.isEnabled()) {
            clutch.disable();
        }
        wasInAir = false;
    }

    @SubscribeEvent
    public void onClientTick(TickEvent.ClientTickEvent event) {
        if (event.phase != TickEvent.Phase.START || !Utils.nullCheck()) return;

        boolean inAir = !mc.thePlayer.onGround;
        Module clutch = ModuleManager.getModule("Clutch");
        boolean currentState = clutch != null && clutch.isEnabled();
        long currentTime = System.currentTimeMillis();
        double delayMs = delay.getInput();

        if (inAir) {
            if (!wasInAir) {
                airStartTime = currentTime;
                wasInAir = true;
            }
            if (currentTime - airStartTime >= delayMs && !currentState && clutch != null) {
                clutch.enable();
            }
        } else {
            wasInAir = false;
            if (currentState && clutch != null) {
                clutch.disable();
            }
        }
    }
}
