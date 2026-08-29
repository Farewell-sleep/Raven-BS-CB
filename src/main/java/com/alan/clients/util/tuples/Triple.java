package com.alan.clients.util.tuples;

public class Triple<A, B, C> {
    private A first;
    private B second;
    private C third;

    public Triple(A var1, B var2, C var3) {
        this.first = var1;
        this.second = var2;
        this.third = var3;
    }

    public A getFirst() {
        return this.first;
    }

    public void setFirst(A var1) {
        this.first = var1;
    }

    public B getSecond() {
        return this.second;
    }

    public void setSecond(B var1) {
        this.second = var1;
    }

    public C getThird() {
        return this.third;
    }

    public void setThird(C var1) {
        this.third = var1;
    }

    @Override
    public String toString() {
        return this.getFirst().toString() + " " + this.getSecond() + " " + this.getThird();
    }
}
