package keystrokesmod.clickgui.rise.util;

public class Animation {
    private Easing easing;
    private long duration;
    private double value;
    private double target;
    private long startTime;
    private double startValue;
    private boolean finished = true;

    public Animation(Easing easing, long duration) {
        this.easing = easing;
        this.duration = duration;
        this.value = 0;
        this.target = 0;
        this.startValue = 0;
        this.startTime = System.currentTimeMillis();
    }

    public void reset() {
        this.startTime = System.currentTimeMillis();
        this.startValue = this.value;
        this.finished = false;
    }

    public void setValue(double value) {
        this.value = value;
        this.target = value;
        this.startValue = value;
        this.finished = true;
    }

    public void Q(double target) {
        if (this.target != target) {
            this.target = target;
            this.reset();
        }
    }

    public void setEasing(Easing easing) { this.easing = easing; }
    public void setDuration(long duration) { this.duration = duration; }
    public long getDuration() { return duration; }

    public double getValue() {
        update();
        return value;
    }

    public boolean isFinished() {
        update();
        return finished;
    }

    private void update() {
        if (finished) return;
        long elapsed = System.currentTimeMillis() - startTime;
        if (elapsed >= duration) {
            value = target;
            finished = true;
        } else {
            float t = elapsed / (float) duration;
            double eased = easing.ease(t);
            value = startValue + (target - startValue) * eased;
        }
    }
}
