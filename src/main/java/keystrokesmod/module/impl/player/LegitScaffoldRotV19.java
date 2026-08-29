package keystrokesmod.module.impl.player;

import keystrokesmod.event.PostMotionEvent;
import keystrokesmod.event.PreMotionEvent;
import keystrokesmod.event.PreUpdateEvent;
import keystrokesmod.module.Module;
import keystrokesmod.module.setting.impl.ButtonSetting;
import keystrokesmod.module.setting.impl.SliderSetting;
import keystrokesmod.utility.RotationUtils;
import keystrokesmod.utility.Utils;
import net.minecraft.item.ItemBlock;
import net.minecraft.item.ItemStack;
import net.minecraft.util.BlockPos;
import net.minecraft.util.EnumFacing;
import net.minecraft.util.MovingObjectPosition;
import net.minecraft.util.Vec3;
import net.minecraftforge.fml.common.eventhandler.SubscribeEvent;

/**
 * LegitScaffoldRotV19 - Silent rotation scaffold + telly.
 *
 * - Player first-person view NEVER changes (silent rotation)
 * - Ground scaffold: body yaw = player yaw, pitch ~72deg (aims at block side ahead)
 * - Telly: on jump, rotate backward to aim at takeoff block, place under feet
 * - Smooth rotation interpolation
 * - Fast placement in PostMotion
 * - No sneak
 */
public class LegitScaffoldRotV19 extends Module {

    // Settings
    private final SliderSetting groundPitch;
    private final SliderSetting tellyPitch;
    private final SliderSetting reach;
    private final SliderSetting smoothness;
    private final ButtonSetting holdingBlocks;
    private final ButtonSetting telly;
    private final ButtonSetting autoPlace;
    private final ButtonSetting groundRotate;

    // Rotation state (server-side, silent)
    private float currentYaw = 0f;
    private float currentPitch = 0f;
    private float targetYaw = 0f;
    private float targetPitch = 0f;
    private boolean initialized = false;
    private boolean rotationActive = false;

    // Telly state
    private boolean wasOnGround = true;
    private boolean tellyActive = false;
    private int tellyTicks = 0;
    private boolean tellyPlaced = false;
    private float tellyStartYaw = 0f;
    private static final int TELLY_DURATION = 4;

    // Placement
    private Vec3 placeTargetPos = null;
    private Vec3 placeHitPos = null;
    private String placeSide = "";
    private boolean placeQueued = false;

    public LegitScaffoldRotV19() {
        super("LegitScaffoldRotV19", category.player);

        this.registerSetting(groundPitch = new SliderSetting("Ground Pitch", 72, 60, 85, 1));
        this.registerSetting(tellyPitch = new SliderSetting("Telly Pitch", 88, 80, 90, 1));
        this.registerSetting(reach = new SliderSetting("Reach", " blocks", 4.5, 1.0, 5.0, 0.1));
        this.registerSetting(smoothness = new SliderSetting("Smoothness", "%", 50, 10, 90, 5));
        this.registerSetting(holdingBlocks = new ButtonSetting("Holding blocks", true));
        this.registerSetting(telly = new ButtonSetting("Telly", true));
        this.registerSetting(autoPlace = new ButtonSetting("Auto Place", true));
        this.registerSetting(groundRotate = new ButtonSetting("Ground rotate", true));
    }

    @Override
    public void onEnable() {
        initialized = false;
        rotationActive = false;
        tellyActive = false;
        tellyTicks = 0;
        tellyPlaced = false;
        wasOnGround = true;
        placeQueued = false;
    }

    @Override
    public void onDisable() {
        rotationActive = false;
        tellyActive = false;
        placeQueued = false;
    }

    @SubscribeEvent
    public void onPreMotion(PreMotionEvent e) {
        if (!Utils.nullCheck()) return;

        // Compute target + find placement FIRST
        computeTargetAndPlacement();

        // Only activate rotation if we have a placement target
        if (rotationActive && placeQueued) {
            if (!initialized) {
                currentYaw = mc.thePlayer.rotationYaw;
                currentPitch = mc.thePlayer.rotationPitch;
                initialized = true;
            }

            float smooth = (float) smoothness.getInput() / 100f;

            // Yaw interpolation (handle wrap-around)
            float yawDiff = wrapYaw(targetYaw - currentYaw);
            currentYaw = wrapYaw(currentYaw + yawDiff * smooth);

            // Pitch interpolation
            currentPitch += (targetPitch - currentPitch) * smooth;

            // SILENT: only set server rotation, player view untouched
            e.setRotations(currentYaw, currentPitch);
        } else {
            initialized = false;
        }
    }

