package keystrokesmod.module.impl.render;

import keystrokesmod.module.Module;
import keystrokesmod.module.setting.impl.ButtonSetting;
import keystrokesmod.module.setting.impl.SliderSetting;
import keystrokesmod.utility.Utils;
import net.minecraft.block.Block;
import net.minecraft.client.Minecraft;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.network.Packet;
import net.minecraft.network.play.client.C07PacketPlayerDigging;
import net.minecraft.util.BlockPos;
import net.minecraft.util.Vec3;
import net.minecraftforge.client.event.RenderWorldLastEvent;
import net.minecraftforge.event.entity.player.PlayerInteractEvent;
import net.minecraftforge.fml.common.eventhandler.SubscribeEvent;
import net.minecraftforge.fml.common.gameevent.TickEvent;
import org.lwjgl.opengl.GL11;

import java.util.Random;

public class CuteVisuals extends Module {

    private static final Minecraft mc = Minecraft.getMinecraft();
    private final Random random = new Random();

    // Settings
    public final ButtonSetting bedSound = new ButtonSetting("Bed Sound", true);
    public final ButtonSetting bedBurst = new ButtonSetting("Bed Burst", true);
    public final SliderSetting bedBurstCount = new SliderSetting("Bed Burst Count", 20, 5, 40, 1);
    public final SliderSetting bedBurstSize = new SliderSetting("Bed Burst Size", 0.20, 0.05, 0.6, 0.01);
    public final SliderSetting bedBurstSpeed = new SliderSetting("Bed Burst Speed", 2.5, 0.5, 7.0, 0.1);
    public final SliderSetting bedBurstLifetime = new SliderSetting("Bed Burst Lifetime", 1500, 500, 3000, 100);
    public final ButtonSetting rainbow = new ButtonSetting("Rainbow", true);
    public final SliderSetting rainbowLineWidth = new SliderSetting("Rainbow Line Width", 5.0, 1.0, 12.0, 0.5);
    public final SliderSetting rainbowDuration = new SliderSetting("Rainbow Duration", 3000, 1000, 6000, 200);
    public final ButtonSetting onlyWhileMoving = new ButtonSetting("Only While Moving", true);
    public final SliderSetting opacity = new SliderSetting("Opacity", 85, 20, 100, 5);
    public final ButtonSetting hearts = new ButtonSetting("Hearts", true);
    public final SliderSetting heartsSpawnRate = new SliderSetting("Hearts Spawn Rate", 200, 50, 500, 10);
    public final SliderSetting heartsLifetime = new SliderSetting("Hearts Lifetime", 1500, 500, 4000, 100);
    public final ButtonSetting dots = new ButtonSetting("Dots", true);
    public final SliderSetting dotsSpawnRate = new SliderSetting("Dots Spawn Rate", 100, 20, 200, 10);
    public final SliderSetting dotsLifetime = new SliderSetting("Dots Lifetime", 1500, 500, 5000, 100);
    public final ButtonSetting pulse = new ButtonSetting("Pulse", false);

    // Constants
    private static final int MAX_HEARTS = 50;
    private static final int MAX_DOTS = 100;
    private static final int MAX_BED_PARTICLES = 200;
    private static final int MAX_RAINBOWS = 5;
    private static final double TWO_PI = Math.PI * 2.0;
    private static final double HEART_RADIUS = 1.5;
    private static final double HEART_SIZE = 0.15;
    private static final double HEART_FLOAT_SPEED = 1.0;
    private static final float HEART_LINE_WIDTH = 1.5f;
    private static final double DOT_SIZE = 0.04;
    private static final double DOT_SPREAD = 0.9;
    private static final double DOT_DRIFT_SPEED = 0.3;
    private static final float DOT_LINE_WIDTH = 2.0f;
    private static final double RAINBOW_SIZE = 3.0;
    private static final double RAINBOW_BAND_WIDTH = 0.15;

    // Heart trail state
    private final double[] heartX = new double[MAX_HEARTS];
    private final double[] heartY = new double[MAX_HEARTS];
    private final double[] heartZ = new double[MAX_HEARTS];
    private final long[] heartTime = new long[MAX_HEARTS];
    private final float[] heartRotY = new float[MAX_HEARTS];
    private final float[] heartRotZ = new float[MAX_HEARTS];
    private final float[] heartScale = new float[MAX_HEARTS];
    private final int[] heartType = new int[MAX_HEARTS];
    private final boolean[] heartActive = new boolean[MAX_HEARTS];
    private int activeHeartCount = 0;
    private long lastHeartSpawn = 0;

