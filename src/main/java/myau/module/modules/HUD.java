package myau.module.modules;

import myau.module.Module;
import myau.property.properties.BooleanProperty;
import myau.property.properties.FloatProperty;

import java.awt.Color;

/**
 * HUD 存根 —— Scaffold 的 block-counter / outline 渲染依赖 scale/shadow/getColor。
 */
public class HUD extends Module {
    public final FloatProperty scale = new FloatProperty("scale", 1.0F, 0.5F, 3.0F);
    public final BooleanProperty shadow = new BooleanProperty("shadow", true);
    public final BooleanProperty toggleSound = new BooleanProperty("toggle-sound", true);

    public HUD() {
        super("HUD", true);
    }

    public Color getColor(long offset) {
        return Color.CYAN;
    }
}
