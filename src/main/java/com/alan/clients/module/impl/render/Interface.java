package com.alan.clients.module.impl.render;

import com.alan.clients.module.Module;
import com.alan.clients.module.api.Category;
import com.alan.clients.module.api.ModuleInfo;
import com.alan.clients.value.impl.BooleanValue;
import com.alan.clients.value.impl.NumberValue;

@ModuleInfo(aliases = "module.render.interface.name", description = "module.render.interface.description", category = Category.RENDER)
public class Interface extends Module {
    public BooleanValue suffix = new BooleanValue("Suffix", this, true);
    public BooleanValue aoc = new BooleanValue("Shaders", this, false);
    private final NumberValue roundingRadius = new NumberValue("Rounding Radius", this, 5, 0, 20, 0.5);

    public Interface() {
    }

    public void rebuildEntries() {
    }

    public void createArrayList() {
    }

    public void updateEntryNames() {
    }

    public int getBlurRadius() {
        return 8;
    }

    public float getBlurCompression() {
        return 0.5F;
    }

    public int getBloomRadius() {
        return 8;
    }

    public float getBloomCompression() {
        return 0.5F;
    }

    public int getBackgroundAlpha() {
        return 150;
    }

    public double getRoundingRadius() {
        return this.roundingRadius.wo().doubleValue();
    }
}
