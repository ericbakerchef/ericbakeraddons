package com.example.mixins;

import com.example.mixinmod.ScrollableTooltipState;
import net.minecraft.class_1703;
import net.minecraft.class_1735;
import net.minecraft.class_332;
import net.minecraft.class_437;
import net.minecraft.class_465;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

@Mixin({class_465.class})
public abstract class ScrollableTooltipContainerMixin<T extends class_1703> extends class_437 {
    @Shadow
    protected class_1735 field_2787;

    protected ScrollableTooltipContainerMixin() {
        super(null);
    }

    @Inject(method = {"method_2380(Lnet/minecraft/class_332;II)V"}, at = {@At("HEAD")})
    private void onTooltipRenderStart(class_332 drawContext, int mouseX, int mouseY, CallbackInfo ci) {
        ScrollableTooltipState.beginRender(this, this.field_2787);
    }

    @Inject(method = {"method_2380(Lnet/minecraft/class_332;II)V"}, at = {@At("TAIL")})
    private void onTooltipRenderEnd(class_332 drawContext, int mouseX, int mouseY, CallbackInfo ci) {
        ScrollableTooltipState.endRender();
    }

    @Inject(method = {"method_25401(DDDD)Z"}, at = {@At("TAIL")})
    private void onMouseScrolled(double mouseX, double mouseY, double horizontalAmount, double verticalAmount, CallbackInfoReturnable<Boolean> cir) {
        ScrollableTooltipState.onMouseScrolled(this, this.field_2787, horizontalAmount, verticalAmount);
    }
}
