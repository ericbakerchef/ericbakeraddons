package com.example;
import com.example.chat.ChatRewriter;
import com.example.module.impl.ChatCommands;
import com.ricedotwho.rsm.addon.Addon;
import com.ricedotwho.rsm.command.Command;
import com.ricedotwho.rsm.component.api.ModComponent;
import com.ricedotwho.rsm.module.Module;
import com.ricedotwho.rsm.utils.ChatUtils;
import java.util.List;
public class ericbakeraddons
implements Addon {
public void onInitialize() {
ChatRewriter.registerHooks();
ChatUtils.chat("ericbakeraddons loaded gg its over for you", new Object[0]);
}
public void onUnload() {
ChatUtils.chat("ericbakeraddons unloaded, you're saved", new Object[0]);
}
public List<Class<? extends Module>> getModules() {
return (List)List.of(ChatCommands.class);
}
public List<Class<? extends ModComponent>> getComponents() {
return List.of();
}
public List<Class<? extends Command>> getCommands() {
return List.of();
}
}

