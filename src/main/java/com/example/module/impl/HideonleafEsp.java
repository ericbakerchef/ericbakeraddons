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
import net.minecraft.class_1297;
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

@ModuleInfo(aliases = {"Hideonleaf"}, id = "hideonleaf_esp", category = Category.RENDER)
public class HideonleafEsp extends Module {
    private static final int HORIZONTAL_RANGE = 24;
    private static final int VERTICAL_RANGE = 16;
    private static final int SCAN_INTERVAL_TICKS = 10;

    private static final Colour FILL_COLOUR = new Colour(30, 120, 30, 55);
    private static final Colour OUTLINE_COLOUR = new Colour(60, 170, 60, 190);

    private final class_310 mc = class_310.method_1551();
    private final DefaultGroupSetting espGroup = new DefaultGroupSetting("ESP", this);
    private final BooleanSetting enable = new BooleanSetting("Enable", true);
    private final BooleanSetting tracer = new BooleanSetting("Tracer", true);
    private final List<class_2338> highlightedBlocks = new ArrayList<>();
    private final List<class_238> highlightedEntityBoxes = new ArrayList<>();
    private final List<class_2338> highlightedEntityTracerTargets = new ArrayList<>();

    private int tickCounter;

    private Constructor<?> filledBoxConstructor;
    private Constructor<?> outlineBoxConstructor;
    private Constructor<?> lineConstructor;
    private Method addTaskMethod;
    private boolean renderBridgeReady;
    private Method entityBoundingBoxMethod;
    private Method entityTypeMethod;

    public HideonleafEsp() {
        setGroup(this.espGroup);
        registerProperty(new Setting[] { this.espGroup });
        this.espGroup.add(new Setting[] { this.enable, this.tracer });
        this.renderBridgeReady = initRenderBridge();
    }

    @SubscribeEvent
    public void onTick(ClientTickEvent.End event) {
        if (!isEnabled() || !((Boolean) this.enable.getValue()).booleanValue()) {
            this.highlightedBlocks.clear();
            this.highlightedEntityBoxes.clear();
            this.highlightedEntityTracerTargets.clear();
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

        List<class_2338> tracerTargets = ((Boolean) this.tracer.getValue()).booleanValue() ? new ArrayList<>() : null;
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

            if (tracerTargets != null) {
                tracerTargets.add(pos);
            }
        }

        for (class_238 box : this.highlightedEntityBoxes) {
            try {
                Object filledTask = this.filledBoxConstructor.newInstance(box, FILL_COLOUR, false);
                Object outlineTask = this.outlineBoxConstructor.newInstance(box, OUTLINE_COLOUR, false);
                this.addTaskMethod.invoke(null, filledTask);
                this.addTaskMethod.invoke(null, outlineTask);
            } catch (ReflectiveOperationException ignored) {
                this.renderBridgeReady = false;
                return;
            }
        }

        if (tracerTargets != null && !this.highlightedEntityTracerTargets.isEmpty()) {
            tracerTargets.addAll(this.highlightedEntityTracerTargets);
        }

        if (tracerTargets == null || tracerTargets.isEmpty()) {
            return;
        }

        try {
            if (this.lineConstructor == null) {
                this.lineConstructor = resolveLineConstructor();
            }
            if (this.lineConstructor == null) {
                return;
            }

            class_243 tracerStart = getTracerStart(event);
            if (tracerStart == null) {
                return;
            }

            for (class_2338 tracerTarget : tracerTargets) {
                class_243 target = new class_243(
                    tracerTarget.method_10263() + 0.5D,
                    tracerTarget.method_10264() + 0.5D,
                    tracerTarget.method_10260() + 0.5D
                );
                Object lineTask = this.lineConstructor.newInstance(tracerStart, target, OUTLINE_COLOUR, OUTLINE_COLOUR, false);
                this.addTaskMethod.invoke(null, lineTask);
            }
        } catch (ReflectiveOperationException ignored) {
            this.lineConstructor = null;
        }
    }

    private void updateBlocks() {
        if (this.mc.field_1687 == null || this.mc.field_1724 == null) {
            this.highlightedBlocks.clear();
            this.highlightedEntityBoxes.clear();
            this.highlightedEntityTracerTargets.clear();
            return;
        }

        class_1937 world = this.mc.field_1687;
        class_2338 playerPos = this.mc.field_1724.method_24515();

        int px = playerPos.method_10263();
        int py = playerPos.method_10264();
        int pz = playerPos.method_10260();

        this.highlightedBlocks.clear();
        this.highlightedEntityBoxes.clear();
        this.highlightedEntityTracerTargets.clear();
        for (int x = px - HORIZONTAL_RANGE; x <= px + HORIZONTAL_RANGE; x++) {
            for (int y = py - VERTICAL_RANGE; y <= py + VERTICAL_RANGE; y++) {
                for (int z = pz - HORIZONTAL_RANGE; z <= pz + HORIZONTAL_RANGE; z++) {
                    class_2338 pos = new class_2338(x, y, z);
                    class_2680 state = world.method_8320(pos);
                    if (isGreenShulkerBox(state)) {
                        this.highlightedBlocks.add(pos);
                    }
                }
            }
        }

        updateEntities(world, px, py, pz);
    }

