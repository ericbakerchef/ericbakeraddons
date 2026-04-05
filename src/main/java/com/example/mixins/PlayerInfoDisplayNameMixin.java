/*    */ package com.example.mixins;
/*    */ 
/*    */ import com.example.chat.ChatRewriter;
/*    */ import net.minecraft.class_2561;
/*    */ import net.minecraft.class_640;
/*    */ import org.spongepowered.asm.mixin.Mixin;
/*    */ import org.spongepowered.asm.mixin.injection.At;
/*    */ import org.spongepowered.asm.mixin.injection.Inject;
/*    */ import org.spongepowered.asm.mixin.injection.ModifyVariable;
/*    */ import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;
/*    */ 
/*    */ @Mixin({class_640.class})
/*    */ public class PlayerInfoDisplayNameMixin
/*    */ {
/*    */   @ModifyVariable(method = {"method_2962(Lnet/minecraft/class_2561;)V"}, at = @At("HEAD"), argsOnly = true, require = 0)
/*    */   private class_2561 rewriteIncomingTabDisplayName(class_2561 displayName) {
/* 17 */     if (displayName == null) {
/* 18 */       return null;
/*    */     }
/* 20 */     try {
/* 21 */       return ChatRewriter.rewriteAll(displayName);
/* 22 */     } catch (NoClassDefFoundError err) {
/* 23 */       return displayName;
/*    */     }
/*    */   }
/*    */   
/*    */   @Inject(method = {"method_2971()Lnet/minecraft/class_2561;"}, at = {@At("RETURN")}, cancellable = true, require = 0)
/*    */   private void rewriteOutgoingTabDisplayName(CallbackInfoReturnable<class_2561> cir) {
/* 25 */     class_2561 displayName = (class_2561)cir.getReturnValue();
/* 26 */     if (displayName == null) {
/*    */       return;
/*    */     }
/* 29 */     try {
/* 30 */       cir.setReturnValue(ChatRewriter.rewriteAll(displayName));
/* 31 */     } catch (NoClassDefFoundError err) {
/* 32 */       cir.setReturnValue(displayName);
/*    */     }
/*    */   }
/*    */ }


/* Location:              F:\exampleaddon-1.0.0.jar!\com\example\mixins\PlayerInfoDisplayNameMixin.class
 * Java compiler version: 21 (65.0)
 * JD-Core Version:       1.1.3
 */
