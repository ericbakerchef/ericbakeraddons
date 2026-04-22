package com.example.mixins;

import com.example.module.impl.ChatCommands;
import net.minecraft.class_1269;
import net.minecraft.class_1297;
import net.minecraft.class_1657;
import net.minecraft.class_3965;
import net.minecraft.class_3966;
import net.minecraft.class_636;
import net.minecraft.class_746;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

@Mixin(class_636.class)
public class ClientInteractionManagerBlazeBlockMixin {
    @Inject(method = "method_2918", at = @At("HEAD"), cancellable = true, require = 0)
    private void blockWrongBlazeAttack(class_1657 player, class_1297 entity, CallbackInfo ci) {
        if (ChatCommands.shouldBlockBlazePuzzleClick()) {
            ci.cancel();
        }
    }

    @Inject(method = "method_2905", at = @At("HEAD"), cancellable = true, require = 0)
    private void blockWrongBlazeEntityInteract(class_1657 player, class_1297 entity, net.minecraft.class_1268 hand, CallbackInfoReturnable<class_1269> cir) {
        if (ChatCommands.shouldBlockBlazePuzzleClick()) {
            cir.setReturnValue(class_1269.field_5811);
            cir.cancel();
        }
    }

    @Inject(method = "method_2917", at = @At("HEAD"), cancellable = true, require = 0)
    private void blockWrongBlazeEntityHitboxInteract(class_1657 player, class_1297 entity, class_3966 hitResult, net.minecraft.class_1268 hand, CallbackInfoReturnable<class_1269> cir) {
        if (ChatCommands.shouldBlockBlazePuzzleClick()) {
            cir.setReturnValue(class_1269.field_5811);
            cir.cancel();
        }
    }

    @Inject(method = "method_2919", at = @At("HEAD"), cancellable = true, require = 0)
    private void blockWrongBlazeItemUse(class_1657 player, net.minecraft.class_1268 hand, CallbackInfoReturnable<class_1269> cir) {
        if (ChatCommands.shouldBlockBlazePuzzleClick()) {
            cir.setReturnValue(class_1269.field_5811);
            cir.cancel();
        }
    }

    @Inject(method = "method_2896", at = @At("HEAD"), cancellable = true, require = 0)
    private void blockWrongBlazeBlockUse(class_746 player, net.minecraft.class_1268 hand, class_3965 hitResult, CallbackInfoReturnable<class_1269> cir) {
        if (ChatCommands.shouldBlockBlazePuzzleClick()) {
            cir.setReturnValue(class_1269.field_5811);
            cir.cancel();
        }
    }
}
