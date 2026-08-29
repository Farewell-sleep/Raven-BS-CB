package myau.module.modules;

import myau.module.Module;

public class AutoHeal extends Module {
    public AutoHeal() {
        super("AutoHeal", false);
    }

    public boolean isSwitching() {
        return false;
    }
}
