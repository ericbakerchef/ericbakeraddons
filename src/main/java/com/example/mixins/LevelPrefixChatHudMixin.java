package com.example.mixins;

import com.example.mixinmod.LevelPrefixState;
import net.minecraft.class_2561;
import net.minecraft.class_338;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.ModifyVariable;

@Mixin(class_338.class)
public abstract class LevelPrefixChatHudMixin {
    @ModifyVariable(
            method = "method_1812(Lnet/minecraft/class_2561;)V",
            at = @At("HEAD"),
            argsOnly = true,
            index = 1,
            require = 0
    )
    private class_2561 rewriteChatMessage(class_2561 input) {
        class_2561 rewritten = LevelPrefixState.rewriteText(input);
        return rewritten == null ? input : rewritten;
    }

    @ModifyVariable(
            method = "method_44811(Lnet/minecraft/class_2561;Lnet/minecraft/class_7469;Lnet/minecraft/class_7591;)V",
            at = @At("HEAD"),
            argsOnly = true,
            index = 1,
            require = 0
    )
    private class_2561 rewriteSignedChatMessage(class_2561 input) {
        class_2561 rewritten = LevelPrefixState.rewriteText(input);
        return rewritten == null ? input : rewritten;
    }
}
