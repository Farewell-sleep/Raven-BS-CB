package keystrokesmod.module.impl.player;

import keystrokesmod.event.PlayerMoveEvent;
import keystrokesmod.event.PostPlayerInputEvent;
import keystrokesmod.event.PreMotionEvent;
import keystrokesmod.event.PreSlotScrollEvent;
import keystrokesmod.event.PreUpdateEvent;
import keystrokesmod.event.StrafeEvent;
import keystrokesmod.module.Module;
import keystrokesmod.module.setting.impl.ButtonSetting;
import keystrokesmod.module.setting.impl.SliderSetting;
import myau.event.EventManager;
import myau.event.types.EventType;
import myau.property.properties.BooleanProperty;
import myau.property.properties.FloatProperty;
import myau.property.properties.IntProperty;
import myau.property.properties.ModeProperty;
import myau.property.properties.PercentProperty;

import java.lang.reflect.Field;
import net.minecraft.client.Minecraft;
import net.minecraft.util.MathHelper;
import net.minecraftforge.client.event.RenderWorldLastEvent;
import net.minecraftforge.common.MinecraftForge;
import net.minecraftforge.event.entity.player.PlayerInteractEvent;
import net.minecraftforge.fml.common.eventhandler.EventPriority;
import net.minecraftforge.fml.common.eventhandler.SubscribeEvent;
import net.minecraftforge.fml.common.gameevent.TickEvent;

/**
 * MyauScaffold —— Myau 客户端 Scaffold 的完整移植。
 * <p>
 * 内部持有 Myau 原版 Scaffold 核心（myau.module.modules.MyauScaffold），
 * 通过事件桥接把 Raven BS 的 Forge 事件转译成 Myau 事件系统调用。
 * 所有搭路逻辑、旋转逻辑均为 Myau 原版 1:1，未使用 Raven 的搭路类。
 */
public class MyauScaffold extends Module {
    private final myau.module.modules.MyauScaffold core;
    private static volatile boolean safeWalkActive;
    private boolean pendingRot;
    private float pendingYaw;
    private float pendingPitch;

    public MyauScaffold() {
        super("MyauScaffold", Module.category.player, 0);
        this.core = new myau.module.modules.MyauScaffold();
        EventManager.register(core);
        registerMyauSettings();
    }

    /**
     * 把 Myau 核心的全部属性桥接成 Raven clickgui 可编辑的 Setting（双向同步）。
     */
    private void registerMyauSettings() {
        try {
            for (Field field : core.getClass().getDeclaredFields()) {
                field.setAccessible(true);
                Object obj = field.get(core);
                if (obj instanceof BooleanProperty) {
                    registerSetting(new MyauButtonSetting((BooleanProperty) obj));
                } else if (obj instanceof ModeProperty) {
                    registerSetting(new MyauModeSetting((ModeProperty) obj));
                } else if (obj instanceof FloatProperty) {
                    registerSetting(new MyauFloatSlider((FloatProperty) obj));
                } else if (obj instanceof IntProperty) {
                    registerSetting(new MyauIntSlider((IntProperty) obj));
                } else if (obj instanceof PercentProperty) {
                    registerSetting(new MyauPercentSlider((PercentProperty) obj));
                }
            }
        } catch (Exception ignored) {
        }
    }

    @Override
    public void onEnable() {
        core.setEnabled(true);
        MinecraftForge.EVENT_BUS.register(this);
        safeWalkActive = false;
    }

    @Override
    public void onDisable() {
        MinecraftForge.EVENT_BUS.unregister(this);
        core.setEnabled(false);
        safeWalkActive = false;
    }

    public static boolean isSafeWalkActive() {
        return safeWalkActive;
    }

    // ===== Myau UpdateEvent (PRE) -> 静默旋转 =====
    @SubscribeEvent(priority = EventPriority.HIGHEST)
    public void onPreUpdate(PreUpdateEvent e) {
        if (mc.thePlayer == null) {
            return;
        }
        myau.events.UpdateEvent event = new myau.events.UpdateEvent(
                EventType.PRE,
                mc.thePlayer.rotationYaw,
                mc.thePlayer.rotationPitch,
                mc.thePlayer.rotationYaw,
                mc.thePlayer.rotationPitch);
        EventManager.call(event);

        boolean rotated = event.isRotated();
        myau.management.RotationState.applyState(
                rotated && !mc.thePlayer.isRiding(),
                event.getNewYaw(),
                event.getNewPitch(),
                event.getPreYaw(),
                event.isRotating());
        if (rotated) {
            pendingRot = true;
            pendingYaw = event.getNewYaw();
            pendingPitch = event.getNewPitch();
        } else {
            pendingRot = false;
        }
    }

    @SubscribeEvent(priority = EventPriority.HIGHEST)
    public void onPreMotion(PreMotionEvent e) {
        if (pendingRot) {
            e.setRotations(pendingYaw, pendingPitch);
            pendingRot = false;
        }
    }

    // ===== Myau StrafeEvent =====
    @SubscribeEvent
    public void onStrafe(StrafeEvent e) {
        if (!core.isEnabled()) {
            return;
        }
        myau.events.StrafeEvent event = new myau.events.StrafeEvent(e.getStrafe(), e.getForward(), e.getFriction());
        EventManager.call(event);
        e.setForward(event.getForward());
        e.setStrafe(event.getStrafe());
    }

    // ===== Myau MoveInputEvent + LivingUpdateEvent + SafeWalkEvent =====
    // PostPlayerInputEvent 在 updatePlayerMoveState() 之后发布，与 Myau 的事件时机一致。
    @SubscribeEvent
    public void onPostPlayerInput(PostPlayerInputEvent e) {
        if (!core.isEnabled() || mc.thePlayer == null) {
            return;
        }
        EventManager.call(new myau.events.MoveInputEvent());
        EventManager.call(new myau.events.LivingUpdateEvent());

        myau.events.SafeWalkEvent safeWalk = new myau.events.SafeWalkEvent(false);
        EventManager.call(safeWalk);
        safeWalkActive = safeWalk.isSafeWalk();
    }

