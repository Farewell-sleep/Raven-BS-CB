package keystrokesmod.clickgui.rise.util;

public class GUIUtil {
    public static boolean c(float x, float y, float w, float h, float mx, float my) {
        return mx >= x && mx <= x + w && my >= y && my <= y + h;
    }
    public static boolean c(double x, double y, double w, double h, double mx, double my) {
        return mx >= x && mx <= x + w && my >= y && my <= y + h;
    }
}
