package keystrokesmod.module.impl.player;

import keystrokesmod.event.PostMotionEvent;
import keystrokesmod.event.PreMotionEvent;
import keystrokesmod.event.PrePlayerInputEvent;
import keystrokesmod.event.PreUpdateEvent;
import keystrokesmod.module.Module;
import keystrokesmod.module.setting.impl.ButtonSetting;
import keystrokesmod.utility.Utils;
import net.minecraft.client.gui.ScaledResolution;
import net.minecraft.client.settings.KeyBinding;
import net.minecraft.init.Blocks;
import net.minecraft.item.ItemBlock;
import net.minecraft.item.ItemStack;
import net.minecraft.util.BlockPos;
import net.minecraft.util.EnumFacing;
import net.minecraft.util.MathHelper;
import net.minecraft.util.MovingObjectPosition;
import net.minecraft.util.Vec3;
import net.minecraftforge.fml.common.eventhandler.SubscribeEvent;
import net.minecraftforge.fml.common.gameevent.TickEvent;

import java.util.Random;

/**
 * SpeedTelly —— 基于 TellyBridge 的高速版本。
 * <p>
 * 底层曲线/相位/静默旋转逻辑与 TellyBridge 一致，仅调整速度参数：
 *  - 旋转插值 50ms -> 25ms（转头更快）
 *  - 每 tick 推进 2 个相位（21 相位周期 -> 约 10.5 tick ≈ 0.52s 一个 telly）
 *  - setup 阶段 12tick -> 6tick（启动更快）
 *  - 放置阈值 phase>=7 -> phase>=5（更早按住右键）
 *  - 每个 telly 周期施加随机 yaw/pitch 偏移，模拟人手不规则搭路与转头
 */
public class SpeedTelly extends Module {

    private final ButtonSetting autoSwap;
    private final ButtonSetting disableSafeWalk;
    private final ButtonSetting showActivationHitbox;

    private boolean armed = false;
    private boolean running = false;
    private long activatePromptAt = 0L;
    private float promptAlpha = 0.0f;
    private long promptFadeLastAt = 0L;

    private int setupTick = -1;
    private int cyclePhase = 19;
    private float baseYaw = 0.0f;
    private int travelX = 0;
    private int travelZ = 0;

    private float rotationStartYaw = 0.0f;
    private float rotationStartPitch = 0.0f;
    private float rotationTargetYaw = 0.0f;
    private float rotationTargetPitch = 0.0f;
    private long rotationStartedAt = 0L;
    private long rotationDuration = 25L;
    private float currentYaw = 0.0f;
    private float currentPitch = 75.0f;
    private boolean rotationInitialized = false;

    private long freezeLastTickAt = 0L;
    private int lastSlot = -1;
    private boolean usePressed = false;

    private float takeoverCameraYaw = 0.0f;
    private float takeoverCameraPitch = 0.0f;
    private long takeoverDetectionAt = 0L;
    private boolean takeoverCameraValid = false;

    // 不规则搭路模拟：每个周期随机偏移
    private final Random jitterRandom = new Random();
    private float cycleYawJitter = 0.0f;
    private float cyclePitchJitter = 0.0f;

    private static final float[] yawCurve = {
            91.68f, 98.88f, 78.94f, 37.45f, 1.61f, -21.69f, -33.98f,
            -35.80f, -34.64f, -33.85f, -33.06f, -31.55f, -29.26f, -26.65f,
            -24.19f, -21.07f, -18.84f, -17.06f, -8.87f, 2.61f, 41.94f
    };

    private static final float[] pitchCurve = {
            64.31f, 59.95f, 60.57f, 61.46f, 60.64f, 58.89f, 56.91f,
            56.63f, 58.65f, 61.63f, 64.20f, 66.74f, 68.69f, 70.64f,
            73.01f, 75.37f, 77.46f, 78.56f, 78.90f, 77.22f, 72.25f
    };

    private static final float[] forwardCurve = {
            1.0f, 1.0f, 0.0f, 0.0f, -1.0f, -1.0f, -1.0f,
            -1.0f, -1.0f, -1.0f, -1.0f, -1.0f, -1.0f, -1.0f,
            -1.0f, -1.0f, -1.0f, -1.0f, -1.0f, -1.0f, 1.0f
    };

