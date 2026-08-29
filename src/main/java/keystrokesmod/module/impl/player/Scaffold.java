package keystrokesmod.module.impl.player;

import keystrokesmod.event.PostMotionEvent;
import keystrokesmod.event.PreMotionEvent;
import keystrokesmod.event.PrePlayerInputEvent;
import keystrokesmod.module.Module;
import keystrokesmod.module.setting.impl.ButtonSetting;
import keystrokesmod.module.setting.impl.SliderSetting;
import keystrokesmod.utility.RotationUtils;
import keystrokesmod.utility.Utils;
import net.minecraft.block.Block;
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

public class Scaffold extends Module {

    private static final String[] MODES = {"GodBridge", "Telly", "Legit"};
    private static final String[] SPRINT_MODES = {"None", "Vanilla"};
    private static final String[] ITEM_SWITCH = {"None", "Silent", "Hotbar", "HoldBlocks"};
    private static final double SENSITIVITY_QUANTUM = 0.03404715;
    private static final double MAX_REACH_SQ = 20.25;
    private static final double EAGLE_HALF_WIDTH = 0.3;
    private static final int[][] FACE_OFFSETS = {{0,1,0},{0,0,-1},{0,0,1},{-1,0,0},{1,0,0}};

    private final ButtonSetting moveFix;
    private final ButtonSetting safeWalk;
    private final ButtonSetting swing;
    private final ButtonSetting blockCounter;
    private final SliderSetting mode;
    private final SliderSetting rotationSpeed;
    private final SliderSetting legitSneakDelay;
    private final SliderSetting sprintMode;
    private final SliderSetting itemSwitch;
    private final ButtonSetting strictRaycast;

    private int lastSlot = -1;
    private int blockCount = -1;
    private boolean canRotate = false;
    private boolean rotationInitialized = false;
    private int rotationTick = 0;
    private boolean keepYLocked = false;
    private boolean forcedSneak = false;
    private int displayBlockCount = 0;
    private float rotCurrentYaw = 0.0f;
    private float rotCurrentPitch = 85.0f;
    private float rotTargetYaw = 0.0f;
    private float rotTargetPitch = 85.0f;
    private Vec3 placementHitVec = null;
    private boolean placementRotationPending = false;
    private Vec3 stableDiagonalHitVec = null;
    private int stableDiagonalSupportX = 0, stableDiagonalSupportY = 0, stableDiagonalSupportZ = 0, stableDiagonalFace = -1;
    private long legitRandomAt = 0L;
    private float legitYawOffset = 0.0f;
    private float legitPitchOffset = 0.0f;
    private boolean legitSneaking = false;
    private long legitUnsneakAt = 0L;
    private int[] currentPlacement = null;
    private boolean wasOnGround = true;

    public Scaffold() {
        super("Scaffold", category.player);
        this.registerSetting(moveFix = new ButtonSetting("Move fix", true));
        this.registerSetting(safeWalk = new ButtonSetting("Safe walk", true));
        this.registerSetting(swing = new ButtonSetting("Swing", true));
        this.registerSetting(blockCounter = new ButtonSetting("Block counter", true));
        this.registerSetting(mode = new SliderSetting("Mode", 0, MODES));
        this.registerSetting(rotationSpeed = new SliderSetting("Rotation speed", 100, 1, 100, 1));
        this.registerSetting(legitSneakDelay = new SliderSetting("Legit sneak delay", 50, 50, 300, 5));
        this.registerSetting(sprintMode = new SliderSetting("Sprint", 0, SPRINT_MODES));
        this.registerSetting(itemSwitch = new SliderSetting("Item switch", 2, ITEM_SWITCH));
        this.registerSetting(strictRaycast = new ButtonSetting("Strict raycast", false));
    }

    @Override
    public void guiUpdate() {
        legitSneakDelay.setVisible((int) mode.getInput() == 2, this);
    }

    @Override
    public void onEnable() {
        lastSlot = mc.thePlayer != null ? mc.thePlayer.inventory.currentItem : -1;
        blockCount = -1;
        rotationTick = 3;
        canRotate = false;
        rotationInitialized = false;
        keepYLocked = false;
        forcedSneak = false;
        displayBlockCount = countBlocks();
        placementHitVec = null;
        placementRotationPending = false;
        clearStableDiagonalHit();
        currentPlacement = null;
    }

