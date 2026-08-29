package com.alan.clients.value.impl;

import com.alan.clients.module.Module;
import com.alan.clients.value.Mode;
import com.alan.clients.value.Value;
import com.alan.clients.ui.click.standard.components.value.ValueComponent;
import com.alan.clients.ui.click.standard.components.value.impl.StringValueComponent;
import java.util.List;
import java.util.function.BooleanSupplier;

public class StringValue extends Value<String> {
    public StringValue(String var1, Module module, String var3) {
        super(var1, module, var3);
    }

    public StringValue(String var1, Mode<?> mode, String var3) {
        super(var1, mode, var3);
    }

    public StringValue(String var1, Module module, String var3, BooleanSupplier booleanSupplier) {
        super(var1, module, var3, booleanSupplier);
    }

    public StringValue(String var1, Mode<?> mode, String var3, BooleanSupplier booleanSupplier) {
        super(var1, mode, var3, booleanSupplier);
    }

    @Override
    public List<Value<?>> getSubValues() {
        return null;
    }

    public StringValueComponent wH() {
        return new StringValueComponent(this);
    }

    @Override
    public ValueComponent wl() {
        return this.wH();
    }
}
