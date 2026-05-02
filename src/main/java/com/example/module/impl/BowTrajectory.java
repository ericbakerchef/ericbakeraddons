package com.example.module.impl;

import java.util.List;
import java.util.Optional;

import net.minecraft.class_1297;
import net.minecraft.class_1531;
import net.minecraft.class_1657;
import net.minecraft.class_1665;
import net.minecraft.class_1937;
import net.minecraft.class_238;
import net.minecraft.class_243;
import net.minecraft.class_310;

final class BowTrajectory {
    private static final int SIM_TICKS = 60;

    private BowTrajectory() {
    }

    /**
     * Simulates an arrow's flight from the player's eye in (yaw + yawOffset, pitch).
     * Returns the first non-arrow non-armor-stand entity intersected, or null if none.
     * Power matches Minecraft bow: pow = (c^2 + 2c) / 3 where c = clamp(useTicks/20, 0, 1);
     * pass useChargeFraction = 1.0 to assume max charge.
     */
    static class_1297 predictHitEntity(float yawOffset, float useChargeFraction) {
        class_310 mc = class_310.method_1551();
        if (mc == null || mc.field_1724 == null || mc.field_1687 == null) {
            return null;
        }
        class_1657 player = mc.field_1724;
        class_1937 level = mc.field_1687;

        double yawRad = Math.toRadians(player.method_36454());
        double offX = -Math.cos(yawRad) * 0.16D;
        double offZ = -Math.sin(yawRad) * 0.16D;

        class_243 startPos = new class_243(
            player.method_23317(),
            player.method_23318() + player.method_5751() - 0.1D,
            player.method_23321()
        ).method_1019(new class_243(offX, 0.0D, offZ));

        float pow = Math.max(0.0F, Math.min(1.0F, useChargeFraction));
        double speed = ((double) (pow * pow + 2.0F * pow) / 3.0D) * 3.0D;
        if (speed <= 0.0D) {
            return null;
        }

        class_243 motion = lookVector(player.method_36454() + yawOffset, player.method_36455())
            .method_1029()
            .method_1021(speed);

        class_243 pos = startPos;
        class_243 prevPos = startPos;
        for (int tick = 0; tick < SIM_TICKS; tick++) {
            class_243 nextPos = pos.method_1019(motion);
            class_238 scanBox = new class_238(prevPos, nextPos).method_1014(1.0D);

            class_1297 nearest = null;
            double nearestSq = Double.MAX_VALUE;
            List<class_1297> entities;
            try {
                entities = level.method_8335(player, scanBox);
            } catch (Throwable ignored) {
                entities = null;
            }
            if (entities != null) {
                for (class_1297 entity : entities) {
                    if (entity == null || entity == player) continue;
                    if (entity instanceof class_1665) continue;
                    if (entity instanceof class_1531) continue;
                    class_238 box;
                    try {
                        box = entity.method_5829();
                    } catch (Throwable ignored) {
                        continue;
                    }
                    if (box == null) continue;
                    box = box.method_1014(entity.method_5871());
                    Optional<class_243> hit = box.method_992(prevPos, nextPos);
                    if (hit.isEmpty()) continue;
                    double sq = prevPos.method_1025(hit.get());
                    if (sq < nearestSq) {
                        nearestSq = sq;
                        nearest = entity;
                    }
                }
            }
            if (nearest != null) {
                return nearest;
            }

            // block-collision early exit
            try {
                net.minecraft.class_239 blockHit = level.method_17742(new net.minecraft.class_3959(
                    pos, nextPos,
                    net.minecraft.class_3959.class_3960.field_17558,
                    net.minecraft.class_3959.class_242.field_1348,
                    player
                ));
                if (blockHit != null && blockHit.method_17783() == net.minecraft.class_239.class_240.field_1332) {
                    return null;
                }
            } catch (Throwable ignored) {
            }

            pos = nextPos;
            motion = new class_243(motion.field_1352 * 0.99D, motion.field_1351 * 0.99D - 0.05D, motion.field_1350 * 0.99D);
            prevPos = pos;
        }
        return null;
    }

    private static class_243 lookVector(float yawDeg, float pitchDeg) {
        double yaw = Math.toRadians(-yawDeg);
        double pitch = Math.toRadians(-pitchDeg);
        double cosPitch = Math.cos(pitch);
        return new class_243(
            Math.sin(yaw - Math.PI) * cosPitch,
            Math.sin(pitch),
            Math.cos(yaw - Math.PI) * cosPitch
        );
    }
}
