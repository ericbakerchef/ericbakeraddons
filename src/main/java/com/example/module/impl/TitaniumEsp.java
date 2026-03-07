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
import net.minecraft.class_243;
import net.minecraft.class_2680;
import net.minecraft.class_310;

import java.lang.reflect.Constructor;
import java.lang.reflect.Method;
import java.util.ArrayList;
import java.util.List;
import java.util.Locale;

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
    private final BooleanSetting tracer = new BooleanSetting("Tracer", true);
    private final List<class_2338> highlightedBlocks = new ArrayList<>();

    private int tickCounter;

    private Constructor<?> filledBoxConstructor;
    private Constructor<?> outlineBoxConstructor;
    private Constructor<?> lineConstructor;
    private Method addTaskMethod;
    private boolean renderBridgeReady;

    public TitaniumEsp() {
        setGroup(this.titaniumGroup);
        registerProperty(new Setting[] { this.titaniumGroup });
        this.titaniumGroup.add(new Setting[] { this.enable, this.tracer });
        this.renderBridgeReady = initRenderBridge();
    }

    @SubscribeEvent
    public void onTick(ClientTickEvent.End event) {
        if (!isEnabled() || !((Boolean) this.enable.getValue()).booleanValue()) {
            this.highlightedBlocks.clear();
            return;
        }

        this.tickCounter++;
        if (this.tickCounter % SCAN_INTERVAL_TICKS == 0) {
            updateBlocks();
        }
    }

    @SubscribeEvent
    public void onRender3D(Render3DEvent.Last event) {
        if (!isEnabled() || !((Boolean) this.enable.getValue()).booleanValue()) {
            return;
        }

        if (!this.renderBridgeReady) {
            this.renderBridgeReady = initRenderBridge();
            if (!this.renderBridgeReady) {
                return;
            }
        }

        if (this.highlightedBlocks.isEmpty()) {
            updateBlocks();
        }

        class_2338 tracerTarget = null;
        for (class_2338 pos : this.highlightedBlocks) {
            try {
                class_238 box = new class_238(pos);
                Object filledTask = this.filledBoxConstructor.newInstance(box, FILL_COLOUR, false);
                Object outlineTask = this.outlineBoxConstructor.newInstance(box, OUTLINE_COLOUR, false);
                this.addTaskMethod.invoke(null, filledTask);
                this.addTaskMethod.invoke(null, outlineTask);
            } catch (ReflectiveOperationException ignored) {
                this.renderBridgeReady = false;
                return;
            }

            if (((Boolean) this.tracer.getValue()).booleanValue() && tracerTarget == null) {
                tracerTarget = pos;
            }
        }

        if (!((Boolean) this.tracer.getValue()).booleanValue() || tracerTarget == null) {
            return;
        }

        try {
            if (this.lineConstructor == null) {
                this.lineConstructor = resolveLineConstructor();
            }
            if (this.lineConstructor == null) {
                return;
            }

            Object cameraPos = getCameraPos(event);
            if (cameraPos == null) {
                return;
            }

            class_243 target = new class_243(
                tracerTarget.method_10263() + 0.5D,
                tracerTarget.method_10264() + 0.5D,
                tracerTarget.method_10260() + 0.5D
            );
            Object lineTask = this.lineConstructor.newInstance(cameraPos, target, OUTLINE_COLOUR, OUTLINE_COLOUR, false);
            this.addTaskMethod.invoke(null, lineTask);
        } catch (ReflectiveOperationException ignored) {
            this.lineConstructor = null;
        }
    }

    private void updateBlocks() {
        if (this.mc.field_1687 == null || this.mc.field_1724 == null) {
            this.highlightedBlocks.clear();
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
                    if (isPolishedDiorite(state)) {
                        this.highlightedBlocks.add(pos);
                    }
                }
            }
        }
    }

    private boolean isPolishedDiorite(class_2680 state) {
        if (state == null) {
            return false;
        }

        String stateText = String.valueOf(state).toLowerCase(Locale.ROOT);
        if (stateText.contains("polished_diorite")) {
            return true;
        }

        String blockText = String.valueOf(state.method_26204()).toLowerCase(Locale.ROOT);
        return blockText.contains("polished_diorite");
    }

    private boolean initRenderBridge() {
        try {
            Class<?> filledBoxClass = Class.forName("com.ricedotwho.rsm.utils.render.render3d.type.FilledBox");
            Class<?> outlineBoxClass = Class.forName("com.ricedotwho.rsm.utils.render.render3d.type.OutlineBox");
            Class<?> renderTaskClass = Class.forName("com.ricedotwho.rsm.utils.render.render3d.type.RenderTask");

            this.filledBoxConstructor = findConstructor(filledBoxClass, 3);
            this.outlineBoxConstructor = findConstructor(outlineBoxClass, 3);
            this.lineConstructor = resolveLineConstructor();
            this.addTaskMethod = Renderer3D.class.getMethod("addTask", renderTaskClass);

            return this.filledBoxConstructor != null && this.outlineBoxConstructor != null;
        } catch (ReflectiveOperationException ignored) {
            return false;
        }
    }

    private Constructor<?> resolveLineConstructor() {
        try {
            Class<?> lineClass = Class.forName("com.ricedotwho.rsm.utils.render.render3d.type.Line");
            return findConstructor(lineClass, 5);
        } catch (ReflectiveOperationException ignored) {
            return null;
        }
    }

    private Constructor<?> findConstructor(Class<?> taskClass, int parameterCount) {
        for (Constructor<?> constructor : taskClass.getConstructors()) {
            Class<?>[] params = constructor.getParameterTypes();
            if (params.length == parameterCount) {
                if (parameterCount == 3 && params[1] == Colour.class && params[2] == boolean.class) {
                    return constructor;
                }
                if (parameterCount == 5 && params[2] == Colour.class && params[3] == Colour.class && params[4] == boolean.class) {
                    return constructor;
                }
            }
        }
        return null;
    }

    private Object getCameraPos(Render3DEvent.Last event) {
        try {
            Object context = invokeNoArg(event, "getContext");
            Object camera = invokeNoArg(context, "camera", "getCamera");
            return invokeNoArg(camera, "getPos", "getPosition");
        } catch (ReflectiveOperationException ignored) {
            return null;
        }
    }

    private Object invokeNoArg(Object target, String... names) throws ReflectiveOperationException {
        if (target == null) {
            return null;
        }

        ReflectiveOperationException last = null;
        for (String name : names) {
            try {
                Method method = target.getClass().getMethod(name);
                return method.invoke(target);
            } catch (ReflectiveOperationException ex) {
                last = ex;
            }
        }
        throw last;
    }
}
