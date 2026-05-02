package com.example.mixins;

import com.example.mixinmod.FuckCoalState;
import net.minecraft.class_2350;
import net.minecraft.class_259;
import net.minecraft.class_265;
import net.minecraft.class_2680;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

@Mixin(targets = "net.minecraft.class_4970$class_4971")
public class AbstractBlockStateCoalCullingMixin {

    @Inject(method = "method_26173", at = @At("HEAD"), cancellable = true, require = 0)
    private void ericbakeraddons$emptyDoorCullingFace(class_2350 direction, CallbackInfoReturnable<class_265> cir) {
        class_2680 self = (class_2680) (Object) this;
        if (FuckCoalState.isBlockActive(self.method_26204())) {
            cir.setReturnValue(class_259.method_1073());
        }
    }

    @Inject(method = "method_26201", at = @At("HEAD"), cancellable = true, require = 0)
    private void ericbakeraddons$emptyDoorCullingShape(CallbackInfoReturnable<class_265> cir) {
        class_2680 self = (class_2680) (Object) this;
        if (FuckCoalState.isBlockActive(self.method_26204())) {
            cir.setReturnValue(class_259.method_1073());
        }
    }

    @Inject(method = "method_26216", at = @At("HEAD"), cancellable = true, require = 0)
    private void ericbakeraddons$doorNotOpaqueFullCube(CallbackInfoReturnable<Boolean> cir) {
        class_2680 self = (class_2680) (Object) this;
        if (FuckCoalState.isBlockActive(self.method_26204())) {
            cir.setReturnValue(false);
        }
    }
}
