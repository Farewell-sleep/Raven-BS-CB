package com.alan.clients.value;

import com.alan.clients.module.Module;
import com.alan.clients.util.interfaces.Toggleable;
import com.alan.clients.ui.click.standard.components.value.ValueComponent;
import java.util.List;
import java.util.function.BooleanSupplier;
import java.util.function.Consumer;
import lombok.Generated;

public abstract class Value<T> {
    private final String name;
    public BooleanSupplier hideIf;
    public BooleanSupplier aQK;
    private T value;
    private boolean visible;
    private Toggleable aQM;
    private Consumer<T> valueChangeConsumer;
    private T defaultValue;

    public Value(String name, Module module, T var3) {
        this.name = name;
        this.hideIf = null;
        this.aQM = module;
        this.defaultValue = var3;
        this.n(var3);
        module.getValues().add(this);
    }

    public Value(String name, Mode<?> mode, T var3) {
        this.name = name;
        this.hideIf = null;
        this.defaultValue = var3;
        this.aQM = mode;
        this.n(var3);
        mode.getValues().add(this);
    }

    public Value(String name, Module module, T var3, BooleanSupplier booleanSupplier) {
        this.name = name;
        this.hideIf = booleanSupplier;
        this.aQM = module;
        this.defaultValue = var3;
        this.n(var3);
        module.getValues().add(this);
    }

    public Value(String name, Mode<?> mode, T var3, BooleanSupplier booleanSupplier) {
        this.name = name;
        this.hideIf = booleanSupplier;
        this.defaultValue = var3;
        this.n(var3);
        mode.getValues().add(this);
    }

    public void setValueAsObject(Object valueAsObject) {
        if (this.valueChangeConsumer != null) {
            this.valueChangeConsumer.accept((T)valueAsObject);
        }

        this.value = (T)valueAsObject;
    }

    public void n(T var1) {
        if (this.valueChangeConsumer != null) {
            this.valueChangeConsumer.accept((T)var1);
        }

        this.value = (T)var1;
    }

    public abstract List<Value<?>> getSubValues();

    public void setDefaultValue(T var1) {
        this.defaultValue = var1;
    }

    public ValueComponent wl() {
        return null;
    }

    @Override
    public String toString() {
        return this.getName();
    }

    @Generated
    public String getName() {
        return this.name;
    }

    @Generated
    public BooleanSupplier getHideIf() {
        return this.hideIf;
    }

    @Generated
    public BooleanSupplier getBooleanSupplier() {
        return this.aQK;
    }

    @Generated
    public T wo() {
        return this.value;
    }

    @Generated
    public boolean isVisible() {
        return this.visible;
    }

    @Generated
    public Toggleable wq() {
        return this.aQM;
    }

    @Generated
    public Consumer<T> getValueChangeConsumer() {
        return this.valueChangeConsumer;
    }

    @Generated
    public T getDefaultValue() {
        return this.defaultValue;
    }

    @Generated
    public void setHideIf(BooleanSupplier hideIf) {
        this.hideIf = hideIf;
    }

    @Generated
    public void setBooleanSupplier(BooleanSupplier booleanSupplier) {
        this.aQK = booleanSupplier;
    }

    @Generated
    public void setVisible(boolean visible) {
        this.visible = visible;
    }

    @Generated
    public void a(Toggleable toggleable) {
        this.aQM = toggleable;
    }

    @Generated
    public void setValueChangeConsumer(Consumer<T> valueChangeConsumer) {
        this.valueChangeConsumer = valueChangeConsumer;
    }
}
