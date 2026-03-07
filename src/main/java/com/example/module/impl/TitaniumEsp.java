package com.example.module.impl;

import com.ricedotwho.rsm.component.impl.Renderer3D;
import com.ricedotwho.rsm.data.Colour;
import com.ricedotwho.rsm.event.api.SubscribeEvent;
import com.ricedotwho.rsm.event.impl.game.ClientTickEvent;
import com.ricedotwho.rsm.event.impl.render.Render3DEvent;
import com.ricedotwho.rsm.module.Module;
import com.ricedotwho.rsm.module.api.Category;
import com.ricedotwho.rsm.module.api.ModuleInfo;
import com.ricedotwho.rsm.ui.clickgui.settings.Setting;
import com.ricedotwho.rsm.ui.clickgui.settings.group.DefaultGroupSetting;
import com.ricedotwho.rsm.ui.clickgui.settings.impl.BooleanSetting;
import net.minecraft.class_1937;
import net.minecraft.class_2338;
import net.minecraft.class_238;
import net.minecraft.class_2680;
import net.minecraft.class_310;

import java.lang.reflect.Constructor;
import java.lang.reflect.Method;
import java.util.ArrayList;
import java.util.List;

@ModuleInfo(aliases = {"Titanium ESP"}, id = "titanium_esp", category = Category.RENDER)
public class TitaniumEsp extends Module {
    private static final int HORIZONTAL_RANGE = 24;
    private static final int VERTICAL_RANGE = 16;
    private static final int SCAN_INTERVAL_TICKS = 10;

    private static final Colour FILL_COLOUR = new Colour(80, 180, 255, 55);
    private static final Colour OUTLINE_COLOUR = new Colour(110, 210, 255, 180);

    private final class_310 mc = class_310.method_1551();
    private final DefaultGroupSetting titaniumGroup = new DefaultGroupSetting("Titanium ESP", this);
    private final BooleanSetting enable = new BooleanSetting("Enable", true);
    private final List<class_2338> highlightedBlocks = new ArrayList<>();

    private Constructor<?> filledBoxConstructor;
    private Constructor<?> outlineBoxConstructor;
    private Method addTaskMethod;
    private boolean rendererBridgeReady;

    private int tickCounter;

    public TitaniumEsp() {
        setGroup(this.titaniumGroup);
        registerProperty(new Setting[] { this.titaniumGroup });
        this.titaniumGroup.add(new Setting[] { this.enable });
        this.rendererBridgeReady = initRendererBridge();
    }

    @SubscribeEvent
    public void onTick(ClientTickEvent.End event) {
        if (!isEnabled() || !((Boolean) this.enable.getValue()).booleanValue()) {
            this.highlightedBlocks.clear();
            return;
        }

        if (this.mc.field_1687 == null || this.mc.field_1724 == null) {
            this.highlightedBlocks.clear();
            return;
        }

        this.tickCounter++;
        if (this.tickCounter % SCAN_INTERVAL_TICKS != 0) {
            return;
        }

        class_1937 world = this.mc.field_1687;
        class_2338 playerPos = this.mc.field_1724.method_24515();

        int px = playerPos.method_10263();
        int py = playerPos.method_10264();
        int pz = playerPos.method_10260();

        this.highlightedBlocks.clear();
        for (int x = px - HORIZONTAL_RANGE; x <= px + HORIZONTAL_RANGE; x++) {
            for (int y = py - VERTICAL_RANGE; y <= py + VERTICAL_RANGE; y++) {
                for (int z = pz - HORIZONTAL_RANGE; z <= pz + HORIZONTAL_RANGE; z++) {
                    class_2338 pos = new class_2338(x, y, z);
                    class_2680 state = world.method_8320(pos);

                    if (!isPolishedDiorite(state)) {
                        continue;
                    }

                    this.highlightedBlocks.add(pos);
                }
            }
        }
    }

    @SubscribeEvent
    public void onRender3D(Render3DEvent.Last event) {
        if (!isEnabled() || !((Boolean) this.enable.getValue()).booleanValue() || !this.rendererBridgeReady) {
            return;
        }

        for (class_2338 pos : this.highlightedBlocks) {
            int x = pos.method_10263();
            int y = pos.method_10264();
            int z = pos.method_10260();
            class_238 box = new class_238(x, y, z, x + 1.0D, y + 1.0D, z + 1.0D);

            try {
                Object filledTask = this.filledBoxConstructor.newInstance(box, FILL_COLOUR, false);
                Object outlineTask = this.outlineBoxConstructor.newInstance(box, OUTLINE_COLOUR, false);
                this.addTaskMethod.invoke(null, filledTask);
                this.addTaskMethod.invoke(null, outlineTask);
            } catch (ReflectiveOperationException ignored) {
                this.rendererBridgeReady = false;
                return;
            }
        }
    }

    private boolean isPolishedDiorite(class_2680 state) {
        if (state == null) {
            return false;
        }

        String blockIdText = state.method_26204().toString();
        return blockIdText != null && blockIdText.contains("polished_diorite");
    }

    private boolean initRendererBridge() {
        try {
            Class<?> filledBoxClass = Class.forName("com.ricedotwho.rsm.utils.render.render3d.type.FilledBox");
            Class<?> outlineBoxClass = Class.forName("com.ricedotwho.rsm.utils.render.render3d.type.OutlineBox");

            this.filledBoxConstructor = findRenderTaskConstructor(filledBoxClass);
            this.outlineBoxConstructor = findRenderTaskConstructor(outlineBoxClass);

            this.addTaskMethod = Renderer3D.class.getMethod("addTask", Class.forName("com.ricedotwho.rsm.utils.render.render3d.type.RenderTask"));
            return this.filledBoxConstructor != null && this.outlineBoxConstructor != null;
        } catch (ReflectiveOperationException ignored) {
            return false;
        }
    }

    private Constructor<?> findRenderTaskConstructor(Class<?> renderTaskClass) {
        for (Constructor<?> constructor : renderTaskClass.getConstructors()) {
            Class<?>[] params = constructor.getParameterTypes();
            if (params.length == 3 && params[1] == Colour.class && params[2] == boolean.class) {
                return constructor;
            }
        }
        return null;
    }
}
