package com.example.mixinmod;

public final class FuckCoalState {
    private static volatile boolean enabled;
    private static volatile boolean witherEnabled;
    private static volatile boolean bloodEnabled;
    private static volatile boolean entranceEnabled;

    private FuckCoalState() {
    }

    public static void setSettings(boolean enabled, boolean wither, boolean blood, boolean entrance) {
        FuckCoalState.enabled = enabled;
        FuckCoalState.witherEnabled = wither;
        FuckCoalState.bloodEnabled = blood;
        FuckCoalState.entranceEnabled = entrance;
    }

    public static boolean isEnabled() {
        return enabled;
    }

    public static boolean isTypeActive(DoorType type) {
        if (!enabled) return false;
        switch (type) {
            case WITHER: return witherEnabled;
            case BLOOD: return bloodEnabled;
            case ENTRANCE: return entranceEnabled;
            default: return false;
        }
    }

    public static boolean isAnyTypeActive() {
        return enabled && (witherEnabled || bloodEnabled || entranceEnabled);
    }

    public static boolean isBlockActive(net.minecraft.class_2248 block) {
        if (!enabled) return false;
        if (witherEnabled && block == DoorType.WITHER.getBlock()) return true;
        if (bloodEnabled && block == DoorType.BLOOD.getBlock()) return true;
        if (entranceEnabled && block == DoorType.ENTRANCE.getBlock()) return true;
        return false;
    }
}
