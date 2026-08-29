package com.alan.clients.util.gui.textbox;

import com.alan.clients.util.font.FontManager;
import com.alan.clients.util.gui.GUIUtil;
import com.alan.clients.util.vector.Vector2d;
import com.alan.clients.util.font.Font;
import com.alan.clients.util.gui.textbox.TextAlign;
import com.alan.clients.util.font.FontWeight;
import java.awt.Color;
import java.awt.Toolkit;
import java.awt.datatransfer.DataFlavor;
import java.awt.datatransfer.StringSelection;
import java.awt.datatransfer.UnsupportedFlavorException;
import java.io.IOException;
import lombok.Generated;
import net.minecraft.client.Minecraft;
import net.minecraft.util.ChatAllowedCharacters;
import org.lwjgl.input.Keyboard;
import rip.vantage.commons.util.time.StopWatch;

public class TextBox {
    public String text = "";
    public boolean selected;
    public int aJk;
    public Vector2d position;
    public float width;
    public Font lq;
    public Color color;
    public TextAlign textAlign;
    public String aJm;
    public boolean hideCharacters;
    private int aJo;
    private int aJp = 32767;
    private String aJq = tN();
    private double posX;
    private double aJr;
    private double aJs;
    private float aJt;
    private float aJu;
    public StopWatch aJv = new StopWatch();

    public TextBox(Vector2d position, Font var2, Color color, TextAlign var4, String var5, float width, boolean var7) {
        this.position = position;
        this.lq = var2;
        this.color = color;
        this.textAlign = var4;
        this.aJm = var5;
        this.width = width;
        this.hideCharacters = var7;
    }

    public TextBox(Vector2d vector2d, Font var2, Color var3, TextAlign var4, String var5, float var6) {
        this(vector2d, var2, var3, var4, var5, var6, false);
    }

    public TextBox(Vector2d vector2d, Font var2, Color var3, TextAlign var4, String var5, float var6, String var7) {
        this(vector2d, var2, var3, var4, var5, var6, false);
        this.aJq = var7;
    }

    public void draw() {
        this.aJv.aX();
        Keyboard.enableRepeatEvents(true);
        this.aJk = Math.max(0, Math.min(this.aJk, this.text.length()));
        this.aJu = (float)this.position.y;
        boolean flag = this.isEmpty();
        String s = flag ? this.aJm : (this.hideCharacters ? this.repeatChar('*', this.text.length()) : this.text);
        String s1;
        if (flag) {
            this.aJo = 0;
            s1 = s;
        } else {
            this.aJo = this.bU(s);
            s1 = s.substring(this.aJo);

            while (this.lq.getStringWidth(s1) > this.width && !s1.isEmpty()) {
                s1 = s1.substring(0, s1.length() - 1);
            }
        }

        float f = this.lq.getStringWidth(s1);
        double now = System.currentTimeMillis();
        double d1 = Math.min(Math.abs(now - this.aJs), 500.0);
        this.aJs = now;
        switch (this.textAlign) {
            case CENTER:

                for (int i = 0; i < d1; i++) {
                    this.posX = (this.posX * 19.0 + (this.position.x - f / 2.0F)) / 20.0;
                }
                break;
            case LEFT:
            default:
                this.posX = this.position.x;
        }

        if (flag) {
            this.lq
                .a(
                    s1,
                    this.posX,
                    this.position.y,
                    new Color(this.color.getRed(), this.color.getBlue(), this.color.getGreen(), (int)(this.color.getAlpha() * (this.selected ? 0.3F : 0.2F))).hashCode()
                );
        } else {
            this.lq.a(s1, this.posX, this.position.y, this.color.hashCode());
        }

        if (this.selected) {
            String s2 = this.c(s, this.aJo, this.aJk);
            float f1 = this.lq.getStringWidth(s2);
            this.aJt = (float)(this.posX + f1);

            for (int j = 0; j < d1; j++) {
                this.aJr = (this.aJr * 19.0 + (f1 - 2.0F)) / 20.0;
            }

            this.lq
                .a(
                    "|",
                    (float)(this.posX + this.aJr + 1.0),
                    this.position.y - 1.0,
                    new Color(
                            this.color.getRed(),
                            this.color.getBlue(),
                            this.color.getGreen(),
                            this.color.getAlpha() == 0 ? 0 : (int)((Math.sin(System.currentTimeMillis() / 150.0) + 1.0) / 2.0 * 255.0)
                        )
                        .hashCode()
                );
        }
    }