    @Override
    public void onDisable() {
        KeyBinding.setKeyBindState(mc.gameSettings.keyBindSneak.getKeyCode(), false);
        if ((int) itemSwitch.getInput() == 2 && lastSlot != -1) {
            mc.thePlayer.inventory.currentItem = lastSlot;
        }
        forcedSneak = false;
        legitSneaking = false;
        currentPlacement = null;
    }

    private int countBlocks() {
        int count = 0;
        for (int i = 0; i < 9; i++) {
            ItemStack stack = mc.thePlayer.inventory.getStackInSlot(i);
            if (stack != null && stack.getItem() instanceof ItemBlock) count += stack.stackSize;
        }
        return count;
    }

    private boolean isHoldingBlock() {
        ItemStack held = mc.thePlayer.getHeldItem();
        return held != null && held.getItem() instanceof ItemBlock;
    }

    private boolean hasHeldBlocks() {
        for (int i = 0; i < 9; i++) {
            ItemStack stack = mc.thePlayer.inventory.getStackInSlot(i);
            if (stack != null && stack.getItem() instanceof ItemBlock) return true;
        }
        return false;
    }

    private void switchToBlock() {
        int mode = (int) itemSwitch.getInput();
        if (mode == 0) return;
        if (mode == 3) return;
        if (isHoldingBlock()) {
            if (lastSlot == -1) lastSlot = mc.thePlayer.inventory.currentItem;
            return;
        }
        for (int i = 0; i < 9; i++) {
            ItemStack stack = mc.thePlayer.inventory.getStackInSlot(i);
            if (stack != null && stack.getItem() instanceof ItemBlock) {
                if (lastSlot == -1) lastSlot = mc.thePlayer.inventory.currentItem;
                mc.thePlayer.inventory.currentItem = i;
                return;
            }
        }
    }

    private boolean isGodBridgeMode() { return (int) mode.getInput() == 0; }
    private boolean isTellyMode() { return (int) mode.getInput() == 1; }
    private boolean isLegitMode() { return (int) mode.getInput() == 2; }

    private String faceName(int face) {
        switch (face) {
            case 0: return "DOWN";
            case 1: return "UP";
            case 2: return "NORTH";
            case 3: return "SOUTH";
            case 4: return "WEST";
            case 5: return "EAST";
            default: return "UP";
        }
    }

    private int faceFromName(String name) {
        if ("DOWN".equals(name)) return 0;
        if ("UP".equals(name)) return 1;
        if ("NORTH".equals(name)) return 2;
        if ("SOUTH".equals(name)) return 3;
        if ("WEST".equals(name)) return 4;
        if ("EAST".equals(name)) return 5;
        return -1;
    }

    private boolean isReplaceable(BlockPos pos) {
        Block b = mc.theWorld.getBlockState(pos).getBlock();
        return b == Blocks.air || b == Blocks.water || b == Blocks.flowing_water || b == Blocks.lava || b == Blocks.flowing_lava || b == Blocks.fire || b == Blocks.tallgrass || b == Blocks.snow_layer;
    }

    private boolean isBlockStack(ItemStack stack) {
        return stack != null && stack.getItem() instanceof ItemBlock;
    }

    private void clearStableDiagonalHit() {
        stableDiagonalHitVec = null;
        stableDiagonalFace = -1;
    }

    private float applyGCD(float yaw) {
        float f = mc.gameSettings.mouseSensitivity * 0.6f + 0.2f;
        float gcd = f * f * f * 1.2f;
        return Math.round(yaw / gcd) * gcd;
    }

