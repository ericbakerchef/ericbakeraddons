package com.example.mixins;

import com.example.mixinmod.ChatCommandsBridge;
import com.mojang.authlib.GameProfile;
import net.minecraft.class_2556;
import net.minecraft.class_2561;
import net.minecraft.class_7471;
import net.minecraft.class_7594;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

@Mixin(class_7594.class)
public class MessageHandlerPickaxeSuppressMixin {
    @Inject(method = "method_44736", at = @At("HEAD"), cancellable = true, require = 0)
    private void suppressGameMessage(class_2561 message, boolean overlay, CallbackInfo ci) {
        if (ChatCommandsBridge.shouldSuppressPickaxeChat(message)) {
            ChatCommandsBridge.handleSuppressedPickaxeMessage(message);
            ci.cancel();
        }
    }

    @Inject(method = "method_45746", at = @At("HEAD"), cancellable = true, require = 0)
    private void suppressProfilelessChat(class_2561 content, class_2556.class_7602 params, CallbackInfo ci) {
        class_2561 decorated = (params == null) ? content : params.method_44837(content);
        if (ChatCommandsBridge.shouldSuppressPickaxeChat(decorated) || ChatCommandsBridge.shouldSuppressPickaxeChat(content)) {
            ChatCommandsBridge.handleSuppressedPickaxeMessage(decorated != null ? decorated : content);
            ci.cancel();
        }
    }

    @Inject(method = "method_45748", at = @At("HEAD"), cancellable = true, require = 0)
    private void suppressSignedChat(class_7471 message, GameProfile sender, class_2556.class_7602 params, CallbackInfo ci) {
        class_2561 decorated = null;
        if (message != null) {
            class_2561 content = message.method_46291();
            decorated = (params == null) ? content : params.method_44837(content);
        }
        if (ChatCommandsBridge.shouldSuppressPickaxeChat(decorated)) {
            ChatCommandsBridge.handleSuppressedPickaxeMessage(decorated);
            ci.cancel();
        }
    }

    @Inject(method = "method_45745", at = @At("HEAD"), cancellable = true, require = 0)
    private void suppressQueuedProfilelessChat(class_2556.class_7602 params, class_2561 content, java.time.Instant receptionTimestamp, CallbackInfoReturnable<Boolean> cir) {
        class_2561 decorated = (params == null) ? content : params.method_44837(content);
        if (ChatCommandsBridge.shouldSuppressPickaxeChat(decorated) || ChatCommandsBridge.shouldSuppressPickaxeChat(content)) {
            ChatCommandsBridge.handleSuppressedPickaxeMessage(decorated != null ? decorated : content);
            cir.setReturnValue(false);
            cir.cancel();
        }
    }

    @Inject(method = "method_44943", at = @At("HEAD"), cancellable = true, require = 0)
    private void suppressQueuedSignedChat(class_2556.class_7602 params, class_7471 message, class_2561 decorated, GameProfile sender, boolean onlyShowSecureChat, java.time.Instant receptionTimestamp, CallbackInfoReturnable<Boolean> cir) {
        class_2561 content = message == null ? null : message.method_46291();
        if (ChatCommandsBridge.shouldSuppressPickaxeChat(decorated) || ChatCommandsBridge.shouldSuppressPickaxeChat(content)) {
            ChatCommandsBridge.handleSuppressedPickaxeMessage(decorated != null ? decorated : content);
            cir.setReturnValue(false);
            cir.cancel();
        }
    }
}
