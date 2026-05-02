package com.example.mixinmod;

import net.fabricmc.api.ClientModInitializer;
import net.fabricmc.fabric.api.client.event.lifecycle.v1.ClientTickEvents;

import net.minecraft.class_310;
import net.minecraft.class_638;

public final class MixinModClient implements ClientModInitializer {
    private static class_638 lastWorld;

    @Override
    public void onInitializeClient() {
        DoorGlassModel.register();
        ClientTickEvents.END_CLIENT_TICK.register(client -> {
            class_638 world = client.field_1687;
            if (world != lastWorld) {
                lastWorld = world;
                DoorPositionTracker.clearAll();
            }
        });
    }
}
