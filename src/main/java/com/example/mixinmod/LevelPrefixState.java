package com.example.mixinmod;

import java.util.regex.Matcher;
import java.util.regex.Pattern;
import net.minecraft.class_124;
import net.minecraft.class_2561;
import net.minecraft.class_5250;

public final class LevelPrefixState {
    private static final Pattern BRACKET_NUMBER = Pattern.compile("\\[(\\d{3})\\]");

    private static boolean enabled;
    private static boolean red480Plus;
    private static boolean goldBrackets;
    private static boolean diamondBrackets;

    private LevelPrefixState() {
    }

    public static void setSettings(boolean enabled, boolean red480Plus, boolean goldBrackets, boolean diamondBrackets) {
        LevelPrefixState.enabled = enabled;
        LevelPrefixState.red480Plus = red480Plus;
        LevelPrefixState.goldBrackets = goldBrackets;
        LevelPrefixState.diamondBrackets = diamondBrackets;
    }

    public static class_2561 rewriteText(class_2561 input) {
        if (!enabled || input == null) {
            return null;
        }
        String raw = input.getString();
        if (raw == null || raw.isBlank()) {
            return null;
        }
        class_5250 out = class_2561.method_43473();
        int index = 0;
        Matcher matcher = BRACKET_NUMBER.matcher(raw);
        boolean changed = false;
        while (matcher.find()) {
            int value;
            try {
                value = Integer.parseInt(matcher.group(1));
            } catch (NumberFormatException ex) {
                continue;
            }
            if (value < 480 || value > 559) {
                continue;
            }
            class_124 bracketColor = (value <= 519)
                    ? (goldBrackets ? class_124.field_1065 : class_124.field_1063)
                    : (diamondBrackets ? class_124.field_1075 : class_124.field_1063);
            class_124 numberColor = red480Plus ? class_124.field_1061 : class_124.field_1079;
            int start = matcher.start();
            int end = matcher.end();
            if (start > index) {
                out.method_10852(class_2561.method_43470(raw.substring(index, start)));
            }
            out.method_10852(buildReplacement(value, bracketColor, numberColor).method_27661());
            index = end;
            changed = true;
        }
        if (!changed) {
            return null;
        }
        if (index < raw.length()) {
            out.method_10852(class_2561.method_43470(raw.substring(index)));
        }
        return out;
    }

    private static class_5250 buildReplacement(int value, class_124 bracketColor, class_124 numberColor) {
        class_5250 replacement = class_2561.method_43470("");
        replacement.method_10852(class_2561.method_43470("[").method_27692(bracketColor));
        replacement.method_10852(class_2561.method_43470(String.valueOf(value)).method_27692(numberColor));
        replacement.method_10852(class_2561.method_43470("]").method_27692(bracketColor));
        return replacement;
    }
}
