package com.alan.clients.ui.theme;

import com.alan.clients.Client;
import com.alan.clients.module.impl.render.Interface;
import com.alan.clients.util.vector.Vector2d;
import com.alan.clients.ui.theme.KeyColors;
import com.alan.clients.util.render.ColorUtil;
import java.awt.Color;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.function.Function;
import lombok.Generated;
import net.minecraft.util.EnumChatFormatting;

public enum Themes {
    AUBERGINE("Aubergine", new Color(170, 7, 107), new Color(97, 4, 95), EnumChatFormatting.DARK_PURPLE, KeyColors.PURPLE, KeyColors.RED),
    AQUA("Aqua", new Color(185, 250, 255), new Color(79, 199, 200), EnumChatFormatting.AQUA, KeyColors.AQUA),
    BANANA("Banana", new Color(253, 236, 177), new Color(255, 255, 255), EnumChatFormatting.YELLOW, KeyColors.YELLOW),
    BLEND("Blend", new Color(71, 148, 253), new Color(71, 253, 160), EnumChatFormatting.AQUA, KeyColors.AQUA, KeyColors.LIME),
    BLOSSOM("Blossom", new Color(226, 208, 249), new Color(49, 119, 115), EnumChatFormatting.DARK_AQUA, KeyColors.PINK, KeyColors.GRAY),
    BUBBLEGUM("Bubblegum", new Color(243, 145, 216), new Color(152, 165, 243), EnumChatFormatting.LIGHT_PURPLE, KeyColors.PINK, KeyColors.PURPLE),
    CANDY_CANE("Candy Cane", new Color(255, 0, 0), new Color(255, 255, 255), EnumChatFormatting.RED, KeyColors.RED),
    CHERRY("Cherry", new Color(187, 55, 125), new Color(251, 211, 233), EnumChatFormatting.RED, KeyColors.RED, KeyColors.PURPLE, KeyColors.PINK),
    CHRISTMAS("Christmas", new Color(255, 64, 64), new Color(255, 255, 255), new Color(64, 255, 64), EnumChatFormatting.RED, KeyColors.RED, KeyColors.LIME),
    CORAL("Coral", new Color(244, 168, 150), new Color(52, 133, 151), EnumChatFormatting.DARK_AQUA, KeyColors.PINK, KeyColors.ORANGE, KeyColors.DARK_BLUE),
    DIGITAL_HORIZON("Digital Horizon", new Color(95, 195, 228), new Color(229, 93, 135), EnumChatFormatting.AQUA, KeyColors.AQUA, KeyColors.RED, KeyColors.PINK),
    EXPRESS("Express", new Color(173, 83, 137), new Color(60, 16, 83), EnumChatFormatting.DARK_PURPLE, KeyColors.PURPLE, KeyColors.PINK),
    LIME_WATER("Lime Water", new Color(18, 255, 247), new Color(179, 255, 171), EnumChatFormatting.AQUA, KeyColors.AQUA, KeyColors.LIME),
    LUSH("Lush", new Color(168, 224, 99), new Color(86, 171, 47), EnumChatFormatting.GREEN, KeyColors.LIME, KeyColors.DARK_GREEN),
    HALOGEN("Halogen", new Color(255, 65, 108), new Color(255, 75, 43), EnumChatFormatting.RED, KeyColors.RED, KeyColors.ORANGE),
    HYPER("Hyper", new Color(236, 110, 173), new Color(52, 148, 230), EnumChatFormatting.LIGHT_PURPLE, KeyColors.PINK, KeyColors.DARK_BLUE, KeyColors.AQUA),
    MAGIC("Magic", new Color(74, 0, 224), new Color(142, 45, 226), EnumChatFormatting.BLUE, KeyColors.DARK_BLUE, KeyColors.PURPLE),
    MAY("May", new Color(238, 79, 238), new Color(253, 219, 245), EnumChatFormatting.LIGHT_PURPLE, KeyColors.PINK, KeyColors.PURPLE),
    ORANGE_JUICE("Orange Juice", new Color(252, 74, 26), new Color(247, 183, 51), EnumChatFormatting.GOLD, KeyColors.ORANGE, KeyColors.YELLOW),
    PASTEL("Pastel", new Color(243, 155, 178), new Color(207, 196, 243), EnumChatFormatting.LIGHT_PURPLE, KeyColors.PINK),
    PUMPKIN("Pumpkin", new Color(241, 166, 98), new Color(255, 216, 169), new Color(227, 139, 42), EnumChatFormatting.GOLD, KeyColors.ORANGE),
    SATIN("Satin", new Color(215, 60, 67), new Color(140, 23, 39), EnumChatFormatting.RED, KeyColors.RED),
    SNOWY_SKY("Snowy Sky", new Color(1, 171, 179), new Color(234, 234, 234), new Color(18, 232, 232), EnumChatFormatting.AQUA, KeyColors.AQUA, KeyColors.GRAY),
    STEEL_FADE("Steel Fade", new Color(66, 134, 244), new Color(55, 59, 68), EnumChatFormatting.BLUE, KeyColors.DARK_BLUE, KeyColors.GRAY),
    SUNDAE("Sundae", new Color(206, 74, 126), new Color(122, 44, 77), EnumChatFormatting.RED, KeyColors.PINK, KeyColors.PURPLE, KeyColors.RED),
    SUNKIST("Sunkist", new Color(242, 201, 76), new Color(242, 153, 74), EnumChatFormatting.YELLOW, KeyColors.YELLOW, KeyColors.ORANGE),
    WATER("Water", new Color(12, 232, 199), new Color(12, 163, 232), EnumChatFormatting.AQUA, KeyColors.AQUA, KeyColors.DARK_BLUE),
    LEGACY("Legacy", new Color(7393023), new Color(7393023), EnumChatFormatting.AQUA, KeyColors.AQUA, KeyColors.DARK_BLUE),
    WINTER("Winter", Color.WHITE, Color.WHITE, EnumChatFormatting.GRAY, KeyColors.GRAY, KeyColors.GRAY),
    PEONY("Peony", new Color(226, 208, 249), new Color(207, 171, 255), EnumChatFormatting.DARK_AQUA, KeyColors.PINK, KeyColors.GRAY),
    SHADOW("Shadow", new Color(97, 131, 255), new Color(206, 212, 255), EnumChatFormatting.AQUA, KeyColors.AQUA),
    WOOD("Wood", new Color(79, 109, 81), new Color(170, 139, 87), new Color(240, 235, 206), EnumChatFormatting.DARK_GREEN, KeyColors.DARK_GREEN),
    CREIDA("Creida", new Color(-11644304).brighter().brighter(), new Color(-11644304).darker(), EnumChatFormatting.RESET, KeyColors.GRAY),
    CREIDA_TWO("Creida Two", new Color(-6632725), new Color(-8406042).darker(), EnumChatFormatting.RESET, KeyColors.GRAY),
    GOTHIC("Gothic", new Color(31, 30, 30), new Color(196, 190, 190), EnumChatFormatting.RESET, KeyColors.GRAY),
    SEN("Rue", new Color(234, 118, 176), new Color(31, 30, 30), EnumChatFormatting.DARK_PURPLE, KeyColors.PINK),
    PURPLE("Purple", new Color(5391249), new Color(5391249).brighter(), EnumChatFormatting.RESET),
    TEST("Rainbow", var0 -> ColorUtil.aB((int)((var0.getX() + var0.getY()) * 10.0)), EnumChatFormatting.RED),
    NORD("Nord", new Color(143, 188, 187), new Color(163, 190, 140), new Color(236, 239, 244), EnumChatFormatting.AQUA, KeyColors.AQUA, KeyColors.GRAY);

