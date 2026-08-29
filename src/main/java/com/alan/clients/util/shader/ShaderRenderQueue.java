package com.alan.clients.util.shader;

import com.alan.clients.util.interfaces.InstanceAccess;
import com.alan.clients.util.shader.base.RiseShader;
import com.alan.clients.util.shader.base.ShaderRenderType;
import java.util.ArrayList;
import lombok.Generated;

public class ShaderRenderQueue implements InstanceAccess {
    private final ArrayList<Runnable> kK = new ArrayList<>();
    RiseShader kL = null;

    public ShaderRenderQueue(RiseShader var1) {
        this.kL = var1;
    }

    public ShaderRenderQueue() {
    }

    public void a(ShaderRenderType var1) {
        if (!this.kK.isEmpty()) {
            if (this.kL == null) {
                aEg.getFramebuffer().bindFramebuffer(false);
                this.kK.forEach(Runnable::run);
            } else {
                this.kL.a(var1, 0.0F, this.kK);
                if (var1 == ShaderRenderType.OVERLAY) {
                    this.kL.update();
                }
            }
        }
    }

    public void clear() {
        this.kK.clear();
    }

    public void c(Runnable runnable) {
        this.kK.add(runnable);
    }

    @Generated
    public ArrayList<Runnable> dT() {
        return this.kK;
    }

    @Generated
    public RiseShader dU() {
        return this.kL;
    }
}
