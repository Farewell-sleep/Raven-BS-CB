package myau.module.modules;

import myau.module.Module;

/**
 * BedNuker 存根 —— Scaffold.canPlace 依赖其 isEnabled/isReady。
 */
public class BedNuker extends Module {
    public BedNuker() {
        super("BedNuker", false);
    }

    public boolean isReady() {
        return false;
    }
}
