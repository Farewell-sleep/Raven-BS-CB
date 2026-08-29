package com.alan.clients.ui.theme;

import com.alan.clients.ui.theme.Themes;
import lombok.Generated;

public final class ThemeManager {
    private Themes theme = Themes.BLEND;

    public ThemeManager() {
    }

    @Generated
    public void a(Themes themes) {
        this.theme = themes;
    }

    @Generated
    public Themes getTheme() {
        return this.theme;
    }
}
