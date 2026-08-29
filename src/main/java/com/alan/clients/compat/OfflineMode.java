package com.alan.clients.compat;

//add code
public final class OfflineMode {

    private static final boolean DEFAULT_OFFLINE = true;

    private static final boolean OFFLINE = !"false".equalsIgnoreCase(
            System.getProperty("rise.net.offline", Boolean.toString(DEFAULT_OFFLINE)));

    private OfflineMode() {
    }

    public static boolean offline() {
        return OFFLINE;
    }
}
