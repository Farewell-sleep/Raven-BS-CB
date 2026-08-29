package com.alan.clients.util.animation;

import java.util.function.Function;
import org.apache.commons.lang3.StringUtils;

public enum Easing {
    LINEAR(d -> d),
    EASE_IN_QUAD(d -> d * d),
    EASE_OUT_QUAD(d -> d * (2.0 - d)),
    EASE_IN_OUT_QUAD(d -> {
        double d2;
        if (d < 0.5) {
            d2 = 2.0 * d * d;
            return d2;
        }
        d2 = -1.0 + (4.0 - 2.0 * d) * d;
        return d2;
    }),
    EASE_IN_CUBIC(d -> d * d * d),
    EASE_OUT_CUBIC(d -> {
        Double d2 = d - 1.0;
        return d2 * d2 * d2 + 1.0;
    }),
    EASE_IN_OUT_CUBIC(d -> {
        double d2;
        if (d < 0.5) {
            d2 = 4.0 * d * d * d;
            return d2;
        }
        d2 = (d - 1.0) * (2.0 * d - 2.0) * (2.0 * d - 2.0) + 1.0;
        return d2;
    }),
    EASE_IN_QUART(d -> d * d * d * d),
    EASE_OUT_QUART(d -> {
        Double d2 = d - 1.0;
        return 1.0 - d2 * d2 * d2 * d2;
    }),
    EASE_IN_OUT_QUART(d -> {
        double d2;
        if (d < 0.5) {
            d2 = 8.0 * d * d * d * d;
            return d2;
        }
        Double d3 = d - 1.0;
        d2 = 1.0 - 8.0 * d3 * d3 * d3 * d3;
        return d2;
    }),
    EASE_IN_QUINT(d -> d * d * d * d * d),
    EASE_OUT_QUINT(d -> {
        Double d2 = d - 1.0;
        return 1.0 + d2 * d2 * d2 * d2 * d2;
    }),
    EASE_IN_OUT_QUINT(d -> {
        double d2;
        if (d < 0.5) {
            d2 = 16.0 * d * d * d * d * d;
            return d2;
        }
        Double d3 = d - 1.0;
        d2 = 1.0 + 16.0 * d3 * d3 * d3 * d3 * d3;
        return d2;
    }),
    EASE_IN_SINE(d -> 1.0 - Math.cos(d * Math.PI / 2.0)),
    EASE_OUT_SINE(d -> Math.sin(d * Math.PI / 2.0)),
    EASE_IN_OUT_SINE(d -> 1.0 - Math.cos(Math.PI * d / 2.0)),
    EASE_IN_EXPO(d -> {
        double d2;
        if (d == 0.0) {
            d2 = 0.0;
            return d2;
        }
        d2 = Math.pow(2.0, 10.0 * d - 10.0);
        return d2;
    }),
    EASE_OUT_EXPO(d -> {
        double d2;
        if (d == 1.0) {
            d2 = 1.0;
            return d2;
        }
        d2 = 1.0 - Math.pow(2.0, -10.0 * d);
        return d2;
    }),
    EASE_IN_OUT_EXPO(d -> {
        double d2;
        if (d == 0.0) {
            d2 = 0.0;
            return d2;
        }
        if (d == 1.0) {
            d2 = 1.0;
            return d2;
        }
        if (d < 0.5) {
            d2 = Math.pow(2.0, 20.0 * d - 10.0) / 2.0;
            return d2;
        }
        d2 = (2.0 - Math.pow(2.0, -20.0 * d + 10.0)) / 2.0;
        return d2;
    }),
    EASE_IN_CIRC(d -> 1.0 - Math.sqrt(1.0 - d * d)),
    EASE_OUT_CIRC(d -> {
        Double d2 = d - 1.0;
        return Math.sqrt(1.0 - d2 * d2);
    }),
    EASE_IN_OUT_CIRC(d -> {
        double d2;
        if (d < 0.5) {
            d2 = (1.0 - Math.sqrt(1.0 - 4.0 * d * d)) / 2.0;
            return d2;
        }
        d2 = (Math.sqrt(1.0 - 4.0 * (d - 1.0) * d) + 1.0) / 2.0;
        return d2;
    }),
    SIGMOID(d -> 1.0 / (1.0 + Math.exp(-d.doubleValue()))),
    EASE_OUT_ELASTIC(d -> {
        double d2;
        if (d == 0.0) {
            d2 = 0.0;
            return d2;
        }
        if (d == 1.0) {
            d2 = 1.0;
            return d2;
        }
        d2 = Math.pow(2.0, -10.0 * d) * Math.sin((d * 10.0 - 0.75) * 2.0943951023931953) * 0.5 + 1.0;
        return d2;
    }),
    EASE_IN_BACK(d -> 2.70158 * d * d * d - 1.70158 * d * d);

    private final Function<Double, Double> function;
    private static final Easing[] $VALUES = Easing.sI();



    private Easing(Function<Double, Double> function) {
        this.function = function;
    }

    public Function<Double, Double> getFunction() {
        return this.function;
    }

    public String toString() {
        return StringUtils.capitalize(super.toString().toLowerCase().replace("_", " "));
    }

    private static Easing[] sI() {
        return new Easing[]{LINEAR, EASE_IN_QUAD, EASE_OUT_QUAD, EASE_IN_OUT_QUAD, EASE_IN_CUBIC, EASE_OUT_CUBIC, EASE_IN_OUT_CUBIC, EASE_IN_QUART, EASE_OUT_QUART, EASE_IN_OUT_QUART, EASE_IN_QUINT, EASE_OUT_QUINT, EASE_IN_OUT_QUINT, EASE_IN_SINE, EASE_OUT_SINE, EASE_IN_OUT_SINE, EASE_IN_EXPO, EASE_OUT_EXPO, EASE_IN_OUT_EXPO, EASE_IN_CIRC, EASE_OUT_CIRC, EASE_IN_OUT_CIRC, SIGMOID, EASE_OUT_ELASTIC, EASE_IN_BACK};
    }
}
