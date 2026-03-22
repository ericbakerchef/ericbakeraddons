package com.example.mixinmod;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import net.minecraft.class_124;
import net.minecraft.class_2561;
import net.minecraft.class_2583;
import net.minecraft.class_5250;

public final class LevelPrefixState {
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
        List<CharSpan> spans = new ArrayList<>();
        class_2583 baseStyle = input.method_10866();
        input.method_27658((style, text) -> {
            if (text != null && !text.isEmpty()) {
                class_2583 effectiveStyle = style == null ? baseStyle : style;
                if (effectiveStyle == null) {
                    effectiveStyle = class_2583.field_24360;
                }
                for (int i = 0; i < text.length(); i++) {
                    spans.add(new CharSpan(text.charAt(i), effectiveStyle));
                }
            }
            return Optional.empty();
        }, baseStyle);
        if (spans.isEmpty()) {
            return null;
        }
        class_5250 out = class_2561.method_43473();
        boolean[] changed = new boolean[] { false };
        StringBuilder buffer = new StringBuilder();
        class_2583 bufferStyle = null;
        int index = 0;
        while (index < spans.size()) {
            CharSpan span = spans.get(index);
            if (span.ch == '[' && index + 4 < spans.size()) {
                CharSpan d1 = spans.get(index + 1);
                CharSpan d2 = spans.get(index + 2);
                CharSpan d3 = spans.get(index + 3);
                CharSpan close = spans.get(index + 4);
                if (isDigit(d1.ch) && isDigit(d2.ch) && isDigit(d3.ch) && close.ch == ']') {
                    int value = (d1.ch - '0') * 100 + (d2.ch - '0') * 10 + (d3.ch - '0');
                    if (value >= 480 && value <= 559) {
                        class_124 bracketColor = (value <= 519)
                                ? (goldBrackets ? class_124.field_1065 : class_124.field_1063)
                                : (diamondBrackets ? class_124.field_1075 : class_124.field_1063);
                        class_124 numberColor = red480Plus ? class_124.field_1061 : class_124.field_1079;
                        bufferStyle = appendChar(out, buffer, bufferStyle, '[', recolor(span.style, bracketColor));
                        bufferStyle = appendChar(out, buffer, bufferStyle, d1.ch, recolor(d1.style, numberColor));
                        bufferStyle = appendChar(out, buffer, bufferStyle, d2.ch, recolor(d2.style, numberColor));
                        bufferStyle = appendChar(out, buffer, bufferStyle, d3.ch, recolor(d3.style, numberColor));
                        bufferStyle = appendChar(out, buffer, bufferStyle, ']', recolor(close.style, bracketColor));
                        changed[0] = true;
                        index += 5;
                        continue;
                    }
                }
            }
            bufferStyle = appendChar(out, buffer, bufferStyle, span.ch, span.style);
            index++;
        }
        flush(out, buffer, bufferStyle);
        return changed[0] ? out : null;
    }

    private static class_2583 appendChar(
            class_5250 out,
            StringBuilder buffer,
            class_2583 bufferStyle,
            char ch,
            class_2583 style
    ) {
        class_2583 effectiveStyle = style == null ? class_2583.field_24360 : style;
        if (bufferStyle == null || !bufferStyle.equals(effectiveStyle)) {
            flush(out, buffer, bufferStyle);
            bufferStyle = effectiveStyle;
        }
        buffer.append(ch);
        return bufferStyle;
    }

    private static void flush(class_5250 out, StringBuilder buffer, class_2583 style) {
        if (buffer.length() == 0) {
            return;
        }
        class_2583 effectiveStyle = style == null ? class_2583.field_24360 : style;
        out.method_10852(class_2561.method_43470(buffer.toString()).method_10862(effectiveStyle));
        buffer.setLength(0);
    }

    private static class_2583 recolor(class_2583 style, class_124 color) {
        class_2583 effectiveStyle = style == null ? class_2583.field_24360 : style;
        return effectiveStyle.method_10977(color);
    }

    private static boolean isDigit(char ch) {
        return ch >= '0' && ch <= '9';
    }

    private static final class CharSpan {
        private final char ch;
        private final class_2583 style;

        private CharSpan(char ch, class_2583 style) {
            this.ch = ch;
            this.style = style;
        }
    }
}
