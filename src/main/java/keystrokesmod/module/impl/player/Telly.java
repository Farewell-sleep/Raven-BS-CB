package keystrokesmod.module.impl.player;

import keystrokesmod.event.PostPlayerInputEvent;
import keystrokesmod.event.PostUpdateEvent;
import keystrokesmod.event.PreMotionEvent;
import keystrokesmod.event.PreUpdateEvent;
import keystrokesmod.event.ReceivePacketEvent;
import keystrokesmod.event.SendPacketEvent;
import keystrokesmod.module.Module;
import keystrokesmod.module.ModuleManager;
import myaupp.OpenMyau;
import myaupp.event.EventManager;
import myaupp.event.types.EventType;
import myaupp.module.ExternalModule;
import net.minecraftforge.client.event.RenderWorldLastEvent;
import net.minecraftforge.common.MinecraftForge;
import net.minecraftforge.event.entity.player.PlayerInteractEvent;
import net.minecraftforge.event.world.WorldEvent;
import net.minecraftforge.fml.common.eventhandler.EventPriority;
import net.minecraftforge.fml.common.eventhandler.SubscribeEvent;
import net.minecraftforge.fml.common.gameevent.TickEvent;

/**
 * Telly —— OpenMyauPP 的 Telly 完整移植。
 * <p>
 * 内部持有 OpenMyauPP 原版 Telly 核心（myaupp.module.modules.Telly），
 * 事件桥接层把 Raven BS 的 Forge 事件转译成 myaupp 事件系统调用。
 * 搭路/激活/旋转逻辑均为 OpenMyauPP 原版 1:1，未使用 Raven 的搭路类。
 * 仅模块注册与按键绑定使用 Raven 基础设施。
 */
public class Telly extends Module {
    private final myaupp.module.modules.Telly core;
    private boolean pendingRot;
    private float pendingYaw;
    private float pendingPitch;

    public Telly() {
        super("Telly", Module.category.player, 0);
        this.core = new myaupp.module.modules.Telly();
        EventManager.register(core);
        // 桥接宿主模块（SafeWalk/Timer），供 Telly 的 disableSafeWalk/isRavenTimerActive 使用。
        registerExternalModule("SafeWalk");
        registerExternalModule("Timer");
    }

    private void registerExternalModule(String name) {
        Module m = ModuleManager.getModule(name);
        if (m != null) {
            OpenMyau.moduleManager.register(new ExternalModule(name, m::isEnabled, m::setEnabled));
        }
    }

    @Override
    public void onEnable() {
        core.setEnabled(true);
        MinecraftForge.EVENT_BUS.register(this);
    }

    @Override
    public void onDisable() {
        MinecraftForge.EVENT_BUS.unregister(this);
        core.setEnabled(false);
    }

    // ===== Myaupp UpdateEvent (PRE/POST) =====
    @SubscribeEvent(priority = EventPriority.HIGHEST)
    public void onPreUpdate(PreUpdateEvent e) {
        if (mc.thePlayer == null) {
            return;
        }
        myaupp.events.UpdateEvent event = new myaupp.events.UpdateEvent(
                EventType.PRE,
                mc.thePlayer.rotationYaw,
                mc.thePlayer.rotationPitch,
                mc.thePlayer.rotationYaw,
                mc.thePlayer.rotationPitch);
        EventManager.call(event);
        if (event.isRotated()) {
            pendingRot = true;
            pendingYaw = event.getNewYaw();
            pendingPitch = event.getNewPitch();
        } else {
            pendingRot = false;
        }
    }

    @SubscribeEvent(priority = EventPriority.HIGHEST)
    public void onPostUpdate(PostUpdateEvent e) {
        if (mc.thePlayer == null) {
            return;
        }
        EventManager.call(new myaupp.events.UpdateEvent(
                EventType.POST,
                mc.thePlayer.rotationYaw,
                mc.thePlayer.rotationPitch,
                mc.thePlayer.rotationYaw,
                mc.thePlayer.rotationPitch));
    }

