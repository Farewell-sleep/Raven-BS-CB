package com.alan.clients.util.font;

import lombok.Generated;

public enum FontWeight {
    NONE(0, ""),
    LIGHT(1, "Light", "light", "LIGHT"),
    MEDIUM(2, "Medium", "medium", "MEDIUM"),
    REGULAR(3, "Regular", "regular", "REGULAR"),
    BOLD(4, "Bold", "bold", "BOLD");

    private final int kI;
    private final String[] aliases;
    private static final FontWeight[] $VALUES = dS();

    FontWeight(int var3, String... aliases) {
        this.kI = var3;
        this.aliases = aliases;
    }

    @Generated
    public int dR() {
        return this.kI;
    }

    @Generated
    public String[] getAliases() {
        return this.aliases;
    }

    private static FontWeight[] dS() {
        return new FontWeight[]{NONE, LIGHT, MEDIUM, REGULAR, BOLD};
    }
}
