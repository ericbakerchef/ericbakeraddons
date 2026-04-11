package com.example.mixins;
import com.example.mixinmod.ChatCommandsBridge;
import java.lang.reflect.Field;
import net.minecraft.class_2558;
import net.minecraft.class_2583;
import net.minecraft.class_437;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;
@Mixin({class_437.class})
public class ScreenClickMixin {
@Inject(method = {"method_25430(Lnet/minecraft/class_2583;)Z"}, at = {@At("HEAD")}, cancellable = true)
private void interceptInternalSsidConfirmClick(class_2583 style, CallbackInfoReturnable<Boolean> cir) {
if (style == null)
return;  class_2558 clickEvent = style.method_10970();
if (clickEvent == null)
return; 
if (containsToken(clickEvent, "__ssid_confirm_internal__") && 
ChatCommandsBridge.confirmPendingSsidFromClick()) {
cir.setReturnValue(Boolean.valueOf(true));
cir.cancel();
} 
}
private boolean containsToken(class_2558 event, String token) {
if (event == null || token == null) return false; 
if (event.toString().contains(token)) return true; 
try {
for (Field field : event.getClass().getDeclaredFields()) {
field.setAccessible(true);
Object value = field.get(event);
if (value instanceof String) { String str = (String)value; if (str.contains(token))
return true;  }
} 
} catch (Exception exception) {}
return false;
}
}

