package com.alan.clients.util.shader.base;

import com.alan.clients.util.interfaces.InstanceAccess;
import java.util.List;
import lombok.Generated;

public abstract class RiseShader implements InstanceAccess {
    private boolean active;

    public RiseShader() {
    }

    public abstract void a(ShaderRenderType var1, float var2, List<Runnable> runnables);

    public abstract void update();

    @Generated
    public boolean isActive() {
        return this.active;
    }

    @Generated
    public void setActive(boolean active) {
        this.active = active;
    }
}
