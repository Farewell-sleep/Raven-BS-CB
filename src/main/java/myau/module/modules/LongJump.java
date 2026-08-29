package myau.module.modules;

import myau.module.Module;

/**
 * LongJump 存根 —— Scaffold.canPlace 依赖其 isEnabled/isAutoMode/isJumping。
 */
public class LongJump extends Module {
    public LongJump() {
        super("LongJump", false);
    }

    public boolean isAutoMode() {
        return false;
    }

    public boolean isJumping() {
        return false;
    }
}
