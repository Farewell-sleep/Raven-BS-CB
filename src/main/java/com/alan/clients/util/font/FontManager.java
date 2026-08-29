package com.alan.clients.util.font;

import com.alan.clients.util.font.impl.minecraft.MinecraftFont;
import com.alan.clients.util.font.impl.rise.FontRenderer;
import com.alan.clients.util.font.Font;
import com.alan.clients.util.font.impl.rise.FontUtil;
import com.alan.clients.util.font.impl.rise.GlyphCache;
import com.alan.clients.util.font.FontWeight;
import java.io.File;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.function.Supplier;
import lombok.Generated;
import net.minecraft.client.Minecraft;

public enum FontManager {
    MAIN("product_sans_%s", "ttf"),
    MINECRAFT("Minecraft", () -> new MinecraftFont(Minecraft.getMinecraft().fontRendererObj)),
    ICONS_1("Icon-1", "ttf"),
    ICONS_2("Icon-3", "ttf"),
    CUSTOM("", "ttf");

    Supplier<Font> kx;
    Font ky;
    String name;
    final String kz;
    private final HashMap<Integer, FontRenderer> kA = new HashMap<>();
    private static final FontManager[] $VALUES = dQ();

    FontManager(String var3, String var4) {
        this.name = var3;
        this.kz = var4;
    }

    FontManager(String var3, Supplier<Font> supplier) {
        this.name = var3;
        this.kz = "";
        this.ky = (Font)supplier.get();
        this.kx = supplier;
    }

    public Font o(int var1) {
        try {
            return this.a(var1, FontWeight.NONE);
        } catch (RuntimeException | Error throwable) {
            throw throwable;
        } catch (Throwable throwable) {
            throw new RuntimeException(throwable);
        }
    }

    public Font dM() {
        try {
            if (this.kx == null) {
                throw new Exception("Please specify a size, this doesn't have a predefined font");
            }
            return this.a(0, FontWeight.NONE);
        } catch (RuntimeException | Error throwable) {
            throw throwable;
        } catch (Throwable throwable) {
            throw new RuntimeException(throwable);
        }
    }

    public Font a(int var1, FontWeight var2) {
        try {
            if (this.kx != null) {
                if (this.ky == null) {
                    this.ky = this.kx.get();
                }

                return this.ky;
            }
            int i = Integer.parseInt("" + var1 + var2.dR());
            if (!this.kA.containsKey(i)) {
                java.awt.Font font = null;
                String s = "unknown";
                if (this.name.contains(":")) {
                    s = this.name;
                    font = FontUtil.q(s, var1);
                } else {
                    for (String s1 : var2.getAliases()) {
                        s = "rise/font/" + String.format(this.name, s1) + "." + this.kz;
                        font = FontUtil.p(s, var1);
                        if (font != null) {
                            break;
                        }
                    }
                }

                if (font == null) {
                    throw new Exception("Unknown Font " + s);
                }

                FontRenderer agf = new FontRenderer(font, true, true, false);
                if (this == MAIN) {
                    java.awt.Font font1 = b(var1, var2);
                    if (font1 != null) {
                        agf.a(new GlyphCache(font1, true, true));
                    }

                    java.awt.Font font2 = FontUtil.p("rise/font/product_sans_medium.ttf", var1);
                    if (font2 != null) {
                        agf.b(new GlyphCache(font2, true, true));
                    }

                    java.awt.Font font3 = c(var1, var2);
                    if (font3 != null) {
                        agf.c(new GlyphCache(font3, true, true));
                    }

                    java.awt.Font font4 = d(var1, var2);
                    if (font4 != null) {
                        agf.d(new GlyphCache(font4, true, true));
                    }
                }

                this.kA.put(i, agf);
            }

            return this.kA.get(i);
        } catch (RuntimeException | Error throwable) {
            throw throwable;
        } catch (Throwable throwable) {
            throw new RuntimeException(throwable);
        }
    }

    private static java.awt.Font b(int var0, FontWeight var1) {
        String s;
        switch (var1) {
            case LIGHT:
                s = "Light";
                break;
            case MEDIUM:
                s = "Medium";
                break;
            case BOLD:
                s = "Bold";
                break;
            default:
                s = "Regular";
        }
        java.awt.Font font = FontUtil.p("rise/font/HarmonyOS_Sans_SC_" + s + ".ttf", var0);
        if (font != null) {
            return font;
        }

        String s1 = System.getProperty("rise.harmonyos_sans_sc_dir");
        return s1 != null && !s1.trim().isEmpty() ? FontUtil.q(s1 + File.separator + "HarmonyOS_Sans_SC_" + s + ".ttf", var0) : null;
    }

    private static java.awt.Font c(int var0, FontWeight var1) {
        String f;
        switch (var1) {
            case LIGHT:
                f = "LINESeedJP_TTF_Th.ttf";
                break;
            case BOLD:
                f = "LINESeedJP_TTF_Bd.ttf";
                break;
            default:
                f = "LINESeedJP_TTF_Rg.ttf";
        }
        return FontUtil.p("rise/font/" + f, var0);
    }

    private static java.awt.Font d(int var0, FontWeight var1) {
        String f;
        switch (var1) {
            case LIGHT:
                f = "LINESeedKR-Th.ttf";
                break;
            case BOLD:
                f = "LINESeedKR-Bd.ttf";
                break;
            default:
                f = "LINESeedKR-Rg.ttf";
        }
        return FontUtil.p("rise/font/" + f, var0);
    }

    public static ArrayList<String> dN() {
        ArrayList arraylist = new ArrayList();
        a(arraylist, "/System/Library/Fonts");
        a(arraylist, "/Library/Fonts");
        a(arraylist, System.getProperty("user.home") + "/Library/Fonts");
        a(arraylist, "C:\\Windows\\Fonts");
        a(arraylist, System.getProperty("user.home") + "\\AppData\\Local\\Microsoft\\Windows\\Fonts");
        a(arraylist, "/usr/share/fonts");
        a(arraylist, "/usr/local/share/fonts");
        a(arraylist, System.getProperty("user.home") + "/.fonts");
        a(arraylist, System.getProperty("user.home") + "/.local/share/fonts");
        return arraylist;
    }

    private static void a(ArrayList<String> var0, String var1) {
        File file1 = new File(var1);
        if (file1.exists() && file1.isDirectory()) {
            File[] afile = file1.listFiles();
            if (afile != null) {
                for (File file2 : afile) {
                    if (file2.isFile() && file2.getName().toLowerCase().endsWith(".ttf")) {
                        var0.add(file2.getAbsolutePath());
                    } else if (file2.isDirectory()) {
                        a(var0, file2.getAbsolutePath());
                    }
                }
            }
        }
    }

    @Generated
    public void setName(String name) {
        this.name = name;
    }

    @Generated
    public String getName() {
        return this.name;
    }

    @Generated
    public HashMap<Integer, FontRenderer> dO() {
        return this.kA;
    }

    private static FontManager[] dQ() {
        return new FontManager[]{MAIN, MINECRAFT, ICONS_1, ICONS_2, CUSTOM};
    }
}