    @SubscribeEvent
    public void onPrePlayerInput(PrePlayerInputEvent e) {
        if (!Utils.nullCheck()) return;
        switchToBlock();

        if (isTellyMode() && mc.thePlayer.onGround
                && (Math.abs(e.getForward()) > 0.01f || Math.abs(e.getStrafe()) > 0.01f)) {
            e.setJump(true);
        }

        if (isLegitMode()) {
            setForcedSneak(false);
            updateEagle(e);
        }

        if ((int) sprintMode.getInput() == 1) {
            mc.thePlayer.setSprinting(e.getForward() > 0);
        }

        if (moveFix.isToggled() && rotationInitialized && canRotate && currentPlacement != null) {
            float forward = e.getForward();
            float strafe = e.getStrafe();
            float yawDiff = MathHelper.wrapAngleTo180_float(rotCurrentYaw - mc.thePlayer.rotationYaw);
            if (Math.abs(yawDiff) > 0.1) {
                float cos = (float) Math.cos(Math.toRadians(yawDiff));
                float sin = (float) Math.sin(Math.toRadians(yawDiff));
                e.setForward(forward * cos + strafe * sin);
                e.setStrafe(strafe * cos - forward * sin);
            }
        }
    }

    private void setForcedSneak(boolean sneak) {
        forcedSneak = sneak;
    }

    private void updateEagle(PrePlayerInputEvent e) {
        if (!safeWalk.isToggled()) return;
        Vec3 pos = mc.thePlayer.getPositionVector();
        int bx = (int) Math.floor(pos.xCoord);
        int by = (int) Math.floor(pos.yCoord - 0.1);
        int bz = (int) Math.floor(pos.zCoord);
        boolean overAir = isReplaceable(new BlockPos(bx, by, bz));
        if (overAir && !mc.thePlayer.onGround) {
            e.setSneak(true);
            legitSneaking = true;
            legitUnsneakAt = System.currentTimeMillis() + (long) legitSneakDelay.getInput();
        } else if (legitSneaking) {
            if (System.currentTimeMillis() < legitUnsneakAt) {
                e.setSneak(true);
            } else {
                legitSneaking = false;
                e.setSneak(false);
            }
        }
    }

    @SubscribeEvent
    public void onPreMotion(PreMotionEvent e) {
        if (!Utils.nullCheck()) return;
        if ((int) itemSwitch.getInput() == 3 && !isHoldingBlock()) return;

        if (rotationTick > 0) rotationTick--;
        canRotate = rotationTick <= 0;

        int[] result = findPlacement();
        if (result != null) {
            currentPlacement = result;
            computeRotations(result);
            if (canRotate) {
                e.setRotations(rotCurrentYaw, rotCurrentPitch);
                mc.thePlayer.rotationYaw = rotCurrentYaw;
                mc.thePlayer.rotationPitch = rotCurrentPitch;
            }
        } else {
            currentPlacement = null;
            placementRotationPending = false;
        }
    }

    private int[] findPlacement() {
        Vec3 eyePos = new Vec3(mc.thePlayer.posX, mc.thePlayer.posY + mc.thePlayer.getEyeHeight(), mc.thePlayer.posZ);
        int px = (int) Math.floor(mc.thePlayer.posX);
        int py = (int) Math.floor(mc.thePlayer.posY - 1.0);
        int pz = (int) Math.floor(mc.thePlayer.posZ);

        if (!isReplaceable(new BlockPos(px, py, pz))) return null;

        int bestFace = -1;
        double bestDist = Double.MAX_VALUE;
        int[] bestSupport = null;

        for (int face = 1; face <= 5; face++) {
            int[] offset = FACE_OFFSETS[face - 1];
            int sx = px + offset[0];
            int sy = py + offset[1];
            int sz = pz + offset[2];
            BlockPos supportPos = new BlockPos(sx, sy, sz);
            if (isReplaceable(supportPos)) continue;
            Block supportBlock = mc.theWorld.getBlockState(supportPos).getBlock();
            if (!supportBlock.isFullBlock() && !supportBlock.isFullCube()) continue;

            Vec3 hitVec = getSupportHitVec(eyePos, sx, sy, sz, face);
            if (hitVec == null) continue;

            double dist = hitVec.distanceTo(eyePos);
            if (dist < bestDist) {
                bestDist = dist;
                bestFace = face;
                bestSupport = new int[]{sx, sy, sz};
            }
        }

        if (bestFace == -1) return null;
        return new int[]{bestSupport[0], bestSupport[1], bestSupport[2], bestFace};
    }

