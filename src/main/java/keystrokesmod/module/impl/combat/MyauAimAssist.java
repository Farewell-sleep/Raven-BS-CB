package keystrokesmod.module.impl.combat;

import keystrokesmod.event.KeyPressEvent;
import keystrokesmod.event.PreMotionEvent;
import keystrokesmod.event.PreUpdateEvent;
import keystrokesmod.module.Module;
import keystrokesmod.module.setting.impl.ButtonSetting;
import keystrokesmod.module.setting.impl.SliderSetting;
import myau.OpenMyau;
import myau.event.EventManager;
import myau.event.types.EventType;
import myau.management.RotationState;
import net.minecraftforge.common.MinecraftForge;
import net.minecraftforge.fml.common.eventhandler.EventPriority;
import net.minecraftforge.fml.common.eventhandler.SubscribeEvent;
import net.minecraftforge.fml.common.gameevent.TickEvent;

/**
 * MyauAimAssist —— OpenMyauPP AimAssist 完整移植（BS 包装层）。
 */
public class MyauAimAssist extends Module {
    private final myau.module.modules.AimAssist core;
    private boolean managersRegistered;

    private final SliderSetting hSpeed = new SliderSetting("Horizontal speed", 3.0, 0.0, 10.0, 0.1);
    private final SliderSetting vSpeed = new SliderSetting("Vertical speed", 0.0, 0.0, 10.0, 0.1);
    private final SliderSetting smoothing = new SliderSetting("Smoothing", 50, 0, 100, 1);
    private final SliderSetting range = new SliderSetting("Range", 4.5, 3.0, 8.0, 0.1);
    private final SliderSetting fov = new SliderSetting("FOV", 90, 30, 360, 1);
    private final ButtonSetting weaponOnly = new ButtonSetting("Weapons only", true);
    private final ButtonSetting allowTools = new ButtonSetting("Allow tools", false);
    private final ButtonSetting botChecks = new ButtonSetting("Bot check", true);
    private final ButtonSetting teams = new ButtonSetting("Teams", true);

    public MyauAimAssist() {
        super("MyauAimAssist", Module.category.combat, 0);
        this.core = new myau.module.modules.AimAssist();
        EventManager.register(core);
        this.registerSetting(hSpeed);
        this.registerSetting(vSpeed);
        this.registerSetting(smoothing);
        this.registerSetting(range);
        this.registerSetting(fov);
        this.registerSetting(weaponOnly);
        this.registerSetting(allowTools);
        this.registerSetting(botChecks);
        this.registerSetting(teams);
    }

    private void syncSettings() {
        core.hSpeed.setValue((float) hSpeed.getInput());
        core.vSpeed.setValue((float) vSpeed.getInput());
        core.smoothing.setValue((int) smoothing.getInput());
        core.range.setValue((float) range.getInput());
        core.fov.setValue((int) fov.getInput());
        core.weaponOnly.setValue(weaponOnly.isToggled());
        core.allowTools.setValue(allowTools.isToggled());
        core.botChecks.setValue(botChecks.isToggled());
        core.team.setValue(teams.isToggled());
    }

    private void ensureManagers() {
        if (!managersRegistered) {
            EventManager.register(OpenMyau.rotationManager);
            managersRegistered = true;
        }
    }

    @Override
    public void onEnable() {
        ensureManagers();
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

    @SubscribeEvent(priority = EventPriority.HIGHEST)
    public void onKeyPress(KeyPressEvent e) {
        if (!core.isEnabled() || mc.thePlayer == null) return;
        EventManager.call(new myau.events.KeyEvent(e.keyCode));
    }

    @SubscribeEvent(priority = EventPriority.HIGHEST)
    public void onPreMotion(PreMotionEvent e) {
        if (RotationState.isActived()) {
            e.setRotations(RotationState.getSmoothedYaw(), RotationState.getRotationPitch());
        }
    }

    @Override
    public String getInfo() {
        return null;
    }
}
