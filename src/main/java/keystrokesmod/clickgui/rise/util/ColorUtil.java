package keystrokesmod.clickgui.rise.util;

import java.awt.Color;

public class ColorUtil {
    public static Color withAlpha(Color c, int alpha) {
        return new Color(c.getRed(), c.getGreen(), c.getBlue(), Math.max(0, Math.min(255, alpha)));
    }
    public static int withAlpha(int rgb, int alpha) {
        Color c = new Color(rgb);
        return new Color(c.getRed(), c.getGreen(), c.getBlue(), Math.max(0, Math.min(255, alpha))).getRGB();
    }
    public static Color interpolate(Color from, Color to, float t) {
        t = Math.max(0, Math.min(1, t));
        return new Color(
            (int) (from.getRed() + (to.getRed() - from.getRed()) * t),
            (int) (from.getGreen() + (to.getGreen() - from.getGreen()) * t),
            (int) (from.getBlue() + (to.getBlue() - from.getBlue()) * t),
            (int) (from.getAlpha() + (to.getAlpha() - from.getAlpha()) * t)
        );
    }
}