    private Vec3 getSupportHitVec(Vec3 eyePos, int sx, int sy, int sz, int face) {
        if (isGodBridgeMode() || isTellyMode()) {
            return getStableDiagonalHitVec(eyePos, sx, sy, sz, face);
        }
        return getLegitHitVec(eyePos, sx, sy, sz, face);
    }

    private Vec3 getStableDiagonalHitVec(Vec3 eyePos, int sx, int sy, int sz, int face) {
        if (stableDiagonalHitVec != null && stableDiagonalSupportX == sx
                && stableDiagonalSupportY == sy && stableDiagonalSupportZ == sz
                && stableDiagonalFace == face) {
            return stableDiagonalHitVec;
        }

        Vec3 best = null;
        double bestRotationDiff = Double.MAX_VALUE;
        float baseYaw = mc.thePlayer.rotationYaw;
        float basePitch = isGodBridgeMode() ? 82.0f : 78.0f;

        double[] offsets = {0.03125, 0.09375, 0.15625, 0.21875, 0.28125, 0.34375, 0.40625, 0.46875,
                0.53125, 0.59375, 0.65625, 0.71875, 0.78125, 0.84375, 0.90625, 0.96875};

        for (double u : offsets) {
            for (double v : offsets) {
                Vec3 hitVec = faceHitVec(sx, sy, sz, face, u, v);
                if (hitVec == null) continue;
                double dx = hitVec.xCoord - eyePos.xCoord;
                double dy = hitVec.yCoord - eyePos.yCoord;
                double dz = hitVec.zCoord - eyePos.zCoord;
                double horizontal = Math.sqrt(dx * dx + dz * dz);
                double distance = Math.sqrt(horizontal * horizontal + dy * dy);
                if (distance < 0.0001 || distance > Math.sqrt(MAX_REACH_SQ)) continue;

                float traceYaw = (float) (Math.toDegrees(Math.atan2(dz, dx)) - 90.0);
                float tracePitch = (float) (-Math.toDegrees(Math.atan2(dy, horizontal)));
                tracePitch = Math.max(-89.0f, Math.min(89.0f, tracePitch));

                if (strictRaycast.isToggled()) {
                    MovingObjectPosition mop = RotationUtils.rayCastBlock(distance + 0.05, traceYaw, tracePitch);
                    if (mop == null || mop.typeOfHit != MovingObjectPosition.MovingObjectType.BLOCK) continue;
                    BlockPos bp = mop.getBlockPos();
                    if (bp.getX() != sx || bp.getY() != sy || bp.getZ() != sz) continue;
                    if (!faceName(face).equalsIgnoreCase(mop.sideHit.name())) continue;
                }

                double rotationDiff = Math.abs(MathHelper.wrapAngleTo180_float(traceYaw - baseYaw))
                        + Math.abs(tracePitch - basePitch);
                if (rotationDiff < bestRotationDiff) {
                    bestRotationDiff = rotationDiff;
                    best = hitVec;
                }
            }
        }

        if (best != null) {
            stableDiagonalHitVec = best;
            stableDiagonalSupportX = sx;
            stableDiagonalSupportY = sy;
            stableDiagonalSupportZ = sz;
            stableDiagonalFace = face;
        }
        return best;
    }

    private Vec3 faceHitVec(int sx, int sy, int sz, int face, double u, double v) {
        switch (face) {
            case 1: return new Vec3(sx + u, sy + 1.0, sz + v);
            case 2: return new Vec3(sx + u, sy + v, sz);
            case 3: return new Vec3(sx + u, sy + v, sz + 1.0);
            case 4: return new Vec3(sx, sy + u, sz + v);
            case 5: return new Vec3(sx + 1.0, sy + u, sz + v);
            default: return null;
        }
    }

