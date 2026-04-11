package com.example.mixins;
import com.example.chat.ChatRewriter;
import net.minecraft.class_2561;
import net.minecraft.class_640;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.ModifyVariable;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;
@Mixin({class_640.class})
public class PlayerInfoDisplayNameMixin
{
@ModifyVariable(method = {"method_2962(Lnet/minecraft/class_2561;)V"}, at = @At("HEAD"), argsOnly = true, require = 0)
private class_2561 rewriteIncomingTabDisplayName(class_2561 displayName) {
if (displayName == null) {
return null;
}
try {
return ChatRewriter.rewriteAll(displayName);
} catch (NoClassDefFoundError err) {
return displayName;
}
}
@Inject(method = {"method_2971()Lnet/minecraft/class_2561;"}, at = {@At("RETURN")}, cancellable = true, require = 0)
private void rewriteOutgoingTabDisplayName(CallbackInfoReturnable<class_2561> cir) {
class_2561 displayName = (class_2561)cir.getReturnValue();
if (displayName == null) {
return;
}
try {
cir.setReturnValue(ChatRewriter.rewriteAll(displayName));
} catch (NoClassDefFoundError err) {
cir.setReturnValue(displayName);
}
}
}

