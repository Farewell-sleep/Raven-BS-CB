package com.alan.clients.module;

import com.alan.clients.Client;
import com.alan.clients.module.api.ModuleInfo;
import com.alan.clients.newevent.impl.other.ModuleToggleEvent;
import com.alan.clients.util.interfaces.InstanceAccess;
import com.alan.clients.util.interfaces.Toggleable;
import com.alan.clients.value.Value;
import com.alan.clients.value.impl.BooleanValue;
import com.alan.clients.value.impl.ModeValue;
import com.alan.clients.util.interfaces.ExecutorAccess;
import com.alan.clients.util.localization.Localization;
import com.alan.clients.util.interfaces.Bindable;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import lombok.Generated;

public abstract class Module implements InstanceAccess, ExecutorAccess, Toggleable, Bindable {
    private String[] aliases;
    private final List<Value<?>> values = new ArrayList<>();
    private ModuleInfo moduleInfo;
    private boolean enabled;
    private int keyCode;

    public Module() {
        if (this.getClass().isAnnotationPresent(ModuleInfo.class)) {
            this.moduleInfo = this.getClass().getAnnotation(ModuleInfo.class);
            this.aliases = Arrays.stream(this.moduleInfo.aliases()).map(Localization::ce).toArray(String[]::new);
            this.keyCode = this.getModuleInfo().keyBind();
        } else {
            throw new RuntimeException("ModuleInfo annotation not found on " + this.getClass().getSimpleName());
        }
    }

    public Module(ModuleInfo moduleInfo) {
        this.moduleInfo = moduleInfo;
        this.aliases = this.moduleInfo.aliases();
        this.keyCode = this.getModuleInfo().keyBind();
    }

    @Override
    public String getName() {
        return this.aliases[0];
    }

    @Override
    public void onKey() {
        this.toggle();
    }

    @Override
    public int getKey() {
        return this.keyCode;
    }

    @Override
    public void toggle() {
        this.setEnabled(!this.enabled);
    }

    public void setEnabled(boolean enabled) {
        if (this.enabled != enabled && (this.moduleInfo.allowDisable() || enabled)) {
            this.enabled = enabled;
            Client.a.e().d(new ModuleToggleEvent(this));
            if (enabled) {
                this.superEnable();
            } else {
                this.superDisable();
            }
        }
    }

    public final void superEnable() {
        Client.a.e().b(this);
        this.values.stream().filter(var0 -> var0 instanceof ModeValue).forEach(var0 -> ((ModeValue)var0).wo().a());
        this.values.stream().filter(var0 -> var0 instanceof BooleanValue).forEach(var0 -> {
            BooleanValue booleanvalue = (BooleanValue)var0;
            if (booleanvalue.wu() != null && booleanvalue.wo()) {
                booleanvalue.wu().a();
            }
        });
        this.onEnable();
    }

    public final void superDisable() {
        Client.a.e().c(this);
        this.values.stream().filter(var0 -> var0 instanceof ModeValue).forEach(var0 -> ((ModeValue)var0).wo().unregister());
        this.values.stream().filter(var0 -> var0 instanceof BooleanValue).forEach(var0 -> {
            BooleanValue booleanvalue = (BooleanValue)var0;
            if (booleanvalue.wu() != null) {
                booleanvalue.wu().unregister();
            }
        });
        this.onDisable();
    }

    @Override
    public void onEnable() {
    }

    @Override
    public void onDisable() {
    }

    public List<Value<?>> getAllValues() {
        ArrayList arraylist = new ArrayList();
        this.values.forEach(var1 -> {
            List list = var1.getSubValues();
            arraylist.add(var1);
            if (list != null) {
                arraylist.addAll(list);
            }
        });
        return arraylist;
    }

    @Generated
    @Override
    public String[] getAliases() {
        return this.aliases;
    }

    @Generated
    public List<Value<?>> getValues() {
        return this.values;
    }

    @Generated
    public ModuleInfo getModuleInfo() {
        return this.moduleInfo;
    }

    @Generated
    public boolean isEnabled() {
        return this.enabled;
    }

    @Generated
    public void setAliases(String[] aliases) {
        this.aliases = aliases;
    }

    @Generated
    public void setModuleInfo(ModuleInfo moduleInfo) {
        this.moduleInfo = moduleInfo;
    }

    @Generated
    @Override
    public void setKey(int var1) {
        this.keyCode = var1;
    }
}