    @SubscribeEvent
    public void onPostMotion(PostMotionEvent e) {
        // Place immediately after rotation is sent (fast placement)
        if (placeQueued && autoPlace.isToggled()) {
            placeQueued = false;
            if (placeHitPos != null && placeTargetPos != null && !placeSide.isEmpty()) {
                if (placeBlock(placeHitPos, placeSide, placeTargetPos)) {
                    mc.thePlayer.swingItem();
                    if (tellyActive) tellyPlaced = true;
                }
            }
        }
    }

    @SubscribeEvent
    public void onPreUpdate(PreUpdateEvent e) {
        if (!Utils.nullCheck()) return;

        boolean onGround = mc.thePlayer.onGround;

        if (telly.isToggled()) {
            // Detect jump: was on ground, now in air moving up
            if (wasOnGround && !onGround && mc.thePlayer.motionY > 0) {
                tellyActive = true;
                tellyTicks = 0;
                tellyPlaced = false;
                tellyStartYaw = mc.thePlayer.rotationYaw;
            }

            if (tellyActive) {
                tellyTicks++;
                if (tellyTicks >= TELLY_DURATION || (tellyPlaced && tellyTicks >= 2)) {
                    tellyActive = false;
                }
            }
        }

        wasOnGround = onGround;
    }

    // === Target rotation + placement computation ===

    private void computeTargetAndPlacement() {
        placeQueued = false;
        rotationActive = false;

        if (mc.thePlayer == null || mc.theWorld == null) return;
        if (holdingBlocks.isToggled() && !isHoldingBlock()) return;

        float playerYaw = mc.thePlayer.rotationYaw;
        double r = reach.getInput();

        if (tellyActive && telly.isToggled()) {
            // TELLY: rotate backward (180) to aim at takeoff block, pitch near vertical
            // This places block under feet / at takeoff position
            targetYaw = wrapYaw(tellyStartYaw + 180f);
            targetPitch = (float) tellyPitch.getInput();
            rotationActive = true;
            findPlacement(targetYaw, targetPitch, r);
        } else if (mc.thePlayer.onGround && groundRotate.isToggled()) {
            // GROUND: body faces player view direction, pitch aims at block side ahead
            // NOT too steep (72deg) so raycast hits block side, not ground below
            targetYaw = playerYaw;
            targetPitch = (float) groundPitch.getInput();
            rotationActive = true;
            findPlacement(targetYaw, targetPitch, r);
        } else if (!mc.thePlayer.onGround) {
            // AIR: keep head down for safety placement
            targetYaw = playerYaw;
            targetPitch = (float) groundPitch.getInput();
            rotationActive = true;
            findPlacement(targetYaw, targetPitch, r);
        }
    }

    private void findPlacement(float yaw, float pitchAngle, double r) {
        MovingObjectPosition ray = RotationUtils.rayCastBlock(r, yaw, pitchAngle);

        if (ray != null && ray.typeOfHit == MovingObjectPosition.MovingObjectType.BLOCK) {
            BlockPos hit = ray.getBlockPos();
            String side = ray.sideHit.getName().toUpperCase();

            // Never place on DOWN face (would place below feet incorrectly)
            if (!"DOWN".equals(side)) {
                Vec3 targetPos = new Vec3(
                        hit.getX() + ray.sideHit.getFrontOffsetX(),
                        hit.getY() + ray.sideHit.getFrontOffsetY(),
                        hit.getZ() + ray.sideHit.getFrontOffsetZ()
                );
                placeHitPos = new Vec3(hit.getX(), hit.getY(), hit.getZ());
                placeSide = side;
                placeTargetPos = targetPos;
                placeQueued = true;
            }
        }
    }

    // === Helpers ===

    private boolean isHoldingBlock() {
        ItemStack held = mc.thePlayer.getHeldItem();
        return held != null && held.getItem() instanceof ItemBlock;
    }

    private boolean placeBlock(Vec3 hitPos, String side, Vec3 targetPos) {
        BlockPos pos = new BlockPos(hitPos.xCoord, hitPos.yCoord, hitPos.zCoord);
        EnumFacing facing = EnumFacing.byName(side.toLowerCase());
        if (facing == null) facing = EnumFacing.UP;

        Vec3 hitVec = new Vec3(
                targetPos.xCoord - hitPos.xCoord,
                targetPos.yCoord - hitPos.yCoord,
                targetPos.zCoord - hitPos.zCoord
        );

        return mc.playerController.onPlayerRightClick(
                mc.thePlayer, mc.theWorld, mc.thePlayer.getHeldItem(), pos, facing, hitVec);
    }

    private float wrapYaw(float yaw) {
        yaw = ((yaw % 360f) + 360f) % 360f;
        return (yaw > 180f) ? (yaw - 360f) : yaw;
    }
}
