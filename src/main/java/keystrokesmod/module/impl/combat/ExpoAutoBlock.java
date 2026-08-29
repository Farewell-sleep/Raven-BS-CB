package keystrokesmod.module.impl.combat;

import keystrokesmod.module.Module;
import keystrokesmod.module.ModuleManager;
import keystrokesmod.module.setting.impl.ButtonSetting;
import keystrokesmod.module.setting.impl.SliderSetting;
import keystrokesmod.utility.Utils;
import net.minecraft.entity.Entity;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.entity.boss.EntityDragon;
import net.minecraft.entity.boss.EntityWither;
import net.minecraft.entity.monster.EntityGolem;
import net.minecraft.entity.monster.EntityMob;
import net.minecraft.entity.monster.EntitySilverfish;
import net.minecraft.entity.passive.EntityAnimal;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.item.ItemStack;
import net.minecraft.item.ItemSword;
import net.minecraft.network.play.client.C02PacketUseEntity;
import net.minecraft.network.play.client.C07PacketPlayerDigging;
import net.minecraft.network.play.client.C08PacketPlayerBlockPlacement;
import net.minecraft.util.BlockPos;
import net.minecraft.util.EnumFacing;
import net.minecraft.util.MathHelper;
import net.minecraftforge.fml.common.eventhandler.SubscribeEvent;
import net.minecraftforge.fml.common.gameevent.TickEvent;

import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;

/**
 * ExpoAutoBlock - Auto sword blocking when enemies are nearby.
 * Skidded from Expo AutoBlock (deobfuscated & reimplemented for bs).
 *
 * Modes:
 *  - LAG / LAG_PRE / LAG_LEGIT / LAG_LEGIT_PRE / LAG_NEW / LAG_NEW_PRE:
 *    Packet-based blocking. Sends C07 RELEASE_USE_ITEM to unblock, attacks,
 *    then C08 placement to reblock. Achieves high APS while blocking.
 *  - LEGIT: Conservative blocking, only blocks when not attacking.
 *  - VANILLA: Uses itemInUse directly (client-side blocking only).
 *  - NONE: Disabled.
 */
public class ExpoAutoBlock extends Module {

    private static final String[] MODES = {
            "LAG_NEW", "LAG_NEW_PRE", "LAG", "LAG_PRE",
            "LAG_LEGIT", "LAG_LEGIT_PRE", "LEGIT", "VANILLA", "NONE"
    };
    private static final String[] APS_MODES = {"3APS", "5APS", "7APS", "10APS", "14APS"};

    // Settings
    private final SliderSetting mode;
    private final SliderSetting apsMode;
    private final SliderSetting targetRange;
    private final SliderSetting fov;
    private final SliderSetting smartUnblockTicks;
    private final SliderSetting smartUnblockChance;

    private final ButtonSetting requireKillAura;
    private final ButtonSetting requireRightClick;
    private final ButtonSetting manualLeftClick;
    private final ButtonSetting smartUnblock;
    private final ButtonSetting visualBlocking;

    // Target filters
    private final ButtonSetting players;
    private final ButtonSetting mobs;
    private final ButtonSetting animals;
    private final ButtonSetting bosses;
    private final ButtonSetting friends;
    private final ButtonSetting enemies;
    private final ButtonSetting teammates;
    private final ButtonSetting bots;
    private final ButtonSetting silverfishes;
    private final ButtonSetting golems;

    // State
    private EntityLivingBase target;
    private int stage = 0; // 0=idle, 1=blocking, 2=unblocked(attack), 3=reblocking
    private int attackTimer = 0;
    private int smartUnblockTimer = 0;
    private boolean leftClicked = false;
    private long lastBlockTime = 0;
    private boolean wasUsingItem = false;