    @SubscribeEvent(priority = EventPriority.HIGHEST)
    public void onPreMotion(PreMotionEvent e) {
        if (pendingRot) {
            e.setRotations(pendingYaw, pendingPitch);
            pendingRot = false;
        }
    }

    // ===== MoveInput + LivingUpdate + SafeWalk =====
    @SubscribeEvent
    public void onPostPlayerInput(PostPlayerInputEvent e) {
        if (!core.isEnabled() || mc.thePlayer == null) {
            return;
        }
        EventManager.call(new myaupp.events.MoveInputEvent());
        EventManager.call(new myaupp.events.LivingUpdateEvent());
        EventManager.call(new myaupp.events.SafeWalkEvent(false));
    }

    // ===== Render =====
    @SubscribeEvent
    public void onRenderTick(TickEvent.RenderTickEvent e) {
        if (e.phase != TickEvent.Phase.END || !core.isEnabled()) {
            return;
        }
        EventManager.call(new myaupp.events.Render2DEvent(e.renderTickTime));
    }

    @SubscribeEvent
    public void onRenderWorld(RenderWorldLastEvent e) {
        if (!core.isEnabled()) {
            return;
        }
        EventManager.call(new myaupp.events.Render3DEvent(e.partialTicks));
    }

    // ===== Packet =====
    @SubscribeEvent(priority = EventPriority.HIGHEST)
    public void onSendPacket(SendPacketEvent e) {
        if (!core.isEnabled()) {
            return;
        }
        myaupp.events.PacketEvent event = new myaupp.events.PacketEvent(EventType.SEND, e.getPacket());
        EventManager.call(event);
        if (event.isCancelled()) {
            e.setCanceled(true);
        }
    }

    @SubscribeEvent(priority = EventPriority.HIGHEST)
    public void onReceivePacket(ReceivePacketEvent e) {
        if (!core.isEnabled()) {
            return;
        }
        myaupp.events.PacketEvent event = new myaupp.events.PacketEvent(EventType.RECEIVE, e.getPacket());
        EventManager.call(event);
        if (event.isCancelled()) {
            e.setCanceled(true);
        }
    }

    // ===== World load =====
    @SubscribeEvent
    public void onWorldLoad(WorldEvent.Load e) {
        if (!core.isEnabled() || e.world == null || !e.world.isRemote) {
            return;
        }
        EventManager.call(new myaupp.events.LoadWorldEvent());
    }

    // ===== Mouse / interact =====
    @SubscribeEvent
    public void onRightClick(keystrokesmod.event.RightClickMouseEvent e) {
        if (!core.isEnabled()) {
            return;
        }
        myaupp.events.RightClickMouseEvent event = new myaupp.events.RightClickMouseEvent();
        EventManager.call(event);
        if (event.isCancelled()) {
            e.setCanceled(true);
        }
    }

    @SubscribeEvent
    public void onMouse(net.minecraftforge.client.event.MouseEvent e) {
        if (!core.isEnabled() || e.button != 0 || !e.buttonstate || mc.currentScreen != null) {
            return;
        }
        myaupp.events.LeftClickMouseEvent event = new myaupp.events.LeftClickMouseEvent();
        EventManager.call(event);
        if (event.isCancelled()) {
            e.setCanceled(true);
        }
    }

    @SubscribeEvent
    public void onHitBlock(PlayerInteractEvent e) {
        if (!core.isEnabled() || e.action != PlayerInteractEvent.Action.LEFT_CLICK_BLOCK) {
            return;
        }
        myaupp.events.HitBlockEvent event = new myaupp.events.HitBlockEvent();
        EventManager.call(event);
        if (event.isCancelled()) {
            e.setCanceled(true);
        }
    }

    @Override
    public String getInfo() {
        return "";
    }
}
