package keystrokesmod.module.impl.combat;

import keystrokesmod.event.PostPlayerInputEvent;
import keystrokesmod.event.PostUpdateEvent;
import keystrokesmod.event.PreMotionEvent;
import keystrokesmod.event.PreUpdateEvent;
import keystrokesmod.event.ReceivePacketEvent;
import keystrokesmod.event.SendPacketEvent;
import keystrokesmod.module.Module;
import keystrokesmod.module.setting.impl.ButtonSetting;
import keystrokesmod.module.setting.impl.SliderSetting;
import myau.OpenMyau;
import myau.event.EventManager;
import myau.event.types.EventType;
import myau.management.RotationState;
import net.minecraft.network.play.client.C07PacketPlayerDigging;
import net.minecraftforge.client.event.RenderWorldLastEvent;
import net.minecraftforge.common.MinecraftForge;
import net.minecraftforge.event.entity.player.PlayerInteractEvent;
import net.minecraftforge.event.world.WorldEvent;
import net.minecraftforge.fml.common.eventhandler.EventPriority;
import net.minecraftforge.fml.common.eventhandler.SubscribeEvent;
import net.minecraftforge.fml.common.gameevent.TickEvent;

/**
 * MyauKillaura —— OpenMyauPP 的 KillAura 完整移植。
 * 内部持有 OpenMyauPP 原版 KillAura 核心（myau.module.modules.KillAura），
 * 事件桥接层把 Raven BS 的 Forge 事件转译成 myau 事件系统调用。
 * 设置桥接层把 BS Setting 同步到 myau Property。
 * 攻击/旋转/自动格挡/目标选择逻辑均为 OpenMyauPP 原版 1:1。
 */
public class MyauKillaura extends Module {
    private final myau.module.modules.KillAura core;
    private boolean managersRegistered;

    // ===== BS Setting（桥接到 myau Property）=====
    private final SliderSetting mode = new SliderSetting("Mode", 0, new String[]{"Single", "Switch"});
    private final SliderSetting sort = new SliderSetting("Sort", 0, new String[]{"Distance", "Health", "Hurt time", "FOV"});
    private final SliderSetting autoBlock = new SliderSetting("Auto block", 2,
            new String[]{"None", "Vanilla", "Spoof", "Hypixel", "Blink", "Interact", "Swap", "Legit"});
    private final SliderSetting rotations = new SliderSetting("Rotations", 2, new String[]{"None", "Legit", "Silent", "Lock view"});
    private final SliderSetting moveFix = new SliderSetting("Move fix", 1, new String[]{"None", "Silent", "Strict"});
    private final SliderSetting showTarget = new SliderSetting("Show target", 0, new String[]{"None", "Default", "HUD"});
    private final SliderSetting minCPS = new SliderSetting("Min CPS", 14, 1, 20, 1);
    private final SliderSetting maxCPS = new SliderSetting("Max CPS", 14, 1, 20, 1);
    private final SliderSetting switchDelay = new SliderSetting("Switch delay", 150, 0, 1000, 10);
    private final SliderSetting attackRange = new SliderSetting("Attack range", 3.0, 3.0, 6.0, 0.1);
    private final SliderSetting swingRange = new SliderSetting("Swing range", 3.5, 3.0, 6.0, 0.1);
    private final SliderSetting fov = new SliderSetting("FOV", 360, 30, 360, 1);
    private final SliderSetting smoothing = new SliderSetting("Smoothing", 0, 0, 100, 1);
    private final SliderSetting angleStep = new SliderSetting("Angle step", 90, 30, 180, 1);
    private final ButtonSetting throughWalls = new ButtonSetting("Through walls", true);
    private final ButtonSetting requirePress = new ButtonSetting("Require press", false);
    private final ButtonSetting weaponsOnly = new ButtonSetting("Weapons only", true);
    private final ButtonSetting players = new ButtonSetting("Players", true);
    private final ButtonSetting mobs = new ButtonSetting("Mobs", false);
    private final ButtonSetting animals = new ButtonSetting("Animals", false);
    private final ButtonSetting bosses = new ButtonSetting("Bosses", false);
    private final ButtonSetting teams = new ButtonSetting("Teams", true);

