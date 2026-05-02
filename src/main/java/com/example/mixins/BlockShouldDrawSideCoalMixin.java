package com.example.mixins;

import com.example.mixinmod.FuckCoalState;
import net.minecraft.class_2248;
import net.minecraft.class_2350;
import net.minecraft.class_2680;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

@Mixin(class_2248.class)
public class BlockShouldDrawSideCoalMixin {

    @Inject(method = "method_9607", at = @At("HEAD"), cancellable = true, require = 0)
    private static void ericbakeraddons$doorSideHandling(
        class_2680 state,
        class_2680 otherState,
        class_2350 side,
        CallbackInfoReturnable<Boolean> cir
    ) {
        if (!FuckCoalState.isEnabled()) {
            return;
        }
        class_2248 stateBlock = state.method_26204();
        class_2248 otherBlock = otherState.method_26204();
        boolean stateActive = FuckCoalState.isBlockActive(stateBlock);
        boolean otherActive = FuckCoalState.isBlockActive(otherBlock);
        if (stateActive && otherActive && stateBlock == otherBlock) {
            cir.setReturnValue(false);
        } else if (otherActive && !stateActive) {
            cir.setReturnValue(true);
        }
    }
}
