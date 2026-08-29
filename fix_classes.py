# -*- coding: utf-8 -*-
import os
import io

BASE = r"C:\Games\raven-bs\src\main\java\com\alan\clients"

# ============ 1. MCPCompat 反射工具 ============
mcpcompat = '''package com.alan.clients.util;

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
'''
with io.open(os.path.join(BASE, "util", "MCPCompat.java"), "w", encoding="utf-8", newline="\n") as f:
    f.write(mcpcompat)
print("wrote MCPCompat.java")

# ============ 2. MinecraftFont 适配类 ============
mc_font = '''package com.alan.clients.util.font.impl.minecraft;

import com.alan.clients.util.font.Font;
import net.minecraft.client.gui.FontRenderer;

import java.awt.Color;

/**
 * 把 MC 原版 FontRenderer 适配为 Rise 的 Font 接口（供 FontManager.MINECRAFT 使用）。
 */
public class MinecraftFont extends Font {
    private final FontRenderer fontRenderer;

    public MinecraftFont(FontRenderer fontRenderer) {
        this.fontRenderer = fontRenderer;
    }

    @Override
    public int b(String var1, double var2, double var4, int var6, boolean var7) {
        return this.fontRenderer.drawString(var1, (float) var2, (float) var4, var6, var7);
    }

    @Override
    public int a(String var1, double var2, double var4, int var6) {
        return this.fontRenderer.drawString(var1, (float) var2, (float) var4, var6, false);
    }

    @Override
    public int b(String var1, double var2, double var4, int var6) {
        return this.fontRenderer.drawString(var1, (float) var2, (float) var4, var6, false);
    }

    @Override
    public int getStringWidth(String var1) {
        return this.fontRenderer.getStringWidth(var1);
    }

    @Override
    public int drawString(String var1, double var2, double var4, int var6) {
        return this.fontRenderer.drawString(var1, (float) var2, (float) var4, var6, false);
    }

    @Override
    public int drawCenteredString(String var1, double var2, double var4, int var6) {
        return this.fontRenderer.drawString(var1, (float) (var2 - this.fontRenderer.getStringWidth(var1) / 2.0), (float) var4, var6, false);
    }

    @Override
    public float height() {
        return (float) this.fontRenderer.FONT_HEIGHT;
    }

    @Override
    public void a(char var1, int var2, int var3, Color color) {
        this.fontRenderer.drawString(String.valueOf(var1), (float) var2, (float) var3, color.getRGB(), false);
    }
}
'''
mcf = os.path.join(BASE, "util", "font", "impl", "minecraft", "MinecraftFont.java")
os.makedirs(os.path.dirname(mcf), exist_ok=True)
with io.open(mcf, "w", encoding="utf-8", newline="\n") as f:
    f.write(mc_font)
print("wrote MinecraftFont.java")
print("DONE")
