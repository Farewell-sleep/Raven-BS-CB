package keystrokesmod.clickgui.rise.util;

public class StopWatch {
    private long startTime = System.currentTimeMillis();

    public void aX() { startTime = System.currentTimeMillis(); }
    public void reset() { startTime = System.currentTimeMillis(); }
    public long getElapsedTime() { return System.currentTimeMillis() - startTime; }
    public void setMillis(long millis) { startTime = System.currentTimeMillis() - millis; }
    public boolean T(long ms) { return getElapsedTime() >= ms; }
    public boolean finished(long ms) { return getElapsedTime() >= ms; }
}
