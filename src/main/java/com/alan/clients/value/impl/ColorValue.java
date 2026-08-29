package com.alan.clients.value.impl;

import com.alan.clients.module.Module;
import com.alan.clients.value.Mode;
import com.alan.clients.value.Value;
import com.alan.clients.ui.click.standard.components.value.ValueComponent;
import com.alan.clients.ui.click.standard.components.value.impl.ColorValueComponent;
import java.awt.Color;
import java.util.List;
import java.util.function.BooleanSupplier;

public class ColorValue extends Value<Color> {
    public ColorValue(String var1, Module module, Color color) {
        super(var1, module, color);
    }

    public ColorValue(String var1, Mode<?> mode, Color color) {
        super(var1, mode, color);
    }

    public ColorValue(String var1, Module module, Color color, BooleanSupplier booleanSupplier) {
        super(var1, module, color, booleanSupplier);
    }

    public ColorValue(String var1, Mode<?> mode, Color color, BooleanSupplier booleanSupplier) {
        super(var1, mode, color, booleanSupplier);
    }

    @Override
    public List<Value<?>> getSubValues() {
        return null;
    }

    public ColorValueComponent wC() {
        return new ColorValueComponent(this);
    }

    @Override
    public ValueComponent wl() {
        return this.wC();
    }
}
