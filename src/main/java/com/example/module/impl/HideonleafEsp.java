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
import com.ricedotwho.rsm.ui.clickgui.settings.impl.NumberSetting;
import net.minecraft.class_1297;
import net.minecraft.class_1937;
import net.minecraft.class_2338;
import net.minecraft.class_238;
import net.minecraft.class_243;
import net.minecraft.class_2561;
import net.minecraft.class_2680;
import net.minecraft.class_310;

import java.lang.reflect.Constructor;
import java.lang.reflect.Method;
import java.util.ArrayList;
import java.util.List;
import java.util.Locale;

@ModuleInfo(aliases = {"Hideonleaf"}, id = "hideonleaf_esp", category = Category.RENDER)
public class HideonleafEsp extends Module {
    private static final int SCAN_BLOCKS_PER_STEP = 16;
    private static final int SCAN_INTERVAL_TICKS = 10;
    private static final int SCAN_IDLE_INTERVAL_TICKS = 40;

    private static final Colour FILL_COLOUR = new Colour(30, 120, 30, 55);
    private static final Colour OUTLINE_COLOUR = new Colour(60, 170, 60, 190);

    private final class_310 mc = class_310.method_1551();
    private final DefaultGroupSetting espGroup = new DefaultGroupSetting("ESP", this);
    private final BooleanSetting enable = new BooleanSetting("Enable", true);
    private final BooleanSetting tracer = new BooleanSetting("Tracer", true);
    private final NumberSetting scanDistance = new NumberSetting("Scan Distance", 1.0D, 12.0D, 2.0D, 1.0D, "chunks", () -> ((Boolean) this.enable.getValue()).booleanValue());
    private final List<class_2338> highlightedBlocks = new ArrayList<>();
    private final List<class_238> highlightedEntityBoxes = new ArrayList<>();
    private final List<class_2338> highlightedEntityTracerTargets = new ArrayList<>();

    private int tickCounter;
    private int lastScanChunkX = Integer.MIN_VALUE;
    private int lastScanChunkY = Integer.MIN_VALUE;
    private int lastScanChunkZ = Integer.MIN_VALUE;
    private int lastScanRange = Integer.MIN_VALUE;
    private boolean scanInitialized;

    private Constructor<?> filledBoxConstructor;
    private Constructor<?> outlineBoxConstructor;
    private Constructor<?> lineConstructor;
    private Method addTaskMethod;
    private boolean renderBridgeReady;
    private Method entityBoundingBoxMethod;
    private Method entityTypeMethod;
    private Method shulkerColorMethod;

    public HideonleafEsp() {
        setGroup(this.espGroup);
        registerProperty(new Setting[] { this.espGroup });
        this.espGroup.add(new Setting[] { this.enable, this.tracer, this.scanDistance });
        this.renderBridgeReady = initRenderBridge();
    }