    public MyauKillaura() {
        super("MyauKillaura", Module.category.combat, 0);
        this.core = new myau.module.modules.KillAura();
        EventManager.register(core);
        // 注册 BS Setting
        this.registerSetting(mode);
        this.registerSetting(sort);
        this.registerSetting(autoBlock);
        this.registerSetting(rotations);
        this.registerSetting(moveFix);
        this.registerSetting(showTarget);
        this.registerSetting(minCPS);
        this.registerSetting(maxCPS);
        this.registerSetting(switchDelay);
        this.registerSetting(attackRange);
        this.registerSetting(swingRange);
        this.registerSetting(fov);
        this.registerSetting(smoothing);
        this.registerSetting(angleStep);
        this.registerSetting(throughWalls);
        this.registerSetting(requirePress);
        this.registerSetting(weaponsOnly);
        this.registerSetting(players);
        this.registerSetting(mobs);
        this.registerSetting(animals);
        this.registerSetting(bosses);
        this.registerSetting(teams);
    }

    /**
     * 把 BS Setting 的值同步到 myau Property（每 tick 调用）。
     */
    private void syncSettings() {
        core.mode.setValue((int) mode.getInput());
        core.sort.setValue((int) sort.getInput());
        core.autoBlock.setValue((int) autoBlock.getInput());
        core.rotations.setValue((int) rotations.getInput());
        core.moveFix.setValue((int) moveFix.getInput());
        core.showTarget.setValue((int) showTarget.getInput());
        core.minCPS.setValue((int) minCPS.getInput());
        core.maxCPS.setValue((int) maxCPS.getInput());
        core.switchDelay.setValue((int) switchDelay.getInput());
        core.attackRange.setValue((float) attackRange.getInput());
        core.swingRange.setValue((float) swingRange.getInput());
        core.fov.setValue((int) fov.getInput());
        core.smoothing.setValue((int) smoothing.getInput());
        core.angleStep.setValue((int) angleStep.getInput());
        core.throughWalls.setValue(throughWalls.isToggled());
        core.requirePress.setValue(requirePress.isToggled());
        core.weaponsOnly.setValue(weaponsOnly.isToggled());
        core.players.setValue(players.isToggled());
        core.mobs.setValue(mobs.isToggled());
        core.animals.setValue(animals.isToggled());
        core.bosses.setValue(bosses.isToggled());
        core.teams.setValue(teams.isToggled());
    }

