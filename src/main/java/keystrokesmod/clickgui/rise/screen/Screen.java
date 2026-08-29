package keystrokesmod.clickgui.rise.screen;

public interface Screen {
    void onRender(int mouseX, int mouseY, float partialTicks);
    void onKey(char typedChar, int keyCode);
    void f(int mouseX, int mouseY, int mouseButton); // click
    void oG(); // release
    void pY(); // pre render (bloom etc)
    void aT(); // init
    boolean pZ(); // can type to search
    boolean qa(); // sidebar visible
}
