package com.alan.clients.util.localization;

import java.util.HashMap;
import lombok.Generated;

public enum Locale {
    EN_US("en_US"),
    FR_FR("fr_FR"),
    ES_ES("es_ES"),
    SV_SE("sv_SE"),
    PL_PL("pl_PL"),
    RU_RU("ru_RU"),
    ZH_ZH("zh_ZH");

    private final String file;
    private final HashMap<String, String> strings = new HashMap<>();
    private static final Locale[] $VALUES = uG();

    @Generated
    public String getFile() {
        return this.file;
    }

    @Generated
    public HashMap<String, String> getStrings() {
        return this.strings;
    }

    @Generated
    Locale(String file) {
        this.file = file;
    }

    private static Locale[] uG() {
        return new Locale[]{EN_US, FR_FR, ES_ES, SV_SE, PL_PL, RU_RU, ZH_ZH};
    }
}
