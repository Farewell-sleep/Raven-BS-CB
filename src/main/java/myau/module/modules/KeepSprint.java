package myau.module.modules;

import myau.module.Module;
import myau.property.properties.BooleanProperty;
import myau.property.properties.PercentProperty;

/**
 * KeepSprint 存根 —— PlayerUtil.attackEntity 依赖。
 */
public class KeepSprint extends Module {
    public final BooleanProperty groundOnly = new BooleanProperty("ground-only", false);
    public final BooleanProperty reachOnly = new BooleanProperty("reach-only", false);
    public final PercentProperty slowdown = new PercentProperty("slowdown", 60);

    public KeepSprint() {
        super("KeepSprint", false);
    }
}