    // Dot trail state
    private final double[] dotX = new double[MAX_DOTS];
    private final double[] dotY = new double[MAX_DOTS];
    private final double[] dotZ = new double[MAX_DOTS];
    private final double[] dotDriftX = new double[MAX_DOTS];
    private final double[] dotDriftY = new double[MAX_DOTS];
    private final double[] dotDriftZ = new double[MAX_DOTS];
    private final long[] dotTime = new long[MAX_DOTS];
    private final float[] dotScale = new float[MAX_DOTS];
    private final int[] dotType = new int[MAX_DOTS];
    private final boolean[] dotActive = new boolean[MAX_DOTS];
    private int activeDotCount = 0;
    private long lastDotSpawn = 0;
    private double lastDotPosX = 0, lastDotPosY = 0, lastDotPosZ = 0;
    private boolean hasLastDotPos = false;

    // Bed burst state
    private final double[] bedParticleX = new double[MAX_BED_PARTICLES];
    private final double[] bedParticleY = new double[MAX_BED_PARTICLES];
    private final double[] bedParticleZ = new double[MAX_BED_PARTICLES];
    private final double[] bedParticleVX = new double[MAX_BED_PARTICLES];
    private final double[] bedParticleVY = new double[MAX_BED_PARTICLES];
    private final double[] bedParticleVZ = new double[MAX_BED_PARTICLES];
    private final float[] bedParticleScale = new float[MAX_BED_PARTICLES];
    private final int[] bedParticleType = new int[MAX_BED_PARTICLES];
    private final long[] bedParticleTime = new long[MAX_BED_PARTICLES];
    private final boolean[] bedParticleActive = new boolean[MAX_BED_PARTICLES];
    private int activeBedParticleCount = 0;

    // Rainbow state
    private final double[] rainbowX = new double[MAX_RAINBOWS];
    private final double[] rainbowY = new double[MAX_RAINBOWS];
    private final double[] rainbowZ = new double[MAX_RAINBOWS];
    private final float[] rainbowYaw = new float[MAX_RAINBOWS];
    private final long[] rainbowTime = new long[MAX_RAINBOWS];
    private final boolean[] rainbowActive = new boolean[MAX_RAINBOWS];
    private int activeRainbowCount = 0;

    // Bed break detection
    private boolean diggingBed = false;
    private double bedX = 0, bedY = 0, bedZ = 0;

    // Precomputed geometry
    private final double[] trailHeartShapeX = new double[31];
    private final double[] trailHeartShapeY = new double[31];
    private final double[] bedHeartShapeX = new double[21];
    private final double[] bedHeartShapeY = new double[21];
    private final double[] dotCircleX = new double[9];
    private final double[] dotCircleY = new double[9];
    private final double[] dotFillX = new double[7];
    private final double[] dotFillY = new double[7];
    private final double[] starShapeX = new double[9];
    private final double[] starShapeY = new double[9];

    private final double[] rainbowRed =   {0.85, 0.60, 0.50, 0.50, 1.00, 1.00, 1.00};
    private final double[] rainbowGreen = {0.50, 0.50, 0.75, 1.00, 0.90, 0.60, 0.40};
    private final double[] rainbowBlue =  {1.00, 1.00, 1.00, 0.65, 0.50, 0.40, 0.50};
    private final double[] bedParticleRed =   {1.00, 1.00, 1.00, 0.50, 0.50, 0.60, 0.85};
    private final double[] bedParticleGreen = {0.40, 0.60, 0.90, 1.00, 0.75, 0.50, 0.50};
    private final double[] bedParticleBlue =  {0.50, 0.40, 0.50, 0.65, 1.00, 1.00, 1.00};

    public CuteVisuals() {
        super("Cute Visuals", category.render);
        this.registerSetting(bedSound);
        this.registerSetting(bedBurst);
        this.registerSetting(bedBurstCount);
        this.registerSetting(bedBurstSize);
        this.registerSetting(bedBurstSpeed);
        this.registerSetting(bedBurstLifetime);
        this.registerSetting(rainbow);
        this.registerSetting(rainbowLineWidth);
        this.registerSetting(rainbowDuration);
        this.registerSetting(onlyWhileMoving);
        this.registerSetting(opacity);
        this.registerSetting(hearts);
        this.registerSetting(heartsSpawnRate);
        this.registerSetting(heartsLifetime);
        this.registerSetting(dots);
        this.registerSetting(dotsSpawnRate);
        this.registerSetting(dotsLifetime);
        this.registerSetting(pulse);
        initializeGeometry();
    }

