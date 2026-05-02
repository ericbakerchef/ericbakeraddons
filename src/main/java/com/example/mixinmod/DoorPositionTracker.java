package com.example.mixinmod;

import java.util.EnumMap;
import java.util.Map;
import java.util.Set;
import java.util.concurrent.ConcurrentHashMap;

import net.minecraft.class_1920;
import net.minecraft.class_2338;

public final class DoorPositionTracker {
    private static final Map<DoorType, Set<Long>> POSITIONS = new EnumMap<>(DoorType.class);

    static {
        for (DoorType type : DoorType.values()) {
            POSITIONS.put(type, ConcurrentHashMap.newKeySet());
        }
    }

    private DoorPositionTracker() {
    }

    public static void clearAll() {
        for (Set<Long> set : POSITIONS.values()) {
            set.clear();
        }
    }

    public static boolean isInDoor(class_1920 view, class_2338 pos, DoorType type) {
        Set<Long> set = POSITIONS.get(type);
        long key = pos.method_10063();
        if (set.contains(key)) {
            return true;
        }
        if (view == null) {
            return false;
        }
        return tryDetect(view, pos, type, set);
    }

    private static boolean tryDetect(class_1920 view, class_2338 pos, DoorType type, Set<Long> set) {
        net.minecraft.class_2248 block = type.getBlock();
        int px = pos.method_10263();
        int py = pos.method_10264();
        int pz = pos.method_10260();
        long[] candidate = new long[36];
        class_2338.class_2339 mut = new class_2338.class_2339();
        for (int dx = 0; dx < 3; dx++) {
            int bx = px - dx;
            for (int dz = 0; dz < 3; dz++) {
                int bz = pz - dz;
                for (int dy = 0; dy < 4; dy++) {
                    int by = py - dy;
                    if (matchesBox(view, bx, by, bz, block, mut, candidate)) {
                        for (long k : candidate) {
                            set.add(k);
                        }
                        return true;
                    }
                }
            }
        }
        return false;
    }

    private static boolean matchesBox(class_1920 view, int bx, int by, int bz, net.minecraft.class_2248 block, class_2338.class_2339 mut, long[] out) {
        int idx = 0;
        for (int ix = 0; ix < 3; ix++) {
            for (int iy = 0; iy < 4; iy++) {
                for (int iz = 0; iz < 3; iz++) {
                    int x = bx + ix;
                    int y = by + iy;
                    int z = bz + iz;
                    mut.method_10103(x, y, z);
                    if (!view.method_8320(mut).method_27852(block)) {
                        return false;
                    }
                    out[idx++] = class_2338.method_10064(x, y, z);
                }
            }
        }
        return true;
    }
}
