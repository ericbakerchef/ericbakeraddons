/*    */ package com.example.mixins;
/*    */ 
/*    */ import com.example.module.impl.ChatCommands;
/*    */ import java.lang.reflect.Field;
/*    */ import net.minecraft.class_2558;
/*    */ import net.minecraft.class_2583;
/*    */ import net.minecraft.class_437;
/*    */ import org.spongepowered.asm.mixin.Mixin;
/*    */ import org.spongepowered.asm.mixin.injection.At;
/*    */ import org.spongepowered.asm.mixin.injection.Inject;
/*    */ import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;
/*    */ 
/*    */ @Mixin({class_437.class})
/*    */ public class ScreenClickMixin {
/*    */   @Inject(method = {"method_25430(Lnet/minecraft/class_2583;)Z"}, at = {@At("HEAD")}, cancellable = true)
/*    */   private void interceptInternalSsidConfirmClick(class_2583 style, CallbackInfoReturnable<Boolean> cir) {
/* 17 */     if (style == null)
/* 18 */       return;  class_2558 clickEvent = style.method_10970();
/* 19 */     if (clickEvent == null)
/*    */       return; 
/* 21 */     if (containsToken(clickEvent, "__ssid_confirm_internal__") && 
/* 22 */       ChatCommands.confirmPendingSsidFromClick()) {
/* 23 */       cir.setReturnValue(Boolean.valueOf(true));
/* 24 */       cir.cancel();
/*    */     } 
/*    */   }
/*    */ 
/*    */   
/*    */   private boolean containsToken(class_2558 event, String token) {
/* 30 */     if (event == null || token == null) return false; 
/* 31 */     if (event.toString().contains(token)) return true; 
/*    */     try {
/* 33 */       for (Field field : event.getClass().getDeclaredFields()) {
/* 34 */         field.setAccessible(true);
/* 35 */         Object value = field.get(event);
/* 36 */         if (value instanceof String) { String str = (String)value; if (str.contains(token))
/* 37 */             return true;  }
/*    */       
/*    */       } 
/* 40 */     } catch (Exception exception) {}
/*    */     
/* 42 */     return false;
/*    */   }
/*    */ }


/* Location:              F:\exampleaddon-1.0.0.jar!\com\example\mixins\ScreenClickMixin.class
 * Java compiler version: 21 (65.0)
 * JD-Core Version:       1.1.3
 */