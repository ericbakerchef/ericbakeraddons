/*     */ package com.example.module.impl;
/*     */ 
/*     */ import com.mojang.authlib.GameProfile;
/*     */ import com.mojang.authlib.properties.Property;
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
/*     */ import com.ricedotwho.rsm.ui.clickgui.settings.impl.ColourSetting;
/*     */ import com.ricedotwho.rsm.ui.clickgui.settings.impl.DragSetting;
/*     */ import com.ricedotwho.rsm.ui.clickgui.settings.impl.ModeSetting;
/*     */ import com.ricedotwho.rsm.ui.clickgui.settings.impl.MultiBoolSetting;
/*     */ import com.ricedotwho.rsm.ui.clickgui.settings.impl.NumberSetting;
/*     */ import com.ricedotwho.rsm.ui.clickgui.settings.impl.StringSetting;
/*     */ import com.ricedotwho.rsm.utils.ChatUtils;
/*     */ import com.ricedotwho.rsm.utils.render.render2d.Gradient;
/*     */ import com.ricedotwho.rsm.utils.render.render2d.NVGUtils;
/*     */ import com.example.mixinmod.ChatCommandsBridge;
/*     */ import com.example.mixinmod.LevelPrefixState;
/*     */ import com.example.mixinmod.ScrollableTooltipState;
/*     */ import com.example.mixinmod.TextShadowState;
/*     */ import java.math.BigDecimal;
/*     */ import java.net.URI;
/*     */ import java.net.http.HttpClient;
/*     */ import java.net.http.HttpRequest;
/*     */ import java.net.http.HttpResponse;
/*     */ import java.nio.charset.StandardCharsets;
/*     */ import java.lang.reflect.Constructor;
/*     */ import java.lang.reflect.Field;
/*     */ import java.lang.reflect.Method;
/*     */ import java.util.ArrayDeque;
/*     */ import java.util.ArrayList;
/*     */ import java.util.Base64;
/*     */ import java.util.Collections;
/*     */ import java.util.HashMap;
/*     */ import java.util.HashSet;
/*     */ import java.util.IdentityHashMap;
/*     */ import java.util.Iterator;
/*     */ import java.util.LinkedHashMap;
/*     */ import java.util.List;
/*     */ import java.util.Locale;
/*     */ import java.util.Map;
/*     */ import java.util.Set;
/*     */ import java.util.concurrent.CompletableFuture;
/*     */ import java.util.concurrent.ThreadLocalRandom;
/*     */ import java.util.concurrent.TimeUnit;
/*     */ import java.util.regex.Matcher;
/*     */ import java.util.regex.Pattern;
/*     */ import net.fabricmc.fabric.api.client.screen.v1.ScreenEvents;
/*     */ import net.fabricmc.fabric.api.client.screen.v1.ScreenMouseEvents;
/*     */ import net.minecraft.class_124;
/*     */ import net.minecraft.class_1304;
/*     */ import net.minecraft.class_1531;
/*     */ import net.minecraft.class_1657;
/*     */ import net.minecraft.class_1661;
/*     */ import net.minecraft.class_1937;
/*     */ import net.minecraft.class_2246;
/*     */ import net.minecraft.class_332;
/*     */ import net.minecraft.class_465;
/*     */ import net.minecraft.class_1799;
/*     */ import net.minecraft.class_2338;
/*     */ import net.minecraft.class_238;
/*     */ import net.minecraft.class_239;
/*     */ import net.minecraft.class_243;
/*     */ import net.minecraft.class_2561;
/*     */ import net.minecraft.class_2583;
/*     */ import net.minecraft.class_266;
/*     */ import net.minecraft.class_268;
/*     */ import net.minecraft.class_269;
/*     */ import net.minecraft.class_2680;
/*     */ import net.minecraft.class_310;
/*     */ import net.minecraft.class_437;
/*     */ import net.minecraft.class_355;
/*     */ import net.minecraft.class_3965;
/*     */ import net.minecraft.class_634;
/*     */ import net.minecraft.class_640;
/*     */ import net.minecraft.class_8646;
/*     */ import net.minecraft.class_9015;
/*     */ import net.minecraft.class_9296;
/*     */ import net.minecraft.class_9334;
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
/*  52 */   private static final List<String> USELESS_TIP_MESSAGES = List.of("Slow down! You can only use /tip every few seconds.", "Slow down! You can only use /tip all every few seconds.", "You are sending commands too fast! Please slow down.", "You already tipped everyone that has boosters active, so there isn't anybody to be tipped right now!", "No one has a network booster active right now! Try again later.");
/*  52 */   private static final List<Pattern> USELESS_TIP_MESSAGE_PATTERNS = List.of(
/*  52 */       Pattern.compile("^slow down you can only use /tip(?: all)? every few seconds$", Pattern.CASE_INSENSITIVE),
/*  52 */       Pattern.compile("^you are sending commands too fast please slow down$", Pattern.CASE_INSENSITIVE),
/*  52 */       Pattern.compile("^you already tipped everyone that has boosters active so there(?: isn t| is not) anybody to be tipped right now$", Pattern.CASE_INSENSITIVE),
/*  52 */       Pattern.compile("^no one has a network booster active right now try again later$", Pattern.CASE_INSENSITIVE));
/*  52 */   private static final String GROK_COMMAND = "!grok";
/*  52 */   private static final String[] GROK_RESPONSES = new String[] { "It is certain", "It is decidedly so", "Without a doubt", "Yes definitely", "You may rely on it", "As I see it, yes", "Most likely", "Outlook good", "Yes", "Signs point to yes", "Reply hazy try again", "Ask again later", "Better not tell you now", "Cannot predict now", "Concentrate and ask again", "Don't count on it", "My reply is no", "My sources say no", "Outlook not so good", "Very doubtful" };
/*  52 */   private static final List<String> AUTO_MEOW_TRIGGERS = List.of("meow", "mrow", "mrrow", "purr", "mrrp", "nya", "nyah");
/*  52 */   private static final String[] AUTO_MEOW_RESPONSES = new String[] { "mroww", "purr", "meowwwwww", "meow :3", "mrow", "moew", "mrow :3", "purrr :3" };
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
/*  64 */   private static final Colour CHEST_FILL_COLOUR = new Colour(255, 190, 70, 60);
/*  64 */   private static final Colour CHEST_OUTLINE_COLOUR = new Colour(255, 220, 120, 200);
/*  64 */   private static final Colour AUTOMATON_FILL_COLOUR = new Colour(170, 170, 180, 55);
/*  64 */   private static final Colour AUTOMATON_OUTLINE_COLOUR = new Colour(215, 215, 230, 200);
/*  64 */   private static final Colour HIDEONLEAF_FILL_COLOUR = new Colour(30, 120, 30, 55);
/*  64 */   private static final Colour HIDEONLEAF_OUTLINE_COLOUR = new Colour(60, 170, 60, 190);
/*  65 */   private static final Colour CUSTOM_FILL_COLOUR = new Colour(255, 120, 50, 120);
/*  66 */   private static final Colour CUSTOM_OUTLINE_COLOUR = new Colour(255, 195, 90, 255);
/*  67 */   private static final double CUSTOM_BOX_PADDING = 0.08D;
/*  68 */   private static final double CUSTOM_BOX_SMOOTHING = 0.45D;
/*  68 */   private static final Colour TEMPLE_SKIP_DEFAULT_COLOUR = new Colour(127, 0, 255, 255);
/*  68 */   private static final String TEMPLE_SKIP_GUARDIAN_NAME = "Kalhuiki Door Guardian";
/*  68 */   private static final String JUNGLE_TEMPLE_AREA = "Jungle Temple";
/*  68 */   private static final int TEMPLE_SKIP_SCAN_INTERVAL_TICKS = 10;
/*  69 */   private static final int ESP_SCAN_INTERVAL_TICKS = 10;
/*  70 */   private static final int ESP_SCAN_IDLE_INTERVAL_TICKS = 40;
/*  70 */   private static final int ESP_SCAN_SLICE_HEIGHT = 4;
/*  70 */   private static final int ESP_SCAN_MOVE_THRESHOLD_BLOCKS = 4;
/*  70 */   private static final int CUSTOM_HIGHLIGHT_SCAN_INTERVAL_TICKS = 5;
/*  70 */   private static final int TOOLTIP_SCROLL_LIMIT = 240;
/*  71 */   private static final byte MATCH_NONE = 0;
/*  72 */   private static final byte MATCH_TITANIUM = 1;
/*  73 */   private static final byte MATCH_NODE = 2;
/*  73 */   private static final byte MATCH_CHEST = 4;
/*  74 */   private static final Pattern HEALTH_FRACTION_PATTERN = Pattern.compile("(?<!\\d)(\\d+)\\s*/\\s*(\\d+)(?!\\d)");
/*  75 */   private static final Pattern COMMISSION_PERCENT_PATTERN = Pattern.compile("(?<!\\d)(\\d+(?:\\.\\d+)?)\\s*%");
/*  75 */   private static final Pattern PICKAXE_ABILITY_USED_PATTERN = Pattern.compile("You used your\\s+(.+?)\\s+Pickaxe Ability!", Pattern.CASE_INSENSITIVE);
/*  75 */   private static final Pattern PICKAXE_ABILITY_COOLDOWN_PATTERN = Pattern.compile("-(\\d+)%\\s*Pickaxe Ability Cooldown", Pattern.CASE_INSENSITIVE);
/*  75 */   private static final Pattern PET_LEVEL_PATTERN = Pattern.compile("\\b(?:lvl|level)\\s*(\\d{1,3})\\b", Pattern.CASE_INSENSITIVE);
/*  75 */   private static final Pattern BAL_PET_PATTERN = Pattern.compile("\\[\\s*lvl\\s*(\\d{1,3})\\s*\\]\\s*bal\\b", Pattern.CASE_INSENSITIVE);
/*  75 */   private static final Pattern PICKAXE_COOLDOWN_CHAT_PATTERN = Pattern.compile("Your Pickaxe ability is on cooldown for\\s*([0-9]+(?:\\.[0-9]+)?)s(?:/|\\.)?", Pattern.CASE_INSENSITIVE);
/*  75 */   private static final Pattern PICKAXE_USED_CHAT_PATTERN = Pattern.compile("You used your\\s+(.+?)\\s+Pickaxe Ability!", Pattern.CASE_INSENSITIVE);
/*  75 */   private static final Pattern PICKAXE_AVAILABLE_CHAT_PATTERN = Pattern.compile("Pickobulus\\s+is\\s+now\\s+available!", Pattern.CASE_INSENSITIVE);
/*  75 */   private static final Pattern PICKAXE_AVAILABLE_GENERIC_CHAT_PATTERN = Pattern.compile("(?:Your\\s+)?(.+?)\\s+is\\s+now\\s+available!?", Pattern.CASE_INSENSITIVE);
/*  75 */   private static final Pattern TIP_TIPPED_GAMES_PATTERN = Pattern.compile("You tipped\\s+\\d+\\s+player(?:\\(s\\)|s)?\\s+in\\s+\\d+\\s+game(?:\\(s\\)|s)!?", Pattern.CASE_INSENSITIVE);
/*  75 */   private static final Pattern TIP_TIPPED_DIFFERENT_GAMES_PATTERN = Pattern.compile("You tipped\\s+\\d+\\s+player(?:\\(s\\)|s)?\\s+in\\s+\\d+\\s+different\\s+games!?", Pattern.CASE_INSENSITIVE);
/*  75 */   private static final Pattern CHAT_SENDER_PATTERN = Pattern.compile("^(?:\\w+(?:-\\w+)?\\s>\\s)?(?:\\[[^\\]]+\\]\\s)?(?:\\S+\\s)?(?:\\[[^\\]]+\\]\\s)?([A-Za-z0-9_.-]+)(?:\\s[^\\s\\[\\]:]+)?(?:\\s\\[[^\\]]+\\])?:");
/*  75 */   private static final Pattern BLAZE_PUZZLE_NAMETAG_PATTERN = Pattern.compile("^\\[lv15\\]\\s*(?:[\\p{So}\\p{Cntrl}\\p{Punct}]\\s*)?blaze\\s+[\\d,]+/([\\d,]+).*$", Pattern.CASE_INSENSITIVE);
/*  75 */   private static final Set<String> PICKAXE_ABILITY_NAMES = Set.of("pickobulus", "mining speed boost", "maniac miner", "tunnel vision");
/*  75 */   private static final long SKY_MALL_PICKAXE_GRACE_MS = TimeUnit.SECONDS.toMillis(10L);
/*  76 */   private static final long BLAZE_BLOCK_MESSAGE_COOLDOWN_MS = 250L;
/*  76 */   private static final double BLAZE_TARGET_LOCK_RANGE = 96.0D;
/*  76 */   private static final double BLAZE_SUPPORT_SEARCH_RANGE_SQ = 9.0D;
/*  76 */   private static final double BLAZE_BOX_PADDING = 0.15D;
/*  76 */   private static final int COMMISSION_SCAN_INTERVAL_TICKS = 5;
/*  76 */   private static final double COMMISSION_ANIMATION_SPEED = 11.0D;
/*  76 */   private static final double COMMISSION_PERCENT_EPSILON = 0.05D;
/*  77 */   private static final Colour COMMISSION_PANEL_FILL = new Colour(22, 18, 34, 185);
/*  78 */   private static final Colour COMMISSION_PANEL_OUTLINE = new Colour(138, 104, 222, 235);
/*  79 */   private static final Colour COMMISSION_TITLE_COLOUR = new Colour(222, 228, 255, 255);
/*  80 */   private static final Colour COMMISSION_TEXT_DEFAULT = new Colour(245, 245, 255, 255);
/*  81 */   private static final Colour COMMISSION_TEXT_PROGRESS = new Colour(255, 247, 120, 255);
/*  82 */   private static final Colour COMMISSION_TEXT_DONE = new Colour(95, 217, 140, 255);
/*  83 */   private static final Colour COMMISSION_TEXT_ZERO = new Colour(255, 96, 124, 255);
/*  83 */   private static final Colour COMMISSION_PROGRESS_TRACK = new Colour(255, 255, 255, 36);
/*  83 */   private static final Colour COMMISSION_PROGRESS_START = new Colour(178, 99, 223, 255);
/*  83 */   private static final Colour COMMISSION_PROGRESS_END = new Colour(215, 147, 244, 255);
/*  84 */   private static final Colour RSA_R_COLOUR = new Colour(178, 99, 223, 255);
/*  85 */   private static final Colour RSA_S_COLOUR = new Colour(197, 123, 234, 255);
/*  86 */   private static final Colour RSA_A_COLOUR = new Colour(215, 147, 244, 255);
/*  86 */   private static final Colour RSM_R_COLOUR = new Colour(46, 131, 67, 255);
/*  86 */   private static final Colour RSM_S_COLOUR = new Colour(41, 168, 79, 255);
/*  86 */   private static final Colour RSM_M_COLOUR = new Colour(37, 205, 92, 255);
/*  86 */   private static final Colour COMMISSION_PANEL_OUTLINE_RSM = new Colour(46, 131, 67, 255);
/*  86 */   private static final Colour COMMISSION_PROGRESS_START_RSM = new Colour(46, 131, 67, 255);
/*  86 */   private static final Colour COMMISSION_PROGRESS_END_RSM = new Colour(37, 205, 92, 255);
/*  86 */   private static final int ODIN_EGG_SCAN_INTERVAL_TICKS = 60;
/*  86 */   private static final double ODIN_EGG_BOX_HALF_WIDTH = 0.35D;
/*  86 */   private static final double ODIN_EGG_BOX_MIN_Y = 1.05D;
/*  86 */   private static final double ODIN_EGG_BOX_MAX_Y = 1.75D;
/*  86 */   private static final double ODIN_EGG_BEAM_HEIGHT = 25.0D;
/*  86 */   private static final Pattern ODIN_EGG_MESSAGE_PATTERN = Pattern.compile(".*(?:found|collected).+Chocolate\\s+(?:Breakfast|Lunch|Dinner|Brunch|D\\u00E9jeuner|Supper).*", Pattern.CASE_INSENSITIVE);
/*  86 */   private static final Pattern ODIN_TEXTURE_URL_PATTERN = Pattern.compile("\"url\"\\s*:\\s*\"([^\"]+)\"", Pattern.CASE_INSENSITIVE);
/*     */   private record ScheduledLine(long delayMs, String command) {}
/*     */   private record BlazePuzzleTarget(net.minecraft.class_1297 hitEntity, int health) {}
/*     */   private record CommissionOverlayMetrics(float boxWidth, float boxHeight, float padding, float titleSize, float lineSize, float rsaSize, float lineGap, float barTopGap, float barHeight, float barRadius, float titleY, float bodyStartY, float rsaY, float radius, float outlineThickness) {}
/*     */   private static final class OdinEggData {
/*     */     private final int entityId;
/*     */     private final class_1531 entity;
/*     */     private final OdinEggKind kind;
/*     */     private boolean found;
/*     */ 
/*     */     private OdinEggData(int entityId, class_1531 entity, OdinEggKind kind) {
/*     */       this.entityId = entityId;
/*     */       this.entity = entity;
/*     */       this.kind = kind;
/*     */     }
/*     */   }
/*     */ 
/*     */   private enum OdinEggKind {
/*     */     BREAKFAST("Breakfast / Brunch", "ewogICJ0aW1lc3RhbXAiIDogMTcxMTQ2MjY3MzE0OSwKICAicHJvZmlsZUlkIiA6ICJiN2I4ZTlhZjEwZGE0NjFmOTY2YTQxM2RmOWJiM2U4OCIsCiAgInByb2ZpbGVOYW1lIiA6ICJBbmFiYW5hbmFZZzciLAogICJzaWduYXR1cmVSZXF1aXJlZCIgOiB0cnVlLAogICJ0ZXh0dXJlcyIgOiB7CiAgICAiU0tJTiIgOiB7CiAgICAgICJ1cmwiIDogImh0dHA6Ly90ZXh0dXJlcy5taW5lY3JhZnQubmV0L3RleHR1cmUvYTQ5MzMzZDg1YjhhMzE1ZDAzMzZlYjJkZjM3ZDhhNzE0Y2EyNGM1MWI4YzYwNzRmMWI1YjkyN2RlYjUxNmMyNCIKICAgIH0KICB9Cn0", new Colour(255, 170, 0, 45), new Colour(255, 170, 0, 220)),
/*     */     LUNCH("Lunch / D\u00E9jeuner", "ewogICJ0aW1lc3RhbXAiIDogMTcxMTQ2MjU2ODExMiwKICAicHJvZmlsZUlkIiA6ICI3NzUwYzFhNTM5M2Q0ZWQ0Yjc2NmQ4ZGUwOWY4MjU0NiIsCiAgInByb2ZpbGVOYW1lIiA6ICJSZWVkcmVsIiwKICAic2lnbmF0dXJlUmVxdWlyZWQiIDogdHJ1ZSwKICAidGV4dHVyZXMiIDogewogICAgIlNLSU4iIDogewogICAgICAidXJsIiA6ICJodHRwOi8vdGV4dHVyZXMubWluZWNyYWZ0Lm5ldC90ZXh0dXJlLzdhZTZkMmQzMWQ4MTY3YmNhZjk1MjkzYjY4YTRhY2Q4NzJkNjZlNzUxZGI1YTM0ZjJjYmM2NzY2YTAzNTZkMGEiCiAgICB9CiAgfQp9", new Colour(85, 85, 255, 45), new Colour(85, 85, 255, 220)),
/*     */     DINNER("Dinner / Supper", "ewogICJ0aW1lc3RhbXAiIDogMTcxMTQ2MjY0OTcwMSwKICAicHJvZmlsZUlkIiA6ICI3NGEwMzQxNWY1OTI0ZTA4YjMyMGM2MmU1NGE3ZjJhYiIsCiAgInByb2ZpbGVOYW1lIiA6ICJNZXp6aXIiLAogICJzaWduYXR1cmVSZXF1aXJlZCIgOiB0cnVlLAogICJ0ZXh0dXJlcyIgOiB7CiAgICAiU0tJTiIgOiB7CiAgICAgICJ1cmwiIDogImh0dHA6Ly90ZXh0dXJlcy5taW5lY3JhZnQubmV0L3RleHR1cmUvZTVlMzYxNjU4MTlmZDI4NTBmOTg1NTJlZGNkNzYzZmY5ODYzMTMxMTkyODNjMTI2YWNlMGM0Y2M0OTVlNzZhOCIKICAgIH0KICB9Cn0", new Colour(85, 255, 85, 45), new Colour(85, 255, 85, 220));
/*     */ 
/*     */     private final String label;
/*     */     private final String textureValueLower;
/*     */     private final String textureUrlLower;
/*     */     private final Colour fillColour;
/*     */     private final Colour outlineColour;
/*     */ 
/*     */     OdinEggKind(String label, String textureValue, Colour fillColour, Colour outlineColour) {
/*     */       this.label = label;
/*     */       this.textureValueLower = textureValue.toLowerCase(Locale.ROOT);
/*     */       this.textureUrlLower = decodeTextureUrl(textureValue).toLowerCase(Locale.ROOT);
/*     */       this.fillColour = fillColour;
/*     */       this.outlineColour = outlineColour;
/*     */     }
/*     */ 
/*     */     private static OdinEggKind fromStack(class_1799 stack) {
/*     */       if (stack == null || stack.method_7960()) {
/*     */         return null;
/*     */       }
/*     */       String textureValue = getSkullTextureValue(stack);
/*     */       if (!textureValue.isBlank()) {
/*     */         String textureValueLower = textureValue.toLowerCase(Locale.ROOT);
/*     */         String textureUrlLower = decodeTextureUrl(textureValue).toLowerCase(Locale.ROOT);
/*     */         for (OdinEggKind kind : values()) {
/*     */           if (textureValueLower.contains(kind.textureValueLower) || (!kind.textureUrlLower.isBlank() && textureUrlLower.contains(kind.textureUrlLower))) {
/*     */             return kind;
/*     */           }
/*     */         }
/*     */       }
/*     */       String search = (String.valueOf(stack) + ' ' + String.valueOf(stack.method_57353()) + ' ' + String.valueOf(stack.method_58658())).toLowerCase(Locale.ROOT);
/*     */       for (OdinEggKind kind : values()) {
/*     */         if (search.contains(kind.textureValueLower) || (!kind.textureUrlLower.isBlank() && search.contains(kind.textureUrlLower))) {
/*     */           return kind;
/*     */         }
/*     */       }
/*     */       return null;
/*     */     }
/*     */   }
/*     */   
/*  65 */   private final class_310 mc = class_310.method_1551(); public class_310 getMc() { return this.mc; }
/*  66 */    private final LinkedHashMap<String, Integer> commandCategories = new LinkedHashMap<>(); public LinkedHashMap<String, Integer> getCommandCategories() { return this.commandCategories; }
/*  67 */    private final LinkedHashMap<String, List<ScheduledLine>> commandResponses = new LinkedHashMap<>(); public LinkedHashMap<String, List<ScheduledLine>> getCommandResponses() { return this.commandResponses; }
/*     */   
/*  69 */   private final List<String> category1Commands = new ArrayList<>(); public List<String> getCategory1Commands() { return this.category1Commands; }
/*  70 */    private final List<String> category2Commands = new ArrayList<>(); public List<String> getCategory2Commands() { return this.category2Commands; }
/*  71 */    private final List<String> category3Commands = new ArrayList<>(); public List<String> getCategory3Commands() { return this.category3Commands; }
/*     */   
/*  74 */   private final BooleanSetting enableChatCommands = new BooleanSetting("Commands", true); public BooleanSetting getEnableChatCommands() { return this.enableChatCommands; }
/*  75 */    private final DefaultGroupSetting chatCommandSettingsGroup = new DefaultGroupSetting("Chat", this); public DefaultGroupSetting getChatCommandSettingsGroup() { return this.chatCommandSettingsGroup; }
/*  76 */    private final BooleanSetting partyChatCommandsEnabled = new BooleanSetting("- Party chat", true, this::isChatCommandSettingsVisible); public BooleanSetting getPartyChatCommandsEnabled() { return this.partyChatCommandsEnabled; }
/*  77 */    private final BooleanSetting guildChatCommandsEnabled = new BooleanSetting("- Guild chat", false, this::isChatCommandSettingsVisible); public BooleanSetting getGuildChatCommandsEnabled() { return this.guildChatCommandsEnabled; }
/*  78 */    private final BooleanSetting privateMessageChatCommandsEnabled = new BooleanSetting("- Private messages", false, this::isChatCommandSettingsVisible); public BooleanSetting getPrivateMessageChatCommandsEnabled() { return this.privateMessageChatCommandsEnabled; }
/*  78 */    private final BooleanSetting grokIntegration = new BooleanSetting("- Grok Integration", true, this::isChatCommandSettingsVisible); public BooleanSetting getGrokIntegration() { return this.grokIntegration; }
/*  78 */    private final BooleanSetting autoMeow = new BooleanSetting("- Auto meow", false, this::isChatCommandSettingsVisible); public BooleanSetting getAutoMeow() { return this.autoMeow; }
/*  78 */    private final DefaultGroupSetting levelPrefixGroup = new DefaultGroupSetting("Level prefix", this); public DefaultGroupSetting getLevelPrefixGroup() { return this.levelPrefixGroup; }
/*  79 */    private final BooleanSetting levelPrefixEnable = new BooleanSetting("Level prefix", true); public BooleanSetting getLevelPrefixEnable() { return this.levelPrefixEnable; }
/*  80 */    private final BooleanSetting red480Plus = new BooleanSetting("- Red 480+", true, () -> ((Boolean)this.levelPrefixEnable.getValue()).booleanValue()); public BooleanSetting getRed480Plus() { return this.red480Plus; }
/*  81 */    private final BooleanSetting goldBrackets = new BooleanSetting("- Gold brackets", true, () -> ((Boolean)this.levelPrefixEnable.getValue()).booleanValue()); public BooleanSetting getGoldBrackets() { return this.goldBrackets; }
/*  82 */    private final BooleanSetting diamondBrackets = new BooleanSetting("- Diamond brackets", true, () -> ((Boolean)this.levelPrefixEnable.getValue()).booleanValue()); public BooleanSetting getDiamondBrackets() { return this.diamondBrackets; }
/*  83 */    private final DefaultGroupSetting webhookGroup = new DefaultGroupSetting("Webhooks", this); public DefaultGroupSetting getWebhookGroup() { return this.webhookGroup; }
/*  84 */    private final DefaultGroupSetting accountShareGroup = new DefaultGroupSetting("Account Share", this); public DefaultGroupSetting getAccountShareGroup() { return this.accountShareGroup; }
/*  85 */    private final DefaultGroupSetting miscGroup = new DefaultGroupSetting("Misc", this); public DefaultGroupSetting getMiscGroup() { return this.miscGroup; }
/*  86 */    private final DefaultGroupSetting espGroup = new DefaultGroupSetting("ESP", this); public DefaultGroupSetting getEspGroup() { return this.espGroup; }
/*  86 */    private final DefaultGroupSetting customHighlightGroup = new DefaultGroupSetting("Custom Highlight", this); public DefaultGroupSetting getCustomHighlightGroup() { return this.customHighlightGroup; }
/*  86 */    private final DefaultGroupSetting commissionOverlayGroup = new DefaultGroupSetting("Mining", this); public DefaultGroupSetting getCommissionOverlayGroup() { return this.commissionOverlayGroup; }
/*  86 */    private final DefaultGroupSetting dungeonsGroup = new DefaultGroupSetting("Dungeons", this); public DefaultGroupSetting getDungeonsGroup() { return this.dungeonsGroup; }
/*  86 */    private final DefaultGroupSetting pickaxeAbilityCooldownGroup = new DefaultGroupSetting("Pickaxe Ability CD", this); public DefaultGroupSetting getPickaxeAbilityCooldownGroup() { return this.pickaxeAbilityCooldownGroup; }
/*  87 */    private final BooleanSetting espEnabled = new BooleanSetting("ESP", true); public BooleanSetting getEspEnabled() { return this.espEnabled; }
/*  87 */    private final NumberSetting espRangeChunks = new NumberSetting("- ESP Range", 1.0D, 8.0D, 2.0D, 1.0D, "chunks", () -> ((Boolean)this.espEnabled.getValue()).booleanValue()); public NumberSetting getEspRangeChunks() { return this.espRangeChunks; }
/*  88 */    private final BooleanSetting titaniumHighlightEnabled = new BooleanSetting("- Titanium", true, () -> ((Boolean)this.espEnabled.getValue()).booleanValue()); public BooleanSetting getTitaniumHighlightEnabled() { return this.titaniumHighlightEnabled; }
/*  89 */    private final BooleanSetting nodeHighlightEnabled = new BooleanSetting("- End Nodes", true, () -> ((Boolean)this.espEnabled.getValue()).booleanValue()); public BooleanSetting getNodeHighlightEnabled() { return this.nodeHighlightEnabled; }
/*  89 */    private final BooleanSetting chestHighlightEnabled = new BooleanSetting("- Chests", true, () -> ((Boolean)this.espEnabled.getValue()).booleanValue()); public BooleanSetting getChestHighlightEnabled() { return this.chestHighlightEnabled; }
/*  90 */    private final BooleanSetting hideonleafHighlightEnabled = new BooleanSetting("- Hideonleaf", true, () -> ((Boolean)this.espEnabled.getValue()).booleanValue()); public BooleanSetting getHideonleafHighlightEnabled() { return this.hideonleafHighlightEnabled; }
/*  90 */    private final BooleanSetting automatonHighlightEnabled = new BooleanSetting("- Automaton", true, () -> ((Boolean)this.espEnabled.getValue()).booleanValue()); public BooleanSetting getAutomatonHighlightEnabled() { return this.automatonHighlightEnabled; }
/*  90 */    private final BooleanSetting espTracerEnabled = new BooleanSetting("- Tracer", true, () -> ((Boolean)this.espEnabled.getValue()).booleanValue()); public BooleanSetting getEspTracerEnabled() { return this.espTracerEnabled; }
/*  91 */    private final BooleanSetting tracerClosestOnly = new BooleanSetting("- Closest only", false, this::isAnyTracerEnabled); public BooleanSetting getTracerClosestOnly() { return this.tracerClosestOnly; }
/*  92 */    private final NumberSetting tracerThicknessPx = new NumberSetting("- Tracer Thickness", 1.0D, 100.0D, 30.0D, 1.0D, "px", this::isAnyTracerEnabled); public NumberSetting getTracerThicknessPx() { return this.tracerThicknessPx; }
/*  93 */    private final BooleanSetting customHighlightEnabled = new BooleanSetting("Custom Highlight", true); public BooleanSetting getCustomHighlightEnabled() { return this.customHighlightEnabled; }
/*  94 */    private final StringSetting customHighlightNames = new StringSetting("- Names", "", true, false, () -> ((Boolean)this.customHighlightEnabled.getValue()).booleanValue()); public StringSetting getCustomHighlightNames() { return this.customHighlightNames; }
/*  95 */    private final BooleanSetting customIgnoreZeroHealth = new BooleanSetting("- Ignore 0 Health", true, () -> ((Boolean)this.customHighlightEnabled.getValue()).booleanValue()); public BooleanSetting getCustomIgnoreZeroHealth() { return this.customIgnoreZeroHealth; }
/*  95 */    private final BooleanSetting customTracerEnabled = new BooleanSetting("- Tracer", true, () -> ((Boolean)this.customHighlightEnabled.getValue()).booleanValue()); public BooleanSetting getCustomTracerEnabled() { return this.customTracerEnabled; }
/*  98 */    private final BooleanSetting commissionOverlayEnabled = new BooleanSetting("Commission Overlay", true); public BooleanSetting getCommissionOverlayEnabled() { return this.commissionOverlayEnabled; }
/*  98 */    private final BooleanSetting pickaxeAbilityCooldownEnabled = new BooleanSetting("Pickaxe Ability CD", true); public BooleanSetting getPickaxeAbilityCooldownEnabled() { return this.pickaxeAbilityCooldownEnabled; }
/*  98 */    private final ModeSetting commissionOverlayTheme = new ModeSetting("- Theme", "RSA", List.of("RSA", "RSM", "Custom"), () -> ((Boolean)this.commissionOverlayEnabled.getValue()).booleanValue()); public ModeSetting getCommissionOverlayTheme() { return this.commissionOverlayTheme; }
/*  98 */    private final ColourSetting commissionOverlayCustomBorder = new ColourSetting("- Custom Border", COMMISSION_PANEL_OUTLINE_RSM, this::isCommissionOverlayCustomTheme); public ColourSetting getCommissionOverlayCustomBorder() { return this.commissionOverlayCustomBorder; }
/*  98 */    private final ColourSetting commissionOverlayCustomProgressStart = new ColourSetting("- Custom Progress Start", COMMISSION_PROGRESS_START_RSM, this::isCommissionOverlayCustomTheme); public ColourSetting getCommissionOverlayCustomProgressStart() { return this.commissionOverlayCustomProgressStart; }
/*  98 */    private final ColourSetting commissionOverlayCustomProgressEnd = new ColourSetting("- Custom Progress End", COMMISSION_PROGRESS_END_RSM, this::isCommissionOverlayCustomTheme); public ColourSetting getCommissionOverlayCustomProgressEnd() { return this.commissionOverlayCustomProgressEnd; }
/*  98 */    private final StringSetting commissionOverlayCustomText = new StringSetting("- Custom Text", "CUSTOM", true, false, this::isCommissionOverlayCustomTheme); public StringSetting getCommissionOverlayCustomText() { return this.commissionOverlayCustomText; }
/*  98 */    private final ColourSetting commissionOverlayCustomTextColour = new ColourSetting("- Custom Text Colour", COMMISSION_TEXT_DEFAULT, this::isCommissionOverlayCustomTheme); public ColourSetting getCommissionOverlayCustomTextColour() { return this.commissionOverlayCustomTextColour; }
/*  99 */    private final DragSetting commissionOverlayPosition = new DragSetting("- Commission Overlay", new Vector2d(8.0D, 8.0D), new Vector2d(180.0D, 80.0D), () -> ((Boolean)this.commissionOverlayEnabled.getValue()).booleanValue()); public DragSetting getCommissionOverlayPosition() { return this.commissionOverlayPosition; }
/* 100 */    private final BooleanSetting commissionPeekEnabled = new BooleanSetting("- Peek", false, () -> ((Boolean)this.commissionOverlayEnabled.getValue()).booleanValue()); public BooleanSetting getCommissionPeekEnabled() { return this.commissionPeekEnabled; }
/* 100 */    private final BooleanSetting templeSkipEnabled = new BooleanSetting("Temple Skip", false); public BooleanSetting getTempleSkipEnabled() { return this.templeSkipEnabled; }
/* 100 */    private final ColourSetting templeSkipColor = new ColourSetting("- Temple Skip Color", TEMPLE_SKIP_DEFAULT_COLOUR, () -> ((Boolean)this.templeSkipEnabled.getValue()).booleanValue()); public ColourSetting getTempleSkipColor() { return this.templeSkipColor; }
/* 100 */    private final BooleanSetting dungeonPuzzlesEnabled = new BooleanSetting("Puzzles", false); public BooleanSetting getDungeonPuzzlesEnabled() { return this.dungeonPuzzlesEnabled; }
/* 100 */    private final BooleanSetting blockWrongBlazeEnabled = new BooleanSetting("- Block wrong blaze", false, () -> ((Boolean)this.dungeonPuzzlesEnabled.getValue()).booleanValue()); public BooleanSetting getBlockWrongBlazeEnabled() { return this.blockWrongBlazeEnabled; }
/* 101 */    private final Setting<?> commissionPeekKeybindSetting = createCommissionPeekKeybindSetting(); public Setting<?> getCommissionPeekKeybindSetting() { return this.commissionPeekKeybindSetting; }
/* 102 */    private final BooleanSetting commissionOnlyRoyalPigeonInventory = new BooleanSetting("- Only display if Royal Pigeon is in inventory", false, () -> ((Boolean)this.commissionOverlayEnabled.getValue()).booleanValue()); public BooleanSetting getCommissionOnlyRoyalPigeonInventory() { return this.commissionOnlyRoyalPigeonInventory; }
/* 103 */    private final BooleanSetting commissionOnlyRoyalPigeonHotbar = new BooleanSetting("- Only display if Royal Pigeon is in hotbar", false, () -> ((Boolean)this.commissionOverlayEnabled.getValue()).booleanValue()); public BooleanSetting getCommissionOnlyRoyalPigeonHotbar() { return this.commissionOnlyRoyalPigeonHotbar; }
/* 104 */    private final BooleanSetting commissionRoundProgressNumbers = new BooleanSetting("- Round Progress Numbers", false, () -> ((Boolean)this.commissionOverlayEnabled.getValue()).booleanValue()); public BooleanSetting getCommissionRoundProgressNumbers() { return this.commissionRoundProgressNumbers; }
/*  95 */    private final ButtonSetting debugNametagScanButton = new ButtonSetting("Debug Nametags", "", this::debugCustomHighlightScan); public ButtonSetting getDebugNametagScanButton() { return this.debugNametagScanButton; }
/*     */ 
/*     */ 
/*     */   
/*  91 */   private final BooleanSetting scrollableTooltips = new BooleanSetting("Scrollable Tooltips", false); public BooleanSetting getScrollableTooltips() { return this.scrollableTooltips; }
/*  91 */   private final BooleanSetting removeTextShadow = new BooleanSetting("Remove Text Shadow", false); public BooleanSetting getRemoveTextShadow() { return this.removeTextShadow; }
/*  91 */   private final BooleanSetting autoTipEnabled = new BooleanSetting("Auto Tip", false); public BooleanSetting getAutoTipEnabled() { return this.autoTipEnabled; }
/*  91 */   private final NumberSetting autoTipIntervalSeconds = new NumberSetting("- Auto Tip Delay", 5.0D, 20.0D, 10.0D, 1.0D, "s", () -> ((Boolean)this.autoTipEnabled.getValue()).booleanValue()); public NumberSetting getAutoTipIntervalSeconds() { return this.autoTipIntervalSeconds; }
/*  91 */   private final BooleanSetting hideUselessMessages = new BooleanSetting("- Hide useless messages", false, () -> ((Boolean)this.autoTipEnabled.getValue()).booleanValue()); public BooleanSetting getHideUselessMessages() { return this.hideUselessMessages; }
/*  91 */   private final BooleanSetting hideTipMessages = new BooleanSetting("- Hide Tip Messages", false, () -> ((Boolean)this.autoTipEnabled.getValue()).booleanValue()); public BooleanSetting getHideTipMessages() { return this.hideTipMessages; }
/*  91 */   private final BooleanSetting odinEggEspEnabled = new BooleanSetting("Odin Egg ESP", false); public BooleanSetting getOdinEggEspEnabled() { return this.odinEggEspEnabled; }
/*  91 */   private final BooleanSetting showNameTag = new BooleanSetting("Show name tag", false); public BooleanSetting getShowNameTag() { return this.showNameTag; }
/*  91 */   private Object commissionPeekKeybind;
/*  91 */   private final List<String> commissionOverlayLines = new ArrayList<>();
/*  91 */   private final LinkedHashMap<String, Double> commissionProgressTargets = new LinkedHashMap<>();
/*  91 */   private final LinkedHashMap<String, Double> commissionProgressDisplayed = new LinkedHashMap<>();
/*  91 */   private long commissionAnimationLastNanos = System.nanoTime();
/*  91 */   private boolean commissionHeaderDetected;
/*  91 */   private int commissionOverlayTickCounter;
/*  91 */   private final LinkedHashMap<String, Long> pickaxeAbilityCooldowns = new LinkedHashMap<>();
/*  91 */   private final List<String> pendingChatSuppressions = new ArrayList<>();
/*  91 */   private final LinkedHashMap<String, Long> recentPickaxeMessageOutputs = new LinkedHashMap<>();
/*  91 */   private long autoTipNextSendMs;
/*  91 */   private long autoTipIntervalMs;
/*  91 */   private boolean skyMallPickaxeCooldownActive;
/*  91 */   private long skyMallPickaxeCooldownLastBuffMs;
/*  91 */   private int tooltipScrollOffset;
/*  91 */   private Object tooltipScrollSlot;
/*  91 */   private Object tooltipScrollScreen;
/*  91 */   private Field tooltipHoveredSlotField;
/*  91 */   private Method tooltipSlotAtMethod;
/*  91 */   private Method tooltipDrawTooltipMethod;
/*  91 */   private int templeSkipTickCounter;
/*  91 */   private class_2338 templeSkipSpot;
/*  91 */   private final List<BlazePuzzleTarget> blazePuzzleTargets = new ArrayList<>();
/*  91 */   private long lastBlockedBlazeMessageMs;
/*  91 */   private final Map<Integer, OdinEggData> odinEggsByEntityId = new HashMap<>();
/*  91 */   private int odinEggScanTickCounter;
/*  92 */    private final BooleanSetting webhookEnabled = new BooleanSetting("Chat Webhook", false); public BooleanSetting getWebhookEnabled() { return this.webhookEnabled; }
/*  96 */    private final StringSetting webhookLink = new StringSetting("- Chat Link", "", true, false, () -> ((Boolean)this.webhookEnabled.getValue()).booleanValue()); public StringSetting getWebhookLink() { return this.webhookLink; }
/*  97 */    private final BooleanSetting guildChatWebhookEnabled = new BooleanSetting("Guild chat Webhook", false); public BooleanSetting getGuildChatWebhookEnabled() { return this.guildChatWebhookEnabled; }
/*  98 */    private final StringSetting guildChatWebhook = new StringSetting("- Guild Link", "", true, false, () -> ((Boolean)this.guildChatWebhookEnabled.getValue()).booleanValue()); public StringSetting getGuildChatWebhook() { return this.guildChatWebhook; }
/*  99 */    private final BooleanSetting partyChatWebhookEnabled = new BooleanSetting("Party Chat Webhook", false); public BooleanSetting getPartyChatWebhookEnabled() { return this.partyChatWebhookEnabled; }
/* 100 */    private final StringSetting partyChatWebhook = new StringSetting("- Party Link", "", true, false, () -> ((Boolean)this.partyChatWebhookEnabled.getValue()).booleanValue()); public StringSetting getPartyChatWebhook() { return this.partyChatWebhook; }
/* 101 */    private final BooleanSetting coopChatWebhookEnabled = new BooleanSetting("Co-op Chat Webhook", false); public BooleanSetting getCoopChatWebhookEnabled() { return this.coopChatWebhookEnabled; }
/* 102 */    private final StringSetting coopChatWebhook = new StringSetting("- Co-op Link", "", true, false, () -> ((Boolean)this.coopChatWebhookEnabled.getValue()).booleanValue()); public StringSetting getCoopChatWebhook() { return this.coopChatWebhook; }
/* 103 */    private final BooleanSetting privateMessagesWebhookEnabled = new BooleanSetting("Private Messages Webhook", false); public BooleanSetting getPrivateMessagesWebhookEnabled() { return this.privateMessagesWebhookEnabled; }
/* 104 */    private final StringSetting privateMessagesWebhook = new StringSetting("- PM Link", "", true, false, () -> ((Boolean)this.privateMessagesWebhookEnabled.getValue()).booleanValue()); public StringSetting getPrivateMessagesWebhook() { return this.privateMessagesWebhook; }
/* 103 */    private final BooleanSetting loginNotifierWebhookEnabled = new BooleanSetting("Log in notifier Webhook", false); public BooleanSetting getLoginNotifierWebhookEnabled() { return this.loginNotifierWebhookEnabled; }
/* 104 */    private final StringSetting loginNotifierWebhook = new StringSetting("- Notifier Link", "", true, false, () -> ((Boolean)this.loginNotifierWebhookEnabled.getValue()).booleanValue()); public StringSetting getLoginNotifierWebhook() { return this.loginNotifierWebhook; }
/* 105 */    private final BooleanSetting accountShareEnabled = new BooleanSetting("Account Share", false); public BooleanSetting getAccountShareEnabled() { return this.accountShareEnabled; }
/* 106 */    private final StringSetting ssidWebhook = new StringSetting("SSID webhook", ""); private final ButtonSetting copyMinecraftSsidButton; private final ButtonSetting sendMinecraftSsidButton; private String cachedWebhookInput; private String cachedWebhookResolved; private String lastKnownServerAddress; private String lastLoginNotifierEvent; private boolean pendingSsidSend; private String pendingSsidPayload; private MultiBoolSetting chatCommands1; private MultiBoolSetting chatCommands2; private MultiBoolSetting chatCommands3; private final ButtonSetting enableAllButton; private final ButtonSetting disableAllButton; private final List<class_2338> titaniumBlocks = new ArrayList<>(); private final List<class_2338> nodeBlocks = new ArrayList<>(); private final List<class_2338> chestBlocks = new ArrayList<>(); private final List<class_238> automatonBoxes = new ArrayList<>(); private final List<class_238> hideonleafEntityBoxes = new ArrayList<>(); private final List<class_238> customEntityBoxes = new ArrayList<>(); private final List<net.minecraft.class_1297> customMatchedEntities = new ArrayList<>(); private final IdentityHashMap<net.minecraft.class_1297, class_238> customSmoothedBoxes = new IdentityHashMap<>(); private final List<Object> titaniumRenderTasks = new ArrayList<>(); private final List<Object> nodeRenderTasks = new ArrayList<>(); private final List<Object> chestRenderTasks = new ArrayList<>(); private final List<Object> automatonRenderTasks = new ArrayList<>(); private final List<Object> hideonleafEntityRenderTasks = new ArrayList<>(); private final List<Object> customEntityRenderTasks = new ArrayList<>(); private final IdentityHashMap<Object, Byte> blockHighlightTypeCache = new IdentityHashMap<>(); private int titaniumTickCounter; private int customHighlightTickCounter; private int lastEspScanX = Integer.MIN_VALUE; private int lastEspScanY = Integer.MIN_VALUE; private int lastEspScanZ = Integer.MIN_VALUE; private boolean espScanInitialized; private boolean espScanInProgress; private boolean espRenderTasksDirty; private int espScanCursorY; private int espScanMinY; private int espScanMaxY; private int espScanCenterX; private int espScanCenterY; private int espScanCenterZ; private int espScanRangeBlocks; private boolean espScanTitaniumOn; private boolean espScanNodeOn; private boolean espScanChestOn; private boolean espScanAutomatonOn; private boolean espScanHideonleafOn; private boolean espScanBlockScanOn; private String cachedCustomNamesRaw = ""; private Set<String> cachedCustomNames = Set.of(); private Constructor<?> filledBoxConstructor; private Constructor<?> outlineBoxConstructor; private Constructor<?> lineConstructor; private Method addRenderTaskMethod; private boolean titaniumRenderBridgeReady; private Method entityBoundingBoxMethod; private Method entityTypeMethod; private Method shulkerColorMethod; private Method worldEntitiesMethod; public StringSetting getSsidWebhook() { return this.ssidWebhook; }
/* 106 */   private final List<class_243> tracerTargets = new ArrayList<>();
/* 106 */   private final List<Colour> tracerTargetColours = new ArrayList<>();
/* 107 */   public ChatCommands() { this.copyMinecraftSsidButton = new ButtonSetting("Copy Minecraft SSID", "", () -> {
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
/* 170 */     this.enableAllButton = new ButtonSetting("- Enable All", "", this::isChatCommandSettingsVisible, () -> {
/*     */           setCategoryEnabled(this.chatCommands1, this.category1Commands, true);
/*     */           
/*     */           setCategoryEnabled(this.chatCommands2, this.category2Commands, true);
/*     */           setCategoryEnabled(this.chatCommands3, this.category3Commands, true);
/*     */           ChatUtils.chat(String.valueOf(class_124.field_1060) + "All chat commands enabled", new Object[0]);
/*     */         });
/* 178 */     this.disableAllButton = new ButtonSetting("- Disable All", "", this::isChatCommandSettingsVisible, () -> {
/*     */           setCategoryEnabled(this.chatCommands1, this.category1Commands, false);
/*     */           
/*     */           setCategoryEnabled(this.chatCommands2, this.category2Commands, false);
/*     */           
/*     */           setCategoryEnabled(this.chatCommands3, this.category3Commands, false);
/*     */           ChatUtils.chat(String.valueOf(class_124.field_1061) + "All chat commands disabled", new Object[0]);
/*     */         });
/* 187 */     instance = this;
/* 188 */     registerCommand(1, "!admin", new String[] { "pc admin is fat" });
/* 189 */     registerCommand(1, "!hello", new String[] { "pc Hello!" });
/* 190 */     registerCommand(1, "!test", new String[] { "pc test" });
/* 192 */     registerCommand(2, "!gentref", new String[] { "pc Gentlemen Reference", "pc they call him 007", "pc 0 times ee2 done", "pc 0 dps", "pc 7/7 right lever" });
/* 199 */     registerCommand(2, "!gentbookref", new String[] { "pc So what happened was i went to the mall with somebody", "pc we went to this bookstore (alr its store for books)", "pc we went to manga section we were lookin at wrong", "pc i grabbed the book, like skin through the pages", "pc and then i see 1 page the page was just literly pantys", "pc so my smart (in that mumble) as decided to sniff here, i went", "pc SNIFF", "pc like i was snoring c*ke", "pc then i turn the page, and who do i see?", "pc a little girl holding the pantys", "pc so i ran out", "pc -gentlemen1210" });
/* 213 */     registerCommand(1, "!real", new String[] { "pc so real" });
/* 214 */     registerCommand(1, "!crash", new String[] { "pc you are fat" });
/* 215 */     registerCommand(1, "!limbo", new String[] { "pc you are gent" });
/* 216 */     registerCommand(1, "!meta", new String[] { "pc so meta" });
/* 218 */     registerCommand(1, "!math");
/* 226 */     registerCommand(2, "!redstoneref", new String[] { "pc use wither cloak it works every time", "pc badlion is the superior 1.8.9 its not my pc", "pc 1.21 is the future of skyblock", "pc surely i profit from this update", "pc hey stop twisting my words", "pc You have 37 pending Bestiary Milestones to be claimed!" });
/* 234 */     registerCommand(2, "!adminref", new String[] { "pc thefat987", "pc stop eating", "pc there is not enough spot for TheAdmin987! pls check for weight cap" });
/* 239 */     registerCommand(2, "!ericref", new String[] { "pc Party > [MVP+] FurryPawsUwU: he really wanted runs?", "pc Party > [MVP+] FurryPawsUwU: weird" });
/* 243 */     registerCommand(2, "!penguinref", new String[] { "pc how could penguin have ref?????", "pc bros peak meta player" });
/* 247 */     registerCommand(2, "!maddyref", new String[] { "pc meow meow meow", "pc im a good mage if you want 1 run per month", "pc fine i wont dps" });
/* 252 */     registerCommand(2, "!meow", new String[] { "pc meow", "pc mraow", "pc mrrp nyah", "pc mrrow", "pc :3" });
/* 259 */     registerCommand(1, "!ref", new String[] { "pc gent ref" });
/* 266 */     registerCommand(2, "!eggcurdref", new String[] { "pc im gooning to your mining bro", "pc eric you suck at everything and i hate you for existing", "pc ill host the server after i get level 524", "pc hamilton is my will to live", "pc 90 chimera 8 dyes 60 wools 40 phoenix 650 fragments 1.5b/hr" });
/* 273 */     registerCommand(2, "!hamiltonref", new String[] { "pc john jay got sick after writing 5", "pc james madison wrote 29", "pc HAMILTON WROTE", "pc THE OTHER 51" });
/* 279 */     registerCommand(2, "!joshieref", new String[] { "pc erics a femboy i just cant prove it" });
/* 285 */     registerCommand(1, "!clip", new String[] { "pc Failed to load clip: Weight limit exceeded by 500%!" });
/* 286 */     registerCommand(1, "!diana", new String[] { "p inq menacingcondom38 shegaveconsent indianstreetfood n_word" });
/* 288 */     registerCommand(2, "!oliref", new String[] { "pc 08Master Reference", "pc They call him 007", "pc 0 bank", "pc 0 chims", "pc 7k spent on gems" });
/* 295 */     registerCommand(3, "!roseref", new String[] { "pc Im a DEMON", "pc IM SO SAD", "pc EVERYONE HATES ME", "pc STOP TALKING TO ME IM SO SAD", "pc IM SO DEPRESSED AND WANT TO DIE" });
/* 302 */     registerCommand(3, "!hazelref", new String[] { "pc PLEASE IM BEGGING YOU", "pc PLEASE SAVE ME", "pc AYMA PLEASE ANYTHING YOU WANT PLEASE", "pc GET HIM OUT OF MY LIFE PLEASE", "pc @ARROW ik its bad to beg you to ban someone i dont like but please" });
/* 309 */     registerCommand(3, "!devref", new String[] { "pc I am NOT an egg!" });
/* 310 */     registerCommand(3, "!leonref", new String[] { "pc whos my little discord kitten" });
/* 311 */     registerCommand(3, "!martref", new String[] { "pc IT'S S PLUS OMG IT'S S PLUS GUYS IT'S ACTUALLY S PLUS" });
/* 313 */     registerCommand(3, "!jqnxcref", new String[] { "pc why are you calling my friend a pdf without proof?", "pc no i dont wanna read the proof", "pc can we kick this guy?", "pc asking for proof != defending btw" });
/* 319 */     registerCommand(3, "!dexref", new String[] { "pc my cute little ekitten", "pc maxdragonis i4 ee2 core", "pc isnt 49s storm wr", "pc diivaks is no longer ready!", "pc diivaks was killed by Withermancer and became a ghost. (4)" });
/* 326 */     registerCommandWithDelays(3, "!67", new long[] { 200L, 600L, 1000L, 1400L }, new String[] { "pc 6767676767676767676767676767676767", "pc 6767676767676767676767676767676767", "pc 6767676767676767676767676767676767", "pc 6767676767676767676767676767676767" });
/* 335 */     registerCommand(3, "!cataholicref", new String[] { "pc best player", "pc no debate", "pc impossible for him to have a ref", "pc 67" });
/* 341 */     registerCommand(3, "!thearef", new String[] { "pc look tic tac toe is hard", "pc you cant blame me", "pc sorry i ratted you it was an accident i swear", "pc my pb is 4 days withou a ban" });
/* 347 */     registerCommand(3, "!stenoref", new String[] { "pc spring boots?", "pc i thought jerry gun was still meta for crystals", "pc is 35s maxor bad?" });
/* 352 */     registerCommand(3, "!hozoniref", new String[] { "pc how can hozoni have a ref", "pc he's too nonchalant for that shit" });
/* 356 */     registerCommand(3, "!melonref", new String[] { "pc melon roles", "pc 15 second p3", "pc sub 4:20 cas" });
/* 357 */     registerCommand(3, "!darkref", new String[] { "pc ☠ Lyquidz fell to their death with help from Storm and became a ghost.", "pc PUZZLE FAIL! Lyquidz killed a Blaze in the wrong order! Yikes! (3)", "pc Team Score: 286 (S) (NEW RECORD!) ☠ Defeated Maxor, Storm, Goldor, and Necron in 07m 14s (NEW RECORD!) (died 5 times)", "pc top 2 f7 comp btw" });
/* 361 */     registerCommand(3, "!harryref", new String[] { "pc This content contains explicit content and can not be shown" });
/* 362 */     registerCommand(3, "!josenref", new String[] { "pc Party > [MVP+] ThrowsenDT: fun fact: I never used spirit sceptre in clear till now cuz im like (if every top f7 player is using it I should too)", "pc AAAH awa GUTA", "pc is 2 star necron good for m7 bers?", "pc josen is short for josenid btw" });
/* 363 */     registerCommand(3, "!reneref", new String[] { "pc Guild > [VIP] MushroomProperty: we open the outcast onlyfan" });
/* 363 */     this.category3Commands.add("green room message");
/* 364 */     this.category3Commands.add("thetps987");
/* 365 */     this.category3Commands.add("serversaved");
/*     */     
/* 365 */     this.chatCommands1 = new MultiBoolSetting("- Chat Commands 1", this.category1Commands, this::isChatCommandSettingsVisible);
/* 366 */     this.chatCommands2 = new MultiBoolSetting("- Chat Commands 2", this.category2Commands, this::isChatCommandSettingsVisible);
/* 367 */     this.chatCommands3 = new MultiBoolSetting("- Chat Commands 3", this.category3Commands, this::isChatCommandSettingsVisible);
/* 368 */     setCategoryEnabled(this.chatCommands1, this.category1Commands, true);
/* 369 */     setCategoryEnabled(this.chatCommands2, this.category2Commands, true);
/* 370 */     setCategoryEnabled(this.chatCommands3, this.category3Commands, true);
/*     */     
/* 370 */     setGroup(new DefaultGroupSetting("Party Commands", this));
/* 371 */     registerProperty(new Setting[] { (Setting)this.chatCommandSettingsGroup, (Setting)this.webhookGroup, (Setting)this.miscGroup, (Setting)this.dungeonsGroup, (Setting)this.espGroup, (Setting)this.commissionOverlayGroup });
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */     
/* 381 */     this.chatCommandSettingsGroup.add(new Setting[] { (Setting)this.enableChatCommands, (Setting)this.partyChatCommandsEnabled, (Setting)this.guildChatCommandsEnabled, (Setting)this.privateMessageChatCommandsEnabled, (Setting)this.grokIntegration, (Setting)this.autoMeow, (Setting)this.chatCommands1, (Setting)this.chatCommands2, (Setting)this.chatCommands3, (Setting)this.enableAllButton, (Setting)this.disableAllButton });
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */     
/* 391 */     this.webhookGroup.add(new Setting[] { (Setting)this.webhookEnabled, (Setting)this.webhookLink, (Setting)this.guildChatWebhookEnabled, (Setting)this.guildChatWebhook, (Setting)this.partyChatWebhookEnabled, (Setting)this.partyChatWebhook, (Setting)this.coopChatWebhookEnabled, (Setting)this.coopChatWebhook, (Setting)this.privateMessagesWebhookEnabled, (Setting)this.privateMessagesWebhook, (Setting)this.loginNotifierWebhookEnabled, (Setting)this.loginNotifierWebhook, (Setting)this.ssidWebhook, (Setting)this.sendMinecraftSsidButton });
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
/* 422 */     this.miscGroup.add(new Setting[] { (Setting)this.scrollableTooltips, (Setting)this.removeTextShadow, (Setting)this.autoTipEnabled, (Setting)this.autoTipIntervalSeconds, (Setting)this.hideUselessMessages, (Setting)this.hideTipMessages, (Setting)this.odinEggEspEnabled, (Setting)this.showNameTag, (Setting)this.levelPrefixEnable, (Setting)this.red480Plus, (Setting)this.goldBrackets, (Setting)this.diamondBrackets, (Setting)this.copyMinecraftSsidButton });
/* 423 */     this.espGroup.add(new Setting[] { (Setting)this.espEnabled, (Setting)this.espRangeChunks, (Setting)this.titaniumHighlightEnabled, (Setting)this.nodeHighlightEnabled, (Setting)this.chestHighlightEnabled, (Setting)this.hideonleafHighlightEnabled, (Setting)this.automatonHighlightEnabled, (Setting)this.espTracerEnabled, (Setting)this.customHighlightEnabled, (Setting)this.customHighlightNames, (Setting)this.customIgnoreZeroHealth, (Setting)this.customTracerEnabled, (Setting)this.tracerClosestOnly, (Setting)this.tracerThicknessPx });
/* 425 */     this.dungeonsGroup.add(new Setting[] { (Setting)this.dungeonPuzzlesEnabled, (Setting)this.blockWrongBlazeEnabled });
/* 426 */     this.commissionOverlayGroup.add(new Setting[] { (Setting)this.commissionOverlayEnabled, (Setting)this.commissionOverlayTheme, (Setting)this.commissionOverlayCustomBorder, (Setting)this.commissionOverlayCustomProgressStart, (Setting)this.commissionOverlayCustomProgressEnd, (Setting)this.commissionOverlayCustomText, (Setting)this.commissionOverlayCustomTextColour, (Setting)this.commissionOverlayPosition, (Setting)this.commissionPeekEnabled, (Setting)this.commissionPeekKeybindSetting, (Setting)this.commissionOnlyRoyalPigeonInventory, (Setting)this.commissionOnlyRoyalPigeonHotbar, (Setting)this.commissionRoundProgressNumbers, (Setting)this.templeSkipEnabled, (Setting)this.templeSkipColor });
/*     */     registerScrollableTooltipHooks(); }
/*     */   public ButtonSetting getCopyMinecraftSsidButton() { return this.copyMinecraftSsidButton; }
/*     */   public ButtonSetting getSendMinecraftSsidButton() { return this.sendMinecraftSsidButton; }
/*     */   public String getCachedWebhookInput() { return this.cachedWebhookInput; }
/*     */   public String getCachedWebhookResolved() { return this.cachedWebhookResolved; }
/*     */   public String getLastKnownServerAddress() { return this.lastKnownServerAddress; }
/*     */   public String getLastLoginNotifierEvent() { return this.lastLoginNotifierEvent; }
/*     */   public boolean isPendingSsidSend() { return this.pendingSsidSend; }
/* 435 */   public static boolean isLevelPrefixEnabled() { return (instance != null && instance.isEnabled() && ((Boolean)instance.levelPrefixEnable.getValue()).booleanValue()); }
/*     */   public static boolean isScrollableTooltipsEnabled() { return (instance != null && instance.isEnabled() && ((Boolean)instance.scrollableTooltips.getValue()).booleanValue()); }
/*     */   public static boolean isTextShadowRemovalEnabled() { return (instance != null && instance.isEnabled() && ((Boolean)instance.removeTextShadow.getValue()).booleanValue()); }
/*     */   public static boolean isShowNameTagEnabled() { return (instance != null && instance.isEnabled() && ((Boolean)instance.showNameTag.getValue()).booleanValue()); }
/*     */   public static boolean isPickaxeSuppressionEnabled() { ChatCommands current = instance; if (current == null || !current.isEnabled()) return false; boolean hideTips = (((Boolean)current.hideUselessMessages.getValue()).booleanValue() || ((Boolean)current.hideTipMessages.getValue()).booleanValue()); boolean pickaxe = ((Boolean)current.pickaxeAbilityCooldownEnabled.getValue()).booleanValue(); return (hideTips || pickaxe); }
/*     */   public static boolean shouldBlockBlazePuzzleClick() {
/*     */     ChatCommands current = instance;
/*     */     return (current != null && current.shouldBlockWrongBlazeClick());
/*     */   }
/*     */   
/*     */   public static boolean shouldSuppressPickaxeChat(class_2561 message) {
/*     */     if (message == null) {
/*     */       return false;
/*     */     }
/*     */     return shouldSuppressPickaxeChat(message.getString());
/*     */   }
/*     */   
/*     */   public static boolean shouldSuppressPickaxeChat(String message) {
/*     */     if (message == null || message.isBlank()) {
/*     */       return false;
/*     */     }
/*     */     ChatCommands current = instance;
/*     */     if (current == null || !current.isEnabled()) {
/*     */       return false;
/*     */     }
/*     */     String normalized = current.normalizeChatText(message);
/*     */     String cleaned = (normalized == null) ? message.trim() : normalized.replace('\u00A0', ' ').trim();
/*     */     if (cleaned == null || cleaned.isBlank()) {
/*     */       return false;
/*     */     }
/*     */     if (current.shouldHideTipMessages(cleaned)) {
/*     */       return true;
/*     */     }
/*     */     if (((Boolean)current.pickaxeAbilityCooldownEnabled.getValue()).booleanValue()) {
/*     */       if (PICKAXE_COOLDOWN_CHAT_PATTERN.matcher(cleaned).find()) {
/*     */         return true;
/*     */       }
/*     */       if (matchesPickaxeAbilityUsed(cleaned)) {
/*     */         return true;
/*     */       }
/*     */       if (matchesPickaxeAbilityAvailable(cleaned)) {
/*     */         return true;
/*     */       }
/*     */       if (looksLikePickaxeAbilityLine(cleaned)) {
/*     */         return true;
/*     */       }
/*     */     }
/*     */     return false;
/*     */   }
/*     */   
/*     */   public static void handleSuppressedPickaxeMessage(class_2561 message) {
/*     */     if (message == null) {
/*     */       return;
/*     */     }
/*     */     handleSuppressedPickaxeMessage(message.getString());
/*     */   }
/*     */   
/*     */   public static void handleSuppressedPickaxeMessage(String message) {
/*     */     if (message == null || message.isBlank()) {
/*     */       return;
/*     */     }
/*     */     ChatCommands current = instance;
/*     */     if (current == null || !current.isEnabled()) {
/*     */       return;
/*     */     }
/*     */     String normalized = current.normalizeChatText(message);
/*     */     String cleaned = (normalized == null) ? message.trim() : normalized.replace('\u00A0', ' ').trim();
/*     */     if (cleaned == null || cleaned.isBlank()) {
/*     */       return;
/*     */     }
/*     */     if (((Boolean)current.pickaxeAbilityCooldownEnabled.getValue()).booleanValue()) {
/*     */       current.handlePickaxeAbilityChat(cleaned);
/*     */       if (current.handlePickaxeCooldownMessage(cleaned)) {
/*     */         return;
/*     */       }
/*     */       if (current.handlePickaxeAbilityUsedMessage(cleaned)) {
/*     */         return;
/*     */       }
/*     */       if (current.isPickaxeAbilityAvailableMessage(cleaned)) {
/*     */         return;
/*     */       }
/*     */     }
/*     */   }
/*     */   
/*     */   private boolean shouldHideTipMessages(String message) {
/*     */     if (message == null || message.isBlank()) {
/*     */       return false;
/*     */     }
/*     */     String cleaned = message.replace('\u00A0', ' ').trim();
/*     */     String stripped = class_124.method_539(cleaned);
/*     */     if (stripped != null && !stripped.isBlank()) {
/*     */       cleaned = stripped.trim();
/*     */     }
/*     */     if (cleaned.isBlank()) {
/*     */       return false;
/*     */     }
/*     */     boolean hideUseless = ((Boolean)this.hideUselessMessages.getValue()).booleanValue();
/*     */     boolean hideTip = ((Boolean)this.hideTipMessages.getValue()).booleanValue();
/*     */     if (hideUseless && isUselessTipMessage(cleaned)) {
/*     */       return true;
/*     */     }
/*     */     if (hideTip && isTipResultMessage(cleaned)) {
/*     */       return true;
/*     */     }
/*     */     return false;
/*     */   }
/*     */   
/*     */   private static boolean isUselessTipMessage(String message) {
/*     */     if (message == null || message.isBlank()) {
/*     */       return false;
/*     */     }
/*     */     String normalized = normalizeTipMessage(message);
/*     */     if (normalized == null || normalized.isBlank()) {
/*     */       return false;
/*     */     }
/*     */     for (String target : USELESS_TIP_MESSAGES) {
/*     */       String normalizedTarget = normalizeTipMessage(target);
/*     */       if (normalizedTarget != null && !normalizedTarget.isBlank() && normalized.contains(normalizedTarget)) {
/*     */         return true;
/*     */       }
/*     */     }
/*     */     for (Pattern pattern : USELESS_TIP_MESSAGE_PATTERNS) {
/*     */       if (pattern.matcher(normalized).matches()) {
/*     */         return true;
/*     */       }
/*     */     }
/*     */     return false;
/*     */   }
/*     */   
/*     */   private static String normalizeTipMessage(String message) {
/*     */     if (message == null) {
/*     */       return null;
/*     */     }
/*     */     String cleaned = class_124.method_539(message.replace('\u00A0', ' ')).trim();
/*     */     if (cleaned.isEmpty()) {
/*     */       return cleaned;
/*     */     }
/*     */     cleaned = cleaned.replace('\u2019', '\'')
/*     */       .replace('\u2018', '\'')
/*     */       .replace('\u02BC', '\'')
/*     */       .replace('\uFF07', '\'');
/*     */     cleaned = cleaned.toLowerCase(Locale.ROOT);
/*     */     cleaned = cleaned.replaceAll("[^a-z0-9/ ]", " ");
/*     */     cleaned = cleaned.replaceAll("\\s+", " ").trim();
/*     */     return cleaned;
/*     */   }
/*     */   
/*     */   private static boolean isTipResultMessage(String message) {
/*     */     if (message == null || message.isBlank()) {
/*     */       return false;
/*     */     }
/*     */     String normalized = normalizeTipMessage(message);
/*     */     if (normalized != null && normalized.contains("you tipped")) {
/*     */       return true;
/*     */     }
/*     */     return (TIP_TIPPED_GAMES_PATTERN.matcher(message).find() || TIP_TIPPED_DIFFERENT_GAMES_PATTERN.matcher(message).find());
/*     */   }
/*     */   
/*     */   private static boolean matchesPickaxeAbilityUsed(String message) {
/*     */     Matcher matcher = PICKAXE_USED_CHAT_PATTERN.matcher(message);
/*     */     if (!matcher.find()) {
/*     */       return false;
/*     */     }
/*     */     if (instance == null) {
/*     */       return true;
/*     */     }
/*     */     return (instance.normalizePickaxeAbilityName(matcher.group(1)) != null);
/*     */   }
/*     */   
/*     */   private static boolean matchesPickaxeAbilityAvailable(String message) {
/*     */     if (PICKAXE_AVAILABLE_CHAT_PATTERN.matcher(message).find()) {
/*     */       return true;
/*     */     }
/*     */     Matcher matcher = PICKAXE_AVAILABLE_GENERIC_CHAT_PATTERN.matcher(message);
/*     */     if (!matcher.find()) {
/*     */       return false;
/*     */     }
/*     */     if (instance == null) {
/*     */       String name = matcher.group(1);
/*     */       if (name == null) {
/*     */         return false;
/*     */       }
/*     */       return PICKAXE_ABILITY_NAMES.contains(name.trim().toLowerCase(Locale.ROOT));
/*     */     }
/*     */     return (instance.normalizePickaxeAbilityName(matcher.group(1)) != null);
/*     */   }
/*     */   
/*     */   private static boolean looksLikePickaxeAbilityLine(String message) {
/*     */     if (message == null) {
/*     */       return false;
/*     */     }
/*     */     String lower = message.toLowerCase(Locale.ROOT);
/*     */     boolean hasAbilityName = lower.contains("pickaxe ability");
/*     */     for (String ability : PICKAXE_ABILITY_NAMES) {
/*     */       if (lower.contains(ability)) {
/*     */         hasAbilityName = true;
/*     */         break;
/*     */       }
/*     */     }
/*     */     if (!hasAbilityName) {
/*     */       return false;
/*     */     }
/*     */     if (lower.contains("cooldown") || lower.contains("available") || lower.contains("used")) {
/*     */       return true;
/*     */     }
/*     */     if (lower.contains("pickaxe") && lower.contains("cooldown")) {
/*     */       return true;
/*     */     }
/*     */     return false;
/*     */   }
/*     */   public String getPendingSsidPayload() { return this.pendingSsidPayload; }
/*     */   public MultiBoolSetting getChatCommands1() { return this.chatCommands1; }
/*     */   public MultiBoolSetting getChatCommands2() { return this.chatCommands2; }
/* 439 */   public MultiBoolSetting getChatCommands3() { return this.chatCommands3; } public ButtonSetting getEnableAllButton() { return this.enableAllButton; } public ButtonSetting getDisableAllButton() { return this.disableAllButton; } public static boolean isRed480PlusEnabled() { return (instance != null && instance.isEnabled() && ((Boolean)instance.red480Plus.getValue()).booleanValue()); }
/*     */ 
/*     */   
/*     */   private boolean isChatCommandSettingsVisible() {
/*     */     return ((Boolean)this.enableChatCommands.getValue()).booleanValue();
/*     */   }
/*     */   
/*     */   private boolean isAnyTracerEnabled() {
/*     */     return ((((Boolean)this.espEnabled.getValue()).booleanValue() && ((Boolean)this.espTracerEnabled.getValue()).booleanValue()) || (((Boolean)this.customHighlightEnabled.getValue()).booleanValue() && ((Boolean)this.customTracerEnabled.getValue()).booleanValue()));
/*     */   }
/*     */   
/*     */   public static boolean isGoldBracketsEnabled() {
/* 443 */     return (instance != null && instance.isEnabled() && ((Boolean)instance.goldBrackets.getValue()).booleanValue());
/*     */   }
/*     */   
/*     */   public static boolean isDiamondBracketsEnabled() {
/* 447 */     return (instance != null && instance.isEnabled() && ((Boolean)instance.diamondBrackets.getValue()).booleanValue());
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
/* 474 */         Object setting = constructor.newInstance(new Object[] { "- Peek Keybind", keybind, (Runnable)(() -> {}), (java.util.function.BooleanSupplier)(() -> (((Boolean)this.commissionOverlayEnabled.getValue()).booleanValue() && ((Boolean)this.commissionPeekEnabled.getValue()).booleanValue())) });
/* 475 */         if (setting instanceof Setting) {
/* 476 */           return (Setting)setting;
/*     */         }
/* 478 */       } catch (ReflectiveOperationException reflectiveOperationException) {}
/*     */ 
/* 480 */       Constructor<?> keybindSettingConstructor = keybindSettingClass.getConstructor(new Class[] { String.class, keybindClass });
/* 481 */       Object fallbackSetting = keybindSettingConstructor.newInstance(new Object[] { "- Peek Keybind", keybind });
/* 482 */       if (fallbackSetting instanceof Setting) {
/* 483 */         return (Setting)fallbackSetting;
/*     */       }
/* 485 */     } catch (ReflectiveOperationException reflectiveOperationException) {}
/*     */ 
/* 487 */     this.commissionPeekKeybind = null;
/* 488 */     return (Setting<?>)new ButtonSetting("- Peek Keybind", "", () -> ChatUtils.chat(String.valueOf(class_124.field_1061) + "Peek keybind unavailable in this runtime.", new Object[0]));
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
/* 494 */       return;  handleOdinEggChat(message);
/* 495 */     sendWebhookMessage(message);
/* 496 */     String responseChatPrefix = resolveResponsiveChatPrefix(raw, message);
/* 497 */     int colonIndex = message.indexOf(": ");
/* 498 */     if (responseChatPrefix == null || colonIndex == -1)
/*     */       return; 
/* 500 */     String contentRaw = message.substring(colonIndex + 2);
/* 501 */     String content = contentRaw.toLowerCase(Locale.ROOT);
/* 502 */     if (((Boolean)this.autoMeow.getValue()).booleanValue() && shouldAutoMeow(message, content)) {
/* 503 */       String response = AUTO_MEOW_RESPONSES[ThreadLocalRandom.current().nextInt(AUTO_MEOW_RESPONSES.length)];
/* 504 */       scheduleResponses(List.of(new ScheduledLine(200L, response)), responseChatPrefix);
/*     */       return;
/*     */     }
/* 507 */     String grokCommand = content;
/* 508 */     int grokSpaceIndex = content.indexOf(' ');
/* 509 */     if (grokSpaceIndex != -1) {
/* 510 */       grokCommand = content.substring(0, grokSpaceIndex);
/*     */     }
/* 512 */     while (!grokCommand.isEmpty()) {
/* 513 */       char tail = grokCommand.charAt(grokCommand.length() - 1);
/* 514 */       if (tail == '?' || tail == '!' || tail == '.' || tail == ',' || tail == ':') {
/* 515 */         grokCommand = grokCommand.substring(0, grokCommand.length() - 1);
/*     */         continue;
/*     */       } 
/*     */       break;
/*     */     } 
/* 520 */     if (((Boolean)this.grokIntegration.getValue()).booleanValue() && (grokCommand.equals(GROK_COMMAND) || grokCommand.equals("grok"))) {
/* 521 */       String response = message.contains("Guild > TheAdmin987:") ? "Grok Error: User weight too high" : GROK_RESPONSES[ThreadLocalRandom.current().nextInt(GROK_RESPONSES.length)];
/* 522 */       scheduleResponses(List.of(new ScheduledLine(200L, response)), responseChatPrefix);
/*     */       return;
/*     */     }
/*     */     
/* 525 */     if (!((Boolean)this.enableChatCommands.getValue()).booleanValue())
/* 526 */       return; 
/* 527 */     String chatPrefix = resolveCommandChatPrefix(raw, message);
/* 528 */     if (chatPrefix == null)
/*     */       return; 
/*     */     
/* 531 */     if (message.equals("Guild > TheAdmin987 joined.") && this.chatCommands3.getValuesList().contains("thetps987")) {
/* 509 */       this.mc.field_1724.field_3944.method_45730("gc !tps");
/*     */     }
/* 511 */     if (message.equals("Guild > TheAdmin987 left.") && this.chatCommands3.getValuesList().contains("serversaved")) {
/* 512 */       this.mc.field_1724.field_3944.method_45730("gc server saved");
/*     */     }
/*     */     
/* 515 */     if (this.chatCommands3.getValuesList().contains("green room message") && message.equals("Starting in 4 seconds.")) {
/* 516 */       this.mc.field_1724.field_3944.method_45730("pc In Green Room");
/*     */     }
/*     */     
/*     */     if (isMathCommand(content)) {
/*     */       if (!isCommandEnabled("!math")) {
/*     */         return;
/*     */       }
/*     */       String expression = contentRaw.substring(5).trim();
/*     */       String response = evaluateMathCommand(expression);
/*     */       scheduleResponses(List.of(new ScheduledLine(200L, response)), chatPrefix);
/*     */       return;
/*     */     }
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
/* 551 */     syncScrollableTooltipState();
/* 551 */     syncTextShadowState();
/* 551 */     syncLevelPrefixState();
/* 551 */     ChatCommandsBridge.setShowOwnNameTagEnabled(isShowNameTagEnabled());
/* 551 */     if (!isEnabled()) {
/* 552 */       clearEspData();
/* 553 */       clearCustomHighlightData();
/* 554 */       clearCommissionOverlayData();
/* 554 */       clearTempleSkipData();
/* 554 */       clearBlazePuzzleData();
/* 554 */       clearOdinEggData();
/* 554 */       resetAutoTipState();
/*     */       return;
/*     */     }
/* 556 */     if (this.mc.field_1687 == null || this.mc.field_1724 == null) {
/* 557 */       clearEspData();
/* 558 */       clearCustomHighlightData();
/* 559 */       clearCommissionOverlayData();
/* 559 */       clearTempleSkipData();
/* 559 */       clearBlazePuzzleData();
/* 559 */       clearOdinEggData();
/* 559 */       resetAutoTipState();
/*     */       return;
/*     */     }
/* 560 */     tickAutoTip();
/* 561 */     tickOdinEggEsp();
/* 561 */     updateBlazePuzzleTargets();
/* 562 */     if (((Boolean)this.espEnabled.getValue()).booleanValue()) {
/* 562 */       boolean titaniumOn = ((Boolean)this.titaniumHighlightEnabled.getValue()).booleanValue();
/* 563 */       boolean nodeOn = ((Boolean)this.nodeHighlightEnabled.getValue()).booleanValue();
/* 563 */       boolean chestOn = ((Boolean)this.chestHighlightEnabled.getValue()).booleanValue();
/* 564 */       boolean hideonleafOn = ((Boolean)this.hideonleafHighlightEnabled.getValue()).booleanValue();
/* 564 */       boolean automatonOn = ((Boolean)this.automatonHighlightEnabled.getValue()).booleanValue();
/* 564 */       if (!titaniumOn && !nodeOn && !chestOn && !hideonleafOn && !automatonOn) {
/* 565 */         clearEspData();
/* 566 */         this.espScanInitialized = true;
/*     */       } else {
/* 568 */         this.titaniumTickCounter++;
/* 569 */         class_2338 playerPos = this.mc.field_1724.method_24515();
/* 570 */         int px = playerPos.method_10263();
/* 571 */         int py = playerPos.method_10264();
/* 572 */         int pz = playerPos.method_10260();
/* 573 */         boolean moved = hasMovedForEspScan(px, py, pz);
/* 574 */         int interval = moved ? ESP_SCAN_INTERVAL_TICKS : ESP_SCAN_IDLE_INTERVAL_TICKS;
/* 575 */         if (this.espScanInProgress || !this.espScanInitialized || moved || this.titaniumTickCounter % interval == 0) {
/* 576 */           updateEspBlocks();
/*     */         }
/*     */       } 
/*     */     } else {
/* 580 */       clearEspData();
/*     */     } 
/* 582 */     if (((Boolean)this.customHighlightEnabled.getValue()).booleanValue()) {
/* 583 */       this.customHighlightTickCounter++;
/* 584 */       if (this.customMatchedEntities.isEmpty() || this.customHighlightTickCounter % CUSTOM_HIGHLIGHT_SCAN_INTERVAL_TICKS == 0) {
/* 585 */         updateCustomHighlightData();
/*     */       }
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
/* 594 */     updateTempleSkipSpot();
/*     */     
/*     */   }
/*     */   
/*     */   private void syncScrollableTooltipState() {
/*     */     boolean enabled = isScrollableTooltipsEnabled();
/*     */     try {
/*     */       ScrollableTooltipState.setEnabled(enabled);
/*     */     } catch (Throwable throwable) {}
/*     */   }
/*     */   
/*     */   private void syncTextShadowState() {
/*     */     boolean enabled = isTextShadowRemovalEnabled();
/*     */     try {
/*     */       TextShadowState.setEnabled(enabled);
/*     */     } catch (Throwable throwable) {}
/*     */   }
/*     */   
/*     */   private void syncLevelPrefixState() {
/*     */     boolean enabled = isLevelPrefixEnabled();
/*     */     boolean red = isRed480PlusEnabled();
/*     */     boolean gold = isGoldBracketsEnabled();
/*     */     boolean diamond = isDiamondBracketsEnabled();
/*     */     try {
/*     */       LevelPrefixState.setSettings(enabled, red, gold, diamond);
/*     */     } catch (Throwable throwable) {}
/*     */   }
/*     */   
/*     */   private void tickAutoTip() {
/*     */     if (!((Boolean)this.autoTipEnabled.getValue()).booleanValue()) {
/*     */       resetAutoTipState();
/*     */       return;
/*     */     }
/*     */     if (this.mc == null || this.mc.field_1724 == null || this.mc.field_1724.field_3944 == null) {
/*     */       return;
/*     */     }
/*     */     long intervalMs = getAutoTipIntervalMs();
/*     */     long nowMs = TimeUnit.NANOSECONDS.toMillis(System.nanoTime());
/*     */     if (intervalMs <= 0L) {
/*     */       intervalMs = 5000L;
/*     */     }
/*     */     if (this.autoTipIntervalMs != intervalMs) {
/*     */       this.autoTipIntervalMs = intervalMs;
/*     */       this.autoTipNextSendMs = nowMs + intervalMs;
/*     */       return;
/*     */     }
/*     */     if (this.autoTipNextSendMs == 0L) {
/*     */       this.autoTipNextSendMs = nowMs + intervalMs;
/*     */       return;
/*     */     }
/*     */     if (nowMs >= this.autoTipNextSendMs) {
/*     */       this.mc.field_1724.field_3944.method_45730("tipall");
/*     */       this.autoTipNextSendMs = nowMs + intervalMs;
/*     */     }
/*     */   }
/*     */   
/*     */   private void resetAutoTipState() {
/*     */     this.autoTipNextSendMs = 0L;
/*     */     this.autoTipIntervalMs = 0L;
/*     */   }
/*     */   
/*     */   private long getAutoTipIntervalMs() {
/*     */     try {
/*     */       Object value = this.autoTipIntervalSeconds.getValue();
/*     */       if (value instanceof java.math.BigDecimal) {
/*     */         double seconds = ((java.math.BigDecimal)value).doubleValue();
/*     */         seconds = Math.max(5.0D, Math.min(20.0D, seconds));
/*     */         return Math.round(seconds * 1000.0D);
/*     */       }
/*     */       if (value instanceof Number) {
/*     */         double seconds = ((Number)value).doubleValue();
/*     */         seconds = Math.max(5.0D, Math.min(20.0D, seconds));
/*     */         return Math.round(seconds * 1000.0D);
/*     */       }
/*     */     } catch (Exception exception) {}
/*     */     return 10000L;
/*     */   }
/*     */ 
/*     */   private void tickOdinEggEsp() {
/*     */     if (!shouldTrackOdinEggs()) {
/*     */       clearOdinEggData();
/*     */       return;
/*     */     }
/*     */     pruneOdinEggs();
/*     */     this.odinEggScanTickCounter++;
/*     */     if (this.odinEggsByEntityId.isEmpty() || this.odinEggScanTickCounter % ODIN_EGG_SCAN_INTERVAL_TICKS == 0) {
/*     */       scanForOdinEggs();
/*     */     }
/*     */   }
/*     */ 
/*     */   private void handleOdinEggChat(String message) {
/*     */     if (!shouldTrackOdinEggs() || message == null || message.isBlank()) {
/*     */       return;
/*     */     }
/*     */     if (!ODIN_EGG_MESSAGE_PATTERN.matcher(message).matches()) {
/*     */       return;
/*     */     }
/*     */     class_243 playerPos = this.mc.field_1724.method_33571();
/*     */     if (playerPos == null) {
/*     */       return;
/*     */     }
/*     */     OdinEggData closestEgg = null;
/*     */     double closestDistance = Double.MAX_VALUE;
/*     */     for (OdinEggData egg : this.odinEggsByEntityId.values()) {
/*     */       if (egg.found) {
/*     */         continue;
/*     */       }
/*     */       class_238 box = getOdinEggHeadBox(egg.entity);
/*     */       if (box == null) {
/*     */         continue;
/*     */       }
/*     */       double centerX = (box.field_1323 + box.field_1320) * 0.5D;
/*     */       double centerY = (box.field_1322 + box.field_1325) * 0.5D;
/*     */       double centerZ = (box.field_1321 + box.field_1324) * 0.5D;
/*     */       double dx = centerX - playerPos.method_10216();
/*     */       double dy = centerY - playerPos.method_10214();
/*     */       double dz = centerZ - playerPos.method_10215();
/*     */       double distanceSq = dx * dx + dy * dy + dz * dz;
/*     */       if (distanceSq < closestDistance) {
/*     */         closestDistance = distanceSq;
/*     */         closestEgg = egg;
/*     */       }
/*     */     }
/*     */     if (closestEgg != null) {
/*     */       closestEgg.found = true;
/*     */     }
/*     */   }
/*     */ 
/*     */   private void renderOdinEggEsp() {
/*     */     if (!shouldTrackOdinEggs() || this.odinEggsByEntityId.isEmpty()) {
/*     */       return;
/*     */     }
/*     */     if (this.filledBoxConstructor == null || this.outlineBoxConstructor == null || this.addRenderTaskMethod == null) {
/*     */       return;
/*     */     }
/*     */     if (this.lineConstructor == null) {
/*     */       this.lineConstructor = resolveLineConstructor();
/*     */     }
/*     */     for (OdinEggData egg : this.odinEggsByEntityId.values()) {
/*     */       if (egg.found) {
/*     */         continue;
/*     */       }
/*     */       class_238 headBox = getOdinEggHeadBox(egg.entity);
/*     */       if (headBox == null) {
/*     */         continue;
/*     */       }
/*     */       try {
/*     */         Object filledTask = this.filledBoxConstructor.newInstance(new Object[] { headBox, egg.kind.fillColour, Boolean.valueOf(false) });
/*     */         Object outlineTask = this.outlineBoxConstructor.newInstance(new Object[] { headBox, egg.kind.outlineColour, Boolean.valueOf(false) });
/*     */         this.addRenderTaskMethod.invoke(null, new Object[] { filledTask });
/*     */         this.addRenderTaskMethod.invoke(null, new Object[] { outlineTask });
/*     */         if (this.lineConstructor != null) {
/*     */           double centerX = (headBox.field_1323 + headBox.field_1320) * 0.5D;
/*     */           double centerZ = (headBox.field_1321 + headBox.field_1324) * 0.5D;
/*     */           submitLine(new class_243(centerX, headBox.field_1322, centerZ), new class_243(centerX, headBox.field_1322 + ODIN_EGG_BEAM_HEIGHT, centerZ), egg.kind.outlineColour);
/*     */         }
/*     */       } catch (ReflectiveOperationException ignored) {
/*     */         this.titaniumRenderBridgeReady = false;
/*     */         return;
/*     */       }
/*     */     }
/*     */   }
/*     */ 
/*     */   private void scanForOdinEggs() {
/*     */     Iterable<?> entities = getEntityIterable(this.mc.field_1687);
/*     */     if (entities == null) {
/*     */       clearOdinEggData();
/*     */       return;
/*     */     }
/*     */     Set<Integer> activeEggs = new HashSet<>();
/*     */     for (Object obj : entities) {
/*     */       if (!(obj instanceof class_1531 armorStand)) {
/*     */         continue;
/*     */       }
/*     */       OdinEggKind eggKind = OdinEggKind.fromStack(getArmorStandHeadStack(armorStand));
/*     */       if (eggKind == null) {
/*     */         continue;
/*     */       }
/*     */       int entityId = armorStand.method_5628();
/*     */       activeEggs.add(Integer.valueOf(entityId));
/*     */       if (!this.odinEggsByEntityId.containsKey(Integer.valueOf(entityId))) {
/*     */         this.odinEggsByEntityId.put(Integer.valueOf(entityId), new OdinEggData(entityId, armorStand, eggKind));
/*     */       }
/*     */     }
/*     */     Iterator<Map.Entry<Integer, OdinEggData>> iterator = this.odinEggsByEntityId.entrySet().iterator();
/*     */     while (iterator.hasNext()) {
/*     */       Map.Entry<Integer, OdinEggData> entry = iterator.next();
/*     */       if (!activeEggs.contains(entry.getKey())) {
/*     */         iterator.remove();
/*     */       }
/*     */     }
/*     */   }
/*     */ 
/*     */   private void pruneOdinEggs() {
/*     */     Iterator<Map.Entry<Integer, OdinEggData>> iterator = this.odinEggsByEntityId.entrySet().iterator();
/*     */     while (iterator.hasNext()) {
/*     */       OdinEggData egg = ((Map.Entry<Integer, OdinEggData>)iterator.next()).getValue();
/*     */       if (egg == null || egg.entity == null || getOdinEggHeadBox(egg.entity) == null) {
/*     */         iterator.remove();
/*     */       }
/*     */     }
/*     */   }
/*     */ 
/*     */   private void clearOdinEggData() {
/*     */     this.odinEggsByEntityId.clear();
/*     */     this.odinEggScanTickCounter = 0;
/*     */   }
/*     */ 
/*     */   private boolean shouldTrackOdinEggs() {
/*     */     return (((Boolean)this.odinEggEspEnabled.getValue()).booleanValue() && this.mc != null && this.mc.field_1687 != null && this.mc.field_1724 != null && isInSkyBlockSidebar());
/*     */   }
/*     */ 
/*     */   private boolean isInSkyBlockSidebar() {
/*     */     if (this.mc == null || this.mc.field_1724 == null || this.mc.field_1724.field_3944 == null) {
/*     */       return false;
/*     */     }
/*     */     class_269 scoreboard = this.mc.field_1724.field_3944.method_55823();
/*     */     if (scoreboard != null) {
/*     */       class_8646 displaySlot = (class_8646)class_8646.field_45176.apply(1);
/*     */       class_266 objective = scoreboard.method_1189(displaySlot);
/*     */       if (objective != null && (containsSkyblockText(objective.method_1120()) || containsSkyblockText(objective.method_1114()))) {
/*     */         return true;
/*     */       }
/*     */     }
/*     */     return !getSkyblockArea().isBlank();
/*     */   }
/*     */ 
/*     */   private boolean containsSkyblockText(class_2561 text) {
/*     */     if (text == null) {
/*     */       return false;
/*     */     }
/*     */     String stripped = class_124.method_539(text.getString());
/*     */     return (stripped != null && stripped.toUpperCase(Locale.ROOT).contains("SKYBLOCK"));
/*     */   }
/*     */ 
/*     */   private class_1799 getArmorStandHeadStack(class_1531 armorStand) {
/*     */     if (armorStand == null) {
/*     */       return class_1799.field_8037;
/*     */     }
/*     */     for (class_1304 slot : class_1304.values()) {
/*     */       String slotName = slot.method_5923();
/*     */       if (slotName != null && slotName.toLowerCase(Locale.ROOT).contains("head")) {
/*     */         return armorStand.method_6118(slot);
/*     */       }
/*     */     }
/*     */     return class_1799.field_8037;
/*     */   }
/*     */ 
/*     */   private class_238 getOdinEggHeadBox(class_1531 armorStand) {
/*     */     class_238 entityBox = getEntityBox(armorStand);
/*     */     if (entityBox == null) {
/*     */       return null;
/*     */     }
/*     */     double centerX = (entityBox.field_1323 + entityBox.field_1320) * 0.5D;
/*     */     double centerZ = (entityBox.field_1321 + entityBox.field_1324) * 0.5D;
/*     */     double baseY = entityBox.field_1322;
/*     */     return new class_238(centerX - ODIN_EGG_BOX_HALF_WIDTH, baseY + ODIN_EGG_BOX_MIN_Y, centerZ - ODIN_EGG_BOX_HALF_WIDTH, centerX + ODIN_EGG_BOX_HALF_WIDTH, baseY + ODIN_EGG_BOX_MAX_Y, centerZ + ODIN_EGG_BOX_HALF_WIDTH);
/*     */   }
/*     */ 
/*     */   private static String getSkullTextureValue(class_1799 stack) {
/*     */     class_9296 profile = (class_9296)stack.method_57381(class_9334.field_49617);
/*     */     if (profile == null) {
/*     */       return "";
/*     */     }
/*     */     GameProfile gameProfile = profile.method_73313();
/*     */     if (gameProfile == null || gameProfile.properties() == null) {
/*     */       return "";
/*     */     }
/*     */     for (Property property : gameProfile.properties().get("textures")) {
/*     */       if (property != null && property.value() != null && !property.value().isBlank()) {
/*     */         return property.value();
/*     */       }
/*     */     }
/*     */     return "";
/*     */   }
/*     */ 
/*     */   private static String decodeTextureUrl(String textureValue) {
/*     */     if (textureValue == null || textureValue.isBlank()) {
/*     */       return "";
/*     */     }
/*     */     try {
/*     */       String decoded = new String(Base64.getDecoder().decode(textureValue), StandardCharsets.UTF_8);
/*     */       Matcher matcher = ODIN_TEXTURE_URL_PATTERN.matcher(decoded);
/*     */       return matcher.find() ? matcher.group(1) : "";
/*     */     } catch (IllegalArgumentException exception) {
/*     */       return "";
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
/* 573 */       rebuildEspRenderTasksIfNeeded();
/* 581 */       renderBlockTasks(this.titaniumRenderTasks, (Boolean)this.titaniumHighlightEnabled.getValue());
/* 582 */       renderBlockTasks(this.nodeRenderTasks, (Boolean)this.nodeHighlightEnabled.getValue());
/* 583 */       renderBlockTasks(this.chestRenderTasks, (Boolean)this.chestHighlightEnabled.getValue());
/*     */       renderBlockTasks(this.automatonRenderTasks, (Boolean)this.automatonHighlightEnabled.getValue());
/*     */       renderBlockTasks(this.hideonleafEntityRenderTasks, (Boolean)this.hideonleafHighlightEnabled.getValue());
/*     */     }
/*     */     try {
/* 582 */       renderBlockTasks(this.customEntityRenderTasks, (Boolean)this.customHighlightEnabled.getValue());
/* 583 */     } catch (Throwable throwable) {
/* 584 */       clearCustomHighlightData();
/*     */     } 
/*     */     renderOdinEggEsp();
/*     */     renderTempleSkip(event);
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
/* 603 */       if (this.commissionOverlayTickCounter % COMMISSION_SCAN_INTERVAL_TICKS == 0) {
/* 604 */         updateCommissionOverlayData();
/*     */       }
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
/*     */   private void registerScrollableTooltipHooks() {
/*     */   }
/*     */   
/*     */   private void updateScrollableTooltipScroll(class_437 screen, double mouseX, double mouseY, double horizontalAmount, double verticalAmount) {
/*     */     if (!isScrollableTooltipEnabled() || screen == null) {
/*     */       return;
/*     */     }
/*     */     Object slot = resolveHoveredSlot(screen, mouseX, mouseY);
/*     */     if (slot == null) {
/*     */       clearScrollableTooltipState(screen);
/*     */       return;
/*     */     }
/*     */     class_1799 stack = resolveSlotStack(slot);
/*     */     if (stack == null || stack.method_7960()) {
/*     */       clearScrollableTooltipState(screen);
/*     */       return;
/*     */     }
/*     */     double rawAmount = (verticalAmount != 0.0D) ? verticalAmount : horizontalAmount;
/*     */     int delta = (int)Math.round(rawAmount * 12.0D);
/*     */     if (delta == 0) {
/*     */       return;
/*     */     }
/*     */     if (this.tooltipScrollScreen != screen || this.tooltipScrollSlot != slot) {
/*     */       this.tooltipScrollScreen = screen;
/*     */       this.tooltipScrollSlot = slot;
/*     */       this.tooltipScrollOffset = 0;
/*     */     }
/*     */     this.tooltipScrollOffset = clampTooltipScrollOffset(this.tooltipScrollOffset + delta);
/*     */   }
/*     */   
/*     */   private void renderScrollableTooltip(class_437 screen, class_332 drawContext, int mouseX, int mouseY) {
/*     */     if (!isScrollableTooltipEnabled() || screen == null || drawContext == null) {
/*     */       clearScrollableTooltipState(screen);
/*     */       return;
/*     */     }
/*     */     Object slot = resolveHoveredSlot(screen, mouseX, mouseY);
/*     */     if (slot == null) {
/*     */       clearScrollableTooltipState(screen);
/*     */       return;
/*     */     }
/*     */     class_1799 stack = resolveSlotStack(slot);
/*     */     if (stack == null || stack.method_7960()) {
/*     */       clearScrollableTooltipState(screen);
/*     */       return;
/*     */     }
/*     */     if (this.tooltipScrollScreen != screen || this.tooltipScrollSlot != slot) {
/*     */       this.tooltipScrollScreen = screen;
/*     */       this.tooltipScrollSlot = slot;
/*     */       this.tooltipScrollOffset = 0;
/*     */     }
/*     */     if (this.tooltipScrollOffset == 0) {
/*     */       return;
/*     */     }
/*     */     Method drawMethod = resolveTooltipDrawMethod(screen);
/*     */     if (drawMethod == null) {
/*     */       return;
/*     */     }
/*     */     int adjustedY = mouseY + this.tooltipScrollOffset;
/*     */     try {
/*     */       drawContext.method_71048();
/*     */       drawMethod.invoke(screen, new Object[] { drawContext, Integer.valueOf(mouseX), Integer.valueOf(adjustedY) });
/*     */     } catch (ReflectiveOperationException reflectiveOperationException) {}
/*     */   }
/*     */   
/*     */   private boolean isScrollableTooltipEnabled() {
/*     */     return (isEnabled() && ((Boolean)this.scrollableTooltips.getValue()).booleanValue());
/*     */   }
/*     */   
/*     */   private int clampTooltipScrollOffset(int offset) {
/*     */     if (offset > TOOLTIP_SCROLL_LIMIT) {
/*     */       return TOOLTIP_SCROLL_LIMIT;
/*     */     }
/*     */     if (offset < -TOOLTIP_SCROLL_LIMIT) {
/*     */       return -TOOLTIP_SCROLL_LIMIT;
/*     */     }
/*     */     return offset;
/*     */   }
/*     */   
/*     */   private void clearScrollableTooltipState(Object screen) {
/*     */     if (screen != null && screen != this.tooltipScrollScreen) {
/*     */       return;
/*     */     }
/*     */     this.tooltipScrollOffset = 0;
/*     */     this.tooltipScrollSlot = null;
/*     */     this.tooltipScrollScreen = null;
/*     */   }
/*     */   
/*     */   private Object resolveHoveredSlot(class_437 screen, double mouseX, double mouseY) {
/*     */     Object hovered = resolveHoveredSlotField(screen);
/*     */     if (hovered != null) {
/*     */       return hovered;
/*     */     }
/*     */     Method slotAtMethod = resolveSlotAtMethod(screen);
/*     */     if (slotAtMethod == null) {
/*     */       return null;
/*     */     }
/*     */     try {
/*     */       return slotAtMethod.invoke(screen, new Object[] { Double.valueOf(mouseX), Double.valueOf(mouseY) });
/*     */     } catch (ReflectiveOperationException reflectiveOperationException) {
/*     */       return null;
/*     */     } 
/*     */   }
/*     */   
/*     */   private Object resolveHoveredSlotField(class_437 screen) {
/*     */     if (!(screen instanceof class_465)) {
/*     */       return null;
/*     */     }
/*     */     try {
/*     */       if (this.tooltipHoveredSlotField == null) {
/*     */         Field field = class_465.class.getDeclaredField("field_2787");
/*     */         field.setAccessible(true);
/*     */         this.tooltipHoveredSlotField = field;
/*     */       }
/*     */       return this.tooltipHoveredSlotField.get(screen);
/*     */     } catch (Throwable throwable) {
/*     */       return null;
/*     */     } 
/*     */   }
/*     */   
/*     */   private class_1799 resolveSlotStack(Object slot) {
/*     */     if (slot == null) {
/*     */       return null;
/*     */     }
/*     */     try {
/*     */       Object result = invokeNoArg(slot, new String[] { "method_7677", "getStack" });
/*     */       if (result instanceof class_1799) {
/*     */         return (class_1799)result;
/*     */       }
/*     */     } catch (ReflectiveOperationException reflectiveOperationException) {}
/*     */     return null;
/*     */   }
/*     */   
/*     */   private Method resolveTooltipDrawMethod(class_437 screen) {
/*     */     if (screen == null) {
/*     */       return null;
/*     */     }
/*     */     if (this.tooltipDrawTooltipMethod != null && this.tooltipDrawTooltipMethod.getDeclaringClass().isInstance(screen)) {
/*     */       return this.tooltipDrawTooltipMethod;
/*     */     }
/*     */     Class<?> current = screen.getClass();
/*     */     while (current != null) {
/*     */       try {
/*     */         Method method = current.getDeclaredMethod("method_2380", new Class[] { class_332.class, int.class, int.class });
/*     */         method.setAccessible(true);
/*     */         this.tooltipDrawTooltipMethod = method;
/*     */         return method;
/*     */       } catch (NoSuchMethodException noSuchMethodException) {
/*     */         current = current.getSuperclass();
/*     */       } 
/*     */     } 
/*     */     return null;
/*     */   }
/*     */   
/*     */   private Method resolveSlotAtMethod(class_437 screen) {
/*     */     if (screen == null) {
/*     */       return null;
/*     */     }
/*     */     if (this.tooltipSlotAtMethod != null && this.tooltipSlotAtMethod.getDeclaringClass().isInstance(screen)) {
/*     */       return this.tooltipSlotAtMethod;
/*     */     }
/*     */     Class<?> current = screen.getClass();
/*     */     while (current != null) {
/*     */       try {
/*     */         Method method = current.getDeclaredMethod("method_64240", new Class[] { double.class, double.class });
/*     */         method.setAccessible(true);
/*     */         this.tooltipSlotAtMethod = method;
/*     */         return method;
/*     */       } catch (NoSuchMethodException noSuchMethodException) {
/*     */         current = current.getSuperclass();
/*     */       } 
/*     */     } 
/*     */     return null;
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
/*     */   private String getCommissionOverlayThemeValue() {
/*     */     if (this.commissionOverlayTheme == null) {
/*     */       return "RSA";
/*     */     }
/*     */     String value = (String)this.commissionOverlayTheme.getValue();
/*     */     return (value == null || value.isBlank()) ? "RSA" : value;
/*     */   }
/*     */   
/*     */   private boolean isCommissionOverlayTheme(String name) {
/*     */     String value = getCommissionOverlayThemeValue();
/*     */     return (value != null && value.equalsIgnoreCase(name));
/*     */   }
/*     */   
/*     */   private boolean isCommissionOverlayCustomTheme() {
/*     */     return (((Boolean)this.commissionOverlayEnabled.getValue()).booleanValue() && isCommissionOverlayTheme("Custom"));
/*     */   }
/*     */   
/*     */   private Colour getCommissionOverlayBorderColour() {
/*     */     if (isCommissionOverlayTheme("RSM")) {
/*     */       return COMMISSION_PANEL_OUTLINE_RSM;
/*     */     }
/*     */     if (isCommissionOverlayTheme("Custom") && this.commissionOverlayCustomBorder != null) {
/*     */       return (Colour)this.commissionOverlayCustomBorder.getValue();
/*     */     }
/*     */     return COMMISSION_PANEL_OUTLINE;
/*     */   }
/*     */   
/*     */   private Colour getCommissionProgressStartColour() {
/*     */     if (isCommissionOverlayTheme("RSM")) {
/*     */       return COMMISSION_PROGRESS_START_RSM;
/*     */     }
/*     */     if (isCommissionOverlayTheme("Custom") && this.commissionOverlayCustomProgressStart != null) {
/*     */       return (Colour)this.commissionOverlayCustomProgressStart.getValue();
/*     */     }
/*     */     return COMMISSION_PROGRESS_START;
/*     */   }
/*     */   
/*     */   private Colour getCommissionProgressEndColour() {
/*     */     if (isCommissionOverlayTheme("RSM")) {
/*     */       return COMMISSION_PROGRESS_END_RSM;
/*     */     }
/*     */     if (isCommissionOverlayTheme("Custom") && this.commissionOverlayCustomProgressEnd != null) {
/*     */       return (Colour)this.commissionOverlayCustomProgressEnd.getValue();
/*     */     }
/*     */     return COMMISSION_PROGRESS_END;
/*     */   }
/*     */   
/*     */   private String getCommissionOverlayCustomTextValue() {
/*     */     if (this.commissionOverlayCustomText == null) {
/*     */       return "";
/*     */     }
/*     */     String value = (String)this.commissionOverlayCustomText.getValue();
/*     */     return (value == null) ? "" : value;
/*     */   }
/*     */   
/*     */   private float getCommissionFooterWidth(float textSize) {
/*     */     if (isCommissionOverlayTheme("Custom")) {
/*     */       String text = getCommissionOverlayCustomTextValue();
/*     */       if (text.isEmpty()) {
/*     */         return 0.0F;
/*     */       }
/*     */       return NVGUtils.getTextWidth(text, textSize, NVGUtils.ROBOTO);
/*     */     }
/*     */     String footerText = isCommissionOverlayTheme("RSM") ? "RSM" : "RSA";
/*     */     float total = 0.0F;
/*     */     for (int i = 0; i < footerText.length(); i++) {
/*     */       total += NVGUtils.getTextWidth(footerText.substring(i, i + 1), textSize, NVGUtils.ROBOTO);
/*     */     }
/*     */     return total;
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
/* 673 */     float footerWidth = getCommissionFooterWidth(rsaSize);
/* 677 */     float titleHeight = NVGUtils.getTextHeight(titleSize, NVGUtils.ROBOTO);
/* 678 */     float lineHeight = NVGUtils.getTextHeight(lineSize, NVGUtils.ROBOTO);
/* 679 */     float rsaHeight = NVGUtils.getTextHeight(rsaSize, NVGUtils.ROBOTO);
/* 680 */     float bodyStartY = padding + titleHeight + 12.0F;
/* 681 */     int bodyLines = Math.max(0, this.commissionOverlayLines.size() - 1);
/* 682 */     float bodyHeight = bodyLines * (lineHeight + barTopGap + barHeight) + Math.max(0, bodyLines - 1) * lineGap;
/* 683 */     float rsaY = bodyStartY + bodyHeight + footerGap;
/* 684 */     float boxWidth = Math.max(minWidth, Math.max(maxWidth + padding * 2.0F + 16.0F, footerWidth + padding * 2.0F + 16.0F));
/* 685 */     float boxHeight = rsaY + rsaHeight + padding;
/* 686 */     return new CommissionOverlayMetrics(boxWidth, boxHeight, padding, titleSize, lineSize, rsaSize, lineGap, barTopGap, barHeight, barRadius, padding, bodyStartY, rsaY, radius, outlineThickness);
/*     */   }
/*     */   
/*     */   private void renderCommissionOverlay(float left, float top, CommissionOverlayMetrics metrics) {
/* 691 */     if (metrics == null || this.commissionOverlayLines.isEmpty()) {
/*     */       return;
/*     */     }
/* 693 */     updateCommissionProgressAnimation();
/* 694 */     float boxWidth = metrics.boxWidth();
/* 695 */     float boxHeight = metrics.boxHeight();
/* 696 */     float padding = metrics.padding();
/* 697 */     NVGUtils.drawRect(left, top, boxWidth, boxHeight, metrics.radius(), COMMISSION_PANEL_FILL);
/* 698 */     NVGUtils.drawOutlineRect(left, top, boxWidth, boxHeight, metrics.radius(), metrics.outlineThickness(), getCommissionOverlayBorderColour());
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
/* 634 */       double progress = getCommissionProgressPercent(line);
/* 635 */       double animatedProgress = getAnimatedCommissionProgress(line, progress);
/* 636 */       String renderedLine = getAnimatedCommissionLine(line, animatedProgress, progress);
/* 637 */       Colour color = getCommissionLineColour(renderedLine, animatedProgress);
/* 638 */       NVGUtils.drawText(renderedLine, left + padding, textY, metrics.lineSize(), color, NVGUtils.ROBOTO);
/* 637 */       if (progress < 0.0D) {
/* 638 */         progress = 0.0D;
/*     */       }
/* 639 */       if (animatedProgress >= 0.0D) {
/* 640 */         progress = animatedProgress;
/*     */       }
/* 640 */       float barY = textY + lineHeight + metrics.barTopGap();
/* 641 */       float barWidth = boxWidth - padding * 2.0F;
/* 642 */       drawCommissionProgressBar(left + padding, barY, barWidth, metrics.barHeight(), metrics.barRadius(), progress, getCommissionProgressStartColour(), getCommissionProgressEndColour());
/* 643 */       textY += lineHeight + metrics.barTopGap() + metrics.barHeight() + metrics.lineGap();
/*     */     } 
/* 638 */     float rsaSize = metrics.rsaSize();
/* 639 */     float rsaY = top + metrics.rsaY();
/* 640 */     if (isCommissionOverlayTheme("Custom")) {
/* 641 */       String customText = getCommissionOverlayCustomTextValue();
/* 642 */       if (!customText.isEmpty()) {
/* 643 */         float textWidth = NVGUtils.getTextWidth(customText, rsaSize, NVGUtils.ROBOTO);
/* 644 */         float textX = left + (boxWidth - textWidth) / 2.0F;
/* 645 */         Colour textColour = (this.commissionOverlayCustomTextColour != null) ? (Colour)this.commissionOverlayCustomTextColour.getValue() : COMMISSION_TEXT_DEFAULT;
/* 646 */         NVGUtils.drawText(customText, textX, rsaY, rsaSize, textColour, NVGUtils.ROBOTO);
/*     */       }
/*     */     } else {
/* 649 */       String footerText = isCommissionOverlayTheme("RSM") ? "RSM" : "RSA";
/* 650 */       Colour[] footerColours = isCommissionOverlayTheme("RSM") ? new Colour[] { RSM_R_COLOUR, RSM_S_COLOUR, RSM_M_COLOUR } : new Colour[] { RSA_R_COLOUR, RSA_S_COLOUR, RSA_A_COLOUR };
/* 651 */       float totalWidth = 0.0F;
/* 652 */       float[] widths = new float[footerText.length()];
/* 653 */       for (int i = 0; i < footerText.length(); i++) {
/* 654 */         widths[i] = NVGUtils.getTextWidth(footerText.substring(i, i + 1), rsaSize, NVGUtils.ROBOTO);
/* 655 */         totalWidth += widths[i];
/*     */       }
/* 657 */       float rsaX = left + (boxWidth - totalWidth) / 2.0F;
/* 658 */       for (int i = 0; i < footerText.length(); i++) {
/* 659 */         NVGUtils.drawText(footerText.substring(i, i + 1), rsaX, rsaY, rsaSize, footerColours[i], NVGUtils.ROBOTO);
/* 660 */         rsaX += widths[i];
/*     */       }
/*     */     }
/*     */   }
/*     */   
/*     */   private Colour getCommissionLineColour(String line, double animatedPercent) {
/* 641 */     if (line == null || line.isBlank()) {
/* 642 */       return COMMISSION_TEXT_DEFAULT;
/*     */     }
/* 644 */     double percent = animatedPercent;
/* 645 */     if (percent < 0.0D) {
/* 646 */       percent = getCommissionProgressPercent(line);
/*     */     }
/* 645 */     if (percent >= 0.0D) {
/* 646 */       if (percent <= 0.0D) return COMMISSION_TEXT_ZERO; 
/* 647 */       if (percent >= 100.0D) return COMMISSION_TEXT_DONE; 
/* 648 */       return COMMISSION_TEXT_PROGRESS;
/*     */     }
/* 657 */     return COMMISSION_TEXT_DEFAULT;
/*     */   }
/*     */   
/*     */   private void updateCommissionProgressAnimation() {
/* 659 */     long now = System.nanoTime();
/* 660 */     long deltaNanos = now - this.commissionAnimationLastNanos;
/* 661 */     this.commissionAnimationLastNanos = now;
/* 662 */     if (deltaNanos <= 0L || this.commissionProgressTargets.isEmpty()) {
/*     */       return;
/*     */     }
/* 665 */     double deltaSeconds = Math.min(0.25D, deltaNanos / 1.0E9D);
/* 666 */     double blend = 1.0D - Math.exp(-COMMISSION_ANIMATION_SPEED * deltaSeconds);
/* 667 */     for (String key : this.commissionProgressTargets.keySet()) {
/* 668 */       double target = ((Double)this.commissionProgressTargets.get(key)).doubleValue();
/* 669 */       double current = ((Double)this.commissionProgressDisplayed.getOrDefault(key, Double.valueOf(target))).doubleValue();
/* 670 */       double next = current + (target - current) * blend;
/* 671 */       if (Math.abs(target - next) <= COMMISSION_PERCENT_EPSILON) {
/* 672 */         next = target;
/*     */       }
/* 674 */       this.commissionProgressDisplayed.put(key, Double.valueOf(next));
/*     */     } 
/*     */   }
/*     */   
/*     */   private double getAnimatedCommissionProgress(String line, double fallbackPercent) {
/* 679 */     if (fallbackPercent < 0.0D || line == null || line.isBlank()) {
/* 680 */       return fallbackPercent;
/*     */     }
/* 682 */     String key = getCommissionProgressKey(line);
/* 683 */     if (key.isEmpty()) {
/* 684 */       return fallbackPercent;
/*     */     }
/* 686 */     return ((Double)this.commissionProgressDisplayed.getOrDefault(key, Double.valueOf(fallbackPercent))).doubleValue();
/*     */   }
/*     */   
/*     */   private String getAnimatedCommissionLine(String line, double animatedPercent, double targetPercent) {
/* 690 */     if (line == null || line.isBlank() || animatedPercent < 0.0D || targetPercent < 0.0D) {
/* 691 */       return line;
/*     */     }
/* 693 */     Matcher matcher = COMMISSION_PERCENT_PATTERN.matcher(line);
/* 694 */     if (!matcher.find()) {
/* 695 */       return line;
/*     */     }
/* 697 */     String animatedText = formatAnimatedCommissionPercent(animatedPercent, targetPercent);
/* 698 */     return line.substring(0, matcher.start(1)) + animatedText + line.substring(matcher.end(1));
/*     */   }
/*     */   
/*     */   private String formatAnimatedCommissionPercent(double animatedPercent, double targetPercent) {
/* 702 */     double clampedAnimated = Math.max(0.0D, Math.min(100.0D, animatedPercent));
/* 703 */     double value = clampedAnimated;
/* 704 */     if (Math.abs(clampedAnimated - targetPercent) <= COMMISSION_PERCENT_EPSILON) {
/* 705 */       value = targetPercent;
/*     */     }
/* 707 */     if (((Boolean)this.commissionRoundProgressNumbers.getValue()).booleanValue()) {
/* 708 */       double rounded = Math.round(value);
/* 709 */       rounded = Math.max(0.0D, Math.min(100.0D, rounded));
/* 710 */       return String.format(Locale.ROOT, "%.0f", rounded);
/*     */     }
/* 712 */     double clampedValue = Math.max(0.0D, Math.min(100.0D, value));
/* 713 */     return String.format(Locale.ROOT, "%.1f", clampedValue);
/*     */   }
/*     */   
/*     */   private String getCommissionProgressKey(String line) {
/* 710 */     if (line == null || line.isBlank()) {
/* 711 */       return "";
/*     */     }
/* 713 */     String normalized = normalizeTabLine(line).toLowerCase(Locale.ROOT);
/* 714 */     int colonIndex = normalized.indexOf(':');
/* 715 */     if (colonIndex > 0) {
/* 716 */       String left = normalized.substring(0, colonIndex).trim();
/* 717 */       if (!left.isEmpty()) {
/* 718 */         return left;
/*     */       }
/*     */     } 
/* 721 */     return normalized;
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
/*     */   private void drawCommissionProgressBar(float x, float y, float width, float height, float radius, double percent, Colour startColour, Colour endColour) {
/* 679 */     if (width <= 0.0F || height <= 0.0F) {
/*     */       return;
/*     */     }
/* 682 */     NVGUtils.drawRect(x, y, width, height, radius, COMMISSION_PROGRESS_TRACK);
/* 683 */     float clampedPercent = (float)Math.max(0.0D, Math.min(100.0D, percent));
/* 684 */     float fillWidth = width * clampedPercent / 100.0F;
/* 685 */     if (fillWidth <= 0.5F) {
/*     */       return;
/*     */     }
/* 686 */     if (startColour == null) {
/* 687 */       startColour = COMMISSION_PROGRESS_START;
/*     */     }
/* 688 */     if (endColour == null) {
/* 689 */       endColour = COMMISSION_PROGRESS_END;
/*     */     }
/* 690 */     NVGUtils.drawGradientRect(x, y, fillWidth, height, radius, startColour, endColour, Gradient.LeftToRight);
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
/* 663 */     this.commissionProgressTargets.clear();
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
/* 704 */       double percent = getCommissionProgressPercent(line);
/* 705 */       if (percent >= 0.0D) {
/* 706 */         String key = getCommissionProgressKey(line);
/* 707 */         if (!key.isEmpty()) {
/* 708 */           this.commissionProgressTargets.put(key, Double.valueOf(percent));
/* 709 */           this.commissionProgressDisplayed.putIfAbsent(key, Double.valueOf(percent));
/*     */         }
/*     */       }
/*     */     } 
/* 705 */     if (this.commissionOverlayLines.size() <= 1) {
/* 706 */       this.commissionOverlayLines.clear();
/* 707 */       this.commissionHeaderDetected = false;
/* 708 */       this.commissionProgressTargets.clear();
/* 709 */       this.commissionProgressDisplayed.clear();
/*     */     } else {
/* 711 */       this.commissionProgressDisplayed.keySet().removeIf(key -> !this.commissionProgressTargets.containsKey(key));
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
/* 788 */     this.commissionProgressTargets.clear();
/* 789 */     this.commissionProgressDisplayed.clear();
/* 790 */     this.commissionAnimationLastNanos = System.nanoTime();
/*     */   }
/*     */   
/*     */   private void handlePickaxeAbilityChat(String message) {
/*     */     if (!((Boolean)this.pickaxeAbilityCooldownEnabled.getValue()).booleanValue()) {
/*     */       return;
/*     */     }
/*     */     if (message == null || message.isBlank()) {
/*     */       return;
/*     */     }
/*     */     if (message.contains("New buff: -20% Pickaxe Ability cooldowns.")) {
/*     */       this.skyMallPickaxeCooldownActive = true;
/*     */       this.skyMallPickaxeCooldownLastBuffMs = getPickaxeAbilityTimeMs();
/*     */       return;
/*     */     }
/*     */     if (message.contains("New day! Your Sky Mall buff changed!")) {
/*     */       if (!this.skyMallPickaxeCooldownActive) {
/*     */         return;
/*     */       }
/*     */       long nowMs = getPickaxeAbilityTimeMs();
/*     */       if (nowMs - this.skyMallPickaxeCooldownLastBuffMs >= SKY_MALL_PICKAXE_GRACE_MS) {
/*     */         this.skyMallPickaxeCooldownActive = false;
/*     */       }
/*     */       return;
/*     */     }
/*     */     Matcher matcher = PICKAXE_ABILITY_USED_PATTERN.matcher(message);
/*     */     String abilityName = null;
/*     */     if (matcher.find()) {
/*     */       abilityName = normalizePickaxeAbilityName(matcher.group(1));
/*     */     } else {
/*     */       abilityName = extractPickaxeAbilityNameFromLine(message);
/*     */     }
/*     */     if (abilityName == null) {
/*     */       return;
/*     */     }
/*     */     int baseSeconds = getPickaxeAbilityBaseSeconds(abilityName);
/*     */     if (baseSeconds <= 0) {
/*     */       return;
/*     */     }
/*     */     double reductionPercent = getPickaxeAbilityCooldownReductionPercent();
/*     */     double finalSeconds = baseSeconds * (1.0D - reductionPercent / 100.0D);
/*     */     if (finalSeconds < 0.0D) {
/*     */       finalSeconds = 0.0D;
/*     */     }
/*     */     long nowMs = getPickaxeAbilityTimeMs();
/*     */     long endMs = nowMs + Math.round(finalSeconds * 1000.0D);
/*     */     this.pickaxeAbilityCooldowns.put(abilityName, Long.valueOf(endMs));
/*     */   }
/*     */   
/*     */   private void updatePickaxeAbilityCooldowns() {
/*     */     if (!((Boolean)this.pickaxeAbilityCooldownEnabled.getValue()).booleanValue()) {
/*     */       clearPickaxeAbilityCooldowns();
/*     */       return;
/*     */     }
/*     */     if (this.pickaxeAbilityCooldowns.isEmpty()) {
/*     */       return;
/*     */     }
/*     */     long nowMs = getPickaxeAbilityTimeMs();
/*     */     List<String> ready = new ArrayList<>();
/*     */     for (String ability : new ArrayList<>(this.pickaxeAbilityCooldowns.keySet())) {
/*     */       long endTime = ((Long)this.pickaxeAbilityCooldowns.get(ability)).longValue();
/*     */       if (nowMs >= endTime) {
/*     */         ready.add(ability);
/*     */       }
/*     */     }
/*     */     for (String ability : ready) {
/*     */       this.pickaxeAbilityCooldowns.remove(ability);
/*     */       ChatUtils.chat(String.valueOf(class_124.field_1060) + "Pickaxe ability off cooldown", new Object[0]);
/*     */     }
/*     */   }
/*     */   
/*     */   private void clearPickaxeAbilityCooldowns() {
/*     */     this.pickaxeAbilityCooldowns.clear();
/*     */     this.recentPickaxeMessageOutputs.clear();
/*     */     this.skyMallPickaxeCooldownActive = false;
/*     */     this.skyMallPickaxeCooldownLastBuffMs = 0L;
/*     */   }
/*     */   
/*     */   private void queueChatSuppression(String message) {
/*     */     if (message == null || message.isBlank()) {
/*     */       return;
/*     */     }
/*     */     this.pendingChatSuppressions.add(message);
/*     */   }
/*     */   
/*     */   private void applyChatSuppressions() {
/*     */     if (this.pendingChatSuppressions.isEmpty()) {
/*     */       return;
/*     */     }
/*     */     Object chatHud = resolveChatHud();
/*     */     if (chatHud == null) {
/*     */       return;
/*     */     }
/*     */     List<String> targets = new ArrayList<>(this.pendingChatSuppressions);
/*     */     this.pendingChatSuppressions.clear();
/*     */     for (String target : targets) {
/*     */       removeMatchingChatEntries(chatHud, target);
/*     */     }
/*     */   }
/*     */   
/*     */   private Object resolveChatHud() {
/*     */     if (this.mc == null || this.mc.field_1705 == null) {
/*     */       return null;
/*     */     }
/*     */     Object hud = this.mc.field_1705;
/*     */     try {
/*     */       Object chatHud = invokeNoArg(hud, new String[] { "getChatHud", "method_1753", "method_1803", "method_1743" });
/*     */       if (chatHud != null) {
/*     */         return chatHud;
/*     */       }
/*     */     } catch (ReflectiveOperationException reflectiveOperationException) {}
/*     */     for (Method method : hud.getClass().getMethods()) {
/*     */       if (method.getParameterCount() != 0) {
/*     */         continue;
/*     */       }
/*     */       try {
/*     */         Object result = method.invoke(hud, new Object[0]);
/*     */         if (result == null) {
/*     */           continue;
/*     */         }
/*     */         String name = result.getClass().getName();
/*     */         if (name.endsWith("class_338") || name.toLowerCase(Locale.ROOT).contains("chathud")) {
/*     */           return result;
/*     */         }
/*     */       } catch (ReflectiveOperationException reflectiveOperationException) {}
/*     */     } 
/*     */     return null;
/*     */   }
/*     */   
/*     */   private void removeMatchingChatEntries(Object chatHud, String target) {
/*     */     String normalizedTarget = normalizeChatText(target);
/*     */     if (normalizedTarget == null || normalizedTarget.isBlank()) {
/*     */       return;
/*     */     }
/*     */     for (Field field : chatHud.getClass().getDeclaredFields()) {
/*     */       if (!List.class.isAssignableFrom(field.getType())) {
/*     */         continue;
/*     */       }
/*     */       try {
/*     */         field.setAccessible(true);
/*     */         Object value = field.get(chatHud);
/*     */         if (!(value instanceof List)) {
/*     */           continue;
/*     */         }
/*     */         List<?> list = (List)value;
/*     */         list.removeIf(entry -> chatEntryMatches(entry, normalizedTarget));
/*     */       } catch (Throwable throwable) {}
/*     */     } 
/*     */   }
/*     */   
/*     */   private boolean chatEntryMatches(Object entry, String target) {
/*     */     String text = extractChatEntryText(entry);
/*     */     if (text == null || text.isBlank()) {
/*     */       return false;
/*     */     }
/*     */     String normalized = normalizeChatText(text);
/*     */     if (normalized == null || normalized.isBlank()) {
/*     */       return false;
/*     */     }
/*     */     String normalizedLower = normalized.toLowerCase(Locale.ROOT);
/*     */     String targetLower = target.toLowerCase(Locale.ROOT);
/*     */     if (normalizedLower.equals(targetLower)) {
/*     */       return true;
/*     */     }
/*     */     if (normalizedLower.contains(targetLower)) {
/*     */       return true;
/*     */     }
/*     */     return (targetLower.startsWith(normalizedLower) || targetLower.endsWith(normalizedLower));
/*     */   }
/*     */   
/*     */   private String extractChatEntryText(Object entry) {
/*     */     if (entry == null) {
/*     */       return null;
/*     */     }
/*     */     if (entry instanceof String) {
/*     */       return (String)entry;
/*     */     }
/*     */     if (entry instanceof class_2561) {
/*     */       return ((class_2561)entry).getString();
/*     */     }
/*     */     try {
/*     */       Object text = invokeNoArg(entry, new String[] { "getString" });
/*     */       if (text instanceof String) {
/*     */         return (String)text;
/*     */       }
/*     */     } catch (ReflectiveOperationException reflectiveOperationException) {}
/*     */     try {
/*     */       Object text = invokeNoArg(entry, new String[] { "getMessage", "getContent", "getText" });
/*     */       if (text instanceof class_2561) {
/*     */         return ((class_2561)text).getString();
/*     */       }
/*     */       if (text instanceof String) {
/*     */         return (String)text;
/*     */       }
/*     */     } catch (ReflectiveOperationException reflectiveOperationException) {}
/*     */     for (Field field : entry.getClass().getDeclaredFields()) {
/*     */       try {
/*     */         field.setAccessible(true);
/*     */         Object value = field.get(entry);
/*     */         if (value instanceof class_2561) {
/*     */           return ((class_2561)value).getString();
/*     */         }
/*     */         if (value instanceof String) {
/*     */           return (String)value;
/*     */         }
/*     */       } catch (Throwable throwable) {}
/*     */     } 
/*     */     return entry.toString();
/*     */   }
/*     */   
/*     */   private String normalizeChatText(String text) {
/*     */     if (text == null) {
/*     */       return null;
/*     */     }
/*     */     String stripped = class_124.method_539(text);
/*     */     if (stripped == null) {
/*     */       return text.trim();
/*     */     }
/*     */     return stripped.trim();
/*     */   }
/*     */   
/*     */   private String extractPickaxeAbilityNameFromLine(String message) {
/*     */     if (message == null || message.isBlank()) {
/*     */       return null;
/*     */     }
/*     */     String lower = message.toLowerCase(Locale.ROOT);
/*     */     for (String ability : PICKAXE_ABILITY_NAMES) {
/*     */       if (lower.contains(ability)) {
/*     */         return normalizePickaxeAbilityName(ability);
/*     */       }
/*     */     }
/*     */     return null;
/*     */   }
/*     */   
/*     */   private String extractCooldownSeconds(String message) {
/*     */     if (message == null || message.isBlank()) {
/*     */       return null;
/*     */     }
/*     */     Matcher matcher = Pattern.compile("([0-9]+(?:\\.[0-9]+)?)\\s*s", Pattern.CASE_INSENSITIVE).matcher(message);
/*     */     if (!matcher.find()) {
/*     */       return null;
/*     */     }
/*     */     return matcher.group(1);
/*     */   }
/*     */   
/*     */   private long getPickaxeAbilityTimeMs() {
/*     */     return TimeUnit.NANOSECONDS.toMillis(System.nanoTime());
/*     */   }
/*     */   
/*     */   private boolean handlePickaxeCooldownMessage(String message) {
/*     */     if (!((Boolean)this.pickaxeAbilityCooldownEnabled.getValue()).booleanValue()) {
/*     */       return false;
/*     */     }
/*     */     if (message == null || message.isBlank()) {
/*     */       return false;
/*     */     }
/*     */     Matcher matcher = PICKAXE_COOLDOWN_CHAT_PATTERN.matcher(message);
/*     */     String seconds = null;
/*     */     if (matcher.find()) {
/*     */       seconds = matcher.group(1);
/*     */     } else {
/*     */       seconds = extractCooldownSeconds(message);
/*     */     }
/*     */     if (seconds == null) {
/*     */       return false;
/*     */     }
/*     */     if (wasRecentlyHandledPickaxeMessage(message)) {
/*     */       return true;
/*     */     }
/*     */     String displaySeconds = seconds;
/*     */     Double remaining = getShortestPickaxeCooldownSeconds();
/*     */     if (remaining != null) {
/*     */       displaySeconds = formatCooldownSeconds(remaining);
/*     */     }
/*     */     ChatUtils.chat(String.valueOf(class_124.field_1060) + "Ability on cooldown for " + displaySeconds + "s", new Object[0]);
/*     */     recordPickaxeMessageHandled(message);
/*     */     return true;
/*     */   }
/*     */   
/*     */   private boolean handlePickaxeAbilityUsedMessage(String message) {
/*     */     if (!((Boolean)this.pickaxeAbilityCooldownEnabled.getValue()).booleanValue()) {
/*     */       return false;
/*     */     }
/*     */     if (message == null || message.isBlank()) {
/*     */       return false;
/*     */     }
/*     */     Matcher matcher = PICKAXE_USED_CHAT_PATTERN.matcher(message);
/*     */     String abilityName = null;
/*     */     if (matcher.find()) {
/*     */       abilityName = normalizePickaxeAbilityName(matcher.group(1));
/*     */     } else {
/*     */       abilityName = extractPickaxeAbilityNameFromLine(message);
/*     */     }
/*     */     if (abilityName == null) {
/*     */       return false;
/*     */     }
/*     */     if (wasRecentlyHandledPickaxeMessage(message)) {
/*     */       return true;
/*     */     }
/*     */     ChatUtils.chat(String.valueOf(class_124.field_1060) + "Pickaxe ability used", new Object[0]);
/*     */     recordPickaxeMessageHandled(message);
/*     */     return true;
/*     */   }
/*     */   
/*     */   private boolean isPickaxeAbilityAvailableMessage(String message) {
/*     */     if (message == null || message.isBlank()) {
/*     */       return false;
/*     */     }
/*     */     if (PICKAXE_AVAILABLE_CHAT_PATTERN.matcher(message).find()) {
/*     */       return true;
/*     */     }
/*     */     Matcher matcher = PICKAXE_AVAILABLE_GENERIC_CHAT_PATTERN.matcher(message);
/*     */     if (!matcher.find()) {
/*     */       String ability = extractPickaxeAbilityNameFromLine(message);
/*     */       if (ability == null) {
/*     */         return false;
/*     */       }
/*     */       return message.toLowerCase(Locale.ROOT).contains("available");
/*     */     }
/*     */     return (normalizePickaxeAbilityName(matcher.group(1)) != null);
/*     */   }
/*     */   
/*     */   private void recordPickaxeMessageHandled(String message) {
/*     */     String key = normalizeChatText(message);
/*     */     if (key == null || key.isBlank()) {
/*     */       return;
/*     */     }
/*     */     long nowMs = getPickaxeAbilityTimeMs();
/*     */     this.recentPickaxeMessageOutputs.put(key.toLowerCase(Locale.ROOT), Long.valueOf(nowMs));
/*     */     pruneRecentPickaxeMessageOutputs(nowMs);
/*     */   }
/*     */   
/*     */   private boolean wasRecentlyHandledPickaxeMessage(String message) {
/*     */     String key = normalizeChatText(message);
/*     */     if (key == null || key.isBlank()) {
/*     */       return false;
/*     */     }
/*     */     long nowMs = getPickaxeAbilityTimeMs();
/*     */     pruneRecentPickaxeMessageOutputs(nowMs);
/*     */     Long last = this.recentPickaxeMessageOutputs.get(key.toLowerCase(Locale.ROOT));
/*     */     return (last != null && nowMs - last.longValue() < 1500L);
/*     */   }
/*     */   
/*     */   private void pruneRecentPickaxeMessageOutputs(long nowMs) {
/*     */     if (this.recentPickaxeMessageOutputs.isEmpty()) {
/*     */       return;
/*     */     }
/*     */     for (String key : new ArrayList<>(this.recentPickaxeMessageOutputs.keySet())) {
/*     */       Long last = this.recentPickaxeMessageOutputs.get(key);
/*     */       if (last == null || nowMs - last.longValue() > 5000L) {
/*     */         this.recentPickaxeMessageOutputs.remove(key);
/*     */       }
/*     */     } 
/*     */   }
/*     */   
/*     */   private boolean handlePickaxeCooldownChatMessage(ChatEvent event, String message) {
/*     */     if (event == null || message == null) {
/*     */       return false;
/*     */     }
/*     */     if (!((Boolean)this.pickaxeAbilityCooldownEnabled.getValue()).booleanValue()) {
/*     */       return false;
/*     */     }
/*     */     if (!handlePickaxeCooldownMessage(message)) {
/*     */       return false;
/*     */     }
/*     */     event.setCancelled(true);
/*     */     queueChatSuppression(message);
/*     */     return true;
/*     */   }
/*     */   
/*     */   private void handlePickaxeAbilityStatusMessage(ChatEvent event, String message) {
/*     */     if (event == null || message == null) {
/*     */       return;
/*     */     }
/*     */     if (!((Boolean)this.pickaxeAbilityCooldownEnabled.getValue()).booleanValue()) {
/*     */       return;
/*     */     }
/*     */     if (handlePickaxeAbilityUsedMessage(message)) {
/*     */       event.setCancelled(true);
/*     */       queueChatSuppression(message);
/*     */       return;
/*     */     }
/*     */     if (isPickaxeAbilityAvailableMessage(message)) {
/*     */       event.setCancelled(true);
/*     */       queueChatSuppression(message);
/*     */     }
/*     */   }
/*     */   
/*     */   private Double getShortestPickaxeCooldownSeconds() {
/*     */     if (this.pickaxeAbilityCooldowns.isEmpty()) {
/*     */       return null;
/*     */     }
/*     */     long nowMs = getPickaxeAbilityTimeMs();
/*     */     long minEnd = Long.MAX_VALUE;
/*     */     for (Long endTick : this.pickaxeAbilityCooldowns.values()) {
/*     */       if (endTick != null && endTick.longValue() < minEnd) {
/*     */         minEnd = endTick.longValue();
/*     */       }
/*     */     }
/*     */     if (minEnd == Long.MAX_VALUE) {
/*     */       return null;
/*     */     }
/*     */     double remaining = (minEnd - nowMs) / 1000.0D;
/*     */     if (remaining < 0.0D) {
/*     */       remaining = 0.0D;
/*     */     }
/*     */     return Double.valueOf(remaining);
/*     */   }
/*     */   
/*     */   private String formatCooldownSeconds(double seconds) {
/*     */     if (seconds < 0.0D) {
/*     */       seconds = 0.0D;
/*     */     }
/*     */     double rounded = Math.round(seconds * 10.0D) / 10.0D;
/*     */     if (Math.abs(rounded - Math.round(rounded)) < 1.0E-4D) {
/*     */       return String.format(Locale.ROOT, "%.0f", rounded);
/*     */     }
/*     */     return String.format(Locale.ROOT, "%.1f", rounded);
/*     */   }
/*     */   
/*     */   private String normalizePickaxeAbilityName(String name) {
/*     */     if (name == null) {
/*     */       return null;
/*     */     }
/*     */     String trimmed = name.trim();
/*     */     if (trimmed.isEmpty()) {
/*     */       return null;
/*     */     }
/*     */     String lower = trimmed.toLowerCase(Locale.ROOT);
/*     */     if (lower.equals("pickobulus")) {
/*     */       return "Pickobulus";
/*     */     }
/*     */     if (lower.equals("mining speed boost")) {
/*     */       return "Mining Speed Boost";
/*     */     }
/*     */     if (lower.equals("maniac miner")) {
/*     */       return "Maniac Miner";
/*     */     }
/*     */     if (lower.equals("tunnel vision")) {
/*     */       return "Tunnel Vision";
/*     */     }
/*     */     return null;
/*     */   }
/*     */   
/*     */   private int getPickaxeAbilityBaseSeconds(String name) {
/*     */     if (name == null) {
/*     */       return 0;
/*     */     }
/*     */     switch (name) {
/*     */       case "Pickobulus":
/*     */         return 50;
/*     */       case "Mining Speed Boost":
/*     */       case "Maniac Miner":
/*     */       case "Tunnel Vision":
/*     */         return 120;
/*     */     }
/*     */     return 0;
/*     */   }
/*     */   
/*     */   private double getPickaxeAbilityCooldownReductionPercent() {
/*     */     double reduction = 0.0D;
/*     */     reduction += getHeldItemPickaxeCooldownReduction();
/*     */     reduction += getLegendaryBalCooldownReduction();
/*     */     if (this.skyMallPickaxeCooldownActive) {
/*     */       reduction += 20.0D;
/*     */     }
/*     */     if (reduction < 0.0D) {
/*     */       reduction = 0.0D;
/*     */     }
/*     */     if (reduction > 100.0D) {
/*     */       reduction = 100.0D;
/*     */     }
/*     */     return reduction;
/*     */   }
/*     */   
/*     */   private double getHeldItemPickaxeCooldownReduction() {
/*     */     if (this.mc.field_1724 == null) {
/*     */       return 0.0D;
/*     */     }
/*     */     class_1799 stack = this.mc.field_1724.method_59958();
/*     */     if (stack == null || stack.method_7960()) {
/*     */       return 0.0D;
/*     */     }
/*     */     List<class_2561> tooltip = getItemTooltip(stack);
/*     */     if (tooltip.isEmpty()) {
/*     */       return 0.0D;
/*     */     }
/*     */     int best = 0;
/*     */     for (class_2561 lineText : tooltip) {
/*     */       if (lineText == null) {
/*     */         continue;
/*     */       }
/*     */       String line = class_124.method_539(lineText.getString());
/*     */       if (line == null || line.isBlank()) {
/*     */         continue;
/*     */       }
/*     */       Matcher matcher = PICKAXE_ABILITY_COOLDOWN_PATTERN.matcher(line);
/*     */       if (!matcher.find()) {
/*     */         continue;
/*     */       }
/*     */       try {
/*     */         int value = Integer.parseInt(matcher.group(1));
/*     */         if (value == 2 || value == 4 || value == 6 || value == 8 || value == 10) {
/*     */           best = Math.max(best, value);
/*     */         }
/*     */       } catch (NumberFormatException numberFormatException) {}
/*     */     }
/*     */     return best;
/*     */   }
/*     */   
/*     */   private double getLegendaryBalCooldownReduction() {
/*     */     int level = findLegendaryBalPetLevel();
/*     */     if (level <= 0) {
/*     */       return 0.0D;
/*     */     }
/*     */     return Math.max(0.0D, Math.min(100.0D, level * 0.1D));
/*     */   }
/*     */   
/*     */   private int findLegendaryBalPetLevel() {
/*     */     List<class_2561> tabTexts = readTabMenuTexts();
/*     */     if (tabTexts.isEmpty()) {
/*     */       return -1;
/*     */     }
/*     */     boolean hasPetHeader = hasTabPetHeader(tabTexts);
/*     */     for (class_2561 text : tabTexts) {
/*     */       if (text == null) {
/*     */         continue;
/*     */       }
/*     */       String plain = text.getString();
/*     */       if (plain == null || plain.isBlank()) {
/*     */         continue;
/*     */       }
/*     */       String lower = plain.toLowerCase(Locale.ROOT);
/*     */       if (!lower.contains("bal")) {
/*     */         continue;
/*     */       }
/*     */       Matcher matcher = BAL_PET_PATTERN.matcher(plain);
/*     */       if (!matcher.find()) {
/*     */         matcher = PET_LEVEL_PATTERN.matcher(plain);
/*     */         if (!matcher.find()) {
/*     */           continue;
/*     */         }
/*     */       }
/*     */       int level;
/*     */       try {
/*     */         level = Integer.parseInt(matcher.group(1));
/*     */       } catch (NumberFormatException numberFormatException) {
/*     */         continue;
/*     */       }
/*     */       if (level <= 0) {
/*     */         continue;
/*     */       }
/*     */       if (isLegendaryBalText(text, lower)) {
/*     */         return level;
/*     */       }
/*     */       if (hasPetHeader) {
/*     */         return level;
/*     */       }
/*     */     }
/*     */     return -1;
/*     */   }
/*     */   
/*     */   private boolean hasTabPetHeader(List<class_2561> tabTexts) {
/*     */     if (tabTexts == null || tabTexts.isEmpty()) {
/*     */       return false;
/*     */     }
/*     */     for (class_2561 text : tabTexts) {
/*     */       if (text == null) {
/*     */         continue;
/*     */       }
/*     */       String line = text.getString();
/*     */       if (line == null || line.isBlank()) {
/*     */         continue;
/*     */       }
/*     */       String lower = line.toLowerCase(Locale.ROOT);
/*     */       if (lower.contains("pet:")) {
/*     */         return true;
/*     */       }
/*     */     }
/*     */     return false;
/*     */   }
/*     */   
/*     */   private boolean isLegendaryBalText(class_2561 text, String lowerPlain) {
/*     */     if (text == null) {
/*     */       return false;
/*     */     }
/*     */     Integer gold = getGoldTextColorValue();
/*     */     if (gold == null) {
/*     */       return (lowerPlain != null && lowerPlain.contains("legendary") && lowerPlain.contains("bal"));
/*     */     }
/*     */     if (hasGoldBalFormatting(text)) {
/*     */       return true;
/*     */     }
/*     */     class_2583 baseStyle = text.method_10866();
/*     */     if (baseStyle != null && lowerPlain != null && lowerPlain.contains("bal")) {
/*     */       Integer baseColour = baseStyle.method_65301();
/*     */       if (baseColour != null && baseColour.equals(gold)) {
/*     */         return true;
/*     */       }
/*     */     }
/*     */     List<class_2561> parts = text.method_44746();
/*     */     if (parts == null || parts.isEmpty()) {
/*     */       parts = List.of(text);
/*     */     }
/*     */     for (class_2561 part : parts) {
/*     */       if (part == null) {
/*     */         continue;
/*     */       }
/*     */       String partText = part.getString();
/*     */       if (partText == null) {
/*     */         continue;
/*     */       }
/*     */       String lower = partText.toLowerCase(Locale.ROOT);
/*     */       if (!lower.contains("bal")) {
/*     */         continue;
/*     */       }
/*     */       class_2583 style = part.method_10866();
/*     */       if (style == null) {
/*     */         continue;
/*     */       }
/*     */       Integer colour = style.method_65301();
/*     */       if (colour != null && colour.equals(gold)) {
/*     */         return true;
/*     */       }
/*     */     }
/*     */     return (lowerPlain != null && lowerPlain.contains("legendary") && lowerPlain.contains("bal"));
/*     */   }
/*     */   
/*     */   private boolean hasGoldBalFormatting(class_2561 text) {
/*     */     if (text == null) {
/*     */       return false;
/*     */     }
/*     */     String formatted = text.method_10858(0);
/*     */     if (formatted == null || formatted.isBlank()) {
/*     */       return false;
/*     */     }
/*     */     String lower = formatted.toLowerCase(Locale.ROOT);
/*     */     int index = lower.indexOf("bal");
/*     */     if (index < 0) {
/*     */       return false;
/*     */     }
/*     */     if (lower.contains("§6bal") || lower.contains("§6 bal")) {
/*     */       return true;
/*     */     }
/*     */     if (lower.contains("§x§f§f§a§a§0§0") && lower.indexOf("§x§f§f§a§a§0§0") < index) {
/*     */       return true;
/*     */     }
/*     */     Integer colour = getFormattedColorBeforeIndex(formatted, index);
/*     */     Integer gold = getGoldTextColorValue();
/*     */     return (colour != null && gold != null && colour.equals(gold));
/*     */   }
/*     */   
/*     */   private Integer getFormattedColorBeforeIndex(String formatted, int index) {
/*     */     if (formatted == null || index <= 0) {
/*     */       return null;
/*     */     }
/*     */     Integer current = null;
/*     */     int i = 0;
/*     */     int limit = Math.min(index, formatted.length());
/*     */     while (i < limit - 1) {
/*     */       char ch = formatted.charAt(i);
/*     */       if (ch == '§') {
/*     */         char code = Character.toLowerCase(formatted.charAt(i + 1));
/*     */         if (code == 'x') {
/*     */           Integer hex = parseHexColor(formatted, i);
/*     */           if (hex != null) {
/*     */             current = hex;
/*     */             i += 14;
/*     */             continue;
/*     */           }
/*     */         }
/*     */         if (code == 'r') {
/*     */           current = null;
/*     */           i += 2;
/*     */           continue;
/*     */         }
/*     */         Integer legacy = getLegacyColourForCode(code);
/*     */         if (legacy != null) {
/*     */           current = legacy;
/*     */         }
/*     */         i += 2;
/*     */         continue;
/*     */       }
/*     */       i++;
/*     */     }
/*     */     return current;
/*     */   }
/*     */   
/*     */   private Integer parseHexColor(String formatted, int startIndex) {
/*     */     if (formatted == null || startIndex < 0 || startIndex + 13 >= formatted.length()) {
/*     */       return null;
/*     */     }
/*     */     if (formatted.charAt(startIndex) != '§' || Character.toLowerCase(formatted.charAt(startIndex + 1)) != 'x') {
/*     */       return null;
/*     */     }
/*     */     StringBuilder hex = new StringBuilder();
/*     */     int idx = startIndex + 2;
/*     */     for (int i = 0; i < 6; i++) {
/*     */       if (idx + 1 >= formatted.length()) {
/*     */         return null;
/*     */       }
/*     */       if (formatted.charAt(idx) != '§') {
/*     */         return null;
/*     */       }
/*     */       char digit = formatted.charAt(idx + 1);
/*     */       if (!isHexDigit(digit)) {
/*     */         return null;
/*     */       }
/*     */       hex.append(digit);
/*     */       idx += 2;
/*     */     }
/*     */     try {
/*     */       return Integer.valueOf(Integer.parseInt(hex.toString(), 16));
/*     */     } catch (NumberFormatException numberFormatException) {
/*     */       return null;
/*     */     }
/*     */   }
/*     */   
/*     */   private boolean isHexDigit(char digit) {
/*     */     char c = Character.toLowerCase(digit);
/*     */     return (c >= '0' && c <= '9') || (c >= 'a' && c <= 'f');
/*     */   }
/*     */   
/*     */   private Integer getLegacyColourForCode(char code) {
/*     */     try {
/*     */       class_124 colour = class_124.method_544(code);
/*     */       if (colour != null) {
/*     */         return colour.method_532();
/*     */       }
/*     */     } catch (Throwable throwable) {}
/*     */     return null;
/*     */   }
/*     */   
/*     */   private Integer getGoldTextColorValue() {
/*     */     try {
/*     */       class_124 gold = class_124.method_533("gold");
/*     */       if (gold != null) {
/*     */         Integer value = gold.method_532();
/*     */         if (value != null) {
/*     */           return value;
/*     */         }
/*     */       }
/*     */     } catch (Throwable throwable) {}
/*     */     return Integer.valueOf(16755200);
/*     */   }
/*     */   
/*     */   private List<class_2561> getItemTooltip(class_1799 stack) {
/*     */     if (stack == null || this.mc.field_1724 == null || this.mc.field_1687 == null) {
/*     */       return Collections.emptyList();
/*     */     }
/*     */     try {
/*     */       Class<?> tooltipContextClass = Class.forName("net.minecraft.class_1792$class_9635");
/*     */       Method contextMethod = tooltipContextClass.getDeclaredMethod("method_59528", new Class[] { class_1937.class });
/*     */       contextMethod.setAccessible(true);
/*     */       Object tooltipContext = contextMethod.invoke(null, new Object[] { this.mc.field_1687 });
/*     */       Class<?> tooltipTypeClass = Class.forName("net.minecraft.class_1836");
/*     */       Field tooltipTypeField = tooltipTypeClass.getDeclaredField("field_41070");
/*     */       tooltipTypeField.setAccessible(true);
/*     */       Object tooltipType = tooltipTypeField.get(null);
/*     */       Method tooltipMethod = stack.getClass().getMethod("method_7950", new Class[] { tooltipContextClass, class_1657.class, tooltipTypeClass });
/*     */       Object result = tooltipMethod.invoke(stack, new Object[] { tooltipContext, this.mc.field_1724, tooltipType });
/*     */       if (result instanceof List) {
/*     */         return (List<class_2561>)result;
/*     */       }
/*     */     } catch (ReflectiveOperationException reflectiveOperationException) {}
/*     */     return Collections.emptyList();
/*     */   }
/*     */   
/*     */   private List<class_2561> readTabMenuTexts() {
/*     */     List<class_2561> texts = new ArrayList<>();
/*     */     class_355 playerListHud = null;
/*     */     try {
/*     */       if (this.mc.field_1705 != null) {
/*     */         Object hudObject = invokeNoArg(this.mc.field_1705, new String[] { "method_1750", "getPlayerListHud" });
/*     */         if (hudObject instanceof class_355) {
/*     */           playerListHud = (class_355)hudObject;
/*     */         }
/*     */       } 
/*     */     } catch (ReflectiveOperationException reflectiveOperationException) {}
/*     */     appendTabHeaderFooterTexts(texts, playerListHud);
/*     */     appendDisplayedTabHudTexts(playerListHud, texts);
/*     */     class_634 networkHandler = this.mc.method_1562();
/*     */     if (networkHandler == null) {
/*     */       return texts;
/*     */     }
/*     */     appendTabEntryTexts(networkHandler.method_2880(), texts, playerListHud);
/*     */     return texts;
/*     */   }
/*     */   
/*     */   private void appendDisplayedTabHudTexts(class_355 playerListHud, List<class_2561> texts) {
/*     */     if (playerListHud == null || texts == null) {
/*     */       return;
/*     */     }
/*     */     try {
/*     */       Method method = playerListHud.getClass().getDeclaredMethod("method_48213", new Class[0]);
/*     */       method.setAccessible(true);
/*     */       Object value = method.invoke(playerListHud, new Object[0]);
/*     */       if (!(value instanceof Iterable)) {
/*     */         return;
/*     */       }
/*     */       appendTabEntryTexts((Iterable)value, texts, playerListHud);
/*     */     } catch (ReflectiveOperationException reflectiveOperationException) {}
/*     */   }
/*     */   
/*     */   private void appendTabHeaderFooterTexts(List<class_2561> texts, class_355 playerListHud) {
/*     */     if (texts == null || playerListHud == null) {
/*     */       return;
/*     */     }
/*     */     appendTextFromPlayerListField(playerListHud, "field_2154", texts);
/*     */     appendTextFromPlayerListField(playerListHud, "field_2153", texts);
/*     */   }
/*     */   
/*     */   private void appendTextFromPlayerListField(Object playerListHud, String fieldName, List<class_2561> texts) {
/*     */     if (playerListHud == null || fieldName == null || texts == null) {
/*     */       return;
/*     */     }
/*     */     try {
/*     */       Field field = playerListHud.getClass().getDeclaredField(fieldName);
/*     */       field.setAccessible(true);
/*     */       Object value = field.get(playerListHud);
/*     */       if (value instanceof class_2561) {
/*     */         texts.add((class_2561)value);
/*     */       }
/*     */     } catch (ReflectiveOperationException reflectiveOperationException) {}
/*     */   }
/*     */   
/*     */   private void appendTabEntryTexts(Iterable<?> entries, List<class_2561> texts, class_355 playerListHud) {
/*     */     if (entries == null || texts == null) {
/*     */       return;
/*     */     }
/*     */     for (Object obj : entries) {
/*     */       if (!(obj instanceof class_640)) {
/*     */         continue;
/*     */       }
/*     */       class_2561 text = getTabTextFromEntry((class_640)obj, playerListHud);
/*     */       if (text != null) {
/*     */         texts.add(text);
/*     */       }
/*     */     } 
/*     */   }
/*     */   
/*     */   private class_2561 getTabTextFromEntry(class_640 entry, class_355 playerListHud) {
/*     */     if (entry == null) {
/*     */       return null;
/*     */     }
/*     */     class_2561 rendered = null;
/*     */     try {
/*     */       if (playerListHud != null) {
/*     */         rendered = playerListHud.method_1918(entry);
/*     */       }
/*     */     } catch (Throwable throwable) {}
/*     */     if (rendered != null) {
/*     */       return rendered;
/*     */     }
/*     */     class_2561 displayName = entry.method_2971();
/*     */     if (displayName != null) {
/*     */       return displayName;
/*     */     }
/*     */     return null;
/*     */   }
/*     */   
/*     */   private void rebuildEspRenderTasksIfNeeded() {
/*     */     if (this.espScanInProgress || !this.espRenderTasksDirty) {
/*     */       return;
/*     */     }
/*     */     this.espRenderTasksDirty = false;
/*     */     if (((Boolean)this.titaniumHighlightEnabled.getValue()).booleanValue() && !this.titaniumBlocks.isEmpty()) {
/*     */       rebuildRenderTasks(this.titaniumBlocks, this.titaniumRenderTasks, TITANIUM_FILL_COLOUR, TITANIUM_OUTLINE_COLOUR);
/*     */     }
/*     */     if (((Boolean)this.nodeHighlightEnabled.getValue()).booleanValue() && !this.nodeBlocks.isEmpty()) {
/*     */       rebuildRenderTasks(this.nodeBlocks, this.nodeRenderTasks, NODE_FILL_COLOUR, NODE_OUTLINE_COLOUR);
/*     */     }
/*     */     if (((Boolean)this.chestHighlightEnabled.getValue()).booleanValue() && !this.chestBlocks.isEmpty()) {
/*     */       rebuildRenderTasks(this.chestBlocks, this.chestRenderTasks, CHEST_FILL_COLOUR, CHEST_OUTLINE_COLOUR);
/*     */     }
/*     */     if (((Boolean)this.automatonHighlightEnabled.getValue()).booleanValue() && !this.automatonBoxes.isEmpty()) {
/*     */       rebuildEntityRenderTasks(this.automatonBoxes, this.automatonRenderTasks, AUTOMATON_FILL_COLOUR, AUTOMATON_OUTLINE_COLOUR);
/*     */     }
/*     */     if (((Boolean)this.hideonleafHighlightEnabled.getValue()).booleanValue() && !this.hideonleafEntityBoxes.isEmpty()) {
/*     */       rebuildEntityRenderTasks(this.hideonleafEntityBoxes, this.hideonleafEntityRenderTasks, HIDEONLEAF_FILL_COLOUR, HIDEONLEAF_OUTLINE_COLOUR);
/*     */     }
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
/* 602 */     if (!((Boolean)this.espEnabled.getValue()).booleanValue() || !((Boolean)this.espTracerEnabled.getValue()).booleanValue()) {
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
/* 620 */     this.tracerTargets.clear();
/* 621 */     this.tracerTargetColours.clear();
/* 621 */     List<class_243> targets = this.tracerTargets;
/* 621 */     List<Colour> targetColours = this.tracerTargetColours;
/* 622 */     collectTracerTargets(this.titaniumBlocks, (Boolean)this.titaniumHighlightEnabled.getValue(), TITANIUM_OUTLINE_COLOUR, targets, targetColours);
/* 623 */     collectTracerTargets(this.nodeBlocks, (Boolean)this.nodeHighlightEnabled.getValue(), NODE_OUTLINE_COLOUR, targets, targetColours);
/* 624 */     collectTracerTargets(this.chestBlocks, (Boolean)this.chestHighlightEnabled.getValue(), CHEST_OUTLINE_COLOUR, targets, targetColours);
/*     */     collectEntityTracerTargets(this.automatonBoxes, (Boolean)this.automatonHighlightEnabled.getValue(), AUTOMATON_OUTLINE_COLOUR, targets, targetColours);
/*     */     collectEntityTracerTargets(this.hideonleafEntityBoxes, (Boolean)this.hideonleafHighlightEnabled.getValue(), HIDEONLEAF_OUTLINE_COLOUR, targets, targetColours);
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
/*     */   private void collectTracerTargets(List<class_2338> blocks, Boolean enabled, Colour tracerColour, List<class_243> targets, List<Colour> targetColours) {
/* 634 */     if (enabled == null || !enabled.booleanValue() || blocks.isEmpty()) {
/*     */       return;
/*     */     }
/* 637 */     for (class_2338 pos : blocks) {
/* 638 */       targets.add(new class_243(pos.method_10263() + 0.5D, pos.method_10264() + 0.5D, pos.method_10260() + 0.5D));
/* 639 */       targetColours.add(tracerColour);
/*     */     }
/*     */   }
/*     */   
/*     */   private void collectEntityTracerTargets(List<class_238> boxes, Boolean enabled, Colour tracerColour, List<class_243> targets, List<Colour> targetColours) {
/*     */     if (enabled == null || !enabled.booleanValue() || boxes.isEmpty()) {
/*     */       return;
/*     */     }
/*     */     for (class_238 box : boxes) {
/*     */       targets.add(getBoxCenter(box));
/*     */       targetColours.add(tracerColour);
/*     */     }
/*     */   }
/*     */   
/*     */   private void renderClosestTracer(List<class_243> targets, List<Colour> colours, class_243 tracerStart, double thicknessPx) {
/* 644 */     int closestIndex = -1;
/* 645 */     double closestDistanceSq = Double.MAX_VALUE;
/* 646 */     double sx = tracerStart.method_10216();
/* 647 */     double sy = tracerStart.method_10214();
/* 648 */     double sz = tracerStart.method_10215();
/* 649 */     for (int i = 0; i < targets.size(); i++) {
/* 650 */       class_243 target = targets.get(i);
/* 651 */       double tx = target.method_10216();
/* 652 */       double ty = target.method_10214();
/* 653 */       double tz = target.method_10215();
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
/* 666 */     class_243 target = targets.get(closestIndex);
/* 667 */     renderThickMergedTracerLine(tracerStart, target, colours.get(closestIndex), thicknessPx);
/*     */   }
/*     */   
/*     */   private void renderTracerTargets(List<class_243> targets, List<Colour> colours, class_243 tracerStart, double thicknessPx) {
/* 672 */     for (int i = 0; i < targets.size(); i++) {
/* 673 */       class_243 target = targets.get(i);
/* 674 */       renderThickMergedTracerLine(tracerStart, target, colours.get(i), thicknessPx);
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
/* 689 */     double thicknessPx = getTracerThicknessPixels();
/* 690 */     if (thicknessPx <= 0.0D) {
/*     */       return;
/*     */     }
/* 693 */     if (((Boolean)this.tracerClosestOnly.getValue()).booleanValue()) {
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
/*     */   private boolean isAutomatonEntity(net.minecraft.class_1297 entity) {
/*     */     if (entity == null) {
/*     */       return false;
/*     */     }
/*     */     String typeText = getEntityTypeText(entity);
/*     */     if (typeText != null && (typeText.contains("iron_golem") || typeText.contains("iron golem") || typeText.contains("irongolem"))) {
/*     */       return true;
/*     */     }
/*     */     String raw = String.valueOf(entity).toLowerCase(Locale.ROOT);
/*     */     return (raw.contains("iron_golem") || raw.contains("iron golem") || raw.contains("irongolem"));
/*     */   }
/*     */   
/*     */   private boolean isGreenShulkerEntity(net.minecraft.class_1297 entity) {
/*     */     if (entity == null) {
/*     */       return false;
/*     */     }
/*     */     String typeText = getEntityTypeText(entity);
/*     */     String raw = String.valueOf(entity).toLowerCase(Locale.ROOT);
/*     */     boolean isShulker = (typeText != null && typeText.contains("shulker")) || raw.contains("shulker");
/*     */     if (!isShulker) {
/*     */       return false;
/*     */     }
/*     */     if (typeText != null && (typeText.contains("green") || typeText.contains("lime"))) {
/*     */       return true;
/*     */     }
/*     */     if (raw.contains("green") || raw.contains("lime")) {
/*     */       return true;
/*     */     }
/*     */     String colour = getShulkerColorText(entity);
/*     */     if (colour != null && (colour.contains("green") || colour.contains("lime"))) {
/*     */       return true;
/*     */     }
/*     */     String nametag = getEntityNametag(entity);
/*     */     return (nametag != null && (nametag.contains("green") || nametag.contains("lime")));
/*     */   }
/*     */   
/*     */   private String getEntityTypeText(net.minecraft.class_1297 entity) {
/*     */     if (entity == null) return null; 
/*     */     if (this.entityTypeMethod == null) {
/*     */       this.entityTypeMethod = resolveEntityTypeMethod(entity);
/*     */     }
/*     */     if (this.entityTypeMethod == null) {
/*     */       return null;
/*     */     }
/*     */     try {
/*     */       if (!this.entityTypeMethod.getDeclaringClass().isInstance(entity)) {
/*     */         this.entityTypeMethod = resolveEntityTypeMethod(entity);
/*     */         if (this.entityTypeMethod == null) {
/*     */           return null;
/*     */         }
/*     */       }
/*     */       Object type = this.entityTypeMethod.invoke(entity, new Object[0]);
/*     */       return (type == null) ? null : String.valueOf(type).toLowerCase(Locale.ROOT);
/*     */     } catch (ReflectiveOperationException ignored) {
/*     */       this.entityTypeMethod = null;
/*     */       return null;
/*     */     } 
/*     */   }
/*     */   
/*     */   private Method resolveEntityTypeMethod(Object entity) {
/*     */     String[] candidates = { "getType", "method_5864" };
/*     */     for (String name : candidates) {
/*     */       try {
/*     */         return entity.getClass().getMethod(name, new Class[0]);
/*     */       } catch (ReflectiveOperationException reflectiveOperationException) {}
/*     */     } 
/*     */     return null;
/*     */   }
/*     */   
/*     */   private String getShulkerColorText(net.minecraft.class_1297 entity) {
/*     */     if (entity == null) {
/*     */       return null;
/*     */     }
/*     */     if (this.shulkerColorMethod == null || !this.shulkerColorMethod.getDeclaringClass().isInstance(entity)) {
/*     */       this.shulkerColorMethod = resolveShulkerColorMethod(entity);
/*     */     }
/*     */     if (this.shulkerColorMethod == null) {
/*     */       return null;
/*     */     }
/*     */     try {
/*     */       Object colour = this.shulkerColorMethod.invoke(entity, new Object[0]);
/*     */       return (colour == null) ? null : String.valueOf(colour).toLowerCase(Locale.ROOT);
/*     */     } catch (ReflectiveOperationException ignored) {
/*     */       this.shulkerColorMethod = null;
/*     */       return null;
/*     */     } 
/*     */   }
/*     */   
/*     */   private Method resolveShulkerColorMethod(Object entity) {
/*     */     if (entity == null) {
/*     */       return null;
/*     */     }
/*     */     for (Method method : entity.getClass().getMethods()) {
/*     */       Method candidate = resolveDyeEnumMethod(method);
/*     */       if (candidate != null) {
/*     */         return candidate;
/*     */       }
/*     */     } 
/*     */     for (Method method : entity.getClass().getDeclaredMethods()) {
/*     */       Method candidate = resolveDyeEnumMethod(method);
/*     */       if (candidate != null) {
/*     */         return candidate;
/*     */       }
/*     */     } 
/*     */     return null;
/*     */   }
/*     */   
/*     */   private Method resolveDyeEnumMethod(Method method) {
/*     */     if (method == null || method.getParameterCount() != 0) {
/*     */       return null;
/*     */     }
/*     */     Class<?> returnType = method.getReturnType();
/*     */     if (returnType == null || !returnType.isEnum()) {
/*     */       return null;
/*     */     }
/*     */     Object[] constants = returnType.getEnumConstants();
/*     */     if (constants == null || constants.length == 0) {
/*     */       return null;
/*     */     }
/*     */     boolean hasGreen = false;
/*     */     boolean hasLime = false;
/*     */     for (Object constant : constants) {
/*     */       String name = String.valueOf(constant).toLowerCase(Locale.ROOT);
/*     */       if (name.contains("green")) hasGreen = true; 
/*     */       if (name.contains("lime")) hasLime = true; 
/*     */       if (hasGreen && hasLime) {
/*     */         try {
/*     */           method.setAccessible(true);
/*     */         } catch (Exception exception) {}
/*     */         return method;
/*     */       }
/*     */     } 
/*     */     return null;
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
/*     */       if (!this.entityBoundingBoxMethod.getDeclaringClass().isInstance(entity)) {
/*     */         this.entityBoundingBoxMethod = resolveEntityBoundingBoxMethod(entity);
/*     */         if (this.entityBoundingBoxMethod == null) {
/*     */           return null;
/*     */         }
/*     */       }
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
/* 553 */       return;  if (event == null || event.getMessage() == null)
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
/* 591 */     postToConfiguredWebhook((String)this.loginNotifierWebhook.getValue(), username + " logged into " + this.lastKnownServerAddress);
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
/* 603 */     postToConfiguredWebhook((String)this.loginNotifierWebhook.getValue(), username + " left " + this.lastKnownServerAddress);
/* 604 */     this.lastLoginNotifierEvent = "leave";
/*     */   }
/*     */   
/*     */   private void updateTempleSkipSpot() {
/* 604 */     if (!isTempleSkipEnabled()) {
/* 604 */       clearTempleSkipData();
/* 604 */       return;
/*     */     }
/* 604 */     if (this.templeSkipSpot != null) {
/* 604 */       return;
/*     */     }
/* 604 */     this.templeSkipTickCounter++;
/* 604 */     if (this.templeSkipTickCounter % TEMPLE_SKIP_SCAN_INTERVAL_TICKS != 0) {
/* 604 */       return;
/*     */     }
/* 604 */     Iterable<?> entities = getEntityIterable(this.mc.field_1687);
/* 604 */     if (entities == null) {
/* 604 */       return;
/*     */     }
/* 604 */     for (Object obj : entities) {
/* 604 */       if (!(obj instanceof net.minecraft.class_1297)) {
/* 604 */         continue;
/*     */       }
/* 604 */       net.minecraft.class_1297 entity = (net.minecraft.class_1297)obj;
/* 604 */       String name = getEntityNametag(entity);
/* 604 */       if (!TEMPLE_SKIP_GUARDIAN_NAME.equals(name)) {
/* 604 */         continue;
/*     */       }
/* 604 */       class_2338 basePos = entity.method_24515();
/* 604 */       class_2338 ground = findGround(basePos, 4);
/* 604 */       if (ground == null) {
/* 604 */         continue;
/*     */       }
/* 604 */       class_2680 state = this.mc.field_1687.method_8320(ground);
/* 604 */       if (state == null || !state.method_26204().equals(class_2246.field_10056)) {
/* 604 */         continue;
/*     */       }
/* 604 */       this.templeSkipSpot = ground.method_10069(20, -45, -35);
/* 604 */       return;
/*     */     }
/*     */   }
/*     */   
/*     */   private void clearTempleSkipData() {
/* 604 */     this.templeSkipSpot = null;
/* 604 */     this.templeSkipTickCounter = 0;
/*     */   }
/*     */   
/*     */   private boolean isTempleSkipEnabled() {
/* 604 */     return (isEnabled() && ((Boolean)this.templeSkipEnabled.getValue()).booleanValue());
/*     */   }
/*     */   
/*     */   private Colour getTempleSkipColour() {
/* 604 */     if (this.templeSkipColor == null) {
/* 604 */       return TEMPLE_SKIP_DEFAULT_COLOUR;
/*     */     }
/* 604 */     Object value = this.templeSkipColor.getValue();
/* 604 */     return (value instanceof Colour) ? (Colour)value : TEMPLE_SKIP_DEFAULT_COLOUR;
/*     */   }
/*     */   
/*     */   private boolean isBlazePuzzleProtectionEnabled() {
/*     */     return (isEnabled() && ((Boolean)this.dungeonPuzzlesEnabled.getValue()).booleanValue() && ((Boolean)this.blockWrongBlazeEnabled.getValue()).booleanValue() && this.mc != null && this.mc.field_1687 != null && this.mc.field_1724 != null);
/*     */   }
/*     */   
/*     */   private void updateBlazePuzzleTargets() {
/*     */     this.blazePuzzleTargets.clear();
/*     */     if (!isBlazePuzzleProtectionEnabled()) {
/*     */       return;
/*     */     }
/*     */     Boolean lowerMode = getBlazePuzzleMode();
/*     */     if (lowerMode == null) {
/*     */       return;
/*     */     }
/*     */     Iterable<?> entities = getEntityIterable(this.mc.field_1687);
/*     */     if (entities == null) {
/*     */       return;
/*     */     }
/*     */     List<net.minecraft.class_1297> allEntities = new ArrayList<>();
/*     */     List<BlazePuzzleTarget> foundTargets = new ArrayList<>();
/*     */     for (Object obj : entities) {
/*     */       if (!(obj instanceof net.minecraft.class_1297)) {
/*     */         continue;
/*     */       }
/*     */       net.minecraft.class_1297 entity = (net.minecraft.class_1297)obj;
/*     */       if (entity == this.mc.field_1724) {
/*     */         continue;
/*     */       }
/*     */       allEntities.add(entity);
/*     */     }
/*     */     for (net.minecraft.class_1297 entity : allEntities) {
/*     */       String nametag = getEntityNametag(entity);
/*     */       int health = extractBlazePuzzleHealth(nametag);
/*     */       if (health <= 0) {
/*     */         continue;
/*     */       }
/*     */       net.minecraft.class_1297 hitEntity = isBlazeEntity(entity) ? entity : findNearestLinkedBlazeEntity(allEntities, entity);
/*     */       if (hitEntity == null) {
/*     */         hitEntity = entity;
/*     */       }
/*     */       if (getEntityBox(hitEntity) == null) {
/*     */         continue;
/*     */       }
/*     */       boolean duplicate = false;
/*     */       for (BlazePuzzleTarget target : foundTargets) {
/*     */         if (target.hitEntity() == hitEntity) {
/*     */           duplicate = true;
/*     */           break;
/*     */         }
/*     */       }
/*     */       if (!duplicate) {
/*     */         foundTargets.add(new BlazePuzzleTarget(hitEntity, health));
/*     */       }
/*     */     }
/*     */     foundTargets.sort((first, second) -> lowerMode.booleanValue() ? Integer.compare(second.health(), first.health()) : Integer.compare(first.health(), second.health()));
/*     */     this.blazePuzzleTargets.addAll(foundTargets);
/*     */   }
/*     */   
/*     */   private void clearBlazePuzzleData() {
/*     */     this.blazePuzzleTargets.clear();
/*     */     this.lastBlockedBlazeMessageMs = 0L;
/*     */   }
/*     */   
/*     */   private Boolean getBlazePuzzleMode() {
/*     */     for (String line : getScoreboardLines()) {
/*     */       if (line == null || line.isBlank()) {
/*     */         continue;
/*     */       }
/*     */       String normalized = line.toLowerCase(Locale.ROOT);
/*     */       if (normalized.contains("lower blaze")) {
/*     */         return Boolean.TRUE;
/*     */       }
/*     */       if (normalized.contains("higher blaze")) {
/*     */         return Boolean.FALSE;
/*     */       }
/*     */     }
/*     */     String area = getSkyblockArea();
/*     */     if (area != null) {
/*     */       String normalizedArea = area.toLowerCase(Locale.ROOT);
/*     */       if (normalizedArea.contains("lower blaze")) {
/*     */         return Boolean.TRUE;
/*     */       }
/*     */       if (normalizedArea.contains("higher blaze")) {
/*     */         return Boolean.FALSE;
/*     */       }
/*     */     }
/*     */     return null;
/*     */   }
/*     */   
/*     */   private int extractBlazePuzzleHealth(String nametag) {
/*     */     if (nametag == null || nametag.isBlank()) {
/*     */       return -1;
/*     */     }
/*     */     String normalized = class_124.method_539(nametag).replace('\u2668', ' ').replace('\u2764', ' ').replace('\u2763', ' ').replaceAll("\\s+", " ").trim();
/*     */     Matcher matcher = BLAZE_PUZZLE_NAMETAG_PATTERN.matcher(normalized);
/*     */     if (!matcher.find()) {
/*     */       Matcher fallbackMatcher = Pattern.compile("blaze\\s+[\\d,]+/([\\d,]+)", Pattern.CASE_INSENSITIVE).matcher(normalized);
/*     */       if (!fallbackMatcher.find()) {
/*     */         return -1;
/*     */       }
/*     */       matcher = fallbackMatcher;
/*     */     }
/*     */     try {
/*     */       return Integer.parseInt(matcher.group(1).replace(",", ""));
/*     */     } catch (RuntimeException runtimeException) {
/*     */       return -1;
/*     */     }
/*     */   }
/*     */   
/*     */   private boolean isBlazeEntity(net.minecraft.class_1297 entity) {
/*     */     if (entity == null) {
/*     */       return false;
/*     */     }
/*     */     String typeText = getEntityTypeText(entity);
/*     */     if (typeText != null && typeText.contains("blaze")) {
/*     */       return true;
/*     */     }
/*     */     return String.valueOf(entity).toLowerCase(Locale.ROOT).contains("blaze");
/*     */   }
/*     */   
/*     */   private net.minecraft.class_1297 findNearestLinkedBlazeEntity(List<net.minecraft.class_1297> entities, net.minecraft.class_1297 source) {
/*     */     if (entities == null || entities.isEmpty() || source == null) {
/*     */       return null;
/*     */     }
/*     */     class_243 sourcePos = source.method_33571();
/*     */     net.minecraft.class_1297 nearest = null;
/*     */     double bestDistanceSq = BLAZE_SUPPORT_SEARCH_RANGE_SQ;
/*     */     for (net.minecraft.class_1297 candidate : entities) {
/*     */       if (candidate == null || candidate == source || !isBlazeEntity(candidate)) {
/*     */         continue;
/*     */       }
/*     */       class_243 candidatePos = candidate.method_33571();
/*     */       double dx = candidatePos.field_1352 - sourcePos.field_1352;
/*     */       double dy = candidatePos.field_1351 - sourcePos.field_1351;
/*     */       double dz = candidatePos.field_1350 - sourcePos.field_1350;
/*     */       double distanceSq = dx * dx + dy * dy + dz * dz;
/*     */       if (distanceSq <= bestDistanceSq) {
/*     */         bestDistanceSq = distanceSq;
/*     */         nearest = candidate;
/*     */       }
/*     */     }
/*     */     return nearest;
/*     */   }
/*     */   
/*     */   private boolean shouldBlockWrongBlazeClick() {
/*     */     if (!isBlazePuzzleProtectionEnabled() || this.mc.field_1724 == null) {
/*     */       return false;
/*     */     }
/*     */     updateBlazePuzzleTargets();
/*     */     if (this.blazePuzzleTargets.size() < 2) {
/*     */       return false;
/*     */     }
/*     */     class_243 eyePos = this.mc.field_1724.method_5836(1.0F);
/*     */     class_243 look = this.mc.field_1724.method_5828(1.0F);
/*     */     if (eyePos == null || look == null) {
/*     */       return false;
/*     */     }
/*     */     class_243 endPos = eyePos.method_1019(look.method_1021(BLAZE_TARGET_LOCK_RANGE));
/*     */     double blockDistanceSq = getBlockingHitDistanceSq(eyePos);
/*     */     BlazePuzzleTarget correctTarget = this.blazePuzzleTargets.get(0);
/*     */     BlazePuzzleTarget aimedTarget = findInterceptedBlazeTarget(eyePos, endPos, blockDistanceSq);
/*     */     if (aimedTarget == null || aimedTarget.hitEntity() == correctTarget.hitEntity()) {
/*     */       return false;
/*     */     }
/*     */     long nowMs = System.currentTimeMillis();
/*     */     if (nowMs - this.lastBlockedBlazeMessageMs >= BLAZE_BLOCK_MESSAGE_COOLDOWN_MS) {
/*     */       this.lastBlockedBlazeMessageMs = nowMs;
/*     */       ChatUtils.chat("Admin ref", new Object[0]);
/*     */     }
/*     */     return true;
/*     */   }
/*     */   
/*     */   private BlazePuzzleTarget findInterceptedBlazeTarget(class_243 eyePos, class_243 endPos, double blockDistanceSq) {
/*     */     BlazePuzzleTarget bestTarget = null;
/*     */     double bestDistanceSq = Double.MAX_VALUE;
/*     */     for (BlazePuzzleTarget target : this.blazePuzzleTargets) {
/*     */       if (target == null || target.hitEntity() == null) {
/*     */         continue;
/*     */       }
/*     */       class_238 box = getEntityBox(target.hitEntity());
/*     */       if (box == null) {
/*     */         continue;
/*     */       }
/*     */       box = box.method_1011(BLAZE_BOX_PADDING);
/*     */       java.util.Optional<class_243> intercept = box.method_992(eyePos, endPos);
/*     */       if (intercept.isEmpty()) {
/*     */         continue;
/*     */       }
/*     */       class_243 interceptPos = intercept.get();
/*     */       double dx = interceptPos.field_1352 - eyePos.field_1352;
/*     */       double dy = interceptPos.field_1351 - eyePos.field_1351;
/*     */       double dz = interceptPos.field_1350 - eyePos.field_1350;
/*     */       double distanceSq = dx * dx + dy * dy + dz * dz;
/*     */       if (blockDistanceSq >= 0.0D && distanceSq > blockDistanceSq) {
/*     */         continue;
/*     */       }
/*     */       if (distanceSq < bestDistanceSq) {
/*     */         bestDistanceSq = distanceSq;
/*     */         bestTarget = target;
/*     */       }
/*     */     }
/*     */     return bestTarget;
/*     */   }
/*     */   
/*     */   private double getBlockingHitDistanceSq(class_243 eyePos) {
/*     */     if (this.mc.field_1724 == null) {
/*     */       return -1.0D;
/*     */     }
/*     */     class_239 hit = this.mc.field_1724.method_5745(BLAZE_TARGET_LOCK_RANGE, 1.0F, false);
/*     */     if (!(hit instanceof class_3965)) {
/*     */       return -1.0D;
/*     */     }
/*     */     class_243 hitPos = hit.method_17784();
/*     */     if (hitPos == null) {
/*     */       return -1.0D;
/*     */     }
/*     */     double dx = hitPos.field_1352 - eyePos.field_1352;
/*     */     double dy = hitPos.field_1351 - eyePos.field_1351;
/*     */     double dz = hitPos.field_1350 - eyePos.field_1350;
/*     */     return dx * dx + dy * dy + dz * dz;
/*     */   }
/*     */   
/*     */   private void renderTempleSkip(Render3DEvent.Last event) {
/* 604 */     if (!isTempleSkipEnabled() || this.templeSkipSpot == null) {
/* 604 */       return;
/*     */     }
/* 604 */     if (!this.titaniumRenderBridgeReady) {
/* 604 */       this.titaniumRenderBridgeReady = initTitaniumRenderBridge();
/* 604 */       if (!this.titaniumRenderBridgeReady) {
/* 604 */         return;
/*     */       }
/*     */     }
/* 604 */     if (this.outlineBoxConstructor == null || this.addRenderTaskMethod == null) {
/* 604 */       return;
/*     */     }
/* 604 */     Colour colour = getTempleSkipColour();
/*     */     try {
/* 604 */       class_238 baseBox = new class_238(this.templeSkipSpot);
/* 604 */       Object baseTask = this.outlineBoxConstructor.newInstance(new Object[] { baseBox, colour, Boolean.valueOf(false) });
/* 604 */       this.addRenderTaskMethod.invoke(null, new Object[] { baseTask });
/* 604 */       class_2338 standPos = this.templeSkipSpot.method_10087(8);
/* 604 */       class_238 standBox = new class_238(standPos);
/* 604 */       Object standTask = this.outlineBoxConstructor.newInstance(new Object[] { standBox, colour, Boolean.valueOf(false) });
/* 604 */       this.addRenderTaskMethod.invoke(null, new Object[] { standTask });
/* 604 */     } catch (ReflectiveOperationException ignored) {
/* 604 */       this.titaniumRenderBridgeReady = false;
/*     */     }
/*     */   }
/*     */   
/*     */   private boolean isInTempleArea() {
/* 604 */     List<String> lines = getScoreboardLines();
/* 604 */     for (String line : lines) {
/* 604 */       if (line != null && line.contains(JUNGLE_TEMPLE_AREA)) {
/* 604 */         return true;
/*     */       }
/*     */     } 
/* 604 */     return false;
/*     */   }
/*     */   
/*     */   private String getSkyblockArea() {
/* 604 */     List<String> lines = getScoreboardLines();
/* 604 */     for (String line : lines) {
/* 604 */       if (line == null || line.isBlank()) continue; 
/* 604 */       if (line.startsWith("Area:") || line.startsWith("Dungeon:")) {
/* 604 */         String[] split = line.split(":", 2);
/* 604 */         if (split.length > 1) {
/* 604 */           return split[1].trim();
/*     */         }
/*     */       } 
/*     */     } 
/* 604 */     return "";
/*     */   }
/*     */   
/*     */   private List<String> getScoreboardLines() {
/* 604 */     if (this.mc == null || this.mc.field_1724 == null) {
/* 604 */       return List.of();
/*     */     }
/* 604 */     class_634 handler = this.mc.field_1724.field_3944;
/* 604 */     if (handler == null) {
/* 604 */       return List.of();
/*     */     }
/* 604 */     class_269 scoreboard = handler.method_55823();
/* 604 */     if (scoreboard == null) {
/* 604 */       return List.of();
/*     */     }
/* 604 */     class_8646 displaySlot = (class_8646)class_8646.field_45176.apply(1);
/* 604 */     class_266 objective = scoreboard.method_1189(displaySlot);
/* 604 */     if (objective == null) {
/* 604 */       return List.of();
/*     */     }
/* 604 */     List<String> lines = new ArrayList<>();
/* 604 */     for (class_9015 holder : scoreboard.method_1178()) {
/* 604 */       if (!scoreboard.method_1166(holder).containsKey(objective)) {
/* 604 */         continue;
/*     */       }
/* 604 */       class_268 team = scoreboard.method_1164(holder.method_5820());
/* 604 */       if (team == null) {
/* 604 */         continue;
/*     */       }
/* 604 */       String line = class_124.method_539(team.method_1144().getString() + team.method_1136().getString()).trim();
/* 604 */       if (!line.isEmpty()) {
/* 604 */         lines.add(line);
/*     */       }
/*     */     } 
/* 604 */     return lines;
/*     */   }
/*     */   
/*     */   private class_2338 findGround(class_2338 start, int range) {
/* 604 */     if (start == null || this.mc.field_1687 == null) {
/* 604 */       return start;
/*     */     }
/* 604 */     int max = Math.max(0, Math.min(256, range));
/* 604 */     for (int i = 0; i <= max; i++) {
/* 604 */       class_2338 pos = start.method_10087(i);
/* 604 */       class_2680 state = this.mc.field_1687.method_8320(pos);
/* 604 */       if (state != null && !state.method_26215()) {
/* 604 */         return pos;
/*     */       }
/*     */     } 
/* 604 */     return start;
/*     */   }
/*     */   
/*     */   private int getEspRangeBlocks() {
/*     */     try {
/*     */       Object value = this.espRangeChunks.getValue();
/*     */       double chunks = 2.0D;
/*     */       if (value instanceof java.math.BigDecimal) {
/*     */         chunks = ((java.math.BigDecimal)value).doubleValue();
/*     */       } else if (value instanceof Number) {
/*     */         chunks = ((Number)value).doubleValue();
/*     */       }
/*     */       chunks = Math.max(1.0D, Math.min(8.0D, chunks));
/*     */       return (int)Math.round(chunks * 16.0D);
/*     */     } catch (Exception exception) {}
/*     */     return 32;
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
/*     */     int rangeBlocks = getEspRangeBlocks();
/*     */     
/* 621 */     boolean titaniumOn = ((Boolean)this.titaniumHighlightEnabled.getValue()).booleanValue();
/* 622 */     boolean nodeOn = ((Boolean)this.nodeHighlightEnabled.getValue()).booleanValue();
/* 622 */     boolean chestOn = ((Boolean)this.chestHighlightEnabled.getValue()).booleanValue();
/* 622 */     boolean hideonleafOn = ((Boolean)this.hideonleafHighlightEnabled.getValue()).booleanValue();
/* 622 */     boolean automatonOn = ((Boolean)this.automatonHighlightEnabled.getValue()).booleanValue();
/* 623 */     boolean blockScanOn = (titaniumOn || nodeOn || chestOn);
/* 623 */     if (!blockScanOn && !automatonOn && !hideonleafOn) {
/* 624 */       this.espScanInitialized = true;
/* 624 */       this.espScanInProgress = false;
/*     */       return;
/*     */     }
/* 626 */     if (this.espScanInProgress && shouldRestartEspScan(px, py, pz, rangeBlocks, titaniumOn, nodeOn, chestOn, automatonOn, hideonleafOn)) {
/* 627 */       startEspScan(px, py, pz, rangeBlocks, titaniumOn, nodeOn, chestOn, automatonOn, hideonleafOn);
/*     */     } else if (!this.espScanInProgress) {
/* 629 */       startEspScan(px, py, pz, rangeBlocks, titaniumOn, nodeOn, chestOn, automatonOn, hideonleafOn);
/*     */     }
/* 631 */     continueEspScan(world);
/*     */   }

/*     */   private boolean hasMovedForEspScan(int px, int py, int pz) {
/*     */     if (this.lastEspScanX == Integer.MIN_VALUE) {
/*     */       return true;
/*     */     }
/*     */     return (Math.abs(px - this.lastEspScanX) > ESP_SCAN_MOVE_THRESHOLD_BLOCKS || Math.abs(py - this.lastEspScanY) > ESP_SCAN_MOVE_THRESHOLD_BLOCKS || Math.abs(pz - this.lastEspScanZ) > ESP_SCAN_MOVE_THRESHOLD_BLOCKS);
/*     */   }

/*     */   private boolean shouldRestartEspScan(int px, int py, int pz, int rangeBlocks, boolean titaniumOn, boolean nodeOn, boolean chestOn, boolean automatonOn, boolean hideonleafOn) {
/*     */     if (!this.espScanInProgress) {
/*     */       return false;
/*     */     }
/*     */     if (this.espScanRangeBlocks != rangeBlocks) {
/*     */       return true;
/*     */     }
/*     */     if (this.espScanTitaniumOn != titaniumOn || this.espScanNodeOn != nodeOn || this.espScanChestOn != chestOn || this.espScanAutomatonOn != automatonOn || this.espScanHideonleafOn != hideonleafOn) {
/*     */       return true;
/*     */     }
/*     */     return (Math.abs(px - this.espScanCenterX) > ESP_SCAN_MOVE_THRESHOLD_BLOCKS || Math.abs(py - this.espScanCenterY) > ESP_SCAN_MOVE_THRESHOLD_BLOCKS || Math.abs(pz - this.espScanCenterZ) > ESP_SCAN_MOVE_THRESHOLD_BLOCKS);
/*     */   }

/*     */   private void startEspScan(int px, int py, int pz, int rangeBlocks, boolean titaniumOn, boolean nodeOn, boolean chestOn, boolean automatonOn, boolean hideonleafOn) {
/*     */     this.espScanCenterX = px;
/*     */     this.espScanCenterY = py;
/*     */     this.espScanCenterZ = pz;
/*     */     this.espScanRangeBlocks = rangeBlocks;
/*     */     this.espScanMinY = py - rangeBlocks;
/*     */     this.espScanMaxY = py + rangeBlocks;
/*     */     this.espScanCursorY = this.espScanMinY;
/*     */     this.espScanTitaniumOn = titaniumOn;
/*     */     this.espScanNodeOn = nodeOn;
/*     */     this.espScanChestOn = chestOn;
/*     */     this.espScanAutomatonOn = automatonOn;
/*     */     this.espScanHideonleafOn = hideonleafOn;
/*     */     this.espScanBlockScanOn = (titaniumOn || nodeOn || chestOn);
/*     */     this.espScanInitialized = false;
/*     */     this.espScanInProgress = true;
/*     */     this.titaniumBlocks.clear();
/*     */     this.nodeBlocks.clear();
/*     */     this.chestBlocks.clear();
/*     */     this.automatonBoxes.clear();
/*     */     this.hideonleafEntityBoxes.clear();
/*     */     this.titaniumRenderTasks.clear();
/*     */     this.nodeRenderTasks.clear();
/*     */     this.chestRenderTasks.clear();
/*     */     this.automatonRenderTasks.clear();
/*     */     this.hideonleafEntityRenderTasks.clear();
/*     */     this.espRenderTasksDirty = true;
/*     */   }

/*     */   private void continueEspScan(class_1937 world) {
/*     */     if (!this.espScanInProgress) {
/*     */       return;
/*     */     }
/*     */     if (world == null) {
/*     */       clearEspData();
/*     */       return;
/*     */     }
/*     */     if (!this.espScanBlockScanOn) {
/*     */       if (this.espScanAutomatonOn) {
/*     */         updateAutomatonEntities(world);
/*     */       }
/*     */       if (this.espScanHideonleafOn) {
/*     */         updateHideonleafEntities(world);
/*     */       }
/*     */       finishEspScan();
/*     */       return;
/*     */     }
/*     */     int rangeBlocks = this.espScanRangeBlocks;
/*     */     int startY = this.espScanCursorY;
/*     */     int endY = Math.min(startY + ESP_SCAN_SLICE_HEIGHT - 1, this.espScanMaxY);
/*     */     for (int x = this.espScanCenterX - rangeBlocks; x <= this.espScanCenterX + rangeBlocks; x++) {
/*     */       for (int y = startY; y <= endY; y++) {
/*     */         for (int z = this.espScanCenterZ - rangeBlocks; z <= this.espScanCenterZ + rangeBlocks; z++) {
/*     */           class_2338 pos = new class_2338(x, y, z);
/*     */           class_2680 state = world.method_8320(pos);
/*     */           byte highlightMatch = getHighlightMatch(state);
/*     */           if (this.espScanTitaniumOn && (highlightMatch & MATCH_TITANIUM) != 0) this.titaniumBlocks.add(pos); 
/*     */           if (this.espScanNodeOn && (highlightMatch & MATCH_NODE) != 0) this.nodeBlocks.add(pos); 
/*     */           if (this.espScanChestOn && (highlightMatch & MATCH_CHEST) != 0) this.chestBlocks.add(pos); 
/*     */         } 
/*     */       } 
/*     */     } 
/*     */     this.espScanCursorY = endY + 1;
/*     */     if (this.espScanCursorY > this.espScanMaxY) {
/*     */       if (this.espScanAutomatonOn) {
/*     */         updateAutomatonEntities(world);
/*     */       }
/*     */       if (this.espScanHideonleafOn) {
/*     */         updateHideonleafEntities(world);
/*     */       }
/*     */       finishEspScan();
/*     */     }
/*     */   }

/*     */   private void finishEspScan() {
/*     */     this.lastEspScanX = this.espScanCenterX;
/*     */     this.lastEspScanY = this.espScanCenterY;
/*     */     this.lastEspScanZ = this.espScanCenterZ;
/*     */     this.espScanInitialized = true;
/*     */     this.espScanInProgress = false;
/*     */     this.espRenderTasksDirty = true;
/*     */   }
/*     */   
/*     */   private void updateAutomatonEntities(class_1937 world) {
/*     */     if (world == null) {
/*     */       return;
/*     */     }
/*     */     Iterable<?> entities = getEntityIterable(world);
/*     */     if (entities == null) {
/*     */       return;
/*     */     }
/*     */     for (Object obj : entities) {
/*     */       if (!(obj instanceof net.minecraft.class_1297)) {
/*     */         continue;
/*     */       }
/*     */       net.minecraft.class_1297 entity = (net.minecraft.class_1297)obj;
/*     */       if (entity == this.mc.field_1724) {
/*     */         continue;
/*     */       }
/*     */       if (!isAutomatonEntity(entity)) {
/*     */         continue;
/*     */       }
/*     */       class_238 box = getEntityBox(entity);
/*     */       if (box == null) {
/*     */         continue;
/*     */       }
/*     */       this.automatonBoxes.add(box);
/*     */     }
/*     */   }
/*     */   
/*     */   private void updateHideonleafEntities(class_1937 world) {
/*     */     if (world == null) {
/*     */       return;
/*     */     }
/*     */     Iterable<?> entities = getEntityIterable(world);
/*     */     if (entities == null) {
/*     */       return;
/*     */     }
/*     */     for (Object obj : entities) {
/*     */       if (!(obj instanceof net.minecraft.class_1297)) {
/*     */         continue;
/*     */       }
/*     */       net.minecraft.class_1297 entity = (net.minecraft.class_1297)obj;
/*     */       if (entity == this.mc.field_1724) {
/*     */         continue;
/*     */       }
/*     */       if (!isGreenShulkerEntity(entity)) {
/*     */         continue;
/*     */       }
/*     */       class_238 box = getEntityBox(entity);
/*     */       if (box == null) {
/*     */         continue;
/*     */       }
/*     */       this.hideonleafEntityBoxes.add(box);
/*     */     }
/*     */   }
/*     */   
/*     */   private void clearEspData() {
/* 645 */     this.titaniumBlocks.clear();
/* 646 */     this.nodeBlocks.clear();
/* 646 */     this.chestBlocks.clear();
/*     */     this.automatonBoxes.clear();
/*     */     this.hideonleafEntityBoxes.clear();
/* 647 */     this.titaniumRenderTasks.clear();
/* 648 */     this.nodeRenderTasks.clear();
/* 648 */     this.chestRenderTasks.clear();
/*     */     this.automatonRenderTasks.clear();
/*     */     this.hideonleafEntityRenderTasks.clear();
/* 649 */     this.espScanInitialized = false;
/* 649 */     this.espScanInProgress = false;
/* 649 */     this.espRenderTasksDirty = false;
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
/* 662 */     if (blockText.contains("chest")) match = (byte)(match | MATCH_CHEST); 
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
/*     */   private boolean shouldAutoMeow(String fullMessage, String content) {
/* 676 */     if (content == null || content.isBlank()) {
/* 677 */       return false;
/*     */     }
/* 679 */     if (isOwnChatMessage(fullMessage)) {
/* 680 */       return false;
/*     */     }
/* 682 */     for (String trigger : AUTO_MEOW_TRIGGERS) {
/* 683 */       if (content.contains(trigger)) {
/* 684 */         return true;
/*     */       }
/*     */     }
/* 687 */     return false;
/*     */   }
/*     */   
/*     */   private boolean isOwnChatMessage(String message) {
/* 691 */     String sender = extractChatSender(message);
/* 692 */     if (sender == null || sender.isBlank() || this.mc == null || this.mc.method_1548() == null) {
/* 693 */       return false;
/*     */     }
/* 695 */     String playerName = this.mc.method_1548().method_1676();
/* 696 */     return (playerName != null && sender.equalsIgnoreCase(playerName));
/*     */   }
/*     */   
/*     */   private String extractChatSender(String message) {
/* 700 */     if (message == null || message.isBlank()) {
/* 701 */       return null;
/*     */     }
/* 703 */     Matcher matcher = CHAT_SENDER_PATTERN.matcher(message);
/* 704 */     if (!matcher.find()) {
/* 705 */       return null;
/*     */     }
/* 707 */     return matcher.group(1);
/*     */   }
/*     */   
/*     */   private String formatChatCommand(String raw, String chatPrefix) {
/* 677 */     String out = raw;
/* 678 */     if (out == null) return (chatPrefix == null) ? "" : chatPrefix;
/* 679 */     out = out.trim();
/* 680 */     if (out.startsWith("/pc ")) out = out.substring(4);
/* 681 */     else if (out.startsWith("pc ")) out = out.substring(3);
/* 682 */     else if (out.startsWith("/gc ")) out = out.substring(4);
/* 683 */     else if (out.startsWith("gc ")) out = out.substring(3);
/* 684 */     if (chatPrefix == null || chatPrefix.isBlank()) return out; 
/* 685 */     return chatPrefix + " " + out;
/*     */   }
/*     */   
/*     */   private static boolean isMathCommand(String content) {
/*     */     return (content.equals("!math") || content.startsWith("!math ") || content.startsWith("!math("));
/*     */   }
/*     */   
/*     */   private static String evaluateMathCommand(String expression) {
/*     */     if (expression == null || expression.isBlank()) {
/*     */       return "Invalid math expression.";
/*     */     }
/*     */     Double result = evaluateMathExpression(expression);
/*     */     if (result == null || result.isNaN() || result.isInfinite()) {
/*     */       return "Invalid math expression.";
/*     */     }
/*     */     return formatMathResult(result.doubleValue());
/*     */   }
/*     */   
/*     */   private static String formatMathResult(double value) {
/*     */     BigDecimal decimal = BigDecimal.valueOf(value).stripTrailingZeros();
/*     */     String text = decimal.toPlainString();
/*     */     return text.equals("-0") ? "0" : text;
/*     */   }
/*     */   
/*     */   private static Double evaluateMathExpression(String expression) {
/*     */     if (expression == null) return null; 
/*     */     String input = expression.trim();
/*     */     if (input.isEmpty()) return null; 
/*     */     ArrayDeque<Double> values = new ArrayDeque<>();
/*     */     ArrayDeque<Character> ops = new ArrayDeque<>();
/*     */     int i = 0;
/*     */     boolean expectNumber = true;
/*     */     while (i < input.length()) {
/*     */       char c = input.charAt(i);
/*     */       if (Character.isWhitespace(c)) {
/*     */         i++;
/*     */         continue;
/*     */       } 
/*     */       if (c == '(') {
/*     */         ops.push(Character.valueOf(c));
/*     */         i++;
/*     */         expectNumber = true;
/*     */         continue;
/*     */       } 
/*     */       if (c == ')') {
/*     */         while (!ops.isEmpty() && ((Character)ops.peek()).charValue() != '(') {
/*     */           if (!applyMathOperator(values, ((Character)ops.pop()).charValue())) return null; 
/*     */         } 
/*     */         if (ops.isEmpty() || ((Character)ops.peek()).charValue() != '(') return null; 
/*     */         ops.pop();
/*     */         i++;
/*     */         expectNumber = false;
/*     */         continue;
/*     */       } 
/*     */       if (expectNumber) {
/*     */         if (c == '+' || c == '-') {
/*     */           int nextIndex = nextNonSpaceIndex(input, i + 1);
/*     */           if (nextIndex == -1) return null; 
/*     */           char next = input.charAt(nextIndex);
/*     */           if (next == '(') {
/*     */             if (c == '-') {
/*     */               values.push(Double.valueOf(0.0D));
/*     */               while (!ops.isEmpty() && ((Character)ops.peek()).charValue() != '(' && shouldApplyMathOperator(((Character)ops.peek()).charValue(), '-')) {
/*     */                 if (!applyMathOperator(values, ((Character)ops.pop()).charValue())) return null; 
/*     */               } 
/*     */               ops.push(Character.valueOf('-'));
/*     */             } 
/*     */             i++;
/*     */             expectNumber = true;
/*     */             continue;
/*     */           } 
/*     */           if (Character.isDigit(next) || next == '.') {
/*     */             NumberParse parsed = parseNumber(input, nextIndex);
/*     */             if (parsed == null) return null; 
/*     */             double value = parsed.value();
/*     */             if (c == '-') value = -value; 
/*     */             values.push(Double.valueOf(value));
/*     */             i = parsed.nextIndex();
/*     */             expectNumber = false;
/*     */             continue;
/*     */           } 
/*     */           return null;
/*     */         } 
/*     */         if (Character.isDigit(c) || c == '.') {
/*     */           NumberParse parsed = parseNumber(input, i);
/*     */           if (parsed == null) return null; 
/*     */           values.push(Double.valueOf(parsed.value()));
/*     */           i = parsed.nextIndex();
/*     */           expectNumber = false;
/*     */           continue;
/*     */         } 
/*     */         return null;
/*     */       } 
/*     */       if (isMathOperator(c)) {
/*     */         while (!ops.isEmpty() && ((Character)ops.peek()).charValue() != '(' && shouldApplyMathOperator(((Character)ops.peek()).charValue(), c)) {
/*     */           if (!applyMathOperator(values, ((Character)ops.pop()).charValue())) return null; 
/*     */         } 
/*     */         ops.push(Character.valueOf(c));
/*     */         i++;
/*     */         expectNumber = true;
/*     */         continue;
/*     */       } 
/*     */       return null;
/*     */     } 
/*     */     if (expectNumber) return null; 
/*     */     while (!ops.isEmpty()) {
/*     */       char op = ((Character)ops.pop()).charValue();
/*     */       if (op == '(' || op == ')') return null; 
/*     */       if (!applyMathOperator(values, op)) return null; 
/*     */     } 
/*     */     if (values.size() != 1) return null; 
/*     */     return values.pop();
/*     */   }
/*     */   
/*     */   private static boolean isMathOperator(char operator) {
/*     */     return (operator == '+' || operator == '-' || operator == '*' || operator == '/' || operator == '%' || operator == '^');
/*     */   }
/*     */   
/*     */   private static int mathOperatorPrecedence(char operator) {
/*     */     if (operator == '+' || operator == '-') return 1; 
/*     */     if (operator == '*' || operator == '/' || operator == '%') return 2; 
/*     */     if (operator == '^') return 3; 
/*     */     return -1;
/*     */   }
/*     */   
/*     */   private static boolean shouldApplyMathOperator(char top, char current) {
/*     */     int topPrecedence = mathOperatorPrecedence(top);
/*     */     int currentPrecedence = mathOperatorPrecedence(current);
/*     */     if (topPrecedence < currentPrecedence) return false; 
/*     */     if (topPrecedence > currentPrecedence) return true; 
/*     */     return (current != '^');
/*     */   }
/*     */   
/*     */   private static boolean applyMathOperator(ArrayDeque<Double> values, char operator) {
/*     */     if (values.size() < 2) return false; 
/*     */     double right = ((Double)values.pop()).doubleValue();
/*     */     double left = ((Double)values.pop()).doubleValue();
/*     */     double result;
/*     */     if (operator == '+') {
/*     */       result = left + right;
/*     */     } else if (operator == '-') {
/*     */       result = left - right;
/*     */     } else if (operator == '*') {
/*     */       result = left * right;
/*     */     } else if (operator == '/') {
/*     */       if (Math.abs(right) < 1.0E-12D) return false; 
/*     */       result = left / right;
/*     */     } else if (operator == '%') {
/*     */       if (Math.abs(right) < 1.0E-12D) return false; 
/*     */       result = left % right;
/*     */     } else if (operator == '^') {
/*     */       result = Math.pow(left, right);
/*     */     } else {
/*     */       return false;
/*     */     } 
/*     */     if (Double.isNaN(result) || Double.isInfinite(result)) return false; 
/*     */     values.push(Double.valueOf(result));
/*     */     return true;
/*     */   }
/*     */   
/*     */   private static int nextNonSpaceIndex(String input, int startIndex) {
/*     */     for (int i = startIndex; i < input.length(); i++) {
/*     */       if (!Character.isWhitespace(input.charAt(i))) return i; 
/*     */     } 
/*     */     return -1;
/*     */   }
/*     */   
/*     */   private static NumberParse parseNumber(String input, int startIndex) {
/*     */     int i = startIndex;
/*     */     boolean seenDigit = false;
/*     */     boolean seenDot = false;
/*     */     while (i < input.length()) {
/*     */       char ch = input.charAt(i);
/*     */       if (Character.isDigit(ch)) {
/*     */         seenDigit = true;
/*     */         i++;
/*     */         continue;
/*     */       } 
/*     */       if (ch == '.' && !seenDot) {
/*     */         seenDot = true;
/*     */         i++;
/*     */         continue;
/*     */       } 
/*     */       break;
/*     */     } 
/*     */     if (!seenDigit) return null; 
/*     */     String token = input.substring(startIndex, i);
/*     */     try {
/*     */       double value = Double.parseDouble(token);
/*     */       return new NumberParse(value, i);
/*     */     } catch (NumberFormatException ex) {
/*     */       return null;
/*     */     } 
/*     */   }
/*     */   
/*     */   private record NumberParse(double value, int nextIndex) {}
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
/*     */   private String resolveCommandChatPrefix(String raw, String stripped) {
/*     */     if (isPartyChatMessage(raw, stripped) && ((Boolean)this.partyChatCommandsEnabled.getValue()).booleanValue()) {
/*     */       return "pc";
/*     */     }
/*     */     if (isGuildChatMessage(raw, stripped) && ((Boolean)this.guildChatCommandsEnabled.getValue()).booleanValue()) {
/*     */       return "gc";
/*     */     }
/*     */     if (((Boolean)this.privateMessageChatCommandsEnabled.getValue()).booleanValue() && isIncomingPrivateMessage(raw, stripped)) {
/*     */       String pmTarget = extractPrivateMessageTarget(stripped);
/*     */       if (pmTarget != null && !pmTarget.isBlank()) {
/*     */         return "msg " + pmTarget;
/*     */       }
/*     */     }
/*     */     return null;
/*     */   }
/*     */   
/*     */   private String resolveResponsiveChatPrefix(String raw, String stripped) {
/*     */     if (isPartyChatMessage(raw, stripped)) {
/*     */       return "pc";
/*     */     }
/*     */     if (isGuildChatMessage(raw, stripped)) {
/*     */       return "gc";
/*     */     }
/*     */     if (isIncomingPrivateMessage(raw, stripped)) {
/*     */       String pmTarget = extractPrivateMessageTarget(stripped);
/*     */       if (pmTarget != null && !pmTarget.isBlank()) {
/*     */         return "msg " + pmTarget;
/*     */       }
/*     */       return null;
/*     */     }
/*     */     return isPublicPlayerChatMessage(stripped) ? "" : null;
/*     */   }
/*     */   
/*     */   private boolean isPublicPlayerChatMessage(String message) {
/*     */     if (message == null || message.isBlank()) {
/*     */       return false;
/*     */     }
/*     */     if (message.startsWith("To ") || message.startsWith("From ")) {
/*     */       return false;
/*     */     }
/*     */     return (message.contains(": ") && extractChatSender(message) != null);
/*     */   }
/*     */   
/*     */   private boolean isIncomingPrivateMessage(String raw, String stripped) {
/* 700 */     if (stripped != null && stripped.startsWith("From ")) return true; 
/* 701 */     if (raw == null) return false; 
/* 702 */     return raw.startsWith("From ");
/*     */   }
/*     */   
/*     */   private String extractPrivateMessageTarget(String message) {
/* 706 */     if (message == null || !message.startsWith("From ")) {
/* 707 */       return null;
/*     */     }
/* 709 */     int colonIndex = message.indexOf(": ");
/* 710 */     if (colonIndex <= 5) {
/* 711 */       return null;
/*     */     }
/* 713 */     String header = message.substring(5, colonIndex).trim();
/* 714 */     if (header.isEmpty()) {
/* 715 */       return null;
/*     */     }
/* 717 */     if (header.startsWith("[")) {
/* 718 */       int rankEnd = header.indexOf(']');
/* 719 */       if (rankEnd != -1 && rankEnd + 1 < header.length()) {
/* 720 */         header = header.substring(rankEnd + 1).trim();
/*     */       }
/*     */     }
/* 723 */     return header.isEmpty() ? null : header;
/*     */   }
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
/* 697 */     if (((Boolean)this.coopChatWebhookEnabled.getValue()).booleanValue() && clean.contains("Co-op > ")) {
/* 698 */       postToConfiguredWebhook((String)this.coopChatWebhook.getValue(), content);
/*     */     }
/* 698 */     if (((Boolean)this.privateMessagesWebhookEnabled.getValue()).booleanValue() && (clean.contains("To ") || clean.contains("From ")) && !clean.contains("From stash: ") && !clean.contains("[WARP] To Elizabeth in the next")) {
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
