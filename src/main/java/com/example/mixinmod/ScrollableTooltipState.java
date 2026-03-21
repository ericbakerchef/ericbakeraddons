package com.example.mixinmod;

import net.minecraft.class_1735;
import net.minecraft.class_310;

public final class ScrollableTooltipState {
    private static final int SCROLL_SENSITIVITY = 12;

    private static Object lastScreen;
    private static class_1735 lastSlotRendered;
    private static int horizontalAmount;
    private static int verticalAmount;
    private static boolean enabled;

    private ScrollableTooltipState() {
    }

    public static void setEnabled(boolean value) {
        enabled = value;
        if (!value) {
            resetAll();
        }
    }

    public static void beginRender(Object screen, class_1735 slot) {
        if (!enabled) {
            resetAll();
            return;
        }
        if (screen != lastScreen) {
            lastScreen = screen;
            resetScroll();
            lastSlotRendered = null;
        }
        if (slot != lastSlotRendered) {
            lastSlotRendered = slot;
            resetScroll();
        }
        if (slot == null || !slot.method_7681()) {
            resetScroll();
        }
    }

    public static void endRender() {
    }

    public static void onMouseScrolled(Object screen, class_1735 slot, double horizontal, double vertical) {
        if (!enabled) {
            return;
        }
        if (screen != lastScreen) {
            lastScreen = screen;
            resetScroll();
            lastSlotRendered = null;
        }
        if (!hasItemRendering()) {
            return;
        }
        int horizontalDelta = Double.compare(horizontal, 0.0D);
        if (!class_310.method_1551().method_74187()) {
            horizontalAmount += horizontalDelta;
        } else {
            verticalAmount += horizontalDelta;
        }
        int verticalDelta = Double.compare(vertical, 0.0D);
        if (!class_310.method_1551().method_74187()) {
            verticalAmount += verticalDelta;
        } else {
            horizontalAmount += verticalDelta;
        }
    }

    public static int getXOffset() {
        if (!enabled || !hasItemRendering()) {
            return 0;
        }
        return horizontalAmount * SCROLL_SENSITIVITY;
    }

    public static int getYOffset() {
        if (!enabled || !hasItemRendering()) {
            return 0;
        }
        return verticalAmount * SCROLL_SENSITIVITY;
    }

    private static boolean hasItemRendering() {
        return (lastSlotRendered != null && lastSlotRendered.method_7681());
    }

    private static void resetScroll() {
        horizontalAmount = 0;
        verticalAmount = 0;
    }

    private static void resetAll() {
        resetScroll();
        lastSlotRendered = null;
        lastScreen = null;
    }
}