    private void initializeGeometry() {
        for (int i = 0; i <= 30; i++) {
            double t = (double) i / 30 * TWO_PI;
            double sinT = Math.sin(t);
            trailHeartShapeX[i] = 16.0 * sinT * sinT * sinT;
            trailHeartShapeY[i] = 13.0 * Math.cos(t) - 5.0 * Math.cos(2.0 * t)
                    - 2.0 * Math.cos(3.0 * t) - Math.cos(4.0 * t);
        }
        for (int i = 0; i <= 20; i++) {
            double t = (double) i / 20 * TWO_PI;
            double sinT = Math.sin(t);
            bedHeartShapeX[i] = 16.0 * sinT * sinT * sinT;
            bedHeartShapeY[i] = 13.0 * Math.cos(t) - 5.0 * Math.cos(2.0 * t)
                    - 2.0 * Math.cos(3.0 * t) - Math.cos(4.0 * t);
        }
        for (int i = 0; i <= 8; i++) {
            double angle = (double) i / 8 * TWO_PI;
            dotCircleX[i] = Math.cos(angle);
            dotCircleY[i] = Math.sin(angle);
        }
        for (int i = 0; i <= 6; i++) {
            double angle = (double) i / 6 * TWO_PI;
            dotFillX[i] = Math.cos(angle);
            dotFillY[i] = Math.sin(angle);
        }
        for (int i = 0; i <= 8; i++) {
            double angle = i * Math.PI / 4.0 - Math.PI / 2.0;
            double radius = (i % 2 == 0) ? 12.0 : 5.0;
            starShapeX[i] = Math.cos(angle) * radius;
            starShapeY[i] = Math.sin(angle) * radius;
        }
    }

    @Override
    public void onEnable() {
        clearHearts();
        clearDots();
        clearBedParticles();
        clearRainbows();
        lastHeartSpawn = 0;
        lastDotSpawn = 0;
        hasLastDotPos = false;
        diggingBed = false;
    }

    @Override
    public void onDisable() {
        clearHearts();
        clearDots();
        clearBedParticles();
        clearRainbows();
    }

    private void clearHearts() {
        for (int i = 0; i < MAX_HEARTS; i++) heartActive[i] = false;
        activeHeartCount = 0;
    }

    private void clearDots() {
        for (int i = 0; i < MAX_DOTS; i++) dotActive[i] = false;
        activeDotCount = 0;
    }

    private void clearBedParticles() {
        for (int i = 0; i < MAX_BED_PARTICLES; i++) bedParticleActive[i] = false;
        activeBedParticleCount = 0;
    }

    private void clearRainbows() {
        for (int i = 0; i < MAX_RAINBOWS; i++) rainbowActive[i] = false;
        activeRainbowCount = 0;
    }

    @SubscribeEvent
    public void onClientTick(TickEvent.ClientTickEvent event) {
        if (event.phase != TickEvent.Phase.END) return;
        if (!Utils.nullCheck()) return;
        EntityPlayer player = mc.thePlayer;
        if (player == null) return;

        boolean heartsEnabled = hearts.isToggled();
        boolean dotsEnabled = dots.isToggled();

        if (!heartsEnabled && activeHeartCount > 0) clearHearts();
        if (!dotsEnabled && activeDotCount > 0) clearDots();

        if (!heartsEnabled && !dotsEnabled) {
            lastDotPosX = player.posX;
            lastDotPosY = player.posY;
            lastDotPosZ = player.posZ;
            hasLastDotPos = true;
            return;
        }

        if (onlyWhileMoving.isToggled() && !isMoving(player)) {
            lastDotPosX = player.posX;
            lastDotPosY = player.posY;
            lastDotPosZ = player.posZ;
            hasLastDotPos = true;
            return;
        }

        long now = System.currentTimeMillis();

        if (heartsEnabled) {
            long rate = (long) heartsSpawnRate.getInput();
            if (now - lastHeartSpawn >= rate) {
                lastHeartSpawn = now;
                spawnHeart(player.posX, player.posY + 0.5, player.posZ, now);
            }
        }

        if (dotsEnabled && hasLastDotPos) {
            long rate = (long) dotsSpawnRate.getInput();
            if (now - lastDotSpawn >= rate) {
                lastDotSpawn = now;
                spawnDot(player, now);
            }
        }

        lastDotPosX = player.posX;
        lastDotPosY = player.posY;
        lastDotPosZ = player.posZ;
        hasLastDotPos = true;
    }

    private boolean isMoving(EntityPlayer player) {
        return player.moveForward != 0f || player.moveStrafing != 0f;
    }

    private void spawnHeart(double x, double y, double z, long now) {
        int slot = findFreeHeartSlot();
        boolean wasActive = heartActive[slot];
        double angle = random.nextDouble() * TWO_PI;
        double distance = random.nextDouble() * HEART_RADIUS;
        heartX[slot] = x + Math.cos(angle) * distance;
        heartZ[slot] = z + Math.sin(angle) * distance;
        heartY[slot] = y + random.nextDouble() * 0.5;
        heartTime[slot] = now;
        heartRotY[slot] = (float) (random.nextDouble() * 360.0);
        heartRotZ[slot] = (float) (random.nextDouble() * 30.0 - 15.0);
        heartScale[slot] = (float) (HEART_SIZE * (0.6 + random.nextDouble() * 0.8));
        heartType[slot] = random.nextInt(3);
        heartActive[slot] = true;
        if (!wasActive) activeHeartCount++;
    }

