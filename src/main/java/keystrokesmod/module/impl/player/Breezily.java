package keystrokesmod.module.impl.player;

import keystrokesmod.module.Module;
import keystrokesmod.module.setting.impl.ButtonSetting;
import keystrokesmod.module.setting.impl.SliderSetting;
import keystrokesmod.utility.Utils;
import net.minecraft.client.settings.KeyBinding;
import net.minecraft.item.ItemBlock;
import net.minecraft.item.ItemStack;
import net.minecraftforge.fml.common.eventhandler.SubscribeEvent;
import net.minecraftforge.fml.common.gameevent.TickEvent;

public class Breezily extends Module {

    private final ButtonSetting enabledBtn;
    private final ButtonSetting onlyWhenHoldingBlock;
    private final ButtonSetting autoStrafe;
    private final SliderSetting switchSpeed;
    private final SliderSetting pitch;
    private final SliderSetting smoothSpeed;

    private float targetPitch;
    private boolean hasTarget = false;
    private int tickCounter = 0;
    private boolean useA = true;

    public Breezily() {
        super("Breezily", category.player);
        this.registerSetting(enabledBtn = new ButtonSetting("Enabled", true));
        this.registerSetting(onlyWhenHoldingBlock = new ButtonSetting("Only When Holding Block", true));
        this.registerSetting(autoStrafe = new ButtonSetting("Auto Strafe", true));
        this.registerSetting(switchSpeed = new SliderSetting("Switch Speed", "ticks", 4, 2, 10, 1));
        this.registerSetting(pitch = new SliderSetting("Pitch", 78, 60, 85, 1));
        this.registerSetting(smoothSpeed = new SliderSetting("Smooth Speed", 0.3, 0.1, 1.0, 0.05));
    }

    @Override
    public void onEnable() {
        tickCounter = 0;
        useA = true;
        hasTarget = false;
    }

    @Override
    public void onDisable() {
        releaseAll();
        hasTarget = false;
        tickCounter = 0;
    }

    private boolean isHoldingBlock() {
        if (mc.thePlayer == null) return false;
        ItemStack held = mc.thePlayer.getHeldItem();
        return held != null && held.getItem() instanceof ItemBlock;
    }

    private void setStrafeKey(boolean left) {
        int leftCode = mc.gameSettings.keyBindLeft.getKeyCode();
        int rightCode = mc.gameSettings.keyBindRight.getKeyCode();
        if (left) {
            KeyBinding.setKeyBindState(leftCode, true);
            KeyBinding.setKeyBindState(rightCode, false);
        } else {
            KeyBinding.setKeyBindState(rightCode, true);
            KeyBinding.setKeyBindState(leftCode, false);
        }
    }

    private void releaseAll() {
        KeyBinding.setKeyBindState(mc.gameSettings.keyBindLeft.getKeyCode(), false);
        KeyBinding.setKeyBindState(mc.gameSettings.keyBindRight.getKeyCode(), false);
    }

    @SubscribeEvent
    public void onClientTick(TickEvent.ClientTickEvent event) {
        if (event.phase != TickEvent.Phase.START || !Utils.nullCheck()) return;

        if (!enabledBtn.isToggled()) {
            releaseAll();
            hasTarget = false;
            tickCounter = 0;
            return;
        }

        if (onlyWhenHoldingBlock.isToggled() && !isHoldingBlock()) {
            releaseAll();
            hasTarget = false;
            tickCounter = 0;
            return;
        }

        float fwd = mc.thePlayer.moveForward;
        if (autoStrafe.isToggled() && fwd < 0) {
            int speed = (int) switchSpeed.getInput();
            tickCounter++;
            if (tickCounter >= speed) {
                tickCounter = 0;
                useA = !useA;
            }
            setStrafeKey(useA);
        } else {
            releaseAll();
            tickCounter = 0;
        }

        targetPitch = (float) pitch.getInput();
        hasTarget = true;
    }

    @SubscribeEvent
    public void onRenderTick(TickEvent.RenderTickEvent event) {
        if (event.phase != TickEvent.Phase.END || !Utils.nullCheck()) return;
        if (!hasTarget || !enabledBtn.isToggled()) return;

        float speed = (float) smoothSpeed.getInput();
        float realPitch = mc.thePlayer.rotationPitch;
        float newPitch = realPitch + (targetPitch - realPitch) * speed;
        mc.thePlayer.rotationPitch = newPitch;
    }
}