    private Vec3 getLegitHitVec(Vec3 eyePos, int sx, int sy, int sz, int face) {
        long now = System.currentTimeMillis();
        if (now - legitRandomAt > 300) {
            legitRandomAt = now;
            legitYawOffset = (float) (Math.random() * 2.0 - 1.0);
            legitPitchOffset = (float) (Math.random() * 2.0 - 1.0);
        }
        Vec3 center = faceHitVec(sx, sy, sz, face, 0.5, 0.5);
        if (center == null) return null;
        return center;
    }

    private void computeRotations(int[] result) {
        int sx = result[0], sy = result[1], sz = result[2], face = result[3];
        Vec3 eyePos = new Vec3(mc.thePlayer.posX, mc.thePlayer.posY + mc.thePlayer.getEyeHeight(), mc.thePlayer.posZ);
        Vec3 hitVec = getSupportHitVec(eyePos, sx, sy, sz, face);
        if (hitVec == null) {
            placementRotationPending = false;
            return;
        }
        placementHitVec = hitVec;
        double dx = hitVec.xCoord - eyePos.xCoord;
        double dy = hitVec.yCoord - eyePos.yCoord;
        double dz = hitVec.zCoord - eyePos.zCoord;
        double horizontal = Math.sqrt(dx * dx + dz * dz);
        rotTargetYaw = (float) (Math.toDegrees(Math.atan2(dz, dx)) - 90.0);
        rotTargetPitch = (float) (-Math.toDegrees(Math.atan2(dy, horizontal)));
        rotTargetPitch = Math.max(-89.0f, Math.min(89.0f, rotTargetPitch));

        if (isLegitMode()) {
            rotTargetYaw += legitYawOffset;
            rotTargetPitch += legitPitchOffset;
        }

        if (!rotationInitialized) {
            rotCurrentYaw = mc.thePlayer.rotationYaw;
            rotCurrentPitch = mc.thePlayer.rotationPitch;
            rotationInitialized = true;
        }

        float speed = (float) rotationSpeed.getInput() / 100.0f;
        float yawDiff = MathHelper.wrapAngleTo180_float(rotTargetYaw - rotCurrentYaw);
        rotCurrentYaw = applyGCD(rotCurrentYaw + yawDiff * speed);
        rotCurrentPitch = rotCurrentPitch + (rotTargetPitch - rotCurrentPitch) * speed;
        placementRotationPending = true;
    }

    @SubscribeEvent
    public void onPostMotion(PostMotionEvent e) {
        if (!Utils.nullCheck() || currentPlacement == null) return;
        if (!placementRotationPending) return;

        int sx = currentPlacement[0], sy = currentPlacement[1], sz = currentPlacement[2], face = currentPlacement[3];
        ItemStack held = mc.thePlayer.getHeldItem();
        String side = faceName(face);
        Vec3 support = new Vec3(sx, sy, sz);

        if (!isBlockStack(held)) {
            placementHitVec = null;
            placementRotationPending = false;
            clearStableDiagonalHit();
            return;
        }

        Vec3 hitVec = placementHitVec;
        if (hitVec == null) return;

        BlockPos pos = new BlockPos(sx, sy, sz);
        EnumFacing facing = EnumFacing.byName(side);
        if (facing == null) facing = EnumFacing.UP;

        boolean placed = mc.playerController.onPlayerRightClick(mc.thePlayer, mc.theWorld, held, pos, facing, hitVec);

        if (placed) {
            if (swing.isToggled()) mc.thePlayer.swingItem();
            displayBlockCount = countBlocks();
            clearStableDiagonalHit();
        } else {
            placementHitVec = null;
            placementRotationPending = false;
            clearStableDiagonalHit();
        }
    }

    @SubscribeEvent
    public void onRenderTick(TickEvent.RenderTickEvent event) {
        if (event.phase != TickEvent.Phase.END || !Utils.nullCheck()) return;
        if (!blockCounter.isToggled()) return;
        if (mc.currentScreen != null) return;

        String text = displayBlockCount + " blocks";
        ScaledResolution sr = new ScaledResolution(mc);
        int x = sr.getScaledWidth() / 2 - mc.fontRendererObj.getStringWidth(text) / 2;
        int y = sr.getScaledHeight() / 2 + 20;
        mc.fontRendererObj.drawStringWithShadow(text, x, y, 0xFFFFFF);
    }
}
