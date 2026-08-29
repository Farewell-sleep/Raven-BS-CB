package com.alan.clients.value.impl;

import com.alan.clients.module.Module;
import com.alan.clients.value.Mode;
import com.alan.clients.value.Value;
import com.alan.clients.ui.click.standard.components.value.ValueComponent;
import com.alan.clients.ui.click.standard.components.value.impl.ListValueComponent;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.function.BooleanSupplier;
import lombok.Generated;

public class ListValue<T> extends Value<T> {
    private final List<T> modes = new ArrayList<>();

    public ListValue(String var1, Module module) {
        super(var1, module, null);
    }

    public ListValue(String var1, Mode<?> mode) {
        super(var1, mode, null);
    }

    public ListValue(String var1, Module module, BooleanSupplier booleanSupplier) {
        super(var1, module, null, booleanSupplier);
    }

    public ListValue(String var1, Mode<?> mode, BooleanSupplier booleanSupplier) {
        super(var1, mode, null, booleanSupplier);
    }

    public ListValue<T> add(T... var1) {
        if (var1 == null) {
            return this;
        }

        this.modes.addAll(Arrays.asList((T[])var1));
        return this;
    }

    public ListValue<T> setDefault(int var1) {
        this.n(this.modes.get(var1));
        return this;
    }

    public ListValue<T> setDefault(T var1) {
        this.n(var1);
        return this;
    }

    @Override
    public List<Value<?>> getSubValues() {
        return null;
    }

    public ListValueComponent wE() {
        return new ListValueComponent(this);
    }

    @Generated
    public List<T> getModes() {
        return this.modes;
    }

    @Override
    public ValueComponent wl() {
        return this.wE();
    }
}
