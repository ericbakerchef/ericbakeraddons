package com.example.mixinmod;

import java.util.Optional;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import net.minecraft.class_124;
import net.minecraft.class_2561;
import net.minecraft.class_2583;
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
        class_5250 out = class_2561.method_43473();
        boolean[] changed = new boolean[] { false };
        class_2583 baseStyle = input.method_10866();
        input.method_27658((style, text) -> {
            appendStyled(out, text, style, changed);
            return Optional.empty();
        }, baseStyle);
        return changed[0] ? out : null;
    }

    private static void appendStyled(class_5250 out, String text, class_2583 style, boolean[] changed) {
        if (text == null || text.isEmpty()) {
            return;
        }
        class_2583 effectiveStyle = style == null ? class_2583.field_24360 : style;
        Matcher matcher = BRACKET_NUMBER.matcher(text);
        int index = 0;
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
            int start = matcher.start();
            int end = matcher.end();
            if (start > index) {
                out.method_10852(class_2561.method_43470(text.substring(index, start)).method_10862(effectiveStyle));
            }
            class_124 bracketColor = (value <= 519)
                    ? (goldBrackets ? class_124.field_1065 : class_124.field_1063)
                    : (diamondBrackets ? class_124.field_1075 : class_124.field_1063);
            class_124 numberColor = red480Plus ? class_124.field_1061 : class_124.field_1079;
            class_2583 bracketStyle = effectiveStyle.method_10977(bracketColor);
            class_2583 numberStyle = effectiveStyle.method_10977(numberColor);
            out.method_10852(class_2561.method_43470("[").method_10862(bracketStyle));
            out.method_10852(class_2561.method_43470(String.valueOf(value)).method_10862(numberStyle));
            out.method_10852(class_2561.method_43470("]").method_10862(bracketStyle));
            index = end;
            changed[0] = true;
        }
        if (index < text.length()) {
            out.method_10852(class_2561.method_43470(text.substring(index)).method_10862(effectiveStyle));
        }
    }
}