    public ExpoAutoBlock() {
        super("ExpoAutoBlock", category.combat);

        this.registerSetting(mode = new SliderSetting("Mode", 0, MODES));
        this.registerSetting(apsMode = new SliderSetting("APS mode", 1, APS_MODES));
        this.registerSetting(targetRange = new SliderSetting("Target range", 5.0, 1.0, 8.0, 0.1));
        this.registerSetting(fov = new SliderSetting("FOV", 360.0, 1.0, 360.0, 1.0));
        this.registerSetting(smartUnblockTicks = new SliderSetting("Smart unblock ticks", 8, 0, 15, 1));
        this.registerSetting(smartUnblockChance = new SliderSetting("Smart unblock chance", 100, 0, 100, 1));

        this.registerSetting(requireKillAura = new ButtonSetting("Require KillAura", true));
        this.registerSetting(requireRightClick = new ButtonSetting("Require right click", false));
        this.registerSetting(manualLeftClick = new ButtonSetting("Manual left click", false));
        this.registerSetting(smartUnblock = new ButtonSetting("Smart unblock", false));
        this.registerSetting(visualBlocking = new ButtonSetting("Visual blocking", true));

        this.registerSetting(players = new ButtonSetting("Players", true));
        this.registerSetting(mobs = new ButtonSetting("Mobs", false));
        this.registerSetting(animals = new ButtonSetting("Animals", false));
        this.registerSetting(bosses = new ButtonSetting("Bosses", false));
        this.registerSetting(friends = new ButtonSetting("Friends", false));
        this.registerSetting(enemies = new ButtonSetting("Enemies", true));
        this.registerSetting(teammates = new ButtonSetting("Teammates", false));
        this.registerSetting(bots = new ButtonSetting("Bots", false));
        this.registerSetting(silverfishes = new ButtonSetting("Silverfishes", false));
        this.registerSetting(golems = new ButtonSetting("Golems", false));
    }

    @Override
    public void onEnable() {
        stage = 0;
        target = null;
        attackTimer = 0;
        smartUnblockTimer = 0;
        leftClicked = false;
        wasUsingItem = false;
    }

    @Override
    public void onDisable() {
        if (mc.thePlayer != null && mc.thePlayer.isUsingItem()) {
            mc.thePlayer.stopUsingItem();
        }
        stage = 0;
        target = null;
    }

    @Override
    public String getInfo() {
        return MODES[(int) mode.getInput()];
    }

    @SubscribeEvent
    public void onClientTick(TickEvent.ClientTickEvent event) {
        if (event.phase != TickEvent.Phase.START || !Utils.nullCheck()) return;

        String currentMode = MODES[(int) mode.getInput()];
        if (currentMode.equals("NONE")) {
            stopBlocking();
            return;
        }

        // Check if holding sword
        ItemStack held = mc.thePlayer.getHeldItem();
        if (held == null || !(held.getItem() instanceof ItemSword)) {
            stopBlocking();
            target = null;
            return;
        }

        // Require KillAura
        if (requireKillAura.isToggled()) {
            Module ka = ModuleManager.getModule("KillAura");
            if (ka == null || !ka.isEnabled()) {
                stopBlocking();
                target = null;
                return;
            }
        }

        // Require right click
        if (requireRightClick.isToggled() && !mc.gameSettings.keyBindUseItem.isKeyDown()) {
            stopBlocking();
            return;
        }

        // Manual left click tracking
        if (mc.gameSettings.keyBindAttack.isKeyDown()) {
            leftClicked = true;
        }

        // Find target
        target = findTarget();

        // Smart unblock
        if (smartUnblock.isToggled() && smartUnblockTimer > 0) {
            smartUnblockTimer--;
            if (smartUnblockTimer == 0) {
                // Chance-based: if failed, keep blocking
                if (Math.random() * 100 > smartUnblockChance.getInput()) {
                    // Keep blocking, don't attack
                    return;
                }
            } else {
                return;
            }
        }

        if (target == null) {
            stopBlocking();
            return;
        }

        // Execute based on mode
        switch (currentMode) {
            case "LAG":
            case "LAG_PRE":
            case "LAG_LEGIT":
            case "LAG_LEGIT_PRE":
            case "LAG_NEW":
            case "LAG_NEW_PRE":
                doLagBlock(currentMode);
                break;
            case "LEGIT":
                doLegitBlock();
                break;
            case "VANILLA":
                doVanillaBlock();
                break;
        }
    }

