package com.alan.clients.bridge;

import com.alan.clients.module.Module;
import com.alan.clients.module.api.Category;
import com.alan.clients.value.impl.BooleanValue;
import com.alan.clients.value.impl.ColorValue;
import com.alan.clients.value.impl.ListValue;
import com.alan.clients.value.impl.NumberValue;
import keystrokesmod.module.setting.Setting;
import keystrokesmod.module.setting.impl.ButtonSetting;
import keystrokesmod.module.setting.impl.ColorSetting;
import keystrokesmod.module.setting.impl.SliderSetting;

import java.awt.Color;

/**
 * 把一个 BS 模块包装成 Rise Module，使 Rise clickgui 能显示并操作 BS 模块。
 */
public class RiseModuleWrapper extends Module {
    private final keystrokesmod.module.Module bs;
    private final Category category;

    public RiseModuleWrapper(keystrokesmod.module.Module bs) {
        super(RiseModuleBridge.of(bs.getName(), bs.getName(), RiseModuleBridge.mapCategory(bs.moduleCategory())));
        this.bs = bs;
        this.category = RiseModuleBridge.mapCategory(bs.moduleCategory());
        this.buildValues();
    }

    public keystrokesmod.module.Module bs() {
        return this.bs;
    }

    public Category riseCategory() {
        return this.category;
    }

    private void buildValues() {
        try {
            for (Setting s : this.bs.getSettings()) {
                if (s == null) continue;
                if (s instanceof ButtonSetting) {
                    ButtonSetting b = (ButtonSetting) s;
                    if (b.isMethodButton) continue;
                    BooleanValue v = new BooleanValue(s.getName(), this, b.isToggled());
                    v.setValueChangeConsumer(val -> b.setEnabled(val));
                } else if (s instanceof SliderSetting) {
                    SliderSetting sl = (SliderSetting) s;
                    if (sl.isString) {
                        String[] options = sl.getOptions();
                        if (options == null || options.length == 0) continue;
                        ListValue<String> lv = new ListValue<>(s.getName(), this);
                        for (String opt : options) lv.add(opt);
                        int idx = (int) Math.round(sl.getInput());
                        if (idx < 0 || idx >= options.length) idx = 0;
                        lv.setDefault(options[idx]);
                        lv.setValueChangeConsumer(val -> {
                            for (int i = 0; i < options.length; i++) {
                                if (options[i].equals(val)) {
                                    sl.setValue(i);
                                    return;
                                }
                            }
                        });
                    } else {
                        NumberValue nv = new NumberValue(s.getName(), this, sl.getInput(), sl.getMin(), sl.getMax(), 2);
                        nv.setValueChangeConsumer(val -> sl.setValue(((Number) val).doubleValue()));
                    }
                } else if (s instanceof ColorSetting) {
                    ColorSetting cs = (ColorSetting) s;
                    ColorValue cv = new ColorValue(s.getName(), this, new Color(cs.getColor(), true));
                    cv.setValueChangeConsumer(color -> {
                        try {
                            cs.setColor(color.getRed(), color.getGreen(), color.getBlue(), color.getAlpha());
                        } catch (Throwable ignored) {
                        }
                    });
                }
            }
        } catch (Throwable ignored) {
        }
    }

    @Override
    public boolean isEnabled() {
        return this.bs.isEnabled();
    }

    @Override
    public void toggle() {
        this.bs.toggle();
    }

    @Override
    public void setEnabled(boolean enabled) {
        if (enabled) {
            if (!this.bs.isEnabled()) this.bs.enable();
        } else {
            if (this.bs.isEnabled()) this.bs.disable();
        }
    }

    @Override
    public void onEnable() {
    }

    @Override
    public void onDisable() {
    }
}
