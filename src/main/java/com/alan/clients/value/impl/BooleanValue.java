package com.alan.clients.value.impl;

import com.alan.clients.module.Module;
import com.alan.clients.value.Mode;
import com.alan.clients.value.Value;
import com.alan.clients.ui.click.standard.components.value.ValueComponent;
import com.alan.clients.ui.click.standard.components.value.impl.BooleanValueComponent;
import java.util.List;
import java.util.function.BooleanSupplier;
import lombok.Generated;

public class BooleanValue extends Value<Boolean> {
    private final Mode<?> mode;

    public BooleanValue(String var1, Module module, Boolean var3) {
        super(var1, module, var3);
        this.mode = null;
    }

    public BooleanValue(String var1, Mode<?> var2, Boolean var3) {
        super(var1, var2, var3);
        this.mode = null;
    }

    public BooleanValue(String var1, Module module, Boolean var3, BooleanSupplier booleanSupplier) {
        super(var1, module, var3, booleanSupplier);
        this.mode = null;
    }

    public BooleanValue(String var1, Mode<?> var2, Boolean var3, BooleanSupplier booleanSupplier) {
        super(var1, var2, var3, booleanSupplier);
        this.mode = null;
    }

    public BooleanValue(String var1, Module module, Boolean var3, Mode<?> mode) {
        super(var1, module, var3);
        this.mode = mode;
        this.wu().getValues().forEach(var1x -> var1x.setHideIf(() -> !this.wo()));
    }

    public BooleanValue(String var1, Mode<?> var2, Boolean var3, Mode<?> mode) {
        super(var1, var2, var3);
        this.mode = mode;
    }

    public BooleanValue(String var1, Module module, Boolean var3, BooleanSupplier booleanSupplier, Mode<?> mode) {
        super(var1, module, var3, booleanSupplier);
        this.mode = mode;
    }

    public BooleanValue(String var1, Mode<?> var2, Boolean var3, BooleanSupplier booleanSupplier, Mode<?> mode) {
        super(var1, var2, var3, booleanSupplier);
        this.mode = mode;
    }

    public void setValue(Boolean var1) {
        super.n(var1);
        if (this.mode != null && this.wq() != null && ((Module)this.wq()).isEnabled()) {
            if (this.wo()) {
                this.mode.a();
            } else {
                this.mode.unregister();
            }
        }
    }

    @Override
    public List<Value<?>> getSubValues() {
        return this.wu() == null ? null : this.wu().getValues();
    }

    @Override
    public void setValueAsObject(Object valueAsObject) {
        if (this.mode != null && this.wq() != null) {
            this.mode.onDisable();
            this.mode.unregister();
        }

        super.setValueAsObject(valueAsObject);
        if (this.mode != null && this.wq() != null && ((Module)this.wq()).isEnabled() && this.wo()) {
            if (this.wo()) {
                this.mode.a();
                this.mode.onEnable();
            } else {
                this.mode.onDisable();
            }
        }
    }

    public BooleanValueComponent wt() {
        return new BooleanValueComponent(this);
    }

    @Generated
    public Mode<?> wu() {
        return this.mode;
    }

    @Override
    public ValueComponent wl() {
        return this.wt();
    }

    @Override
    public void n(Boolean var1) {
        this.setValue(var1);
    }
}
