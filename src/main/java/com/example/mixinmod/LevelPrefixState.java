package com.example.mixinmod;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import net.minecraft.class_124;
import net.minecraft.class_2561;
import net.minecraft.class_2583;
import net.minecraft.class_5250;

public final class LevelPrefixState {
    private static final Pattern BRACKET_NUMBER = Pattern.compile("\\[(\\d{3})\\]");
    private static final Pattern THREE_DIGITS = Pattern.compile("\\d{3}");

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
        List<Segment> segments = new ArrayList<>();
        class_2583 baseStyle = input.method_10866();
        input.method_27658((style, text) -> {
            if (text != null && !text.isEmpty()) {
                class_2583 effectiveStyle = style == null ? baseStyle : style;
                if (effectiveStyle == null) {
                    effectiveStyle = class_2583.field_24360;
                }
                segments.add(new Segment(text, effectiveStyle));
            }
            return Optional.empty();
        }, baseStyle);
        if (segments.isEmpty()) {
            return null;
        }
        class_5250 out = class_2561.method_43473();
        boolean[] changed = new boolean[] { false };
        for (int i = 0; i < segments.size(); i++) {
            Segment segment = segments.get(i);
            if (matchesSplitPrefix(segments, i)) {
                int value = Integer.parseInt(segments.get(i + 1).text);
                if (value >= 480 && value <= 559) {
                    class_124 bracketColor = (value <= 519)
                            ? (goldBrackets ? class_124.field_1065 : class_124.field_1063)
                            : (diamondBrackets ? class_124.field_1075 : class_124.field_1063);
                    class_124 numberColor = red480Plus ? class_124.field_1061 : class_124.field_1079;
                    appendColored(out, "[", segment.style, bracketColor);
                    appendColored(out, segments.get(i + 1).text, segments.get(i + 1).style, numberColor);
                    appendColored(out, "]", segments.get(i + 2).style, bracketColor);
                    changed[0] = true;
                    i += 2;
                    continue;
                }
            }
            appendStyled(out, segment.text, segment.style, changed);
        }
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

    private static void appendColored(class_5250 out, String text, class_2583 baseStyle, class_124 color) {
        if (text == null || text.isEmpty()) {
            return;
        }
        class_2583 effectiveStyle = baseStyle == null ? class_2583.field_24360 : baseStyle;
        out.method_10852(class_2561.method_43470(text).method_10862(effectiveStyle.method_10977(color)));
    }

    private static boolean matchesSplitPrefix(List<Segment> segments, int index) {
        if (index + 2 >= segments.size()) {
            return false;
        }
        String open = segments.get(index).text;
        String number = segments.get(index + 1).text;
        String close = segments.get(index + 2).text;
        return "[".equals(open) && "]".equals(close) && THREE_DIGITS.matcher(number).matches();
    }

    private static final class Segment {
        private final String text;
        private final class_2583 style;

        private Segment(String text, class_2583 style) {
            this.text = text;
            this.style = style;
        }
    }
}
