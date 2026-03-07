/*    */ package com.example;
/*    */ 
/*    */ import com.example.chat.ChatRewriter;
/*    */ import com.example.module.impl.ChatCommands;
/*    */ import com.example.module.impl.TitaniumEsp;
/*    */ import com.ricedotwho.rsm.addon.Addon;
/*    */ import com.ricedotwho.rsm.command.Command;
/*    */ import com.ricedotwho.rsm.component.api.ModComponent;
/*    */ import com.ricedotwho.rsm.module.Module;
/*    */ import com.ricedotwho.rsm.utils.ChatUtils;
/*    */ import java.util.List;
/*    */ 
/*    */ public class ericbakeraddons
/*    */   implements Addon {
/*    */   public void onInitialize() {
/* 27 */     ChatRewriter.registerHooks();
/* 28 */     ChatUtils.chat("ericbakeraddons loaded gg its over for you", new Object[0]);
/*    */   }
/*    */ 
/*    */   
/*    */   public void onUnload() {
/* 33 */     ChatUtils.chat("ericbakeraddons unloaded, you're saved", new Object[0]);
/*    */   }
/*    */ 
/*    */   
/*    */   public List<Class<? extends Module>> getModules() {
/* 38 */     return (List)List.of(ChatCommands.class, TitaniumEsp.class);
/*    */   }
/*    */ 
/*    */   
/*    */   public List<Class<? extends ModComponent>> getComponents() {
/* 43 */     return List.of();
/*    */   }
/*    */ 
/*    */   
/*    */   public List<Class<? extends Command>> getCommands() {
/* 48 */     return List.of();
/*    */   }
/*    */ }


/* Location:              F:\exampleaddon-1.0.0.jar!\com\example\ericbakeraddons.class
 * Java compiler version: 21 (65.0)
 * JD-Core Version:       1.1.3
 */
