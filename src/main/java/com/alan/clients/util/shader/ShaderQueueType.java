package com.alan.clients.util.shader;

import com.alan.clients.util.shader.ShaderType;
import lombok.Generated;

public enum ShaderQueueType {
    BLOOM(ShaderType.BLOOM),
    BLUR(ShaderType.BLUR),
    REGULAR(ShaderType.REGULAR);

    final ShaderType kW;
    private static final ShaderQueueType[] $VALUES = dX();

    @Generated
    public ShaderType dW() {
        return this.kW;
    }

    @Generated
    ShaderQueueType(ShaderType var3) {
        this.kW = var3;
    }

    private static ShaderQueueType[] dX() {
        return new ShaderQueueType[]{BLOOM, BLUR, REGULAR};
    }
}
