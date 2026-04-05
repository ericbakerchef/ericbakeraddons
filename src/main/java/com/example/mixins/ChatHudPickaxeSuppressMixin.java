package com.example.mixins;

import com.example.mixinmod.ChatCommandsBridge;
import java.lang.reflect.Field;
import java.lang.reflect.Method;
import java.util.IdentityHashMap;
import java.util.List;
import net.minecraft.class_2561;
import net.minecraft.class_338;
import net.minecraft.class_332;
import net.minecraft.class_408;
import net.minecraft.class_7469;
import net.minecraft.class_7591;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

@Mixin({class_338.class})
public class ChatHudPickaxeSuppressMixin {
    private static final long PURGE_INTERVAL_MS = 1000L;
    private static long nextPurgeAllowedMs;
    private static boolean pendingPurge;

    @Shadow
    private List<?> field_2061;

    @Shadow
    private List<?> field_2064;

    @Shadow
    private List<?> field_40392;

    @Inject(method = {"method_1812(Lnet/minecraft/class_2561;)V"}, at = @At("HEAD"), cancellable = true, require = 0)
    private void suppressPickaxeMessageSimple(class_2561 message, CallbackInfo ci) {
        if (ChatCommandsBridge.shouldSuppressPickaxeChat(message)) {
            ChatCommandsBridge.handleSuppressedPickaxeMessage(message);
            markPurgeNeeded();
            ci.cancel();
        }
    }

    @Inject(method = {"method_44811(Lnet/minecraft/class_2561;Lnet/minecraft/class_7469;Lnet/minecraft/class_7591;)V"}, at = @At("HEAD"), cancellable = true, require = 0)
    private void suppressPickaxeMessageSigned(class_2561 message, class_7469 signature, class_7591 indicator, CallbackInfo ci) {
        if (ChatCommandsBridge.shouldSuppressPickaxeChat(message)) {
            ChatCommandsBridge.handleSuppressedPickaxeMessage(message);
            markPurgeNeeded();
            ci.cancel();
        }
    }

    @Inject(method = {"method_1803(Ljava/lang/String;)V"}, at = @At("HEAD"), cancellable = true, require = 0)
    private void suppressPickaxeMessageString(String message, CallbackInfo ci) {
        if (ChatCommandsBridge.shouldSuppressPickaxeChat(message)) {
            ChatCommandsBridge.handleSuppressedPickaxeMessage(message);
            markPurgeNeeded();
            ci.cancel();
        }
    }

    @Inject(method = {"method_73203(Ljava/lang/String;)V"}, at = @At("HEAD"), cancellable = true, require = 0)
    private void suppressPickaxeMessageStringAlt(String message, CallbackInfo ci) {
        if (ChatCommandsBridge.shouldSuppressPickaxeChat(message)) {
            ChatCommandsBridge.handleSuppressedPickaxeMessage(message);
            markPurgeNeeded();
            ci.cancel();
        }
    }

    @Inject(method = {"method_73201"}, at = @At("HEAD"), cancellable = true, require = 0)
    private void suppressPickaxeMessageLine(class_338.class_11732 line, class_408.class_11738 style, CallbackInfoReturnable<class_408> cir) {
        String message = extractLineText(line);
        if (ChatCommandsBridge.shouldSuppressPickaxeChat(message)) {
            ChatCommandsBridge.handleSuppressedPickaxeMessage(message);
            markPurgeNeeded();
            cir.setReturnValue(null);
        }
    }

    @Inject(method = {"method_73202"}, at = @At("HEAD"), cancellable = true, require = 0)
    private void suppressPickaxeMessageLineVoid(class_338.class_11732 line, class_408.class_11738 style, CallbackInfo ci) {
        String message = extractLineText(line);
        if (ChatCommandsBridge.shouldSuppressPickaxeChat(message)) {
            ChatCommandsBridge.handleSuppressedPickaxeMessage(message);
            markPurgeNeeded();
            ci.cancel();
        }
    }

    @Inject(method = {"method_1817()V"}, at = @At("TAIL"), require = 0)
    private void purgePickaxeMessages(CallbackInfo ci) {
        purgeAllChatLists();
    }

