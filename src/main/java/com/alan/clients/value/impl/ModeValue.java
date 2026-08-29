package com.alan.clients.value.impl;

import com.alan.clients.module.Module;
import com.alan.clients.value.Mode;
import com.alan.clients.value.Value;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.function.BooleanSupplier;
import lombok.Generated;

public class ModeValue extends ListValue<Mode<?>> {
    private final List<Mode<?>> modes = new ArrayList<>();

    public ModeValue(String var1, Module module) {
        super(var1, module);
    }

    public ModeValue(String var1, Mode<?> mode) {
        super(var1, mode);
    }

    public ModeValue(String var1, Module module, BooleanSupplier booleanSupplier) {
        super(var1, module, booleanSupplier);
    }

    public ModeValue(String var1, Mode<?> mode, BooleanSupplier booleanSupplier) {
        super(var1, mode, booleanSupplier);
    }

    public void update(Mode<?> mode) {
        if (this.wq() == null || this.wq() instanceof Module && !((Module)this.wq()).isEnabled()) {
            this.n(mode);
        } else {
            this.wo().unregister();
            this.n(mode);
            this.wo().a();
        }
    }

    public ModeValue add(Mode<?>... mode) {
        if (mode == null) {
            return this;
        }

        this.modes.addAll(Arrays.asList(mode));
        return this;
    }

    public ModeValue setDefault(String var1) {
        this.n(this.modes.stream().filter(var1x -> var1x.getName().equalsIgnoreCase(var1)).findFirst().orElse(this.modes.get(0)));
        this.setDefaultValue(this.wo());
        this.modes.forEach(var1x -> var1x.getValues().forEach(var2 -> var2.setBooleanSupplier(() -> var1x != this.wo())));
        return this;
    }

    public void co(String var1) {
        this.n(this.modes.stream().filter(var1x -> var1x.getName().equalsIgnoreCase(var1)).findFirst().orElse(this.modes.get(0)));
    }

    @Override
    public List<Value<?>> getSubValues() {
        ArrayList arraylist = new ArrayList();

        for (Mode mode : this.getModes()) {
            arraylist.addAll(mode.getValues());
        }

        return arraylist;
    }

    @Generated
    @Override
    public List<Mode<?>> getModes() {
        return this.modes;
    }
}
