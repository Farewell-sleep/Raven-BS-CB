package com.alan.clients.util.font.impl.minecraft;

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
