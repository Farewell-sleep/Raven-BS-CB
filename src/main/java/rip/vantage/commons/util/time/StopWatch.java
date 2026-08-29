package rip.vantage.commons.util.time;

/**
 * Rise 使用的 StopWatch（rip.vantage.commons.util.time）——为 BS 移植而建的兼容实现。
 */
public class StopWatch {
    private long millis = System.currentTimeMillis();

    public StopWatch() {
    }

    public void reset() {
        this.millis = System.currentTimeMillis();
    }

    public void aX() {
        this.millis = System.currentTimeMillis();
    }

    public void setMillis(long millis) {
        this.millis = millis;
    }

    public long getElapsedTime() {
        return System.currentTimeMillis() - this.millis;
    }

    public boolean T(long delay) {
        return this.getElapsedTime() >= delay;
    }

    public boolean finished(long delay) {
        return this.T(delay);
    }

    public long getTime() {
        return this.millis;
    }
}
