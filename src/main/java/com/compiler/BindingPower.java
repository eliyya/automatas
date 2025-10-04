package com.compiler;

public class BindingPower {
    private final float l;
    private final float r;

    public BindingPower(float left, float right) {
        this.l = left;
        this.r = right;
    }

    public float left() {
        return l;
    }

    public float right() {
        return r;
    }
}