    public float tL() {
        return this.aJt;
    }

    public float tM() {
        return this.aJu;
    }

    private int bU(String var1) {
        if (this.lq.getStringWidth(var1) <= this.width) {
            return 0;
        }

        int i = this.aJk;

        while (i > 0 && this.lq.getStringWidth(var1.substring(i - 1, this.aJk)) < this.width) {
            i--;
        }

        return i;
    }

    private String c(String var1, int var2, int var3) {
        int i = Math.max(0, Math.min(var2, var1.length()));
        int j = Math.max(i, Math.min(var3, var1.length()));
        return var1.substring(i, j);
    }

    public void click(int var1, int var2, int var3) {
        Vector2d vector2d = this.getPosition();
        boolean flag = GUIUtil.c(vector2d.x + (this.textAlign == TextAlign.CENTER ? -this.width / 2.0F : 0.0F), vector2d.y, this.width, this.lq.height(), var1, var2);
        this.selected = var3 == 0 && flag;
        if (this.selected) {
            this.aJk = this.text.length();
        }
    }

    public void key(char var1, int var2) {
        if (this.selected && var2 != 28) {
            this.aJk = Math.max(0, Math.min(this.aJk, this.text.length()));
            String s = String.valueOf(var1);
            boolean flag = Keyboard.isKeyDown(29) || Keyboard.isKeyDown(157) || Keyboard.isKeyDown(219) || Keyboard.isKeyDown(220);
            if (var2 != 208 && var2 != 200) {
                if (flag && var2 == 47) {
                    try {
                        String s1 = (String)Toolkit.getDefaultToolkit().getSystemClipboard().getData(DataFlavor.stringFlavor);
                        int i = this.text.length();
                        this.addText(s1, this.aJk);
                        this.aJk = this.aJk + (this.text.length() - i);
                    } catch (UnsupportedFlavorException | IOException unsupportedflavorexception) {
                    }
                } else if (flag && var2 == 46) {
                    Toolkit.getDefaultToolkit().getSystemClipboard().setContents(new StringSelection(this.text), null);
                } else if (var2 == 211 && !this.text.isEmpty()) {
                    this.aq(this.aJk + 1);
                } else if (var2 == 14 && !this.text.isEmpty()) {
                    this.aq(this.aJk);
                    this.aJk--;
                    if (flag) {
                        while (this.aJk > 0) {
                            this.aq(this.aJk);
                            this.aJk--;
                        }
                    }
                } else if (var2 == 205) {
                    this.aJk++;
                    if (flag) {
                        this.aJk = this.text.length();
                    }
                } else if (var2 == 203) {
                    this.aJk--;
                    if (flag) {
                        this.aJk = 0;
                    }
                } else {
                    if (this.m(var1)) {
                        int j = this.text.length();
                        this.addText(s, this.aJk);
                        if (this.text.length() > j) {
                            this.aJk++;
                        }
                    }

                    this.aJk = Math.max(0, Math.min(this.aJk, this.text.length()));
                }
            }
        }
    }

    public int bV(String var1) {
        if (var1 != null && !var1.isEmpty()) {
            this.aJk = Math.max(0, Math.min(this.aJk, this.text.length()));
            int i = this.text.length();
            this.addText(var1, this.aJk);
            int j = this.text.length() - i;
            if (j > 0) {
                this.aJk = Math.max(0, Math.min(this.aJk + j, this.text.length()));
            }

            return j;
        }
        return 0;
    }

    private boolean m(char var1) {
        if (var1 == 0) {
            return false;
        } else if (var1 == '\n' || var1 == '\r' || var1 == '\t') {
            return false;
        }
        return this.aJq != null && !this.aJq.isEmpty() ? this.aJq.indexOf(var1) >= 0 || this.aJq.indexOf(Character.toLowerCase(var1)) >= 0 : true;
    }

