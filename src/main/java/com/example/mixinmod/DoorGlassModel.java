package com.example.mixinmod;

import java.util.function.Predicate;

import org.jetbrains.annotations.Nullable;

import net.fabricmc.fabric.api.client.model.loading.v1.ModelLoadingPlugin;
import net.fabricmc.fabric.api.client.model.loading.v1.wrapper.WrapperBlockStateModel;
import net.fabricmc.fabric.api.renderer.v1.mesh.QuadEmitter;
import net.fabricmc.fabric.api.renderer.v1.model.FabricBlockStateModel;

import net.minecraft.class_1087;
import net.minecraft.class_1920;
import net.minecraft.class_2246;
import net.minecraft.class_2338;
import net.minecraft.class_2350;
import net.minecraft.class_2680;
import net.minecraft.class_310;
import net.minecraft.class_5819;

public final class DoorGlassModel {
    private static volatile class_1087 cachedGlassModel;

    private DoorGlassModel() {
    }

    public static void register() {
        ModelLoadingPlugin.register(ctx -> ctx.modifyBlockModelAfterBake().register((model, context) -> {
            class_2680 state = context.state();
            for (DoorType type : DoorType.values()) {
                if (state.method_27852(type.getBlock())) {
                    return new DoorWrapper(model, type);
                }
            }
            return model;
        }));
    }

    public static void clearCache() {
        cachedGlassModel = null;
    }

    @Nullable
    private static class_1087 getGlassModel() {
        class_1087 cached = cachedGlassModel;
        if (cached != null) {
            return cached;
        }
        try {
            class_310 mc = class_310.method_1551();
            if (mc == null) return null;
            class_1087 model = mc.method_1554().method_4743().method_3335(class_2246.field_10033.method_9564());
            cachedGlassModel = model;
            return model;
        } catch (Throwable ignored) {
            return null;
        }
    }

    private static final class DoorWrapper extends WrapperBlockStateModel {
        private final DoorType type;

        private DoorWrapper(class_1087 wrapped, DoorType type) {
            super(wrapped);
            this.type = type;
        }

        @Override
        public void emitQuads(QuadEmitter emitter, class_1920 blockView, class_2338 pos, class_2680 state, class_5819 random, Predicate<@Nullable class_2350> cullTest) {
            if (!FuckCoalState.isTypeActive(this.type)
                || !DoorPositionTracker.isInDoor(blockView, pos, this.type)) {
                super.emitQuads(emitter, blockView, pos, state, random, cullTest);
                return;
            }
            class_1087 glass = getGlassModel();
            if (glass == null) {
                super.emitQuads(emitter, blockView, pos, state, random, cullTest);
                return;
            }
            DoorType wrapperType = this.type;
            class_2338.class_2339 scratch = new class_2338.class_2339();
            Predicate<@Nullable class_2350> wrappedCull = (dir) -> {
                if (cullTest.test(dir)) return true;
                if (dir == null) return false;
                scratch.method_10103(
                    pos.method_10263() + dir.method_10148(),
                    pos.method_10264() + dir.method_10164(),
                    pos.method_10260() + dir.method_10165()
                );
                class_2680 neighbor = blockView.method_8320(scratch);
                if (neighbor.method_27852(wrapperType.getBlock())
                    && DoorPositionTracker.isInDoor(blockView, scratch.method_10062(), wrapperType)) {
                    return true;
                }
                return false;
            };
            ((FabricBlockStateModel) glass).emitQuads(emitter, blockView, pos, state, random, wrappedCull);
        }

        @Override
        @Nullable
        public Object createGeometryKey(class_1920 blockView, class_2338 pos, class_2680 state, class_5819 random) {
            if (FuckCoalState.isTypeActive(this.type)) {
                return null;
            }
            return super.createGeometryKey(blockView, pos, state, random);
        }
    }
}
