package com.minikloon.hardness2flagd.flagd;

public enum FlagdState {
    ENABLED(true),
    DISABLED(false),
    ;

    private final boolean bool;

    FlagdState(boolean bool) {
        this.bool = bool;
    }

    public boolean asBoolean() {
        return bool;
    }

    public static FlagdState parseFromHarness(String stateStr) {
        return "on".equals(stateStr) ? ENABLED : DISABLED;
    }
}
