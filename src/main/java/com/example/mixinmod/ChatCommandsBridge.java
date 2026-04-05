package com.example.mixinmod;

import java.lang.reflect.Method;
import net.minecraft.class_2561;

public final class ChatCommandsBridge {
    private static final String CHAT_COMMANDS_CLASS = "com.example.module.impl.ChatCommands";
    private static volatile boolean resolved;
    private static volatile boolean available;
    private static volatile Method shouldSuppressPickaxeChat;
    private static volatile Method handleSuppressedPickaxeMessage;
    private static volatile Method confirmPendingSsidFromClick;
    private static volatile Method isPickaxeSuppressionEnabled;

    private ChatCommandsBridge() {
    }

    public static boolean shouldSuppressPickaxeChat(class_2561 message) {
        if (message == null) {
            return false;
        }
        return shouldSuppressPickaxeChat(message.getString());
    }

    public static boolean shouldSuppressPickaxeChat(String message) {
        if (message == null || !ensureResolved()) {
            return false;
        }
        try {
            Object result = shouldSuppressPickaxeChat.invoke(null, message);
            return Boolean.TRUE.equals(result);
        } catch (Throwable ignored) {
            return false;
        }
    }

    public static void handleSuppressedPickaxeMessage(class_2561 message) {
        if (message == null) {
            return;
        }
        handleSuppressedPickaxeMessage(message.getString());
    }

    public static void handleSuppressedPickaxeMessage(String message) {
        if (message == null || !ensureResolved()) {
            return;
        }
        try {
            handleSuppressedPickaxeMessage.invoke(null, message);
        } catch (Throwable ignored) {
        }
    }

    public static boolean confirmPendingSsidFromClick() {
        if (!ensureResolved()) {
            return false;
        }
        try {
            Object result = confirmPendingSsidFromClick.invoke(null);
            return Boolean.TRUE.equals(result);
        } catch (Throwable ignored) {
            return false;
        }
    }

    public static boolean isPickaxeSuppressionEnabled() {
        if (!ensureResolved()) {
            return false;
        }
        try {
            Object result = isPickaxeSuppressionEnabled.invoke(null);
            return Boolean.TRUE.equals(result);
        } catch (Throwable ignored) {
            return false;
        }
    }

    private static boolean ensureResolved() {
        if (resolved) {
            return available;
        }
        synchronized (ChatCommandsBridge.class) {
            if (resolved) {
                return available;
            }
            available = resolveChatCommands();
            resolved = true;
            return available;
        }
    }

    private static boolean resolveChatCommands() {
        try {
            Class<?> clazz = Class.forName(CHAT_COMMANDS_CLASS);
            shouldSuppressPickaxeChat = clazz.getMethod("shouldSuppressPickaxeChat", String.class);
            handleSuppressedPickaxeMessage = clazz.getMethod("handleSuppressedPickaxeMessage", String.class);
            confirmPendingSsidFromClick = clazz.getMethod("confirmPendingSsidFromClick");
            isPickaxeSuppressionEnabled = clazz.getMethod("isPickaxeSuppressionEnabled");
            return true;
        } catch (Throwable ignored) {
            return false;
        }
    }
}
