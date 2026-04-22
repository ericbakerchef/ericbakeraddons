package com.example.mixinmod;

import java.lang.reflect.Method;

public final class ChatCommandsBridge {
    private static final String CHAT_COMMANDS_CLASS = "com.example.module.impl.ChatCommands";
    private static volatile boolean showOwnNameTagEnabled;
    private static volatile boolean resolved;
    private static volatile boolean available;
    private static volatile Method confirmPendingSsidFromClick;

    private ChatCommandsBridge() {
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

    public static void setShowOwnNameTagEnabled(boolean enabled) {
        showOwnNameTagEnabled = enabled;
    }

    public static boolean isShowOwnNameTagEnabled() {
        return showOwnNameTagEnabled;
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
            confirmPendingSsidFromClick = clazz.getMethod("confirmPendingSsidFromClick");
            return true;
        } catch (Throwable ignored) {
            return false;
        }
    }
}
