/*    */ package com.example.mixins;
/*    */ 
/*    */ import com.example.chat.ChatRewriter;
/*    */ import net.minecraft.class_2561;
/*    */ import net.minecraft.class_355;
/*    */ import net.minecraft.class_5250;
/*    */ import net.minecraft.class_640;
/*    */ import org.spongepowered.asm.mixin.Mixin;
/*    */ import org.spongepowered.asm.mixin.injection.At;
/*    */ import org.spongepowered.asm.mixin.injection.Inject;
/*    */ import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;
/*    */ 
/*    */ @Mixin({class_355.class})
/*    */ public class TabListNameMixin
/*    */ {
/*    */   @Inject(method = {"method_27538(Lnet/minecraft/class_640;Lnet/minecraft/class_5250;)Lnet/minecraft/class_2561;"}, at = {@At("RETURN")}, cancellable = true, require = 0)
/*    */   private void rewriteDecoratedTabName(class_640 playerInfo, class_5250 name, CallbackInfoReturnable<class_2561> cir) {
/* 18 */     class_2561 text = (class_2561)cir.getReturnValue();
/* 19 */     if (text == null) {
/*    */       return;
/*    */     }
/* 22 */     cir.setReturnValue(ChatRewriter.rewriteAll(text));
/*    */   }
/*    */   
/*    */   @Inject(method = {"method_1918(Lnet/minecraft/class_640;)Lnet/minecraft/class_2561;"}, at = {@At("RETURN")}, cancellable = true, require = 0)
/*    */   private void rewriteNameForDisplay(class_640 playerInfo, CallbackInfoReturnable<class_2561> cir) {
/* 27 */     class_2561 text = (class_2561)cir.getReturnValue();
/* 28 */     if (text == null) {
/*    */       return;
/*    */     }
/* 31 */     cir.setReturnValue(ChatRewriter.rewriteAll(text));
/*    */   }
/*    */ }


/* Location:              F:\exampleaddon-1.0.0.jar!\com\example\mixins\TabListNameMixin.class
 * Java compiler version: 21 (65.0)
 * JD-Core Version:       1.1.3
 */