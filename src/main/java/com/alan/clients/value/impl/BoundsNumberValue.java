package com.alan.clients.value.impl;

import com.alan.clients.module.Module;
import com.alan.clients.value.Mode;
import com.alan.clients.value.Value;
import com.alan.clients.ui.click.standard.components.value.ValueComponent;
import com.alan.clients.ui.click.standard.components.value.impl.BoundsNumberValueComponent;
import java.util.List;
import java.util.function.BooleanSupplier;
import lombok.Generated;

public class BoundsNumberValue extends Value<Number> {
    private final Number aQQ;
    private final Number aQR;
    private final Number aQS;
    private Number aQT;
    private Number aQU;

    public BoundsNumberValue(String var1, Module module, Number var3, Number var4, Number var5, Number var6, Number var7) {
        super(var1, module, var3);
        this.aQS = var7;
        this.aQQ = var5;
        this.aQR = var6;
        this.aQT = var4;
        this.aQU = var4;
    }

    public BoundsNumberValue(String var1, Mode<?> mode, Number var3, Number var4, Number var5, Number var6, Number var7) {
        super(var1, mode, var3);
        this.aQS = var7;
        this.aQQ = var5;
        this.aQR = var6;
        this.aQT = var4;
        this.aQU = var4;
    }

    public BoundsNumberValue(String var1, Module module, Number var3, Number var4, Number var5, Number var6, Number var7, BooleanSupplier booleanSupplier) {
        super(var1, module, var3, booleanSupplier);
        this.aQS = var7;
        this.aQQ = var5;
        this.aQR = var6;
        this.aQT = var4;
        this.aQU = var4;
    }

    public BoundsNumberValue(String var1, Mode<?> mode, Number var3, Number var4, Number var5, Number var6, Number var7, BooleanSupplier booleanSupplier) {
        super(var1, mode, var3, booleanSupplier);
        this.aQS = var7;
        this.aQQ = var5;
        this.aQR = var6;
        this.aQT = var4;
        this.aQU = var4;
    }

    @Override
    public List<Value<?>> getSubValues() {
        return null;
    }

    public Number wv() {
        long i = this.wo().longValue();
        long j = this.wA().longValue();
        if (i == j) {
            return i;
        }

        if (i > j) {
            long k = i;
            i = j;
            j = k;
        }

        long l = (long)(i + (j - i) * Math.random() * Math.random());
        return l;
    }

    public BoundsNumberValueComponent ww() {
        return new BoundsNumberValueComponent(this);
    }

    @Generated
    public Number wx() {
        return this.aQQ;
    }

    @Generated
    public Number wy() {
        return this.aQR;
    }

    @Generated
    public Number wz() {
        return this.aQS;
    }

    @Generated
    public Number wA() {
        return this.aQT;
    }

    @Generated
    public Number wB() {
        return this.aQU;
    }

    @Generated
    public void a(Number var1) {
        this.aQT = var1;
    }

    @Generated
    public void b(Number var1) {
        this.aQU = var1;
    }

    @Override
    public ValueComponent wl() {
        return this.ww();
    }
}
