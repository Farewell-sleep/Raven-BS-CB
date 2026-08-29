package com.alan.clients.util.font;

import java.awt.Color;

public abstract class Font {
    public Font() {
    }

    public abstract int b(String var1, double var2, double var4, int var6, boolean var7);

    public abstract int a(String var1, double var2, double var4, int var6);

    public abstract int b(String var1, double var2, double var4, int var6);

    public abstract int getStringWidth(String var1);

    public abstract int drawString(String var1, double var2, double var4, int var6);

    public abstract int drawCenteredString(String var1, double var2, double var4, int var6);

    public abstract float height();

    public abstract void a(char var1, int var2, int var3, Color color);
}