    private void spawnDot(EntityPlayer player, long now) {
        int slot = findFreeDotSlot();
        boolean wasActive = dotActive[slot];
        dotX[slot] = lastDotPosX + (random.nextDouble() - 0.5) * DOT_SPREAD;
        dotY[slot] = player.posY + 0.3 + random.nextDouble() * 1.2;
        dotZ[slot] = lastDotPosZ + (random.nextDouble() - 0.5) * DOT_SPREAD;
        dotDriftX[slot] = (random.nextDouble() - 0.5) * DOT_DRIFT_SPEED;
        dotDriftY[slot] = (0.3 + random.nextDouble() * 0.7) * DOT_DRIFT_SPEED;
        dotDriftZ[slot] = (random.nextDouble() - 0.5) * DOT_DRIFT_SPEED;
        dotTime[slot] = now;
        dotScale[slot] = (float) (DOT_SIZE * (0.5 + random.nextDouble()));
        dotType[slot] = random.nextInt(4);
        dotActive[slot] = true;
        if (!wasActive) activeDotCount++;
    }

    private int findFreeHeartSlot() {
        for (int i = 0; i < MAX_HEARTS; i++) if (!heartActive[i]) return i;
        long oldest = Long.MAX_VALUE;
        int idx = 0;
        for (int i = 0; i < MAX_HEARTS; i++) {
            if (heartTime[i] < oldest) { oldest = heartTime[i]; idx = i; }
        }
        return idx;
    }

    private int findFreeDotSlot() {
        for (int i = 0; i < MAX_DOTS; i++) if (!dotActive[i]) return i;
        long oldest = Long.MAX_VALUE;
        int idx = 0;
        for (int i = 0; i < MAX_DOTS; i++) {
            if (dotTime[i] < oldest) { oldest = dotTime[i]; idx = i; }
        }
        return idx;
    }

    private int findFreeBedParticleSlot() {
        for (int i = 0; i < MAX_BED_PARTICLES; i++) if (!bedParticleActive[i]) return i;
        long oldest = Long.MAX_VALUE;
        int idx = 0;
        for (int i = 0; i < MAX_BED_PARTICLES; i++) {
            if (bedParticleTime[i] < oldest) { oldest = bedParticleTime[i]; idx = i; }
        }
        return idx;
    }

    private int findFreeRainbowSlot() {
        for (int i = 0; i < MAX_RAINBOWS; i++) if (!rainbowActive[i]) return i;
        long oldest = Long.MAX_VALUE;
        int idx = 0;
        for (int i = 0; i < MAX_RAINBOWS; i++) {
            if (rainbowTime[i] < oldest) { oldest = rainbowTime[i]; idx = i; }
        }
        return idx;
    }

    @SubscribeEvent
    public void onSendPacket(net.minecraftforge.event.entity.player.PlayerInteractEvent event) {
        // Bed break detection via player interact
    }

    @SubscribeEvent
    public void onPacket(keystrokesmod.event.SendPacketEvent event) {
        if (!Utils.nullCheck()) return;
        Packet<?> packet = event.getPacket();
        if (!(packet instanceof C07PacketPlayerDigging)) return;

        C07PacketPlayerDigging digging = (C07PacketPlayerDigging) packet;
        if (digging.getPosition() == null) return;
        BlockPos pos = digging.getPosition();
        String status = digging.getStatus().name();

        if (status.equals("START_DESTROY_BLOCK")) {
            Block block = mc.theWorld.getBlockState(pos).getBlock();
            boolean isBed = block != null && block.getRegistryName() != null
                    && block.getRegistryName().toLowerCase().contains("bed");
            if (!isBed) { diggingBed = false; return; }
            double x = pos.getX() + 0.5;
            double y = pos.getY() + 0.5;
            double z = pos.getZ() + 0.5;
            if (mc.playerController != null && mc.playerController.isInCreativeMode()) {
                diggingBed = false;
                spawnBedBreak(x, y, z);
            } else {
                diggingBed = true;
                bedX = x; bedY = y; bedZ = z;
            }
        } else if (status.equals("STOP_DESTROY_BLOCK")) {
            if (diggingBed) {
                spawnBedBreak(bedX, bedY, bedZ);
                diggingBed = false;
            }
        } else if (status.equals("ABORT_DESTROY_BLOCK")) {
            diggingBed = false;
        }
    }

