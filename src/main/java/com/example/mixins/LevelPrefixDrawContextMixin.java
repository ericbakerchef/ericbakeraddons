package com.example.mixins;

import com.example.mixinmod.LevelPrefixState;
import java.util.ArrayList;
import java.util.List;
import net.minecraft.class_2561;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.ModifyVariable;

@Mixin({net.minecraft.class_332.class})
public abstract class LevelPrefixDrawContextMixin {
    @ModifyVariable(
            method = {
                    "method_27534(Lnet/minecraft/class_327;Lnet/minecraft/class_2561;III)V",
                    "method_27535(Lnet/minecraft/class_327;Lnet/minecraft/class_2561;III)V",
                    "method_51439(Lnet/minecraft/class_327;Lnet/minecraft/class_2561;IIIZ)V",
                    "method_60649(Lnet/minecraft/class_327;Lnet/minecraft/class_2561;IIII)V",
                    "method_51438(Lnet/minecraft/class_327;Lnet/minecraft/class_2561;II)V",
                    "method_64235(Lnet/minecraft/class_327;Lnet/minecraft/class_2561;IILnet/minecraft/class_2960;)V"
            },
            at = @At("HEAD"),
            argsOnly = true,
            index = 2,
            require = 0
    )
    private class_2561 rewriteTextArg(class_2561 input) {
        class_2561 rewritten = LevelPrefixState.rewriteText(input);
        return rewritten == null ? input : rewritten;
    }

    @ModifyVariable(
            method = {"method_71276(Lnet/minecraft/class_2561;II)V"},
            at = @At("HEAD"),
            argsOnly = true,
            index = 1,
            require = 0
    )
    private class_2561 rewriteTextArgSimple(class_2561 input) {
        class_2561 rewritten = LevelPrefixState.rewriteText(input);
        return rewritten == null ? input : rewritten;
    }

    @ModifyVariable(
            method = {
                    "method_51434(Lnet/minecraft/class_327;Ljava/util/List;II)V",
                    "method_64037(Lnet/minecraft/class_327;Ljava/util/List;IILnet/minecraft/class_2960;)V",
                    "method_64038(Lnet/minecraft/class_327;Ljava/util/List;Ljava/util/Optional;II)V",
                    "method_51437(Lnet/minecraft/class_327;Ljava/util/List;Ljava/util/Optional;IILnet/minecraft/class_2960;)V"
            },
            at = @At("HEAD"),
            argsOnly = true,
            index = 2,
            require = 0
    )
    private List<class_2561> rewriteTextList(List<class_2561> input) {
        if (input == null || input.isEmpty()) {
            return input;
        }
        List<class_2561> out = new ArrayList<>(input.size());
        boolean changed = false;
        for (class_2561 text : input) {
            class_2561 rewritten = LevelPrefixState.rewriteText(text);
            if (rewritten != null) {
                out.add(rewritten);
                changed = true;
            } else {
                out.add(text);
            }
        }
        return changed ? out : input;
    }
}
