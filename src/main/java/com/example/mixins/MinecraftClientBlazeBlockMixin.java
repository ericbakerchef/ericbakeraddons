package com.example.mixins;

import com.example.module.impl.ChatCommands;
import net.minecraft.class_310;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

@Mixin(class_310.class)
public class MinecraftClientBlazeBlockMixin {
    @Inject(method = "method_1536", at = @At("HEAD"), cancellable = true, require = 0)
    private void blockWrongBlazeLeftClick(CallbackInfoReturnable<Boolean> cir) {
        if (ChatCommands.shouldBlockBlazePuzzleClick()) {
            cir.setReturnValue(false);
            cir.cancel();
        }
    }

    @Inject(method = "method_1583", at = @At("HEAD"), cancellable = true, require = 0)
    private void blockWrongBlazeRightClick(CallbackInfo ci) {
        if (ChatCommands.shouldBlockBlazePuzzleClick()) {
            ci.cancel();
        }
    }
}
