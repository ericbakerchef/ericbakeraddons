package com.example.mixinmod;

public final class TextShadowState {
    private static boolean enabled;

    private TextShadowState() {
    }

    public static void setEnabled(boolean value) {
        enabled = value;
    }

    public static boolean isEnabled() {
        return enabled;
    }
}