    private void ensureManagers() {
        if (!managersRegistered) {
            EventManager.register(OpenMyau.rotationManager);
            EventManager.register(OpenMyau.blinkManager);
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

    // ===== Myau UpdateEvent (PRE/POST) =====
    @SubscribeEvent(priority = EventPriority.HIGHEST)
    public void onPreUpdate(PreUpdateEvent e) {
        if (mc.thePlayer == null) return;
        syncSettings();
        myau.events.UpdateEvent event = new myau.events.UpdateEvent(
                EventType.PRE,
                mc.thePlayer.rotationYaw, mc.thePlayer.rotationPitch,
                mc.thePlayer.rotationYaw, mc.thePlayer.rotationPitch);
        EventManager.call(event);
    }

    @SubscribeEvent(priority = EventPriority.HIGHEST)
    public void onPostUpdate(PostUpdateEvent e) {
        if (mc.thePlayer == null) return;
        EventManager.call(new myau.events.UpdateEvent(
                EventType.POST,
                mc.thePlayer.rotationYaw, mc.thePlayer.rotationPitch,
                mc.thePlayer.rotationYaw, mc.thePlayer.rotationPitch));
    }

    // ===== Myau TickEvent (PRE/POST) =====
    @SubscribeEvent
    public void onClientTick(TickEvent.ClientTickEvent e) {
        if (mc.thePlayer == null) return;
        EventType type = (e.phase == TickEvent.Phase.START) ? EventType.PRE : EventType.POST;
        EventManager.call(new myau.events.TickEvent(type));
    }

    // ===== 旋转应用 =====
    @SubscribeEvent(priority = EventPriority.HIGHEST)
    public void onPreMotion(PreMotionEvent e) {
        if (RotationState.isActived()) {
            e.setRotations(RotationState.getSmoothedYaw(), RotationState.getRotationPitch());
        }
    }

    // ===== MoveInput =====
    @SubscribeEvent
    public void onPostPlayerInput(PostPlayerInputEvent e) {
        if (!core.isEnabled() || mc.thePlayer == null) return;
        EventManager.call(new myau.events.MoveInputEvent());
    }

    // ===== Render =====
    @SubscribeEvent
    public void onRenderWorld(RenderWorldLastEvent e) {
        if (!core.isEnabled()) return;
        EventManager.call(new myau.events.Render3DEvent(e.partialTicks));
    }

    // ===== Packet =====
    @SubscribeEvent(priority = EventPriority.HIGHEST)
    public void onSendPacket(SendPacketEvent e) {
        if (!core.isEnabled()) return;
        myau.events.PacketEvent event = new myau.events.PacketEvent(EventType.SEND, e.getPacket());
        EventManager.call(event);
        if (event.isCancelled()) e.setCanceled(true);
        if (e.getPacket() instanceof C07PacketPlayerDigging) {
            C07PacketPlayerDigging dig = (C07PacketPlayerDigging) e.getPacket();
            if (dig.getStatus() == C07PacketPlayerDigging.Action.RELEASE_USE_ITEM) {
                EventManager.call(new myau.events.CancelUseEvent());
            }
        }
    }

    @SubscribeEvent(priority = EventPriority.HIGHEST)
    public void onReceivePacket(ReceivePacketEvent e) {
        if (!core.isEnabled()) return;
        myau.events.PacketEvent event = new myau.events.PacketEvent(EventType.RECEIVE, e.getPacket());
        EventManager.call(event);
        if (event.isCancelled()) e.setCanceled(true);
    }

    // ===== World load =====
    @SubscribeEvent
    public void onWorldLoad(WorldEvent.Load e) {
        if (!core.isEnabled() || e.world == null || !e.world.isRemote) return;
        EventManager.call(new myau.events.LoadWorldEvent());
    }

    // ===== Mouse =====
    @SubscribeEvent
    public void onRightClick(keystrokesmod.event.RightClickMouseEvent e) {
        if (!core.isEnabled()) return;
        myau.events.RightClickMouseEvent event = new myau.events.RightClickMouseEvent();
        EventManager.call(event);
        if (event.isCancelled()) e.setCanceled(true);
    }

    @SubscribeEvent
    public void onMouse(net.minecraftforge.client.event.MouseEvent e) {
        if (!core.isEnabled() || e.button != 0 || !e.buttonstate || mc.currentScreen != null) return;
        myau.events.LeftClickMouseEvent event = new myau.events.LeftClickMouseEvent();
        EventManager.call(event);
        if (event.isCancelled()) e.setCanceled(true);
    }

    // ===== HitBlock =====
    @SubscribeEvent
    public void onHitBlock(PlayerInteractEvent e) {
        if (!core.isEnabled() || e.action != PlayerInteractEvent.Action.LEFT_CLICK_BLOCK) return;
        myau.events.HitBlockEvent event = new myau.events.HitBlockEvent();
        EventManager.call(event);
        if (event.isCancelled()) e.setCanceled(true);
    }

    @Override
    public String getInfo() {
        return core.mode.getModeString();
    }
}
