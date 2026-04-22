package com.example.mixins;

import com.example.mixinmod.TextShadowState;
import net.minecraft.class_327;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.ModifyVariable;

@Mixin(class_327.class)
public abstract class TextRendererShadowMixin {
    @ModifyVariable(
            method = "method_71795(Lnet/minecraft/class_5481;FFIZI)Lnet/minecraft/class_327$class_11465;",
            at = @At("HEAD"),
            argsOnly = true,
            index = 5,
            require = 0
    )
    private boolean ericbakeraddons$removeSequenceShadow(boolean shadow) {
        return TextShadowState.isEnabled() ? false : shadow;
    }

    @ModifyVariable(
            method = "method_71796(Ljava/lang/String;FFIZI)Lnet/minecraft/class_327$class_11465;",
            at = @At("HEAD"),
            argsOnly = true,
            index = 5,
            require = 0
    )
    private boolean ericbakeraddons$removeStringShadow(boolean shadow) {
        return TextShadowState.isEnabled() ? false : shadow;
    }
}
