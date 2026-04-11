package com.example.mixins;
import com.example.chat.ChatRewriter;
import net.minecraft.class_2561;
import net.minecraft.class_355;
import net.minecraft.class_5250;
import net.minecraft.class_640;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;
@Mixin({class_355.class})
public class TabListNameMixin
{
@Inject(method = {"method_27538(Lnet/minecraft/class_640;Lnet/minecraft/class_5250;)Lnet/minecraft/class_2561;"}, at = {@At("RETURN")}, cancellable = true, require = 0)
private void rewriteDecoratedTabName(class_640 playerInfo, class_5250 name, CallbackInfoReturnable<class_2561> cir) {
class_2561 text = (class_2561)cir.getReturnValue();
if (text == null) {
return;
}
try {
cir.setReturnValue(ChatRewriter.rewriteAll(text));
} catch (NoClassDefFoundError err) {
cir.setReturnValue(text);
}
}
@Inject(method = {"method_1918(Lnet/minecraft/class_640;)Lnet/minecraft/class_2561;"}, at = {@At("RETURN")}, cancellable = true, require = 0)
private void rewriteNameForDisplay(class_640 playerInfo, CallbackInfoReturnable<class_2561> cir) {
class_2561 text = (class_2561)cir.getReturnValue();
if (text == null) {
return;
}
try {
cir.setReturnValue(ChatRewriter.rewriteAll(text));
} catch (NoClassDefFoundError err) {
cir.setReturnValue(text);
}
}
}

