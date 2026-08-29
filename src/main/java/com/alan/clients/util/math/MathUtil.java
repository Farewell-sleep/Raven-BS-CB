package com.alan.clients.util.math;

import com.alan.clients.util.vector.Vector3d;
import java.math.BigDecimal;
import java.math.RoundingMode;
import java.util.concurrent.ThreadLocalRandom;
import lombok.Generated;
import net.minecraft.util.MathHelper;

public final class MathUtil {
    public static double l(double var0, double var2) {
        if (var0 == var2) {
            return var0;
        }

        if (var0 > var2) {
            double d0 = var0;
            var0 = var2;
            var2 = d0;
        }

        return ThreadLocalRandom.current().nextDouble(var0, var2);
    }

    public static double round(double var0, int var2) {
        try {
            return BigDecimal.valueOf(var0).setScale(var2, RoundingMode.HALF_UP).doubleValue();
        } catch (Exception exception) {
            return 0.0;
        }
    }

    public static float f(float var0, float var1) {
        float f = Math.round(var0 / var1);
        return var1 * f;
    }

    public static double a(double var0, int var2, double var3) {
        double d0 = var3 / 2.0;
        double d1 = Math.floor(var0 / var3) * var3;
        return var0 >= d1 + d0
            ? new BigDecimal(Math.ceil(var0 / var3) * var3).setScale(var2, RoundingMode.HALF_UP).doubleValue()
            : new BigDecimal(d1).setScale(var2, RoundingMode.HALF_UP).doubleValue();
    }

    public static double m(double var0, double var2) {
        return (int)(Math.round(var0 / var2) * var2 * 1000.0) / 1000.0;
    }

    public static double m(double var0, double var2, double var4) {
        return var0 + var4 * (var2 - var0);
    }

    public static float lerp(float var0, float var1, float var2) {
        return var0 + var2 * (var1 - var0);
    }

    public static double getDistance(double var0, double var2, double var4, double var6, double var8, double var10) {
        double d0 = var6 - var0;
        double d1 = var8 - var2;
        double d2 = var10 - var4;
        return MathHelper.sqrt_double(d0 * d0 + d1 * d1 + d2 * d2);
    }

    public static double c(double var0, double var2, double var4) {
        return Math.max(var0, Math.min(var2, var4));
    }

    public static double n(double var0, double var2) {
        return Math.min(Math.abs(var0 - var2), Math.min(Math.abs(var0 - 360.0) - Math.abs(var2 - 0.0), Math.abs(var2 - 360.0) - Math.abs(var0 - 0.0)));
    }

    public static double b(Vector3d var0, Vector3d var1) {
        double d0 = var1.x - var0.x;
        return MathHelper.wrapAngleTo180_float((float)(Math.toDegrees(Math.atan2(var1.z - var0.z, d0)) - 90.0));
    }

    @Generated
    private MathUtil() {
        throw new UnsupportedOperationException("This is a utility class and cannot be instantiated");
    }
}
