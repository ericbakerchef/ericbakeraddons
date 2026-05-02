package com.example.mixins;

import com.example.mixinmod.FuckCoalState;
import net.minecraft.class_11515;
import net.minecraft.class_2680;
import net.minecraft.class_4696;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

@Mixin(class_4696.class)
public class RenderLayersCoalGlassMixin {

    @Inject(method = "method_23679", at = @At("HEAD"), cancellable = true, require = 0)
    private static void ericbakeraddons$doorRendersOnCutout(class_2680 state, CallbackInfoReturnable<class_11515> cir) {
        if (FuckCoalState.isBlockActive(state.method_26204())) {
            cir.setReturnValue(class_11515.field_60925);
        }
    }
}
