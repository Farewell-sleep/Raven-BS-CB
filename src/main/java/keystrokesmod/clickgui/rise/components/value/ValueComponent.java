package keystrokesmod.clickgui.rise.components.value;

import keystrokesmod.clickgui.rise.util.Vector2d;

public abstract class ValueComponent {
    protected Vector2d position;
    protected float height = 18;

    public abstract void draw(Vector2d position, int mouseX, int mouseY, float partialTicks);
    public abstract void click(int mouseX, int mouseY, int mouseButton);
    public abstract void release();
    public abstract void key(char typedChar, int keyCode);
    public boolean e(int mouseX, int mouseY, int mouseButton) { return false; }
    public float getHeight() { return height; }
    public Vector2d getPosition() { return position; }
    public void released() {}
    public void pz() {}
    public int pT() { return 0; }
    public void U(int alpha) {}
}
