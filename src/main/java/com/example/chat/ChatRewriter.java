package com.example.chat;
import com.example.module.impl.ChatCommands;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import net.fabricmc.fabric.api.client.message.v1.ClientReceiveMessageEvents;
import net.minecraft.class_124;
import net.minecraft.class_2561;
import net.minecraft.class_5250;
public final class ChatRewriter
{
private static boolean registered = false;
private static final Pattern BRACKET_NUMBER = Pattern.compile("\\[(\\d{3})\\]");
public static void registerHooks() {
if (registered) {
return;
}
ClientReceiveMessageEvents.ALLOW_GAME.register((text, overlay) -> {
if (ChatCommands.shouldSuppressPickaxeChat(text)) {
ChatCommands.handleSuppressedPickaxeMessage(text);
return false;
}
return true;
});
ClientReceiveMessageEvents.ALLOW_CHAT.register((text, signedMessage, sender, params, timestamp) -> {
if (ChatCommands.shouldSuppressPickaxeChat(text)) {
ChatCommands.handleSuppressedPickaxeMessage(text);
return false;
}
return true;
});
ClientReceiveMessageEvents.MODIFY_GAME.register((text, overlay) -> {
if (ChatCommands.shouldSuppressPickaxeChat(text)) {
return (class_2561)class_2561.method_43470("");
}
return rewriteAll(text);
});
registered = true;
}
public static class_2561 rewriteAll(class_2561 input) {
if (input == null) {
return null;
}
String raw = input.getString();
class_2561 rewritten = rewriteText(raw);
return (rewritten == null) ? input : rewritten;
}
public static class_2561 rewriteBracketRanges(class_2561 input) {
return rewriteAll(input);
}
private static class_2561 rewriteText(String raw) {
boolean rewriteBrackets = ChatCommands.isLevelPrefixEnabled();
if (!rewriteBrackets) {
return null;
}
class_5250 out = class_2561.method_43473();
int index = 0;
Matcher matcher = BRACKET_NUMBER.matcher(raw);
boolean changedBrackets = false;
while (matcher.find()) {
class_124 bracketColor; int value = Integer.parseInt(matcher.group(1));
if (value < 480 || value > 559) {
continue;
}
if (value <= 519) {
bracketColor = ChatCommands.isGoldBracketsEnabled() ? class_124.field_1065 : class_124.field_1063;
} else {
bracketColor = ChatCommands.isDiamondBracketsEnabled() ? class_124.field_1075 : class_124.field_1063;
} 
class_124 numberColor = ChatCommands.isRed480PlusEnabled() ? class_124.field_1061 : class_124.field_1079;
class_5250 replacement = buildReplacement(value, bracketColor, numberColor);
changedBrackets = true;
int start = matcher.start();
int end = matcher.end();
if (start > index) {
String segment = raw.substring(index, start);
out.method_10852((class_2561)class_2561.method_43470(segment));
} 
out.method_10852((class_2561)replacement.method_27661());
index = end;
} 
if (index < raw.length()) {
String segment = raw.substring(index);
out.method_10852((class_2561)class_2561.method_43470(segment));
} 
if (!changedBrackets) {
return null;
}
return (class_2561)out;
}
private static class_5250 buildReplacement(int value, class_124 bracketColor, class_124 numberColor) {
class_5250 replacement = class_2561.method_43470("");
replacement.method_10852((class_2561)class_2561.method_43470("[").method_27692(bracketColor));
replacement.method_10852((class_2561)class_2561.method_43470(String.valueOf(value)).method_27692(numberColor));
replacement.method_10852((class_2561)class_2561.method_43470("]").method_27692(bracketColor));
return replacement;
}
}