    private void spawnBedBreak(double x, double y, double z) {
        long now = System.currentTimeMillis();

        if (rainbow.isToggled()) {
            int slot = findFreeRainbowSlot();
            boolean wasActive = rainbowActive[slot];
            rainbowX[slot] = x;
            rainbowY[slot] = y;
            rainbowZ[slot] = z;
            rainbowTime[slot] = now;
            rainbowActive[slot] = true;
            EntityPlayer player = mc.thePlayer;
            if (player != null) {
                rainbowYaw[slot] = (float) Math.toDegrees(
                        Math.atan2(player.posX - x, player.posZ - z));
            } else {
                rainbowYaw[slot] = 0;
            }
            if (!wasActive) activeRainbowCount++;
        }

        if (bedBurst.isToggled()) {
            double speed = bedBurstSpeed.getInput();
            double baseSize = bedBurstSize.getInput();
            int count = (int) bedBurstCount.getInput();
            for (int i = 0; i < count; i++) {
                int slot = findFreeBedParticleSlot();
                boolean wasActive = bedParticleActive[slot];
                bedParticleX[slot] = x;
                bedParticleY[slot] = y;
                bedParticleZ[slot] = z;
                double theta = random.nextDouble() * TWO_PI;
                double phi = random.nextDouble() * Math.PI * 0.67 - Math.PI / 6.0;
                double particleSpeed = (0.8 + random.nextDouble() * 1.2) * speed;
                double cosPhi = Math.cos(phi);
                bedParticleVX[slot] = cosPhi * Math.cos(theta) * particleSpeed;
                bedParticleVY[slot] = Math.sin(phi) * particleSpeed + 1.0;
                bedParticleVZ[slot] = cosPhi * Math.sin(theta) * particleSpeed;
                int typeRoll = random.nextInt(5);
                bedParticleType[slot] = typeRoll < 2 ? 0 : typeRoll - 1;
                bedParticleScale[slot] = (float) (baseSize * (0.6 + random.nextDouble() * 0.8));
                bedParticleTime[slot] = now;
                bedParticleActive[slot] = true;
                if (!wasActive) activeBedParticleCount++;
            }
        }

        if (bedSound.isToggled()) {
            mc.thePlayer.playSound("random.orb", 1.0f, 1.5f);
            mc.thePlayer.playSound("random.levelup", 0.5f, 2.0f);
        }
    }

    @SubscribeEvent
    public void onRenderWorld(RenderWorldLastEvent event) {
        if (!Utils.nullCheck()) return;
        if (activeHeartCount <= 0 && activeDotCount <= 0
                && activeBedParticleCount <= 0 && activeRainbowCount <= 0) return;

        EntityPlayer player = mc.thePlayer;
        if (player == null) return;

        Vec3 camera = new Vec3(
                mc.getRenderManager().viewerPosX,
                mc.getRenderManager().viewerPosY,
                mc.getRenderManager().viewerPosZ);

        long now = System.currentTimeMillis();
        renderTrail(camera, now);
        renderBedVisuals(camera, now);
    }

    private void renderTrail(Vec3 camera, long now) {
        boolean renderHearts = activeHeartCount > 0 && hearts.isToggled();
        boolean renderDots = activeDotCount > 0 && dots.isToggled();
        if (!renderHearts && !renderDots) return;

        double op = opacity.getInput() / 100.0;

        GL11.glPushMatrix();
        GL11.glDisable(GL11.GL_TEXTURE_2D);
        GL11.glDepthMask(false);
        GL11.glEnable(GL11.GL_BLEND);
        GL11.glBlendFunc(GL11.GL_SRC_ALPHA, GL11.GL_ONE_MINUS_SRC_ALPHA);
        GL11.glDisable(GL11.GL_CULL_FACE);

        if (renderHearts) renderHearts(camera, now, op);
        if (renderDots) renderDots(camera, now, op);

        GL11.glEnable(GL11.GL_CULL_FACE);
        GL11.glDepthMask(true);
        GL11.glEnable(GL11.GL_TEXTURE_2D);
        GL11.glDisable(GL11.GL_BLEND);
        GL11.glColor4f(1, 1, 1, 1);
        GL11.glPopMatrix();
    }

