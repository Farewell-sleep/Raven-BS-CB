package keystrokesmod.module.impl.combat;

import keystrokesmod.event.PreUpdateEvent;
import keystrokesmod.module.Module;
import keystrokesmod.module.setting.impl.ButtonSetting;
import keystrokesmod.module.setting.impl.SliderSetting;
import myau.event.EventManager;
import myau.event.types.EventType;
import net.minecraftforge.common.MinecraftForge;
import net.minecraftforge.fml.common.eventhandler.EventPriority;
import net.minecraftforge.fml.common.eventhandler.SubscribeEvent;
import net.minecraftforge.fml.common.gameevent.TickEvent;

/**
 * MyauAutoClicker —— OpenMyauPP AutoClicker 完整移植（BS 包装层）。
 */
public class MyauAutoClicker extends Module {
    private final myau.module.modules.AutoClicker core;

    private final SliderSetting minCPS = new SliderSetting("Min CPS", 8, 1, 20, 1);
    private final SliderSetting maxCPS = new SliderSetting("Max CPS", 12, 1, 20, 1);
    private final ButtonSetting blockHit = new ButtonSetting("Block hit", false);
    private final SliderSetting blockHitTicks = new SliderSetting("Block hit ticks", 1.5, 1.0, 20.0, 0.5);
    private final ButtonSetting weaponsOnly = new ButtonSetting("Weapons only", true);
    private final ButtonSetting allowTools = new ButtonSetting("Allow tools", false);
    private final ButtonSetting breakBlocks = new ButtonSetting("Break blocks", true);
    private final SliderSetting range = new SliderSetting("Range", 3.0, 3.0, 8.0, 0.1);
    private final SliderSetting hitBoxVertical = new SliderSetting("Hit box vertical", 0.1, 0.0, 1.0, 0.05);
    private final SliderSetting hitBoxHorizontal = new SliderSetting("Hit box horizontal", 0.2, 0.0, 1.0, 0.05);

    public MyauAutoClicker() {
        super("MyauAutoClicker", Module.category.combat, 0);
        this.core = new myau.module.modules.AutoClicker();
        EventManager.register(core);
        this.registerSetting(minCPS);
        this.registerSetting(maxCPS);
        this.registerSetting(blockHit);
        this.registerSetting(blockHitTicks);
        this.registerSetting(weaponsOnly);
        this.registerSetting(allowTools);
        this.registerSetting(breakBlocks);
        this.registerSetting(range);
        this.registerSetting(hitBoxVertical);
        this.registerSetting(hitBoxHorizontal);
    }

    private void syncSettings() {
        core.minCPS.setValue((int) minCPS.getInput());
        core.maxCPS.setValue((int) maxCPS.getInput());
        core.blockHit.setValue(blockHit.isToggled());
        core.blockHitTicks.setValue((float) blockHitTicks.getInput());
        core.weaponsOnly.setValue(weaponsOnly.isToggled());
        core.allowTools.setValue(allowTools.isToggled());
        core.breakBlocks.setValue(breakBlocks.isToggled());
        core.range.setValue((float) range.getInput());
        core.hitBoxVertical.setValue((float) hitBoxVertical.getInput());
        core.hitBoxHorizontal.setValue((float) hitBoxHorizontal.getInput());
    }

    @Override
    public void onEnable() {
        syncSettings();
        core.setEnabled(true);
        MinecraftForge.EVENT_BUS.register(this);
    }

    @Override
    public void onDisable() {
        MinecraftForge.EVENT_BUS.unregister(this);
        core.setEnabled(false);
    }

    @SubscribeEvent(priority = EventPriority.HIGHEST)
    public void onPreUpdate(PreUpdateEvent e) {
        if (mc.thePlayer == null) return;
        syncSettings();
    }

    @SubscribeEvent
    public void onClientTick(TickEvent.ClientTickEvent e) {
        if (mc.thePlayer == null) return;
        EventType type = (e.phase == TickEvent.Phase.START) ? EventType.PRE : EventType.POST;
        EventManager.call(new myau.events.TickEvent(type));
    }

    @SubscribeEvent
    public void onMouse(net.minecraftforge.client.event.MouseEvent e) {
        if (!core.isEnabled() || e.button != 0 || !e.buttonstate || mc.currentScreen != null) return;
        myau.events.LeftClickMouseEvent event = new myau.events.LeftClickMouseEvent();
        EventManager.call(event);
        if (event.isCancelled()) e.setCanceled(true);
    }

    @Override
    public String getInfo() {
        int min = (int) minCPS.getInput();
        int max = (int) maxCPS.getInput();
        return min == max ? String.valueOf(min) : min + "-" + max;
    }
}
