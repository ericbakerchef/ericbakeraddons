package com.example.module.impl;

import java.util.ArrayList;
import java.util.Comparator;
import java.util.HashMap;
import java.util.List;
import java.util.Locale;
import java.util.Map;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import net.minecraft.class_124;
import net.minecraft.class_1297;
import net.minecraft.class_1531;
import net.minecraft.class_1545;
import net.minecraft.class_310;

final class OdinBlazeSolver {
    private static final Pattern HEALTH_REGEX = Pattern.compile(
        "\\[Lv15\\]\\s*[\\p{So}\\p{Cntrl}\\p{Punct}]?\\s*Blaze\\s+[\\d,]+/([\\d,]+)",
        Pattern.CASE_INSENSITIVE);
    private static final double SUPPORT_SEARCH_RANGE_SQ = 9.0D;

    private OdinBlazeSolver() {
    }

    static class_1297 getNextBlaze(class_310 mc, Boolean lowerMode) {
        if (mc == null || mc.field_1687 == null || lowerMode == null) {
            return null;
        }
        Iterable<? extends class_1297> entities;
        try {
            entities = mc.field_1687.method_18112();
        } catch (Throwable ignored) {
            return null;
        }
        if (entities == null) {
            return null;
        }
        List<class_1531> stands = new ArrayList<>();
        Map<class_1531, Integer> hpMap = new HashMap<>();
        List<class_1297> allEntities = new ArrayList<>();
        for (class_1297 entity : entities) {
            if (entity == null) continue;
            allEntities.add(entity);
            if (!(entity instanceof class_1531)) continue;
            String name;
            try {
                name = entity.method_5477() == null ? null : entity.method_5477().getString();
            } catch (Throwable ignored) {
                continue;
            }
            if (name == null || name.isEmpty()) continue;
            String stripped = class_124.method_539(name);
            Matcher m = HEALTH_REGEX.matcher(stripped);
            if (!m.find()) continue;
            int hp;
            try {
                hp = Integer.parseInt(m.group(1).replace(",", ""));
            } catch (NumberFormatException ignored) {
                continue;
            }
            stands.add((class_1531) entity);
            hpMap.put((class_1531) entity, hp);
        }
        if (stands.isEmpty()) {
            return null;
        }
        Comparator<class_1531> cmp = Comparator.comparingInt(s -> hpMap.getOrDefault(s, 0));
        if (lowerMode) {
            cmp = cmp.reversed();
        }
        stands.sort(cmp);
        class_1531 firstStand = stands.get(0);
        class_1297 linked = findNearestLinkedBlaze(allEntities, firstStand);
        return linked != null ? linked : firstStand;
    }

    private static class_1297 findNearestLinkedBlaze(List<class_1297> entities, class_1297 source) {
        if (source == null) return null;
        net.minecraft.class_243 src = source.method_33571();
        if (src == null) return null;
        class_1297 best = null;
        double bestSq = SUPPORT_SEARCH_RANGE_SQ;
        for (class_1297 candidate : entities) {
            if (candidate == null || candidate == source) continue;
            if (!isBlaze(candidate)) continue;
            net.minecraft.class_243 pos = candidate.method_33571();
            if (pos == null) continue;
            double dx = pos.field_1352 - src.field_1352;
            double dy = pos.field_1351 - src.field_1351;
            double dz = pos.field_1350 - src.field_1350;
            double sq = dx * dx + dy * dy + dz * dz;
            if (sq <= bestSq) {
                bestSq = sq;
                best = candidate;
            }
        }
        return best;
    }

    private static boolean isBlaze(class_1297 entity) {
        if (entity instanceof class_1545) return true;
        try {
            String type = entity.method_5864() == null ? null : String.valueOf(entity.method_5864()).toLowerCase(Locale.ROOT);
            return type != null && type.contains("blaze");
        } catch (Throwable ignored) {
            return false;
        }
    }
}
