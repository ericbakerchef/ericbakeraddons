package com.example.mixins;

import com.example.module.impl.ChatCommands;
import com.mojang.authlib.GameProfile;
import java.time.Instant;
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
    @Inject(method = "onGameMessage", at = @At("HEAD"), cancellable = true, require = 0)
    private void suppressGameMessage(class_2561 message, boolean overlay, CallbackInfo ci) {
        if (ChatCommands.shouldSuppressPickaxeChat(message)) {
            ChatCommands.handleSuppressedPickaxeMessage(message);
            ci.cancel();
        }
    }

    @Inject(method = "method_45745", at = @At("HEAD"), cancellable = true, require = 0)
    private void suppressProfilelessChat(class_2556.class_7602 params, class_2561 content, Instant receptionTimestamp, CallbackInfoReturnable<Boolean> cir) {
        if (ChatCommands.shouldSuppressPickaxeChat(content)) {
            ChatCommands.handleSuppressedPickaxeMessage(content);
            cir.setReturnValue(false);
        }
    }

    @Inject(method = "processChatMessageInternal", at = @At("HEAD"), cancellable = true, require = 0)
    private void suppressSignedChat(class_2556.class_7602 params, class_7471 message, class_2561 decorated, GameProfile sender, boolean onlyShowSecureChat, Instant receptionTimestamp, CallbackInfoReturnable<Boolean> cir) {
        if (ChatCommands.shouldSuppressPickaxeChat(decorated)) {
            ChatCommands.handleSuppressedPickaxeMessage(decorated);
            cir.setReturnValue(false);
        }
    }
}