    @SubscribeEvent
    public void onTick(ClientTickEvent.End event) {
        if (!isEnabled() || !((Boolean) this.enable.getValue()).booleanValue()) {
            this.highlightedBlocks.clear();
            this.highlightedEntityBoxes.clear();
            this.highlightedEntityTracerTargets.clear();
            this.scanInitialized = false;
            return;
        }

        if (this.mc.field_1687 == null || this.mc.field_1724 == null) {
            this.highlightedBlocks.clear();
            this.highlightedEntityBoxes.clear();
            this.highlightedEntityTracerTargets.clear();
            this.scanInitialized = false;
            return;
        }

        this.tickCounter++;
        class_2338 playerPos = this.mc.field_1724.method_24515();
        int px = playerPos.method_10263();
        int py = playerPos.method_10264();
        int pz = playerPos.method_10260();
        int range = getScanRange();
        int chunkX = px >> 4;
        int chunkY = py >> 4;
        int chunkZ = pz >> 4;
        boolean moved = (chunkX != this.lastScanChunkX || chunkY != this.lastScanChunkY || chunkZ != this.lastScanChunkZ || range != this.lastScanRange);
        int interval = moved ? SCAN_INTERVAL_TICKS : SCAN_IDLE_INTERVAL_TICKS;
        if (!this.scanInitialized || moved || this.tickCounter % interval == 0) {
            updateBlocks(range);
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

    private void updateBlocks(int range) {
        class_1937 world = this.mc.field_1687;
        class_2338 playerPos = this.mc.field_1724.method_24515();

        int px = playerPos.method_10263();
        int py = playerPos.method_10264();
        int pz = playerPos.method_10260();

        this.highlightedBlocks.clear();
        this.highlightedEntityBoxes.clear();
        this.highlightedEntityTracerTargets.clear();
        for (int x = px - range; x <= px + range; x++) {
            for (int y = py - range; y <= py + range; y++) {
                for (int z = pz - range; z <= pz + range; z++) {
                    class_2338 pos = new class_2338(x, y, z);
                    class_2680 state = world.method_8320(pos);
                    if (isGreenShulkerBox(state)) {
                        this.highlightedBlocks.add(pos);
                    }
                }
            }
        }

        updateEntities(world, px, py, pz, range);
        this.lastScanChunkX = px >> 4;
        this.lastScanChunkY = py >> 4;
        this.lastScanChunkZ = pz >> 4;
        this.lastScanRange = range;
        this.scanInitialized = true;
    }

    private void updateEntities(class_1937 world, int px, int py, int pz, int range) {
        class_238 scanBox = new class_238(
            px - range,
            py - range,
            pz - range,
            px + range,
            py + range,
            pz + range
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
        if (stateText.contains("green_shulker_box") || stateText.contains("lime_shulker_box")) {
            return true;
        }

        String blockText = String.valueOf(state.method_26204()).toLowerCase(Locale.ROOT);
        if (blockText.contains("green_shulker_box") || blockText.contains("lime_shulker_box")) {
            return true;
        }
        if (blockText.contains("shulker_box")) {
            return stateText.contains("color=green") || stateText.contains("color=lime");
        }
        return false;
    }

    private int getScanRange() {
        double scale = ((Number) this.scanDistance.getValue()).doubleValue();
        int range = (int) Math.round(scale * SCAN_BLOCKS_PER_STEP);
        return Math.max(SCAN_BLOCKS_PER_STEP, range);
    }

    private boolean isGreenShulkerEntity(class_1297 entity) {
        if (entity == null) {
            return false;
        }

        String typeText = getEntityTypeText(entity);
        String raw = String.valueOf(entity).toLowerCase(Locale.ROOT);
        boolean isShulker = (typeText != null && typeText.contains("shulker")) || raw.contains("shulker");
        if (!isShulker) {
            return false;
        }

        if (typeText != null && (typeText.contains("green") || typeText.contains("lime"))) {
            return true;
        }
        if (raw.contains("green") || raw.contains("lime")) {
            return true;
        }
        String colour = getShulkerColorText(entity);
        if (colour != null && (colour.contains("green") || colour.contains("lime"))) {
            return true;
        }
        String nametag = getEntityNametag(entity);
        return nametag != null && (nametag.contains("green") || nametag.contains("lime"));
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

    private String getEntityNametag(class_1297 entity) {
        if (entity == null) {
            return null;
        }
        try {
            if (!entity.method_16914()) {
                return null;
            }
            class_2561 text = entity.method_5797();
            if (text == null) {
                return null;
            }
            String cleaned = text.getString().trim().toLowerCase(Locale.ROOT);
            return cleaned.isEmpty() ? null : cleaned;
        } catch (Throwable ignored) {
            return null;
        }
    }

    private String getShulkerColorText(class_1297 entity) {
        if (entity == null) {
            return null;
        }
        if (this.shulkerColorMethod == null || !this.shulkerColorMethod.getDeclaringClass().isInstance(entity)) {
            this.shulkerColorMethod = resolveShulkerColorMethod(entity);
        }
        if (this.shulkerColorMethod == null) {
            return null;
        }
        try {
            Object colour = this.shulkerColorMethod.invoke(entity, new Object[0]);
            return colour == null ? null : String.valueOf(colour).toLowerCase(Locale.ROOT);
        } catch (ReflectiveOperationException ignored) {
            this.shulkerColorMethod = null;
            return null;
        }
    }

    private Method resolveShulkerColorMethod(Object entity) {
        if (entity == null) {
            return null;
        }
        for (Method method : entity.getClass().getMethods()) {
            Method candidate = resolveDyeEnumMethod(method);
            if (candidate != null) {
                return candidate;
            }
        }
        for (Method method : entity.getClass().getDeclaredMethods()) {
            Method candidate = resolveDyeEnumMethod(method);
            if (candidate != null) {
                return candidate;
            }
        }
        return null;
    }

    private Method resolveDyeEnumMethod(Method method) {
        if (method == null || method.getParameterCount() != 0) {
            return null;
        }
        Class<?> returnType = method.getReturnType();
        if (returnType == null || !returnType.isEnum()) {
            return null;
        }
        Object[] constants = returnType.getEnumConstants();
        if (constants == null || constants.length == 0) {
            return null;
        }
        boolean hasGreen = false;
        boolean hasLime = false;
        for (Object constant : constants) {
            String name = String.valueOf(constant).toLowerCase(Locale.ROOT);
            if (name.contains("green")) {
                hasGreen = true;
            }
            if (name.contains("lime")) {
                hasLime = true;
            }
            if (hasGreen && hasLime) {
                try {
                    method.setAccessible(true);
                } catch (Exception ignored) {
                    // ignore
                }
                return method;
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