    private void updateEntities(class_1937 world, int px, int py, int pz) {
        class_238 scanBox = new class_238(
            px - HORIZONTAL_RANGE,
            py - VERTICAL_RANGE,
            pz - HORIZONTAL_RANGE,
            px + HORIZONTAL_RANGE,
            py + VERTICAL_RANGE,
            pz + HORIZONTAL_RANGE
        );
        Iterable<?> entities = world.method_8333(null, scanBox, entity -> true);
        if (entities == null) {
            return;
        }

        for (Object obj : entities) {
            if (!(obj instanceof class_1297)) {
                continue;
            }
            class_1297 entity = (class_1297) obj;
            if (entity == this.mc.field_1724) {
                continue;
            }
            if (!isGreenShulkerEntity(entity)) {
                continue;
            }

            class_238 box = getEntityBox(entity);
            if (box != null) {
                this.highlightedEntityBoxes.add(box);
            }

            class_2338 pos = entity.method_24515();
            if (pos != null) {
                this.highlightedEntityTracerTargets.add(pos);
            }
        }
    }

    private boolean isGreenShulkerBox(class_2680 state) {
        if (state == null) {
            return false;
        }

        String stateText = String.valueOf(state).toLowerCase(Locale.ROOT);
        if (stateText.contains("green_shulker_box")) {
            return true;
        }

        String blockText = String.valueOf(state.method_26204()).toLowerCase(Locale.ROOT);
        return blockText.contains("green_shulker_box");
    }

    private boolean isGreenShulkerEntity(class_1297 entity) {
        if (entity == null) {
            return false;
        }

        String typeText = getEntityTypeText(entity);
        if (typeText != null && (typeText.contains("green_shulker") || (typeText.contains("shulker") && typeText.contains("green")))) {
            return true;
        }

        String raw = String.valueOf(entity).toLowerCase(Locale.ROOT);
        return raw.contains("green_shulker") || (raw.contains("shulker") && raw.contains("green"));
    }

    private String getEntityTypeText(class_1297 entity) {
        if (entity == null) {
            return null;
        }
        if (this.entityTypeMethod == null) {
            this.entityTypeMethod = resolveEntityTypeMethod(entity);
        }
        if (this.entityTypeMethod == null) {
            return null;
        }
        try {
            if (!this.entityTypeMethod.getDeclaringClass().isInstance(entity)) {
                this.entityTypeMethod = resolveEntityTypeMethod(entity);
                if (this.entityTypeMethod == null) {
                    return null;
                }
            }
            Object type = this.entityTypeMethod.invoke(entity, new Object[0]);
            return type == null ? null : String.valueOf(type).toLowerCase(Locale.ROOT);
        } catch (ReflectiveOperationException ignored) {
            this.entityTypeMethod = null;
            return null;
        }
    }

    private Method resolveEntityTypeMethod(Object entity) {
        String[] candidates = { "getType", "method_5864" };
        for (String name : candidates) {
            try {
                return entity.getClass().getMethod(name, new Class[0]);
            } catch (ReflectiveOperationException ignored) {
                // try next
            }
        }
        return null;
    }

    private class_238 getEntityBox(Object entity) {
        if (entity == null) {
            return null;
        }
        if (this.entityBoundingBoxMethod == null) {
            this.entityBoundingBoxMethod = resolveEntityBoundingBoxMethod(entity);
        }
        if (this.entityBoundingBoxMethod == null) {
            return null;
        }
        try {
            if (!this.entityBoundingBoxMethod.getDeclaringClass().isInstance(entity)) {
                this.entityBoundingBoxMethod = resolveEntityBoundingBoxMethod(entity);
                if (this.entityBoundingBoxMethod == null) {
                    return null;
                }
            }
            Object box = this.entityBoundingBoxMethod.invoke(entity, new Object[0]);
            return box instanceof class_238 ? (class_238) box : null;
        } catch (ReflectiveOperationException ignored) {
            this.entityBoundingBoxMethod = null;
            return null;
        }
    }

    private Method resolveEntityBoundingBoxMethod(Object entity) {
        String[] candidates = { "getBoundingBox", "method_5829" };
        for (String name : candidates) {
            try {
                Method method = entity.getClass().getMethod(name, new Class[0]);
                if (class_238.class.isAssignableFrom(method.getReturnType())) {
                    return method;
                }
            } catch (ReflectiveOperationException ignored) {
                // try next
            }
        }
        return null;
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

    private class_243 getTracerStart(Render3DEvent.Last event) {
        try {
            Object context = invokeNoArg(event, "getContext");
            Object camera = invokeNoArg(context, "camera", "getCamera");
            Object cameraPos = invokeNoArg(camera, "getPos", "getPosition");
            if (!(cameraPos instanceof class_243) || this.mc.field_1724 == null) {
                return null;
            }

            class_243 eyePos = (class_243) cameraPos;
            class_243 look = this.mc.field_1724.method_5828(1.0F);
            return eyePos.method_1019(look.method_1029());
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
