package com.alan.clients.util.animation;

import lombok.Generated;

public class Animation {
    private Easing easing;
    private long duration;
    private long millis;
    private long startTime;
    private double startValue;
    private double destinationValue;
    private double value;
    private boolean finished;

    public Animation(Easing easing, long var2) {
        this.easing = easing;
        this.startTime = System.currentTimeMillis();
        this.duration = var2;
    }

    public void Q(double var1) {
        this.millis = System.currentTimeMillis();
        if (this.destinationValue != var1) {
            this.destinationValue = var1;
            this.reset();
        } else {
            this.finished = this.millis - this.duration > this.startTime;
            if (this.finished) {
                this.value = var1;
                return;
            }
        }

        double d0 = this.easing.getFunction().apply(this.sz());
        if (this.value > var1) {
            this.value = this.startValue - (this.startValue - var1) * d0;
        } else {
            this.value = this.startValue + (var1 - this.startValue) * d0;
        }
    }

    public double sz() {
        return (double)(System.currentTimeMillis() - this.startTime) / this.duration;
    }

    public void reset() {
        this.startTime = System.currentTimeMillis();
        this.startValue = this.value;
        this.finished = false;
    }

    @Generated
    public Easing getEasing() {
        return this.easing;
    }

    @Generated
    public long getDuration() {
        return this.duration;
    }

    @Generated
    public long getMillis() {
        return this.millis;
    }

    @Generated
    public long getStartTime() {
        return this.startTime;
    }

    @Generated
    public double getStartValue() {
        return this.startValue;
    }

    @Generated
    public double getDestinationValue() {
        return this.destinationValue;
    }

    @Generated
    public double getValue() {
        return this.value;
    }

    @Generated
    public boolean isFinished() {
        return this.finished;
    }

    @Generated
    public void setEasing(Easing easing) {
        this.easing = easing;
    }

    @Generated
    public void setDuration(long var1) {
        this.duration = var1;
    }

    @Generated
    public void setMillis(long var1) {
        this.millis = var1;
    }

    @Generated
    public void setStartTime(long var1) {
        this.startTime = var1;
    }

    @Generated
    public void setStartValue(double var1) {
        this.startValue = var1;
    }

    @Generated
    public void setDestinationValue(double var1) {
        this.destinationValue = var1;
    }

    @Generated
    public void setValue(double var1) {
        this.value = var1;
    }

    @Generated
    public void setFinished(boolean finished) {
        this.finished = finished;
    }
}
