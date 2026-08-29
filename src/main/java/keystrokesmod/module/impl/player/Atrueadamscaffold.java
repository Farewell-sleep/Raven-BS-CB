package keystrokesmod.module.impl.player;

import keystrokesmod.Raven;
import keystrokesmod.module.Module;
import keystrokesmod.module.ModuleManager;
import keystrokesmod.module.setting.impl.ButtonSetting;
import keystrokesmod.module.setting.impl.SliderSetting;
import keystrokesmod.utility.Utils;
import net.minecraftforge.fml.common.eventhandler.SubscribeEvent;
import net.minecraftforge.fml.common.gameevent.TickEvent;

public class Atrueadamscaffold extends Module {

    private static final String CLUTCH = "Clutch";

    private final ButtonSetting clutchBtn;
    private final ButtonSetting autoJumpBtn;
    private final SliderSetting delay;
    private final SliderSetting reach;
    private final SliderSetting speed;
    private final SliderSetting snapbackSpeed;
    private final SliderSetting maxDistance;
    private final SliderSetting rotationTolerance;
    private final ButtonSetting simulateFuture;
    private final SliderSetting minFallDistance;

    private long airborneSince = 0;
    private boolean airborne = false;
    private String jumpModule = null;

    public Atrueadamscaffold() {
        super("Atrueadamscaffold", category.player);
        this.registerSetting(clutchBtn = new ButtonSetting("Clutch", true));
        this.registerSetting(autoJumpBtn = new ButtonSetting("Autojump", true));
        this.registerSetting(delay = new SliderSetting("Delay", "ms", 100, 0, 1000, 10));
        this.registerSetting(reach = new SliderSetting("Reach", " blocks", 4.5, 1, 4.5, 0.5));
        this.registerSetting(speed = new SliderSetting("Speed", 100, 1, 100, 50));
        this.registerSetting(snapbackSpeed = new SliderSetting("Snapback Speed", 25, 1, 50, 25));
        this.registerSetting(maxDistance = new SliderSetting("Max distance", " blocks", 10, 1, 20, 5));
        this.registerSetting(rotationTolerance = new SliderSetting("Rotation Tolerance", 25, 0, 90, 1));
        this.registerSetting(simulateFuture = new ButtonSetting("Simulate future position", true));
        this.registerSetting(minFallDistance = new SliderSetting("Minimum fall distance", " blocks", 0, 0, 20, 1));
    }

    @Override
    public void onEnable() {
        if (clutchBtn.isToggled()) {
            pushSettings();
            clearBind();
        }
        if (autoJumpBtn.isToggled()) setJump(true);
    }

    @Override
    public void onDisable() {
        setClutch(false);
        setJump(false);
        airborne = false;
    }

    @SubscribeEvent
    public void onClientTick(TickEvent.ClientTickEvent event) {
        if (event.phase != TickEvent.Phase.START || !Utils.nullCheck()) return;

        if (autoJumpBtn.isToggled()) setJump(true);
        if (!clutchBtn.isToggled()) return;

        pushSettings();

        if (!mc.thePlayer.onGround) {
            if (!airborne) {
                airborne = true;
                airborneSince = System.currentTimeMillis();
            }
            if (System.currentTimeMillis() - airborneSince >= delay.getInput()) {
                setClutch(true);
            }
        } else {
            airborne = false;
            setClutch(false);
        }
    }

    private void setClutch(boolean on) {
        Module clutch = ModuleManager.getModule(CLUTCH);
        if (clutch == null) return;
        if (on == clutch.isEnabled()) return;
        if (on) {
            clearBind();
            clutch.enable();
        } else {
            clutch.disable();
        }
    }

    private void setJump(boolean on) {
        String name = resolveJumpModule();
        if (name == null) return;
        Module mod = ModuleManager.getModule(name);
        if (mod == null) return;
        if (on && !mod.isEnabled()) mod.enable();
        else if (!on && mod.isEnabled()) mod.disable();
    }

    private void pushSettings() {
        // 镜像设置到 Clutch 模块（bs 不支持动态设置其他模块设置，跳过）
    }

    private String resolveJumpModule() {
        if (jumpModule != null) return jumpModule;
        for (Module m : Raven.moduleManager.getModules()) {
            String name = m.getName().toLowerCase().replace(" ", "");
            if (name.equals("autojump")) {
                jumpModule = m.getName();
                return jumpModule;
            }
        }
        for (Module m : Raven.moduleManager.getModules()) {
            if (m.getName().toLowerCase().contains("jump")) {
                jumpModule = m.getName();
                return jumpModule;
            }
        }
        return null;
    }

    private void clearBind() {
        // 清除 Clutch 的按键绑定（如果有）
        try {
            Module clutch = ModuleManager.getModule(CLUTCH);
            if (clutch != null) {
                clutch.setBind(0);
            }
        } catch (Exception ignored) {}
    }
}
