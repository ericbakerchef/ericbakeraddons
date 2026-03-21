package com.example.mixins;

import com.example.mixinmod.ScrollableTooltipState;
import net.minecraft.class_332;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.ModifyVariable;

@Mixin({class_332.class})
public abstract class ScrollableTooltipDrawContextMixin {
    @ModifyVariable(
            method = {"method_51435(Lnet/minecraft/class_327;Ljava/util/List;IILnet/minecraft/class_8000;Lnet/minecraft/class_2960;)V"},
            at = @At(value = "STORE", ordinal = 0),
            index = 12,
            require = 0
    )
    private int adjustTooltipX(int x) {
        return x + ScrollableTooltipState.getXOffset();
    }

    @ModifyVariable(
            method = {"method_51435(Lnet/minecraft/class_327;Ljava/util/List;IILnet/minecraft/class_8000;Lnet/minecraft/class_2960;)V"},
            at = @At(value = "STORE", ordinal = 0),
            index = 13,
            require = 0
    )
    private int adjustTooltipY(int y) {
        return y + ScrollableTooltipState.getYOffset();
    }
}
