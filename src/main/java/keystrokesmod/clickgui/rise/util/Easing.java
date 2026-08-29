package keystrokesmod.clickgui.rise.util;

public enum Easing {
    LINEAR,
    EASE_IN_EXPO,
    EASE_OUT_EXPO,
    EASE_IN_OUT_EXPO,
    EASE_OUT_CUBIC,
    EASE_IN_CUBIC,
    EASE_OUT_BACK,
    EASE_IN_OUT_QUART;

    public float ease(float t) {
        switch (this) {
            case LINEAR: return t;
            case EASE_IN_EXPO: return t == 0 ? 0 : (float) Math.pow(2, 10 * t - 10);
            case EASE_OUT_EXPO: return t == 1 ? 1 : 1 - (float) Math.pow(2, -10 * t);
            case EASE_IN_OUT_EXPO:
                if (t == 0) return 0;
                if (t == 1) return 1;
                return t < 0.5 ? (float) Math.pow(2, 20 * t - 10) / 2 : (2 - (float) Math.pow(2, -20 * t + 10)) / 2;
            case EASE_OUT_CUBIC: return 1 - (float) Math.pow(1 - t, 3);
            case EASE_IN_CUBIC: return t * t * t;
            case EASE_OUT_BACK:
                float c1 = 1.70158f, c3 = c1 + 1;
                return 1 + c3 * (float) Math.pow(t - 1, 3) + c1 * (float) Math.pow(t - 1, 2);
            case EASE_IN_OUT_QUART:
                return t < 0.5 ? 8 * t * t * t * t : 1 - (float) Math.pow(-2 * t + 2, 4) / 2;
            default: return t;
        }
    }
}
