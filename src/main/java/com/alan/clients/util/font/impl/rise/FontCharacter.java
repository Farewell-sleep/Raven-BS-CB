package com.alan.clients.util.font.impl.rise;

import lombok.Generated;
import net.minecraft.client.renderer.GlStateManager;
import org.lwjgl.opengl.GL11;

public class FontCharacter {
    private final int texture;
    private final float width;
    private final float height;

    public void render(float var1, float var2) {
        GlStateManager.bindTexture(this.texture);
        GL11.glBegin(7);
        GL11.glTexCoord2f(0.0F, 0.0F);
        GL11.glVertex2f(var1, var2);
        GL11.glTexCoord2f(0.0F, 1.0F);
        GL11.glVertex2f(var1, var2 + this.height);
        GL11.glTexCoord2f(1.0F, 1.0F);
        GL11.glVertex2f(var1 + this.width, var2 + this.height);
        GL11.glTexCoord2f(1.0F, 0.0F);
        GL11.glVertex2f(var1 + this.width, var2);
        GL11.glEnd();
    }

    @Generated
    public int getTexture() {
        return this.texture;
    }

    @Generated
    public float getWidth() {
        return this.width;
    }

    @Generated
    public float getHeight() {
        return this.height;
    }

    @Generated
    public FontCharacter(int var1, float var2, float var3) {
        this.texture = var1;
        this.width = var2;
        this.height = var3;
    }
}