    // ===== Myau Render2DEvent =====
    @SubscribeEvent
    public void onRenderTick(TickEvent.RenderTickEvent e) {
        if (e.phase != TickEvent.Phase.END || !core.isEnabled()) {
            return;
        }
        EventManager.call(new myau.events.Render2DEvent(e.renderTickTime));
    }

    // ===== Myau Render3DEvent =====
    @SubscribeEvent
    public void onRenderWorld(RenderWorldLastEvent e) {
        if (!core.isEnabled()) {
            return;
        }
        EventManager.call(new myau.events.Render3DEvent(e.partialTicks));
    }

    // ===== Myau RightClickMouseEvent (取消原版右键放置) =====
    @SubscribeEvent
    public void onRightClick(keystrokesmod.event.RightClickMouseEvent e) {
        if (!core.isEnabled()) {
            return;
        }
        myau.events.RightClickMouseEvent event = new myau.events.RightClickMouseEvent();
        EventManager.call(event);
        if (event.isCancelled()) {
            e.setCanceled(true);
        }
    }

    // ===== Myau LeftClickMouseEvent (取消原版左键) =====
    @SubscribeEvent
    public void onMouse(net.minecraftforge.client.event.MouseEvent e) {
        if (!core.isEnabled() || e.button != 0 || !e.buttonstate || mc.currentScreen != null) {
            return;
        }
        myau.events.LeftClickMouseEvent event = new myau.events.LeftClickMouseEvent();
        EventManager.call(event);
        if (event.isCancelled()) {
            e.setCanceled(true);
        }
    }

    // ===== Myau HitBlockEvent (取消原版挖方块) =====
    @SubscribeEvent
    public void onHitBlock(PlayerInteractEvent e) {
        if (!core.isEnabled() || e.action != PlayerInteractEvent.Action.LEFT_CLICK_BLOCK) {
            return;
        }
        myau.events.HitBlockEvent event = new myau.events.HitBlockEvent();
        EventManager.call(event);
        if (event.isCancelled()) {
            e.setCanceled(true);
        }
    }

    // ===== Myau SwapItemEvent (接管滚轮切槽) =====
    @SubscribeEvent
    public void onSwap(PreSlotScrollEvent e) {
        if (!core.isEnabled()) {
            return;
        }
        myau.events.SwapItemEvent event = new myau.events.SwapItemEvent(e.slot, e.slot - e.previousSlot);
        EventManager.call(event);
        if (event.isCancelled()) {
            e.slot = event.setSlot(e.previousSlot);
            e.setCanceled(true);
        }
    }

    @Override
    public String getInfo() {
        return core.isEnabled() ? "" : super.getInfo();
    }

    /**
     * 布尔属性桥接 Setting。
     */
    private static final class MyauButtonSetting extends ButtonSetting {
        private final BooleanProperty prop;

        MyauButtonSetting(BooleanProperty prop) {
            super(prop.getName(), prop.getValue());
            this.prop = prop;
        }

        @Override
        public void toggle() {
            super.toggle();
            prop.setValue(isToggled());
        }

        @Override
        public void setEnabled(boolean b) {
            super.setEnabled(b);
            prop.setValue(b);
        }

        @Override
        public void enable() {
            super.enable();
            prop.setValue(true);
        }

        @Override
        public void disable() {
            super.disable();
            prop.setValue(false);
        }
    }

    /**
     * 模式属性桥接 Setting（下拉式 options）。
     */
    private static final class MyauModeSetting extends SliderSetting {
        private final ModeProperty prop;

        MyauModeSetting(ModeProperty prop) {
            super(prop.getName(), prop.getValue(), prop.getModes());
            this.prop = prop;
        }

        @Override
        public double setValue(double newValue) {
            double r = super.setValue(newValue);
            prop.setValue((int) Math.round(r));
            return r;
        }
    }

    /**
     * 浮点属性桥接 Setting。
     */
    private static final class MyauFloatSlider extends SliderSetting {
        private final FloatProperty prop;

        MyauFloatSlider(FloatProperty prop) {
            super(prop.getName(), prop.getValue().doubleValue(), prop.getMinimum(), prop.getMaximum(),
                    Math.max(0.01, (prop.getMaximum() - prop.getMinimum()) / 100.0));
            this.prop = prop;
        }

        @Override
        public double setValue(double newValue) {
            double r = super.setValue(newValue);
            prop.setValue((float) r);
            return r;
        }
    }

    /**
     * 整型属性桥接 Setting。
     */
    private static final class MyauIntSlider extends SliderSetting {
        private final IntProperty prop;

        MyauIntSlider(IntProperty prop) {
            super(prop.getName(), prop.getValue().doubleValue(), prop.getMinimum(), prop.getMaximum(), 1);
            this.prop = prop;
        }

        @Override
        public double setValue(double newValue) {
            double r = super.setValue(newValue);
            prop.setValue((int) Math.round(r));
            return r;
        }
    }

    /**
     * 百分比属性桥接 Setting。
     */
    private static final class MyauPercentSlider extends SliderSetting {
        private final PercentProperty prop;

        MyauPercentSlider(PercentProperty prop) {
            super(prop.getName(), prop.getValue().doubleValue(), prop.getMinimum(), prop.getMaximum(), 1);
            this.prop = prop;
        }

        @Override
        public double setValue(double newValue) {
            double r = super.setValue(newValue);
            prop.setValue((int) Math.round(r));
            return r;
        }
    }
}