    private void addText(String var1, int var2) {
        if (var1 != null && !var1.isEmpty()) {
            StringBuilder stringbuilder = new StringBuilder(var1.length());

            for (int i = 0; i < var1.length(); i++) {
                char c0 = var1.charAt(i);
                if (this.m(c0)) {
                    stringbuilder.append(c0);
                }
            }

            if (stringbuilder.length() != 0) {
                if (this.text.length() + stringbuilder.length() <= this.aJp) {
                    int j = Math.max(0, Math.min(var2, this.text.length()));
                    StringBuilder stringbuilder1 = new StringBuilder(this.text);
                    stringbuilder1.insert(j, stringbuilder);
                    this.text = stringbuilder1.toString();
                }
            }
        }
    }

    private void aq(int var1) {
        if (var1 > 0 && var1 <= this.text.length()) {
            this.text = new StringBuilder(this.text).deleteCharAt(var1 - 1).toString();
        }
    }

    public boolean isEmpty() {
        return this.text.trim().isEmpty();
    }

    private static String tN() {
        StringBuilder stringbuilder = new StringBuilder(4096);

        for (char c0 = 0; c0 < '\uffff'; c0++) {
            if (ChatAllowedCharacters.isAllowedCharacter(c0) || c0 == ' ') {
                stringbuilder.append(c0);
            }
        }

        return stringbuilder.toString();
    }

    @Generated
    public String getText() {
        return this.text;
    }

    @Generated
    public boolean isSelected() {
        return this.selected;
    }

    @Generated
    public int tP() {
        return this.aJk;
    }

    @Generated
    public Vector2d getPosition() {
        return this.position;
    }

    @Generated
    public float getWidth() {
        return this.width;
    }

    @Generated
    public Font eb() {
        return this.lq;
    }

    @Generated
    public Color getColor() {
        return this.color;
    }

    @Generated
    public TextAlign getTextAlign() {
        return this.textAlign;
    }

    @Generated
    public String tR() {
        return this.aJm;
    }

    @Generated
    public boolean isHideCharacters() {
        return this.hideCharacters;
    }

    @Generated
    public int tT() {
        return this.aJo;
    }

    @Generated
    public int tU() {
        return this.aJp;
    }

    @Generated
    public String tV() {
        return this.aJq;
    }

    @Generated
    public double getPosX() {
        return this.posX;
    }

    @Generated
    public double tW() {
        return this.aJr;
    }

    @Generated
    public double tX() {
        return this.aJs;
    }

    @Generated
    public StopWatch tY() {
        return this.aJv;
    }

    @Generated
    public void bW(String var1) {
        this.text = var1;
    }

    @Generated
    public void setSelected(boolean var1) {
        this.selected = var1;
    }

    @Generated
    public void ar(int var1) {
        this.aJk = var1;
    }

    @Generated
    public void setPosition(Vector2d var1) {
        this.position = var1;
    }

    @Generated
    public void setWidth(float width) {
        this.width = width;
    }

    @Generated
    public void c(Font var1) {
        this.lq = var1;
    }

    @Generated
    public void setColor(Color color) {
        this.color = color;
    }

    @Generated
    public void setTextAlign(TextAlign var1) {
        this.textAlign = var1;
    }

    @Generated
    public void bX(String var1) {
        this.aJm = var1;
    }

    @Generated
    public void setHideCharacters(boolean var1) {
        this.hideCharacters = var1;
    }

    @Generated
    public void as(int var1) {
        this.aJo = var1;
    }

    @Generated
    public void at(int var1) {
        this.aJp = var1;
    }

    @Generated
    public void bY(String var1) {
        this.aJq = var1;
    }

    @Generated
    public void setPosX(double var1) {
        this.posX = var1;
    }

    @Generated
    public void W(double var1) {
        this.aJr = var1;
    }

    @Generated
    public void X(double var1) {
        this.aJs = var1;
    }

    @Generated
    public void A(float var1) {
        this.aJt = var1;
    }

    @Generated
    public void B(float var1) {
        this.aJu = var1;
    }

    @Generated
    public void e(StopWatch var1) {
        this.aJv = var1;
    }
    private static String repeatChar(char c, int count) {
        StringBuilder stringbuilder = new StringBuilder(count);
        for (int i = 0; i < count; i++) {
            stringbuilder.append(c);
        }
        return stringbuilder.toString();
    }

}