    private void renderHearts(Vec3 camera, long now, double opacity) {
        long lifetime = (long) heartsLifetime.getInput();
        float lineWidth = Math.max(HEART_LINE_WIDTH * 0.6f, 1.0f);
        GL11.glLineWidth(lineWidth);

        for (int i = 0; i < MAX_HEARTS; i++) {
            if (!heartActive[i]) continue;
            long age = now - heartTime[i];
            if (age > lifetime) { heartActive[i] = false; activeHeartCount--; continue; }

            double progress = (double) age / (double) lifetime;
            double floatY = HEART_FLOAT_SPEED * progress * 1.5;
            double alpha;
            if (progress < 0.1) alpha = progress / 0.1;
            else if (progress > 0.6) alpha = (1.0 - progress) / 0.4;
            else alpha = 1.0;
            alpha *= opacity;

            double scaleAnim;
            if (progress < 0.1) scaleAnim = progress / 0.1;
            else if (progress > 0.8) scaleAnim = (1.0 - progress) / 0.2;
            else scaleAnim = 1.0;

            double drawX = heartX[i] + Math.sin(age * 0.002 + i * 1.7) * 0.1 - camera.xCoord;
            double drawY = heartY[i] + floatY - camera.yCoord;
            double drawZ = heartZ[i] + Math.cos(age * 0.0015 + i * 2.3) * 0.1 - camera.zCoord;

            double red, green, blue;
            int type = heartType[i];
            if (type == 0) { red = 1.0; green = 0.5; blue = 0.8; }
            else if (type == 1) { red = 1.0; green = 0.3; blue = 0.6; }
            else { red = 0.9; green = 0.4; blue = 0.9; }

            double size = heartScale[i] * scaleAnim;
            double billboardYaw = Math.toDegrees(Math.atan2(-drawX, -drawZ));
            double spinAngle = (age * 0.1 + heartRotY[i]) % 360.0;

            GL11.glPushMatrix();
            GL11.glTranslated(drawX, drawY, drawZ);
            GL11.glRotated(billboardYaw, 0, 1, 0);
            GL11.glRotated(spinAngle, 0, 1, 0);
            GL11.glRotated(heartRotZ[i], 0, 0, 1);
            GL11.glLineWidth(lineWidth + 3.0f);
            drawHeartShape(trailHeartShapeX, trailHeartShapeY, 30, size, alpha, red, green, blue);
            GL11.glLineWidth(lineWidth);
            GL11.glPopMatrix();
        }
    }

    private void renderDots(Vec3 camera, long now, double opacity) {
        long lifetime = (long) dotsLifetime.getInput();
        boolean pulseEnabled = pulse.isToggled();
        GL11.glLineWidth(DOT_LINE_WIDTH);

        for (int i = 0; i < MAX_DOTS; i++) {
            if (!dotActive[i]) continue;
            long age = now - dotTime[i];
            if (age > lifetime) { dotActive[i] = false; activeDotCount--; continue; }

            double progress = (double) age / (double) lifetime;
            double fade;
            if (progress < 0.1) fade = progress / 0.1;
            else if (progress > 0.5) fade = (1.0 - progress) / 0.5;
            else fade = 1.0;

            double pulseFactor = 1.0;
            if (pulseEnabled) {
                double flickerSpeed = 3.0 + dotType[i] * 1.5;
                pulseFactor = 0.5 + 0.5 * Math.sin(age * 0.01 * flickerSpeed + i * 2.7);
            }

            double ageSeconds = age / 1000.0;
            double drawX = dotX[i] + dotDriftX[i] * ageSeconds
                    + Math.sin(ageSeconds * 1.5 + i * 1.3) * 0.15 - camera.xCoord;
            double drawY = dotY[i] + dotDriftY[i] * ageSeconds - camera.yCoord;
            double drawZ = dotZ[i] + dotDriftZ[i] * ageSeconds
                    + Math.cos(ageSeconds * 1.2 + i * 2.1) * 0.15 - camera.zCoord;

            double red, green, blue;
            int type = dotType[i];
            if (type == 0) { red = 1.0; green = 0.45; blue = 0.7; }
            else if (type == 1) { red = 1.0; green = 0.6; blue = 0.85; }
            else if (type == 2) { red = 1.0; green = 0.3; blue = 0.55; }
            else { red = 1.0; green = 0.75; blue = 0.95; }

            double alpha = fade * pulseFactor * opacity;
            if (alpha < 0.02) continue;

            double size = dotScale[i];
            double billboardYaw = Math.toDegrees(Math.atan2(-drawX, -drawZ));

            GL11.glPushMatrix();
            GL11.glTranslated(drawX, drawY, drawZ);
            GL11.glRotated(billboardYaw, 0, 1, 0);
            GL11.glColor4d(red, green, blue, alpha);

            GL11.glBegin(GL11.GL_TRIANGLE_FAN);
            GL11.glVertex3d(0, 0, 0);
            for (int j = 0; j <= 6; j++) {
                GL11.glVertex3d(dotFillX[j] * size, dotFillY[j] * size, 0);
            }
            GL11.glEnd();

            GL11.glBegin(GL11.GL_LINE_LOOP);
            for (int j = 0; j <= 8; j++) {
                GL11.glVertex3d(dotCircleX[j] * size, dotCircleY[j] * size, 0);
            }
            GL11.glEnd();

            GL11.glPopMatrix();
        }
    }