    private final String themeName;
    private Color aDL = null;
    private Color aDM = null;
    private Color aDN = null;
    private Function<Vector2d, Color> aDO;
    private final EnumChatFormatting chatAccentColor;
    private final ArrayList<KeyColors> keyColors;
    private final boolean triColor;
    static Color aDS = new Color(0, 0, 0, 110);
    private static final Themes[] $VALUES = rL();

    Themes(String themeName, Color color, Color var5, EnumChatFormatting chatAccentColor, KeyColors... var7) {
        this.themeName = themeName;
        this.aDL = this.aDN = color;
        this.aDM = var5;
        this.chatAccentColor = chatAccentColor;
        this.keyColors = new ArrayList<>(Arrays.asList(var7));
        this.triColor = false;
    }

    Themes(String themeName, Color color, Color var5, Color var6, EnumChatFormatting chatAccentColor, KeyColors... var8) {
        this.themeName = themeName;
        this.aDL = color;
        this.aDM = var5;
        this.aDN = var6;
        this.chatAccentColor = chatAccentColor;
        this.keyColors = new ArrayList<>(Arrays.asList(var8));
        this.triColor = true;
    }

    Themes(String themeName, Function<Vector2d, Color> function, EnumChatFormatting chatAccentColor, KeyColors... var6) {
        this.themeName = themeName;
        this.aDO = function;
        this.chatAccentColor = chatAccentColor;
        this.keyColors = new ArrayList<>(Arrays.asList(var6));
        this.triColor = true;
    }

