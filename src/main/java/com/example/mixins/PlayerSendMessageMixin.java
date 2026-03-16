package com.example.mixins;

import com.example.module.impl.ChatCommands;
import net.minecraft.class_1657;
import net.minecraft.class_2561;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin({class_1657.class})
public class PlayerSendMessageMixin {
    @Inject(method = {"method_7353(Lnet/minecraft/class_2561;Z)V"}, at = @At("HEAD"), cancellable = true, require = 0)
    private void suppressPickaxeSendMessage(class_2561 message, boolean overlay, CallbackInfo ci) {
        if (ChatCommands.shouldSuppressPickaxeChat(message)) {
            ChatCommands.handleSuppressedPickaxeMessage(message);
            ci.cancel();
        }
    }
}