    private void renderBedVisuals(Vec3 camera, long now) {
        if (activeBedParticleCount <= 0 && activeRainbowCount <= 0) return;

        long bedLifetime = activeBedParticleCount > 0 ? (long) bedBurstLifetime.getInput() : 0;
        long rainbowDur = activeRainbowCount > 0 ? (long) rainbowDuration.getInput() : 0;
        float rainbowLW = (float) rainbowLineWidth.getInput();

        GL11.glPushMatrix();
        GL11.glDisable(GL11.GL_TEXTURE_2D);
        GL11.glDisable(GL11.GL_DEPTH_TEST);
        GL11.glDepthMask(false);
        GL11.glEnable(GL11.GL_BLEND);
        GL11.glBlendFunc(GL11.GL_SRC_ALPHA, GL11.GL_ONE_MINUS_SRC_ALPHA);
        GL11.glEnable(GL11.GL_LINE_SMOOTH);
        GL11.glDisable(GL11.GL_CULL_FACE);

        if (activeRainbowCount > 0) renderRainbows(camera, now, rainbowDur, rainbowLW);
        if (activeBedParticleCount > 0) renderBedParticles(camera, now, bedLifetime, rainbowLW);

        GL11.glDisable(GL11.GL_LINE_SMOOTH);
        GL11.glEnable(GL11.GL_CULL_FACE);
        GL11.glEnable(GL11.GL_DEPTH_TEST);
        GL11.glDepthMask(true);
        GL11.glEnable(GL11.GL_TEXTURE_2D);
        GL11.glDisable(GL11.GL_BLEND);
        GL11.glColor4f(1, 1, 1, 1);
        GL11.glPopMatrix();
    }

    private void renderRainbows(Vec3 camera, long now, long duration, float lineWidth) {
        for (int r = 0; r < MAX_RAINBOWS; r++) {
            if (!rainbowActive[r]) continue;
            long age = now - rainbowTime[r];
            if (age > duration) { rainbowActive[r] = false; activeRainbowCount--; continue; }

            double progress = (double) age / (double) duration;
            double alpha;
            if (progress < 0.15) alpha = progress / 0.15;
            else if (progress > 0.6) alpha = (1.0 - progress) / 0.4;
            else alpha = 1.0;

            double arcProgress = progress < 0.2 ? Math.pow(progress / 0.2, 2.0) : 1.0;
            double drawX = rainbowX[r] - camera.xCoord;
            double drawY = rainbowY[r] - camera.yCoord;
            double drawZ = rainbowZ[r] - camera.zCoord;

            GL11.glPushMatrix();
            GL11.glTranslated(drawX, drawY, drawZ);
            GL11.glRotated(rainbowYaw[r], 0, 1, 0);

            for (int band = 0; band < 7; band++) {
                double radius = RAINBOW_SIZE + (band - 3) * RAINBOW_BAND_WIDTH;
                if (radius < 0.1) continue;
                double red = rainbowRed[band], green = rainbowGreen[band], blue = rainbowBlue[band];
                drawRainbowArc(radius, red, green, blue, alpha * 0.15, lineWidth + 3.0f, arcProgress);
                drawRainbowArc(radius, red, green, blue, alpha * 0.30, lineWidth + 1.5f, arcProgress);
                drawRainbowArc(radius, red, green, blue, alpha * 0.85, lineWidth, arcProgress);
            }
            GL11.glPopMatrix();
        }
    }

    private void drawRainbowArc(double radius, double red, double green, double blue,
                                double alpha, float lineWidth, double arcProgress) {
        GL11.glLineWidth(lineWidth);
        GL11.glColor4d(red, green, blue, alpha);
        GL11.glBegin(GL11.GL_LINE_STRIP);
        for (int i = 0; i <= 30; i++) {
            double angle = (double) i / 30 * Math.PI * arcProgress;
            GL11.glVertex3d(Math.cos(angle) * radius, Math.sin(angle) * radius, 0);
        }
        GL11.glEnd();
    }

