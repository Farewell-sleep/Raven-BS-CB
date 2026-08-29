package myau.module.modules;

import myau.module.Module;

/**
 * TargetStrafe 存根 —— MoveUtil.adjustYaw 依赖其 isEnabled/getTargetYaw。
 */
public class TargetStrafe extends Module {
    public TargetStrafe() {
        super("TargetStrafe", false);
    }

    public float getTargetYaw() {
        return Float.NaN;
    }
}
