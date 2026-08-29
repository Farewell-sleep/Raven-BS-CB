package com.alan.clients.module.api;

import com.alan.clients.ui.click.standard.screen.Screen;
import com.alan.clients.ui.click.standard.screen.impl.CategoryScreen;
import com.alan.clients.ui.click.standard.screen.impl.ProfileScreen;
import com.alan.clients.ui.click.standard.screen.impl.ScriptScreen;
import com.alan.clients.ui.click.standard.screen.impl.SearchScreen;
import com.alan.clients.util.font.Font;
import com.alan.clients.util.font.FontManager;
import lombok.Generated;

public enum Category {
    SEARCH("category.search", FontManager.ICONS_2.o(17), "U", 1, new SearchScreen()),
    COMBAT("category.combat", FontManager.ICONS_1.o(17), "a", 2, new CategoryScreen()),
    MOVEMENT("category.movement", FontManager.ICONS_1.o(17), "b", 3, new CategoryScreen()),
    PLAYER("category.player", FontManager.ICONS_1.o(17), "c", 4, new CategoryScreen()),
    WORLD("category.world", FontManager.ICONS_1.o(17), "a", 5, new CategoryScreen()),
    RENDER("category.render", FontManager.ICONS_1.o(17), "g", 6, new CategoryScreen()),
    MINIGAMES("category.minigames", FontManager.ICONS_1.o(17), "f", 7, new CategoryScreen()),
    FUN("category.fun", FontManager.ICONS_1.o(17), "d", 8, new CategoryScreen()),
    OTHER("category.other", FontManager.ICONS_1.o(17), "e", 9, new CategoryScreen()),
    CLIENT("category.client", FontManager.ICONS_1.o(17), "d", 10, new CategoryScreen()),
    PROFILES("category.profiles", FontManager.ICONS_1.o(17), "f", 11, new ProfileScreen()),
    SCRIPTS("category.scripts", FontManager.ICONS_2.o(17), "m", 12, new ScriptScreen());

    private final String name;
    private final String icon;
    private final int color;
    private final Font fontRenderer;
    public final Screen clickGUIScreen;
    private static final Category[] $VALUES = createValues();

    Category(String var3, Font var4, String var5, int color, Screen screen) {
        this.name = var3;
        this.icon = var5;
        this.color = color;
        this.clickGUIScreen = screen;
        this.fontRenderer = var4;
    }

    @Generated
    public String getIcon() {
        return this.icon;
    }

    @Generated
    public Font getFontRenderer() {
        return this.fontRenderer;
    }

    @Generated
    public Screen getClickGUIScreen() {
        return this.clickGUIScreen;
    }

    @Generated
    public String getName() {
        return this.name;
    }

    @Generated
    public int getColor() {
        return this.color;
    }

    private static Category[] createValues() {
        return new Category[]{SEARCH, COMBAT, MOVEMENT, PLAYER, WORLD, RENDER, MINIGAMES, FUN, OTHER, CLIENT, PROFILES, SCRIPTS};
    }
}