    private static final float[] strafeCurve = {
            -1.0f, -1.0f, -1.0f, -1.0f, 0.0f, 0.0f, 0.0f,
            0.0f, 0.0f, 0.0f, 0.0f, 0.0f, 0.0f, 0.0f,
            0.0f, 0.0f, 0.0f, -1.0f, -1.0f, -1.0f, -1.0f
    };

    // 速度参数
    private static final int SETUP_TICKS = 6;          // 原 12
    private static final int PHASE_STEP = 2;           // 每 tick 推进相位数（原 1）
    private static final long ROTATION_MS = 25L;       // 旋转插值时长（原 50）
    private static final int USE_THRESHOLD = 5;        // 放置起始相位（原 7）
    private static final float YAW_JITTER = 3.0f;      // yaw 不规则偏移 ±1.5°
    private static final float PITCH_JITTER = 1.5f;    // pitch 不规则偏移 ±0.75°

    public SpeedTelly() {
        super("SpeedTelly", category.player);
        this.registerSetting(autoSwap = new ButtonSetting("Auto swap", true));
        this.registerSetting(disableSafeWalk = new ButtonSetting("Disable SafeWalk", true));
        this.registerSetting(showActivationHitbox = new ButtonSetting("Show activation hitbox", false));
    }

    @Override
    public void onEnable() {
        armed = false;
        running = false;
        activatePromptAt = 0;
        setupTick = -1;
        cyclePhase = 19;
        rotationInitialized = false;
        freezeLastTickAt = 0;
        lastSlot = mc.thePlayer != null ? mc.thePlayer.inventory.currentItem : -1;
        usePressed = false;
        cycleYawJitter = 0f;
        cyclePitchJitter = 0f;
        releaseMovementKeys();
    }

    @Override
    public void onDisable() {
        stopAutomation();
        if (autoSwap.isToggled() && lastSlot != -1) {
            mc.thePlayer.inventory.currentItem = lastSlot;
        }
        releaseMovementKeys();
        KeyBinding.setKeyBindState(mc.gameSettings.keyBindUseItem.getKeyCode(), false);
    }

    private void stopAutomation() {
        running = false;
        armed = false;
        setupTick = -1;
        usePressed = false;
        KeyBinding.setKeyBindState(mc.gameSettings.keyBindUseItem.getKeyCode(), false);
        releaseMovementKeys();
    }

    private void releaseMovementKeys() {
        KeyBinding.setKeyBindState(mc.gameSettings.keyBindForward.getKeyCode(), false);
        KeyBinding.setKeyBindState(mc.gameSettings.keyBindBack.getKeyCode(), false);
        KeyBinding.setKeyBindState(mc.gameSettings.keyBindLeft.getKeyCode(), false);
        KeyBinding.setKeyBindState(mc.gameSettings.keyBindRight.getKeyCode(), false);
        KeyBinding.setKeyBindState(mc.gameSettings.keyBindJump.getKeyCode(), false);
        KeyBinding.setKeyBindState(mc.gameSettings.keyBindSprint.getKeyCode(), false);
    }

    private boolean isHoldingBlock() {
        ItemStack held = mc.thePlayer.getHeldItem();
        return held != null && held.getItem() instanceof ItemBlock;
    }

    private boolean isUsableBlockStack(ItemStack stack) {
        if (stack == null || !(stack.getItem() instanceof ItemBlock)) return false;
        net.minecraft.block.Block block = ((ItemBlock) stack.getItem()).getBlock();
        return block.isFullBlock() || block.isFullCube();
    }

    private void handleAutoSwap() {
        if (!autoSwap.isToggled()) return;
        int threshold = 5;
        ItemStack held = mc.thePlayer.getHeldItem();
        int heldCount = held != null && isUsableBlockStack(held) ? held.stackSize : 0;
        if (heldCount > threshold) return;
        int bestSlot = -1;
        int bestSize = heldCount;
        for (int slot = 0; slot <= 8; slot++) {
            if (slot == mc.thePlayer.inventory.currentItem) continue;
            ItemStack stack = mc.thePlayer.inventory.getStackInSlot(slot);
            if (!isUsableBlockStack(stack)) continue;
            if (stack.stackSize > bestSize) {
                bestSize = stack.stackSize;
                bestSlot = slot;
            }
        }
        if (bestSlot != -1) {
            mc.thePlayer.inventory.currentItem = bestSlot;
        }
    }