    private void doLagBlock(String currentMode) {
        boolean isPre = currentMode.contains("PRE");
        boolean isLegit = currentMode.contains("LEGIT");
        boolean isNew = currentMode.contains("NEW");

        int aps = getAPS();
        int attackInterval = Math.max(1, 20 / aps);

        if (attackTimer > 0) {
            attackTimer--;
        }

        // Stage machine
        if (stage == 0) {
            // Start blocking
            startBlocking();
            stage = 1;
        } else if (stage == 1) {
            // Blocking, check if should attack
            if (attackTimer <= 0) {
                if (manualLeftClick.isToggled() && !leftClicked) {
                    return;
                }
                leftClicked = false;

                // Unblock
                unblock();
                stage = 2;
                attackTimer = attackInterval;

                // Attack immediately for PRE modes
                if (isPre) {
                    attackTarget();
                    if (isLegit) {
                        // LEGIT_PRE: wait a tick then reblock
                        stage = 3;
                    } else {
                        reblock();
                        stage = 1;
                    }
                }
            }
        } else if (stage == 2) {
            // Unblocked, attack (for non-PRE modes)
            if (!isPre) {
                attackTarget();
            }

            if (isLegit && !isPre) {
                // LAG_LEGIT: wait before reblocking
                stage = 3;
            } else if (!isPre) {
                reblock();
                stage = 1;
            }
        } else if (stage == 3) {
            // Reblock after delay
            reblock();
            stage = 1;
        }
    }

    private void doLegitBlock() {
        // Only block when not attacking
        if (mc.gameSettings.keyBindAttack.isKeyDown()) {
            if (mc.thePlayer.isUsingItem()) {
                unblock();
            }
            return;
        }
        startBlocking();
    }

    private void doVanillaBlock() {
        // Client-side blocking via itemInUse
        if (!mc.thePlayer.isUsingItem()) {
            mc.thePlayer.setItemInUse(mc.thePlayer.getHeldItem(),
                    mc.thePlayer.getHeldItem().getMaxItemUseDuration());
        }
    }

    private void startBlocking() {
        if (mc.thePlayer.isUsingItem()) {
            wasUsingItem = true;
            return;
        }
        ItemStack held = mc.thePlayer.getHeldItem();
        if (held == null) return;

        // Send C08 to start blocking
        mc.getNetHandler().addToSendQueue(new C08PacketPlayerBlockPlacement(held));
        mc.thePlayer.setItemInUse(held, held.getMaxItemUseDuration());
        wasUsingItem = true;
        lastBlockTime = System.currentTimeMillis();
    }

    private void unblock() {
        if (!mc.thePlayer.isUsingItem() && !wasUsingItem) return;

        // Send C07 RELEASE_USE_ITEM
        mc.getNetHandler().addToSendQueue(new C07PacketPlayerDigging(
                C07PacketPlayerDigging.Action.RELEASE_USE_ITEM,
                BlockPos.ORIGIN, EnumFacing.DOWN));
        mc.thePlayer.stopUsingItem();
        wasUsingItem = false;
    }

    private void reblock() {
        ItemStack held = mc.thePlayer.getHeldItem();
        if (held == null) return;

        mc.getNetHandler().addToSendQueue(new C08PacketPlayerBlockPlacement(held));
        mc.thePlayer.setItemInUse(held, held.getMaxItemUseDuration());
        wasUsingItem = true;
    }

    private void stopBlocking() {
        if (mc.thePlayer.isUsingItem() || wasUsingItem) {
            unblock();
        }
        stage = 0;
    }