    @Inject(method = {"method_1805(Lnet/minecraft/class_332;IIIZ)V"}, at = @At("HEAD"), require = 0)
    private void purgePickaxeMessagesOnRender(class_332 context, int x, int y, int z, boolean focused, CallbackInfo ci) {
        purgeAllChatLists();
    }

    private void purgeAllChatLists() {
        if (!ChatCommandsBridge.isPickaxeSuppressionEnabled()) {
            return;
        }
        long now = System.currentTimeMillis();
        if (!pendingPurge && now < nextPurgeAllowedMs) {
            return;
        }
        pendingPurge = false;
        nextPurgeAllowedMs = now + PURGE_INTERVAL_MS;
        purgeList(this.field_2061);
        purgeList(this.field_2064);
        purgeList(this.field_40392);
        IdentityHashMap<Object, Boolean> visited = new IdentityHashMap<>();
        purgeListsDeep(this, 2, visited);
    }

    private void markPurgeNeeded() {
        pendingPurge = true;
    }

    private void purgeListsDeep(Object root, int depth, IdentityHashMap<Object, Boolean> visited) {
        if (root == null || depth < 0) {
            return;
        }
        if (visited.put(root, Boolean.TRUE) != null) {
            return;
        }
        for (Field field : root.getClass().getDeclaredFields()) {
            try {
                field.setAccessible(true);
                Object value = field.get(root);
                if (value == null) {
                    continue;
                }
                if (value instanceof List) {
                    purgeList((List<?>) value);
                    continue;
                }
                if (depth > 0 && isCandidateContainer(value)) {
                    purgeListsDeep(value, depth - 1, visited);
                }
            } catch (Throwable ignored) {
            }
        }
    }

    private boolean isCandidateContainer(Object value) {
        String name = value.getClass().getName();
        return name.startsWith("net.minecraft") || name.contains("class_338");
    }

    private void purgeList(List<?> list) {
        if (list == null || list.isEmpty()) {
            return;
        }
        list.removeIf(entry -> ChatCommandsBridge.shouldSuppressPickaxeChat(extractText(entry)));
    }

    private String extractText(Object entry) {
        if (entry == null) {
            return null;
        }
        if (entry instanceof String) {
            return (String) entry;
        }
        if (entry instanceof class_2561) {
            return ((class_2561) entry).getString();
        }
        try {
            Method method = entry.getClass().getMethod("getString", new Class[0]);
            Object value = method.invoke(entry, new Object[0]);
            if (value instanceof String) {
                return (String) value;
            }
        } catch (ReflectiveOperationException ignored) {
        }
        try {
            Method method = entry.getClass().getMethod("getMessage", new Class[0]);
            Object value = method.invoke(entry, new Object[0]);
            if (value instanceof class_2561) {
                return ((class_2561) value).getString();
            }
            if (value instanceof String) {
                return (String) value;
            }
        } catch (ReflectiveOperationException ignored) {
        }
        for (Field field : entry.getClass().getDeclaredFields()) {
            try {
                field.setAccessible(true);
                Object value = field.get(entry);
                if (value instanceof class_2561) {
                    return ((class_2561) value).getString();
                }
                if (value instanceof String) {
                    return (String) value;
                }
            } catch (Throwable ignored) {
            }
        }
        return entry.toString();
    }

    private String extractLineText(Object line) {
        if (line == null) {
            return null;
        }
        if (line instanceof String) {
            return (String)line;
        }
        if (line instanceof class_2561) {
            return ((class_2561)line).getString();
        }
        try {
            Method method = line.getClass().getMethod("method_73207", new Class[0]);
            Object value = method.invoke(line, new Object[0]);
            if (value instanceof String) {
                return (String)value;
            }
        } catch (ReflectiveOperationException reflectiveOperationException) {}
        try {
            Method method = line.getClass().getMethod("getString", new Class[0]);
            Object value = method.invoke(line, new Object[0]);
            if (value instanceof String) {
                return (String)value;
            }
        } catch (ReflectiveOperationException reflectiveOperationException) {}
        return line.toString();
    }
}