    private boolean isActivationAligned() {
        if (!mc.gameSettings.keyBindBack.isKeyDown() || !mc.gameSettings.keyBindRight.isKeyDown()) return false;
        if (!isHoldingBlock()) return false;
        MovingObjectPosition mop = mc.thePlayer.rayTrace(3.0, 1.0f);
        if (mop == null || mop.typeOfHit != MovingObjectPosition.MovingObjectType.BLOCK) return false;
        if (mop.sideHit == EnumFacing.UP || mop.sideHit == EnumFacing.DOWN) return false;
        BlockPos hitPos = mop.getBlockPos();
        int px = (int) Math.floor(mc.thePlayer.posX);
        int pz = (int) Math.floor(mc.thePlayer.posZ);
        BlockPos ahead = hitPos.offset(mop.sideHit);
        return Math.abs(ahead.getX() - px) <= 1 && Math.abs(ahead.getZ() - pz) <= 1;
    }

    @SubscribeEvent
    public void onPreUpdate(PreUpdateEvent e) {
        if (!Utils.nullCheck()) return;

        if (running) {
            KeyBinding.setKeyBindState(mc.gameSettings.keyBindAttack.getKeyCode(), false);
        }

        if (armed && !running) {
            updatePromptFade();
        }

        if (!running) {
            if (isActivationAligned()) {
                if (!armed) {
                    armed = true;
                    activatePromptAt = System.currentTimeMillis();
                    baseYaw = mc.thePlayer.rotationYaw;
                }
            } else {
                armed = false;
            }

            if (armed && mc.gameSettings.keyBindUseItem.isKeyDown()) {
                startAutomation();
            }
            return;
        }

        // Running
        long now = System.currentTimeMillis();
        if (freezeLastTickAt != 0L && now - freezeLastTickAt > 300L) {
            stopAutomation();
            return;
        }
        freezeLastTickAt = now;

        if (mc.thePlayer.isDead || mc.thePlayer.fallDistance > 7.0f) {
            stopAutomation();
            return;
        }

        handleAutoSwap();
        if (!isHoldingBlock()) {
            stopAutomation();
            return;
        }
    }

    private void startAutomation() {
        running = true;
        armed = false;
        setupTick = 0;
        cyclePhase = 19;
        baseYaw = mc.thePlayer.rotationYaw;
        rotationInitialized = false;
        takeoverDetectionAt = System.currentTimeMillis() + 125L;
        takeoverCameraValid = true;
        takeoverCameraYaw = mc.thePlayer.rotationYaw;
        takeoverCameraPitch = mc.thePlayer.rotationPitch;
        cycleYawJitter = 0f;
        cyclePitchJitter = 0f;
        calculateTravelDirection(baseYaw);
    }

    private void calculateTravelDirection(float yaw) {
        double radians = Math.toRadians(yaw);
        double rawX = Math.sin(radians) - Math.cos(radians);
        double rawZ = -Math.cos(radians) - Math.sin(radians);
        if (Math.abs(rawX) >= Math.abs(rawZ)) {
            travelX = rawX >= 0.0 ? 1 : -1;
            travelZ = 0;
        } else {
            travelX = 0;
            travelZ = rawZ >= 0.0 ? 1 : -1;
        }
    }

