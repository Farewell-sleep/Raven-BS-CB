package com.alan.clients.value.impl;

import com.alan.clients.module.Module;
import com.alan.clients.value.Mode;
import com.alan.clients.value.Value;
import com.alan.clients.ui.click.standard.components.value.ValueComponent;
import com.alan.clients.ui.click.standard.components.value.impl.NumberValueComponent;
import java.util.List;
import java.util.function.BooleanSupplier;
import lombok.Generated;

public class NumberValue extends Value<Number> {
    private final Number min;
    private final Number max;
    private final Number decimalPlaces;

    public NumberValue(String var1, Module module, Number var3, Number var4, Number var5, Number var6) {
        super(var1, module, var3);
        this.decimalPlaces = var6;
        this.min = var4;
        this.max = var5;
    }

    public NumberValue(String var1, Mode<?> mode, Number var3, Number var4, Number var5, Number var6) {
        super(var1, mode, var3);
        this.decimalPlaces = var6;
        this.min = var4;
        this.max = var5;
    }

    public NumberValue(String var1, Module module, Number var3, Number var4, Number var5, Number var6, BooleanSupplier booleanSupplier) {
        super(var1, module, var3, booleanSupplier);
        this.decimalPlaces = var6;
        this.min = var4;
        this.max = var5;
    }

    public NumberValue(String var1, Mode<?> mode, Number var3, Number var4, Number var5, Number var6, BooleanSupplier booleanSupplier) {
        super(var1, mode, var3, booleanSupplier);
        this.decimalPlaces = var6;
        this.min = var4;
        this.max = var5;
    }

    @Override
    public List<Value<?>> getSubValues() {
        return null;
    }

    public NumberValueComponent wG() {
        return new NumberValueComponent(this);
    }

    @Generated
    public Number getMin() {
        return this.min;
    }

    @Generated
    public Number getMax() {
        return this.max;
    }

    @Generated
    public Number getDecimalPlaces() {
        return this.decimalPlaces;
    }

    @Override
    public ValueComponent wl() {
        return this.wG();
    }
}