    private void renderBedParticles(Vec3 camera, long now, long lifetime, float lineWidth) {
        GL11.glLineWidth(lineWidth);
        for (int i = 0; i < MAX_BED_PARTICLES; i++) {
            if (!bedParticleActive[i]) continue;
            long age = now - bedParticleTime[i];
            if (age > lifetime) { bedParticleActive[i] = false; activeBedParticleCount--; continue; }

            double progress = (double) age / (double) lifetime;
            double ageSeconds = age / 1000.0;
            double px = bedParticleX[i] + bedParticleVX[i] * ageSeconds;
            double py = bedParticleY[i] + bedParticleVY[i] * ageSeconds - 1.5 * ageSeconds * ageSeconds;
            double pz = bedParticleZ[i] + bedParticleVZ[i] * ageSeconds;
            px += Math.sin(age * 0.002 + i * 1.7) * 0.05;
            pz += Math.cos(age * 0.0015 + i * 2.3) * 0.05;

            double alpha;
            if (progress < 0.1) alpha = progress / 0.1;
            else if (progress > 0.7) alpha = (1.0 - progress) / 0.3;
            else alpha = 1.0;

            double scaleAnim;
            if (progress < 0.1) scaleAnim = progress / 0.1;
            else if (progress > 0.7) scaleAnim = (1.0 - progress) / 0.3;
            else scaleAnim = 1.0;

            double drawX = px - camera.xCoord;
            double drawY = py - camera.yCoord;
            double drawZ = pz - camera.zCoord;

            int colorIndex = i % 7;
            double red = bedParticleRed[colorIndex], green = bedParticleGreen[colorIndex], blue = bedParticleBlue[colorIndex];
            double size = bedParticleScale[i] * scaleAnim;
            double billboardYaw = Math.toDegrees(Math.atan2(drawX, drawZ));

            GL11.glPushMatrix();
            GL11.glTranslated(drawX, drawY, drawZ);
            GL11.glRotated(billboardYaw, 0, 1, 0);
            GL11.glRotated(ageSeconds * 40.0 + i * 60.0, 0, 0, 1);

            int type = bedParticleType[i];
            if (type == 0) drawHeartShape(bedHeartShapeX, bedHeartShapeY, 20, size, alpha, red, green, blue);
            else if (type == 1) drawStarShape(size, alpha, red, green, blue);
            else if (type == 2) drawBedDot(size, alpha, red, green, blue);
            else drawBedDiamond(size, alpha, red, green, blue);

            GL11.glPopMatrix();
        }
    }

    private void drawHeartShape(double[] shapeX, double[] shapeY, int segments,
                                double size, double baseAlpha, double red, double green, double blue) {
        double scale = size / 16.0;
        for (int layer = 2; layer >= 0; layer--) {
            double glowScale = scale * (1.0 + layer * 0.1);
            double layerAlpha = layer == 0 ? baseAlpha * 0.9 : baseAlpha * (0.25 / layer);
            GL11.glColor4d(red, green, blue, layerAlpha);
            GL11.glBegin(GL11.GL_LINE_LOOP);
            for (int i = 0; i <= segments; i++) {
                GL11.glVertex3d(shapeX[i] * glowScale, shapeY[i] * glowScale, 0);
            }
            GL11.glEnd();
        }
    }

    private void drawStarShape(double size, double baseAlpha, double red, double green, double blue) {
        double scale = size / 16.0;
        for (int layer = 2; layer >= 0; layer--) {
            double glowScale = scale * (1.0 + layer * 0.1);
            double layerAlpha = layer == 0 ? baseAlpha * 0.9 : baseAlpha * (0.25 / layer);
            GL11.glColor4d(red, green, blue, layerAlpha);
            GL11.glBegin(GL11.GL_LINE_LOOP);
            for (int i = 0; i <= 8; i++) {
                GL11.glVertex3d(starShapeX[i] * glowScale, starShapeY[i] * glowScale, 0);
            }
            GL11.glEnd();
        }
    }

    private void drawBedDot(double size, double baseAlpha, double red, double green, double blue) {
        double scale = size / 2.0;
        for (int layer = 2; layer >= 0; layer--) {
            double glowScale = scale * (1.0 + layer * 0.15);
            double layerAlpha = layer == 0 ? baseAlpha * 0.9 : baseAlpha * (0.25 / layer);
            GL11.glColor4d(red, green, blue, layerAlpha);
            GL11.glBegin(GL11.GL_TRIANGLE_FAN);
            GL11.glVertex3d(0, 0, 0);
            for (int i = 0; i <= 8; i++) {
                GL11.glVertex3d(dotCircleX[i] * glowScale, dotCircleY[i] * glowScale, 0);
            }
            GL11.glEnd();
        }
    }

    private void drawBedDiamond(double size, double baseAlpha, double red, double green, double blue) {
        double scale = size / 16.0;
        for (int layer = 2; layer >= 0; layer--) {
            double glowScale = scale * (1.0 + layer * 0.1);
            double layerAlpha = layer == 0 ? baseAlpha * 0.9 : baseAlpha * (0.25 / layer);
            GL11.glColor4d(red, green, blue, layerAlpha);
            GL11.glBegin(GL11.GL_LINE_LOOP);
            GL11.glVertex3d(0, 14.0 * glowScale, 0);
            GL11.glVertex3d(8.0 * glowScale, 0, 0);
            GL11.glVertex3d(0, -14.0 * glowScale, 0);
            GL11.glVertex3d(-8.0 * glowScale, 0, 0);
            GL11.glVertex3d(0, 14.0 * glowScale, 0);
            GL11.glEnd();
        }
    }
}