    @SubscribeEvent
    public void onPrePlayerInput(PrePlayerInputEvent e) {
        if (!Utils.nullCheck() || !running) return;

        // 检测手动相机接管
        if (System.currentTimeMillis() > takeoverDetectionAt && takeoverCameraValid) {
            float yawDiff = Math.abs(MathHelper.wrapAngleTo180_float(mc.thePlayer.rotationYaw - takeoverCameraYaw));
            float pitchDiff = Math.abs(mc.thePlayer.rotationPitch - takeoverCameraPitch);
            if (yawDiff > 5.0f || pitchDiff > 5.0f) {
                stopAutomation();
                return;
            }
        }

        if (setupTick >= 0) {
            if (setupTick < SETUP_TICKS) {
                boolean setupJump = setupTick >= (SETUP_TICKS / 2);
                applyMovement(e, -1.0f, -1.0f, setupJump, false);
                usePressed = true;
                KeyBinding.setKeyBindState(mc.gameSettings.keyBindUseItem.getKeyCode(), true);

                if (setupTick == SETUP_TICKS - 1) {
                    setRotationTarget(baseYaw + yawCurve[19] + cycleYawJitter, pitchCurve[19] + cyclePitchJitter, ROTATION_MS);
                } else {
                    setRotationTarget(baseYaw, 74.52f, ROTATION_MS);
                }
                setupTick++;
                return;
            }
            setupTick = -1;
            cyclePhase = 19;
        }

        int phase = cyclePhase;
        float strafe = strafeCurve[phase];
        boolean sprinting = phase == 0 || phase == 1;
        boolean jumping = phase >= 1 && phase <= 19;
        boolean use = phase >= USE_THRESHOLD;

        applyMovement(e, forwardCurve[phase], strafe, jumping, sprinting);
        usePressed = use;
        KeyBinding.setKeyBindState(mc.gameSettings.keyBindUseItem.getKeyCode(), use);

        int nextPhase = (phase + PHASE_STEP) % yawCurve.length;
        // 新周期开始：生成不规则偏移，模拟人手搭路
        if (nextPhase < PHASE_STEP || nextPhase == 0) {
            cycleYawJitter = (jitterRandom.nextFloat() - 0.5f) * YAW_JITTER;
            cyclePitchJitter = (jitterRandom.nextFloat() - 0.5f) * PITCH_JITTER;
        }
        setRotationTarget(baseYaw + yawCurve[nextPhase] + cycleYawJitter, pitchCurve[nextPhase] + cyclePitchJitter, ROTATION_MS);
        cyclePhase = nextPhase;
    }

    private void applyMovement(PrePlayerInputEvent e, float forward, float strafe, boolean jumping, boolean sprinting) {
        e.setForward(forward);
        e.setStrafe(strafe);
        e.setJump(jumping);
        e.setSneak(false);
        if (sprinting) mc.thePlayer.setSprinting(true);
    }

    private void setRotationTarget(float yaw, float pitch, long duration) {
        if (!rotationInitialized) {
            rotationStartYaw = mc.thePlayer.rotationYaw;
            rotationStartPitch = mc.thePlayer.rotationPitch;
            rotationInitialized = true;
        } else {
            rotationStartYaw = currentYaw;
            rotationStartPitch = currentPitch;
        }
        rotationTargetYaw = yaw;
        rotationTargetPitch = pitch;
        rotationStartedAt = System.currentTimeMillis();
        rotationDuration = duration;
    }

    private float getSmoothedRotation() {
        long elapsed = System.currentTimeMillis() - rotationStartedAt;
        float t = Math.min(1.0f, (float) elapsed / (float) rotationDuration);
        float easeT = t * t * (3 - 2 * t);
        float yawDiff = MathHelper.wrapAngleTo180_float(rotationTargetYaw - rotationStartYaw);
        currentYaw = rotationStartYaw + yawDiff * easeT;
        currentPitch = rotationStartPitch + (rotationTargetPitch - rotationStartPitch) * easeT;
        return t;
    }

    @SubscribeEvent
    public void onPreMotion(PreMotionEvent e) {
        if (!Utils.nullCheck() || !running) return;
        getSmoothedRotation();
        // 静默旋转：只设置服务器端旋转，不改变玩家视角
        e.setRotations(currentYaw, currentPitch);
    }

    @SubscribeEvent
    public void onPostMotion(PostMotionEvent e) {
        if (!Utils.nullCheck() || !running) return;
        // 自动放置由 keyBindUseItem 触发，不需要额外处理
    }

    private void updatePromptFade() {
        long now = System.currentTimeMillis();
        if (promptFadeLastAt == 0) promptFadeLastAt = now;
        long dt = now - promptFadeLastAt;
        promptFadeLastAt = now;
        if (armed) {
            promptAlpha = Math.min(1.0f, promptAlpha + dt / 200.0f);
        } else {
            promptAlpha = Math.max(0.0f, promptAlpha - dt / 200.0f);
        }
    }

    @SubscribeEvent
    public void onRenderTick(TickEvent.RenderTickEvent event) {
        if (event.phase != TickEvent.Phase.END || !Utils.nullCheck()) return;

        if (armed && !running && promptAlpha > 0) {
            ScaledResolution sr = new ScaledResolution(mc);
            String text = "Activate?";
            int x = sr.getScaledWidth() / 2 - mc.fontRendererObj.getStringWidth(text) / 2;
            int y = sr.getScaledHeight() / 2 - 20;
            int alpha = (int) (promptAlpha * 255);
            int color = (alpha << 24) | 0xFF5555;
            mc.fontRendererObj.drawStringWithShadow(text, x, y, color);
        }
    }
}
