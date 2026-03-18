package com.example.mixins;

import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.ricedotwho.rsm.ui.clickgui.settings.impl.DragSetting;
import org.joml.Vector2d;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(value = DragSetting.class, remap = false)
public abstract class DragSettingLoadMixin {
    @Shadow(remap = false)
    public abstract void setPosition(Vector2d position);

    @Shadow(remap = false)
    public abstract void setScale(Vector2d scale);

    @Inject(method = {"loadFromJson(Lcom/google/gson/JsonObject;)V"}, at = {@At("HEAD")}, cancellable = true, remap = false)
    private void loadFromJsonLenient(JsonObject json, CallbackInfo ci) {
        if (json != null) {
            try {
                int x = readInt(json, "x");
                int y = readInt(json, "y");
                double scaleX = readDouble(json, "scaleX");
                double scaleY = readDouble(json, "scaleY");
                setPosition(new Vector2d(x, y));
                setScale(new Vector2d(scaleX, scaleY));
            } catch (RuntimeException ignored) {
            }
        }
        ci.cancel();
    }

    private static int readInt(JsonObject json, String key) {
        JsonElement element = json.get(key);
        if (element == null || element.isJsonNull()) {
            throw new IllegalArgumentException("Missing " + key);
        }
        try {
            return element.getAsInt();
        } catch (RuntimeException ex) {
            String raw = element.getAsString();
            String cleaned = normalizeNumberString(raw);
            return (int) Double.parseDouble(cleaned);
        }
    }

    private static double readDouble(JsonObject json, String key) {
        JsonElement element = json.get(key);
        if (element == null || element.isJsonNull()) {
            throw new IllegalArgumentException("Missing " + key);
        }
        try {
            return element.getAsDouble();
        } catch (RuntimeException ex) {
            String raw = element.getAsString();
            String cleaned = normalizeNumberString(raw);
            return Double.parseDouble(cleaned);
        }
    }

    private static String normalizeNumberString(String input) {
        if (input == null || input.isEmpty()) {
            return input;
        }
        StringBuilder out = new StringBuilder(input.length());
        for (int i = 0; i < input.length(); i++) {
            char c = input.charAt(i);
            if (c == '-' || c == '+' || c == '.' || c == 'e' || c == 'E' || (c >= '0' && c <= '9')) {
                out.append(c);
            }
        }
        String normalized = out.toString();
        if (normalized.isEmpty()) {
            throw new NumberFormatException("Empty numeric value");
        }
        return normalized;
    }
}