    private void attackTarget() {
        if (target == null) return;

        // Swing and attack
        mc.thePlayer.swingItem();
        mc.getNetHandler().addToSendQueue(new C02PacketUseEntity(target, C02PacketUseEntity.Action.ATTACK));

        // Smart unblock trigger
        if (smartUnblock.isToggled() && target.hurtTime > 0) {
            smartUnblockTimer = (int) smartUnblockTicks.getInput();
        }
    }

    private int getAPS() {
        switch (APS_MODES[(int) apsMode.getInput()]) {
            case "3APS": return 3;
            case "5APS": return 5;
            case "7APS": return 7;
            case "10APS": return 10;
            case "14APS": return 14;
            default: return 5;
        }
    }

    private EntityLivingBase findTarget() {
        double range = targetRange.getInput();
        List<EntityLivingBase> candidates = new ArrayList<>();

        for (Entity entity : mc.theWorld.loadedEntityList) {
            if (!(entity instanceof EntityLivingBase)) continue;
            if (entity == mc.thePlayer) continue;
            if (entity.isDead) continue;

            EntityLivingBase living = (EntityLivingBase) entity;
            if (living.getHealth() <= 0) continue;

            double dist = mc.thePlayer.getDistanceToEntity(living);
            if (dist > range) continue;

            // FOV check
            if (fov.getInput() < 360) {
                if (!isInFOV(living, (float) fov.getInput())) continue;
            }

            // Filter by type
            if (!isValidTarget(living)) continue;

            candidates.add(living);
        }

        if (candidates.isEmpty()) return null;

        // Sort by distance
        candidates.sort(Comparator.comparingDouble(e -> mc.thePlayer.getDistanceToEntity(e)));
        return candidates.get(0);
    }

    private boolean isValidTarget(EntityLivingBase entity) {
        if (entity instanceof EntityPlayer) {
            EntityPlayer player = (EntityPlayer) entity;

            // Friends
            if (isFriend(player.getName()) && !friends.isToggled()) return false;
            // Enemies
            if (isEnemy(player.getName()) && !enemies.isToggled()) return false;
            // Teammates
            if (isTeammate(player) && !teammates.isToggled()) return false;
            // Bots
            if (isBot(player) && !bots.isToggled()) return false;

            return players.isToggled();
        }

        if (entity instanceof EntitySilverfish) return silverfishes.isToggled();
        if (entity instanceof EntityGolem) return golems.isToggled();
        if (entity instanceof EntityDragon || entity instanceof EntityWither) return bosses.isToggled();
        if (entity instanceof EntityMob) return mobs.isToggled();
        if (entity instanceof EntityAnimal) return animals.isToggled();

        return false;
    }

    private boolean isInFOV(EntityLivingBase entity, float fov) {
        double diffX = entity.posX - mc.thePlayer.posX;
        double diffZ = entity.posZ - mc.thePlayer.posZ;
        float yaw = (float) (Math.toDegrees(Math.atan2(diffZ, diffX)) - 90);
        float diff = Math.abs(((mc.thePlayer.rotationYaw - yaw) % 360 + 540) % 360 - 180);
        return diff <= fov / 2;
    }

    private boolean isFriend(String name) {
        // bs has no built-in friend system in this context
        return false;
    }

    private boolean isEnemy(String name) {
        return false;
    }

    private boolean isTeammate(EntityPlayer player) {
        // Simple teammate check: same team color
        try {
            if (player.getTeam() != null && mc.thePlayer.getTeam() != null) {
                return player.getTeam().isSameTeam(mc.thePlayer.getTeam());
            }
        } catch (Exception e) {
            // ignore
        }
        return false;
    }

    private boolean isBot(EntityPlayer player) {
        // Simple bot heuristic: no tab entry / weird name
        try {
            if (player.getName() == null || player.getName().isEmpty()) return true;
            if (player.ticksExisted < 5) return true;
        } catch (Exception e) {
            // ignore
        }
        return false;
    }
}