    public Color rA() {
        return this.aDO == null ? this.aDL : this.getAccentColor(new Vector2d(0.0, 0.0));
    }

    public Color rB() {
        return this.aDO == null ? this.aDM : this.getAccentColor(new Vector2d(0.0, 50.0));
    }

    public Color rC() {
        return this.aDO == null ? this.aDN : this.getAccentColor(new Vector2d(0.0, 100.0));
    }

    public Color getAccentColor(Vector2d vector2d) {
        if (this.aDO != null) {
            return this.aDO.apply(vector2d);
        } else if (this.triColor) {
            double d0 = this.getBlendFactor(vector2d);
            return d0 <= 0.5 ? ColorUtil.a(this.rB(), this.rA(), d0 * 2.0) : ColorUtil.a(this.rC(), this.rB(), (d0 - 0.5) * 2.0);
        }
        return ColorUtil.a(this.rA(), this.rB(), this.getBlendFactor(vector2d));
    }

    public Color rD() {
        return this.getAccentColor(new Vector2d(0.0, 0.0));
    }

    @Deprecated
    public int getRound() {
        try {
            Interface interfaceModule = Client.a.g().c(Interface.class);
            if (interfaceModule != null) {
                return (int)interfaceModule.getRoundingRadius();
            }
        } catch (Exception exception) {
        }

        return 4;
    }

    public float qd() {
        return 4.5F;
    }

    public Color rE() {
        return new Color(0, 0, 0, 190);
    }

    public double getBlendFactor(Vector2d vector2d) {
        return Math.sin(System.currentTimeMillis() / 600.0 + vector2d.getX() * 0.005 + vector2d.getY() * 0.06) * 0.5 + 0.5;
    }

    @Generated
    public String getThemeName() {
        return this.themeName;
    }

    @Generated
    public Function<Vector2d, Color> getFunction() {
        return this.aDO;
    }

    @Generated
    public EnumChatFormatting getChatAccentColor() {
        return this.chatAccentColor;
    }

    @Generated
    public ArrayList<KeyColors> getKeyColors() {
        return this.keyColors;
    }

    @Generated
    public boolean isTriColor() {
        return this.triColor;
    }

    @Generated
    public static Color rK() {
        return aDS;
    }

    @Generated
    public static void c(Color color) {
        aDS = color;
    }

    private static Themes[] rL() {
        return new Themes[]{
            AUBERGINE,
            AQUA,
            BANANA,
            BLEND,
            BLOSSOM,
            BUBBLEGUM,
            CANDY_CANE,
            CHERRY,
            CHRISTMAS,
            CORAL,
            DIGITAL_HORIZON,
            EXPRESS,
            LIME_WATER,
            LUSH,
            HALOGEN,
            HYPER,
            MAGIC,
            MAY,
            ORANGE_JUICE,
            PASTEL,
            PUMPKIN,
            SATIN,
            SNOWY_SKY,
            STEEL_FADE,
            SUNDAE,
            SUNKIST,
            WATER,
            LEGACY,
            WINTER,
            PEONY,
            SHADOW,
            WOOD,
            CREIDA,
            CREIDA_TWO,
            GOTHIC,
            SEN,
            PURPLE,
            TEST,
            NORD
        };
    }
}
