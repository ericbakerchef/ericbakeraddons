/*     */ package com.example.chat;
/*     */ 
/*     */ import com.example.module.impl.ChatCommands;
/*     */ import java.util.regex.Matcher;
/*     */ import java.util.regex.Pattern;
/*     */ import net.fabricmc.fabric.api.client.message.v1.ClientReceiveMessageEvents;
/*     */ import net.minecraft.class_124;
/*     */ import net.minecraft.class_2561;
/*     */ import net.minecraft.class_5250;
/*     */ 
/*     */ 
/*     */ public final class ChatRewriter
/*     */ {
/*     */   private static boolean registered = false;
/*  15 */   private static final Pattern BRACKET_NUMBER = Pattern.compile("\\[(\\d{3})\\]");
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*     */   public static void registerHooks() {
/*  23 */     if (registered) {
/*     */       return;
/*     */     }
/*     */     
/*  27 */     ClientReceiveMessageEvents.MODIFY_GAME.register((text, overlay) -> rewriteAll(text));
/*     */ 
/*     */     
/*  30 */     registered = true;
/*     */   }
/*     */   
/*     */   public static class_2561 rewriteAll(class_2561 input) {
/*  34 */     if (input == null) {
/*  35 */       return null;
/*     */     }
/*  37 */     String raw = input.getString();
/*  38 */     class_2561 rewritten = rewriteText(raw);
/*  39 */     return (rewritten == null) ? input : rewritten;
/*     */   }
/*     */   
/*     */   public static class_2561 rewriteBracketRanges(class_2561 input) {
/*  43 */     return rewriteAll(input);
/*     */   }
/*     */   
/*     */   private static class_2561 rewriteText(String raw) {
/*  47 */     boolean rewriteBrackets = ChatCommands.isLevelPrefixEnabled();
/*  48 */     if (!rewriteBrackets) {
/*  53 */       return null;
/*     */     }
/*     */     
/*  64 */     class_5250 out = class_2561.method_43473();
/*  65 */     int index = 0;
/*  66 */     Matcher matcher = BRACKET_NUMBER.matcher(raw);
/*  67 */     boolean changedBrackets = false;
/*     */     
/*  70 */     while (matcher.find()) {
/*  71 */       class_124 bracketColor; int value = Integer.parseInt(matcher.group(1));
/*  72 */       if (value < 480 || value > 559) {
/*     */         continue;
/*     */       }
/*     */ 
/*     */       
/*  77 */       if (value <= 519) {
/*  78 */         bracketColor = ChatCommands.isGoldBracketsEnabled() ? class_124.field_1065 : class_124.field_1063;
/*     */       } else {
/*  80 */         bracketColor = ChatCommands.isDiamondBracketsEnabled() ? class_124.field_1075 : class_124.field_1063;
/*     */       } 
/*     */       
/*  83 */       class_124 numberColor = ChatCommands.isRed480PlusEnabled() ? class_124.field_1061 : class_124.field_1079;
/*  84 */       class_5250 replacement = buildReplacement(value, bracketColor, numberColor);
/*     */       
/*  86 */       changedBrackets = true;
/*  87 */       int start = matcher.start();
/*  88 */       int end = matcher.end();
/*     */       
/*  90 */       if (start > index) {
/*  91 */         String segment = raw.substring(index, start);
/*  96 */         out.method_10852((class_2561)class_2561.method_43470(segment));
/*     */       } 
/*     */       
/*  99 */       out.method_10852((class_2561)replacement.method_27661());
/*     */       
/* 101 */       index = end;
/*     */     } 
/*     */     
/* 104 */     if (index < raw.length()) {
/* 105 */       String segment = raw.substring(index);
/* 110 */       out.method_10852((class_2561)class_2561.method_43470(segment));
/*     */     } 
/*     */     
/* 113 */     if (!changedBrackets) {
/* 114 */       return null;
/*     */     }
/*     */     
/* 117 */     return (class_2561)out;
/*     */   }
/*     */   
/*     */   private static class_5250 buildReplacement(int value, class_124 bracketColor, class_124 numberColor) {
/* 132 */     class_5250 replacement = class_2561.method_43470("");
/* 133 */     replacement.method_10852((class_2561)class_2561.method_43470("[").method_27692(bracketColor));
/* 134 */     replacement.method_10852((class_2561)class_2561.method_43470(String.valueOf(value)).method_27692(numberColor));
/* 135 */     replacement.method_10852((class_2561)class_2561.method_43470("]").method_27692(bracketColor));
/* 136 */     return replacement;
/*     */   }
/*     */ }


/* Location:              F:\exampleaddon-1.0.0.jar!\com\example\chat\ChatRewriter.class
 * Java compiler version: 21 (65.0)
 * JD-Core Version:       1.1.3
 */
