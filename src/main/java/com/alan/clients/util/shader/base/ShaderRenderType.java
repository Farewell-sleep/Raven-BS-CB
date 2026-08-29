package com.alan.clients.util.shader.base;

public enum ShaderRenderType {
    CAMERA,
    OVERLAY;

    private static final ShaderRenderType[] $VALUES = vP();

    ShaderRenderType() {
    }

    private static ShaderRenderType[] vP() {
        return new ShaderRenderType[]{CAMERA, OVERLAY};
    }
}
