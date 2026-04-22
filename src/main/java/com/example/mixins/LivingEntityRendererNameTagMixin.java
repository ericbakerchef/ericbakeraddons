package com.example.mixins;

import com.example.module.impl.ChatCommands;
import net.minecraft.class_1309;
import net.minecraft.class_310;
import net.minecraft.class_922;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

@Mixin(class_922.class)
public abstract class LivingEntityRendererNameTagMixin {
    @Inject(
            method = "method_4055(Lnet/minecraft/class_1309;D)Z",
            at = @At("HEAD"),
            cancellable = true,
            require = 0
    )
    private void ericbakeraddons$showOwnNameTag(class_1309 entity, double distanceSquared, CallbackInfoReturnable<Boolean> cir) {
        class_310 client = class_310.method_1551();
        if (entity == client.field_1724 && ChatCommands.isShowNameTagEnabled()) {
            cir.setReturnValue(class_310.method_1498());
        }
    }
}
