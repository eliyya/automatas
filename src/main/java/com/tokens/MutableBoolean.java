package com.tokens;

public class MutableBoolean {
    private boolean value;

    public MutableBoolean(boolean initial) {
        this.value = initial;
    }

    public MutableBoolean() {
        this.value = false;
    }

    public boolean get() {
        return this.value;
    }

    public void set(boolean value) {
        this.value = value;
    }

    public void toggle() {
        this.value = !this.value;
    }
}
