package com.alan.clients.util.shader.kernel;

public class GaussianKernel {
    private final int size;
    private final float[] kernel;

    public GaussianKernel(int var1) {
        this.size = var1;
        this.kernel = new float[var1];
    }

    public void uR() {
        float f = this.size / 2.0F;
        float f1 = 0.0F;

        for (int i = 0; i < this.size; i++) {
            float f2 = i / f;
            this.kernel[i] = 1.0F / (Math.abs(f) * 2.5066283F) * (float)Math.exp(-0.5 * f2 * f2);
            f1 += i > 0 ? this.kernel[i] * 2.0F : this.kernel[0];
        }

        for (int j = 0; j < this.size; j++) {
            this.kernel[j] = this.kernel[j] / f1;
        }
    }

    public int getSize() {
        return this.size;
    }

    public float[] getKernel() {
        return this.kernel;
    }
}
