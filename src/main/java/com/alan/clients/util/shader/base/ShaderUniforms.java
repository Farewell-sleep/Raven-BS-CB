package com.alan.clients.util.shader.base;

import java.nio.FloatBuffer;
import org.lwjgl.opengl.GL20;

public class ShaderUniforms {
    public ShaderUniforms() {
    }

    public static void uniformFB(int var0, String var1, FloatBuffer floatBuffer) {
        GL20.glUniform1(getLocation(var0, var1), floatBuffer);
    }

    public static void uniform1i(int var0, String var1, int var2) {
        GL20.glUniform1i(getLocation(var0, var1), var2);
    }

    public static void uniform2i(int var0, String var1, int var2, int var3) {
        GL20.glUniform2i(getLocation(var0, var1), var2, var3);
    }

    public static void uniform1f(int var0, String var1, float var2) {
        GL20.glUniform1f(getLocation(var0, var1), var2);
    }

    public static void uniform2f(int var0, String var1, float var2, float var3) {
        GL20.glUniform2f(getLocation(var0, var1), var2, var3);
    }

    public static void uniform3f(int var0, String var1, float var2, float var3, float var4) {
        GL20.glUniform3f(getLocation(var0, var1), var2, var3, var4);
    }

    public static void uniform4f(int var0, String var1, float var2, float var3, float var4, float var5) {
        GL20.glUniform4f(getLocation(var0, var1), var2, var3, var4, var5);
    }

    private static int getLocation(int var0, String var1) {
        return GL20.glGetUniformLocation(var0, var1);
    }
}
