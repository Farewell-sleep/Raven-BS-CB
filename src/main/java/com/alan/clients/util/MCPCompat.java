package com.alan.clients.util;

import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.GuiIngame;
import net.minecraft.client.gui.ScaledResolution;
import net.minecraft.client.renderer.entity.RenderManager;
import net.minecraft.entity.EntityLivingBase;

import java.lang.reflect.Field;
import java.lang.reflect.Method;

/**
 * MCP 映射兼容层：Rise 源码中使用的官方混淆字段/方法名在 BS(MCP named) 环境下
 * 不存在或访问级别不同，统一通过反射适配。
 */
public final class MCPCompat {
    private static Field timerField;
    private static Field renderPartialTicksField;
    private static Field renderPosXField;
    private static Field renderPosYField;
    private static Field renderPosZField;
    private static Method armSwingMethod;
    private static Method showCrosshairMethod;

    static {
        try {
            timerField = Minecraft.class.getDeclaredField("timer");
            timerField.setAccessible(true);
            renderPartialTicksField = Class.forName("net.minecraft.util.Timer").getDeclaredField("renderPartialTicks");
            renderPartialTicksField.setAccessible(true);
            renderPosXField = RenderManager.class.getDeclaredField("renderPosX");
            renderPosXField.setAccessible(true);
            renderPosYField = RenderManager.class.getDeclaredField("renderPosY");
            renderPosYField.setAccessible(true);
            renderPosZField = RenderManager.class.getDeclaredField("renderPosZ");
            renderPosZField.setAccessible(true);
            armSwingMethod = EntityLivingBase.class.getDeclaredMethod("getArmSwingAnimationEnd");
            armSwingMethod.setAccessible(true);
            showCrosshairMethod = GuiIngame.class.getDeclaredMethod("showCrosshair");
            showCrosshairMethod.setAccessible(true);
        } catch (Throwable ignored) {
        }
    }

    private MCPCompat() {
    }

    public static ScaledResolution scaledResolution() {
        return new ScaledResolution(Minecraft.getMinecraft());
    }

    public static float renderPartialTicks() {
        try {
            Object timer = timerField.get(Minecraft.getMinecraft());
            if (timer != null) return renderPartialTicksField.getFloat(timer);
        } catch (Throwable ignored) {
        }
        return 0.0F;
    }

    public static double renderPosX() {
        try {
            return renderPosXField.getDouble(Minecraft.getMinecraft().getRenderManager());
        } catch (Throwable ignored) {
            return 0.0;
        }
    }

    public static double renderPosY() {
        try {
            return renderPosYField.getDouble(Minecraft.getMinecraft().getRenderManager());
        } catch (Throwable ignored) {
            return 0.0;
        }
    }

    public static double renderPosZ() {
        try {
            return renderPosZField.getDouble(Minecraft.getMinecraft().getRenderManager());
        } catch (Throwable ignored) {
            return 0.0;
        }
    }

    public static int armSwingAnimationEnd(EntityLivingBase entity) {
        try {
            return (Integer) armSwingMethod.invoke(entity);
        } catch (Throwable ignored) {
            return 6;
        }
    }

    public static boolean showCrosshair() {
        try {
            return (Boolean) showCrosshairMethod.invoke(Minecraft.getMinecraft().ingameGUI);
        } catch (Throwable ignored) {
            return false;
        }
    }
}
